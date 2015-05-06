package com.inase.android.gocci.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kinagafuji on 15/05/06.
 */
public class SavedData {

    //端末内で使うやつ
    private static String mName;
    private static String mPicture;
    private static String mBackground;
    private static int mFollower;
    private static int mFollowee;
    private static int mCheer;

    //ログイン時に投げるやつ
    private static String mLoginName;
    private static String mLoginPicture;

    private static final String KEY_SERVER_NAME = "ServerName";
    private static final String KEY_SERVER_PICTURE = "ServerPicture";
    private static final String KEY_SERVER_BACKGROUND = "ServerBackground";
    private static final String KEY_SERVER_FOLLOWEE = "ServerFollowee";
    private static final String KEY_SERVER_FOLLOWER = "ServerFollower";
    private static final String KEY_SERVER_CHEER = "ServerCheer";

    public static void setAccount(Context context, String name, String picture, String background, int followee, int follower, int cheer) {
        mName = name;
        mPicture = picture;
        mBackground = background;
        mFollowee = followee;
        mFollower = follower;
        mCheer = cheer;

        SharedPreferences prefs = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVER_NAME, mName);
        editor.putString(KEY_SERVER_PICTURE, mPicture);
        editor.putString(KEY_SERVER_BACKGROUND, mBackground);
        editor.putInt(KEY_SERVER_FOLLOWEE, mFollowee);
        editor.putInt(KEY_SERVER_FOLLOWER, mFollower);
        editor.putInt(KEY_SERVER_CHEER, mCheer);
        editor.apply();
    }
}
