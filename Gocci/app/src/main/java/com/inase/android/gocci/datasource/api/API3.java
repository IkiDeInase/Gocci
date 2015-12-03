package com.inase.android.gocci.datasource.api;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.map.HeatmapLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kinagafuji on 15/11/06.
 */
public interface API3 {
    Util.AuthLoginLocalCode auth_login_parameter_regex(String identity_id);

    Util.AuthLoginLocalCode auth_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token);

    Util.AuthCheckLocalCode auth_check_parameter_regex(String register_id);

    Util.AuthCheckLocalCode auth_check_response_regex(String register_id);

    Util.AuthSignupLocalCode auth_signup_parameter_regex(String username, String os, String ver, String model, String register_id);

    Util.AuthSignupLocalCode auth_signup_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token);

    Util.AuthSnsLoginLocalCode auth_sns_login_parameter_regex(String identity_id, String os, String ver, String model, String register_id);

    Util.AuthSnsLoginLocalCode auth_sns_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token);

    Util.AuthPassLoginLocalCode auth_pass_login_parameter_regex(String username, String password, String os, String ver, String model, String register_id);

    Util.AuthPassLoginLocalCode auth_pass_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token);

    Util.GetTimelineLocalCode get_nearline_parameter_regex(double lon, double lat);

    Util.GetTimelineLocalCode get_nearline_response_regex();

    Util.GetTimelineLocalCode get_followline_parameter_regex();

    Util.GetTimelineLocalCode get_followline_response_regex();

    Util.GetTimelineLocalCode get_timeline_parameter_regex();

    Util.GetTimelineLocalCode get_timeline_response_regex();

    Util.GetUserLocalCode get_user_parameter_regex(String user_id);

    Util.GetUserLocalCode get_user_response_regex();

    Util.GetRestLocalCode get_rest_parameter_regex(String rest_id);

    Util.GetRestLocalCode get_rest_response_regex();

    Util.GetCommentLocalCode get_comment_parameter_regex(String post_id);

    Util.GetCommentLocalCode get_comment_response_regex();

    Util.GetFollowLocalCode get_follow_parameter_regex(String user_id);

    Util.GetFollowLocalCode get_follow_response_regex();

    Util.GetFollowerLocalCode get_follower_parameter_regex(String user_id);

    Util.GetFollowerLocalCode get_follower_response_regex();

    Util.GetWantLocalCode get_want_parameter_regex(String user_id);

    Util.GetWantLocalCode get_want_response_regex();

    Util.GetUserCheerLocalCode get_user_cheer_parameter_regex(String user_id);

    Util.GetUserCheerLocalCode get_user_cheer_response_regex();

    Util.GetRestCheerLocalCode get_rest_cheer_parameter_regex(String rest_id);

    Util.GetRestCheerLocalCode get_rest_cheer_response_regex();

    Util.GetNoticeLocalCode get_notice_parameter_regex();

    Util.GetNoticeLocalCode get_notice_response_regex();

    Util.GetNearLocalCode get_near_parameter_regex(double lon, double lat);

    Util.GetNearLocalCode get_near_response_regex();

    Util.GetHeatmapLocalCode get_heatmap_parameter_regex();

    Util.GetHeatmapLocalCode get_heatmap_response_regex();

    Util.PostSnsLinkLocalCode post_sns_link_parameter_regex(String provider, String token);

    Util.PostSnsLinkLocalCode post_sns_link_response_regex();

    Util.PostSnsUnlinkLocalCode post_sns_unlink_parameter_regex(String provider, String token);

    Util.PostSnsUnlinkLocalCode post_sns_unlink_response_regex();

    Util.PostGochiLocalCode post_gochi_parameter_regex(String post_id);

    Util.PostGochiLocalCode post_gochi_response_regex();

    Util.PostDeleteLocalCode post_delete_parameter_regex(String post_id);

    Util.PostDeleteLocalCode post_delete_response_regex();

    Util.PostBlockLocalCode post_block_parameter_regex(String post_id);

    Util.PostBlockLocalCode post_block_response_regex();

    Util.PostFollowLocalCode post_follow_parameter_regex(String user_id);

    Util.PostFollowLocalCode post_follow_response_regex();

    Util.PostUnfollowLocalCode post_unFollow_parameter_regex(String user_id);

    Util.PostUnfollowLocalCode post_unFollow_response_regex();

    Util.PostFeedbackLocalCode post_feedback_parameter_regex(String feedback);

    Util.PostFeedbackLocalCode post_feedback_response_regex();

    Util.PostPasswordLocalCode post_password_parameter_regex(String password);

    Util.PostPasswordLocalCode post_password_response_regex();

    Util.PostCommentLocalCode post_comment_parameter_regex(String post_id, String comment, String re_user_id);

    Util.PostCommentLocalCode post_comment_response_regex();

    Util.PostPostLocalCode post_post_parameter_regex(String rest_id, String movie_name, int category_id, String value, String memo, int cheer_flag);

    Util.PostPostLocalCode post_post_response_regex();

    Util.PostRestAddLocalCode post_restadd_parameter_regex(String restname, double lon, double lat);

    Util.PostRestAddLocalCode post_restadd_response_regex();

    Util.PublicUpdateDeviceLocalCode public_update_device_parameter_regex(String user_id, String regId, String os, String ver, String model);

    Util.PublicUpdateDeviceLocalCode public_update_device_response_regex();

    Util.PostUsernameLocalCode post_username_parameter_regex(String username);

    Util.PostUsernameLocalCode post_username_response_regex(String username);

    Util.PostProfileImgLocalCode post_profileImg_parameter_regex();

    Util.PostProfileImgLocalCode post_profileImg_response_regex(String profile_img);

    Util.GlobalCode check_global_error();

    void auth_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_check_response(JSONObject jsonObject, CheckResponseCallback cb);

    void auth_signup_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_sns_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_pass_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void get_timeline_response(JSONObject jsonObject, GetPostdataResponseCallback cb);

    void get_user_response(JSONObject jsonObject, GetUserAndRestResponseCallback cb);

    void get_rest_response(JSONObject jsonObject, GetUserAndRestResponseCallback cb);

    void get_comment_response(JSONObject jsonObject, GetCommentResponseCallback cb);

    void get_follow_response(JSONObject jsonObject, GetListResponseCallback cb);

    void get_follower_response(JSONObject jsonObject, GetListResponseCallback cb);

    void get_want_response(JSONObject jsonObject, GetListResponseCallback cb);

    void get_user_cheer_response(JSONObject jsonObject, GetListResponseCallback cb);

    void get_rest_cheer_response(JSONObject jsonObject, GetListResponseCallback cb);

    void get_notice_response(JSONObject jsonObject, GetNoticeResponseCallback cb);

    void get_near_response(JSONObject jsonObject, GetNearResponseCallback cb);

    void get_heatmap_response(JSONObject jsonObject, GetHeatmapResponseCallback cb);

    void post_sns_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_sns_unlink_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_gochi_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_delete_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_block_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_follow_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_unFollow_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_feedback_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_password_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_comment_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_post_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_restadd_response(JSONObject jsonObject, PostRestAddResponseCallback cb);

    void public_update_device_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_username_response(JSONObject jsonObject, PostResponseCallback cb);

    void post_profileImg_response(JSONObject jsonObject, PostResponseCallback cb);

    interface AuthResponseCallback {
        void onSuccess();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface CheckResponseCallback {
        void onSuccess();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String id, String errorMessage);
    }

    interface PostResponseCallback {
        void onSuccess();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface PostRestAddResponseCallback {
        void onSuccess(String rest_id);

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetPostdataResponseCallback {
        void onSuccess(ArrayList<TwoCellData> postData, ArrayList<String> post_ids);

        void onEmpty();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetUserAndRestResponseCallback {
        void onSuccess(HeaderData headerData, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void onEmpty(HeaderData headerData);

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetCommentResponseCallback {
        void onSuccess(HeaderData headerData, ArrayList<HeaderData> commentData);

        void onEmpty(HeaderData headerData);

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetListResponseCallback {
        void onSuccess(ArrayList<ListGetData> list);

        void onEmpty();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetNoticeResponseCallback {
        void onSuccess(ArrayList<HeaderData> list);

        void onEmpty();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetHeatmapResponseCallback {
        void onSuccess(ArrayList<HeatmapLog> list);

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    interface GetNearResponseCallback {
        void onSuccess(String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray);

        void onEmpty();

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    class Util {
        private static final String baseurl = "http://mobile.api.gocci.me/v3";
        private static final String testurl = "http://test.mobile.api.gocci.me/v3";

        public static final ConcurrentHashMap<GlobalCode, String> globalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GlobalCode> globalReverseMap = new ConcurrentHashMap<>();

        public enum GlobalCode {
            SUCCESS,
            ERROR_UNKNOWN_ERROR,
            ERROR_SESSION_EXPIRED,
            ERROR_CLIENT_OUTDATED,
            ERROR_NO_INTERNET_CONNECTION,
            ERROR_CONNECTION_FAILED,
            ERROR_CONNECTION_TIMEOUT,
            ERROR_SERVER_SIDE_FAILURE,
            ERROR_NO_DATA_RECIEVED,
            ERROR_BASEFRAME_JSON_MALFORMED,
        }

        public static GlobalCode globalErrorReverseLookupTable(String code) {
            if (globalReverseMap.isEmpty()) {
                globalReverseMap.put("SUCCESS", GlobalCode.SUCCESS);
                globalReverseMap.put("ERROR_UNKNOWN_ERROR", GlobalCode.ERROR_UNKNOWN_ERROR);
                globalReverseMap.put("ERROR_SESSION_EXPIRED", GlobalCode.ERROR_SESSION_EXPIRED);
                globalReverseMap.put("ERROR_CLIENT_OUTDATED", GlobalCode.ERROR_CLIENT_OUTDATED);
                globalReverseMap.put("ERROR_NO_INTERNET_CONNECTION", GlobalCode.ERROR_NO_INTERNET_CONNECTION);
                globalReverseMap.put("ERROR_CONNECTION_FAILED", GlobalCode.ERROR_CONNECTION_FAILED);
                globalReverseMap.put("ERROR_CONNECTION_TIMEOUT", GlobalCode.ERROR_CONNECTION_TIMEOUT);
                globalReverseMap.put("ERROR_SERVER_SIDE_FAILURE", GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                globalReverseMap.put("ERROR_NO_DATA_RECIEVED", GlobalCode.ERROR_NO_DATA_RECIEVED);
                globalReverseMap.put("ERROR_BASEFRAME_JSON_MALFORMED", GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
            GlobalCode globalCode = null;
            for (Map.Entry<String, GlobalCode> entry : globalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    globalCode = entry.getValue();
                    break;
                }
            }
            return globalCode;
        }

        public static String globalErrorMessageTable(GlobalCode globalCode) {
            if (globalMap.isEmpty()) {
                globalMap.put(GlobalCode.SUCCESS, "Successful API request");
                globalMap.put(GlobalCode.ERROR_UNKNOWN_ERROR, "Unknown global error");
                globalMap.put(GlobalCode.ERROR_SESSION_EXPIRED, "Session cookie is not valid anymore");
                globalMap.put(GlobalCode.ERROR_CLIENT_OUTDATED, "The client version is too old for this API. Client update necessary");
                globalMap.put(GlobalCode.ERROR_NO_INTERNET_CONNECTION, "The device appreas to be not connected to the internet");
                globalMap.put(GlobalCode.ERROR_CONNECTION_FAILED, "Server connection failed");
                globalMap.put(GlobalCode.ERROR_CONNECTION_TIMEOUT, "Timeout reached before request finished");
                globalMap.put(GlobalCode.ERROR_SERVER_SIDE_FAILURE, "HTTP status differed from 200, indicationg failure on the server side");
                globalMap.put(GlobalCode.ERROR_NO_DATA_RECIEVED, "Connection successful but no data recieved");
                globalMap.put(GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED, "JSON response baseframe not parsable");
            }
            String message = null;
            for (Map.Entry<GlobalCode, String> entry : globalMap.entrySet()) {
                if (entry.getKey().equals(globalCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<AuthSignupLocalCode, String> authSignupLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, AuthSignupLocalCode> authSignupLocalReverseMap = new ConcurrentHashMap<>();

        public enum AuthSignupLocalCode {
            ERROR_USERNAME_ALREADY_REGISTERD,
            ERROR_REGISTER_ID_ALREADY_REGISTERD,
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED,
            ERROR_PARAMETER_OS_MISSING,
            ERROR_PARAMETER_OS_MALFORMED,
            ERROR_PARAMETER_VER_MISSING,
            ERROR_PARAMETER_VER_MALFORMED,
            ERROR_PARAMETER_MODEL_MISSING,
            ERROR_PARAMETER_MODEL_MALFORMED,
            ERROR_PARAMETER_REGISTER_ID_MISSING,
            ERROR_PARAMETER_REGISTER_ID_MALFORMED,
            ERROR_RESPONSE_USER_ID_MISSING,
            ERROR_RESPONSE_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERNAME_MISSING,
            ERROR_RESPONSE_USERNAME_MALFORMED,
            ERROR_RESPONSE_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_BADGE_NUM_MISSING,
            ERROR_RESPONSE_BADGE_NUM_MALFORMED,
            ERROR_RESPONSE_TOKEN_MISSING,
            ERROR_RESPONSE_TOKEN_MALFORMED
        }

        public static AuthSignupLocalCode authSignupLocalErrorReverseLookupTable(String code) {
            if (authSignupLocalReverseMap.isEmpty()) {
                authSignupLocalReverseMap.put("ERROR_USERNAME_ALREADY_REGISTERD", AuthSignupLocalCode.ERROR_USERNAME_ALREADY_REGISTERD);
                authSignupLocalReverseMap.put("ERROR_REGISTER_ID_ALREADY_REGISTERD", AuthSignupLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_OS_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_OS_MISSING);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_OS_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_OS_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_VER_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_VER_MISSING);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_VER_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_VER_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_MODEL_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MISSING);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_MODEL_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING);
                authSignupLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MISSING);
                authSignupLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED);
            }
            AuthSignupLocalCode localCode = null;
            for (Map.Entry<String, AuthSignupLocalCode> entry : authSignupLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String authSignupLocalErrorMessageTable(AuthSignupLocalCode localCode) {
            if (authSignupLocalMap.isEmpty()) {
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_USERNAME_ALREADY_REGISTERD, "The provided username was already registerd by another user");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD, "This deviced already has an registerd account");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_OS_MISSING, "Parameter 'os' does not exist.");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_OS_MALFORMED, "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_VER_MISSING, "Parameter 'ver' does not exist.");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_VER_MALFORMED, "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MISSING, "Parameter 'model' does not exist.");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MALFORMED, "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING, "Parameter 'register_id' does not exist.");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED, "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MISSING, "Response 'user_id' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING, "Response 'badge_num' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED, "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MISSING, "Response 'token' was not received");
                authSignupLocalMap.put(AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED, "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'");
            }
            String message = null;
            for (Map.Entry<AuthSignupLocalCode, String> entry : authSignupLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<AuthCheckLocalCode, String> authCheckLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, AuthCheckLocalCode> authCheckLocalReverseMap = new ConcurrentHashMap<>();

        public enum AuthCheckLocalCode {
            ERROR_REGISTER_ID_ALREADY_REGISTERD,
            ERROR_PARAMETER_REGISTER_ID_MISSING,
            ERROR_PARAMETER_REGISTER_ID_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED
        }

        public static AuthCheckLocalCode authCheckLocalErrorReverseLookupTable(String code) {
            if (authCheckLocalReverseMap.isEmpty()) {
                authCheckLocalReverseMap.put("ERROR_REGISTER_ID_ALREADY_REGISTERD", AuthCheckLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD);
                authCheckLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MISSING", AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING);
                authCheckLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MALFORMED", AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED);
                authCheckLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                authCheckLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
            }
            AuthCheckLocalCode localCode = null;
            for (Map.Entry<String, AuthCheckLocalCode> entry : authCheckLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String authCheckLocalErrorMessageTable(AuthCheckLocalCode localCode) {
            if (authCheckLocalMap.isEmpty()) {
                authCheckLocalMap.put(AuthCheckLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD, "This deviced already has an registerd account");
                authCheckLocalMap.put(AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING, "Parameter 'register_id' does not exist.");
                authCheckLocalMap.put(AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED, "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
                authCheckLocalMap.put(AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                authCheckLocalMap.put(AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
            }
            String message = null;
            for (Map.Entry<AuthCheckLocalCode, String> entry : authCheckLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<AuthLoginLocalCode, String> authLoginLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, AuthLoginLocalCode> authLoginLocalReverseMap = new ConcurrentHashMap<>();

        public enum AuthLoginLocalCode {
            ERROR_IDENTITY_ID_NOT_REGISTERD,
            ERROR_PARAMETER_IDENTITY_ID_MISSING,
            ERROR_PARAMETER_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_USER_ID_MISSING,
            ERROR_RESPONSE_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERNAME_MISSING,
            ERROR_RESPONSE_USERNAME_MALFORMED,
            ERROR_RESPONSE_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_BADGE_NUM_MISSING,
            ERROR_RESPONSE_BADGE_NUM_MALFORMED,
            ERROR_RESPONSE_TOKEN_MISSING,
            ERROR_RESPONSE_TOKEN_MALFORMED
        }

        public static AuthLoginLocalCode authLoginLocalErrorReverseLookupTable(String code) {
            if (authLoginLocalReverseMap.isEmpty()) {
                authLoginLocalReverseMap.put("ERROR_IDENTITY_ID_NOT_REGISTERD", AuthLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD);
                authLoginLocalReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MISSING", AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING);
                authLoginLocalReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MALFORMED", AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING);
                authLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED);
            }
            AuthLoginLocalCode localCode = null;
            for (Map.Entry<String, AuthLoginLocalCode> entry : authLoginLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String authLoginLocalErrorMessageTable(AuthLoginLocalCode localCode) {
            if (authLoginLocalMap.isEmpty()) {
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD, "This deviced already has an registerd account");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING, "Parameter 'identity_id' does not exist.");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED, "Parameter 'identity_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING, "Response 'user_id' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING, "Response 'badge_num' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED, "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING, "Response 'token' was not received");
                authLoginLocalMap.put(AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED, "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'");
            }
            String message = null;
            for (Map.Entry<AuthLoginLocalCode, String> entry : authLoginLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<AuthSnsLoginLocalCode, String> authSnsLoginLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, AuthSnsLoginLocalCode> authSnsLoginLocalReverseMap = new ConcurrentHashMap<>();

        public enum AuthSnsLoginLocalCode {
            ERROR_REGISTER_ID_ALREADY_REGISTERD,
            ERROR_IDENTITY_ID_NOT_REGISTERD,
            ERROR_PARAMETER_IDENTITY_ID_MISSING,
            ERROR_PARAMETER_IDENTITY_ID_MALFORMED,
            ERROR_PARAMETER_OS_MISSING,
            ERROR_PARAMETER_OS_MALFORMED,
            ERROR_PARAMETER_VER_MISSING,
            ERROR_PARAMETER_VER_MALFORMED,
            ERROR_PARAMETER_MODEL_MISSING,
            ERROR_PARAMETER_MODEL_MALFORMED,
            ERROR_PARAMETER_REGISTER_ID_MISSING,
            ERROR_PARAMETER_REGISTER_ID_MALFORMED,
            ERROR_RESPONSE_USER_ID_MISSING,
            ERROR_RESPONSE_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERNAME_MISSING,
            ERROR_RESPONSE_USERNAME_MALFORMED,
            ERROR_RESPONSE_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_BADGE_NUM_MISSING,
            ERROR_RESPONSE_BADGE_NUM_MALFORMED,
            ERROR_RESPONSE_TOKEN_MISSING,
            ERROR_RESPONSE_TOKEN_MALFORMED
        }

        public static AuthSnsLoginLocalCode authSnsLoginLocalErrorReverseLookupTable(String code) {
            if (authSnsLoginLocalReverseMap.isEmpty()) {
                authSnsLoginLocalReverseMap.put("ERROR_REGISTER_ID_ALREADY_REGISTERD", AuthSnsLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD);
                authSnsLoginLocalReverseMap.put("ERROR_IDENTITY_ID_NOT_REGISTERD", AuthSnsLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MISSING", AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MALFORMED", AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_OS_MISSING", AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_OS_MALFORMED", AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_VER_MISSING", AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_VER_MALFORMED", AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_MODEL_MISSING", AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_MODEL_MALFORMED", AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MISSING", AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MALFORMED", AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MISSING", AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING);
                authSnsLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MALFORMED", AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED);
            }
            AuthSnsLoginLocalCode localCode = null;
            for (Map.Entry<String, AuthSnsLoginLocalCode> entry : authSnsLoginLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String authSnsLoginLocalErrorMessageTable(AuthSnsLoginLocalCode localCode) {
            if (authSnsLoginLocalMap.isEmpty()) {
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD, "This deviced already has an registerd account");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD, "This deviced already has an registerd account");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING, "Parameter 'username' does not exist.");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MISSING, "Parameter 'os' does not exist.");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED, "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MISSING, "Parameter 'ver' does not exist.");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED, "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING, "Parameter 'model' does not exist.");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED, "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING, "Parameter 'register_id' does not exist.");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED, "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING, "Response 'user_id' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING, "Response 'badge_num' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED, "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING, "Response 'token' was not received");
                authSnsLoginLocalMap.put(AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED, "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'");
            }
            String message = null;
            for (Map.Entry<AuthSnsLoginLocalCode, String> entry : authSnsLoginLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<AuthPassLoginLocalCode, String> authPassLoginLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, AuthPassLoginLocalCode> authPassLoginLocalReverseMap = new ConcurrentHashMap<>();

        public enum AuthPassLoginLocalCode {
            ERROR_REGISTER_ID_ALREADY_REGISTERD,
            ERROR_USERNAME_NOT_REGISTERD,
            ERROR_PASSWORD_NOT_REGISTERD,
            ERROR_PASSWORD_WRONG,
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED,
            ERROR_PARAMETER_PASSWORD_MISSING,
            ERROR_PARAMETER_PASSWORD_MALFORMED,
            ERROR_PARAMETER_OS_MISSING,
            ERROR_PARAMETER_OS_MALFORMED,
            ERROR_PARAMETER_VER_MISSING,
            ERROR_PARAMETER_VER_MALFORMED,
            ERROR_PARAMETER_MODEL_MISSING,
            ERROR_PARAMETER_MODEL_MALFORMED,
            ERROR_PARAMETER_REGISTER_ID_MISSING,
            ERROR_PARAMETER_REGISTER_ID_MALFORMED,
            ERROR_RESPONSE_USER_ID_MISSING,
            ERROR_RESPONSE_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERNAME_MISSING,
            ERROR_RESPONSE_USERNAME_MALFORMED,
            ERROR_RESPONSE_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_BADGE_NUM_MISSING,
            ERROR_RESPONSE_BADGE_NUM_MALFORMED,
            ERROR_RESPONSE_TOKEN_MISSING,
            ERROR_RESPONSE_TOKEN_MALFORMED
        }

        public static AuthPassLoginLocalCode authPassLoginLocalErrorReverseLookupTable(String code) {
            if (authPassLoginLocalReverseMap.isEmpty()) {
                authPassLoginLocalReverseMap.put("ERROR_REGISTER_ID_ALREADY_REGISTERD", AuthPassLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD);
                authPassLoginLocalReverseMap.put("ERROR_USERNAME_NOT_REGISTERD", AuthPassLoginLocalCode.ERROR_USERNAME_NOT_REGISTERD);
                authPassLoginLocalReverseMap.put("ERROR_PASSWORD_NOT_REGISTERD", AuthPassLoginLocalCode.ERROR_PASSWORD_NOT_REGISTERD);
                authPassLoginLocalReverseMap.put("ERROR_PASSWODR_WRONG", AuthPassLoginLocalCode.ERROR_PASSWORD_WRONG);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_PASSWORD_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_PASSWORD_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_OS_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_OS_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_VER_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_VER_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_MODEL_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_MODEL_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MISSING", AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_PARAMETER_REGISTER_ID_MALFORMED", AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_USER_ID_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MISSING", AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING);
                authPassLoginLocalReverseMap.put("ERROR_RESPONSE_TOKEN_MALFORMED", AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED);
            }
            AuthPassLoginLocalCode localCode = null;
            for (Map.Entry<String, AuthPassLoginLocalCode> entry : authPassLoginLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String authPassLoginLocalErrorMessageTable(AuthPassLoginLocalCode localCode) {
            if (authPassLoginLocalMap.isEmpty()) {
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD, "This deviced already has an registerd account");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_USERNAME_NOT_REGISTERD, "username does not exist");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PASSWORD_NOT_REGISTERD, "password does not exist");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PASSWORD_WRONG, "password is wrong");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MISSING, "Parameter 'password' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED, "Parameter 'password' is malformed. Should correspond to '^\\w{4,20}$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MISSING, "Parameter 'os' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED, "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MISSING, "Parameter 'ver' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED, "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING, "Parameter 'model' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED, "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING, "Parameter 'register_id' does not exist.");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED, "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING, "Response 'user_id' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING, "Response 'badge_num' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED, "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING, "Response 'token' was not received");
                authPassLoginLocalMap.put(AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED, "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'");
            }
            String message = null;
            for (Map.Entry<AuthPassLoginLocalCode, String> entry : authPassLoginLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetTimelineLocalCode, String> getTimelineLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetTimelineLocalCode> getTimelineLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetTimelineLocalCode {
            ERROR_PARAMETER_LON_MISSING,
            ERROR_PARAMETER_LON_MALFORMED,
            ERROR_PARAMETER_LAT_MISSING,
            ERROR_PARAMETER_LAT_MALFORMED,
        }

        public static GetTimelineLocalCode getTimelineLocalErrorReverseLookupTable(String code) {
            if (getTimelineLocalReverseMap.isEmpty()) {
                getTimelineLocalReverseMap.put("ERROR_PARAMETER_LON_MISSING", GetTimelineLocalCode.ERROR_PARAMETER_LON_MISSING);
                getTimelineLocalReverseMap.put("ERROR_PARAMETER_LON_MALFORMED", GetTimelineLocalCode.ERROR_PARAMETER_LON_MALFORMED);
                getTimelineLocalReverseMap.put("ERROR_PARAMETER_LAT_MISSING", GetTimelineLocalCode.ERROR_PARAMETER_LAT_MISSING);
                getTimelineLocalReverseMap.put("ERROR_PARAMETER_LAT_MALFORMED", GetTimelineLocalCode.ERROR_PARAMETER_LAT_MALFORMED);
            }
            GetTimelineLocalCode localCode = null;
            for (Map.Entry<String, GetTimelineLocalCode> entry : getTimelineLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getTimelineLocalErrorMessageTable(GetTimelineLocalCode localCode) {
            if (getTimelineLocalMap.isEmpty()) {
                getTimelineLocalMap.put(GetTimelineLocalCode.ERROR_PARAMETER_LON_MISSING, "Parameter 'lon' does not exist.");
                getTimelineLocalMap.put(GetTimelineLocalCode.ERROR_PARAMETER_LON_MALFORMED, "Parameter 'lon' is malformed. Should correspond to ''DOUBLE");
                getTimelineLocalMap.put(GetTimelineLocalCode.ERROR_PARAMETER_LAT_MISSING, "Parameter 'lat' does not exist.");
                getTimelineLocalMap.put(GetTimelineLocalCode.ERROR_PARAMETER_LAT_MALFORMED, "Parameter 'lat' is malformed. Should correspond to ''DOUBLE");
            }
            String message = null;
            for (Map.Entry<GetTimelineLocalCode, String> entry : getTimelineLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetUserLocalCode, String> getUserLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetUserLocalCode> getUserLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetUserLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetUserLocalCode getUserLocalErrorReverseLookupTable(String code) {
            if (getUserLocalReverseMap.isEmpty()) {
                getUserLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                getUserLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetUserLocalCode localCode = null;
            for (Map.Entry<String, GetUserLocalCode> entry : getUserLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getUserLocalErrorMessageTable(GetUserLocalCode localCode) {
            if (getUserLocalMap.isEmpty()) {
                getUserLocalMap.put(GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' was not received");
                getUserLocalMap.put(GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<GetUserLocalCode, String> entry : getUserLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetRestLocalCode, String> getRestLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetRestLocalCode> getRestLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetRestLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
        }

        public static GetRestLocalCode getRestLocalErrorReverseLookupTable(String code) {
            if (getRestLocalReverseMap.isEmpty()) {
                getRestLocalReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
                getRestLocalReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
            }
            GetRestLocalCode localCode = null;
            for (Map.Entry<String, GetRestLocalCode> entry : getRestLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getRestLocalErrorMessageTable(GetRestLocalCode localCode) {
            if (getRestLocalMap.isEmpty()) {
                getRestLocalMap.put(GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' was not received");
                getRestLocalMap.put(GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<GetRestLocalCode, String> entry : getRestLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetCommentLocalCode, String> getCommentLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetCommentLocalCode> getCommentLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetCommentLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static GetCommentLocalCode getCommentLocalErrorReverseLookupTable(String code) {
            if (getCommentLocalReverseMap.isEmpty()) {
                getCommentLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                getCommentLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
            }
            GetCommentLocalCode localCode = null;
            for (Map.Entry<String, GetCommentLocalCode> entry : getCommentLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getCommentLocalErrorMessageTable(GetCommentLocalCode localCode) {
            if (getCommentLocalMap.isEmpty()) {
                getCommentLocalMap.put(GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' was not received");
                getCommentLocalMap.put(GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<GetCommentLocalCode, String> entry : getCommentLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetFollowLocalCode, String> getFollowLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetFollowLocalCode> getFollowLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetFollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetFollowLocalCode getFollowLocalErrorReverseLookupTable(String code) {
            if (getFollowLocalReverseMap.isEmpty()) {
                getFollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                getFollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetFollowLocalCode localCode = null;
            for (Map.Entry<String, GetFollowLocalCode> entry : getFollowLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getFollowLocalErrorMessageTable(GetFollowLocalCode localCode) {
            if (getFollowLocalMap.isEmpty()) {
                getFollowLocalMap.put(GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                getFollowLocalMap.put(GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<GetFollowLocalCode, String> entry : getFollowLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetFollowerLocalCode, String> getFollowerLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetFollowerLocalCode> getFollowerLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetFollowerLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetFollowerLocalCode getFollowerLocalErrorReverseLookupTable(String code) {
            if (getFollowerLocalReverseMap.isEmpty()) {
                getFollowerLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                getFollowerLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetFollowerLocalCode localCode = null;
            for (Map.Entry<String, GetFollowerLocalCode> entry : getFollowerLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getFollowerLocalErrorMessageTable(GetFollowerLocalCode localCode) {
            if (getFollowerLocalMap.isEmpty()) {
                getFollowerLocalMap.put(GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                getFollowerLocalMap.put(GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<GetFollowerLocalCode, String> entry : getFollowerLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetWantLocalCode, String> getWantLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetWantLocalCode> getWantLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetWantLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetWantLocalCode getWantLocalErrorReverseLookupTable(String code) {
            if (getWantLocalReverseMap.isEmpty()) {
                getWantLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                getWantLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetWantLocalCode localCode = null;
            for (Map.Entry<String, GetWantLocalCode> entry : getWantLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getWantLocalErrorMessageTable(GetWantLocalCode localCode) {
            if (getWantLocalMap.isEmpty()) {
                getWantLocalMap.put(GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                getWantLocalMap.put(GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<GetWantLocalCode, String> entry : getWantLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetUserCheerLocalCode, String> getUserCheerLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetUserCheerLocalCode> getUserCheerLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetUserCheerLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetUserCheerLocalCode getUserCheerLocalErrorReverseLookupTable(String code) {
            if (getUserCheerLocalReverseMap.isEmpty()) {
                getUserCheerLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                getUserCheerLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetUserCheerLocalCode localCode = null;
            for (Map.Entry<String, GetUserCheerLocalCode> entry : getUserCheerLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getUserCheerLocalErrorMessageTable(GetUserCheerLocalCode localCode) {
            if (getUserCheerLocalMap.isEmpty()) {
                getUserCheerLocalMap.put(GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                getUserCheerLocalMap.put(GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<GetUserCheerLocalCode, String> entry : getUserCheerLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetRestCheerLocalCode, String> getRestCheerLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetRestCheerLocalCode> getRestCheerLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetRestCheerLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
        }

        public static GetRestCheerLocalCode getRestCheerLocalErrorReverseLookupTable(String code) {
            if (getRestCheerLocalReverseMap.isEmpty()) {
                getRestCheerLocalReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
                getRestCheerLocalReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
            }
            GetRestCheerLocalCode localCode = null;
            for (Map.Entry<String, GetRestCheerLocalCode> entry : getRestCheerLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getRestCheerLocalErrorMessageTable(GetRestCheerLocalCode localCode) {
            if (getRestCheerLocalMap.isEmpty()) {
                getRestCheerLocalMap.put(GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
                getRestCheerLocalMap.put(GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<GetRestCheerLocalCode, String> entry : getRestCheerLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetNoticeLocalCode, String> getNoticeLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetNoticeLocalCode> getNoticeLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetNoticeLocalCode {

        }

        public static GetNoticeLocalCode getNoticeLocalErrorReverseLookupTable(String code) {
            if (getNoticeLocalReverseMap.isEmpty()) {
            }
            GetNoticeLocalCode localCode = null;
            for (Map.Entry<String, GetNoticeLocalCode> entry : getNoticeLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getNoticeLocalErrorMessageTable(GetNoticeLocalCode localCode) {
            if (getNoticeLocalMap.isEmpty()) {
            }
            String message = null;
            for (Map.Entry<GetNoticeLocalCode, String> entry : getNoticeLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetNearLocalCode, String> getNearLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetNearLocalCode> getNearLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetNearLocalCode {

        }

        public static GetNearLocalCode getNearLocalErrorReverseLookupTable(String code) {
            if (getNearLocalReverseMap.isEmpty()) {
            }
            GetNearLocalCode localCode = null;
            for (Map.Entry<String, GetNearLocalCode> entry : getNearLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getNearLocalErrorMessageTable(GetNearLocalCode localCode) {
            if (getNearLocalMap.isEmpty()) {
            }
            String message = null;
            for (Map.Entry<GetNearLocalCode, String> entry : getNearLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<GetHeatmapLocalCode, String> getHeatmapLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GetHeatmapLocalCode> getHeatmapLocalReverseMap = new ConcurrentHashMap<>();

        public enum GetHeatmapLocalCode {

        }

        public static GetHeatmapLocalCode getHeatmapLocalErrorReverseLookupTable(String code) {
            if (getHeatmapLocalReverseMap.isEmpty()) {
            }
            GetHeatmapLocalCode localCode = null;
            for (Map.Entry<String, GetHeatmapLocalCode> entry : getHeatmapLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String getHeatmapLocalErrorMessageTable(GetHeatmapLocalCode localCode) {
            if (getHeatmapLocalMap.isEmpty()) {
            }
            String message = null;
            for (Map.Entry<GetHeatmapLocalCode, String> entry : getHeatmapLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostSnsLinkLocalCode, String> postSnsLinkLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostSnsLinkLocalCode> postSnsLinkLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostSnsLinkLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROFILE_IMAGE_DOES_NOT_EXIST,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_TOKEN_MISSING,
            ERROR_PARAMETER_TOKEN_MALFORMED,
        }

        public static PostSnsLinkLocalCode postSnsLinkLocalErrorReverseLookupTable(String code) {
            if (postSnsLinkLocalReverseMap.isEmpty()) {
                postSnsLinkLocalReverseMap.put("ERROR_SNS_PROVIDER_TOKEN_NOT_VALID", PostSnsLinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID);
                postSnsLinkLocalReverseMap.put("ERROR_PROFILE_IMAGE_DOES_NOT_EXIST", PostSnsLinkLocalCode.ERROR_PROFILE_IMAGE_DOES_NOT_EXIST);
                postSnsLinkLocalReverseMap.put("ERROR_PROVIDER_UNREACHABLE", PostSnsLinkLocalCode.ERROR_PROVIDER_UNREACHABLE);
                postSnsLinkLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MISSING", PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING);
                postSnsLinkLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MALFORMED", PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED);
                postSnsLinkLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MISSING", PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING);
                postSnsLinkLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MALFORMED", PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED);
            }
            PostSnsLinkLocalCode localCode = null;
            for (Map.Entry<String, PostSnsLinkLocalCode> entry : postSnsLinkLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postSnsLinkLocalErrorMessageTable(PostSnsLinkLocalCode localCode) {
            if (postSnsLinkLocalMap.isEmpty()) {
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID, "The provided sns token is invalid or has expired");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PROFILE_IMAGE_DOES_NOT_EXIST, "The provided link to the profile image cound not be downloaded");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PROVIDER_UNREACHABLE, "The providers server infrastructure appears to be down");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING, "Parameter 'provider' does not exist.");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED, "Parameter 'provider' is malformed. Should correspond to '^\\w{4,20}$'");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING, "Parameter 'token' does not exist.");
                postSnsLinkLocalMap.put(PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED, "Parameter 'token' is malformed. Should correspond to '^\\w{4,20}$'");
            }
            String message = null;
            for (Map.Entry<PostSnsLinkLocalCode, String> entry : postSnsLinkLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostSnsUnlinkLocalCode, String> postSnsUnlinkLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostSnsUnlinkLocalCode> postSnsUnlinkLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostSnsUnlinkLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_TOKEN_MISSING,
            ERROR_PARAMETER_TOKEN_MALFORMED,
        }

        public static PostSnsUnlinkLocalCode postSnsUnlinkLocalErrorReverseLookupTable(String code) {
            if (postSnsUnlinkLocalReverseMap.isEmpty()) {
                postSnsUnlinkLocalReverseMap.put("ERROR_SNS_PROVIDER_TOKEN_NOT_VALID", PostSnsUnlinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID);
                postSnsUnlinkLocalReverseMap.put("ERROR_PROVIDER_UNREACHABLE", PostSnsUnlinkLocalCode.ERROR_PROVIDER_UNREACHABLE);
                postSnsUnlinkLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MISSING", PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING);
                postSnsUnlinkLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MALFORMED", PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED);
                postSnsUnlinkLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MISSING", PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING);
                postSnsUnlinkLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MALFORMED", PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED);
            }
            PostSnsUnlinkLocalCode localCode = null;
            for (Map.Entry<String, PostSnsUnlinkLocalCode> entry : postSnsUnlinkLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postSnsUnlinkLocalErrorMessageTable(PostSnsUnlinkLocalCode localCode) {
            if (postSnsUnlinkLocalMap.isEmpty()) {
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID, "The provided sns token is invalid or has expired");
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_PROVIDER_UNREACHABLE, "The providers server infrastructure appears to be down");
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING, "Parameter 'provider' does not exist.");
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED, "Parameter 'provider' is malformed. Should correspond to '^\\w{4,20}$'");
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING, "Parameter 'token' does not exist.");
                postSnsUnlinkLocalMap.put(PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED, "Parameter 'token' is malformed. Should correspond to '^\\w{4,20}$'");
            }
            String message = null;
            for (Map.Entry<PostSnsUnlinkLocalCode, String> entry : postSnsUnlinkLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostGochiLocalCode, String> postGochiLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostGochiLocalCode> postGochiLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostGochiLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static PostGochiLocalCode postGochiLocalErrorReverseLookupTable(String code) {
            if (postGochiLocalReverseMap.isEmpty()) {
                postGochiLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                postGochiLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
            }
            PostGochiLocalCode localCode = null;
            for (Map.Entry<String, PostGochiLocalCode> entry : postGochiLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postGochiLocalErrorMessageTable(PostGochiLocalCode localCode) {
            if (postGochiLocalMap.isEmpty()) {
                postGochiLocalMap.put(PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' was not received");
                postGochiLocalMap.put(PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostGochiLocalCode, String> entry : postGochiLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostDeleteLocalCode, String> postDeleteLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostDeleteLocalCode> postDeleteLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostDeleteLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static PostDeleteLocalCode postDeleteLocalErrorReverseLookupTable(String code) {
            if (postDeleteLocalReverseMap.isEmpty()) {
                postDeleteLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                postDeleteLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
            }
            PostDeleteLocalCode localCode = null;
            for (Map.Entry<String, PostDeleteLocalCode> entry : postDeleteLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postDeleteLocalErrorMessageTable(PostDeleteLocalCode localCode) {
            if (postDeleteLocalMap.isEmpty()) {
                postDeleteLocalMap.put(PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' was not received");
                postDeleteLocalMap.put(PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostDeleteLocalCode, String> entry : postDeleteLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostBlockLocalCode, String> postBlockLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostBlockLocalCode> postBlockLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostBlockLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static PostBlockLocalCode postBlockLocalErrorReverseLookupTable(String code) {
            if (postBlockLocalReverseMap.isEmpty()) {
                postBlockLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                postBlockLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
            }
            PostBlockLocalCode localCode = null;
            for (Map.Entry<String, PostBlockLocalCode> entry : postBlockLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postBlockLocalErrorMessageTable(PostBlockLocalCode localCode) {
            if (postBlockLocalMap.isEmpty()) {
                postBlockLocalMap.put(PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' was not received");
                postBlockLocalMap.put(PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostBlockLocalCode, String> entry : postBlockLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostFollowLocalCode, String> postFollowLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostFollowLocalCode> postFollowLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostFollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static PostFollowLocalCode postFollowLocalErrorReverseLookupTable(String code) {
            if (postFollowLocalReverseMap.isEmpty()) {
                postFollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                postFollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            PostFollowLocalCode localCode = null;
            for (Map.Entry<String, PostFollowLocalCode> entry : postFollowLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postFollowLocalErrorMessageTable(PostFollowLocalCode localCode) {
            if (postFollowLocalMap.isEmpty()) {
                postFollowLocalMap.put(PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' was not received");
                postFollowLocalMap.put(PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostFollowLocalCode, String> entry : postFollowLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostUnfollowLocalCode, String> postUnfollowLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostUnfollowLocalCode> postUnfollowLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostUnfollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static PostUnfollowLocalCode postUnfollowLocalErrorReverseLookupTable(String code) {
            if (postUnfollowLocalReverseMap.isEmpty()) {
                postUnfollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                postUnfollowLocalReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            PostUnfollowLocalCode localCode = null;
            for (Map.Entry<String, PostUnfollowLocalCode> entry : postUnfollowLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postUnfollowLocalErrorMessageTable(PostUnfollowLocalCode localCode) {
            if (postUnfollowLocalMap.isEmpty()) {
                postUnfollowLocalMap.put(PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' was not received");
                postUnfollowLocalMap.put(PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostUnfollowLocalCode, String> entry : postUnfollowLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostFeedbackLocalCode, String> postFeedbackLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostFeedbackLocalCode> postFeedbackLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostFeedbackLocalCode {
            ERROR_PARAMETER_FEEDBACK_MISSING,
            ERROR_PARAMETER_FEEDBACK_MALFORMED,
        }

        public static PostFeedbackLocalCode postFeedbackLocalErrorReverseLookupTable(String code) {
            if (postFeedbackLocalReverseMap.isEmpty()) {
                postFeedbackLocalReverseMap.put("ERROR_PARAMETER_FEEDBACK_MISSING", PostFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MISSING);
                postFeedbackLocalReverseMap.put("ERROR_PARAMETER_FEEDBACK_MALFORMED", PostFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MALFORMED);
            }
            PostFeedbackLocalCode localCode = null;
            for (Map.Entry<String, PostFeedbackLocalCode> entry : postFeedbackLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postFeedbackLocalErrorMessageTable(PostFeedbackLocalCode localCode) {
            if (postFeedbackLocalMap.isEmpty()) {
                postFeedbackLocalMap.put(PostFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MISSING, "Parameter 'feedback' was not received");
                postFeedbackLocalMap.put(PostFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MALFORMED, "Parameter 'feedback' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostFeedbackLocalCode, String> entry : postFeedbackLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostPasswordLocalCode, String> postPasswordLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostPasswordLocalCode> postPasswordLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostPasswordLocalCode {
            ERROR_PARAMETER_PASSWORD_MISSING,
            ERROR_PARAMETER_PASSWORD_MALFORMED,
        }

        public static PostPasswordLocalCode postPasswordLocalErrorReverseLookupTable(String code) {
            if (postPasswordLocalReverseMap.isEmpty()) {
                postPasswordLocalReverseMap.put("ERROR_PARAMETER_PASSWORD_MISSING", PostPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING);
                postPasswordLocalReverseMap.put("ERROR_PARAMETER_PASSWORD_MALFORMED", PostPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED);
            }
            PostPasswordLocalCode localCode = null;
            for (Map.Entry<String, PostPasswordLocalCode> entry : postPasswordLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postPasswordLocalErrorMessageTable(PostPasswordLocalCode localCode) {
            if (postPasswordLocalMap.isEmpty()) {
                postPasswordLocalMap.put(PostPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING, "Parameter 'password' was not received");
                postPasswordLocalMap.put(PostPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED, "Parameter 'password' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostPasswordLocalCode, String> entry : postPasswordLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostCommentLocalCode, String> postCommentLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostCommentLocalCode> postCommentLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostCommentLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
            ERROR_PARAMETER_COMMENT_MISSING,
            ERROR_PARAMETER_COMMENT_MALFORMED,
            ERROR_PARAMETER_RE_USER_ID_MISSING,
            ERROR_PARAMETER_RE_USER_ID_MALFORMED,
        }

        public static PostCommentLocalCode postCommentLocalErrorReverseLookupTable(String code) {
            if (postCommentLocalReverseMap.isEmpty()) {
                postCommentLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                postCommentLocalReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                postCommentLocalReverseMap.put("ERROR_PARAMETER_COMMENT_MISSING", PostCommentLocalCode.ERROR_PARAMETER_COMMENT_MISSING);
                postCommentLocalReverseMap.put("ERROR_PARAMETER_COMMENT_MALFORMED", PostCommentLocalCode.ERROR_PARAMETER_COMMENT_MALFORMED);
                postCommentLocalReverseMap.put("ERROR_PARAMETER_RE_USER_ID_MISSING", PostCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MISSING);
                postCommentLocalReverseMap.put("ERROR_PARAMETER_RE_USER_ID_MALFORMED", PostCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MISSING);
            }
            PostCommentLocalCode localCode = null;
            for (Map.Entry<String, PostCommentLocalCode> entry : postCommentLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postCommentLocalErrorMessageTable(PostCommentLocalCode localCode) {
            if (postCommentLocalMap.isEmpty()) {
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' was not received");
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' is malformed. Should correspond to '^[0-9]+$'");
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_COMMENT_MISSING, "Parameter 'comment' was not received");
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_COMMENT_MALFORMED, "Parameter 'comment' is malformed. Should correspond to '^[0-9]+$'");
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MISSING, "Parameter 're_user_id' was not received");
                postCommentLocalMap.put(PostCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MISSING, "Parameter 're_user_id' is malformed. Should correspond to '^[0-9]+$'");
            }
            String message = null;
            for (Map.Entry<PostCommentLocalCode, String> entry : postCommentLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostPostLocalCode, String> postPostLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostPostLocalCode> postPostLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostPostLocalCode {

        }

        public static PostPostLocalCode postPostLocalErrorReverseLookupTable(String code) {
            if (postPostLocalReverseMap.isEmpty()) {

            }
            PostPostLocalCode localCode = null;
            for (Map.Entry<String, PostPostLocalCode> entry : postPostLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postPostLocalErrorMessageTable(PostPostLocalCode localCode) {
            if (postPostLocalMap.isEmpty()) {

            }
            String message = null;
            for (Map.Entry<PostPostLocalCode, String> entry : postPostLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostRestAddLocalCode, String> postRestAddLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostRestAddLocalCode> postRestAddLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostRestAddLocalCode {

        }

        public static PostRestAddLocalCode postRestAddLocalErrorReverseLookupTable(String code) {
            if (postRestAddLocalReverseMap.isEmpty()) {

            }
            PostRestAddLocalCode localCode = null;
            for (Map.Entry<String, PostRestAddLocalCode> entry : postRestAddLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postRestAddLocalErrorMessageTable(PostRestAddLocalCode localCode) {
            if (postRestAddLocalMap.isEmpty()) {

            }
            String message = null;
            for (Map.Entry<PostRestAddLocalCode, String> entry : postRestAddLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PublicUpdateDeviceLocalCode, String> publicUpdateDeviceLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PublicUpdateDeviceLocalCode> publicUpdateDeviceLocalReverseMap = new ConcurrentHashMap<>();

        public enum PublicUpdateDeviceLocalCode {

        }

        public static PublicUpdateDeviceLocalCode publicUpdateDeviceLocalErrorReverseLookupTable(String code) {
            if (publicUpdateDeviceLocalReverseMap.isEmpty()) {

            }
            PublicUpdateDeviceLocalCode localCode = null;
            for (Map.Entry<String, PublicUpdateDeviceLocalCode> entry : publicUpdateDeviceLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String publicUpdateDeviceErrorMessageTable(PublicUpdateDeviceLocalCode localCode) {
            if (publicUpdateDeviceLocalMap.isEmpty()) {

            }
            String message = null;
            for (Map.Entry<PublicUpdateDeviceLocalCode, String> entry : publicUpdateDeviceLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostUsernameLocalCode, String> postUsernameLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostUsernameLocalCode> postUsernameLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostUsernameLocalCode {
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED
        }

        public static PostUsernameLocalCode postUsernameLocalErrorReverseLookupTable(String code) {
            if (postUsernameLocalReverseMap.isEmpty()) {
                postUsernameLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                postUsernameLocalReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
            }
            PostUsernameLocalCode localCode = null;
            for (Map.Entry<String, PostUsernameLocalCode> entry : postUsernameLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postUsernameLocalErrorMessageTable(PostUsernameLocalCode localCode) {
            if (postUsernameLocalMap.isEmpty()) {
                postUsernameLocalMap.put(PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                postUsernameLocalMap.put(PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'");
            }
            String message = null;
            for (Map.Entry<PostUsernameLocalCode, String> entry : postUsernameLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static final ConcurrentHashMap<PostProfileImgLocalCode, String> postProfileImgLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostProfileImgLocalCode> postProfileImgLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostProfileImgLocalCode {
            ERROR_PARAMETER_PROFILE_IMG_MISSING,
            ERROR_PARAMETER_PROFILE_IMG_MALFORMED;
        }

        public static PostProfileImgLocalCode postProfileImgLocalErrorReverseLookupTable(String code) {
            if (postProfileImgLocalReverseMap.isEmpty()) {
                postProfileImgLocalReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MISSING", PostProfileImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING);
                postProfileImgLocalReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MALFORMED", PostProfileImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED);

            }
            PostProfileImgLocalCode localCode = null;
            for (Map.Entry<String, PostProfileImgLocalCode> entry : postProfileImgLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postProfileImgLocalErrorMessageTable(PostProfileImgLocalCode localCode) {
            if (postProfileImgLocalMap.isEmpty()) {
                postProfileImgLocalMap.put(PostProfileImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING, "Parameter 'profile_img' was not received");
                postProfileImgLocalMap.put(PostProfileImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED, "Parameter 'profile_img' is malformed. Should correspond to '^http\\S+$'");
            }
            String message = null;
            for (Map.Entry<PostProfileImgLocalCode, String> entry : postProfileImgLocalMap.entrySet()) {
                if (entry.getKey().equals(localCode)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static String getAuthLoginAPI(String identity_id) {
            return testurl + "/auth/login/?identity_id=" + identity_id;
        }

        public static String getAuthCheckAPI(String register_id) {
            return testurl + "/auth/check/?register_id=" + register_id;
        }

        public static String getAuthSNSLoginAPI(String identity_id, String os, String ver, String model, String register_id) {
            return testurl + "/auth/sns_login/?identity_id=" + identity_id + "&os=" + os + "&ver=" + ver + "&model=" + model + "&register_id=" + register_id;
        }

        public static String getAuthSignupAPI(String username, String os, String ver, String model, String register_id) {
            return testurl + "/auth/signup/?username=" + username + "&os=" + os + "&ver=" + ver + "&model=" + model + "&register_id=" + register_id;
        }

        public static String getAuthUsernamePasswordAPI(String username, String password, String os, String ver, String model, String register_id) {
            return testurl + "/auth/pass_login/?username=" + username + "&password=" + password + "&os=" + os + "&ver=" + ver + "&model=" + model + "&register_id=" + register_id;
        }

        public static String getGetNearlineAPI(double lon, double lat) {
            return testurl + "/get/nearline/?lon=" + lon + "&lat=" + lat;
        }

        public static String getGetNearlineCustomAPI(double lon, double lat, int page, int category_id, int value_id) {
            StringBuilder url = null;
            url = new StringBuilder(testurl + "/get/nearline/?lon=" + lon + "&lat=" + lat + "&page=" + page);
            if (category_id != 0) url.append("&category_id=").append(category_id);
            if (value_id != 0) url.append("&value_id=").append(value_id);
            return new String(url);
        }

        public static String getGetFollowlineAPI() {
            return testurl + "/get/followline";
        }

        public static String getGetFollowlineCustomAPI(int page, int category_id, int value_id) {
            StringBuilder url = null;
            url = new StringBuilder(testurl + "/get/followline/?page=" + page);
            if (category_id != 0) url.append("&category_id=").append(category_id);
            if (value_id != 0) url.append("&value_id=").append(value_id);
            return new String(url);
        }

        public static String getGetTimelineAPI() {
            return testurl + "/get/timeline";
        }

        public static String getGetTimelineCustomAPI(int page, int category_id, int value_id) {
            StringBuilder url = null;
            url = new StringBuilder(testurl + "/get/timeline/?page=" + page);
            if (category_id != 0) url.append("&category_id=").append(category_id);
            if (value_id != 0) url.append("&value_id=").append(value_id);
            return new String(url);
        }

        public static String getGetUserAPI(String user_id) {
            return testurl + "/get/user/?user_id=" + user_id;
        }

        public static String getGetRestAPI(String rest_id) {
            return testurl + "/get/rest/?rest_id=" + rest_id;
        }

        public static String getGetCommentAPI(String post_id) {
            return testurl + "/get/comment/?post_id=" + post_id;
        }

        public static String getGetFollowAPI(String user_id) {
            return testurl + "/get/follow/?user_id=" + user_id;
        }

        public static String getGetFollowerAPI(String user_id) {
            return testurl + "/get/follower/?user_id=" + user_id;
        }

        public static String getGetWantAPI(String user_id) {
            return testurl + "/get/want/?user_id=" + user_id;
        }

        public static String getGetUserCheerAPI(String user_id) {
            return testurl + "/get/user_cheer/?user_id=" + user_id;
        }

        public static String getGetRestCheerAPI(String rest_id) {
            return testurl + "/get/rest_cheer/?rest_id=" + rest_id;
        }

        public static String getGetNoticeAPI() {
            return testurl + "/get/notice";
        }

        public static String getGetNearAPI(double lon, double lat) {
            return testurl + "/get/near/?lon=" + lon + "&lat=" + lat;
        }

        public static String getGetHeatmapAPI() {
            return testurl + "/get/heatmap";
        }

        public static String getPostSnsLinkAPI(String provider, String token) {
            return testurl + "/set/sns_link/?provider=" + provider + "&sns_token=" + token;
        }

        public static String getPostSnsUnlinkAPI(String provider, String token) {
            return testurl + "/set/sns_unlink/?provider=" + provider + "&sns_token=" + token;
        }

        public static String getPostGochiAPI(String post_id) {
            return testurl + "/set/gochi/?post_id=" + post_id;
        }

        public static String getPostDeleteAPI(String post_id) {
            return testurl + "/set/post_delete/?post_id=" + post_id;
        }

        public static String getPostBlockAPI(String post_id) {
            return testurl + "/set/post_block/?post_id=" + post_id;
        }

        public static String getPostFollowAPI(String user_id) {
            return testurl + "/set/follow/?user_id=" + user_id;
        }

        public static String getPostUnfollowAPI(String user_id) {
            return testurl + "/set/unfollow/?user_id=" + user_id;
        }

        public static String getPostFeedbackAPI(String feedback) {
            return testurl + "/set/feedback/?feedback=" + feedback;
        }

        public static String getPostPasswordAPI(String password) {
            return testurl + "/set/password/?password=" + password;
        }

        public static String getPostCommentAPI(String post_id, String comment, String re_user_id) {
            return testurl + "/set/comment/?post_id=" + post_id + "&comment=" + comment + "&re_user_id=" + re_user_id;
        }

        public static String getPostMovieAPI(String rest_id, String movie_name, int category_id, String value, String memo, int cheer_flag) {
            return testurl + "/set/post/?rest_id=" + rest_id + "&movie_name=" + movie_name +
                    "&category_id=" + category_id + "&value=" + value + "&memo=" + memo + "&cheer_flag=" + cheer_flag;
        }

        public static String getPostRestAddAPI(String restname, double lon, double lat) {
            return testurl + "/set/rest/?restname=" + restname +
                    "&lon=" + lon + "&lat=" + lat;
        }

        public static String getPublicUpdateDevice(String user_id, String regId, String os, String ver, String model) {
            return testurl + "/update/device/?user_id=" + user_id +
                    "&register_id=" + regId + "&os=" + os + "&ver=" + ver + "&model=" + model;
        }

        public static String getPostUsernameAPI(String username) {
            return testurl + "/set/username/?username=" + username;
        }

        public static String getPostProfileImg(String profile_img) {
            return testurl + "/set/profile_img/?profile_img=" + profile_img;
        }
    }

    class Impl implements API3 {
        private static Impl sAPI3;

        public Impl() {
        }

        public static Impl getRepository() {
            if (sAPI3 == null) {
                sAPI3 = new Impl();
            }
            return sAPI3;
        }

        @Override
        public Util.AuthLoginLocalCode auth_login_parameter_regex(String identity_id) {
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthLoginLocalCode auth_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
            }
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
            }
            if (profile_img != null) {
                if (!profile_img.matches("^http\\S+$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
            }
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
            }
            if (badge_num != null) {
                if (!badge_num.matches("^[0-9]+$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
            }
            if (token != null) {
                if (!token.matches("^[a-zA-Z0-9_.-]{400,2200}$")) {
                    return Util.AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                }
            } else {
                return Util.AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthCheckLocalCode auth_check_parameter_regex(String register_id) {
            if (register_id != null) {
                if (!register_id.matches("^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$")) {
                    return Util.AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                }
            } else {
                return Util.AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthCheckLocalCode auth_check_response_regex(String identity_id) {
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthSignupLocalCode auth_signup_parameter_regex(String username, String os, String ver, String model, String register_id) {
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            if (os != null) {
                if (!os.matches("^android$|^iOS$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_OS_MISSING;
            }
            if (ver != null) {
                if (!ver.matches("^\\d+.\\d$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_VER_MISSING;
            }
            if (model != null) {
//                if (!model.matches("^[a-zA-Z0-9_-]{0,10}$")) {
//                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
//                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MISSING;
            }
            if (register_id != null) {
                if (!register_id.matches("^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthSignupLocalCode auth_signup_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
            }
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
            }
            if (profile_img != null) {
                if (!profile_img.matches("^http\\S+$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
            }
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
            }
            if (badge_num != null) {
                if (!badge_num.matches("^[0-9]+$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
            }
            if (token != null) {
                if (!token.matches("^[a-zA-Z0-9_.-]{400,2200}$")) {
                    return Util.AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthSnsLoginLocalCode auth_sns_login_parameter_regex(String identity_id, String os, String ver, String model, String register_id) {
            if (identity_id != null) {
                if (!identity_id.matches("\"^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$\"")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING;
            }
            if (os != null) {
                if (!os.matches("^android$|^iOS$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MISSING;
            }
            if (ver != null) {
                if (!ver.matches("^\\d+.\\d$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MISSING;
            }
            if (model != null) {
                if (!model.matches("^[a-zA-Z0-9_-]{0,10}$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING;
            }
            if (register_id != null) {
                if (!register_id.matches("^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthSnsLoginLocalCode auth_sns_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
            }
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
            }
            if (profile_img != null) {
                if (!profile_img.matches("^http\\S+$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
            }
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
            }
            if (badge_num != null) {
                if (!badge_num.matches("^[0-9]+$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
            }
            if (token != null) {
                if (!token.matches("^[a-zA-Z0-9_.-]{400,2200}$")) {
                    return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                }
            } else {
                return Util.AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthPassLoginLocalCode auth_pass_login_parameter_regex(String username, String password, String os, String ver, String model, String register_id) {
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            if (password != null) {
                if (!password.matches("^\\w{6,25}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MISSING;
            }
            if (os != null) {
                if (!os.matches("^android$|^iOS$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MISSING;
            }
            if (ver != null) {
                if (!ver.matches("^\\d+.\\d$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MISSING;
            }
            if (model != null) {
                if (!model.matches("^[a-zA-Z0-9_-]{0,10}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING;
            }
            if (register_id != null) {
                if (!register_id.matches("^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.AuthPassLoginLocalCode auth_pass_login_response_regex(String user_id, String username, String profile_img, String identity_id, String badge_num, String token) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
            }
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
            }
            if (profile_img != null) {
                if (!profile_img.matches("^http\\S+$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
            }
            if (identity_id != null) {
                if (!identity_id.matches("^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
            }
            if (badge_num != null) {
                if (!badge_num.matches("^[0-9]+$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
            }
            if (token != null) {
                if (!token.matches("^[a-zA-Z0-9_.-]{400,2200}$")) {
                    return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                }
            } else {
                return Util.AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostSnsLinkLocalCode post_sns_link_parameter_regex(String provider, String token) {
            if (provider != null) {
                if (!provider.matches("^(api.twitter.com)|(graph.facebook.com)$")) {
                    return Util.PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                }
            } else {
                return Util.PostSnsLinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
            }
            if (token != null) {
                if (!token.matches("^\\S{20,4000}$")) {
                    return Util.PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED;
                }
            } else {
                return Util.PostSnsLinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostSnsLinkLocalCode post_sns_link_response_regex() {
            return null;
        }

        @Override
        public Util.PostSnsUnlinkLocalCode post_sns_unlink_parameter_regex(String provider, String token) {
            if (provider != null) {
                if (!provider.matches("^(api.twitter.com)|(graph.facebook.com)$")) {
                    return Util.PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                }
            } else {
                return Util.PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
            }
            if (token != null) {
                if (!token.matches("^\\S{20,4000}$")) {
                    return Util.PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED;
                }
            } else {
                return Util.PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostSnsUnlinkLocalCode post_sns_unlink_response_regex() {
            return null;
        }

        @Override
        public Util.PostGochiLocalCode post_gochi_parameter_regex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^[0-9]+$")) {
                    return Util.PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.PostGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostGochiLocalCode post_gochi_response_regex() {
            return null;
        }

        @Override
        public Util.PostDeleteLocalCode post_delete_parameter_regex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^[0-9]+$")) {
                    return Util.PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.PostDeleteLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostDeleteLocalCode post_delete_response_regex() {
            return null;
        }

        @Override
        public Util.PostBlockLocalCode post_block_parameter_regex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^[0-9]+$")) {
                    return Util.PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.PostBlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostBlockLocalCode post_block_response_regex() {
            return null;
        }

        @Override
        public Util.PostFollowLocalCode post_follow_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.PostFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostFollowLocalCode post_follow_response_regex() {
            return null;
        }

        @Override
        public Util.PostUnfollowLocalCode post_unFollow_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.PostUnfollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostUnfollowLocalCode post_unFollow_response_regex() {
            return null;
        }

        @Override
        public Util.PostFeedbackLocalCode post_feedback_parameter_regex(String feedback) {
            return null;
        }

        @Override
        public Util.PostFeedbackLocalCode post_feedback_response_regex() {
            return null;
        }

        @Override
        public Util.PostPasswordLocalCode post_password_parameter_regex(String password) {
            return null;
        }

        @Override
        public Util.PostPasswordLocalCode post_password_response_regex() {
            return null;
        }

        @Override
        public Util.PostCommentLocalCode post_comment_parameter_regex(String post_id, String comment, String re_user_id) {
            if (post_id != null) {
                if (!post_id.matches("^[0-9]+$")) {
                    return Util.PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.PostCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostCommentLocalCode post_comment_response_regex() {
            return null;
        }

        @Override
        public Util.PostPostLocalCode post_post_parameter_regex(String rest_id, String movie_name, int category_id, String value, String memo, int cheer_flag) {
            return null;
        }

        @Override
        public Util.PostPostLocalCode post_post_response_regex() {
            return null;
        }

        @Override
        public Util.PostRestAddLocalCode post_restadd_parameter_regex(String restname, double lon, double lat) {
            return null;
        }

        @Override
        public Util.PostRestAddLocalCode post_restadd_response_regex() {
            return null;
        }

        @Override
        public Util.PublicUpdateDeviceLocalCode public_update_device_parameter_regex(String user_id, String regId, String os, String ver, String model) {
            return null;
        }

        @Override
        public Util.PublicUpdateDeviceLocalCode public_update_device_response_regex() {
            return null;
        }

        @Override
        public Util.PostUsernameLocalCode post_username_parameter_regex(String username) {
            if (username != null) {
                if (!username.matches("^\\S{4,20}$")) {
                    return Util.PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.PostUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostUsernameLocalCode post_username_response_regex(String username) {
            return null;
        }

        @Override
        public Util.PostProfileImgLocalCode post_profileImg_parameter_regex() {

            return null;
        }

        @Override
        public Util.PostProfileImgLocalCode post_profileImg_response_regex(String profile_img) {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_nearline_parameter_regex(double lon, double lat) {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_nearline_response_regex() {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_followline_parameter_regex() {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_followline_response_regex() {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_timeline_parameter_regex() {
            return null;
        }

        @Override
        public Util.GetTimelineLocalCode get_timeline_response_regex() {
            return null;
        }

        @Override
        public Util.GetUserLocalCode get_user_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetUserLocalCode get_user_response_regex() {
            return null;
        }

        @Override
        public Util.GetRestLocalCode get_rest_parameter_regex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^[0-9]+$")) {
                    return Util.GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetRestLocalCode get_rest_response_regex() {
            return null;
        }

        @Override
        public Util.GetCommentLocalCode get_comment_parameter_regex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^[0-9]+$")) {
                    return Util.GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetCommentLocalCode get_comment_response_regex() {
            return null;
        }

        @Override
        public Util.GetFollowLocalCode get_follow_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetFollowLocalCode get_follow_response_regex() {
            return null;
        }

        @Override
        public Util.GetFollowerLocalCode get_follower_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetFollowerLocalCode get_follower_response_regex() {
            return null;
        }

        @Override
        public Util.GetWantLocalCode get_want_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetWantLocalCode get_want_response_regex() {
            return null;
        }

        @Override
        public Util.GetUserCheerLocalCode get_user_cheer_parameter_regex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^[0-9]+$")) {
                    return Util.GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetUserCheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetUserCheerLocalCode get_user_cheer_response_regex() {
            return null;
        }

        @Override
        public Util.GetRestCheerLocalCode get_rest_cheer_parameter_regex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^[0-9]+$")) {
                    return Util.GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.GetRestCheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public Util.GetRestCheerLocalCode get_rest_cheer_response_regex() {
            return null;
        }

        @Override
        public Util.GetNoticeLocalCode get_notice_parameter_regex() {
            return null;
        }

        @Override
        public Util.GetNoticeLocalCode get_notice_response_regex() {
            return null;
        }

        @Override
        public Util.GetNearLocalCode get_near_parameter_regex(double lon, double lat) {
            return null;
        }

        @Override
        public Util.GetNearLocalCode get_near_response_regex() {
            return null;
        }

        @Override
        public Util.GetHeatmapLocalCode get_heatmap_parameter_regex() {
            return null;
        }

        @Override
        public Util.GetHeatmapLocalCode get_heatmap_response_regex() {
            return null;
        }

        @Override
        public Util.GlobalCode check_global_error() {
            if (com.inase.android.gocci.utils.Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) == com.inase.android.gocci.utils.Util.NetworkStatus.OFF) {
                return Util.GlobalCode.ERROR_NO_INTERNET_CONNECTION;
            }
            return Util.GlobalCode.SUCCESS;
        }

        @Override
        public void auth_login_response(JSONObject jsonObject, AuthResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String user_id = payload.getString("user_id");
                        String username = payload.getString("username");
                        String identity_id = payload.getString("identity_id");
                        String profile_img = payload.getString("profile_img");
                        String badge_num = payload.getString("badge_num");
                        String cognito_token = payload.getString("cognito_token");

                        Util.AuthLoginLocalCode localCode = auth_login_response_regex(user_id, username, profile_img, identity_id, badge_num, cognito_token);
                        if (localCode == null) {
                            //エラーなし
                            Application_Gocci.GuestInit(Application_Gocci.getInstance().getApplicationContext(), identity_id, cognito_token, user_id);
                            SavedData.setWelcome(Application_Gocci.getInstance().getApplicationContext(), username, profile_img, user_id, identity_id, Integer.parseInt(badge_num));
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.authLoginLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthLoginLocalCode localCode = Util.authLoginLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.authLoginLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void auth_check_response(JSONObject jsonObject, CheckResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthCheckLocalCode localCode = Util.authCheckLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.authCheckLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            if (localCode == Util.AuthCheckLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD) {
                                JSONObject payload = jsonObject.getJSONObject("payload");
                                String identity_id = payload.getString("identity_id");

                                Util.AuthCheckLocalCode check = auth_check_response_regex(identity_id);
                                if (check == null) {
                                    //エラーなし
                                    cb.onLocalError(identity_id, message);
                                } else {
                                    //LocalCodeエラー
                                    String checkErrorMessage = Util.authCheckLocalErrorMessageTable(check);
                                    cb.onLocalError(null, checkErrorMessage);
                                }
                            } else {
                                cb.onLocalError(null, message);
                            }
                        } else {
                            //ハンドリング不可　致命的バグ
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void auth_signup_response(JSONObject jsonObject, AuthResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String user_id = payload.getString("user_id");
                        String username = payload.getString("username");
                        String identity_id = payload.getString("identity_id");
                        String profile_img = payload.getString("profile_img");
                        String badge_num = payload.getString("badge_num");
                        String cognito_token = payload.getString("cognito_token");

                        Util.AuthSignupLocalCode localCode = auth_signup_response_regex(user_id, username, profile_img, identity_id, badge_num, cognito_token);
                        if (localCode == null) {
                            //エラーなし
                            Application_Gocci.GuestInit(Application_Gocci.getInstance().getApplicationContext(), identity_id, cognito_token, user_id);
                            SavedData.setWelcome(Application_Gocci.getInstance().getApplicationContext(), username, profile_img, user_id, identity_id, Integer.parseInt(badge_num));
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.authSignupLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthSignupLocalCode localCode = Util.authSignupLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.authSignupLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void auth_sns_login_response(JSONObject jsonObject, AuthResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String user_id = payload.getString("user_id");
                        String username = payload.getString("username");
                        String identity_id = payload.getString("identity_id");
                        String profile_img = payload.getString("profile_img");
                        String badge_num = payload.getString("badge_num");
                        String cognito_token = payload.getString("cognito_token");

                        Util.AuthSnsLoginLocalCode localCode = auth_sns_login_response_regex(user_id, username, profile_img, identity_id, badge_num, cognito_token);
                        if (localCode == null) {
                            //エラーなし
                            Application_Gocci.GuestInit(Application_Gocci.getInstance().getApplicationContext(), identity_id, cognito_token, user_id);
                            SavedData.setWelcome(Application_Gocci.getInstance().getApplicationContext(), username, profile_img, user_id, identity_id, Integer.parseInt(badge_num));
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.authSnsLoginLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthSnsLoginLocalCode localCode = Util.authSnsLoginLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.authSnsLoginLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void auth_pass_login_response(JSONObject jsonObject, AuthResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String user_id = payload.getString("user_id");
                        String username = payload.getString("username");
                        String identity_id = payload.getString("identity_id");
                        String profile_img = payload.getString("profile_img");
                        String badge_num = payload.getString("badge_num");
                        String cognito_token = payload.getString("cognito_token");

                        Util.AuthPassLoginLocalCode localCode = auth_pass_login_response_regex(user_id, username, profile_img, identity_id, badge_num, cognito_token);
                        if (localCode == null) {
                            //エラーなし
                            Application_Gocci.GuestInit(Application_Gocci.getInstance().getApplicationContext(), identity_id, cognito_token, user_id);
                            SavedData.setWelcome(Application_Gocci.getInstance().getApplicationContext(), username, profile_img, user_id, identity_id, Integer.parseInt(badge_num));
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.authPassLoginLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthPassLoginLocalCode localCode = Util.authPassLoginLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.authPassLoginLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_sns_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostSnsLinkLocalCode localCode = Util.postSnsLinkLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postSnsLinkLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_sns_unlink_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostSnsUnlinkLocalCode localCode = Util.postSnsUnlinkLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postSnsUnlinkLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_gochi_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostGochiLocalCode localCode = Util.postGochiLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postGochiLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_delete_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostDeleteLocalCode localCode = Util.postDeleteLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postDeleteLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_block_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostBlockLocalCode localCode = Util.postBlockLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postBlockLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_follow_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostFollowLocalCode localCode = Util.postFollowLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postFollowLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_unFollow_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostUnfollowLocalCode localCode = Util.postUnfollowLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postUnfollowLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_feedback_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostFeedbackLocalCode localCode = Util.postFeedbackLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postFeedbackLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_password_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostPasswordLocalCode localCode = Util.postPasswordLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postPasswordLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_comment_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostCommentLocalCode localCode = Util.postCommentLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postCommentLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_post_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        cb.onSuccess();
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostPostLocalCode localCode = Util.postPostLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postPostLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_restadd_response(JSONObject jsonObject, PostRestAddResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String rest_id = payload.getString("rest_id");

                        Util.PostRestAddLocalCode localCode = post_restadd_response_regex();
                        if (localCode == null) {
                            //エラーなし
                            cb.onSuccess(rest_id);
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.postRestAddLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostRestAddLocalCode localCode = Util.postRestAddLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postRestAddLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void public_update_device_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String register_id = payload.getString("register_id");

                        Util.PublicUpdateDeviceLocalCode localCode = public_update_device_response_regex();
                        if (localCode == null) {
                            //エラーなし
                            SavedData.setRegId(Application_Gocci.getInstance().getApplicationContext(), register_id);
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.publicUpdateDeviceErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PublicUpdateDeviceLocalCode localCode = Util.publicUpdateDeviceLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.publicUpdateDeviceErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_username_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String username = payload.getString("username");

                        Util.PostUsernameLocalCode localCode = post_username_response_regex(username);
                        if (localCode == null) {
                            //エラーなし
                            SavedData.setServerName(Application_Gocci.getInstance().getApplicationContext(), username);
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.postUsernameLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostUsernameLocalCode localCode = Util.postUsernameLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postUsernameLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void post_profileImg_response(JSONObject jsonObject, PostResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        String profile_img = payload.getString("profile_img");

                        Util.PostProfileImgLocalCode localCode = post_profileImg_parameter_regex();
                        if (localCode == null) {
                            //エラーなし
                            SavedData.setServerPicture(Application_Gocci.getInstance().getApplicationContext(), profile_img);
                            cb.onSuccess();
                        } else {
                            //LocalCodeエラー
                            String errorMessage = Util.postProfileImgLocalErrorMessageTable(localCode);
                            cb.onLocalError(errorMessage);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.PostProfileImgLocalCode localCode = Util.postProfileImgLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postProfileImgLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_timeline_response(JSONObject jsonObject, GetPostdataResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<TwoCellData> mPostData = new ArrayList<>();
                        final ArrayList<String> mPost_Ids = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject postdata = payload.getJSONObject(i);
                                mPostData.add(TwoCellData.createPostData(postdata));
                                mPost_Ids.add(postdata.getString("post_id"));
                            }
                            cb.onSuccess(mPostData, mPost_Ids);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetTimelineLocalCode localCode = Util.getTimelineLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getTimelineLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_user_response(JSONObject jsonObject, GetUserAndRestResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<PostData> mPostData = new ArrayList<>();
                        final ArrayList<String> mPost_Ids = new ArrayList<>();

                        JSONObject payload = jsonObject.getJSONObject("payload");
                        JSONObject user = payload.getJSONObject("user");

                        HeaderData headerData = HeaderData.createUserHeaderData(user);

                        JSONArray posts = payload.getJSONArray("posts");
                        if (posts.length() != 0) {
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject postdata = posts.getJSONObject(i);
                                mPostData.add(PostData.createUserPostData(postdata));
                                mPost_Ids.add(postdata.getString("post_id"));
                            }
                            cb.onSuccess(headerData, mPostData, mPost_Ids);
                        } else {
                            cb.onEmpty(headerData);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetUserLocalCode localCode = Util.getUserLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getUserLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_rest_response(JSONObject jsonObject, GetUserAndRestResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<PostData> mPostData = new ArrayList<>();
                        final ArrayList<String> mPost_Ids = new ArrayList<>();

                        JSONObject payload = jsonObject.getJSONObject("payload");
                        JSONObject user = payload.getJSONObject("rest");

                        HeaderData headerData = HeaderData.createTenpoHeaderData(user);

                        JSONArray posts = payload.getJSONArray("posts");
                        if (posts.length() != 0) {
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject postdata = posts.getJSONObject(i);
                                mPostData.add(PostData.createRestPostData(postdata));
                                mPost_Ids.add(postdata.getString("post_id"));
                            }
                            cb.onSuccess(headerData, mPostData, mPost_Ids);
                        } else {
                            cb.onEmpty(headerData);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetRestLocalCode localCode = Util.getRestLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getRestLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_comment_response(JSONObject jsonObject, GetCommentResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<HeaderData> mCommentData = new ArrayList<>();

                        JSONObject payload = jsonObject.getJSONObject("payload");
                        JSONObject memo = payload.getJSONObject("memo");

                        HeaderData headerData = HeaderData.createMemoData(memo);

                        JSONArray comments = payload.getJSONArray("comments");
                        if (comments.length() != 0) {
                            for (int i = 0; i < comments.length(); i++) {
                                JSONObject commentData = comments.getJSONObject(i);
                                mCommentData.add(HeaderData.createCommentData(commentData));
                            }
                            cb.onSuccess(headerData, mCommentData);
                        } else {
                            cb.onEmpty(headerData);
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetCommentLocalCode localCode = Util.getCommentLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getCommentLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_follow_response(JSONObject jsonObject, GetListResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<ListGetData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(ListGetData.createUserData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetFollowLocalCode localCode = Util.getFollowLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getFollowLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_follower_response(JSONObject jsonObject, GetListResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<ListGetData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(ListGetData.createUserData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetFollowerLocalCode localCode = Util.getFollowerLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getFollowerLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_want_response(JSONObject jsonObject, GetListResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<ListGetData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(ListGetData.createRestData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetWantLocalCode localCode = Util.getWantLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getWantLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_user_cheer_response(JSONObject jsonObject, GetListResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<ListGetData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(ListGetData.createRestData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetUserCheerLocalCode localCode = Util.getUserCheerLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getUserCheerLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_rest_cheer_response(JSONObject jsonObject, GetListResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<ListGetData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(ListGetData.createUserData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetRestCheerLocalCode localCode = Util.getRestCheerLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getRestCheerLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_notice_response(JSONObject jsonObject, GetNoticeResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<HeaderData> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                mListData.add(HeaderData.createNoticeHeaderData(listData));
                            }
                            cb.onSuccess(mListData);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetNoticeLocalCode localCode = Util.getNoticeLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getNoticeLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_near_response(JSONObject jsonObject, GetNearResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        String[] restnames = new String[30];
                        final ArrayList<String> restIdArray = new ArrayList<>();
                        final ArrayList<String> restnameArray = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        if (payload.length() != 0) {
                            for (int i = 0; i < payload.length(); i++) {
                                JSONObject listData = payload.getJSONObject(i);
                                final String rest_name = listData.getString("restname");
                                String rest_id = listData.getString("rest_id");

                                restnames[i] = rest_name;
                                restIdArray.add(rest_id);
                                restnameArray.add(rest_name);
                            }
                            cb.onSuccess(restnames, restIdArray, restnameArray);
                        } else {
                            cb.onEmpty();
                        }
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetNearLocalCode localCode = Util.getNearLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getNearLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }

        @Override
        public void get_heatmap_response(JSONObject jsonObject, GetHeatmapResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.globalErrorReverseLookupTable(code);
                if (globalCode != null) {
                    //GlobalCodeにヒット && LocalCodeではない
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        //成功
                        final ArrayList<HeatmapLog> mListData = new ArrayList<>();

                        JSONArray payload = jsonObject.getJSONArray("payload");
                        for (int i = 0; i < payload.length(); i++) {
                            JSONObject listData = payload.getJSONObject(i);
                            String rest_id = listData.getString("post_rest_id");
                            String restname = listData.getString("restname");
                            double lat = listData.getDouble("lat");
                            double lon = listData.getDouble("lon");
                            mListData.add(new HeatmapLog(rest_id, restname, lat, lon));
                        }
                        cb.onSuccess(mListData);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetHeatmapLocalCode localCode = Util.getHeatmapLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.getHeatmapLocalErrorMessageTable(localCode);
                        if (message.equals(errorMessage)) {
                            cb.onLocalError(message);
                        } else {
                            cb.onGlobalError(Util.GlobalCode.ERROR_SERVER_SIDE_FAILURE);
                        }
                    } else {
                        //ハンドリング不可　致命的バグ
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
            }
        }
    }
}
