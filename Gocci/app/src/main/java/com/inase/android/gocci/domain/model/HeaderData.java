package com.inase.android.gocci.domain.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/07/06.
 */
public class HeaderData {

    //追加メモ
    private static final String TAG_MEMO = "memo";
    private static final String TAG_POST_DATE = "post_date";

    //追加コメントページ
    private static final String TAG_COMMENT_ID = "comment_id";
    private static final String TAG_COMMENT_USER_ID = "comment_user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_COMMENT_DATE = "comment_date";
    private static final String TAG_RE_USER = "re_user";

    //追加店舗ページ
    private static final String TAG_REST_ID = "rest_id";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_TELL = "tell";
    private static final String TAG_HOMEPAGE = "homepage";
    private static final String TAG_REST_CATEGORY = "rest_category";
    private static final String TAG_WANT_FLAG = "want_flag";

    //追加ユーザーページ
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_FOLLOW_NUM = "follow_num";
    private static final String TAG_FOLLOWER_NUM = "follower_num";
    private static final String TAG_CHEER_NUM = "cheer_num";
    private static final String TAG_WANT_NUM = "want_num";
    private static final String TAG_FOLLOW_FLAG = "follow_flag";

    //通知
    private static final String TAG_NOTICE_ID = "notice_id";
    private static final String TAG_NOTICE_USER_ID = "notice_a_user_id";
    private static final String TAG_NOTICE = "notice";
    private static final String TAG_NOTICE_POST_ID = "notice_post_id";
    private static final String TAG_READ_FLAG = "read_flag";
    private static final String TAG_NOTICE_DATE = "notice_date";

    private static final String TAG_TOTAL_CHEER_NUM = "total_cheer_num";
    private static final String TAG_TOTAL_WANT_NUM = "total_want_num";

    private String memo;
    private String post_date;

    //コメントページ
    private String comment_id;
    private String comment_user_id;
    private String username;
    private String profile_img;
    private String comment;
    private String comment_date;
    private ArrayList<CommentUserData> comment_user_data = new ArrayList<>();

    //店舗ページ
    private String rest_id;
    private String restname;
    private String locality;
    private double lat;
    private double lon;
    private String tell;
    private String homepage;
    private String rest_category;
    private int want_flag;

    //ユーザーページ
    private String user_id;
    private int follow_num;
    private int follower_num;
    private int cheer_num;
    private int want_num;
    private int follow_flag;

    //通知
    private String notice_id;
    private String notice_user_id;
    private String notice;
    private String notice_post_id;
    private int read_flag;
    private String notice_date;

    //ニアー
    //リスト

    private int total_cheer_num;

    public HeaderData() {
    }

    public HeaderData(String user_id, String username, String profile_img, String memo, String post_date) {
        this.user_id = user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.memo = memo;
        this.post_date = post_date;
    }

    //コメント
    public HeaderData(String comment_id, String comment_user_id, String username, String profile_img, String comment, String comment_date, ArrayList<CommentUserData> comment_user_data) {
        this.comment_id = comment_id;
        this.comment_user_id = comment_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.comment = comment;
        this.comment_date = comment_date;
        this.comment_user_data = comment_user_data;
    }

    //店舗
    public HeaderData(String rest_id, String restname, String locality, double lat, double lon,
                      String tell, String homepage, String rest_category, int want_flag) {
        this.rest_id = rest_id;
        this.restname = restname;
        this.locality = locality;
        this.lat = lat;
        this.lon = lon;
        this.tell = tell;
        this.homepage = homepage;
        this.rest_category = rest_category;
        this.want_flag = want_flag;
    }

    //ユーザー
    public HeaderData(String user_id, String username, String profile_img, int follow_num, int follower_num,
                      int cheer_num, int want_num, int follow_flag) {
        this.user_id = user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.follow_num = follow_num;
        this.follower_num = follower_num;
        this.cheer_num = cheer_num;
        this.want_num = want_num;
        this.follow_flag = follow_flag;
    }

    //通知
    public HeaderData(String notice_id, String notice_user_id, String username, String profile_img, String notice,
                      String notice_post_id, int read_flag, String notice_date) {
        this.notice_id = notice_id;
        this.notice_user_id = notice_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.notice = notice;
        this.notice_post_id = notice_post_id;
        this.read_flag = read_flag;
        this.notice_date = notice_date;
    }

