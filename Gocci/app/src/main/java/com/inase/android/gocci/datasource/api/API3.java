package com.inase.android.gocci.datasource.api;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.utils.SavedData;

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

    Util.PostSnsLocalCode post_sns_parameter_regex(String provider, String token, String profile_img);

    Util.PostSnsLocalCode post_sns_response_regex();

    Util.PostSnsUnlinkLocalCode post_sns_unlink_parameter_regex(String provider, String token);

    Util.PostSnsUnlinkLocalCode post_sns_unlink_response_regex();

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

    Util.GlobalCode check_global_error();

    void auth_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_check_response(JSONObject jsonObject, CheckResponseCallback cb);

    void auth_signup_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_sns_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void auth_pass_login_response(JSONObject jsonObject, AuthResponseCallback cb);

    void post_sns_response(JSONObject jsonObject, PostSnsResponseCallback cb);

    void post_sns_unlink_response(JSONObject jsonObject, PostSnsResponseCallback cb);

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

    interface PostSnsResponseCallback {
        void onSuccess();

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

    class Util {
        private static final String baseurl = "https://api.gocci.me/v1/mobile";
        private static final String testurl = "http://test.mobile.api.gocci.me/v3";

        public static final ConcurrentHashMap<GlobalCode, String> globalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, GlobalCode> globalReverseMap = new ConcurrentHashMap<>();

        public enum GlobalCode {
            SUCCESS,    //"Successful API request"
            ERROR_UNKNOWN_ERROR,    //"Unknown global error" //サーバに送る
            ERROR_SESSION_EXPIRED,  //"Session cookie is not valid anymore"　//ログインとコグニートリフレッシュ　→　リトライ
            ERROR_CLIENT_OUTDATED,  //"The client version is too old for this API. Client update necessary" //アップデートダイアログ
            ERROR_NO_INTERNET_CONNECTION,   //"The device appreas to be not connected to the internet" //リトライ
            ERROR_CONNECTION_FAILED,    //"Server connection failed"
            ERROR_CONNECTION_TIMEOUT,   //"Timeout reached before request finished" //リトライ？
            ERROR_SERVER_SIDE_FAILURE,  //"HTTP status differed from 200, indicationg failure on the server side"　//サーバーに送る
            ERROR_NO_DATA_RECIEVED, //"Connection successful but no data recieved" //サーバーに送る
            ERROR_BASEFRAME_JSON_MALFORMED,   //"JSON response baseframe not parsable" //サーバーに送る
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
            ERROR_USERNAME_ALREADY_REGISTERD,   //"The provided username was already registerd by another user"
            ERROR_REGISTER_ID_ALREADY_REGISTERD,    //"This deviced already has an registerd account"
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
            ERROR_REGISTER_ID_ALREADY_REGISTERD,    //"This deviced already has an registerd account"
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

        public static final ConcurrentHashMap<PostSnsLocalCode, String> postSnsLocalMap = new ConcurrentHashMap<>();
        public static final ConcurrentHashMap<String, PostSnsLocalCode> postSnsLocalReverseMap = new ConcurrentHashMap<>();

        public enum PostSnsLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROFILE_IMAGE_DOES_NOT_EXIST,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_TOKEN_MISSING,
            ERROR_PARAMETER_TOKEN_MALFORMED,
            ERROR_PARAMETER_PROFILE_IMG_MISSING,
            ERROR_PARAMETER_PROFILE_IMG_MALFORMED,
        }

        public static PostSnsLocalCode postSnsLocalErrorReverseLookupTable(String code) {
            if (postSnsLocalReverseMap.isEmpty()) {
                postSnsLocalReverseMap.put("ERROR_SNS_PROVIDER_TOKEN_NOT_VALID", PostSnsLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID);
                postSnsLocalReverseMap.put("ERROR_PROFILE_IMAGE_DOES_NOT_EXIST", PostSnsLocalCode.ERROR_PROFILE_IMAGE_DOES_NOT_EXIST);
                postSnsLocalReverseMap.put("ERROR_PROVIDER_UNREACHABLE", PostSnsLocalCode.ERROR_PROVIDER_UNREACHABLE);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MISSING", PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MISSING);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_PROVIDER_MALFORMED", PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MISSING", PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MISSING);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_TOKEN_MALFORMED", PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MISSING", PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING);
                postSnsLocalReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MALFORMED", PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED);
            }
            PostSnsLocalCode localCode = null;
            for (Map.Entry<String, PostSnsLocalCode> entry : postSnsLocalReverseMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    localCode = entry.getValue();
                    break;
                }
            }
            return localCode;
        }

        public static String postSnsLocalErrorMessageTable(PostSnsLocalCode localCode) {
            if (postSnsLocalMap.isEmpty()) {
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID, "The provided sns token is invalid or has expired");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PROFILE_IMAGE_DOES_NOT_EXIST, "The provided link to the profile image cound not be downloaded");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PROVIDER_UNREACHABLE, "The providers server infrastructure appears to be down");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MISSING, "Parameter 'provider' does not exist.");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED, "Parameter 'provider' is malformed. Should correspond to '^\\w{4,20}$'");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MISSING, "Parameter 'token' does not exist.");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED, "Parameter 'token' is malformed. Should correspond to '^\\w{4,20}$'");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING, "Parameter 'profile_img' does not exist.");
                postSnsLocalMap.put(PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED, "Parameter 'profile_img' is malformed. Should correspond to '^\\w{4,20}$'");
            }
            String message = null;
            for (Map.Entry<PostSnsLocalCode, String> entry : postSnsLocalMap.entrySet()) {
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

        public static String getPostSnsAPI(String provider, String token, String profile_img) {
            return testurl + "/post/sns/?provider=" + provider + "&token=" + token + "&profile_img=" + profile_img;
        }

        public static String getPostSnsUnlinkAPI(String provider, String token) {
            return testurl + "/post/sns_unlink/?provider=" + provider + "&token=" + token;
        }

        public static String getGetNearlineAPI(double lon, double lat) {
            return testurl + "/get/nearline/?lon=" + lon + "&lat=" + lat;
        }

        public static String getGetFollowlineAPI() {
            return testurl + "/get/followline";
        }

        public static String getGetTimelineAPI() {
            return testurl + "/get/timeline";
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
                if (!model.matches("^[a-zA-Z0-9_-]{0,10}$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                }
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
        public Util.PostSnsLocalCode post_sns_parameter_regex(String provider, String token, String profile_img) {
            if (provider != null) {
                if (!provider.matches("^(api.twitter.com)|(graph.facebook.com)$")) {
                    return Util.PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                }
            } else {
                return Util.PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
            }
            if (token != null) {
                if (!token.matches("^\\S{20,4000}$")) {
                    return Util.PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED;
                }
            } else {
                return Util.PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MISSING;
            }
            if (profile_img != null) {
                if (!profile_img.matches("^http\\S+$")) {
                    return Util.PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING;
            }
            return null;
        }

        @Override
        public Util.PostSnsLocalCode post_sns_response_regex() {
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
        public void post_sns_response(JSONObject jsonObject, PostSnsResponseCallback cb) {
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
                    Util.PostSnsLocalCode localCode = Util.postSnsLocalErrorReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.postSnsLocalErrorMessageTable(localCode);
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
        public void post_sns_unlink_response(JSONObject jsonObject, PostSnsResponseCallback cb) {
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
    }
}
