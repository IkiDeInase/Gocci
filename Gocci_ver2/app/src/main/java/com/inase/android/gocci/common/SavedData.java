package com.inase.android.gocci.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.RequestParams;

/**
 * Created by kinagafuji on 15/05/06.
 */
public class SavedData {

    private static final String KEY_SERVER_NAME = "ServerName";
    private static final String KEY_SERVER_PICTURE = "ServerPicture";
    private static final String KEY_SERVER_BACKGROUND = "ServerBackground";
    private static final String KEY_SERVER_FOLLOWEE = "ServerFollowee";
    private static final String KEY_SERVER_FOLLOWER = "ServerFollower";
    private static final String KEY_SERVER_CHEER = "ServerCheer";

    private static final String KEY_LOGIN_NAME = "name";
    private static final String KEY_LOGIN_PICTURE = "picture";
    private static final String KEY_LOGIN_JUDGE = "judge";

    public static void setAccount(Context context, String name, String picture, String background, int followee, int follower, int cheer) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, name);
        editor.putString(KEY_SERVER_PICTURE, picture);
        editor.putString(KEY_SERVER_BACKGROUND, background);
        editor.putInt(KEY_SERVER_FOLLOWEE, followee);
        editor.putInt(KEY_SERVER_FOLLOWER, follower);
        editor.putInt(KEY_SERVER_CHEER, cheer);
        editor.apply();
    }

    public static void setLoginParam(Context context, String name, String picture, String judge) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOGIN_NAME, name);
        editor.putString(KEY_LOGIN_PICTURE, picture);
        editor.putString(KEY_LOGIN_JUDGE, judge);
        editor.apply();
    }

    public static void changeProfile(Context context, String name, String picture, String background) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, name);
        editor.putString(KEY_SERVER_PICTURE, picture);
        editor.putString(KEY_SERVER_BACKGROUND, background);
        editor.apply();
    }


    public static String getServerName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_SERVER_NAME, "ログアウトして下さい");
    }

    public static String getServerPicture(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_SERVER_PICTURE, "http://api-gocci.jp/img/s_1.png");
    }

    public static String getServerBackground(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_SERVER_BACKGROUND, "http://api-gocci.jp/img/back.png");
    }

    public static int getServerFollowee(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_SERVER_FOLLOWEE, -1);
    }

    public static int getServerFollower(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_SERVER_FOLLOWER, -1);
    }

    public static int getServerCheer(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_SERVER_CHEER, -1);
    }

    public static String getLoginName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_LOGIN_NAME, "dummy");
    }

    public static String getLoginPicture(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_LOGIN_PICTURE, "http://api-gocci.jp/img/s_1.png");
    }

    public static String getLoginJudge(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_LOGIN_JUDGE, "no judge");
    }

    public static void addFollower(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int follower = prefs.getInt(KEY_SERVER_FOLLOWER, -2);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_FOLLOWER, follower + 1);
        editor.apply();
    }

    public static void addFollowee(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int followee = prefs.getInt(KEY_SERVER_FOLLOWEE, -2);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_FOLLOWEE, followee + 1);
        editor.apply();
    }

    public static void addCheer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int cheer = prefs.getInt(KEY_SERVER_CHEER, -2);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_CHEER, cheer + 1);
        editor.apply();
    }

    public static void downFollower(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int follower = prefs.getInt(KEY_SERVER_FOLLOWER, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_FOLLOWER, follower - 1);
        editor.apply();
    }

    public static void downFollowee(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int followee = prefs.getInt(KEY_SERVER_FOLLOWEE, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_FOLLOWEE, followee - 1);
        editor.apply();
    }

    public static void downCheer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int cheer = prefs.getInt(KEY_SERVER_CHEER, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SERVER_CHEER, cheer - 1);
        editor.apply();
    }
}
