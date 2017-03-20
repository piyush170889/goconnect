/**
 *
 */
package com.devjiva.goconnect;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONException;
import org.json.JSONObject;

import com.devjiva.exception.UnauthorizedUserException;
import com.goconnect.events.http.Connection;
import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.model.RegistrationResponse;
import com.goconnect.util.SingletonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lib.DialogBox;
import com.lib.KeyboardUtil;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Ravi
 */

public class LoginActivity extends GoconnectActivity implements OnClickListener {

    Button facebook;
    TextView aboutUs_txt, privacy_txt;
    public static SocialAuthAdapter adapter;
    String providerName;
    Activity context = this;

    private static final String TAG = "LoginModeActivity";
    private SharedPreference sharedPreference;
    private String fb_profileData;
    private SharedPreferences mLoginPrefs;
    private String fb_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);
        mLoginPrefs = getSharedPreferences(Globals.LOGIN_SPF_NAME, Context.MODE_PRIVATE);
        sharedPreference = new SharedPreference();
        adapter = new SocialAuthAdapter(new ResponseListener());
        adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);

        try {
            adapter.addConfig(Provider.FACEBOOK, "351657481663875", "c505baa9c08a42c2acc5eddaee98125c", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        intializeViews();
    }

    private void intializeViews() {
        facebook = (Button) findViewById(R.id.fb_login);
        aboutUs_txt = (TextView) findViewById(R.id.aboutus_txt);
        privacy_txt = (TextView) findViewById(R.id.privacy_txt);

        facebook.setOnClickListener(this);
        aboutUs_txt.setOnClickListener(this);
        privacy_txt.setOnClickListener(this);
    }

    private final class ResponseListener implements DialogListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onComplete(Bundle values) {

            providerName = values.getString(SocialAuthAdapter.PROVIDER);

            adapter.getUserProfileAsync(new ProfileDataListener());

        }

        @Override
        public void onError(SocialAuthError error) {
            error.printStackTrace();
            Log.d(TAG, error.getMessage());
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "Authentication Cancelled");
        }

        @Override
        public void onBack() {
            Log.d(TAG, "Dialog Closed by pressing Back Key");

        }
    }

    @SuppressWarnings("rawtypes")
    private final class ProfileDataListener implements SocialAuthListener {

        @Override
        public void onError(SocialAuthError err) {

        }

        @Override
        public void onExecute(String arg0, Object obj) {
            Log.d("Custom-UI", "Receiving Data");
            Profile profileMap = (Profile) obj;
            Log.d("Custom-UI", "First Name��������� = " + profileMap.getFirstName());
            Log.d("Custom-UI", "Last Name���������� = " + profileMap.getLastName());
            Log.d("Custom-UI", "Contact No���������� = " + profileMap.getContactInfo());
            Log.d("Custom-UI", "Email�������������� = " + profileMap.getEmail());
            Log.d("Custom-UI", "Validate ID�������� = " + profileMap.getValidatedId());
            Log.d("Custom-UI", "Profile Image URL� = " + profileMap.getProfileImageURL());
            Log.d("Custom-UI", "Gender������������������ = " + profileMap.getGender());
            // Log.d("Custom-UI", "Description�������� = " + profileMap.get);
            Log.d("Custom-UI", "Country����������������� = " + profileMap.getCountry());
            // Log.d("Custom-UI", "City�������� = " + profileMap.get);
            Log.d("Custom-UI", "Address�������� = " + profileMap.getLocation());


            JsonObject mainObj = new JsonObject();
            JsonObject obj1 = new JsonObject();
            obj1.addProperty("id", profileMap.getValidatedId());
            obj1.addProperty("last_name", profileMap.getLastName());
            obj1.addProperty("first_name", profileMap.getFirstName());
            obj1.addProperty("email", profileMap.getEmail());
            obj1.addProperty("name", profileMap.getFirstName() + " " + profileMap.getLastName());
            mainObj.add("facebook", obj1);

            fb_profileData = mainObj.toString();
            Log.d(TAG, "onExecute: fb_profileData" + fb_profileData);
            fb_profile_image = profileMap.getProfileImageURL();


            JSONObject requestObj = new JSONObject();
            try {
                requestObj.put("facebookid", profileMap.getValidatedId());
                requestObj.put("activeFlag", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateToEMedServer(profileMap.getValidatedId(), mainObj.toString(), profileMap.getProfileImageURL(), requestObj);

//            callToCheckExistingUser(profileMap.getValidatedId(), mainObj.toString(), profileMap.getProfileImageURL());

//            callToProfile(mainObj.toString(), profileMap.getProfileImageURL());

        }
    }


    private void callToProfile(String mainobj, String profileImageURL) {
        Intent i;
        i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra("facebook_profileData", mainobj);
        sharedPreference.saveBoolValueWithKey(getApplicationContext(), true, "SwtShareSafe");
        i.putExtra("fb_profile_image", profileImageURL);
        startActivity(i);
        finish();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            // getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            // Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {

            case R.id.privacy_txt:
                i = new Intent(getApplicationContext(), ShowWebUrlActivity.class);
                i.putExtra("url", "http://www.uwcgo.org/policy");
                startActivity(i);

                break;

            case R.id.aboutus_txt:
                i = new Intent(getApplicationContext(), ShowWebUrlActivity.class);
                i.putExtra("url", "http://www.uwcgo.org/about");
                startActivity(i);
                break;

            case R.id.fb_login:
                if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(this)) {
                    adapter.authorize(LoginActivity.this, Provider.FACEBOOK);
                } else {
                    SingletonUtil.getSingletonConfigInstance().showErrorDialog(this);
                }
                break;
            default:
                break;
        }
    }


    public void updateToEMedServer(final String fbId, final String mainObj, final String profileImageURL, final JSONObject requestObj) {

        new BackgroundTask(LoginActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {
                    if (!Network.isAvailable(LoginActivity.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof UnauthorizedUserException) {
                        callToProfile(mainObj, profileImageURL);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            if (data.toString().contains("errMsg")) {
                                String errorMessage = jsonObject.getString("errMsg");
                                DialogBox newFragment = DialogBox.newInstance(errorMessage);
                                newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                            } else {

                                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                                    //user is already registered
                                    /** saving credentials here **/
                                    saveCredentials();



                                    Log.d(TAG, "taskCompleted: Response of UserProfileData: "+data.toString());
                                    sharedPreference.saveWithKey(context, data.toString(), "UserProfileData");
                                    sharedPreference.saveWithKey(context, fb_profileData, "facebook_profileData");
                                    sharedPreference.saveWithKey(context, fb_profile_image, "fb_profile_image");

                                    Log.d(TAG, "UserProfileData=" + data.toString());
                                    Intent i;
                                    sharedPreference.saveBoolValueWithKey(getApplicationContext(), true, "SwtShareSafe");
                                    i = new Intent(getApplicationContext(), Dashboard.class);
                                    i.putExtra("facebook_profileData", mainObj);
                                    i.putExtra("fb_profile_image", profileImageURL);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                }/* else if (jsonObject.getString("status").equalsIgnoreCase("Unauthorized")) {
                                    callToProfile(mainObj, profileImageURL);
                                }*/
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
                    response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/findUserByFid", requestObj);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
        }, Globals.LOADING).execute();
    }

    protected void saveCredentials() {

        JSONObject obj;
        try {
            obj = new JSONObject(fb_profileData);
            SharedPreferences.Editor editor = mLoginPrefs.edit();
            editor.putString("userFacebookId", obj.getJSONObject("facebook").getString("id"));
            // editor.putString("userName", mLogData.getUserPreferedName());

            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}