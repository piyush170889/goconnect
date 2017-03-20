package com.devjiva.goconnect;

/**
 *
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.goconnect.events.lib.Globals;

import org.brickred.socialauth.android.SocialAuthAdapter;

/**
 * @author Ravi
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;
    SharedPreferences mLoginPrefs;
    private String TAG = "SplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            private String userId;

            @Override
            public void run() {

                mLoginPrefs = getSharedPreferences(Globals.LOGIN_SPF_NAME, 0);
                userId = mLoginPrefs.getString("userFacebookId", "");
                if (userId.equalsIgnoreCase("")) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("username", "");
                    Log.d(TAG, TAG);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                    i.putExtra("username", "");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGHT);
    }

}