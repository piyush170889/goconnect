package com.devjiva.goconnect;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import com.goconnect.events.image.ImageLoader;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BusinessCardUserActivity extends Activity implements
        OnClickListener {
    Activity context = this;
    Button facebook;
    TextView aboutUs_txt, privacy_txt;
    SocialAuthAdapter adapter;
    String providerName;
    private static final String TAG = "BusinessCardUser";
    TextView user_name, college, year, designation, companyName, topictoconnect, userInstitutionName;
    private ImageLoader imageLoader;
    private com.goconnect.events.lib.CircularImageView profilePic;
    private String userProfileData;
    private SharedPreference sharedPreference;


    private JSONObject userProfileDataJsonOject;

    private TextView linkedin_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card_user);
        sharedPreference = new SharedPreference();

//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);

        userProfileData = sharedPreference.getValueWithKey(context, "UserProfileData");
//        UserProfileLinkedInData = sharedPreference.getValueWithKey(context, "UserProfileLinkedInData");

        if (userProfileData != null) {
            initializeUI();
            try {

                userProfileDataJsonOject = new JSONObject(userProfileData);

                Log.e(TAG, "current user profile jsonArrayString" + userProfileData);
                updateDataToUI(userProfileDataJsonOject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    private String fb_profile_image;

    private void updateDataToUI(JSONObject userProfileDataJsonOject) {
        JSONObject jsonOjectFB;

        Log.d(TAG, "updateDataToUI: userProfileDataJsonOject: " + userProfileDataJsonOject.toString());
        if (userProfileDataJsonOject != null) {
            try {
                try {
                    user_name.setText(userProfileDataJsonOject.getJSONObject("user").getJSONObject("facebook").get("name").toString());
                    fb_profile_image = userProfileDataJsonOject.getJSONObject("user").getJSONObject("facebook").get("facebookProfileImage").toString();
                } catch (JSONException e) {

                    jsonOjectFB = new JSONObject(userProfileDataJsonOject.getJSONObject("user").get("facebook").toString());

                    user_name.setText(jsonOjectFB.getJSONObject("facebook").get("name").toString());
                    fb_profile_image = jsonOjectFB.getJSONObject("facebook").get("facebookProfileImage").toString();
                }
                college.setText(userProfileDataJsonOject.getJSONObject("user").getJSONObject("affiliation").get("college").toString());
                year.setText(userProfileDataJsonOject.getJSONObject("user").getJSONObject("affiliation").get("endYear").toString());
                designation.setText(userProfileDataJsonOject.getJSONObject("user").getJSONObject("position").get("jobTitle").toString());
                companyName.setText(userProfileDataJsonOject.getJSONObject("user").getJSONObject("position").get("organisationName").toString());
                topictoconnect.setText("TopicToConnect:\n " + userProfileDataJsonOject.getJSONObject("user").get("topicToConnect").toString());
//				fb_profile_image=jsonOject.getJSONObject("user").get("avatar").toString();
                imageLoader = new ImageLoader(getApplicationContext());
                // DisplayImage function from ImageLoader Class
                imageLoader.DisplayImage(fb_profile_image, profilePic);
                userInstitutionName.setText(companyName.getText());

                String linkedinUrlStr = null;
                try {
                    if (userProfileDataJsonOject.getJSONObject("user").getJSONObject("linkedin") != null) {
                        if (userProfileDataJsonOject.getJSONObject("user").getJSONObject("linkedin").getString("publicProfileUrl") != null) {
                            linkedinUrlStr = userProfileDataJsonOject.getJSONObject("user").getJSONObject("linkedin").getString("publicProfileUrl").toString();
                        }
                    }
                } catch (JSONException jse) {
                    jse.printStackTrace();
                }

                if (linkedinUrlStr != null) {
                    linkedin_url.setText(linkedinUrlStr);
                    Linkify.addLinks(linkedin_url, Linkify.WEB_URLS);
                } else
                    linkedin_url.setVisibility(View.GONE);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private void initializeUI() {
        user_name = (TextView) findViewById(R.id.business_card_use_name);
        linkedin_url = (TextView) findViewById(R.id.linkedin_url);
        college = (TextView) findViewById(R.id.business_card_college);
        year = (TextView) findViewById(R.id.business_card_year);
        designation = (TextView) findViewById(R.id.business_card_designation);
        companyName = (TextView) findViewById(R.id.business_card_companyname);
        userInstitutionName = (TextView) findViewById(R.id.user_institution);
        topictoconnect = (TextView) findViewById(R.id.business_card_topictoconnect);
        profilePic = (com.goconnect.events.lib.CircularImageView) findViewById(R.id.business_card_user_images);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    public void closeActivity(View v) {
        finish();
    }

    public void editUserProfile(View v) {

        Intent intent = new Intent(BusinessCardUserActivity.this, ProfileActivity.class);
        startActivity(intent);

    }

}