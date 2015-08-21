package com.inase.android.gocci.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.PersistentCookieStore;

import java.util.StringTokenizer;

/**
 * Created by kinagafuji on 15/05/06.
 */
public class SavedData {

    private static final String KEY_SERVER_NAME = "ServerName";
    private static final String KEY_SERVER_NAME_ID = "ServerNameID";
    private static final String KEY_SERVER_PICTURE = "ServerPicture";

    private static final String KEY_LOGIN_JUDGE = "judge";

    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_REGID = "regId";

    private static final String KEY_IDENTITYID = "identityId";
    private static final String KEY_FLAG = "flag";

    public static void setWelcome(Context context, String username, String picture, String user_id, String identityId, int badge_num) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, username);
        editor.putString(KEY_SERVER_PICTURE, picture);
        editor.putString(KEY_SERVER_NAME_ID, user_id);
        editor.putString(KEY_IDENTITYID, identityId);
        editor.putInt(KEY_NOTIFICATION, badge_num);
        editor.apply();
    }

    public static void changeProfile(Context context, String name, String picture) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, name);
        editor.putString(KEY_SERVER_PICTURE, picture);
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

    public static void setServerName(Context context, String name) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SERVER_NAME, name);
        editor.apply();
    }

    public static void setServerPicture(Context context, String picture) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SERVER_PICTURE, picture);
        editor.apply();
    }

    public static void setLoginJudge(Context context, String judge) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_LOGIN_JUDGE, judge);
        editor.apply();
    }

    public static String getServerUserId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_SERVER_NAME_ID, "1");
    }

    public static String getRegId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_REGID, null);
    }

    public static String getLoginJudge(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_LOGIN_JUDGE, "no judge");
    }

    public static String getIdentityId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_IDENTITYID, "no identityId");
    }

    public static int getFlag(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_FLAG, -1);
    }

    public static int getNotification(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_NOTIFICATION, 0);
    }

    public static void setNotification(Context context, int notification) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NOTIFICATION, notification);
        editor.apply();
    }

    public static void setRegId(Context context, String regId) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_REGID, regId);
        editor.apply();
    }

    public static void setIdentityId(Context context, String identityId) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_IDENTITYID, identityId);
        editor.apply();
    }

    public static void setFlag(Context context, int flag) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_FLAG, flag);
        editor.apply();
    }

    public static void setSettingNotifications(Context context, Integer[] notifications) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < notifications.length; i++) {
            str.append(notifications[i]).append(",");
        }
        editor.putString("notifications", str.toString());
        editor.putInt("notifications_size", notifications.length);
        editor.apply();
    }

    public static Integer[] getSettingNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String savedString = prefs.getString("notifications", "0,1,2,3");
        int size = prefs.getInt("notifications_size", 4);
        StringTokenizer st = new StringTokenizer(savedString, ",");
        Integer[] savedList = new Integer[size];
        for (int i = 0; i < size; i++) {
            savedList[i] = Integer.parseInt(st.nextToken());
        }
        return savedList;
    }

    public static PersistentCookieStore getCookieStore(Context context) {
        return new PersistentCookieStore(context);
    }
}
