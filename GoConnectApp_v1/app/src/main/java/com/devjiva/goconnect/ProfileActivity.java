package com.devjiva.goconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.devjiva.goconnect.locations.MyLocationUpdateServiceNew;
import com.goconnect.events.http.Connection;
//import com.goconnect.events.image.ImageLoader;
import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.model.RegistrationResponse;
import com.goconnect.util.SingletonUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.lib.DialogBox;
import com.lib.KeyboardUtil;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * @author Ravi
 */


public class ProfileActivity extends GoconnectActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 500;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API = 110;
    private static final long INTERVAL_FOR_LOCATION = 1 * 60 * 1000;
    private static final long FASTEST_INTERVAL_FOR_LOCATION = 2 * 60 * 1000;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";


    SharedPreferences mLoginPrefs;
    /* String[] colleges = {"* Select a College/School", "Adriatic", "Armenia", "Atlantic", "Changshu", "Costa Rica",
             "Li Po Chun", "Maastricht IB level", "Maastricht pre-IB level", "Mahindra", "Mostar", "Pearson",
             "Red Cross Nordik", "Robert Bosch", "South East Asia IB level", "South East Asia pre-IB level", "USA",
             "Waterford kamhlaba IB level", "Waterford kamhlaba pre-IB level"};

     String[] graduationYrs = {"* What is your year of graduation", "1962", "1963", "1964", "1965", "1962", "1967",
             "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980",
             "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993",
             "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2004", "2005",
             "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018",
             "2019", "2020"};
 */
    com.goconnect.events.lib.CircularImageView profilePic;
    private static final String TAG = "ProfileActivity";
    EditText name_headmaster, name, email, topic2connect, jobTitle, orgName;
    Spinner collegeNames, graduationYears;
    Button next_btn, back_btn, save_btn;
    LinearLayout profile_layout_one, profile_layout_two;
    private String selectedCollegeName = "";
    private String selectedGraduationYr = "";

    private ImageLoader imageLoader;

    private String fb_profileData;
    private String fb_profile_image, userProfileData;
    private SharedPreference sharedPreference;
    public DisplayImageOptions doptions;
    Activity context = this;
    //    private CircularImageView orgPic;
    private static final int PICK_IMAGE_FROM_GALLERY = 200;
    private String imgPath;
    private File mediaFile;
    private Button linked_in_btn;
    private ProgressDialog progress;
    //    private static final String topCardUrl = "https://api.linkedin.com/v1/people/~?format=json";
    private static final String topCardUrl = "https://api.linkedin.com/v1/people/~:(first-name,last-name,headline,formatted-name,phone-numbers,industry,positions,public-profile-url)?format=json";
    private String emailIDFromFb;
    private String nameFromFb;
    private String publicProfileUrl;
    private LocationManager mLocationManager;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeLocationManager();


//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);

        callBackService();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_FOR_LOCATION);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL_FOR_LOCATION);
        // intializeViews();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        doptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.defaultuser).showImageOnFail(R.drawable.defaultuser).showImageOnLoading(R.drawable.defaultuser)
                .cacheInMemory(true).build();

        getSupportActionBar().setTitle("Profile");
        sharedPreference = new SharedPreference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fb_profileData = extras.getString("facebook_profileData");
            fb_profile_image = extras.getString("fb_profile_image");
