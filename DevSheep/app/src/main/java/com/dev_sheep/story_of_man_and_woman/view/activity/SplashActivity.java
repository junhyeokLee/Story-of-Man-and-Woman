package com.dev_sheep.story_of_man_and_woman.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;


public class SplashActivity extends Activity {
    private boolean saveLoginData;
    private static int SPLASH_TIME_OUT = 100;
    private Intent intent;

    private String email;
    private String pwd;
    private String loginEmail, loginPassword;


    private SharedPreferences appData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if(saveLoginData) {
//                    autoLogin();
//                }
//                else {
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));

                finish();
//                }
            }
        },SPLASH_TIME_OUT);

    }


}
