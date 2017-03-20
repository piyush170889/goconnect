package com.devjiva.goconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goconnect.events.http.Connection;
import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.model.RegistrationResponse;
import com.google.gson.Gson;
import com.lib.DialogBox;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MoreUsersActivity extends GoconnectActivity {

    ImageView user1_1, user1_2, user1_3, user1_4, user2_1, user2_2, user2_3, user2_4, user3_1, user3_2, user3_3,
            user3_4, user4_1, user4_2, user4_3, user4_4;
    private static final String TAG = "MoreUsersActivity";
    LinearLayout layout100Meters, layout1km, layout10km, layoutFurther;
    LinearLayout layout100MetersMain, layout1kmMain, layout10kmMain, layoutFurtherMain;
    com.goconnect.events.lib.CircularImageView cImageView_user_icon;
    LayoutInflater inflater;
    private String fb_profileData;
    private String fb_profile_image, userProfileData;
    Activity context = this;
    private ImageLoader imageLoader;
    private SharedPreference sharedPreference;
    private String distance = "Further";
    private Intent intent;
    private int pageIndex = 1;
    private int totalPages = 1;
    private String Latitude, Longitude;
    private String facebookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dashboard_moreusers);
        sharedPreference = new SharedPreference();
        intializeComponents();
        Latitude = sharedPreference.getValueWithKey(getApplicationContext(), "Latitude");
        Longitude = sharedPreference.getValueWithKey(getApplicationContext(), "Longitude");
        intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("distanceRange") != null) {
                distance = intent.getStringExtra("distanceRange");
            }
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        fb_profileData = sharedPreference.getValueWithKey(context, "facebook_profileData");
        try {
            JSONObject obj = new JSONObject(fb_profileData);
            facebookId = obj.getJSONObject("facebook").getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        getMoreData(pageIndex);


    }

    private void getMoreData(int pageIndex) {
        JSONArray array = new JSONArray();
//		array.put("78.486671");
//		array.put("16.385044");
        array.put(Longitude);
        array.put(Latitude);

        JSONObject obj = new JSONObject();
        try {
            obj.put("pageNum", pageIndex);
            obj.put("distance", distance);
            obj.put("facebookId", facebookId);
            obj.put("currLocation", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getMoreNearestusers(obj);
    }

    private void intializeComponents() {

        layout100MetersMain = (LinearLayout) findViewById(R.id.dashbord_hundred_meters_layout_main);
        layout1kmMain = (LinearLayout) findViewById(R.id.dashbord_onekm_layout_main);
        layout10kmMain = (LinearLayout) findViewById(R.id.dashbord_tenkm_layout_main);
        layoutFurtherMain = (LinearLayout) findViewById(R.id.dashbord_further_layout_main);

        layout100Meters = (LinearLayout) findViewById(R.id.dashbord_hundred_meters_layout);
        layout1km = (LinearLayout) findViewById(R.id.dashbord_onekm_layout);
        layout10km = (LinearLayout) findViewById(R.id.dashbord_tenkm_layout);
        layoutFurther = (LinearLayout) findViewById(R.id.dashbord_further_layout);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutFurtherMain.setVisibility(View.GONE);
        layout10kmMain.setVisibility(View.GONE);
        layout1kmMain.setVisibility(View.GONE);
        layout100MetersMain.setVisibility(View.GONE);

    }

    private void getMoreNearestusers(final JSONObject obj) {
        new BackgroundTask(MoreUsersActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {
                    Log.d(TAG, "taskCompleted: " + data.toString());
                    if (!Network.isAvailable(MoreUsersActivity.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            if (data.toString().contains("errMsg")) {
                                JSONObject jsonObject = new JSONObject(data.toString());
                                String errorMessage = jsonObject.getString("errMsg");

                                DialogBox newFragment = DialogBox.newInstance(errorMessage);
                                newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                            } else {
                                RegistrationResponse regResponse = new Gson().fromJson(data.toString(),
                                        RegistrationResponse.class);

                                int requestCode = 0;
                                try {
                                    requestCode = getIntent().getExtras().getInt("requestCode");
                                } catch (Exception e) {
                                }
                                if (requestCode == Globals.SCR_REGISTER) {
                                    Intent output = new Intent();
                                    setResult(RESULT_OK, output);
                                    finish();
                                } else if (regResponse.getStatus().equals("success")
                                        || regResponse.getStatus().equals("Existed user details updated")) {
                                    JSONObject jsonObject = new JSONObject(data.toString());
                                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                        JSONObject jsonUsers = new JSONObject(jsonObject.getString("users"));
                                        totalPages = jsonUsers.getInt("totalpages");
                                        JSONArray JsonArrayresults = new JSONArray(jsonUsers.getString("result"));

                                        UpdateResults(JsonArrayresults);

                                    }
                                }
                            }
                        } catch (Exception e) {
                            DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                            newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                    newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                }

            }


            @Override
            public Object runTask() {
                try {
                    Object response;
                    try {
                        response = Connection.callHttpPostRequestsV2(Connection.URL_BASE
                                + "api/users/paginateUserDetails", obj);

                        Log.d(TAG, "runTask: " + obj.toString());
                        return response;
                    } catch (Exception e) {
                        return e;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
        }, Globals.LOADING).execute();
    }

    private void UpdateResults(JSONArray jsonArray) {


        if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe")) {
            if (distance.equals("100m")) {
                UpdateDlist1(jsonArray);
            } else if (distance.equals("1km")) {
                UpdateDlist2(jsonArray);
            } else if (distance.equals("10km")) {
                UpdateDlist3(jsonArray);
            } else if (distance.equals("Further")) {
                UpdateMoreData(jsonArray);
            }

        } else
            Toast.makeText(MoreUsersActivity.this, "Plese turn on the sharemode to view users", Toast.LENGTH_SHORT).show();
    }

    private void UpdateMoreData(JSONArray jsonArray) {
        layoutFurtherMain.setVisibility(View.VISIBLE);
        UpdateUI(jsonArray, layoutFurther);
        Log.d(TAG, jsonArray.toString());
        layout10kmMain.setVisibility(View.GONE);
        layout1kmMain.setVisibility(View.GONE);
        layout100MetersMain.setVisibility(View.GONE);
    }

    private void UpdateDlist3(JSONArray jsonArray) {
        layout10kmMain.setVisibility(View.VISIBLE);
        UpdateUI(jsonArray, layout10km);
        Log.d(TAG, jsonArray.toString());
        layoutFurtherMain.setVisibility(View.GONE);
        layout1kmMain.setVisibility(View.GONE);
        layout100MetersMain.setVisibility(View.GONE);
    }

    private void UpdateDlist2(JSONArray jsonArray) {
        layout1kmMain.setVisibility(View.VISIBLE);
        UpdateUI(jsonArray, layout1km);
        Log.d(TAG, jsonArray.toString());
        layout10kmMain.setVisibility(View.GONE);
        layoutFurtherMain.setVisibility(View.GONE);
        layout100MetersMain.setVisibility(View.GONE);
    }

    private void UpdateDlist1(JSONArray jsonArray) {
        layout100MetersMain.setVisibility(View.VISIBLE);
        UpdateUI(jsonArray, layout100Meters);
        Log.d(TAG, jsonArray.toString());
        layout10kmMain.setVisibility(View.GONE);
        layoutFurtherMain.setVisibility(View.GONE);
        layout1kmMain.setVisibility(View.GONE);
    }

    public void UpdateUI(final JSONArray jsonArray, LinearLayout layout) {
        layout.removeAllViews();
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
                    String url = otherUserJSONObject.getJSONObject("facebook").getString("facebookProfileImage");

                    com.goconnect.events.lib.CircularImageView cImageView1meater = (com.goconnect.events.lib.CircularImageView) inflater.inflate(R.layout.image_view_sample, null);

                    imageLoader.displayImage(url, cImageView1meater);

/*
                    if (null == url || url.isEmpty()) {
                        Picasso.with(this).load(R.drawable.defaultuser).into(cImageView1meater);
                    } else
                        Picasso.with(this).load(url).into(cImageView1meater);*/


                    int mainsize = getMobileSizeWidth() / 4;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mainsize, mainsize);
                    layoutParams.setMargins(20, 20, 20, 0);
                    cImageView1meater.setLayoutParams(layoutParams);


//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(190, 190);
//                    layoutParams.setMargins(30, 20, 30, 0);
//                    cImageView1meater.setLayoutParams(layoutParams);

                    cImageView1meater.setOnClickListener(new OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showBusinessCardOfOtherUser(otherUserJSONObject);
                        }
                    });
                    if (ll != null) {
                        dataSizeTemp++;
                        ll.addView(cImageView1meater);
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
                if (false) {
                    LayoutParams lparams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(lparams);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv.setText("MoreData");
                    tv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(MoreUsersActivity.this, MoreUsersActivity.class);
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
        Intent intent = new Intent(MoreUsersActivity.this, BusinessCardOtherUserActivity.class);
        Bundle b = new Bundle();
        b.putString("BusinessUserData", otherUserJSONObject.toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    public void closeActivity(View v) {
        finish();
    }

    public void backPageEvent(View v) {

        pageIndex--;
        if (pageIndex != -1 && pageIndex != 0 && totalPages > pageIndex) {
            getMoreData(pageIndex);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void nextPageEvent(View v) {

        pageIndex++;
        if (totalPages > pageIndex) {
            getMoreData(pageIndex);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

}
