package com.dev_sheep.story_of_man_and_woman.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dev_sheep.story_of_man_and_woman.BuildConfig;
import com.dev_sheep.story_of_man_and_woman.R;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class SplashActivity extends Activity {
    private final int CAMERA_CODE = 1111;
    private final int REQUEST_PERMISSION_CODE = 2222;

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

        checkTedPermission();

//        if(checkPermissions() == true){
//            Log.e("camera checked ok","OK");
//        }else{
//            checkPermission();
//
//            return;
//        }

    }

    // [퍼미션 체크] ==================================================================================
    private void checkTedPermission(){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                        finish();
                    }
                },SPLASH_TIME_OUT);

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SplashActivity.this, "애플리케이션 저장공간 권한을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

}


