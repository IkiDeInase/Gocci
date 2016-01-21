package com.inase.android.gocci.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kinagafuji on 16/01/21.
 */
public class SearchUserData {

    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";
    private static final String TAG_GOCHI_NUM = "gochi_num";
    private static final String TAG_FOLLOW_FLAG = "follow_flag";

    private String user_id;
    private String username;
    private String profile_img;
    private int gochi_num;
    private boolean follow_flag;

    public SearchUserData(String user_id, String username, String profile_img, int gochi_num, boolean follow_flag) {
        this.user_id = user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.gochi_num = gochi_num;
        this.follow_flag = follow_flag;
    }

    public static SearchUserData createSearchUserData(JSONObject jsonObject) {
        try {
            String user_id = jsonObject.getString(TAG_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            boolean follow_flag = jsonObject.getBoolean(TAG_FOLLOW_FLAG);

            return new SearchUserData(user_id, username, profile_img, gochi_num, follow_flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public int getGochi_num() {
        return gochi_num;
    }

    public void setGochi_num(int gochi_num) {
        this.gochi_num = gochi_num;
    }

    public boolean isFollow_flag() {
        return follow_flag;
    }

    public void setFollow_flag(boolean follow_flag) {
        this.follow_flag = follow_flag;
    }
}
