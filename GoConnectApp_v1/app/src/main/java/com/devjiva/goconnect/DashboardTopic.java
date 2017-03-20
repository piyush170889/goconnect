package com.devjiva.goconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goconnect.events.http.Connection;
import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;
import com.lib.DialogBox;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DashboardTopic extends SwipeActivity implements OnClickListener {

    //    private GestureDetectorCompat gestureDetectorCompat;
    private static final String TAG = "DashboardTopic";
    private Intent intent;
    private LinearLayout layout100Meters, layout1km, layout10km, layoutFurther, userTopictoconnectLogoLayout;
    private LayoutInflater inflater;
    private String fb_profileData;
    private String fb_profile_image, userProfileData;
    private Activity context = this;
    private SharedPreference sharedPreference;
    private String nearUserDataObject;
    private JSONObject jsonOjectNearUser;
    private TextView userTopictoconnect;
    private ScrollView scrollView;
    private int getNearCallCount = 0;
    private String facebookId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected`
            case R.id.action_settings:
                // Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                openSettingPage();

                break;
            case android.R.id.home:
                finish();
                break;

            case R.id.share_by_gmail:
                String to = "";
                String subject = "Try-out  GOconnect App";
                String message = "Please take this app for a spin with some people near you.(only Android 4 and up, sorry!)  Be aware that we will clear the database and you are only granted access for this short initial testing period\n" +
                        "\n\nDownload GoConnect app from the below link and install \n www.bit.ly/TestGOconnectAppAndroid" +
                        "\n\nAll feedback is welcome on \n www.bit.ly/GOconnectTestingfeedback ";

                final Intent intentToMail = new Intent(Intent.ACTION_SENDTO);
                intentToMail.setData(Uri.parse("mailto:"));
                // intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intentToMail.putExtra(Intent.EXTRA_SUBJECT, subject);
                intentToMail.putExtra(Intent.EXTRA_TEXT, message);
                if (intentToMail.resolveActivity(getPackageManager()) != null)
                    startActivity(intentToMail);
                break;

            default:
                break;
        }

        return true;
    }




 /*   @Override
    protected void onPause() {
        super.onPause();
        getNearCallCount = 0;
//        openSettingPage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getNearCallCount = 0;
    }*/


    private void openSettingPage() {
//        onPause();
        Intent intent = new Intent(DashboardTopic.this, SettingsActivity.class);
        startActivity(intent);
//        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard_topic_new);

//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sharedPreference = new SharedPreference();
        userTopictoconnectLogoLayout = (LinearLayout) findViewById(R.id.user_topictoconnect_logo_layout);
        int mainsizeTemp = getMobileSizeWidth() / 4;
        LinearLayout.LayoutParams mlayoutParams = new LinearLayout.LayoutParams(mainsizeTemp, mainsizeTemp);
        mlayoutParams.setMargins(30, 20, 30, 0);
        userTopictoconnectLogoLayout.setLayoutParams(mlayoutParams);
        layout100Meters = (LinearLayout) findViewById(R.id.dashbord_hundred_meters_layout);
        layout1km = (LinearLayout) findViewById(R.id.dashbord_onekm_layout);
        layout10km = (LinearLayout) findViewById(R.id.dashbord_tenkm_layout);
        layoutFurther = (LinearLayout) findViewById(R.id.dashbord_further_layout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setEnabled(false);

        userTopictoconnect = (TextView) findViewById(R.id.user_topictoconnect);
        userTopictoconnect.setOnClickListener(this);
        intent = getIntent();

        if (intent != null) {
            if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(this)) {
                nearUserDataObject = intent.getStringExtra("nearUserDataObject");
                if (nearUserDataObject != null) {
                    try {
                        jsonOjectNearUser = new JSONObject(nearUserDataObject);
                        if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe")) {
                            if (getNearCallCount == 0) {
                                if (jsonOjectNearUser != null) {
                                    UpdateDataToUI(jsonOjectNearUser);
                                } else {
                                    Toast.makeText(DashboardTopic.this, "Unable to fetch data!!Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else
                            Toast.makeText(DashboardTopic.this, "Plese turn on the sharemode to view users", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
//                SingletonUtil.getSingletonConfigInstance().showErrorDialog(this);
            }
        }

        try {
            userProfileData = sharedPreference.getValueWithKey(context, "UserProfileData");
            fb_profileData = sharedPreference.getValueWithKey(context, "facebook_profileData");
            fb_profile_image = sharedPreference.getValueWithKey(context, "fb_profile_image");
            JSONObject obj = new JSONObject(fb_profileData);
            facebookId = obj.getJSONObject("facebook").getString("id");
            try {
                JSONObject jsonOject = new JSONObject(userProfileData);

                Log.e(TAG, "current user profile jsonArrayString" + userProfileData);

                userTopictoconnect.setText(jsonOject.getJSONObject("user").get("topicToConnect").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
        }

        findViewById(R.id.txt_topic_face).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DashboardTopic.this, Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("nearUserDataObject", nearUserDataObject);
                startActivity(intent);
            }
        });

        findViewById(R.id.txt_topic_institution).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DashboardTopic.this, DashboardInstitution.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("nearUserDataObject", nearUserDataObject);
                startActivity(intent);
            }
        });

//        gestureDetectorCompat = new GestureDetectorCompat(this, new My3ndGestureListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(this)) {
            if (!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"))
                Toast.makeText(DashboardTopic.this, "Plese turn on the sharemode to view users", Toast.LENGTH_SHORT).show();
            if (getNearCallCount == 0) {
                if (jsonOjectNearUser != null) {
                    UpdateDataToUI(jsonOjectNearUser);
                } else {
                    Toast.makeText(DashboardTopic.this, "Unable to fetch data!!Please try again", Toast.LENGTH_SHORT).show();
                }
            }


        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_topictoconnect:
                showBusinessCardOfCurrentUser();
                break;

            default:
                break;
        }

    }


    private void UpdateMoreData(JSONArray jsonArray) {

        UpdateUI(jsonArray, layoutFurther);
    }

    private void UpdateDlist3(JSONArray jsonArray) {
        UpdateUI(jsonArray, layout10km);
    }

    private void UpdateDlist2(JSONArray jsonArray) {
        UpdateUI(jsonArray, layout1km);
    }

    private void UpdateDlist1(JSONArray jsonArray) {
        UpdateUI(jsonArray, layout100Meters);
    }

    public void UpdateUI(final JSONArray jsonArray, LinearLayout layout) {

        if (!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"))
            layout.setVisibility(View.GONE);

        int rowLimit = 3;
        int dataSize = jsonArray.length();
        int dataSizeTemp = 0;
        int dataSizeLimit = dataSize;
        float weight = 2f;
        LinearLayout ll = null;
        if (jsonArray.length() < rowLimit) {
            rowLimit = jsonArray.length();
        }
        for (int i = 0; i < dataSize; i++) {
            if (dataSizeTemp == 0) {
                dataSizeLimit = dataSizeLimit - 3;
                ll = null;
                ll = new LinearLayout(getApplicationContext());
                ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ll.setPadding(2, 5, 2, 5);
                ll.setOrientation(LinearLayout.HORIZONTAL);
            }
            if (dataSizeTemp < rowLimit) {

                try {
                    final JSONObject otherUserJSONObject = (JSONObject) jsonArray.get(i);

                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mLinearLayout = inflater.inflate(R.layout.topic_view_sample, null);
                    int mainsize = getMobileSizeWidth() / 4;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mainsize, mainsize);
                    layoutParams.setMargins(20, 20, 20, 0);
                    mLinearLayout.setLayoutParams(layoutParams);
                    TextView txtView = (TextView) mLinearLayout.findViewById(R.id.user_topic_name);

                    txtView.setText(otherUserJSONObject.get("topicToConnect").toString());
                    mLinearLayout.setOnClickListener(new OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showBusinessCardOfOtherUser(otherUserJSONObject);
                        }
                    });
                    if (ll != null) {
                        dataSizeTemp++;
                        ll.addView(mLinearLayout);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
            if (dataSizeTemp == rowLimit) {
                if (dataSizeLimit < dataSizeTemp)
                    rowLimit = dataSizeLimit;
                dataSizeTemp = 0;
                if (i == 9) {
                    LayoutParams lparams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(lparams);
                    tv.setPadding(20, 100, 0, 0);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv.setText("MoreData");
                    tv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(DashboardTopic.this, MoreUsersActivity.class);
                            startActivity(intent);
                        }
                    });
                    ll.addView(tv);
                    layout.addView(ll);
                    break;
                } else {
                    layout.addView(ll);
                }
                ll = null;

            }
        }

    }

    public int getMobileSizeWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
        }

        int width = size.x;
        int height = size.y;
        return width;
    }

    private void showBusinessCardOfOtherUser(JSONObject otherUserJSONObject) {
        onStop();
        Intent intent = new Intent(DashboardTopic.this, BusinessCardOtherUserActivity.class);
        Bundle b = new Bundle();
        b.putString("BusinessUserData", otherUserJSONObject.toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void showBusinessCardOfCurrentUser() {
        onStop();
        Intent intent = new Intent(DashboardTopic.this, BusinessCardUserActivity.class);
        startActivity(intent);
    }

    private void UpdateDataToUI(JSONObject jsonObject) {

        try {
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                JSONObject jsonUsers = new JSONObject(jsonObject.getString("users"));
                JSONArray JsonArrayDlist1 = new JSONArray(jsonUsers.getString("distl1"));
                JSONArray JsonArrayDlist2 = new JSONArray(jsonUsers.getString("distl2"));
                JSONArray JsonArrayDlist3 = new JSONArray(jsonUsers.getString("distl3"));
                JSONArray JsonArrayMoreData = new JSONArray(jsonUsers.getString("moreData"));

//                if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe")) {
                UpdateDlist1(JsonArrayDlist1);
                UpdateDlist2(JsonArrayDlist2);
                UpdateDlist3(JsonArrayDlist3);
                UpdateMoreData(JsonArrayMoreData);
                getNearCallCount++;
//                } else
//                    Toast.makeText(DashboardTopic.this, "Plese turn on the sharemode to view users", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
*/


    //for previous and next actions
    @Override
    protected void previous() {
        Intent intent = new Intent(DashboardTopic.this, DashboardInstitution.class);
        intent.putExtra("nearUserDataObject", nearUserDataObject);
        startActivity(intent);
    }

    @Override
    protected void next() {
        Intent intent = new Intent(DashboardTopic.this, Dashboard.class);
        intent.putExtra("nearUserDataObject", nearUserDataObject);
        startActivity(intent);
    }

  /*  class My3ndGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {


//             Toast.makeText(getBaseContext(),
//	          event1.toString() + "\n\n" +event2.toString(),
//	          Toast.LENGTH_SHORT).show();


            if (event2.getX() > event1.getX()) {

//	          Toast.makeText(getBaseContext(), 
//	           "Swipe right - finish()", 
//	           Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }
    }*/

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureDetectorCompat.onTouchEvent(ev);
    }*/
}