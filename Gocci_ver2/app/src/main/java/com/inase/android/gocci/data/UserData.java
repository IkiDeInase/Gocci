package com.inase.android.gocci.data;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData {

    private static final String TAG_TELL = "tell";
    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_BACKGROUND = "background_picture";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_GOODNUM = "goodnum";
    private static final String TAG_COMMENT_NUM = "comment_num";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_STAR_EVALUATION = "star_evaluation";
    private static final String TAG_HOMEPAGE = "homepage";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_PUSHED_AT = "pushed_at";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_DATETIME = "date_time";
    private static final String TAG_ID = "id";
    private static final String TAG_PERSONAL_ID = "personal_id";
    private static final String TAG_VALUE = "value";
    private static final String TAG_ATMOSPHERE = "atmosphere";
    private static final String TAG_TAG_CATEGORY = "tag_category";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_STATUS = "status";


    //JSON用のsetter/getter

    private String movie;
    private String circleImage;
    private String background;
    private String post_id;
    private String user_name;
    private String restname;
    private String tell;
    private String category;
    private Double lat;
    private Double lon;
    private String locality;
    private String distance;
    private Integer goodnum;
    private Integer comment_num;
    private String thumbnail;
    private Integer star_evaluation;
    private String datetime;
    private Integer pushed_at;
    private String homepage;
    private String comment;
    private Integer personal_id;
    private Integer status;
    private String tag_category;
    private String atmosphere;
    private String value;

    public UserData() {
    }

    public UserData(String movie, String circleImage, String background, String post_id, String user_name, String restname, String tell, String category, Double lat, Double lon, String locality, Integer goodnum, Integer comment_num, String thumbnail, Integer star_evaluation, Integer pushed_at, String homepage, String datetime, String distance, String value, String atmosphere, String tag_category, String comment, Integer status) {
        this.movie = movie;
        this.circleImage = circleImage;
        this.background = background;
        this.post_id = post_id;
        this.user_name = user_name;
        this.restname = restname;
        this.tell = tell;
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.locality = locality;
        this.goodnum = goodnum;
        this.comment_num = comment_num;
        this.thumbnail = thumbnail;
        this.star_evaluation = star_evaluation;
        this.pushed_at = pushed_at;
        this.homepage = homepage;
        this.datetime = datetime;
        this.distance = distance;
        this.value = value;
        this.atmosphere = atmosphere;
        this.tag_category = tag_category;
        this.comment = comment;
        this.status = status;
    }

    public String getMovie() {
        return this.movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getPicture() {
        return this.circleImage;
    }

    public void setPicture(String circleImage) {
        this.circleImage = circleImage;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getPost_id() {
        return this.post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRest_name() {
        return this.restname;
    }

    public void setRest_name(String restname) {
        this.restname = restname;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTell() {
        return this.tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getgoodnum() {
        return this.goodnum;
    }

    public void setgoodnum(Integer goodnum) {
        this.goodnum = goodnum;
    }

    public Integer getComment_num() {
        return this.comment_num;
    }

    public void setComment_num(Integer comment_num) {
        this.comment_num = comment_num;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getStar_evaluation() {
        return this.star_evaluation;
    }

    public void setStar_evaluation(Integer star_evaluation) {
        this.star_evaluation = star_evaluation;
    }

    public Integer getPushed_at() {
        return this.pushed_at;
    }

    public void setPushed_at(Integer pushed_at) {
        this.pushed_at = pushed_at;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getPersonal_id() {
        return this.personal_id;
    }

    public void setPersonal_id(Integer personal_id) {
        this.personal_id = personal_id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAtmosphere() {
        return this.atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getTagCategory() {
        return this.tag_category;
    }

    public void setTagCategory(String tag_category) {
        this.tag_category = tag_category;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static UserData createUserData(JSONObject jsonObject) {
        try {
            String tell = jsonObject.getString(TAG_TELL);
            String post_id = jsonObject.getString(TAG_POST_ID);
            String user_name = jsonObject.getString(TAG_USER_NAME);
            String picture = jsonObject.getString(TAG_PICTURE);
            String background = jsonObject.getString(TAG_BACKGROUND);
            String movie = jsonObject.getString(TAG_MOVIE);
            String restname = jsonObject.getString(TAG_RESTNAME);
            Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
            Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
            Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
            String homepage = jsonObject.getString(TAG_HOMEPAGE);
            String locality = jsonObject.getString(TAG_LOCALITY);
            String category = jsonObject.getString(TAG_CATEGORY);
            Integer pushed_at = jsonObject.getInt(TAG_PUSHED_AT);
            Double lat = jsonObject.getDouble(TAG_LAT);
            Double lon = jsonObject.getDouble(TAG_LON);
            String datetime = jsonObject.getString(TAG_DATETIME);
            String distance = jsonObject.getString(TAG_DATETIME);
            String value = jsonObject.getString(TAG_VALUE);
            String atmosphere = jsonObject.getString(TAG_ATMOSPHERE);
            String tag_category = jsonObject.getString(TAG_TAG_CATEGORY);
            String comment = jsonObject.getString(TAG_COMMENT);
            Integer status = jsonObject.getInt(TAG_STATUS);

            return new UserData(movie, picture, background, post_id, user_name, restname, tell, category, lat, lon, locality, goodnum, comment_num, thumbnail, star_evaluation, pushed_at, homepage, datetime, distance, value, atmosphere, tag_category, comment, status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
