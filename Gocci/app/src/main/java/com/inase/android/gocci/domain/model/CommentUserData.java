package com.inase.android.gocci.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kinagafuji on 15/08/29.
 */
public class CommentUserData {

    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USERNAME = "username";

    private int user_id;
    private String username;

    public CommentUserData(int user_id, String username) {
        this.user_id = user_id;
        this.username = username;
    }

    public static CommentUserData createCommentUserData(JSONObject jsonObject) {
        try {
            int user_id = jsonObject.getInt(TAG_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);

            return new CommentUserData(user_id, username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUserName() {
        return username;
    }

}
