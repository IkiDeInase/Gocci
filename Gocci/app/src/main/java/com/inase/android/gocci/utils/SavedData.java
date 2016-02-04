package com.inase.android.gocci.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by kinagafuji on 15/05/06.
 */
public class SavedData {

    private static final String KEY_SERVER_NAME = "ServerName";
    private static final String KEY_SERVER_NAME_ID = "ServerNameID";
    private static final String KEY_SERVER_PICTURE = "ServerPicture";

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
    private static final String KEY_MEMO = "memo";
    private static final String KEY_VALUE = "value";
    private static final String KEY_ISNEWRESTNAME = "isNewRestname";
    private static final String KEY_LON = "lon";
    private static final String KEY_LAT = "lat";

    private static final String KEY_POST_FINISHED = "post_finished";

    private static final String KEY_ID = "id";

    private static final String KEY_TOTAL_TIME = "total_time";
    private static final String KEY_MOVIE_NAME = "movie_name";

    public static void setWelcome(Context context, String username, String picture, String user_id, int badge_num) {
        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, username);
        editor.putString(KEY_SERVER_PICTURE, picture);
        editor.putString(KEY_SERVER_NAME_ID, user_id);
        editor.putInt(KEY_NOTIFICATION, badge_num);
        editor.apply();
    }

    public static int getPostingId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getInt(KEY_ID, 0);
    }

    public static void setPostingId(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_ID, id);
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

    public static String getServerUserId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_SERVER_NAME_ID, "1");
    }

    public static String getRegId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(KEY_REGID, null);
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
        return prefs.getString(KEY_VERSION_NAME, "3.0");
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

    public static void setPostVideoPreview(Context context, String restname, String rest_id, String video_url, String aws_post_name, int category_id,
                                           String memo, String value, boolean isNewRestname, String lon, String lat) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RESTNAME, restname);
        editor.putString(KEY_REST_ID, rest_id);
        editor.putString(KEY_VIDEO_URL, video_url);
        editor.putString(KEY_AWS_POST_NAME, aws_post_name);
        editor.putInt(KEY_CATEGORY_ID, category_id);
        editor.putString(KEY_MEMO, memo);
        editor.putString(KEY_VALUE, value);
        editor.putBoolean(KEY_ISNEWRESTNAME, isNewRestname);
        editor.putString(KEY_LON, lon);
        editor.putString(KEY_LAT, lat);
        editor.apply();
    }

    public static void setRestname(Context context, String restname) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RESTNAME, restname);
        editor.apply();
    }

    public static void setRest_id(Context context, String rest_id) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_REST_ID, rest_id);
        editor.apply();
    }

    public static void setCategory_id(Context context, int category_id) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CATEGORY_ID, category_id);
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

    public static void setVideoUrl(Context context, String videoUrl) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_VIDEO_URL, videoUrl);
        editor.apply();
    }

    public static void setAwsPostname(Context context, String awsPostname) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AWS_POST_NAME, awsPostname);
        editor.apply();
    }

    public static void setIsNewRestname(Context context, boolean isNew) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ISNEWRESTNAME, isNew);
        editor.apply();
    }

    public static void setLon(Context context, String longitude) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LON, longitude);
        editor.apply();
    }

    public static void setLat(Context context, String latitude) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAT, latitude);
        editor.apply();
    }

    public static void setPostFinished(Context context, boolean isPostFinished) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_POST_FINISHED, isPostFinished);
        editor.apply();
    }

    public static String getRestname(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_RESTNAME, "");
    }

    public static String getRest_id(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_REST_ID, "1");
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

    public static String getLon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_LON, "");
    }

    public static String getLat(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAT, "");
    }

    public static boolean getPostFinished(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_POST_FINISHED, false);
    }

    public static PersistentCookieStore getCookieStore(Context context) {
        return new PersistentCookieStore(context);
    }

    public static void saveList(Context ctx, String key, List<String> list) {
        JSONArray jsonAry = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonAry.put(list.get(i));
        }
        SharedPreferences prefs = ctx.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, jsonAry.toString());
        editor.apply();
    }

    // 設定値 ArrayList<String> を取得（Context は Activity や Application や Service）
    public static List<String> loadList(Context ctx, String key) {
        List<String> list = new ArrayList<String>();
        SharedPreferences prefs = ctx.getSharedPreferences("movie", Context.MODE_PRIVATE);
        String strJson = prefs.getString(key, ""); // 第２引数はkeyが存在しない時に返す初期値
        if (!strJson.equals("")) {
            try {
                JSONArray jsonAry = new JSONArray(strJson);
                for (int i = 0; i < jsonAry.length(); i++) {
                    list.add(jsonAry.getString(i));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public static void setTotalTime(Context context, int totalTime) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TOTAL_TIME, totalTime);
        editor.apply();
    }

    public static int getTotalTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TOTAL_TIME, 0);
    }

    public static void setMovieName(Context context, String movieName) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_MOVIE_NAME, movieName);
        editor.apply();
    }

    public static String getMovieName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("movie", Context.MODE_PRIVATE);
        return prefs.getString(KEY_MOVIE_NAME, "");
    }
}
