package com.devjiva.goconnect;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goconnect.events.image.ImageLoader;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author
 * @desc
 */
public class BusinessCardOtherUserActivity extends Activity implements
        OnClickListener {


    Button facebook;
    TextView aboutUs_txt, privacy_txt;
    SocialAuthAdapter adapter;
    String providerName;
    private static final String TAG = "BusinessCardOtherUser";
    private Intent intent;
    private String jsonOjectString, userEmailId;
    private JSONObject jsonOject;
    TextView user_name, college, year, designation, companyName, topictoconnect, userInstitutionName;
    private ImageLoader imageLoader;
    private TextView linkedinUrl;
    private SharedPreference sharedPreference;
    private String userProfileData;
    private JSONObject userProfileDataJson;
    private String collegeStr;
    private String yearStr;
    private String nameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        sharedPreference = new SharedPreference();
//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);

        intent = getIntent();
        if (intent != null) {

            jsonOjectString = intent.getStringExtra("BusinessUserData");

            Log.d(TAG, "onCreate: jsonOjectString" + jsonOjectString);

            try {
                jsonOject = new JSONObject(jsonOjectString);

                Log.e(TAG, "Other user profile jsonArrayString" + jsonOjectString);
                InitializeUI();
                UpdateDataToUI(jsonOject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

/*
        userProfileData = sharedPreference.getValueWithKey(getApplicationContext(), "UserProfileData");
        try {
            userProfileDataJson = new JSONObject(userProfileData);
            collegeStr = userProfileDataJson.getJSONObject("user").getJSONObject("affiliation").getString("college");
            yearStr = userProfileDataJson.getJSONObject("user").getJSONObject("affiliation").getString("endYear");
            nameStr = userProfileDataJson.getJSONObject("user").getJSONObject("facebook").getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/


    }

    private String fb_profile_image;
    com.goconnect.events.lib.CircularImageView profilePic;
    JSONObject jsonOjectFB;

    private void UpdateDataToUI(JSONObject jsonOject) {

        if (jsonOject != null) {
            try {
                try {
                    nameStr = jsonOject.getJSONObject("facebook").get("name").toString();
                    user_name.setText(nameStr);

                    userEmailId = jsonOject.getJSONObject("facebook").get("email").toString();
                    fb_profile_image = jsonOject.getJSONObject("facebook").get("facebookProfileImage").toString();

                    Log.d(TAG, "fb_profile_image -try block" + fb_profile_image);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonOjectFB = new JSONObject(jsonOject.get("facebook").toString());
                    user_name.setText(jsonOjectFB.getJSONObject("facebook").get("name").toString());
                    userEmailId = jsonOjectFB.getJSONObject("facebook").get("email").toString();
                    fb_profile_image = jsonOjectFB.getJSONObject("facebook").get("facebookProfileImage").toString();
                    Log.d(TAG, "fb_profile_image-catch block" + fb_profile_image);
                }

				/*try{
                    user_name.setText(jsonOject.getJSONObject("facebook").get("name").toString());
					userEmailId=jsonOject.getJSONObject("facebook").get("email").toString();
				} catch (JSONException e) {
					user_name.setText(jsonOject.getJSONObject("facebook").getJSONObject("facebook").get("name").toString());
					userEmailId=jsonOject.getJSONObject("facebook").getJSONObject("facebook").get("email").toString();
				}*/

                collegeStr = jsonOject.getJSONObject("affiliation").get("college").toString();
                college.setText(collegeStr);

                yearStr = jsonOject.getJSONObject("affiliation").get("endYear").toString();
                year.setText(yearStr);
                designation.setText(jsonOject.getJSONObject("position").get("jobTitle").toString());
                companyName.setText(jsonOject.getJSONObject("position").get("organisationName").toString());
                topictoconnect.setText(jsonOject.get("topicToConnect").toString());
                String linkedinUrlStr = null;
                try {
                    if (jsonOject.getJSONObject("linkedin") != null) {
                        if (jsonOject.getJSONObject("linkedin").get("publicProfileUrl") != null) {
                            linkedinUrlStr = jsonOject.getJSONObject("linkedin").get("publicProfileUrl").toString();
                        }
                    }
                } catch (JSONException jse) {
                    jse.printStackTrace();
                }

                if (linkedinUrlStr != null) {
                    linkedinUrl.setText(linkedinUrlStr);
                    Linkify.addLinks(linkedinUrl, Linkify.WEB_URLS);
                } else
                    linkedinUrl.setVisibility(View.GONE);
                //fb_profile_image=jsonOject.get("avatar").toString();
                imageLoader = new ImageLoader(getApplicationContext());
                userInstitutionName.setText(companyName.getText());
                // DisplayImage function from ImageLoader Class
                imageLoader.DisplayImage(fb_profile_image, profilePic);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void InitializeUI() {
        user_name = (TextView) findViewById(R.id.business_use_name);
        college = (TextView) findViewById(R.id.business_college);
        year = (TextView) findViewById(R.id.business_year);
        designation = (TextView) findViewById(R.id.business_designation);
        companyName = (TextView) findViewById(R.id.business_companyname);
        topictoconnect = (TextView) findViewById(R.id.business_topictoconnect);
        profilePic = (com.goconnect.events.lib.CircularImageView) findViewById(R.id.fb_user_image);
        userInstitutionName = (TextView) findViewById(R.id.user_institution);
        linkedinUrl = (TextView) findViewById(R.id.linkedin_url);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    public void closeActivity(View v) {
        finish();
    }

    public void blockUserDialog(final View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(BusinessCardOtherUserActivity.this).create();
        alertDialog.setTitle("GOconnect");
        alertDialog.setMessage("Do you want to report?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Report",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                        reportEmail(v);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Block",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                        blockDialog();
                    }
                });
        alertDialog.show();
    }

    private void reportEmail(View v) {

        String to = "info@uwcgo.org";
        String subject = "report/complain about a member";
        String message = "<Write your complaint here>\n" +
                "\n" + nameStr + "\n" +
                collegeStr + "\n" +
                yearStr;

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        // email.putExtra(Intent.EXTRA_CC, new String[]{ to});
        // email.putExtra(Intent.EXTRA_BCC, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        // need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void blockDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(BusinessCardOtherUserActivity.this).create();
        alertDialog.setTitle("GOconnect");
        alertDialog.setMessage("Not yet enabled, send report message instead");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public void sendEmail(View v) {


        String to = "" + userEmailId;
        String subject = "UWCer nearby you";
        String message = "Hi, I see you are close by. If you have time I would love to say Hi\n " +
                "\nThank You.";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        // email.putExtra(Intent.EXTRA_CC, new String[]{ to});
        // email.putExtra(Intent.EXTRA_BCC, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        // need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}