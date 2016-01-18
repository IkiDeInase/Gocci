package com.inase.android.gocci.domain.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PostData {

    //タイムライン　
    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_VALUE = "value";
    private static final String TAG_MEMO = "memo";
    private static final String TAG_POST_DATE = "post_date";
    private static final String TAG_CHEER_FLAG = "cheer_flag";

    private static final String TAG_POST_REST_ID = "rest_id";
    private static final String TAG_RESTNAME = "restname";

    private static final String TAG_POST_USER_ID = "user_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PROFILE_IMG = "profile_img";

    private static final String TAG_LON = "lon";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_MP4_MOVIE = "mp4_movie";
    private static final String TAG_HLS_MOVIE = "hls_movie";
    private static final String TAG_GOCHI_NUM = "gochi_num";
    private static final String TAG_GOCHI_FLAG = "gochi_flag";
    private static final String TAG_COMMENT_NUM = "comment_num";
    //JSON用のsetter/getter

    //タイムライン
    private String post_id;
    private String movie;
    private String thumbnail;
    private String category;
    private String value;
    private String memo;
    private String post_date;
    private boolean cheer_flag;

    private String post_rest_id;
    private String restname;

    private String post_user_id;
    private String username;
    private String profile_img;

    private double lon;
    private double lat;
    private String mp4_movie;
    private String hls_movie;
    private int gochi_num;
    private boolean gochi_flag;
    private int comment_num;

    private String locality;

    public PostData() {
    }

    //投稿
    public PostData(String post_id, String movie, String thumbnail, String category,
                    String value, String memo, String post_user_id, String username, String profile_img,
                    boolean cheer_flag, String post_date, String post_rest_id, String restname, String locality, String mp4_movie,
                    String hls_movie, int gochi_num, boolean gochi_flag, int comment_num) {
        this.post_id = post_id;
        this.movie = movie;
        this.thumbnail = thumbnail;
        this.category = category;
        this.value = value;
        this.memo = memo;
        this.post_user_id = post_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.cheer_flag = cheer_flag;
        this.post_date = post_date;
        this.post_rest_id = post_rest_id;
        this.restname = restname;
        this.locality = locality;
        this.mp4_movie = mp4_movie;
        this.hls_movie = hls_movie;
        this.gochi_num = gochi_num;
        this.gochi_flag = gochi_flag;
        this.comment_num = comment_num;
    }

    //店舗
    public PostData(String post_id, String movie, String thumbnail, String category,
                    String value, String memo, String post_date, boolean cheer_flag, String post_user_id,
                    String username, String profile_img, String mp4_movie, String hls_movie,
                    int gochi_num, boolean gochi_flag, int comment_num) {
        this.post_id = post_id;
        this.movie = movie;
        this.thumbnail = thumbnail;
        this.category = category;
        this.value = value;
        this.memo = memo;
        this.post_date = post_date;
        this.cheer_flag = cheer_flag;
        this.post_user_id = post_user_id;
        this.username = username;
        this.profile_img = profile_img;
        this.mp4_movie = mp4_movie;
        this.hls_movie = hls_movie;
        this.gochi_num = gochi_num;
        this.gochi_flag = gochi_flag;
        this.comment_num = comment_num;
    }

    //ユーザー
    public PostData(String post_id, String movie, String thumbnail, String category,
                    String value, String memo, String post_date, boolean cheer_flag, String post_rest_id,
                    String restname, double lon, double lat, String locality, String mp4_movie, String hls_movie,
                    int gochi_num, boolean gochi_flag, int comment_num) {
        this.post_id = post_id;
        this.movie = movie;
        this.thumbnail = thumbnail;
        this.category = category;
        this.value = value;
        this.memo = memo;
        this.post_date = post_date;
        this.cheer_flag = cheer_flag;
        this.post_rest_id = post_rest_id;
        this.restname = restname;
        this.lon = lon;
        this.lat = lat;
        this.locality = locality;
        this.mp4_movie = mp4_movie;
        this.hls_movie = hls_movie;
        this.gochi_num = gochi_num;
        this.gochi_flag = gochi_flag;
        this.comment_num = comment_num;
    }

    public static PostData createPostData(JSONObject jsonObject) {
        try {
            String post_id = jsonObject.getString(TAG_POST_ID);
            String movie = jsonObject.getString(TAG_MOVIE);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            String category = jsonObject.getString(TAG_CATEGORY);
            String value = jsonObject.getString(TAG_VALUE);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_user_id = jsonObject.getString(TAG_POST_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            boolean cheer_flag = jsonObject.getBoolean(TAG_CHEER_FLAG);
            String post_date = jsonObject.getString(TAG_POST_DATE);
            String rest_id = jsonObject.getString(TAG_POST_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            String locality = jsonObject.getString(TAG_LOCALITY);
            String mp4_movie = jsonObject.getString(TAG_MP4_MOVIE);
            String hls_movie = jsonObject.getString(TAG_HLS_MOVIE);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            boolean gochi_flag = jsonObject.getBoolean(TAG_GOCHI_FLAG);
            int comment_num = jsonObject.getInt(TAG_COMMENT_NUM);

            return new PostData(post_id, movie, thumbnail, category, value,
                    memo, post_user_id, username, profile_img, cheer_flag, post_date,
                    rest_id, restname, locality, mp4_movie, hls_movie, gochi_num, gochi_flag, comment_num);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PostData createUserPostData(JSONObject jsonObject) {
        try {
            String post_id = jsonObject.getString(TAG_POST_ID);
            String movie = jsonObject.getString(TAG_MOVIE);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            String category = jsonObject.getString(TAG_CATEGORY);
            String value = jsonObject.getString(TAG_VALUE);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_date = jsonObject.getString(TAG_POST_DATE);
            boolean cheer_flag = jsonObject.getBoolean(TAG_CHEER_FLAG);
            String post_rest_id = jsonObject.getString(TAG_POST_REST_ID);
            String restname = jsonObject.getString(TAG_RESTNAME);
            double lon = jsonObject.getDouble(TAG_LON);
            double lat = jsonObject.getDouble(TAG_LAT);
            String locality = jsonObject.getString(TAG_LOCALITY);
            String mp4_movie = jsonObject.getString(TAG_MP4_MOVIE);
            String hls_movie = jsonObject.getString(TAG_HLS_MOVIE);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            boolean gochi_flag = jsonObject.getBoolean(TAG_GOCHI_FLAG);
            int comment_num = jsonObject.getInt(TAG_COMMENT_NUM);

            return new PostData(post_id, movie, thumbnail, category, value,
                    memo, post_date, cheer_flag, post_rest_id, restname, lon, lat,
                    locality, mp4_movie, hls_movie, gochi_num, gochi_flag, comment_num);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PostData createRestPostData(JSONObject jsonObject) {
        try {
            String post_id = jsonObject.getString(TAG_POST_ID);
            String movie = jsonObject.getString(TAG_MOVIE);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            String category = jsonObject.getString(TAG_CATEGORY);
            String value = jsonObject.getString(TAG_VALUE);
            String memo = jsonObject.getString(TAG_MEMO);
            String post_date = jsonObject.getString(TAG_POST_DATE);
            boolean cheer_flag = jsonObject.getBoolean(TAG_CHEER_FLAG);
            String post_user_id = jsonObject.getString(TAG_POST_USER_ID);
            String username = jsonObject.getString(TAG_USERNAME);
            String profile_img = jsonObject.getString(TAG_PROFILE_IMG);
            String mp4_movie = jsonObject.getString(TAG_MP4_MOVIE);
            String hls_movie = jsonObject.getString(TAG_HLS_MOVIE);
            int gochi_num = jsonObject.getInt(TAG_GOCHI_NUM);
            boolean gochi_flag = jsonObject.getBoolean(TAG_GOCHI_FLAG);
            int comment_num = jsonObject.getInt(TAG_COMMENT_NUM);

            return new PostData(post_id, movie, thumbnail, category, value,
                    memo, post_date, cheer_flag, post_user_id, username, profile_img,
                    mp4_movie, hls_movie, gochi_num, gochi_flag, comment_num);

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

    public String getPost_user_id() {
        return post_user_id;
    }

    public void setPost_user_id(String post_user_id) {
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

    public String getPost_rest_id() {
        return post_rest_id;
    }

    public void setPost_rest_id(String post_rest_id) {
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

    public String getMp4_movie() {
        return mp4_movie;
    }

    public void setMp4_movie(String mp4_movie) {
        this.mp4_movie = mp4_movie;
    }

    public String getHls_movie() {
        return hls_movie;
    }

    public void setHls_movie(String hls_movie) {
        this.hls_movie = hls_movie;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public boolean isCheer_flag() {
        return cheer_flag;
    }

    public void setCheer_flag(boolean cheer_flag) {
        this.cheer_flag = cheer_flag;
    }

    public boolean isGochi_flag() {
        return gochi_flag;
    }

    public void setGochi_flag(boolean gochi_flag) {
        this.gochi_flag = gochi_flag;
    }
}
