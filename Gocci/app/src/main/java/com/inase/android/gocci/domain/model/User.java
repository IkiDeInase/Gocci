package com.inase.android.gocci.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class User {

    private static final String TAG_CODE = "code";
    private static final String TAG_USERID = "user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";
    private static final String TAG_IDENTITY_ID = "identity_id";
    private static final String TAG_BADGE_NUM = "badge_num";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_TOKEN = "token";

    private int code;
    private String message;
    private int userId;
    private String userName;
    private String profileImg;
    private String identityId;
    private int badgeNum;
    private String token;

    public User() {
    }

    public User(int code, String message, int userId, String userName, String profileImg, String identityId,
                int badgeNum, String token) {
        this.code = code;
        this.message = message;
        this.userId = userId;
        this.userName = userName;
        this.profileImg = profileImg;
        this.identityId = identityId;
        this.badgeNum = badgeNum;
        this.token = token;
    }

    public static User createUser(JSONObject jsonObject) {
        try {
            int code = jsonObject.getInt(TAG_CODE);
            String message = jsonObject.getString(TAG_MESSAGE);
            int userId = jsonObject.getInt(TAG_USERID);
            String userName = jsonObject.getString(TAG_USERNAME);
            String profileImg = jsonObject.getString(TAG_PROFILE_IMG);
            String identityId = jsonObject.getString(TAG_IDENTITY_ID);
            int badgeNum = jsonObject.getInt(TAG_BADGE_NUM);
            String token = jsonObject.getString(TAG_TOKEN);

            return new User(code, message, userId, userName, profileImg,
                    identityId, badgeNum, token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public int getBadgeNum() {
        return badgeNum;
    }

    public void setBadgeNum(int badgeNum) {
        this.badgeNum = badgeNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
