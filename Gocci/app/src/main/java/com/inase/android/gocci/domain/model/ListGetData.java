package com.inase.android.gocci.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ListGetData {

    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";
    private static final String TAG_FOLLOW_FLAG = "follow_flag";

    private static final String TAG_REST_ID = "rest_id";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_LOCALITY = "locality";

    private String user_id;
    private String username;
    private String profile_img;
    private int follow_flag;

    private String rest_id;
    private String restname;
    private String locality;

    public ListGetData() {
    }

    public ListGetData(String user_id, String username, String profile_img, int follow_flag) {
        this.user_id = user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.follow_flag = follow_flag;
    }

    public ListGetData(String rest_id, String restname, String locality) {
        this.rest_id = rest_id;
        this.restname = restname;
        this.locality = locality;
    }

    public static ListGetData createUserData(JSONObject jsonObject) {
        try {
            String user_id = jsonObject.getString(TAG_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            int follow_flag = jsonObject.getInt(TAG_FOLLOW_FLAG);

            return new ListGetData(user_id, username, profile_img, follow_flag);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ListGetData createRestData(JSONObject jsonObject) {
        try {
            String rest_id = jsonObject.getString(TAG_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            String localoty = jsonObject.getString(TAG_LOCALITY);

            return new ListGetData(rest_id, restname, localoty);

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

    public int getFollow_flag() {
        return follow_flag;
    }

    public void setFollow_flag(int follow_flag) {
        this.follow_flag = follow_flag;
    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
