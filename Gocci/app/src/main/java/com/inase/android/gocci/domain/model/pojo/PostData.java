package com.inase.android.gocci.domain.model.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class PostData {

    //タイムライン　
    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_POST_USER_ID = "user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";
    private static final String TAG_POST_REST_ID = "rest_id";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_TAG = "tag";
    private static final String TAG_VALUE = "value";
    private static final String TAG_MEMO = "memo";
    private static final String TAG_POST_DATE = "post_date";
    private static final String TAG_CHEER_FLAG = "cheer_flag";
    private static final String TAG_GOCHI_NUM = "gochi_num";
    private static final String TAG_COMMENT_NUM = "comment_num";
    private static final String TAG_WANT_FLAG = "want_flag";
    private static final String TAG_FOLLOW_FLAG = "follow_flag";
    private static final String TAG_GOCHI_FLAG = "gochi_flag";
    private static final String TAG_LON = "X(lon_lat)";
    private static final String TAG_LAT = "Y(lon_lat)";
    private static final String TAG_SHARE = "share";

    private static final String TAG_DISTANCE = "distance";

    //JSON用のsetter/getter

    //タイムライン
    private String post_id;
    private int post_user_id;
    private String username;
    private String profile_img;
    private int post_rest_id;
    private String restname;
    private String movie;
    private String thumbnail;
    private String category;
    private String tag;
    private String value;
    private String memo;
    private String post_date;
    private int cheer_flag;
    private int gochi_num;
    private int comment_num;
    private int want_flag;
    private int follow_flag;
    private int gochi_flag;
    private double lat;
    private double lon;
    private String share;

    private int distance;

    public PostData() {
    }

    public PostData(String post_id, int post_user_id, String username, String profile_img, int post_rest_id,
                    String restname, String movie, String thumbnail, String category, String tag,
                    String value, String memo, String post_date, int cheer_flag, int gochi_num,
                    int comment_num, int want_flag, int follow_flag, int gochi_flag, double lat,
                    double lon, String share) {
        this.post_id = post_id;
        this.post_user_id = post_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.post_rest_id = post_rest_id;
        this.restname = restname;
        this.movie = movie;
        this.thumbnail = thumbnail;
        this.category = category;
        this.tag = tag;
        this.value = value;
        this.memo = memo;
        this.post_date = post_date;
        this.cheer_flag = cheer_flag;
        this.gochi_num = gochi_num;
        this.comment_num = comment_num;
        this.want_flag = want_flag;
        this.follow_flag = follow_flag;
        this.gochi_flag = gochi_flag;
        this.lat = lat;
        this.lon = lon;
        this.share = share;
    }

    public PostData(String post_id, int post_user_id, String username, String profile_img, int post_rest_id,
                    String restname, String movie, String thumbnail, String category, String tag,
                    String value, String memo, String post_date, int cheer_flag, int gochi_num,
                    int comment_num, int want_flag, int follow_flag, int gochi_flag, double lat,
                    double lon, String share, int distance) {
        this.post_id = post_id;
        this.post_user_id = post_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.post_rest_id = post_rest_id;
        this.restname = restname;
        this.movie = movie;
        this.thumbnail = thumbnail;
        this.category = category;
        this.tag = tag;
        this.value = value;
        this.memo = memo;
        this.post_date = post_date;
        this.cheer_flag = cheer_flag;
        this.gochi_num = gochi_num;
        this.comment_num = comment_num;
        this.want_flag = want_flag;
        this.follow_flag = follow_flag;
        this.gochi_flag = gochi_flag;
        this.lat = lat;
        this.lon = lon;
        this.share = share;

        this.distance = distance;
    }

    public static PostData createPostData(JSONObject jsonObject) {
        try {
            String post_id = jsonObject.getString(TAG_POST_ID);
            int post_user_id = jsonObject.getInt(TAG_POST_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            int post_rest_id = jsonObject.getInt(TAG_POST_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            String movie = jsonObject.getString(TAG_MOVIE);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            String category = jsonObject.getString(TAG_CATEGORY);
            String tag = jsonObject.getString(TAG_TAG);
            String value = jsonObject.getString(TAG_VALUE);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_date = jsonObject.getString(TAG_POST_DATE);
            int cheer_flag = jsonObject.getInt(TAG_CHEER_FLAG);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            int comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
            int want_flag = jsonObject.getInt(TAG_WANT_FLAG);
            int follow_flag = jsonObject.getInt(TAG_FOLLOW_FLAG);
            int gochi_flag = jsonObject.getInt(TAG_GOCHI_FLAG);
            double lat = jsonObject.getDouble(TAG_LAT);
            double lon = jsonObject.getDouble(TAG_LON);
            String share = jsonObject.getString(TAG_SHARE);

            return new PostData(post_id, post_user_id, username, profile_img, post_rest_id,
                    restname, movie, thumbnail, category, tag,
                    value, memo, post_date, cheer_flag, gochi_num,
                    comment_num, want_flag, follow_flag, gochi_flag, lat,
                    lon, share);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PostData createDistPostData(JSONObject jsonObject) {
        try {
            String post_id = jsonObject.getString(TAG_POST_ID);
            int post_user_id = jsonObject.getInt(TAG_POST_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            int post_rest_id = jsonObject.getInt(TAG_POST_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            String movie = jsonObject.getString(TAG_MOVIE);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            String category = jsonObject.getString(TAG_CATEGORY);
            String tag = jsonObject.getString(TAG_TAG);
            String value = jsonObject.getString(TAG_VALUE);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_date = jsonObject.getString(TAG_POST_DATE);
            int cheer_flag = jsonObject.getInt(TAG_CHEER_FLAG);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            int comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
            int want_flag = jsonObject.getInt(TAG_WANT_FLAG);
            int follow_flag = jsonObject.getInt(TAG_FOLLOW_FLAG);
            int gochi_flag = jsonObject.getInt(TAG_GOCHI_FLAG);
            double lat = jsonObject.getDouble(TAG_LAT);
            double lon = jsonObject.getDouble(TAG_LON);
            String share = jsonObject.getString(TAG_SHARE);

            int distance = jsonObject.getInt(TAG_DISTANCE);

            return new PostData(post_id, post_user_id, username, profile_img, post_rest_id,
                    restname, movie, thumbnail, category, tag,
                    value, memo, post_date, cheer_flag, gochi_num,
                    comment_num, want_flag, follow_flag, gochi_flag, lat,
                    lon, share, distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public int getPost_user_id() {
        return post_user_id;
    }

    public void setPost_user_id(int post_user_id) {
        this.post_user_id = post_user_id;
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

    public int getPost_rest_id() {
        return post_rest_id;
    }

    public void setPost_rest_id(int post_rest_id) {
        this.post_rest_id = post_rest_id;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public int getCheer_flag() {
        return cheer_flag;
    }

    public void setCheer_flag(int cheer_flag) {
        this.cheer_flag = cheer_flag;
    }

    public int getGochi_num() {
        return gochi_num;
    }

    public void setGochi_num(int gochi_num) {
        this.gochi_num = gochi_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getWant_flag() {
        return want_flag;
    }

    public void setWant_flag(int want_flag) {
        this.want_flag = want_flag;
    }

    public int getFollow_flag() {
        return follow_flag;
    }

    public void setFollow_flag(int follow_flag) {
        this.follow_flag = follow_flag;
    }

    public int getGochi_flag() {
        return gochi_flag;
    }

    public void setGochi_flag(int gochi_flag) {
        this.gochi_flag = gochi_flag;
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

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
