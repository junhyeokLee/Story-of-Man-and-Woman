package com.dev_sheep.story_of_man_and_woman.view.activity;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_EMAIL = "USER_EMAIL";
    static final String PREF_USER_PASSWORD = "USER_PASSWORD";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setLoginSave(Context ctx, String userEmail,String userPassword) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, userEmail);
        editor.putString(PREF_USER_PASSWORD, userPassword);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getLogin(Context ctx) {


        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }

    // 로그아웃
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}