package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.utils.SavedData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    Util.GetNearlineLocalCode get_nearline_parameter_regex(double lon, double lat);

    Util.GetNearlineLocalCode get_nearline_response_regex();

    Util.GetFollowlineLocalCode get_followline_parameter_regex();

    Util.GetFollowlineLocalCode get_followline_response_regex();

    Util.GetTimelineLocalCode get_timeline_parameter_regex();

    Util.GetTimelineLocalCode get_timeline_response_regex();

    Util.GetUserLocalCode get_user_parameter_regex(String user_id);

    Util.GetUserLocalCode get_user_response_regex();

    Util.GetRestLocalCode get_rest_parameter_regex(String rest_id);

    Util.GetRestLocalCode get_rest_response_regex();

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

    class Util {
        private static final String baseurl = "https://api.gocci.me/v1/mobile";
        private static final String testurl = "http://test.mobile.api.gocci.me/v3";

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
            GlobalCode globalCode = null;
            switch (code) {
                case "SUCCESS":
                    globalCode = GlobalCode.SUCCESS;
                    break;
                case "ERROR_UNKNOWN_ERROR":
                    globalCode = GlobalCode.ERROR_UNKNOWN_ERROR;
                    break;
                case "ERROR_SESSION_EXPIRED":
                    globalCode = GlobalCode.ERROR_SESSION_EXPIRED;
                    break;
                case "ERROR_CLIENT_OUTDATED":
                    globalCode = GlobalCode.ERROR_CLIENT_OUTDATED;
                    break;
                case "ERROR_NO_INTERNET_CONNECTION":
                    globalCode = GlobalCode.ERROR_NO_INTERNET_CONNECTION;
                    break;
                case "ERROR_CONNECTION_FAILED":
                    globalCode = GlobalCode.ERROR_CONNECTION_FAILED;
                    break;
                case "ERROR_CONNECTION_TIMEOUT":
                    globalCode = GlobalCode.ERROR_CONNECTION_TIMEOUT;
                    break;
                case "ERROR_SERVER_SIDE_FAILURE":
                    globalCode = GlobalCode.ERROR_SERVER_SIDE_FAILURE;
                    break;
                case "ERROR_NO_DATA_RECIEVED":
                    globalCode = GlobalCode.ERROR_NO_DATA_RECIEVED;
                    break;
                case "ERROR_BASEFRAME_JSON_MALFORMED":
                    globalCode = GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED;
                    break;
            }
            return globalCode;
        }

        public static String globalErrorMessageTable(GlobalCode globalCode) {
            String message = null;
            switch (globalCode) {
                case SUCCESS:
                    message = "Successful API request";
                    break;
                case ERROR_UNKNOWN_ERROR:
                    message = "Unknown global error";
                    break;
                case ERROR_SESSION_EXPIRED:
                    message = "Session cookie is not valid anymore";
                    break;
                case ERROR_CLIENT_OUTDATED:
                    message = "The client version is too old for this API. Client update necessary";
                    break;
                case ERROR_NO_INTERNET_CONNECTION:
                    message = "The device appreas to be not connected to the internet";
                    break;
                case ERROR_CONNECTION_FAILED:
                    message = "Server connection failed";
                    break;
                case ERROR_CONNECTION_TIMEOUT:
                    message = "Timeout reached before request finished";
                    break;
                case ERROR_SERVER_SIDE_FAILURE:
                    message = "HTTP status differed from 200, indicationg failure on the server side";
                    break;
                case ERROR_NO_DATA_RECIEVED:
                    message = "Connection successful but no data recieved";
                    break;
                case ERROR_BASEFRAME_JSON_MALFORMED:
                    message = "JSON response baseframe not parsable";
                    break;
            }
            return message;
        }

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
            AuthSignupLocalCode localCode = null;
            switch (code) {
                case "ERROR_USERNAME_ALREADY_REGISTERD":
                    localCode = AuthSignupLocalCode.ERROR_USERNAME_ALREADY_REGISTERD;
                    break;
                case "ERROR_REGISTER_ID_ALREADY_REGISTERD":
                    localCode = AuthSignupLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD;
                    break;
                case "ERROR_PARAMETER_USERNAME_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
                    break;
                case "ERROR_PARAMETER_USERNAME_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                    break;
                case "ERROR_PARAMETER_OS_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_OS_MISSING;
                    break;
                case "RROR_PARAMETER_OS_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                    break;
                case "ERROR_PARAMETER_VER_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_VER_MISSING;
                    break;
                case "ERROR_PARAMETER_VER_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                    break;
                case "ERROR_PARAMETER_MODEL_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MISSING;
                    break;
                case "ERROR_PARAMETER_MODEL_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USER_ID_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_USER_ID_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USERNAME_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
                    break;
                case "ERROR_RESPONSE_USERNAME_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                    break;
                case "ERROR_RESPONSE_TOKEN_MISSING":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
                    break;
                case "ERROR_RESPONSE_TOKEN_MALFORMED":
                    localCode = AuthSignupLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String authSignupLocalErrorMessageTable(AuthSignupLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_USERNAME_ALREADY_REGISTERD:
                    message = "The provided username was already registerd by another user";
                    break;
                case ERROR_REGISTER_ID_ALREADY_REGISTERD:
                    message = "This deviced already has an registerd account";
                    break;
                case ERROR_PARAMETER_USERNAME_MISSING:
                    message = "Parameter 'username' does not exist.";
                    break;
                case ERROR_PARAMETER_USERNAME_MALFORMED:
                    message = "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_PARAMETER_OS_MISSING:
                    message = "Parameter 'os' does not exist.";
                    break;
                case ERROR_PARAMETER_OS_MALFORMED:
                    message = "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'";
                    break;
                case ERROR_PARAMETER_VER_MISSING:
                    message = "Parameter 'ver' does not exist.";
                    break;
                case ERROR_PARAMETER_VER_MALFORMED:
                    message = "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_PARAMETER_MODEL_MISSING:
                    message = "Parameter 'model' does not exist.";
                    break;
                case ERROR_PARAMETER_MODEL_MALFORMED:
                    message = "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MISSING:
                    message = "Parameter 'register_id' does not exist.";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MALFORMED:
                    message = "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'";
                    break;
                case ERROR_RESPONSE_USER_ID_MISSING:
                    message = "Response 'user_id' was not received";
                    break;
                case ERROR_RESPONSE_USER_ID_MALFORMED:
                    message = "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_USERNAME_MISSING:
                    message = "Response 'username' was not received";
                    break;
                case ERROR_RESPONSE_USERNAME_MALFORMED:
                    message = "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MISSING:
                    message = "Response 'profile_img' was not received";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MALFORMED:
                    message = "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MISSING:
                    message = "Response 'identity_id' was not received";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MALFORMED:
                    message = "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MISSING:
                    message = "Response 'badge_num' was not received";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MALFORMED:
                    message = "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_TOKEN_MISSING:
                    message = "Response 'token' was not received";
                    break;
                case ERROR_RESPONSE_TOKEN_MALFORMED:
                    message = "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'";
                    break;
            }
            return message;
        }

        public enum AuthCheckLocalCode {
            ERROR_REGISTER_ID_ALREADY_REGISTERD,    //"This deviced already has an registerd account"
            ERROR_PARAMETER_REGISTER_ID_MISSING,
            ERROR_PARAMETER_REGISTER_ID_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED
        }

        public static AuthCheckLocalCode authCheckLocalErrorReverseLookupTable(String code) {
            AuthCheckLocalCode localCode = null;
            switch (code) {
                case "ERROR_REGISTER_ID_ALREADY_REGISTERD":
                    localCode = AuthCheckLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MISSING":
                    localCode = AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MALFORMED":
                    localCode = AuthCheckLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MISSING":
                    localCode = AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MALFORMED":
                    localCode = AuthCheckLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String authCheckLocalErrorMessageTable(AuthCheckLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_REGISTER_ID_ALREADY_REGISTERD:
                    message = "This deviced already has an registerd account";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MISSING:
                    message = "Parameter 'register_id' does not exist.";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MALFORMED:
                    message = "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MISSING:
                    message = "Response 'identity_id' was not received";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MALFORMED:
                    message = "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
            }
            return message;
        }

        public enum AuthLoginLocalCode {
            ERROR_IDENTITY_ID_NOT_REGISTERD,    //"This deviced already has an registerd account"
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
            AuthLoginLocalCode localCode = null;
            switch (code) {
                case "ERROR_IDENTITY_ID_NOT_REGISTERD":
                    localCode = AuthLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD;
                    break;
                case "ERROR_PARAMETER_IDENTITY_ID_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_IDENTITY_ID_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USER_ID_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_USER_ID_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USERNAME_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
                    break;
                case "ERROR_RESPONSE_USERNAME_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                    break;
                case "ERROR_RESPONSE_TOKEN_MISSING":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
                    break;
                case "ERROR_RESPONSE_TOKEN_MALFORMED":
                    localCode = AuthLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String authLoginLocalErrorMessageTable(AuthLoginLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_IDENTITY_ID_NOT_REGISTERD:
                    message = "The provided identity_id is not bound to any account";
                    break;
                case ERROR_PARAMETER_IDENTITY_ID_MISSING:
                    message = "Parameter 'identity_id' does not exist.";
                    break;
                case ERROR_PARAMETER_IDENTITY_ID_MALFORMED:
                    message = "Parameter 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_RESPONSE_USER_ID_MISSING:
                    message = "Response 'user_id' was not received";
                    break;
                case ERROR_RESPONSE_USER_ID_MALFORMED:
                    message = "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_USERNAME_MISSING:
                    message = "Response 'username' was not received";
                    break;
                case ERROR_RESPONSE_USERNAME_MALFORMED:
                    message = "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MISSING:
                    message = "Response 'profile_img' was not received";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MALFORMED:
                    message = "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MISSING:
                    message = "Response 'identity_id' was not received";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MALFORMED:
                    message = "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MISSING:
                    message = "Response 'badge_num' was not received";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MALFORMED:
                    message = "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_TOKEN_MISSING:
                    message = "Response 'token' was not received";
                    break;
                case ERROR_RESPONSE_TOKEN_MALFORMED:
                    message = "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'";
                    break;
            }
            return message;
        }

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
            AuthSnsLoginLocalCode localCode = null;
            switch (code) {
                case "ERROR_REGISTER_ID_ALREADY_REGISTERD":
                    localCode = AuthSnsLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD;
                    break;
                case "ERROR_IDENTITY_ID_NOT_REGISTERD":
                    localCode = AuthSnsLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD;
                    break;
                case "ERROR_PARAMETER_IDENTITY_ID_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_USERNAME_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_PARAMETER_OS_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MISSING;
                    break;
                case "RROR_PARAMETER_OS_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                    break;
                case "ERROR_PARAMETER_VER_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MISSING;
                    break;
                case "ERROR_PARAMETER_VER_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                    break;
                case "ERROR_PARAMETER_MODEL_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING;
                    break;
                case "ERROR_PARAMETER_MODEL_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USER_ID_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_USER_ID_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USERNAME_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
                    break;
                case "ERROR_RESPONSE_USERNAME_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                    break;
                case "ERROR_RESPONSE_TOKEN_MISSING":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
                    break;
                case "ERROR_RESPONSE_TOKEN_MALFORMED":
                    localCode = AuthSnsLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String authSnsLoginLocalErrorMessageTable(AuthSnsLoginLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_REGISTER_ID_ALREADY_REGISTERD:
                    message = "This deviced already has an registerd account";
                    break;
                case ERROR_IDENTITY_ID_NOT_REGISTERD:
                    message = "The provided identity_id is not bound to any account";
                    break;
                case ERROR_PARAMETER_IDENTITY_ID_MISSING:
                    message = "Parameter 'identity_id' does not exist.";
                    break;
                case ERROR_PARAMETER_IDENTITY_ID_MALFORMED:
                    message = "Parameter 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_PARAMETER_OS_MISSING:
                    message = "Parameter 'os' does not exist.";
                    break;
                case ERROR_PARAMETER_OS_MALFORMED:
                    message = "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'";
                    break;
                case ERROR_PARAMETER_VER_MISSING:
                    message = "Parameter 'ver' does not exist.";
                    break;
                case ERROR_PARAMETER_VER_MALFORMED:
                    message = "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_PARAMETER_MODEL_MISSING:
                    message = "Parameter 'model' does not exist.";
                    break;
                case ERROR_PARAMETER_MODEL_MALFORMED:
                    message = "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MISSING:
                    message = "Parameter 'register_id' does not exist.";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MALFORMED:
                    message = "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'";
                    break;
                case ERROR_RESPONSE_USER_ID_MISSING:
                    message = "Response 'user_id' was not received";
                    break;
                case ERROR_RESPONSE_USER_ID_MALFORMED:
                    message = "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_USERNAME_MISSING:
                    message = "Response 'username' was not received";
                    break;
                case ERROR_RESPONSE_USERNAME_MALFORMED:
                    message = "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MISSING:
                    message = "Response 'profile_img' was not received";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MALFORMED:
                    message = "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MISSING:
                    message = "Response 'identity_id' was not received";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MALFORMED:
                    message = "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MISSING:
                    message = "Response 'badge_num' was not received";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MALFORMED:
                    message = "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_TOKEN_MISSING:
                    message = "Response 'token' was not received";
                    break;
                case ERROR_RESPONSE_TOKEN_MALFORMED:
                    message = "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'";
                    break;
            }
            return message;
        }

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
            AuthPassLoginLocalCode localCode = null;
            switch (code) {
                case "ERROR_REGISTER_ID_ALREADY_REGISTERD":
                    localCode = AuthPassLoginLocalCode.ERROR_REGISTER_ID_ALREADY_REGISTERD;
                    break;
                case "ERROR_USERNAME_NOT_REGISTERD":
                    localCode = AuthPassLoginLocalCode.ERROR_USERNAME_NOT_REGISTERD;
                    break;
                case "ERROR_PASSWORD_NOT_REGISTERD":
                    localCode = AuthPassLoginLocalCode.ERROR_PASSWORD_NOT_REGISTERD;
                    break;
                case "ERROR_PASSWORD_WRONG":
                    localCode = AuthPassLoginLocalCode.ERROR_PASSWORD_WRONG;
                    break;
                case "ERROR_PARAMETER_USERNAME_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
                    break;
                case "ERROR_PARAMETER_USERNAME_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                    break;
                case "ERROR_PARAMETER_PASSWORD_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MISSING;
                    break;
                case "ERROR_PARAMETER_PASSWORD_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED;
                    break;
                case "ERROR_PARAMETER_OS_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MISSING;
                    break;
                case "RROR_PARAMETER_OS_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                    break;
                case "ERROR_PARAMETER_VER_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MISSING;
                    break;
                case "ERROR_PARAMETER_VER_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                    break;
                case "ERROR_PARAMETER_MODEL_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MISSING;
                    break;
                case "ERROR_PARAMETER_MODEL_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_REGISTER_ID_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_PARAMETER_REGISTER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USER_ID_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_USER_ID_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_USERNAME_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING;
                    break;
                case "ERROR_RESPONSE_USERNAME_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING;
                    break;
                case "ERROR_RESPONSE_PROFILE_IMG_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING;
                    break;
                case "ERROR_RESPONSE_IDENTITY_ID_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING;
                    break;
                case "ERROR_RESPONSE_BADGE_NUM_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED;
                    break;
                case "ERROR_RESPONSE_TOKEN_MISSING":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MISSING;
                    break;
                case "ERROR_RESPONSE_TOKEN_MALFORMED":
                    localCode = AuthPassLoginLocalCode.ERROR_RESPONSE_TOKEN_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String authPassLoginLocalErrorMessageTable(AuthPassLoginLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_REGISTER_ID_ALREADY_REGISTERD:
                    message = "This deviced already has an registerd account";
                    break;
                case ERROR_USERNAME_NOT_REGISTERD:
                    message = "The entered username does not exist";
                    break;
                case ERROR_PASSWORD_NOT_REGISTERD:
                    message = "The entered password does not exist";
                    break;
                case ERROR_PASSWORD_WRONG:
                    message = "Password wrong";
                    break;
                case ERROR_PARAMETER_USERNAME_MISSING:
                    message = "Parameter 'username' does not exist.";
                    break;
                case ERROR_PARAMETER_USERNAME_MALFORMED:
                    message = "Parameter 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_PARAMETER_PASSWORD_MISSING:
                    message = "Parameter 'password' does not exist.";
                    break;
                case ERROR_PARAMETER_PASSWORD_MALFORMED:
                    message = "Parameter 'password' is malformed. Should correspond to '^\\w{6,25}$'";
                    break;
                case ERROR_PARAMETER_OS_MISSING:
                    message = "Parameter 'os' does not exist.";
                    break;
                case ERROR_PARAMETER_OS_MALFORMED:
                    message = "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'";
                    break;
                case ERROR_PARAMETER_VER_MISSING:
                    message = "Parameter 'ver' does not exist.";
                    break;
                case ERROR_PARAMETER_VER_MALFORMED:
                    message = "Parameter 'ver' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_PARAMETER_MODEL_MISSING:
                    message = "Parameter 'model' does not exist.";
                    break;
                case ERROR_PARAMETER_MODEL_MALFORMED:
                    message = "Parameter 'model' is malformed. Should correspond to '^[a-zA-Z0-9_-]{0,10}$'";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MISSING:
                    message = "Parameter 'register_id' does not exist.";
                    break;
                case ERROR_PARAMETER_REGISTER_ID_MALFORMED:
                    message = "Parameter 'register_id' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'";
                    break;
                case ERROR_RESPONSE_USER_ID_MISSING:
                    message = "Response 'user_id' was not received";
                    break;
                case ERROR_RESPONSE_USER_ID_MALFORMED:
                    message = "Response 'user_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_USERNAME_MISSING:
                    message = "Response 'username' was not received";
                    break;
                case ERROR_RESPONSE_USERNAME_MALFORMED:
                    message = "Response 'username' is malformed. Should correspond to '^\\w{4,20}$'";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MISSING:
                    message = "Response 'profile_img' was not received";
                    break;
                case ERROR_RESPONSE_PROFILE_IMG_MALFORMED:
                    message = "Response 'profile_img' is malformed. Should correspond to '^http\\S+$'";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MISSING:
                    message = "Response 'identity_id' was not received";
                    break;
                case ERROR_RESPONSE_IDENTITY_ID_MALFORMED:
                    message = "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MISSING:
                    message = "Response 'badge_num' was not received";
                    break;
                case ERROR_RESPONSE_BADGE_NUM_MALFORMED:
                    message = "Response 'badge_num' is malformed. Should correspond to '^[0-9]+$'";
                    break;
                case ERROR_RESPONSE_TOKEN_MISSING:
                    message = "Response 'token' was not received";
                    break;
                case ERROR_RESPONSE_TOKEN_MALFORMED:
                    message = "Response 'token' is malformed. Should correspond to '^[a-zA-Z0-9.-_]{400,2200}$'";
                    break;
            }
            return message;
        }

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
            PostSnsLocalCode localCode = null;
            switch (code) {
                case "ERROR_SNS_PROVIDER_TOKEN_NOT_VALID":
                    localCode = PostSnsLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID;
                    break;
                case "ERROR_PROFILE_IMAGE_DOES_NOT_EXIST":
                    localCode = PostSnsLocalCode.ERROR_PROFILE_IMAGE_DOES_NOT_EXIST;
                    break;
                case "ERROR_PROVIDER_UNREACHABLE":
                    localCode = PostSnsLocalCode.ERROR_PROVIDER_UNREACHABLE;
                    break;
                case "ERROR_PARAMETER_PROVIDER_MISSING":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
                    break;
                case "ERROR_PARAMETER_PROVIDER_MALFORMED":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                    break;
                case "ERROR_PARAMETER_TOKEN_MISSING":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MISSING;
                    break;
                case "ERROR_PARAMETER_TOKEN_MALFORMED":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED;
                    break;
                case "ERROR_PARAMETER_PROFILE_IMG_MISSING":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING;
                    break;
                case "ERROR_PARAMETER_PROFILE_IMG_MALFORMED":
                    localCode = PostSnsLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String postSnsLocalErrorMessageTable(PostSnsLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_SNS_PROVIDER_TOKEN_NOT_VALID:
                    message = "The provided sns token is invalid or has expired";
                    break;
                case ERROR_PROFILE_IMAGE_DOES_NOT_EXIST:
                    message = "The provided link to the profile image cound not be downloaded";
                    break;
                case ERROR_PROVIDER_UNREACHABLE:
                    message = "The providers server infrastructure appears to be down";
                    break;
                case ERROR_PARAMETER_PROVIDER_MISSING:
                    message = "Parameter 'provider' does not exist.'";
                    break;
                case ERROR_PARAMETER_PROVIDER_MALFORMED:
                    message = "Parameter 'provider' is malformed. Should correspond to '^(api.twitter.com)|(graph.facebook.com)$'";
                    break;
                case ERROR_PARAMETER_TOKEN_MISSING:
                    message = "Parameter 'token' does not exist.";
                    break;
                case ERROR_PARAMETER_TOKEN_MALFORMED:
                    message = "Parameter 'token' is malformed. Should correspond to '^\\S{20,4000}$'";
                    break;
                case ERROR_PARAMETER_PROFILE_IMG_MISSING:
                    message = "Parameter 'profile_img' does not exist.";
                    break;
                case ERROR_PARAMETER_PROFILE_IMG_MALFORMED:
                    message = "Parameter 'profile_img' is malformed. Should correspond to '^http\\S+$'";
                    break;
            }
            return message;
        }

        public enum PostSnsUnlinkLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_TOKEN_MISSING,
            ERROR_PARAMETER_TOKEN_MALFORMED,
        }

        public static PostSnsUnlinkLocalCode postSnsUnlinkLocalErrorReverseLookupTable(String code) {
            PostSnsUnlinkLocalCode localCode = null;
            switch (code) {
                case "ERROR_SNS_PROVIDER_TOKEN_NOT_VALID":
                    localCode = PostSnsUnlinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID;
                    break;
                case "ERROR_PROVIDER_UNREACHABLE":
                    localCode = PostSnsUnlinkLocalCode.ERROR_PROVIDER_UNREACHABLE;
                    break;
                case "ERROR_PARAMETER_PROVIDER_MISSING":
                    localCode = PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
                    break;
                case "ERROR_PARAMETER_PROVIDER_MALFORMED":
                    localCode = PostSnsUnlinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                    break;
                case "ERROR_PARAMETER_TOKEN_MISSING":
                    localCode = PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MISSING;
                    break;
                case "ERROR_PARAMETER_TOKEN_MALFORMED":
                    localCode = PostSnsUnlinkLocalCode.ERROR_PARAMETER_TOKEN_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String postSnsUnlinkLocalErrorMessageTable(PostSnsUnlinkLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_SNS_PROVIDER_TOKEN_NOT_VALID:
                    message = "The provided sns token is invalid or has expired";
                    break;
                case ERROR_PROVIDER_UNREACHABLE:
                    message = "The providers server infrastructure appears to be down";
                    break;
                case ERROR_PARAMETER_PROVIDER_MISSING:
                    message = "Parameter 'provider' does not exist.'";
                    break;
                case ERROR_PARAMETER_PROVIDER_MALFORMED:
                    message = "Parameter 'provider' is malformed. Should correspond to '^(api.twitter.com)|(graph.facebook.com)$'";
                    break;
                case ERROR_PARAMETER_TOKEN_MISSING:
                    message = "Parameter 'token' does not exist.";
                    break;
                case ERROR_PARAMETER_TOKEN_MALFORMED:
                    message = "Parameter 'token' is malformed. Should correspond to '^\\S{20,4000}$'";
                    break;
            }
            return message;
        }

        public enum GetTimelineLocalCode {

        }

        public static GetTimelineLocalCode getTimelineLocalErrorReverseLookupTable(String code) {
            GetTimelineLocalCode localCode = null;
            switch (code) {

            }
            return localCode;
        }

        public static String getTimelineLocalErrorMessageTable(GetTimelineLocalCode localCode) {
            String message = null;
            switch (localCode) {

            }
            return message;
        }

        public enum GetFollowlineLocalCode {
            ERROR_FOLLOW_USER_NOT_EXIST,
        }

        public static GetFollowlineLocalCode getFollowlineLocalErrorReverseLookupTable(String code) {
            GetFollowlineLocalCode localCode = null;
            switch (code) {
                case "":
                    localCode = GetFollowlineLocalCode.ERROR_FOLLOW_USER_NOT_EXIST;
                    break;
            }
            return localCode;
        }

        public static String getFollowlineLocalErrorMessageTable(GetFollowlineLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_FOLLOW_USER_NOT_EXIST:
                    message = "";
                    break;
            }
            return message;
        }

        public enum GetNearlineLocalCode {
            ERROR_PARAMETER_LON_MISSING,
            ERROR_PARAMETER_LON_MALFORMED,
            ERROR_PARAMETER_LAT_MISSING,
            ERROR_PARAMETER_LAT_MALFORMED,
        }

        public static GetNearlineLocalCode getNearlineLocalErrorReverseLookupTable(String code) {
            GetNearlineLocalCode localCode = null;
            switch (code) {
                case "ERROR_PARAMETER_LON_MISSING":
                    localCode = GetNearlineLocalCode.ERROR_PARAMETER_LON_MISSING;
                    break;
                case "ERROR_PARAMETER_LON_MALFORMED":
                    localCode = GetNearlineLocalCode.ERROR_PARAMETER_LON_MALFORMED;
                    break;
                case "ERROR_PARAMETER_LAT_MISSING":
                    localCode = GetNearlineLocalCode.ERROR_PARAMETER_LAT_MISSING;
                    break;
                case "ERROR_PARAMETER_LAT_MALFORMED":
                    localCode = GetNearlineLocalCode.ERROR_PARAMETER_LAT_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String getNearlineLocalErrorMessageTable(GetNearlineLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_PARAMETER_LON_MISSING:
                    message = "Parameter 'lon' does not exist.";
                    break;
                case ERROR_PARAMETER_LON_MALFORMED:
                    message = "";
                    break;
                case ERROR_PARAMETER_LAT_MISSING:
                    message = "Parameter 'lat' does not exist.";
                    break;
                case ERROR_PARAMETER_LAT_MALFORMED:
                    message = "";
                    break;
            }
            return message;
        }

        public enum GetUserLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static GetUserLocalCode getUserLocalErrorReverseLookupTable(String code) {
            GetUserLocalCode localCode = null;
            switch (code) {
                case "ERROR_PARAMETER_USER_ID_MISSING":
                    localCode = GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_USER_ID_MALFORMED":
                    localCode = GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String getUserLocalErrorMessageTable(GetUserLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_PARAMETER_USER_ID_MISSING:
                    message = "Parameter 'user_id' does not exist.";
                    break;
                case ERROR_PARAMETER_USER_ID_MALFORMED:
                    message = "Parameter 'user_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
            }
            return message;
        }

        public enum GetRestLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
        }

        public static GetRestLocalCode getRestLocalErrorReverseLookupTable(String code) {
            GetRestLocalCode localCode = null;
            switch (code) {
                case "ERROR_PARAMETER_REST_ID_MISSING":
                    localCode = GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
                    break;
                case "ERROR_PARAMETER_USER_ID_MALFORMED":
                    localCode = GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                    break;
            }
            return localCode;
        }

        public static String getRestLocalErrorMessageTable(GetRestLocalCode localCode) {
            String message = null;
            switch (localCode) {
                case ERROR_PARAMETER_REST_ID_MISSING:
                    message = "Parameter 'rest_id' does not exist.";
                    break;
                case ERROR_PARAMETER_REST_ID_MALFORMED:
                    message = "Parameter 'rest_id' is malformed. Should correspond to '^[0-9]+$'";
                    break;
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
        public Util.GetNearlineLocalCode get_nearline_parameter_regex(double lon, double lat) {
            return null;
        }

        @Override
        public Util.GetNearlineLocalCode get_nearline_response_regex() {
            return null;
        }

        @Override
        public Util.GetFollowlineLocalCode get_followline_parameter_regex() {
            return null;
        }

        @Override
        public Util.GetFollowlineLocalCode get_followline_response_regex() {
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
    }
}