    public static HeaderData createMemoData(JSONObject jsonObject) {
        try {
            String user_id = jsonObject.getString(TAG_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_date = jsonObject.getString(TAG_POST_DATE);

            return new HeaderData(user_id, username, profile_img, memo, post_date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HeaderData createCommentData(JSONObject jsonObject) {
        try {
            String comment_id = jsonObject.getString(TAG_COMMENT_ID);
            String comment_user_id = jsonObject.getString(TAG_COMMENT_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            String comment = jsonObject.getString(TAG_COMMENT);
            String comment_date = jsonObject.getString(TAG_COMMENT_DATE);
            ArrayList<CommentUserData> commentUserDatas = new ArrayList<>();
            JSONArray re_user_array = jsonObject.getJSONArray(TAG_RE_USER);
            for (int i = 0; i < re_user_array.length(); i++) {
                JSONObject re_user_obj = re_user_array.getJSONObject(i);
                commentUserDatas.add(CommentUserData.createCommentUserData(re_user_obj));
            }

            return new HeaderData(comment_id, comment_user_id, username, profile_img, comment, comment_date, commentUserDatas);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HeaderData createTenpoHeaderData(JSONObject jsonObject) {
        try {
            String rest_id = jsonObject.getString(TAG_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            String localoty = jsonObject.getString(TAG_LOCALITY);
            double lat = jsonObject.getDouble(TAG_LAT);
            double lon = jsonObject.getDouble(TAG_LON);
            String tell = jsonObject.getString(TAG_TELL);
            String homepage = jsonObject.getString(TAG_HOMEPAGE);
            String rest_category = jsonObject.getString(TAG_REST_CATEGORY);
            int want_flag = jsonObject.getInt(TAG_WANT_FLAG);

            return new HeaderData(rest_id, restname, localoty, lat, lon,
                    tell, homepage, rest_category, want_flag);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HeaderData createUserHeaderData(JSONObject jsonObject) {
        try {
            String user_id = jsonObject.getString(TAG_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            int follow_num = jsonObject.getInt(TAG_FOLLOW_NUM);
            int follower_num = jsonObject.getInt(TAG_FOLLOWER_NUM);
            int cheer_num = jsonObject.getInt(TAG_CHEER_NUM);
            int want_num = jsonObject.getInt(TAG_WANT_NUM);
            int follow_flag = jsonObject.getInt(TAG_FOLLOW_FLAG);

            return new HeaderData(user_id, username, profile_img, follow_num, follower_num,
                    cheer_num, want_num, follow_flag);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HeaderData createNoticeHeaderData(JSONObject jsonObject) {
        try {
            String notice_id = jsonObject.getString(TAG_NOTICE_ID);
            String notice_user_id = jsonObject.getString(TAG_NOTICE_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            String notice = jsonObject.getString(TAG_NOTICE);
            String notice_post_id = jsonObject.getString(TAG_NOTICE_POST_ID);
            int read_flag = jsonObject.getInt(TAG_READ_FLAG);
            String notice_date = jsonObject.getString(TAG_NOTICE_DATE);

            return new HeaderData(notice_id, notice_user_id, username, profile_img, notice,
                    notice_post_id, read_flag, notice_date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getRest_category() {
        return rest_category;
    }

    public void setRest_category(String rest_category) {
        this.rest_category = rest_category;
    }

    public int getWant_flag() {
        return want_flag;
    }

    public void setWant_flag(int want_flag) {
        this.want_flag = want_flag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }

    public int getFollower_num() {
        return follower_num;
    }

    public void setFollower_num(int follower_num) {
        this.follower_num = follower_num;
    }

    public int getCheer_num() {
        return cheer_num;
    }

    public void setCheer_num(int cheer_num) {
        this.cheer_num = cheer_num;
    }

    public int getWant_num() {
        return want_num;
    }

    public void setWant_num(int want_num) {
        this.want_num = want_num;
    }

    public int getFollow_flag() {
        return follow_flag;
    }

    public void setFollow_flag(int follow_flag) {
        this.follow_flag = follow_flag;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_user_id() {
        return notice_user_id;
    }

    public void setNotice_user_id(String notice_user_id) {
        this.notice_user_id = notice_user_id;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getNotice_post_id() {
        return notice_post_id;
    }

    public void setNotice_post_id(String notice_post_id) {
        this.notice_post_id = notice_post_id;
    }

    public int getRead_flag() {
        return read_flag;
    }

    public void setRead_flag(int read_flag) {
        this.read_flag = read_flag;
    }

    public String getNotice_date() {
        return notice_date;
    }

    public void setNotice_date(String notice_date) {
        this.notice_date = notice_date;
    }

    public ArrayList<CommentUserData> getComment_user_data() {
        return comment_user_data;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