//			sharedPreference.saveWithKey(context, fb_profileData, "facebook_profileData");
//			sharedPreference.saveWithKey(context, fb_profile_image, "fb_profile_image");
        } else {
            try {
                userProfileData = sharedPreference.getValueWithKey(context, "UserProfileData");
                fb_profileData = sharedPreference.getValueWithKey(context, "facebook_profileData");
                fb_profile_image = sharedPreference.getValueWithKey(context, "fb_profile_image");
            } catch (Exception ex) {
                finish();
            }
        }

        mLoginPrefs = getSharedPreferences(Globals.LOGIN_SPF_NAME, Context.MODE_PRIVATE);

        try {
            JSONObject obj = new JSONObject(fb_profileData);
            JSONObject obj1 = new JSONObject(obj.getString("facebook"));

            nameFromFb = obj1.getString("name");
            emailIDFromFb = obj1.getString("email");
            Log.d(TAG, "nameFromFb=" + nameFromFb);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        intializeViews();
        loadFacebookImage();


        if (Build.VERSION.SDK_INT >= 19) {
            linked_in_btn.setVisibility(View.VISIBLE);
        } else {
            linked_in_btn.setVisibility(View.GONE);
        }

        fetchPrepopulatedData();


    }

    private void callBackService() {
        if (Build.VERSION.SDK_INT == 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE);

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                startBackService();
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            startBackService();
        }
    }

    private void startBackService() {
        getApplicationContext().startService(new Intent(getBaseContext(), MyLocationUpdateServiceNew.class)
                .putExtra("Dashboard", true));
        Log.d(TAG, "updateMyLocation: Service started.................");
    }


    private void loadFacebookImage() {
        new BackgroundTask(ProfileActivity.this, new BackgroundThread() {

            @Override
            public Object runTask() {

                try {
                    Object response;
                    try {
                        response = Connection.callHttpGetRequestsV2(fb_profile_image + "?type=large&redirect=false");
                        return response;
                    } catch (Exception e) {
                        return e;
                    }


                } catch (Exception e) {
                }
                return null;
            }

            @Override
            public void taskCompleted(Object data) {

                JSONObject obj;
                try {
                    obj = new JSONObject(data.toString());
                    fb_profile_image = obj.getJSONObject("data").getString("url");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                sharedPreference.saveWithKey(context, fb_profileData, "facebook_profileData");
                sharedPreference.saveWithKey(context, fb_profile_image, "fb_profile_image");

//					imageLoader.DisplayImage(fb_profile_image, profilePic);
                imageLoader.displayImage(fb_profile_image, profilePic, doptions);

            }
        }).execute();


    }

   /* private void updateMyLocation(Context ctx, boolean value) {
        try {
            if (value) {
                ctx.startService(new Intent(ctx,
                        MyLocationUpdateServiceNew.class).putExtra("isFromProfile", true));
            } else {
                ctx.stopService(new Intent(ctx, MyLocationUpdateServiceNew.class));
                Log.d(TAG, "updateMyLocation: Service stopped.................");
            }

        } catch (Exception ex) {
        }
    }*/

    private void intializeViews() {

        next_btn = (Button) findViewById(R.id.next_btn);
        back_btn = (Button) findViewById(R.id.back_btn);
        save_btn = (Button) findViewById(R.id.save_btn);
        linked_in_btn = (Button) findViewById(R.id.linked_in_btn);

        name_headmaster = (EditText) findViewById(R.id.headmaster_name);
        name = (EditText) findViewById(R.id.et_name);
        email = (EditText) findViewById(R.id.et_email);
        topic2connect = (EditText) findViewById(R.id.et_topic_2_connect);
        jobTitle = (EditText) findViewById(R.id.et_job_title);
        orgName = (EditText) findViewById(R.id.et_org_name);

        profilePic = (com.goconnect.events.lib.CircularImageView) findViewById(R.id.profile_image);
//        orgPic = (com.goconnect.events.lib.CircularImageView) findViewById(R.id.org_image);

        collegeNames = (Spinner) findViewById(R.id.schollnames_spinner);
        graduationYears = (Spinner) findViewById(R.id.gradutaion_yr_spinner);

        profile_layout_one = (LinearLayout) findViewById(R.id.profile_part_one);
        profile_layout_two = (LinearLayout) findViewById(R.id.profile_part_two);


        next_btn.setOnClickListener(ProfileActivity.this);
        back_btn.setOnClickListener(ProfileActivity.this);
        save_btn.setOnClickListener(ProfileActivity.this);
        linked_in_btn.setOnClickListener(ProfileActivity.this);
//        orgPic.setOnClickListener(ProfileActivity.this);
        // if (speaker.getImageURl() != null) {
        // Call ImageLoader constructor to initialize FileCache
//		imageLoader = new ImageLoader(getApplicationContext());

        // DisplayImage function from ImageLoader Class
//		imageLoader.DisplayImage(fb_profile_image, profilePic);

        name.setText(nameFromFb);
        if (userProfileData != null) {

            profile_layout_one.setVisibility(View.GONE);
            profile_layout_two.setVisibility(View.VISIBLE);
//            email.setFocusable(true);
//            email.setInputType(InputType.TYPE_EMAIL);
            try {

                Log.d(TAG, "userProfileData inside try catch=" + userProfileData.toString());
                JSONObject jsonOject = new JSONObject(userProfileData.toString());
                topic2connect.setText(jsonOject.getJSONObject("user").get("topicToConnect").toString());
                selectedGraduationYr = jsonOject.getJSONObject("user").getJSONObject("affiliation").get("endYear").toString();
                jobTitle.setText(jsonOject.getJSONObject("user").getJSONObject("position").get("jobTitle").toString());
                orgName.setText(jsonOject.getJSONObject("user").getJSONObject("position").get("organisationName").toString());
                String emailFromUser = jsonOject.getJSONObject("user").getJSONObject("facebook").get("email").toString();
                Log.d(TAG, "emailFromUser" + emailFromUser.toString());
                email.setText(emailFromUser);
                name_headmaster.setText(jsonOject.getJSONObject("user").getJSONObject("affiliation").get("uwcContact").toString());
                selectedCollegeName = jsonOject.getJSONObject("user").getJSONObject("affiliation").get("college").toString();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
//            email.setInputType(InputType.TYPE);
//            email.setFocusable(false);
            email.setText(emailIDFromFb);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                if (checkValidationLayoutOne()) {
                    KeyboardUtil.hideKeyboard(ProfileActivity.this);
                    profile_layout_one.setVisibility(View.GONE);
                    profile_layout_two.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.back_btn:
                KeyboardUtil.hideKeyboard(ProfileActivity.this);
                profile_layout_one.setVisibility(View.VISIBLE);
                profile_layout_two.setVisibility(View.GONE);
                break;

            case R.id.save_btn:

                /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askPermissionGPS();                        // only for gingerbread and newer versions
                } else {
                    checkGps();
                }*/

                if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(this)) {
                    KeyboardUtil.hideKeyboard(ProfileActivity.this);
                    if (checkValidationLayoutTwo()) {
                        saveUserProfileData();
                    }
                } else {
                    SingletonUtil.getSingletonConfigInstance().showErrorDialog(this);
                }


                break;

            case R.id.linked_in_btn:
//                Intent intent = new Intent(this, LinkedInNewActivity.class);
//                startActivity(intent);
                login_linkedin();
                break;

/*
            case R.id.org_image:

                pickImageFromGallery();
                break;*/
            default:
                break;
        }

    }

    private void checkGps() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            final android.app.AlertDialog.Builder adb2 = new android.app.AlertDialog.Builder(
                    ProfileActivity.this);

            adb2.setMessage("Please turn ON GPS");
            adb2.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            adb2.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb2.show();
        } else
            onGpsIsOn();
    }


    /* private void askPermissionGPS() {

         if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             // TODO: Consider calling
             ActivityCompat.requestPermissions(ProfileActivity.this,
                     new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                     MY_PERMISSIONS_REQUEST_READ_LOCATION);

             //    ActivityCompat#requestPermissions
             // here to request the missing permissions, and then overriding
             //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
             //                                          int[] grantResults)
             // to handle the case where the user grants the permission. See the documentation
             // for ActivityCompat#requestPermissions for more details.
             return;
         } else {
             checkGps();
         }
     }

 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    checkGps();
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission denied!! ", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start back service

                    startBackService();
                    Log.d(TAG, "onRequestPermissionsResult: Service started");

                } else {
                    Toast.makeText(ProfileActivity.this, "Permission denied!! You won't get notifications ", Toast.LENGTH_SHORT).show();
                }
                break;


            case MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (googleApiClient != null) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                        }

                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission denied!!", Toast.LENGTH_SHORT).show();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void onGpsIsOn() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                coonectGoogleApiClient();
            }
        } else {
            coonectGoogleApiClient();
        }

    }

    private void coonectGoogleApiClient() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    private void setLatLongInSharedPref(Location location) {
        try {
            String lat = "" + location.getLatitude();
            String lng = "" + location.getLongitude();

            sharedPreference.saveWithKey(getApplicationContext(), "" + location.getLatitude(), "Latitude");
            sharedPreference.saveWithKey(getApplicationContext(), "" + location.getLongitude(), "Longitude");

//           saveUserProfileData();

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Toast.makeText(ProfileActivity.this, "GPS is connecting ..Please try again", Toast.LENGTH_SHORT).show();
//            saveUserProfileData();
        }

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void login_linkedin() {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {

                // Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();
//                linked_in_btn.setVisibility(View.GONE);
                Log.d(TAG, "Auth succeeded");
            }

            @Override
            public void onAuthError(LIAuthError error) {

                Log.d(TAG, "onAuthError: " + error.toString());
                Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);
    }

    // This method is used to make permissions to retrieve data from linkedin

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    // After complete authentication start new HomePage Activity

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        Intent intent = new Intent(ProfileActivity.this,UserProfile.class);
        startActivity(intent);
    }*/

   /* private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_GALLERY);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        //activity result after image fetched from gallery
        // TODO: 02-Jul-16 Don't delete it
        /*if (requestCode == PICK_IMAGE_FROM_GALLERY) {
            if (resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();

                // Get the Image's file name
//            String fileNameSegments[] = imgPath.split("/");
//            fileName = fileNameSegments[fileNameSegments.length - 1];
                // Put file name in Async Http Post Param which will used in Java web app


                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
//            HashMap<String, String> userDetails = db.getUserDetails();
//            cust_id = userDetails.get("customer_id");
//            Log.d(TAG, "cust_id:" + cust_id);

                mediaFile = new File(imgPath + File.separator + "_" + timeStamp + ".jpg");
                Log.d(TAG, "Image Name=" + mediaFile.getName());
                boolean isImage = true;
       *//*     Intent i = new Intent(ProfileActivity.this, UploadImgToServerActivity.class);
            i.putExtra("filePath", imgPath);
            i.putExtra("isImage", isImage);
            i.putExtra("fileName", mediaFile.getName());
            i.putExtra("custId", cust_id);
            startActivity(i);
            finish();*//*
                if (imgPath != null) {
                    // Displaying the image or video on the screen
                    previewMedia(isImage);
                } else {
                    Toast.makeText(ProfileActivity.this, "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } else

        {*/
        //result callback for fetched linkedin account details
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
//            Intent intent = new Intent(ProfileActivity.this, UserProfile.class);
//            startActivity(intent);
        Log.d(TAG, "requestCode=" + requestCode + "resultCode=" + resultCode);
        progress = new ProgressDialog(this);
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        getUserData();
       /* }*/

    }

    public void getUserData() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(ProfileActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    setUserProfile(result.getResponseDataAsJson());
                    progress.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.dismiss();
                    Toast.makeText(ProfileActivity.this, "Unable To connect Linkedin server.Please try again!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onApiError(LIApiError error) {
//                ((TextView) findViewById(R.id.error)).setText(error.toString());
                error.printStackTrace();
                progress.dismiss();
                Toast.makeText(ProfileActivity.this, "Unable To connect Linkedin server.Please try again!!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void setUserProfile(JSONObject response) {

        try {

            Log.d(TAG, response.get("firstName").toString());
            Log.d(TAG, response.get("lastName").toString());
            Log.d(TAG, response.get("headline").toString());
//            Log.d(TAG, response.get("formattedName").toString());

//            linked_in_btn.setVisibility(View.GONE);

            JSONObject positionsObj = response.getJSONObject("positions");
            publicProfileUrl = response.getString("publicProfileUrl");

            sharedPreference.saveWithKey(getApplicationContext(), publicProfileUrl, "publicProfileLinkedInUrl");


            JSONArray jsonArray = positionsObj.getJSONArray("values");
            String compnyName;
            String jobTitleString;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject Obj = jsonArray.getJSONObject(i);

                if (Obj.getBoolean("isCurrent")) {
                    JSONObject cmpnyObj = Obj.getJSONObject("company");
                    compnyName = cmpnyObj.getString("name");
                    jobTitleString = Obj.getString("title");
                    orgName.setText(compnyName);
                    jobTitle.setText(jobTitleString);
                } else {
                    Toast.makeText(ProfileActivity.this, "No current organization fetched. Please fill up details manually", Toast.LENGTH_LONG).show();
                }
            }


//            user_email.setText(response.get("firstName").toString());
//            user_name.setText(response.get("formattedName").toString());

//            Picasso.with(this).load(response.getString("pictureUrl"))
//                    .into(profile_pic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to show picked image in imageview
   /* private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
//            imgPreview.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

            orgPic.setImageBitmap(bitmap);
        }
    }*/

    private void saveUserProfileData() {

        String Latitude = sharedPreference.getValueWithKey(getApplicationContext(), "Latitude");
        String Longitude = sharedPreference.getValueWithKey(getApplicationContext(), "Longitude");

        if (Latitude == null || Longitude == null) {
            checkGps();
        } else {
            createRequestJSONForSaveProfile(Latitude, Longitude);
        }
    }

    private void createRequestJSONForSaveProfile(String latitude, String longitude) {
        JSONObject finalProfileObje = new JSONObject();
        JSONObject afflicationObj = new JSONObject();
        try {
            afflicationObj.put("uwcContact", name_headmaster.getText().toString().trim());
            afflicationObj.put("endYear", selectedGraduationYr);
            afflicationObj.put("college", selectedCollegeName);
            afflicationObj.put("mainAffiliation", "college");
            finalProfileObje.putOpt("affiliation", afflicationObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject currLocObj = new JSONObject();
        try {
            currLocObj.put("latitude", latitude);
            currLocObj.put("longitude", longitude);
            Log.e(TAG, "profile onLocation: Longitude" + longitude);
            Log.e(TAG, "profile onLocation: Latitude " + latitude);
//            updateMyLocation(getApplicationContext(), false);
            finalProfileObje.putOpt("currLocation", currLocObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject positionObj = new JSONObject();

        try {
            positionObj.put("organisationName", orgName.getText().toString().trim());
            positionObj.put("jobTitle", jobTitle.getText().toString().trim());
            finalProfileObje.putOpt("position", positionObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject linkedinProfileObj = new JSONObject();
        try {
            if (publicProfileUrl == null) {
                publicProfileUrl = sharedPreference.getValueWithKey(context, "publicProfileLinkedInUrl");
            }
            linkedinProfileObj.put("publicProfileUrl", publicProfileUrl);
            finalProfileObje.putOpt("linkedin", linkedinProfileObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        sharedPreference.saveWithKey(context, linkedinProfileObj.toString(), "UserProfileLinkedInData");

        JSONObject fbData = new JSONObject();
        try {
            JSONObject obj = new JSONObject(fb_profileData);
            fbData.put("facebookProfileImage", fb_profile_image);
            fbData.put("id", obj.getJSONObject("facebook").getString("id"));
            fbData.put("last_name", obj.getJSONObject("facebook").getString("last_name"));
            fbData.put("first_name", obj.getJSONObject("facebook").getString("first_name"));
            fbData.put("email", email.getText().toString().trim());
            fbData.put("name", obj.getJSONObject("facebook").getString("name"));

            finalProfileObje.put("facebook", fbData);
            finalProfileObje.put("topicToConnect", topic2connect.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateToEMedServer(finalProfileObje);

    }

    private boolean checkValidationLayoutTwo() {
        boolean isValid = true;
        if (name.getText().toString().trim().length() == 0) {
            name.setError("Please enter name.");
            name.requestFocus();
            isValid = false;

        } else if (email.getText().toString().trim().length() == 0) {
            email.setError("Please enter email.");
            email.requestFocus();
            isValid = false;
        } else if (topic2connect.getText().toString().trim().length() == 0) {
            topic2connect.setError("Please enter topic to connect.");
            topic2connect.requestFocus();
            isValid = false;
        } else if (topic2connect.getText().toString().trim().length() > 20) {
            topic2connect.setError("Topic name should be maximum 20 characters long.");
            topic2connect.requestFocus();
            isValid = false;
        } else if (jobTitle.getText().toString().trim().length() == 0) {
            jobTitle.setError("Please enter job title.");
            jobTitle.requestFocus();
            isValid = false;
        } else if (jobTitle.getText().toString().trim().length() > 50) {
            jobTitle.setError("Job title should be maximum 50 characters long.");
            jobTitle.requestFocus();
            isValid = false;
        } else if (orgName.getText().toString().trim().length() == 0) {
            orgName.setError("Please enter organization name.");
            orgName.requestFocus();
            isValid = false;
        } else if (orgName.getText().toString().trim().length() > 20) {
            orgName.setError("Organization name should be maximum 20 characters long.");
            orgName.requestFocus();
            isValid = false;
        } else
            isValid = true;

        return isValid;
    }

    private boolean checkValidationLayoutOne() {
        boolean isValid = true;
        if (selectedCollegeName.equals("* Select a College/School")) {
            Toast.makeText(getApplicationContext(), "Please select School/College.", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (selectedGraduationYr.equals("* What is your year of graduation")) {
            Toast.makeText(getApplicationContext(), "Please select year of graduation.", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (name_headmaster.getText().toString().trim().length() == 0) {
            name_headmaster.setError("Please select.");
            isValid = false;
        } else
            isValid = true;

        return isValid;
    }

    public void updateToEMedServer(final JSONObject finalProfileObje) {

        new BackgroundTask(ProfileActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {

                    if (!Network.isAvailable(ProfileActivity.this)) {
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

                                Log.d(TAG, "updateToEMedServer: " + data.toString());
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
                                } else if (regResponse.getStatus().equals("SUCCESS")
                                        || regResponse.getStatus().equals("Existed user details updated")) {

                                    /** saving credentials here **/
                                    saveCredentials();
                                    //save profile page data
                                    sharedPreference.saveWithKey(context, data.toString(), "UserProfileData");

                                    Log.d(TAG, "UserProfileData=" + data.toString());
                                    Intent i;
                                    i = new Intent(getApplicationContext(), Dashboard.class);
                                    i.putExtra("facebook_profileData", fb_profileData);
                                    i.putExtra("fb_profile_image", fb_profile_image);
                                    startActivity(i);
                                    finish();
                                } else if (regResponse.getStatus().equals("VERIFICTION_FAILD")) {
                                    Toast.makeText(ProfileActivity.this, "Please enter valid last name of principal", Toast.LENGTH_LONG).show();
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
                        response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/saveProfile",
                                finalProfileObje);
                        Log.d(TAG, "updateToEMedServer: " + Connection.URL_BASE + "api/users/saveProfile" +
                                finalProfileObje);
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

    public void fetchPrepopulatedData() {

        new BackgroundTask(ProfileActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {

                try {
                    if (!Network.isAvailable(ProfileActivity.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            Log.d(TAG, jsonObject.toString());
                            if (data.toString().contains("errMsg")) {
                                String errorMessage = jsonObject.getString("errMsg");
                                DialogBox newFragment = DialogBox.newInstance(errorMessage);
                                newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                            } else {

                                //for prepopulating names
                                JSONArray jsonArrayNames = jsonObject.getJSONArray("names");
                                String[] colleges = new String[jsonArrayNames.length()];
                                for (int i = 0; i < jsonArrayNames.length(); i++) {
                                    String strr = jsonArrayNames.getString(i);
                                    colleges[i] = strr.toString();
                                }

                                //for prepopulating gratuation years
                                JSONArray jsonArrayYears = jsonObject.getJSONArray("years");
                                String[] graduationYrs = new String[jsonArrayYears.length()];
                                for (int i = 0; i < jsonArrayYears.length(); i++) {
                                    String strr = jsonArrayYears.getString(i);
                                    graduationYrs[i] = strr.toString();
                                }

                                setValuesInSpinner(colleges, graduationYrs);
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
                        response = Connection.callHttpGetRequestsV2(Connection.URL_BASE + "api/users/collegeInfo");

                        Log.d(TAG, "url=" + Connection.URL_BASE + "api/users/collegeInfo");
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

    private void setValuesInSpinner(final String[] colleges, final String[] graduationYrs) {
        ArrayAdapter<String> collegeNamesArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_spinner_dropdown_item, colleges);
        collegeNamesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeNames.setAdapter(collegeNamesArrayAdapter);

        ArrayAdapter<String> yr_graduation_ArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_spinner_dropdown_item, graduationYrs);
        yr_graduation_ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graduationYears.setAdapter(yr_graduation_ArrayAdapter);

        collegeNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int possition, long arg3) {

                selectedCollegeName = colleges[possition];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                selectedCollegeName = "";
            }
        });

        graduationYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int possition, long arg3) {

                selectedGraduationYr = graduationYrs[possition];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
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
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API);

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                if (googleApiClient != null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                }
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                if (googleApiClient != null) {
                    googleApiClient.connect();
                }

            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((Dashboard) getActivity()).onDialogDismissed();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: " + location);
        mLastLocation = location;
        if (mLastLocation != null) {
            setLatLongInSharedPref(mLastLocation);
        }

        Log.d(TAG, "onLocationChanged: Lat=" + mLastLocation.getLatitude() + "Long=" + mLastLocation.getLongitude());
    }
}
