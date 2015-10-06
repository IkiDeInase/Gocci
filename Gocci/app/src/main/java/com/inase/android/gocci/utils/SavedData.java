package com.inase.android.gocci.utils;

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

    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_NOTIFICATIONS_SIZE = "notifications_size";

    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_AUTOPLAY = "autoplay";
    private static final String KEY_MUTE = "mute";

    private static final int STATE_MUTE = -1;
    private static final int STATE_UNMUTE = 0;

    private static final String KEY_REGIONS = "regions";

    private static final String KEY_REST_ID = "rest_id";
    private static final String KEY_RESTNAME = "restname";
    private static final String KEY_VIDEO_URL = "video_url";
    private static final String KEY_AWS_POST_NAME = "aws_post_name";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_TAG_ID = "tag_id";
    private static final String KEY_MEMO = "memo";
    private static final String KEY_VALUE = "value";
    private static final String KEY_ISNEWRESTNAME = "isNewRestname";
    private static final String KEY_LON = "lon";
    private static final String KEY_LAT = "lat";

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
        editor.putString(KEY_NOTIFICATIONS, str.toString());
        editor.putInt(KEY_NOTIFICATIONS_SIZE, notifications.length);
        editor.apply();
    }

    public static Integer[] getSettingNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String savedString = prefs.getString(KEY_NOTIFICATIONS, "0,1,2,3");
        int size = prefs.getInt(KEY_NOTIFICATIONS_SIZE, 4);
        StringTokenizer st = new StringTokenizer(savedString, ",");
        Integer[] savedList = new Integer[size];
        for (int i = 0; i < size; i++) {
            savedList[i] = Integer.parseInt(st.nextToken());
        }
        return savedList;
    }

    public static void setSettingAutoPlay(Context context, int setting) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_AUTOPLAY, setting);
        editor.apply();
    }

    public static void setSettingMute(Context context, int setting) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_MUTE, setting);
        editor.apply();
    }

    public static String getVersionName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getString(KEY_VERSION_NAME, "1.0.0");
    }

    public static void setVersionName(Context context, String versionName) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_VERSION_NAME, versionName);
        editor.apply();
    }

    public static int getSettingAutoPlay(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_AUTOPLAY, 0);
    }

    public static int getSettingMute(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MUTE, STATE_UNMUTE);
    }

    public static int getSettingRegions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_REGIONS, 0);
    }

    public static void setSettingRegions(Context context, int region) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_REGIONS, region);
        editor.apply();
    }

    public static void setPostVideoPreview(Context context, String restname, int rest_id, String video_url, String aws_post_name, int category_id, int tag_id,
                                           String memo, String value, boolean isNewRestname, double lon, double lat) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RESTNAME, restname);
        editor.putInt(KEY_REST_ID, rest_id);
        editor.putString(KEY_VIDEO_URL, video_url);
        editor.putString(KEY_AWS_POST_NAME, aws_post_name);
        editor.putInt(KEY_CATEGORY_ID, category_id);
        editor.putInt(KEY_TAG_ID, tag_id);
        editor.putString(KEY_MEMO, memo);
        editor.putString(KEY_VALUE, value);
        editor.putBoolean(KEY_ISNEWRESTNAME, isNewRestname);
        editor.putLong(KEY_LON, Double.doubleToRawLongBits(lon));
        editor.putLong(KEY_LAT, Double.doubleToRawLongBits(lat));
        editor.apply();
    }

    public static void setRestname(Context context, String restname) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RESTNAME, restname);
        editor.apply();
    }

    public static void setRest_id(Context context, int rest_id) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_REST_ID, rest_id);
        editor.apply();
    }

    public static void setCategory_id(Context context, int category_id) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CATEGORY_ID, category_id);
        editor.apply();
    }

    public static void setTag_id(Context context, int tag_id) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TAG_ID, tag_id);
        editor.apply();
    }

    public static void setMemo(Context context, String memo) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_MEMO, memo);
        editor.apply();
    }

    public static void setValue(Context context, String value) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_VALUE, value);
        editor.apply();
    }

    public static void setIsNewRestname(Context context, boolean isNew) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ISNEWRESTNAME, isNew);
        editor.apply();
    }

    public static void setLon(Context context, double longitude) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LON, Double.doubleToRawLongBits(longitude));
        editor.apply();
    }

    public static void setLat(Context context, double latitude) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAT, Double.doubleToRawLongBits(latitude));
        editor.apply();
    }

    public static String getRestname(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_RESTNAME, "");
    }

    public static int getRest_id(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_REST_ID, 1);
    }

    public static String getVideoUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_VIDEO_URL, "");
    }

    public static String getAwsPostname(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_AWS_POST_NAME, "");
    }

    public static int getCategory_id(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_CATEGORY_ID, 1);
    }

    public static int getTag_id(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TAG_ID, 1);
    }

    public static String getMemo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_MEMO, "");
    }

    public static String getValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_VALUE, "");
    }

    public static boolean getIsNewRestname(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ISNEWRESTNAME, false);
    }

    public static double getLon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_LON))
            return 0.0;

        return Double.longBitsToDouble(prefs.getLong(KEY_LON, 0));
    }

    public static double getLat(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_LAT))
            return 0.0;

        return Double.longBitsToDouble(prefs.getLong(KEY_LAT, 0));
    }

    public static PersistentCookieStore getCookieStore(Context context) {
        return new PersistentCookieStore(context);
    }
}
