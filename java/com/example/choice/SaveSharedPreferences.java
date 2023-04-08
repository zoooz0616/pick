package com.example.choice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {
    static final String PREF_USER_EMAIL = "email";
    static final String PREF_USER_PWD = "pwd";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 사용자 정보 저장
    public static void setUserEmail(Context ctx, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, email);
        editor.commit();
    }

    public static void setUserPwd(Context ctx, String pwd) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PWD, pwd);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }

    // 저장된 정보 가져오기
    public static String getUserPwd(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PWD, "");
    }

    // 로그아웃 (앱 내에서 로그아웃 누를 경우 사용할 예정)
    public static void clearUser(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
