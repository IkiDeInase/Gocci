package com.inase.android.gocci.datasource.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface API3 {
    Util.UnsetSns_LinkLocalCode UnsetSns_LinkParameterRegex(String provider, String sns_token);

    void UnsetSns_LinkResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.UnsetFollowLocalCode UnsetFollowParameterRegex(String user_id);

    void UnsetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.UnsetDeviceLocalCode UnsetDeviceParameterRegex();

    void UnsetDeviceResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.UnsetWantLocalCode UnsetWantParameterRegex(String rest_id);

    void UnsetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.UnsetPostLocalCode UnsetPostParameterRegex(String post_id);

    void UnsetPostResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetFollowLocalCode SetFollowParameterRegex(String user_id);

    void SetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetPasswordLocalCode SetPasswordParameterRegex(String password);

    void SetPasswordResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetCommentLocalCode SetCommentParameterRegex(String post_id, String comment, String re_user_id);

    void SetCommentResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetGochiLocalCode SetGochiParameterRegex(String post_id);

    void SetGochiResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetFeedbackLocalCode SetFeedbackParameterRegex(String feedback);

    void SetFeedbackResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetWantLocalCode SetWantParameterRegex(String rest_id);

    void SetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetRestLocalCode SetRestParameterRegex(String restname, String lat, String lon);

    void SetRestResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetProfile_ImgLocalCode SetProfile_ImgParameterRegex(String profile_img);

    void SetProfile_ImgResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetPost_BlockLocalCode SetPost_BlockParameterRegex(String post_id);

    void SetPost_BlockResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetUsernameLocalCode SetUsernameParameterRegex(String username);

    void SetUsernameResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetSns_LinkLocalCode SetSns_LinkParameterRegex(String provider, String sns_token);

    void SetSns_LinkResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetDeviceLocalCode SetDeviceParameterRegex(String device_token, String os, String ver, String model);

    void SetDeviceResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.SetPostLocalCode SetPostParameterRegex(String rest_id, String movie_name, String category_id, String value, String memo, String cheer_flag);

    void SetPostResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.AuthPasswordLocalCode AuthPasswordParameterRegex(String username, String password);

    void AuthPasswordResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.AuthSignupLocalCode AuthSignupParameterRegex(String username);

    void AuthSignupResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.AuthLoginLocalCode AuthLoginParameterRegex(String identity_id);

    void AuthLoginResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetFollowlineLocalCode GetFollowlineParameterRegex(String page, String category_id, String value_id);

    void GetFollowlineResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetFollowLocalCode GetFollowParameterRegex(String user_id);

    void GetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetNearlineLocalCode GetNearlineParameterRegex(String lat, String lon, String page, String category_id, String value_id);

    void GetNearlineResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetCommentLocalCode GetCommentParameterRegex(String post_id);

    void GetCommentResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetHeatmapLocalCode GetHeatmapParameterRegex();

    void GetHeatmapResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetRest_CheerLocalCode GetRest_CheerParameterRegex(String rest_id);

    void GetRest_CheerResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetUser_CheerLocalCode GetUser_CheerParameterRegex(String user_id);

    void GetUser_CheerResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetWantLocalCode GetWantParameterRegex(String user_id);

    void GetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetNoticeLocalCode GetNoticeParameterRegex();

    void GetNoticeResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetNearLocalCode GetNearParameterRegex(String lat, String lon);

    void GetNearResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetRestLocalCode GetRestParameterRegex(String rest_id);

    void GetRestResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetTimelineLocalCode GetTimelineParameterRegex(String page, String category_id, String value_id);

    void GetTimelineResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetFollowerLocalCode GetFollowerParameterRegex(String user_id);

    void GetFollowerResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    Util.GetUserLocalCode GetUserParameterRegex(String user_id);

    void GetUserResponse(JSONObject jsonObject, PayloadResponseCallback cb);

    interface PayloadResponseCallback {
        void onSuccess(JSONObject payload);

        void onGlobalError(Util.GlobalCode globalCode);

        void onLocalError(String errorMessage);
    }

    class Util {
        public static final String liveurl = "https://mobile.api.gocci.me/v3";
        public static final String testurl = "http://test.mobile.api.gocci.me/v3";
        public static final String version = "3.7";

        private static final ConcurrentHashMap<GlobalCode, String> GlobalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GlobalCode> GlobalCodeReverseMap = new ConcurrentHashMap<>();

        public enum GlobalCode {
            SUCCESS,
            ERROR_SESSION_EXPIRED,
            ERROR_CLIENT_OUTDATED,
            ERROR_UNKNOWN_ERROR,
        }

        public static GlobalCode GlobalCodeReverseLookupTable(String message) {
            if (GlobalCodeReverseMap.isEmpty()) {
                GlobalCodeReverseMap.put("SUCCESS", GlobalCode.SUCCESS);
                GlobalCodeReverseMap.put("ERROR_SESSION_EXPIRED", GlobalCode.ERROR_SESSION_EXPIRED);
                GlobalCodeReverseMap.put("ERROR_CLIENT_OUTDATED", GlobalCode.ERROR_CLIENT_OUTDATED);
                GlobalCodeReverseMap.put("ERROR_UNKNOWN_ERROR", GlobalCode.ERROR_UNKNOWN_ERROR);
            }
            GlobalCode code = null;
            for (Map.Entry<String, GlobalCode> entry : GlobalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        public static String GlobalCodeMessageTable(GlobalCode code) {
            if (GlobalCodeMap.isEmpty()) {
                GlobalCodeMap.put(GlobalCode.SUCCESS, "Successful API request");
                GlobalCodeMap.put(GlobalCode.ERROR_SESSION_EXPIRED, "Session cookie is not valid anymore");
                GlobalCodeMap.put(GlobalCode.ERROR_CLIENT_OUTDATED, "The client version is too old for this API. Client update necessary");
                GlobalCodeMap.put(GlobalCode.ERROR_UNKNOWN_ERROR, "Unknown global error");
            }
            String message = null;
            for (Map.Entry<GlobalCode, String> entry : GlobalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        private static final ConcurrentHashMap<UnsetSns_LinkLocalCode, String> UnsetSns_LinkLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, UnsetSns_LinkLocalCode> UnsetSns_LinkLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getUnsetSnsLinkAPI(String provider, String sns_token) {
            StringBuilder url = new StringBuilder(testurl + "/unset/sns_link/");
            url.append("&provider=").append(provider);
            url.append("&sns_token=").append(sns_token);
            return url.toString().replace("/&", "/?");
        }

        public enum UnsetSns_LinkLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_SNS_TOKEN_MISSING,
            ERROR_PARAMETER_SNS_TOKEN_MALFORMED,
        }

        public static String UnsetSns_LinkLocalCodeMessageTable(UnsetSns_LinkLocalCode code) {
            if (UnsetSns_LinkLocalCodeMap.isEmpty()) {
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING, "Parameter 'sns_token' does not exist.");
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING, "Parameter 'provider' does not exist.");
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID, "The provided sns token is invalid or has expired");
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED, "Parameter 'sns_token' is malformed. Should correspond to '^[^\\p{Cntrl}]{20,4000}$'");
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_PROVIDER_UNREACHABLE, "The providers server infrastructure appears to be down");
                UnsetSns_LinkLocalCodeMap.put(UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED, "Parameter 'provider' is malformed. Should correspond to '^(api.twitter.com)|(graph.facebook.com)$'");
            }
            String message = null;
            for (Map.Entry<UnsetSns_LinkLocalCode, String> entry : UnsetSns_LinkLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static UnsetSns_LinkLocalCode UnsetSns_LinkLocalCodeReverseLookupTable(String message) {
            if (UnsetSns_LinkLocalCodeReverseMap.isEmpty()) {
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_SNS_TOKEN_MISSING", UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING);
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_PROVIDER_MISSING", UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING);
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_SNS_PROVIDER_TOKEN_NOT_VALID", UnsetSns_LinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID);
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_SNS_TOKEN_MALFORMED", UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED);
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_PROVIDER_UNREACHABLE", UnsetSns_LinkLocalCode.ERROR_PROVIDER_UNREACHABLE);
                UnsetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_PROVIDER_MALFORMED", UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED);
            }
            UnsetSns_LinkLocalCode code = null;
            for (Map.Entry<String, UnsetSns_LinkLocalCode> entry : UnsetSns_LinkLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<UnsetFollowLocalCode, String> UnsetFollowLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, UnsetFollowLocalCode> UnsetFollowLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getUnsetFollowAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/unset/follow/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum UnsetFollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static String UnsetFollowLocalCodeMessageTable(UnsetFollowLocalCode code) {
            if (UnsetFollowLocalCodeMap.isEmpty()) {
                UnsetFollowLocalCodeMap.put(UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                UnsetFollowLocalCodeMap.put(UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<UnsetFollowLocalCode, String> entry : UnsetFollowLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static UnsetFollowLocalCode UnsetFollowLocalCodeReverseLookupTable(String message) {
            if (UnsetFollowLocalCodeReverseMap.isEmpty()) {
                UnsetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                UnsetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            UnsetFollowLocalCode code = null;
            for (Map.Entry<String, UnsetFollowLocalCode> entry : UnsetFollowLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<UnsetDeviceLocalCode, String> UnsetDeviceLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, UnsetDeviceLocalCode> UnsetDeviceLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getUnsetDeviceAPI() {
            StringBuilder url = new StringBuilder(testurl + "/unset/device/");
            return url.toString().replace("/&", "/?");
        }

        public enum UnsetDeviceLocalCode {
        }

        public static String UnsetDeviceLocalCodeMessageTable(UnsetDeviceLocalCode code) {
            if (UnsetDeviceLocalCodeMap.isEmpty()) {
            }
            String message = null;
            for (Map.Entry<UnsetDeviceLocalCode, String> entry : UnsetDeviceLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static UnsetDeviceLocalCode UnsetDeviceLocalCodeReverseLookupTable(String message) {
            if (UnsetDeviceLocalCodeReverseMap.isEmpty()) {
            }
            UnsetDeviceLocalCode code = null;
            for (Map.Entry<String, UnsetDeviceLocalCode> entry : UnsetDeviceLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<UnsetWantLocalCode, String> UnsetWantLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, UnsetWantLocalCode> UnsetWantLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getUnsetWantAPI(String rest_id) {
            StringBuilder url = new StringBuilder(testurl + "/unset/want/");
            url.append("&rest_id=").append(rest_id);
            return url.toString().replace("/&", "/?");
        }

        public enum UnsetWantLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
        }

        public static String UnsetWantLocalCodeMessageTable(UnsetWantLocalCode code) {
            if (UnsetWantLocalCodeMap.isEmpty()) {
                UnsetWantLocalCodeMap.put(UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                UnsetWantLocalCodeMap.put(UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<UnsetWantLocalCode, String> entry : UnsetWantLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static UnsetWantLocalCode UnsetWantLocalCodeReverseLookupTable(String message) {
            if (UnsetWantLocalCodeReverseMap.isEmpty()) {
                UnsetWantLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
                UnsetWantLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
            }
            UnsetWantLocalCode code = null;
            for (Map.Entry<String, UnsetWantLocalCode> entry : UnsetWantLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<UnsetPostLocalCode, String> UnsetPostLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, UnsetPostLocalCode> UnsetPostLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getUnsetPostAPI(String post_id) {
            StringBuilder url = new StringBuilder(testurl + "/unset/post/");
            url.append("&post_id=").append(post_id);
            return url.toString().replace("/&", "/?");
        }

        public enum UnsetPostLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static String UnsetPostLocalCodeMessageTable(UnsetPostLocalCode code) {
            if (UnsetPostLocalCodeMap.isEmpty()) {
                UnsetPostLocalCodeMap.put(UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                UnsetPostLocalCodeMap.put(UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<UnsetPostLocalCode, String> entry : UnsetPostLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static UnsetPostLocalCode UnsetPostLocalCodeReverseLookupTable(String message) {
            if (UnsetPostLocalCodeReverseMap.isEmpty()) {
                UnsetPostLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
                UnsetPostLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
            }
            UnsetPostLocalCode code = null;
            for (Map.Entry<String, UnsetPostLocalCode> entry : UnsetPostLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetFollowLocalCode, String> SetFollowLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetFollowLocalCode> SetFollowLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetFollowAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/set/follow/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum SetFollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
        }

        public static String SetFollowLocalCodeMessageTable(SetFollowLocalCode code) {
            if (SetFollowLocalCodeMap.isEmpty()) {
                SetFollowLocalCodeMap.put(SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                SetFollowLocalCodeMap.put(SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<SetFollowLocalCode, String> entry : SetFollowLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetFollowLocalCode SetFollowLocalCodeReverseLookupTable(String message) {
            if (SetFollowLocalCodeReverseMap.isEmpty()) {
                SetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                SetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            SetFollowLocalCode code = null;
            for (Map.Entry<String, SetFollowLocalCode> entry : SetFollowLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetPasswordLocalCode, String> SetPasswordLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetPasswordLocalCode> SetPasswordLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetPasswordAPI(String password) {
            StringBuilder url = new StringBuilder(testurl + "/set/password/");
            url.append("&password=").append(password);
            return url.toString().replace("/&", "/?");
        }

        public enum SetPasswordLocalCode {
            ERROR_PARAMETER_PASSWORD_MISSING,
            ERROR_PARAMETER_PASSWORD_MALFORMED,
        }

        public static String SetPasswordLocalCodeMessageTable(SetPasswordLocalCode code) {
            if (SetPasswordLocalCodeMap.isEmpty()) {
                SetPasswordLocalCodeMap.put(SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED, "Parameter 'password' is malformed. Should correspond to '^[^\\p{Cntrl}]{6,25}$'");
                SetPasswordLocalCodeMap.put(SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING, "Parameter 'password' does not exist.");
            }
            String message = null;
            for (Map.Entry<SetPasswordLocalCode, String> entry : SetPasswordLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetPasswordLocalCode SetPasswordLocalCodeReverseLookupTable(String message) {
            if (SetPasswordLocalCodeReverseMap.isEmpty()) {
                SetPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_PASSWORD_MALFORMED", SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED);
                SetPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_PASSWORD_MISSING", SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING);
            }
            SetPasswordLocalCode code = null;
            for (Map.Entry<String, SetPasswordLocalCode> entry : SetPasswordLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetCommentLocalCode, String> SetCommentLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetCommentLocalCode> SetCommentLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetCommentAPI(String post_id, String comment, String re_user_id) {
            StringBuilder url = new StringBuilder(testurl + "/set/comment/");
            url.append("&post_id=").append(post_id);
            url.append("&comment=").append(comment);
            if (re_user_id != null) url.append("&re_user_id=").append(re_user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum SetCommentLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
            ERROR_PARAMETER_COMMENT_MISSING,
            ERROR_PARAMETER_COMMENT_MALFORMED,
            ERROR_PARAMETER_RE_USER_ID_MALFORMED,
        }

        public static String SetCommentLocalCodeMessageTable(SetCommentLocalCode code) {
            if (SetCommentLocalCodeMap.isEmpty()) {
                SetCommentLocalCodeMap.put(SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MALFORMED, "Parameter 'comment' is malformed. Should correspond to '^(\\n|[^\\p{Cntrl}]){1,140}$'");
                SetCommentLocalCodeMap.put(SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetCommentLocalCodeMap.put(SetCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MALFORMED, "Parameter 're_user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetCommentLocalCodeMap.put(SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MISSING, "Parameter 'comment' does not exist.");
                SetCommentLocalCodeMap.put(SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<SetCommentLocalCode, String> entry : SetCommentLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetCommentLocalCode SetCommentLocalCodeReverseLookupTable(String message) {
            if (SetCommentLocalCodeReverseMap.isEmpty()) {
                SetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_COMMENT_MALFORMED", SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MALFORMED);
                SetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
                SetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_RE_USER_ID_MALFORMED", SetCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MALFORMED);
                SetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_COMMENT_MISSING", SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MISSING);
                SetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
            }
            SetCommentLocalCode code = null;
            for (Map.Entry<String, SetCommentLocalCode> entry : SetCommentLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetGochiLocalCode, String> SetGochiLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetGochiLocalCode> SetGochiLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetGochiAPI(String post_id) {
            StringBuilder url = new StringBuilder(testurl + "/set/gochi/");
            url.append("&post_id=").append(post_id);
            return url.toString().replace("/&", "/?");
        }

        public enum SetGochiLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static String SetGochiLocalCodeMessageTable(SetGochiLocalCode code) {
            if (SetGochiLocalCodeMap.isEmpty()) {
                SetGochiLocalCodeMap.put(SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetGochiLocalCodeMap.put(SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<SetGochiLocalCode, String> entry : SetGochiLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetGochiLocalCode SetGochiLocalCodeReverseLookupTable(String message) {
            if (SetGochiLocalCodeReverseMap.isEmpty()) {
                SetGochiLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
                SetGochiLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
            }
            SetGochiLocalCode code = null;
            for (Map.Entry<String, SetGochiLocalCode> entry : SetGochiLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetFeedbackLocalCode, String> SetFeedbackLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetFeedbackLocalCode> SetFeedbackLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetFeedbackAPI(String feedback) {
            StringBuilder url = new StringBuilder(testurl + "/set/feedback/");
            url.append("&feedback=").append(feedback);
            return url.toString().replace("/&", "/?");
        }

        public enum SetFeedbackLocalCode {
            ERROR_PARAMETER_FEEDBACK_MISSING,
            ERROR_PARAMETER_FEEDBACK_MALFORMED,
        }

        public static String SetFeedbackLocalCodeMessageTable(SetFeedbackLocalCode code) {
            if (SetFeedbackLocalCodeMap.isEmpty()) {
                SetFeedbackLocalCodeMap.put(SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MISSING, "Parameter 'feedback' does not exist.");
                SetFeedbackLocalCodeMap.put(SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MALFORMED, "Parameter 'feedback' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,10000}$'");
            }
            String message = null;
            for (Map.Entry<SetFeedbackLocalCode, String> entry : SetFeedbackLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetFeedbackLocalCode SetFeedbackLocalCodeReverseLookupTable(String message) {
            if (SetFeedbackLocalCodeReverseMap.isEmpty()) {
                SetFeedbackLocalCodeReverseMap.put("ERROR_PARAMETER_FEEDBACK_MISSING", SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MISSING);
                SetFeedbackLocalCodeReverseMap.put("ERROR_PARAMETER_FEEDBACK_MALFORMED", SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MALFORMED);
            }
            SetFeedbackLocalCode code = null;
            for (Map.Entry<String, SetFeedbackLocalCode> entry : SetFeedbackLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetWantLocalCode, String> SetWantLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetWantLocalCode> SetWantLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetWantAPI(String rest_id) {
            StringBuilder url = new StringBuilder(testurl + "/set/want/");
            url.append("&rest_id=").append(rest_id);
            return url.toString().replace("/&", "/?");
        }

        public enum SetWantLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
        }

        public static String SetWantLocalCodeMessageTable(SetWantLocalCode code) {
            if (SetWantLocalCodeMap.isEmpty()) {
                SetWantLocalCodeMap.put(SetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetWantLocalCodeMap.put(SetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<SetWantLocalCode, String> entry : SetWantLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetWantLocalCode SetWantLocalCodeReverseLookupTable(String message) {
            if (SetWantLocalCodeReverseMap.isEmpty()) {
                SetWantLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", SetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
                SetWantLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", SetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
            }
            SetWantLocalCode code = null;
            for (Map.Entry<String, SetWantLocalCode> entry : SetWantLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetRestLocalCode, String> SetRestLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetRestLocalCode> SetRestLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetRestAPI(String restname, String lat, String lon) {
            StringBuilder url = new StringBuilder(testurl + "/set/rest/");
            url.append("&restname=").append(restname);
            url.append("&lat=").append(lat);
            url.append("&lon=").append(lon);
            return url.toString().replace("/&", "/?");
        }

        public enum SetRestLocalCode {
            ERROR_PARAMETER_RESTNAME_MISSING,
            ERROR_PARAMETER_RESTNAME_MALFORMED,
            ERROR_PARAMETER_LAT_MISSING,
            ERROR_PARAMETER_LAT_MALFORMED,
            ERROR_PARAMETER_LON_MISSING,
            ERROR_PARAMETER_LON_MALFORMED,
        }

        public static String SetRestLocalCodeMessageTable(SetRestLocalCode code) {
            if (SetRestLocalCodeMap.isEmpty()) {
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_LAT_MISSING, "Parameter 'lat' does not exist.");
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_LON_MISSING, "Parameter 'lon' does not exist.");
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_LAT_MALFORMED, "Parameter 'lat' is malformed. Should correspond to '^\\d{1,3}.\\d{1,20}$'");
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MISSING, "Parameter 'restname' does not exist.");
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MALFORMED, "Parameter 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                SetRestLocalCodeMap.put(SetRestLocalCode.ERROR_PARAMETER_LON_MALFORMED, "Parameter 'lon' is malformed. Should correspond to '^\\d{1,3}.\\d{1,20}$'");
            }
            String message = null;
            for (Map.Entry<SetRestLocalCode, String> entry : SetRestLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetRestLocalCode SetRestLocalCodeReverseLookupTable(String message) {
            if (SetRestLocalCodeReverseMap.isEmpty()) {
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MISSING", SetRestLocalCode.ERROR_PARAMETER_LAT_MISSING);
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MISSING", SetRestLocalCode.ERROR_PARAMETER_LON_MISSING);
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MALFORMED", SetRestLocalCode.ERROR_PARAMETER_LAT_MALFORMED);
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_RESTNAME_MISSING", SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MISSING);
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_RESTNAME_MALFORMED", SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MALFORMED);
                SetRestLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MALFORMED", SetRestLocalCode.ERROR_PARAMETER_LON_MALFORMED);
            }
            SetRestLocalCode code = null;
            for (Map.Entry<String, SetRestLocalCode> entry : SetRestLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetProfile_ImgLocalCode, String> SetProfile_ImgLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetProfile_ImgLocalCode> SetProfile_ImgLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetProfileImgAPI(String profile_img) {
            StringBuilder url = new StringBuilder(testurl + "/set/profile_img/");
            url.append("&profile_img=").append(profile_img);
            return url.toString().replace("/&", "/?");
        }

        public enum SetProfile_ImgLocalCode {
            ERROR_PARAMETER_PROFILE_IMG_MISSING,
            ERROR_PARAMETER_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_PROFILE_IMG_MALFORMED,
        }

        public static String SetProfile_ImgLocalCodeMessageTable(SetProfile_ImgLocalCode code) {
            if (SetProfile_ImgLocalCodeMap.isEmpty()) {
                SetProfile_ImgLocalCodeMap.put(SetProfile_ImgLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                SetProfile_ImgLocalCodeMap.put(SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED, "Parameter 'profile_img' is malformed. Should correspond to '^[0-9_-]+_img$'");
                SetProfile_ImgLocalCodeMap.put(SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING, "Parameter 'profile_img' does not exist.");
                SetProfile_ImgLocalCodeMap.put(SetProfile_ImgLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
            }
            String message = null;
            for (Map.Entry<SetProfile_ImgLocalCode, String> entry : SetProfile_ImgLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetProfile_ImgLocalCode SetProfile_ImgLocalCodeReverseLookupTable(String message) {
            if (SetProfile_ImgLocalCodeReverseMap.isEmpty()) {
                SetProfile_ImgLocalCodeReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", SetProfile_ImgLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                SetProfile_ImgLocalCodeReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MALFORMED", SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED);
                SetProfile_ImgLocalCodeReverseMap.put("ERROR_PARAMETER_PROFILE_IMG_MISSING", SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING);
                SetProfile_ImgLocalCodeReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", SetProfile_ImgLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
            }
            SetProfile_ImgLocalCode code = null;
            for (Map.Entry<String, SetProfile_ImgLocalCode> entry : SetProfile_ImgLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetPost_BlockLocalCode, String> SetPost_BlockLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetPost_BlockLocalCode> SetPost_BlockLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetPostBlockAPI(String post_id) {
            StringBuilder url = new StringBuilder(testurl + "/set/post_block/");
            url.append("&post_id=").append(post_id);
            return url.toString().replace("/&", "/?");
        }

        public enum SetPost_BlockLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
        }

        public static String SetPost_BlockLocalCodeMessageTable(SetPost_BlockLocalCode code) {
            if (SetPost_BlockLocalCodeMap.isEmpty()) {
                SetPost_BlockLocalCodeMap.put(SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetPost_BlockLocalCodeMap.put(SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<SetPost_BlockLocalCode, String> entry : SetPost_BlockLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetPost_BlockLocalCode SetPost_BlockLocalCodeReverseLookupTable(String message) {
            if (SetPost_BlockLocalCodeReverseMap.isEmpty()) {
                SetPost_BlockLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
                SetPost_BlockLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
            }
            SetPost_BlockLocalCode code = null;
            for (Map.Entry<String, SetPost_BlockLocalCode> entry : SetPost_BlockLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetUsernameLocalCode, String> SetUsernameLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetUsernameLocalCode> SetUsernameLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetUsernameAPI(String username) {
            StringBuilder url = new StringBuilder(testurl + "/set/username/");
            url.append("&username=").append(username);
            return url.toString().replace("/&", "/?");
        }

        public enum SetUsernameLocalCode {
            ERROR_USERNAME_ALREADY_REGISTERD,
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED,
            ERROR_RESPONSE_USERNAME_MISSING,
            ERROR_RESPONSE_USERNAME_MALFORMED,
        }

        public static String SetUsernameLocalCodeMessageTable(SetUsernameLocalCode code) {
            if (SetUsernameLocalCodeMap.isEmpty()) {
                SetUsernameLocalCodeMap.put(SetUsernameLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                SetUsernameLocalCodeMap.put(SetUsernameLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                SetUsernameLocalCodeMap.put(SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                SetUsernameLocalCodeMap.put(SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                SetUsernameLocalCodeMap.put(SetUsernameLocalCode.ERROR_USERNAME_ALREADY_REGISTERD, "The provided username was already registerd by another user");
            }
            String message = null;
            for (Map.Entry<SetUsernameLocalCode, String> entry : SetUsernameLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetUsernameLocalCode SetUsernameLocalCodeReverseLookupTable(String message) {
            if (SetUsernameLocalCodeReverseMap.isEmpty()) {
                SetUsernameLocalCodeReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", SetUsernameLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                SetUsernameLocalCodeReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", SetUsernameLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                SetUsernameLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
                SetUsernameLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                SetUsernameLocalCodeReverseMap.put("ERROR_USERNAME_ALREADY_REGISTERD", SetUsernameLocalCode.ERROR_USERNAME_ALREADY_REGISTERD);
            }
            SetUsernameLocalCode code = null;
            for (Map.Entry<String, SetUsernameLocalCode> entry : SetUsernameLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetSns_LinkLocalCode, String> SetSns_LinkLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetSns_LinkLocalCode> SetSns_LinkLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetSnsLinkAPI(String provider, String sns_token) {
            StringBuilder url = new StringBuilder(testurl + "/set/sns_link/");
            url.append("&provider=").append(provider);
            url.append("&sns_token=").append(sns_token);
            return url.toString().replace("/&", "/?");
        }

        public enum SetSns_LinkLocalCode {
            ERROR_SNS_PROVIDER_TOKEN_NOT_VALID,
            ERROR_PROVIDER_UNREACHABLE,
            ERROR_PARAMETER_PROVIDER_MISSING,
            ERROR_PARAMETER_PROVIDER_MALFORMED,
            ERROR_PARAMETER_SNS_TOKEN_MISSING,
            ERROR_PARAMETER_SNS_TOKEN_MALFORMED,
        }

        public static String SetSns_LinkLocalCodeMessageTable(SetSns_LinkLocalCode code) {
            if (SetSns_LinkLocalCodeMap.isEmpty()) {
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING, "Parameter 'sns_token' does not exist.");
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING, "Parameter 'provider' does not exist.");
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID, "The provided sns token is invalid or has expired");
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED, "Parameter 'sns_token' is malformed. Should correspond to '^[^\\p{Cntrl}]{20,4000}$'");
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_PROVIDER_UNREACHABLE, "The providers server infrastructure appears to be down");
                SetSns_LinkLocalCodeMap.put(SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED, "Parameter 'provider' is malformed. Should correspond to '^(api.twitter.com)|(graph.facebook.com)$'");
            }
            String message = null;
            for (Map.Entry<SetSns_LinkLocalCode, String> entry : SetSns_LinkLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetSns_LinkLocalCode SetSns_LinkLocalCodeReverseLookupTable(String message) {
            if (SetSns_LinkLocalCodeReverseMap.isEmpty()) {
                SetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_SNS_TOKEN_MISSING", SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING);
                SetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_PROVIDER_MISSING", SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING);
                SetSns_LinkLocalCodeReverseMap.put("ERROR_SNS_PROVIDER_TOKEN_NOT_VALID", SetSns_LinkLocalCode.ERROR_SNS_PROVIDER_TOKEN_NOT_VALID);
                SetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_SNS_TOKEN_MALFORMED", SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED);
                SetSns_LinkLocalCodeReverseMap.put("ERROR_PROVIDER_UNREACHABLE", SetSns_LinkLocalCode.ERROR_PROVIDER_UNREACHABLE);
                SetSns_LinkLocalCodeReverseMap.put("ERROR_PARAMETER_PROVIDER_MALFORMED", SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED);
            }
            SetSns_LinkLocalCode code = null;
            for (Map.Entry<String, SetSns_LinkLocalCode> entry : SetSns_LinkLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetDeviceLocalCode, String> SetDeviceLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetDeviceLocalCode> SetDeviceLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetDeviceAPI(String device_token, String os, String ver, String model) {
            StringBuilder url = new StringBuilder(testurl + "/set/device/");
            url.append("&device_token=").append(device_token);
            url.append("&os=").append(os);
            url.append("&ver=").append(ver);
            url.append("&model=").append(model);
            return url.toString().replace("/&", "/?");
        }

        public enum SetDeviceLocalCode {
            ERROR_PARAMETER_DEVICE_TOKEN_MISSING,
            ERROR_PARAMETER_DEVICE_TOKEN_MALFORMED,
            ERROR_PARAMETER_OS_MISSING,
            ERROR_PARAMETER_OS_MALFORMED,
            ERROR_PARAMETER_VER_MISSING,
            ERROR_PARAMETER_VER_MALFORMED,
            ERROR_PARAMETER_MODEL_MISSING,
            ERROR_PARAMETER_MODEL_MALFORMED,
        }

        public static String SetDeviceLocalCodeMessageTable(SetDeviceLocalCode code) {
            if (SetDeviceLocalCodeMap.isEmpty()) {
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_VER_MALFORMED, "Parameter 'ver' is malformed. Should correspond to '^[0-9.]{1,6}$'");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_VER_MISSING, "Parameter 'ver' does not exist.");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MISSING, "Parameter 'model' does not exist.");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MALFORMED, "Parameter 'model' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,50}$'");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MISSING, "Parameter 'device_token' does not exist.");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_OS_MALFORMED, "Parameter 'os' is malformed. Should correspond to '^android$|^iOS$'");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_OS_MISSING, "Parameter 'os' does not exist.");
                SetDeviceLocalCodeMap.put(SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MALFORMED, "Parameter 'device_token' is malformed. Should correspond to '^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$'");
            }
            String message = null;
            for (Map.Entry<SetDeviceLocalCode, String> entry : SetDeviceLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetDeviceLocalCode SetDeviceLocalCodeReverseLookupTable(String message) {
            if (SetDeviceLocalCodeReverseMap.isEmpty()) {
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_VER_MALFORMED", SetDeviceLocalCode.ERROR_PARAMETER_VER_MALFORMED);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_VER_MISSING", SetDeviceLocalCode.ERROR_PARAMETER_VER_MISSING);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_MODEL_MISSING", SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MISSING);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_MODEL_MALFORMED", SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MALFORMED);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_DEVICE_TOKEN_MISSING", SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MISSING);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_OS_MALFORMED", SetDeviceLocalCode.ERROR_PARAMETER_OS_MALFORMED);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_OS_MISSING", SetDeviceLocalCode.ERROR_PARAMETER_OS_MISSING);
                SetDeviceLocalCodeReverseMap.put("ERROR_PARAMETER_DEVICE_TOKEN_MALFORMED", SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MALFORMED);
            }
            SetDeviceLocalCode code = null;
            for (Map.Entry<String, SetDeviceLocalCode> entry : SetDeviceLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<SetPostLocalCode, String> SetPostLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, SetPostLocalCode> SetPostLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getSetPostAPI(String rest_id, String movie_name, String category_id, String value, String memo, String cheer_flag) {
            StringBuilder url = new StringBuilder(testurl + "/set/post/");
            url.append("&rest_id=").append(rest_id);
            url.append("&movie_name=").append(movie_name);
            if (category_id != null) url.append("&category_id=").append(category_id);
            if (value != null) url.append("&value=").append(value);
            if (memo != null) url.append("&memo=").append(memo);
            if (cheer_flag != null) url.append("&cheer_flag=").append(cheer_flag);
            return url.toString().replace("/&", "/?");
        }

        public enum SetPostLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
            ERROR_PARAMETER_MOVIE_NAME_MISSING,
            ERROR_PARAMETER_MOVIE_NAME_MALFORMED,
            ERROR_PARAMETER_CATEGORY_ID_MALFORMED,
            ERROR_PARAMETER_VALUE_MALFORMED,
            ERROR_PARAMETER_MEMO_MALFORMED,
            ERROR_PARAMETER_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POST_ID_MISSING,
            ERROR_RESPONSE_POST_ID_MALFORMED,
        }

        public static String SetPostLocalCodeMessageTable(SetPostLocalCode code) {
            if (SetPostLocalCodeMap.isEmpty()) {
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MISSING, "Parameter 'movie_name' does not exist.");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_CHEER_FLAG_MALFORMED, "Parameter 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_VALUE_MALFORMED, "Parameter 'value' is malformed. Should correspond to '^\\d{0,8}$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED, "Parameter 'category_id' is malformed. Should correspond to '^\\d$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_RESPONSE_POST_ID_MISSING, "Response 'post_id' was not received");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_MEMO_MALFORMED, "Parameter 'memo' is malformed. Should correspond to '^\\S{1,140}$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MALFORMED, "Parameter 'movie_name' is malformed. Should correspond to '^\\d{4}(-\\d{2}){5}_\\d{1,9}$'");
                SetPostLocalCodeMap.put(SetPostLocalCode.ERROR_RESPONSE_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<SetPostLocalCode, String> entry : SetPostLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static SetPostLocalCode SetPostLocalCodeReverseLookupTable(String message) {
            if (SetPostLocalCodeReverseMap.isEmpty()) {
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_MOVIE_NAME_MISSING", SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MISSING);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", SetPostLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_CHEER_FLAG_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_CHEER_FLAG_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_VALUE_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_VALUE_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_CATEGORY_ID_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_RESPONSE_POST_ID_MISSING", SetPostLocalCode.ERROR_RESPONSE_POST_ID_MISSING);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_MEMO_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_MEMO_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_PARAMETER_MOVIE_NAME_MALFORMED", SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MALFORMED);
                SetPostLocalCodeReverseMap.put("ERROR_RESPONSE_POST_ID_MALFORMED", SetPostLocalCode.ERROR_RESPONSE_POST_ID_MALFORMED);
            }
            SetPostLocalCode code = null;
            for (Map.Entry<String, SetPostLocalCode> entry : SetPostLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<AuthPasswordLocalCode, String> AuthPasswordLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, AuthPasswordLocalCode> AuthPasswordLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getAuthPasswordAPI(String username, String password) {
            StringBuilder url = new StringBuilder(testurl + "/auth/password/");
            url.append("&username=").append(username);
            url.append("&password=").append(password);
            return url.toString().replace("/&", "/?");
        }

        public enum AuthPasswordLocalCode {
            ERROR_USERNAME_NOT_REGISTERD,
            ERROR_PASSWORD_NOT_REGISTERD,
            ERROR_PASSWORD_WRONG,
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED,
            ERROR_PARAMETER_PASSWORD_MISSING,
            ERROR_PARAMETER_PASSWORD_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
        }

        public static String AuthPasswordLocalCodeMessageTable(AuthPasswordLocalCode code) {
            if (AuthPasswordLocalCodeMap.isEmpty()) {
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED, "Parameter 'password' is malformed. Should correspond to '^[^\\p{Cntrl}]{6,25}$'");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_USERNAME_NOT_REGISTERD, "The entered username does not exist");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING, "Parameter 'password' does not exist.");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PASSWORD_WRONG, "Password wrong");
                AuthPasswordLocalCodeMap.put(AuthPasswordLocalCode.ERROR_PASSWORD_NOT_REGISTERD, "The entered password does not exist");
            }
            String message = null;
            for (Map.Entry<AuthPasswordLocalCode, String> entry : AuthPasswordLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static AuthPasswordLocalCode AuthPasswordLocalCodeReverseLookupTable(String message) {
            if (AuthPasswordLocalCodeReverseMap.isEmpty()) {
                AuthPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_PASSWORD_MALFORMED", AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED);
                AuthPasswordLocalCodeReverseMap.put("ERROR_USERNAME_NOT_REGISTERD", AuthPasswordLocalCode.ERROR_USERNAME_NOT_REGISTERD);
                AuthPasswordLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthPasswordLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                AuthPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                AuthPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_PASSWORD_MISSING", AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING);
                AuthPasswordLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthPasswordLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                AuthPasswordLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
                AuthPasswordLocalCodeReverseMap.put("ERROR_PASSWORD_WRONG", AuthPasswordLocalCode.ERROR_PASSWORD_WRONG);
                AuthPasswordLocalCodeReverseMap.put("ERROR_PASSWORD_NOT_REGISTERD", AuthPasswordLocalCode.ERROR_PASSWORD_NOT_REGISTERD);
            }
            AuthPasswordLocalCode code = null;
            for (Map.Entry<String, AuthPasswordLocalCode> entry : AuthPasswordLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<AuthSignupLocalCode, String> AuthSignupLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, AuthSignupLocalCode> AuthSignupLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getAuthSignupAPI(String username) {
            StringBuilder url = new StringBuilder(testurl + "/auth/signup/");
            url.append("&username=").append(username);
            return url.toString().replace("/&", "/?");
        }

        public enum AuthSignupLocalCode {
            ERROR_USERNAME_ALREADY_REGISTERD,
            ERROR_PARAMETER_USERNAME_MISSING,
            ERROR_PARAMETER_USERNAME_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
        }

        public static String AuthSignupLocalCodeMessageTable(AuthSignupLocalCode code) {
            if (AuthSignupLocalCodeMap.isEmpty()) {
                AuthSignupLocalCodeMap.put(AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                AuthSignupLocalCodeMap.put(AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED, "Parameter 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                AuthSignupLocalCodeMap.put(AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                AuthSignupLocalCodeMap.put(AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING, "Parameter 'username' does not exist.");
                AuthSignupLocalCodeMap.put(AuthSignupLocalCode.ERROR_USERNAME_ALREADY_REGISTERD, "The provided username was already registerd by another user");
            }
            String message = null;
            for (Map.Entry<AuthSignupLocalCode, String> entry : AuthSignupLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static AuthSignupLocalCode AuthSignupLocalCodeReverseLookupTable(String message) {
            if (AuthSignupLocalCodeReverseMap.isEmpty()) {
                AuthSignupLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                AuthSignupLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MALFORMED", AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED);
                AuthSignupLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthSignupLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                AuthSignupLocalCodeReverseMap.put("ERROR_PARAMETER_USERNAME_MISSING", AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING);
                AuthSignupLocalCodeReverseMap.put("ERROR_USERNAME_ALREADY_REGISTERD", AuthSignupLocalCode.ERROR_USERNAME_ALREADY_REGISTERD);
            }
            AuthSignupLocalCode code = null;
            for (Map.Entry<String, AuthSignupLocalCode> entry : AuthSignupLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<AuthLoginLocalCode, String> AuthLoginLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, AuthLoginLocalCode> AuthLoginLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getAuthLoginAPI(String identity_id) {
            StringBuilder url = new StringBuilder(testurl + "/auth/login/");
            url.append("&identity_id=").append(identity_id);
            return url.toString().replace("/&", "/?");
        }

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
            ERROR_RESPONSE_BADGE_NUM_MISSING,
            ERROR_RESPONSE_BADGE_NUM_MALFORMED,
            ERROR_RESPONSE_IDENTITY_ID_MISSING,
            ERROR_RESPONSE_IDENTITY_ID_MALFORMED,
            ERROR_RESPONSE_COGNITO_TOKEN_MISSING,
            ERROR_RESPONSE_COGNITO_TOKEN_MALFORMED,
        }

        public static String AuthLoginLocalCodeMessageTable(AuthLoginLocalCode code) {
            if (AuthLoginLocalCodeMap.isEmpty()) {
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED, "Response 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED, "Parameter 'identity_id' is malformed. Should correspond to '^us-east-1:[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}$'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_COGNITO_TOKEN_MISSING, "Response 'cognito_token' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING, "Response 'username' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING, "Response 'badge_num' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED, "Response 'badge_num' is malformed. It is no a valid 'INTEGER'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING, "Response 'identity_id' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_COGNITO_TOKEN_MALFORMED, "Response 'cognito_token' is malformed. Should correspond to '^[a-zA-Z0-9_.-]{400,2200}$'");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING, "Response 'user_id' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD, "The provided identity_id is not bound to any account");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                AuthLoginLocalCodeMap.put(AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING, "Parameter 'identity_id' does not exist.");
            }
            String message = null;
            for (Map.Entry<AuthLoginLocalCode, String> entry : AuthLoginLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static AuthLoginLocalCode AuthLoginLocalCodeReverseLookupTable(String message) {
            if (AuthLoginLocalCodeReverseMap.isEmpty()) {
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_USER_ID_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MALFORMED", AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_COGNITO_TOKEN_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_COGNITO_TOKEN_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_USERNAME_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_USERNAME_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_USERNAME_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_BADGE_NUM_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_BADGE_NUM_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_IDENTITY_ID_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_IDENTITY_ID_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_COGNITO_TOKEN_MALFORMED", AuthLoginLocalCode.ERROR_RESPONSE_COGNITO_TOKEN_MALFORMED);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_USER_ID_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_USER_ID_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_IDENTITY_ID_NOT_REGISTERD", AuthLoginLocalCode.ERROR_IDENTITY_ID_NOT_REGISTERD);
                AuthLoginLocalCodeReverseMap.put("ERROR_RESPONSE_PROFILE_IMG_MISSING", AuthLoginLocalCode.ERROR_RESPONSE_PROFILE_IMG_MISSING);
                AuthLoginLocalCodeReverseMap.put("ERROR_PARAMETER_IDENTITY_ID_MISSING", AuthLoginLocalCode.ERROR_PARAMETER_IDENTITY_ID_MISSING);
            }
            AuthLoginLocalCode code = null;
            for (Map.Entry<String, AuthLoginLocalCode> entry : AuthLoginLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetFollowlineLocalCode, String> GetFollowlineLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetFollowlineLocalCode> GetFollowlineLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetFollowlineAPI(String page, String category_id, String value_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/followline/");
            if (page != null) url.append("&page=").append(page);
            if (category_id != null) url.append("&category_id=").append(category_id);
            if (value_id != null) url.append("&value_id=").append(value_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetFollowlineLocalCode {
            ERROR_PARAMETER_PAGE_MALFORMED,
            ERROR_PARAMETER_CATEGORY_ID_MALFORMED,
            ERROR_PARAMETER_VALUE_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_DATE_MISSING,
            ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_REST_ID_MISSING,
            ERROR_RESPONSE_POSTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_RESTNAME_MISSING,
            ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED,
            ERROR_RESPONSE_POSTS_USER_ID_MISSING,
            ERROR_RESPONSE_POSTS_USER_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_USERNAME_MISSING,
            ERROR_RESPONSE_POSTS_USERNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_VALUE_MISSING,
            ERROR_RESPONSE_POSTS_VALUE_MALFORMED,
        }

        public static String GetFollowlineLocalCodeMessageTable(GetFollowlineLocalCode code) {
            if (GetFollowlineLocalCodeMap.isEmpty()) {
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED, "Response 'hls_movie' is malformed. Should correspond to '^http\\S+$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED, "Parameter 'page' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING, "Response 'post_id' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING, "Response 'cheer_flag' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED, "Response 'thumbnail' is malformed. Should correspond to '^http\\S+$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING, "Response 'thumbnail' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED, "Response 'movie' is malformed. Should correspond to '^[0-9_-]+$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED, "Response 'gochi_flag' is malformed. Should correspond to '^0$|^1$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING, "Response 'movie' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING, "Response 'gochi_flag' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED, "Response 'value' is malformed. It is no a valid 'INTEGER'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED, "Parameter 'value_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING, "Response 'hls_movie' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED, "Response 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING, "Response 'mp4_movie' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED, "Response 'mp4_movie' is malformed. Should correspond to '^http\\S$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED, "Parameter 'category_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING, "Response 'username' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING, "Response 'value' was not received");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowlineLocalCodeMap.put(GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MISSING, "Response 'posts' was not received");
            }
            String message = null;
            for (Map.Entry<GetFollowlineLocalCode, String> entry : GetFollowlineLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetFollowlineLocalCode GetFollowlineLocalCodeReverseLookupTable(String message) {
            if (GetFollowlineLocalCodeReverseMap.isEmpty()) {
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_PARAMETER_PAGE_MALFORMED", GetFollowlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_PARAMETER_VALUE_ID_MALFORMED", GetFollowlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_PARAMETER_CATEGORY_ID_MALFORMED", GetFollowlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MALFORMED", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED);
                GetFollowlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MISSING", GetFollowlineLocalCode.ERROR_RESPONSE_POSTS_MISSING);
            }
            GetFollowlineLocalCode code = null;
            for (Map.Entry<String, GetFollowlineLocalCode> entry : GetFollowlineLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetFollowLocalCode, String> GetFollowLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetFollowLocalCode> GetFollowLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetFollowAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/follow/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetFollowLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERS_MISSING,
            ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING,
            ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED,
            ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_USERS_USER_ID_MISSING,
            ERROR_RESPONSE_USERS_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERS_USERNAME_MISSING,
            ERROR_RESPONSE_USERS_USERNAME_MALFORMED,
        }

        public static String GetFollowLocalCodeMessageTable(GetFollowLocalCode code) {
            if (GetFollowLocalCodeMap.isEmpty()) {
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_USERNAME_MISSING, "Response 'username' was not received");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_MISSING, "Response 'users' was not received");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED, "Response 'follow_flag' is malformed. Should correspond to '^0$|^1$'");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING, "Response 'follow_flag' was not received");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_RESPONSE_USERS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetFollowLocalCodeMap.put(GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<GetFollowLocalCode, String> entry : GetFollowLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetFollowLocalCode GetFollowLocalCodeReverseLookupTable(String message) {
            if (GetFollowLocalCodeReverseMap.isEmpty()) {
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USERNAME_MISSING", GetFollowLocalCode.ERROR_RESPONSE_USERS_USERNAME_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_MISSING", GetFollowLocalCode.ERROR_RESPONSE_USERS_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USERNAME_MALFORMED", GetFollowLocalCode.ERROR_RESPONSE_USERS_USERNAME_MALFORMED);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED", GetFollowLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING", GetFollowLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING", GetFollowLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED", GetFollowLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USER_ID_MALFORMED", GetFollowLocalCode.ERROR_RESPONSE_USERS_USER_ID_MALFORMED);
                GetFollowLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USER_ID_MISSING", GetFollowLocalCode.ERROR_RESPONSE_USERS_USER_ID_MISSING);
                GetFollowLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetFollowLocalCode code = null;
            for (Map.Entry<String, GetFollowLocalCode> entry : GetFollowLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetNearlineLocalCode, String> GetNearlineLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetNearlineLocalCode> GetNearlineLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetNearlineAPI(String lat, String lon, String page, String category_id, String value_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/nearline/");
            url.append("&lat=").append(lat);
            url.append("&lon=").append(lon);
            if (page != null) url.append("&page=").append(page);
            if (category_id != null) url.append("&category_id=").append(category_id);
            if (value_id != null) url.append("&value_id=").append(value_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetNearlineLocalCode {
            ERROR_PARAMETER_LAT_MISSING,
            ERROR_PARAMETER_LAT_MALFORMED,
            ERROR_PARAMETER_LON_MISSING,
            ERROR_PARAMETER_LON_MALFORMED,
            ERROR_PARAMETER_PAGE_MALFORMED,
            ERROR_PARAMETER_CATEGORY_ID_MALFORMED,
            ERROR_PARAMETER_VALUE_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_DISTANCE_MISSING,
            ERROR_RESPONSE_POSTS_DISTANCE_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_DATE_MISSING,
            ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_REST_ID_MISSING,
            ERROR_RESPONSE_POSTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_RESTNAME_MISSING,
            ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED,
            ERROR_RESPONSE_POSTS_USER_ID_MISSING,
            ERROR_RESPONSE_POSTS_USER_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_USERNAME_MISSING,
            ERROR_RESPONSE_POSTS_USERNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_VALUE_MISSING,
            ERROR_RESPONSE_POSTS_VALUE_MALFORMED,
        }

        public static String GetNearlineLocalCodeMessageTable(GetNearlineLocalCode code) {
            if (GetNearlineLocalCodeMap.isEmpty()) {
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_DISTANCE_MISSING, "Response 'distance' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED, "Response 'hls_movie' is malformed. Should correspond to '^http\\S+$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_LAT_MISSING, "Parameter 'lat' does not exist.");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED, "Parameter 'page' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING, "Response 'post_id' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING, "Response 'cheer_flag' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED, "Response 'thumbnail' is malformed. Should correspond to '^http\\S+$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED, "Response 'mp4_movie' is malformed. Should correspond to '^http\\S$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED, "Parameter 'value_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED, "Response 'movie' is malformed. Should correspond to '^[0-9_-]+$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING, "Response 'thumbnail' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING, "Response 'movie' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING, "Response 'gochi_flag' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED, "Response 'value' is malformed. It is no a valid 'INTEGER'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_LON_MISSING, "Parameter 'lon' does not exist.");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING, "Response 'hls_movie' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED, "Response 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING, "Response 'mp4_movie' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_DISTANCE_MALFORMED, "Response 'distance' is malformed. It is no a valid 'INTEGER'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_LAT_MALFORMED, "Parameter 'lat' is malformed. Should correspond to '^\\d{1,3}\\.\\d{1,20}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED, "Parameter 'category_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING, "Response 'username' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING, "Response 'value' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED, "Response 'gochi_flag' is malformed. Should correspond to '^0$|^1$'");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MISSING, "Response 'posts' was not received");
                GetNearlineLocalCodeMap.put(GetNearlineLocalCode.ERROR_PARAMETER_LON_MALFORMED, "Parameter 'lon' is malformed. Should correspond to '^\\d{1,3}\\.\\d{1,20}$'");
            }
            String message = null;
            for (Map.Entry<GetNearlineLocalCode, String> entry : GetNearlineLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetNearlineLocalCode GetNearlineLocalCodeReverseLookupTable(String message) {
            if (GetNearlineLocalCodeReverseMap.isEmpty()) {
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_DISTANCE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_DISTANCE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MISSING", GetNearlineLocalCode.ERROR_PARAMETER_LAT_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_PAGE_MALFORMED", GetNearlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_VALUE_ID_MALFORMED", GetNearlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MISSING", GetNearlineLocalCode.ERROR_PARAMETER_LON_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_DISTANCE_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_DISTANCE_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MALFORMED", GetNearlineLocalCode.ERROR_PARAMETER_LAT_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_CATEGORY_ID_MALFORMED", GetNearlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED);
                GetNearlineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MISSING", GetNearlineLocalCode.ERROR_RESPONSE_POSTS_MISSING);
                GetNearlineLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MALFORMED", GetNearlineLocalCode.ERROR_PARAMETER_LON_MALFORMED);
            }
            GetNearlineLocalCode code = null;
            for (Map.Entry<String, GetNearlineLocalCode> entry : GetNearlineLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetCommentLocalCode, String> GetCommentLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetCommentLocalCode> GetCommentLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetCommentAPI(String post_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/comment/");
            url.append("&post_id=").append(post_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetCommentLocalCode {
            ERROR_PARAMETER_POST_ID_MISSING,
            ERROR_PARAMETER_POST_ID_MALFORMED,
            ERROR_RESPONSE_MEMO_MISSING,
            ERROR_RESPONSE_MEMO_MEMO_MISSING,
            ERROR_RESPONSE_MEMO_MEMO_MALFORMED,
            ERROR_RESPONSE_MEMO_POST_DATE_MISSING,
            ERROR_RESPONSE_MEMO_POST_DATE_MALFORMED,
            ERROR_RESPONSE_MEMO_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_MEMO_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_MEMO_USER_ID_MISSING,
            ERROR_RESPONSE_MEMO_USER_ID_MALFORMED,
            ERROR_RESPONSE_MEMO_USERNAME_MISSING,
            ERROR_RESPONSE_MEMO_USERNAME_MALFORMED,
            ERROR_RESPONSE_COMMENTS_MISSING,
            ERROR_RESPONSE_COMMENTS_COMMENT_MISSING,
            ERROR_RESPONSE_COMMENTS_COMMENT_MALFORMED,
            ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MISSING,
            ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MALFORMED,
            ERROR_RESPONSE_COMMENTS_COMMENT_ID_MISSING,
            ERROR_RESPONSE_COMMENTS_COMMENT_ID_MALFORMED,
            ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MISSING,
            ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MALFORMED,
            ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_COMMENTS_RE_USERS_MISSING,
            ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MISSING,
            ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MALFORMED,
            ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MISSING,
            ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MALFORMED,
            ERROR_RESPONSE_COMMENTS_USERNAME_MISSING,
            ERROR_RESPONSE_COMMENTS_USERNAME_MALFORMED,
        }

        public static String GetCommentLocalCodeMessageTable(GetCommentLocalCode code) {
            if (GetCommentLocalCodeMap.isEmpty()) {
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_USERNAME_MISSING, "Response 'username' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING, "Parameter 'post_id' does not exist.");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MISSING, "Response 'comment_user_id' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_MALFORMED, "Response 'comment' is malformed. Should correspond to '^(\\n|[^\\p{Cntrl}]){1,140}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_ID_MISSING, "Response 'comment_id' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED, "Parameter 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MISSING, "Response 'comment_date' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_MISSING, "Response 'comment' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MALFORMED, "Response 'comment_user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_MISSING, "Response 're_users' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_USER_ID_MISSING, "Response 'user_id' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_MEMO_MALFORMED, "Response 'memo' is malformed. Should correspond to '^\\S{1,140}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_ID_MALFORMED, "Response 'comment_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MISSING, "Response 'username' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_USERNAME_MISSING, "Response 'username' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_MISSING, "Response 'comments' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_MISSING, "Response 'memo' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_MEMO_MISSING, "Response 'memo' was not received");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MALFORMED, "Response 'comment_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetCommentLocalCodeMap.put(GetCommentLocalCode.ERROR_RESPONSE_MEMO_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
            }
            String message = null;
            for (Map.Entry<GetCommentLocalCode, String> entry : GetCommentLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetCommentLocalCode GetCommentLocalCodeReverseLookupTable(String message) {
            if (GetCommentLocalCodeReverseMap.isEmpty()) {
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_USERNAME_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_USERNAME_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MISSING", GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_PROFILE_IMG_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_MEMO_PROFILE_IMG_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_USERNAME_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_MEMO_USERNAME_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_ID_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_ID_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_PARAMETER_POST_ID_MALFORMED", GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_USER_ID_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_MEMO_USER_ID_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_POST_DATE_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_POST_DATE_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_USER_ID_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_PROFILE_IMG_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_PROFILE_IMG_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_RE_USERS_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_USER_ID_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_USER_ID_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_USERNAME_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_USERNAME_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_MEMO_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_MEMO_MEMO_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_ID_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_ID_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_PROFILE_IMG_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USER_ID_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_USERNAME_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_USERNAME_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_MISSING", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_RE_USERS_USERNAME_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_MEMO_MISSING", GetCommentLocalCode.ERROR_RESPONSE_MEMO_MEMO_MISSING);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_COMMENTS_COMMENT_DATE_MALFORMED);
                GetCommentLocalCodeReverseMap.put("ERROR_RESPONSE_MEMO_POST_DATE_MALFORMED", GetCommentLocalCode.ERROR_RESPONSE_MEMO_POST_DATE_MALFORMED);
            }
            GetCommentLocalCode code = null;
            for (Map.Entry<String, GetCommentLocalCode> entry : GetCommentLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetHeatmapLocalCode, String> GetHeatmapLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetHeatmapLocalCode> GetHeatmapLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetHeatmapAPI() {
            StringBuilder url = new StringBuilder(testurl + "/get/heatmap/");
            return url.toString().replace("/&", "/?");
        }

        public enum GetHeatmapLocalCode {
            ERROR_RESPONSE_RESTS_MISSING,
            ERROR_RESPONSE_RESTS_POST_REST_ID_MISSING,
            ERROR_RESPONSE_RESTS_POST_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_RESTNAME_MISSING,
            ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED,
            ERROR_RESPONSE_RESTS_LAT_MISSING,
            ERROR_RESPONSE_RESTS_LAT_MALFORMED,
            ERROR_RESPONSE_RESTS_LON_MISSING,
            ERROR_RESPONSE_RESTS_LON_MALFORMED,
        }

        public static String GetHeatmapLocalCodeMessageTable(GetHeatmapLocalCode code) {
            if (GetHeatmapLocalCodeMap.isEmpty()) {
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LON_MALFORMED, "Response 'lon' is malformed. It is no a valid 'FLOAT'");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_POST_REST_ID_MISSING, "Response 'post_rest_id' was not received");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LAT_MISSING, "Response 'lat' was not received");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LON_MISSING, "Response 'lon' was not received");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_POST_REST_ID_MALFORMED, "Response 'post_rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LAT_MALFORMED, "Response 'lat' is malformed. It is no a valid 'FLOAT'");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_MISSING, "Response 'rests' was not received");
                GetHeatmapLocalCodeMap.put(GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
            }
            String message = null;
            for (Map.Entry<GetHeatmapLocalCode, String> entry : GetHeatmapLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetHeatmapLocalCode GetHeatmapLocalCodeReverseLookupTable(String message) {
            if (GetHeatmapLocalCodeReverseMap.isEmpty()) {
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LON_MALFORMED", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LON_MALFORMED);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_POST_REST_ID_MISSING", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_POST_REST_ID_MISSING);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MISSING", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LAT_MISSING", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LAT_MISSING);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LON_MISSING", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LON_MISSING);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_POST_REST_ID_MALFORMED", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_POST_REST_ID_MALFORMED);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LAT_MALFORMED", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_LAT_MALFORMED);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_MISSING", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_MISSING);
                GetHeatmapLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED", GetHeatmapLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED);
            }
            GetHeatmapLocalCode code = null;
            for (Map.Entry<String, GetHeatmapLocalCode> entry : GetHeatmapLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetRest_CheerLocalCode, String> GetRest_CheerLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetRest_CheerLocalCode> GetRest_CheerLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetRestCheerAPI(String rest_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/rest_cheer/");
            url.append("&rest_id=").append(rest_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetRest_CheerLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED,
            ERROR_RESPONSE_RESTS_REST_ID_MISSING,
            ERROR_RESPONSE_RESTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_RESTNAME_MISSING,
            ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED,
        }

        public static String GetRest_CheerLocalCodeMessageTable(GetRest_CheerLocalCode code) {
            if (GetRest_CheerLocalCodeMap.isEmpty()) {
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING, "Response 'locality' was not received");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_MISSING, "Response 'rests' was not received");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetRest_CheerLocalCodeMap.put(GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED, "Response 'locality' is malformed. Should correspond to '^\\S[1,100]$'");
            }
            String message = null;
            for (Map.Entry<GetRest_CheerLocalCode, String> entry : GetRest_CheerLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetRest_CheerLocalCode GetRest_CheerLocalCodeReverseLookupTable(String message) {
            if (GetRest_CheerLocalCodeReverseMap.isEmpty()) {
                GetRest_CheerLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MISSING", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MISSING", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MALFORMED", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_MISSING", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_MISSING);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MISSING", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING);
                GetRest_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED", GetRest_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED);
            }
            GetRest_CheerLocalCode code = null;
            for (Map.Entry<String, GetRest_CheerLocalCode> entry : GetRest_CheerLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetUser_CheerLocalCode, String> GetUser_CheerLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetUser_CheerLocalCode> GetUser_CheerLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetUserCheerAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/user_cheer/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetUser_CheerLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED,
            ERROR_RESPONSE_RESTS_REST_ID_MISSING,
            ERROR_RESPONSE_RESTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_RESTNAME_MISSING,
            ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED,
        }

        public static String GetUser_CheerLocalCodeMessageTable(GetUser_CheerLocalCode code) {
            if (GetUser_CheerLocalCodeMap.isEmpty()) {
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING, "Response 'locality' was not received");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED, "Response 'locality' is malformed. Should correspond to '^\\S[1,100]$'");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetUser_CheerLocalCodeMap.put(GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_MISSING, "Response 'rests' was not received");
            }
            String message = null;
            for (Map.Entry<GetUser_CheerLocalCode, String> entry : GetUser_CheerLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetUser_CheerLocalCode GetUser_CheerLocalCodeReverseLookupTable(String message) {
            if (GetUser_CheerLocalCodeReverseMap.isEmpty()) {
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MISSING", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MALFORMED", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MISSING", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MISSING", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING);
                GetUser_CheerLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_MISSING", GetUser_CheerLocalCode.ERROR_RESPONSE_RESTS_MISSING);
            }
            GetUser_CheerLocalCode code = null;
            for (Map.Entry<String, GetUser_CheerLocalCode> entry : GetUser_CheerLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetWantLocalCode, String> GetWantLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetWantLocalCode> GetWantLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetWantAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/want/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetWantLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MISSING,
            ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED,
            ERROR_RESPONSE_RESTS_REST_ID_MISSING,
            ERROR_RESPONSE_RESTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_RESTNAME_MISSING,
            ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED,
        }

        public static String GetWantLocalCodeMessageTable(GetWantLocalCode code) {
            if (GetWantLocalCodeMap.isEmpty()) {
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING, "Response 'locality' was not received");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED, "Response 'locality' is malformed. Should correspond to '^\\S[1,100]$'");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetWantLocalCodeMap.put(GetWantLocalCode.ERROR_RESPONSE_RESTS_MISSING, "Response 'rests' was not received");
            }
            String message = null;
            for (Map.Entry<GetWantLocalCode, String> entry : GetWantLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetWantLocalCode GetWantLocalCodeReverseLookupTable(String message) {
            if (GetWantLocalCodeReverseMap.isEmpty()) {
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MISSING", GetWantLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING);
                GetWantLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MALFORMED", GetWantLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MISSING", GetWantLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MISSING);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED", GetWantLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED);
                GetWantLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED", GetWantLocalCode.ERROR_RESPONSE_RESTS_LOCALITY_MALFORMED);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MISSING", GetWantLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING);
                GetWantLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_MISSING", GetWantLocalCode.ERROR_RESPONSE_RESTS_MISSING);
            }
            GetWantLocalCode code = null;
            for (Map.Entry<String, GetWantLocalCode> entry : GetWantLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetNoticeLocalCode, String> GetNoticeLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetNoticeLocalCode> GetNoticeLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetNoticeAPI() {
            StringBuilder url = new StringBuilder(testurl + "/get/notice/");
            return url.toString().replace("/&", "/?");
        }

        public enum GetNoticeLocalCode {
            ERROR_RESPONSE_NOTICES_MISSING,
            ERROR_RESPONSE_NOTICES_NOTICE_MISSING,
            ERROR_RESPONSE_NOTICES_NOTICE_MALFORMED,
            ERROR_RESPONSE_NOTICES_NOTICE_DATE_MISSING,
            ERROR_RESPONSE_NOTICES_NOTICE_DATE_MALFORMED,
            ERROR_RESPONSE_NOTICES_NOTICE_ID_MISSING,
            ERROR_RESPONSE_NOTICES_NOTICE_ID_MALFORMED,
            ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MISSING,
            ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MALFORMED,
            ERROR_RESPONSE_NOTICES_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_NOTICES_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_NOTICES_USER_ID_MISSING,
            ERROR_RESPONSE_NOTICES_USER_ID_MALFORMED,
            ERROR_RESPONSE_NOTICES_USERNAME_MISSING,
            ERROR_RESPONSE_NOTICES_USERNAME_MALFORMED,
        }

        public static String GetNoticeLocalCodeMessageTable(GetNoticeLocalCode code) {
            if (GetNoticeLocalCodeMap.isEmpty()) {
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_MALFORMED, "Response 'notice' is malformed. Should correspond to '^gochi$|^follow$|^comment$|^announce$'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_DATE_MALFORMED, "Response 'notice_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USER_ID_MISSING, "Response 'user_id' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MISSING, "Response 'notice_post_id' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_ID_MALFORMED, "Response 'notice_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_ID_MISSING, "Response 'notice_id' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USERNAME_MISSING, "Response 'username' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_DATE_MISSING, "Response 'notice_date' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_MISSING, "Response 'notice' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_MISSING, "Response 'notices' was not received");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetNoticeLocalCodeMap.put(GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MALFORMED, "Response 'notice_post_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<GetNoticeLocalCode, String> entry : GetNoticeLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetNoticeLocalCode GetNoticeLocalCodeReverseLookupTable(String message) {
            if (GetNoticeLocalCodeReverseMap.isEmpty()) {
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_PROFILE_IMG_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_PROFILE_IMG_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_DATE_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_DATE_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_USER_ID_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USER_ID_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_PROFILE_IMG_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_PROFILE_IMG_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_ID_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_ID_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_ID_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_ID_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_USERNAME_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USERNAME_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_DATE_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_DATE_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_USER_ID_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USER_ID_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_MISSING", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_MISSING);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_USERNAME_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_USERNAME_MALFORMED);
                GetNoticeLocalCodeReverseMap.put("ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MALFORMED", GetNoticeLocalCode.ERROR_RESPONSE_NOTICES_NOTICE_POST_ID_MALFORMED);
            }
            GetNoticeLocalCode code = null;
            for (Map.Entry<String, GetNoticeLocalCode> entry : GetNoticeLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetNearLocalCode, String> GetNearLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetNearLocalCode> GetNearLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetNearAPI(String lat, String lon) {
            StringBuilder url = new StringBuilder(testurl + "/get/near/");
            url.append("&lat=").append(lat);
            url.append("&lon=").append(lon);
            return url.toString().replace("/&", "/?");
        }

        public enum GetNearLocalCode {
            ERROR_PARAMETER_LAT_MISSING,
            ERROR_PARAMETER_LAT_MALFORMED,
            ERROR_PARAMETER_LON_MISSING,
            ERROR_PARAMETER_LON_MALFORMED,
            ERROR_RESPONSE_RESTS_MISSING,
            ERROR_RESPONSE_RESTS_REST_ID_MISSING,
            ERROR_RESPONSE_RESTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_RESTS_RESTNAME_MISSING,
            ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED,
        }

        public static String GetNearLocalCodeMessageTable(GetNearLocalCode code) {
            if (GetNearLocalCodeMap.isEmpty()) {
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_PARAMETER_LAT_MISSING, "Parameter 'lat' does not exist.");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_PARAMETER_LON_MISSING, "Parameter 'lon' does not exist.");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_PARAMETER_LAT_MALFORMED, "Parameter 'lat' is malformed. Should correspond to '^\\d{1,3}.\\d{1,20}$'");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_RESPONSE_RESTS_MISSING, "Response 'rests' was not received");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetNearLocalCodeMap.put(GetNearLocalCode.ERROR_PARAMETER_LON_MALFORMED, "Parameter 'lon' is malformed. Should correspond to '^\\d{1,3}.\\d{1,20}$'");
            }
            String message = null;
            for (Map.Entry<GetNearLocalCode, String> entry : GetNearLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetNearLocalCode GetNearLocalCodeReverseLookupTable(String message) {
            if (GetNearLocalCodeReverseMap.isEmpty()) {
                GetNearLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MISSING", GetNearLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MISSING);
                GetNearLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MISSING", GetNearLocalCode.ERROR_PARAMETER_LAT_MISSING);
                GetNearLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MISSING", GetNearLocalCode.ERROR_PARAMETER_LON_MISSING);
                GetNearLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED", GetNearLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MALFORMED);
                GetNearLocalCodeReverseMap.put("ERROR_PARAMETER_LAT_MALFORMED", GetNearLocalCode.ERROR_PARAMETER_LAT_MALFORMED);
                GetNearLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_REST_ID_MALFORMED", GetNearLocalCode.ERROR_RESPONSE_RESTS_REST_ID_MALFORMED);
                GetNearLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_MISSING", GetNearLocalCode.ERROR_RESPONSE_RESTS_MISSING);
                GetNearLocalCodeReverseMap.put("ERROR_RESPONSE_RESTS_RESTNAME_MISSING", GetNearLocalCode.ERROR_RESPONSE_RESTS_RESTNAME_MISSING);
                GetNearLocalCodeReverseMap.put("ERROR_PARAMETER_LON_MALFORMED", GetNearLocalCode.ERROR_PARAMETER_LON_MALFORMED);
            }
            GetNearLocalCode code = null;
            for (Map.Entry<String, GetNearLocalCode> entry : GetNearLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetRestLocalCode, String> GetRestLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetRestLocalCode> GetRestLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetRestAPI(String rest_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/rest/");
            url.append("&rest_id=").append(rest_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetRestLocalCode {
            ERROR_PARAMETER_REST_ID_MISSING,
            ERROR_PARAMETER_REST_ID_MALFORMED,
            ERROR_RESPONSE_REST_MISSING,
            ERROR_RESPONSE_REST_HOMEPAGE_MISSING,
            ERROR_RESPONSE_REST_HOMEPAGE_MALFORMED,
            ERROR_RESPONSE_REST_LAT_MISSING,
            ERROR_RESPONSE_REST_LAT_MALFORMED,
            ERROR_RESPONSE_REST_LOCALITY_MISSING,
            ERROR_RESPONSE_REST_LOCALITY_MALFORMED,
            ERROR_RESPONSE_REST_LON_MISSING,
            ERROR_RESPONSE_REST_LON_MALFORMED,
            ERROR_RESPONSE_REST_REST_CATEGORY_MISSING,
            ERROR_RESPONSE_REST_REST_CATEGORY_MALFORMED,
            ERROR_RESPONSE_REST_REST_ID_MISSING,
            ERROR_RESPONSE_REST_REST_ID_MALFORMED,
            ERROR_RESPONSE_REST_RESTNAME_MISSING,
            ERROR_RESPONSE_REST_RESTNAME_MALFORMED,
            ERROR_RESPONSE_REST_TELL_MISSING,
            ERROR_RESPONSE_REST_TELL_MALFORMED,
            ERROR_RESPONSE_REST_WANT_FLAG_MISSING,
            ERROR_RESPONSE_REST_WANT_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_MISSING,
            ERROR_RESPONSE_POSTS_CATEGORY_MISSING,
            ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING,
            ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MEMO_MISSING,
            ERROR_RESPONSE_POSTS_MEMO_MALFORMED,
            ERROR_RESPONSE_POSTS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_DATE_MISSING,
            ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_REST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_REST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_POSTS_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED,
            ERROR_RESPONSE_POSTS_USER_ID_MISSING,
            ERROR_RESPONSE_POSTS_USER_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_USERNAME_MISSING,
            ERROR_RESPONSE_POSTS_USERNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_VALUE_MISSING,
            ERROR_RESPONSE_POSTS_VALUE_MALFORMED,
        }

        public static String GetRestLocalCodeMessageTable(GetRestLocalCode code) {
            if (GetRestLocalCodeMap.isEmpty()) {
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_REST_ID_MALFORMED, "Response 'post_rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MEMO_MISSING, "Response 'memo' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_TELL_MALFORMED, "Response 'tell' is malformed. Should correspond to '^[0-9-]{9,20}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING, "Response 'cheer_flag' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_HOMEPAGE_MALFORMED, "Response 'homepage' is malformed. Should correspond to '^http\\S+$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_TELL_MISSING, "Response 'tell' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MEMO_MALFORMED, "Response 'memo' is malformed. Should correspond to '^\\S{1,140}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_RESTNAME_MISSING, "Response 'restname' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MISSING, "Response 'category' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING, "Response 'comment_num' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LOCALITY_MALFORMED, "Response 'locality' is malformed. Should correspond to '^\\S[1,100]$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LOCALITY_MISSING, "Response 'locality' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING, "Response 'username' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED, "Response 'hls_movie' is malformed. Should correspond to '^http\\S+$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED, "Response 'value' is malformed. It is no a valid 'INTEGER'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_REST_ID_MISSING, "Response 'post_rest_id' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED, "Response 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING, "Response 'mp4_movie' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED, "Response 'mp4_movie' is malformed. Should correspond to '^http\\S$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LAT_MISSING, "Response 'lat' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED, "Response 'category' is malformed. Should correspond to '^\\S[1,20]$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LON_MALFORMED, "Response 'lon' is malformed. It is no a valid 'FLOAT'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_REST_CATEGORY_MALFORMED, "Response 'rest_category' is malformed. Should correspond to '^\\d$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MISSING, "Response 'posts' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LAT_MALFORMED, "Response 'lat' is malformed. It is no a valid 'FLOAT'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING, "Parameter 'rest_id' does not exist.");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING, "Response 'thumbnail' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING, "Response 'post_id' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED, "Response 'thumbnail' is malformed. Should correspond to '^http\\S+$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_HOMEPAGE_MISSING, "Response 'homepage' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED, "Response 'gochi_num' is malformed. It is no a valid 'INTEGER'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED, "Response 'comment_num' is malformed. It is no a valid 'INTEGER'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING, "Response 'movie' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_WANT_FLAG_MISSING, "Response 'want_flag' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING, "Response 'gochi_flag' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED, "Parameter 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING, "Response 'hls_movie' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED, "Response 'gochi_flag' is malformed. Should correspond to '^0$|^1$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_LON_MISSING, "Response 'lon' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING, "Response 'gochi_num' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_REST_CATEGORY_MISSING, "Response 'rest_category' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING, "Response 'value' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_MISSING, "Response 'rest' was not received");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_REST_WANT_FLAG_MALFORMED, "Response 'want_flag' is malformed. Should correspond to '^0$|^1$'");
                GetRestLocalCodeMap.put(GetRestLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED, "Response 'movie' is malformed. Should correspond to '^[0-9_-]+$'");
            }
            String message = null;
            for (Map.Entry<GetRestLocalCode, String> entry : GetRestLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetRestLocalCode GetRestLocalCodeReverseLookupTable(String message) {
            if (GetRestLocalCodeReverseMap.isEmpty()) {
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_REST_ID_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_REST_ID_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_REST_ID_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_REST_ID_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MEMO_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_MEMO_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_TELL_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_TELL_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_HOMEPAGE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_HOMEPAGE_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_TELL_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_TELL_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MEMO_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_MEMO_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_RESTNAME_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_RESTNAME_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CATEGORY_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LOCALITY_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_LOCALITY_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LOCALITY_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_LOCALITY_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_REST_ID_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_REST_ID_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LAT_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_LAT_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LON_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_LON_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_REST_CATEGORY_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_REST_CATEGORY_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LAT_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_LAT_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MISSING", GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_HOMEPAGE_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_HOMEPAGE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_PROFILE_IMG_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_PROFILE_IMG_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_WANT_FLAG_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_WANT_FLAG_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_PARAMETER_REST_ID_MALFORMED", GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_PROFILE_IMG_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_PROFILE_IMG_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_LON_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_LON_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_RESTNAME_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_RESTNAME_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_REST_ID_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_REST_ID_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_REST_CATEGORY_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_REST_CATEGORY_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MISSING", GetRestLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_MISSING", GetRestLocalCode.ERROR_RESPONSE_REST_MISSING);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_REST_WANT_FLAG_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_REST_WANT_FLAG_MALFORMED);
                GetRestLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MALFORMED", GetRestLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED);
            }
            GetRestLocalCode code = null;
            for (Map.Entry<String, GetRestLocalCode> entry : GetRestLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetTimelineLocalCode, String> GetTimelineLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetTimelineLocalCode> GetTimelineLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetTimelineAPI(String page, String category_id, String value_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/timeline/");
            if (page != null) url.append("&page=").append(page);
            if (category_id != null) url.append("&category_id=").append(category_id);
            if (value_id != null) url.append("&value_id=").append(value_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetTimelineLocalCode {
            ERROR_PARAMETER_PAGE_MALFORMED,
            ERROR_PARAMETER_CATEGORY_ID_MALFORMED,
            ERROR_PARAMETER_VALUE_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_DATE_MISSING,
            ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_REST_ID_MISSING,
            ERROR_RESPONSE_POSTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_RESTNAME_MISSING,
            ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED,
            ERROR_RESPONSE_POSTS_USER_ID_MISSING,
            ERROR_RESPONSE_POSTS_USER_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_USERNAME_MISSING,
            ERROR_RESPONSE_POSTS_USERNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_VALUE_MISSING,
            ERROR_RESPONSE_POSTS_VALUE_MALFORMED,
        }

        public static String GetTimelineLocalCodeMessageTable(GetTimelineLocalCode code) {
            if (GetTimelineLocalCodeMap.isEmpty()) {
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED, "Response 'hls_movie' is malformed. Should correspond to '^http\\S+$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED, "Parameter 'page' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING, "Response 'post_id' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING, "Response 'cheer_flag' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED, "Response 'thumbnail' is malformed. Should correspond to '^http\\S+$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING, "Response 'thumbnail' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED, "Response 'movie' is malformed. Should correspond to '^[0-9_-]+$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED, "Response 'gochi_flag' is malformed. Should correspond to '^0$|^1$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING, "Response 'movie' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING, "Response 'gochi_flag' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED, "Response 'value' is malformed. It is no a valid 'INTEGER'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED, "Parameter 'value_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING, "Response 'hls_movie' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED, "Response 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING, "Response 'mp4_movie' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED, "Response 'mp4_movie' is malformed. Should correspond to '^http\\S$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED, "Parameter 'category_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING, "Response 'username' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING, "Response 'value' was not received");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetTimelineLocalCodeMap.put(GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MISSING, "Response 'posts' was not received");
            }
            String message = null;
            for (Map.Entry<GetTimelineLocalCode, String> entry : GetTimelineLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetTimelineLocalCode GetTimelineLocalCodeReverseLookupTable(String message) {
            if (GetTimelineLocalCodeReverseMap.isEmpty()) {
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_PARAMETER_PAGE_MALFORMED", GetTimelineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_PARAMETER_VALUE_ID_MALFORMED", GetTimelineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USER_ID_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USER_ID_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_PARAMETER_CATEGORY_ID_MALFORMED", GetTimelineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_USERNAME_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_USERNAME_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MALFORMED", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED);
                GetTimelineLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MISSING", GetTimelineLocalCode.ERROR_RESPONSE_POSTS_MISSING);
            }
            GetTimelineLocalCode code = null;
            for (Map.Entry<String, GetTimelineLocalCode> entry : GetTimelineLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetFollowerLocalCode, String> GetFollowerLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetFollowerLocalCode> GetFollowerLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetFollowerAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/follower/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetFollowerLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERS_MISSING,
            ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING,
            ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED,
            ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_USERS_USER_ID_MISSING,
            ERROR_RESPONSE_USERS_USER_ID_MALFORMED,
            ERROR_RESPONSE_USERS_USERNAME_MISSING,
            ERROR_RESPONSE_USERS_USERNAME_MALFORMED,
        }

        public static String GetFollowerLocalCodeMessageTable(GetFollowerLocalCode code) {
            if (GetFollowerLocalCodeMap.isEmpty()) {
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_USERNAME_MISSING, "Response 'username' was not received");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_MISSING, "Response 'users' was not received");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED, "Response 'follow_flag' is malformed. Should correspond to '^0$|^1$'");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING, "Response 'follow_flag' was not received");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_RESPONSE_USERS_USER_ID_MISSING, "Response 'user_id' was not received");
                GetFollowerLocalCodeMap.put(GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
            }
            String message = null;
            for (Map.Entry<GetFollowerLocalCode, String> entry : GetFollowerLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetFollowerLocalCode GetFollowerLocalCodeReverseLookupTable(String message) {
            if (GetFollowerLocalCodeReverseMap.isEmpty()) {
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USERNAME_MISSING", GetFollowerLocalCode.ERROR_RESPONSE_USERS_USERNAME_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_MISSING", GetFollowerLocalCode.ERROR_RESPONSE_USERS_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USERNAME_MALFORMED", GetFollowerLocalCode.ERROR_RESPONSE_USERS_USERNAME_MALFORMED);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED", GetFollowerLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MALFORMED);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING", GetFollowerLocalCode.ERROR_RESPONSE_USERS_FOLLOW_FLAG_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING", GetFollowerLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED", GetFollowerLocalCode.ERROR_RESPONSE_USERS_PROFILE_IMG_MALFORMED);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USER_ID_MALFORMED", GetFollowerLocalCode.ERROR_RESPONSE_USERS_USER_ID_MALFORMED);
                GetFollowerLocalCodeReverseMap.put("ERROR_RESPONSE_USERS_USER_ID_MISSING", GetFollowerLocalCode.ERROR_RESPONSE_USERS_USER_ID_MISSING);
                GetFollowerLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
            }
            GetFollowerLocalCode code = null;
            for (Map.Entry<String, GetFollowerLocalCode> entry : GetFollowerLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
        }

        private static final ConcurrentHashMap<GetUserLocalCode, String> GetUserLocalCodeMap = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<String, GetUserLocalCode> GetUserLocalCodeReverseMap = new ConcurrentHashMap<>();

        public static String getGetUserAPI(String user_id) {
            StringBuilder url = new StringBuilder(testurl + "/get/user/");
            url.append("&user_id=").append(user_id);
            return url.toString().replace("/&", "/?");
        }

        public enum GetUserLocalCode {
            ERROR_PARAMETER_USER_ID_MISSING,
            ERROR_PARAMETER_USER_ID_MALFORMED,
            ERROR_RESPONSE_USER_MISSING,
            ERROR_RESPONSE_USER_CHEER_NUM_MISSING,
            ERROR_RESPONSE_USER_CHEER_NUM_MALFORMED,
            ERROR_RESPONSE_USER_FOLLOW_FLAG_MISSING,
            ERROR_RESPONSE_USER_FOLLOW_FLAG_MALFORMED,
            ERROR_RESPONSE_USER_FOLLOW_NUM_MISSING,
            ERROR_RESPONSE_USER_FOLLOW_NUM_MALFORMED,
            ERROR_RESPONSE_USER_FOLLOWER_NUM_MISSING,
            ERROR_RESPONSE_USER_FOLLOWER_NUM_MALFORMED,
            ERROR_RESPONSE_USER_PROFILE_IMG_MISSING,
            ERROR_RESPONSE_USER_PROFILE_IMG_MALFORMED,
            ERROR_RESPONSE_USER_USER_ID_MISSING,
            ERROR_RESPONSE_USER_USER_ID_MALFORMED,
            ERROR_RESPONSE_USER_USERNAME_MISSING,
            ERROR_RESPONSE_USER_USERNAME_MALFORMED,
            ERROR_RESPONSE_USER_WANT_NUM_MISSING,
            ERROR_RESPONSE_USER_WANT_NUM_MALFORMED,
            ERROR_RESPONSE_POSTS_MISSING,
            ERROR_RESPONSE_POSTS_CATEGORY_MISSING,
            ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING,
            ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED,
            ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING,
            ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_LAT_MISSING,
            ERROR_RESPONSE_POSTS_LAT_MALFORMED,
            ERROR_RESPONSE_POSTS_LON_MISSING,
            ERROR_RESPONSE_POSTS_LON_MALFORMED,
            ERROR_RESPONSE_POSTS_MEMO_MISSING,
            ERROR_RESPONSE_POSTS_MEMO_MALFORMED,
            ERROR_RESPONSE_POSTS_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING,
            ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_DATE_MISSING,
            ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED,
            ERROR_RESPONSE_POSTS_POST_ID_MISSING,
            ERROR_RESPONSE_POSTS_POST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_REST_ID_MISSING,
            ERROR_RESPONSE_POSTS_REST_ID_MALFORMED,
            ERROR_RESPONSE_POSTS_RESTNAME_MISSING,
            ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING,
            ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED,
            ERROR_RESPONSE_POSTS_VALUE_MISSING,
            ERROR_RESPONSE_POSTS_VALUE_MALFORMED,
        }

        public static String GetUserLocalCodeMessageTable(GetUserLocalCode code) {
            if (GetUserLocalCodeMap.isEmpty()) {
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MEMO_MISSING, "Response 'memo' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_USERNAME_MALFORMED, "Response 'username' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,20}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING, "Response 'cheer_flag' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_PROFILE_IMG_MISSING, "Response 'profile_img' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_FLAG_MALFORMED, "Response 'follow_flag' is malformed. Should correspond to '^0$|^1$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MEMO_MALFORMED, "Response 'memo' is malformed. Should correspond to '^\\S{1,140}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_USERNAME_MISSING, "Response 'username' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_LON_MALFORMED, "Response 'lon' is malformed. It is no a valid 'FLOAT'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MISSING, "Response 'category' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED, "Response 'movie' is malformed. Should correspond to '^[0-9_-]+$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_CHEER_NUM_MISSING, "Response 'cheer_num' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED, "Response 'hls_movie' is malformed. Should correspond to '^http\\S+$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_LON_MISSING, "Response 'lon' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOWER_NUM_MALFORMED, "Response 'follower_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED, "Response 'restname' is malformed. Should correspond to '^[^\\p{Cntrl}]{1,80}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_USER_ID_MISSING, "Response 'user_id' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED, "Response 'cheer_flag' is malformed. Should correspond to '^0$|^1$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING, "Response 'mp4_movie' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED, "Response 'mp4_movie' is malformed. Should correspond to '^http\\S$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_FLAG_MISSING, "Response 'follow_flag' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING, "Response 'gochi_flag' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOWER_NUM_MISSING, "Response 'follower_num' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_NUM_MISSING, "Response 'follow_num' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING, "Response 'post_date' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MISSING, "Response 'posts' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING, "Parameter 'user_id' does not exist.");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING, "Response 'thumbnail' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_LAT_MISSING, "Response 'lat' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING, "Response 'post_id' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED, "Response 'post_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_PROFILE_IMG_MALFORMED, "Response 'profile_img' is malformed. Should correspond to '^http\\S{1,2000}'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED, "Response 'value' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_WANT_NUM_MISSING, "Response 'want_num' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED, "Response 'post_date' is malformed. Should correspond to '^\\S{1,20}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED, "Response 'gochi_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED, "Response 'comment_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_MISSING, "Response 'user' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_CHEER_NUM_MALFORMED, "Response 'cheer_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING, "Response 'movie' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING, "Response 'restname' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED, "Response 'category' is malformed. Should correspond to '^\\S[1,20]$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_USER_ID_MALFORMED, "Response 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING, "Response 'hls_movie' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING, "Response 'rest_id' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING, "Response 'value' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED, "Response 'gochi_flag' is malformed. Should correspond to '^0$|^1$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_LAT_MALFORMED, "Response 'lat' is malformed. It is no a valid 'FLOAT'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING, "Response 'gochi_num' was not received");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_NUM_MALFORMED, "Response 'follow_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_USER_WANT_NUM_MALFORMED, "Response 'want_num' is malformed. It is no a valid 'INTEGER'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED, "Parameter 'user_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED, "Response 'thumbnail' is malformed. Should correspond to '^http\\S+$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED, "Response 'rest_id' is malformed. Should correspond to '^\\d{1,9}$'");
                GetUserLocalCodeMap.put(GetUserLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING, "Response 'comment_num' was not received");
            }
            String message = null;
            for (Map.Entry<GetUserLocalCode, String> entry : GetUserLocalCodeMap.entrySet()) {
                if (entry.getKey().equals(code)) {
                    message = entry.getValue();
                    break;
                }
            }
            return message;
        }

        public static GetUserLocalCode GetUserLocalCodeReverseLookupTable(String message) {
            if (GetUserLocalCodeReverseMap.isEmpty()) {
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MEMO_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_MEMO_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_USERNAME_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_USERNAME_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_PROFILE_IMG_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_PROFILE_IMG_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOW_FLAG_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_FLAG_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MEMO_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_MEMO_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_USERNAME_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_USERNAME_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_LON_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_LON_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CATEGORY_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_CHEER_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_CHEER_NUM_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_LON_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_LON_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOWER_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOWER_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_USER_ID_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_USER_ID_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_CHEER_FLAG_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_MP4_MOVIE_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOW_FLAG_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_FLAG_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOWER_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOWER_NUM_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOW_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_NUM_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MISSING", GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_LAT_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_LAT_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_ID_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_ID_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_PROFILE_IMG_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_PROFILE_IMG_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_VALUE_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_WANT_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_WANT_NUM_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_POST_DATE_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_MISSING", GetUserLocalCode.ERROR_RESPONSE_USER_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_CHEER_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_CHEER_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_MOVIE_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_MOVIE_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_RESTNAME_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_RESTNAME_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_CATEGORY_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_USER_ID_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_USER_ID_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_HLS_MOVIE_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_VALUE_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_VALUE_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_FLAG_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_LAT_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_LAT_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_GOCHI_NUM_MISSING);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_FOLLOW_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_FOLLOW_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_USER_WANT_NUM_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_USER_WANT_NUM_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_PARAMETER_USER_ID_MALFORMED", GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_THUMBNAIL_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_REST_ID_MALFORMED", GetUserLocalCode.ERROR_RESPONSE_POSTS_REST_ID_MALFORMED);
                GetUserLocalCodeReverseMap.put("ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING", GetUserLocalCode.ERROR_RESPONSE_POSTS_COMMENT_NUM_MISSING);
            }
            GetUserLocalCode code = null;
            for (Map.Entry<String, GetUserLocalCode> entry : GetUserLocalCodeReverseMap.entrySet()) {
                if (entry.getKey().equals(message)) {
                    code = entry.getValue();
                    break;
                }
            }
            return code;
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
        public Util.UnsetSns_LinkLocalCode UnsetSns_LinkParameterRegex(String provider, String sns_token) {
            if (provider != null) {
                if (!provider.matches("^(api.twitter.com)|(graph.facebook.com)$")) {
                    return Util.UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                }
            } else {
                return Util.UnsetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
            }
            if (sns_token != null) {
                if (!sns_token.matches("^[^\\p{Cntrl}]{20,4000}$")) {
                    return Util.UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED;
                }
            } else {
                return Util.UnsetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public void UnsetSns_LinkResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.UnsetSns_LinkLocalCode localCode = Util.UnsetSns_LinkLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.UnsetSns_LinkLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.UnsetFollowLocalCode UnsetFollowParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.UnsetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void UnsetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.UnsetFollowLocalCode localCode = Util.UnsetFollowLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.UnsetFollowLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.UnsetDeviceLocalCode UnsetDeviceParameterRegex() {
            return null;
        }

        @Override
        public void UnsetDeviceResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.UnsetDeviceLocalCode localCode = Util.UnsetDeviceLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.UnsetDeviceLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.UnsetWantLocalCode UnsetWantParameterRegex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^\\d{1,9}$")) {
                    return Util.UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.UnsetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void UnsetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.UnsetWantLocalCode localCode = Util.UnsetWantLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.UnsetWantLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.UnsetPostLocalCode UnsetPostParameterRegex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^\\d{1,9}$")) {
                    return Util.UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.UnsetPostLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void UnsetPostResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.UnsetPostLocalCode localCode = Util.UnsetPostLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.UnsetPostLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetFollowLocalCode SetFollowParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.SetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void SetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetFollowLocalCode localCode = Util.SetFollowLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetFollowLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetPasswordLocalCode SetPasswordParameterRegex(String password) {
            if (password != null) {
                if (!password.matches("^[^\\p{Cntrl}]{6,25}$")) {
                    return Util.SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED;
                }
            } else {
                return Util.SetPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING;
            }
            return null;
        }

        @Override
        public void SetPasswordResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetPasswordLocalCode localCode = Util.SetPasswordLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetPasswordLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetCommentLocalCode SetCommentParameterRegex(String post_id, String comment, String re_user_id) {
            if (post_id != null) {
                if (!post_id.matches("^\\d{1,9}$")) {
                    return Util.SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.SetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            if (comment != null) {
                if (!comment.matches("^(\\n|[^\\p{Cntrl}]){1,140}$")) {
                    return Util.SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MALFORMED;
                }
            } else {
                return Util.SetCommentLocalCode.ERROR_PARAMETER_COMMENT_MISSING;
            }
            if (re_user_id != null) {
                if (!re_user_id.matches("^\\d{1,9}$")) {
                    return Util.SetCommentLocalCode.ERROR_PARAMETER_RE_USER_ID_MALFORMED;
                }
            }
            return null;
        }

        @Override
        public void SetCommentResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetCommentLocalCode localCode = Util.SetCommentLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetCommentLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetGochiLocalCode SetGochiParameterRegex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^\\d{1,9}$")) {
                    return Util.SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.SetGochiLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void SetGochiResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetGochiLocalCode localCode = Util.SetGochiLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetGochiLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetFeedbackLocalCode SetFeedbackParameterRegex(String feedback) {
            if (feedback != null) {
                if (!feedback.matches("^[^\\p{Cntrl}]{1,10000}$")) {
                    return Util.SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MALFORMED;
                }
            } else {
                return Util.SetFeedbackLocalCode.ERROR_PARAMETER_FEEDBACK_MISSING;
            }
            return null;
        }

        @Override
        public void SetFeedbackResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetFeedbackLocalCode localCode = Util.SetFeedbackLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetFeedbackLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetWantLocalCode SetWantParameterRegex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^\\d{1,9}$")) {
                    return Util.SetWantLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.SetWantLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void SetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetWantLocalCode localCode = Util.SetWantLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetWantLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetRestLocalCode SetRestParameterRegex(String restname, String lat, String lon) {
            if (restname != null) {
                if (!restname.matches("^[^\\p{Cntrl}]{1,80}$")) {
                    return Util.SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MALFORMED;
                }
            } else {
                return Util.SetRestLocalCode.ERROR_PARAMETER_RESTNAME_MISSING;
            }
            if (lat != null) {
                if (!lat.matches("^\\d{1,3}.\\d{1,20}$")) {
                    return Util.SetRestLocalCode.ERROR_PARAMETER_LAT_MALFORMED;
                }
            } else {
                return Util.SetRestLocalCode.ERROR_PARAMETER_LAT_MISSING;
            }
            if (lon != null) {
                if (!lon.matches("^\\d{1,3}.\\d{1,20}$")) {
                    return Util.SetRestLocalCode.ERROR_PARAMETER_LON_MALFORMED;
                }
            } else {
                return Util.SetRestLocalCode.ERROR_PARAMETER_LON_MISSING;
            }
            return null;
        }

        @Override
        public void SetRestResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetRestLocalCode localCode = Util.SetRestLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetRestLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetProfile_ImgLocalCode SetProfile_ImgParameterRegex(String profile_img) {
            if (profile_img != null) {
                if (!profile_img.matches("^[0-9_-]+_img$")) {
                    return Util.SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MALFORMED;
                }
            } else {
                return Util.SetProfile_ImgLocalCode.ERROR_PARAMETER_PROFILE_IMG_MISSING;
            }
            return null;
        }

        @Override
        public void SetProfile_ImgResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetProfile_ImgLocalCode localCode = Util.SetProfile_ImgLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetProfile_ImgLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetPost_BlockLocalCode SetPost_BlockParameterRegex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^\\d{1,9}$")) {
                    return Util.SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.SetPost_BlockLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void SetPost_BlockResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetPost_BlockLocalCode localCode = Util.SetPost_BlockLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetPost_BlockLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetUsernameLocalCode SetUsernameParameterRegex(String username) {
            if (username != null) {
                if (!username.matches("^[^\\p{Cntrl}]{1,20}$")) {
                    return Util.SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.SetUsernameLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            return null;
        }

        @Override
        public void SetUsernameResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetUsernameLocalCode localCode = Util.SetUsernameLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetUsernameLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetSns_LinkLocalCode SetSns_LinkParameterRegex(String provider, String sns_token) {
            if (provider != null) {
                if (!provider.matches("^(api.twitter.com)|(graph.facebook.com)$")) {
                    return Util.SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MALFORMED;
                }
            } else {
                return Util.SetSns_LinkLocalCode.ERROR_PARAMETER_PROVIDER_MISSING;
            }
            if (sns_token != null) {
                if (!sns_token.matches("^[^\\p{Cntrl}]{20,4000}$")) {
                    return Util.SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MALFORMED;
                }
            } else {
                return Util.SetSns_LinkLocalCode.ERROR_PARAMETER_SNS_TOKEN_MISSING;
            }
            return null;
        }

        @Override
        public void SetSns_LinkResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetSns_LinkLocalCode localCode = Util.SetSns_LinkLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetSns_LinkLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetDeviceLocalCode SetDeviceParameterRegex(String device_token, String os, String ver, String model) {
            if (device_token != null) {
                if (!device_token.matches("^([a-f0-9]{64})|([a-zA-Z0-9:_-]{140,250})$")) {
                    return Util.SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MALFORMED;
                }
            } else {
                return Util.SetDeviceLocalCode.ERROR_PARAMETER_DEVICE_TOKEN_MISSING;
            }
            if (os != null) {
                if (!os.matches("^android$|^iOS$")) {
                    return Util.SetDeviceLocalCode.ERROR_PARAMETER_OS_MALFORMED;
                }
            } else {
                return Util.SetDeviceLocalCode.ERROR_PARAMETER_OS_MISSING;
            }
            if (ver != null) {
                if (!ver.matches("^[0-9.]{1,6}$")) {
                    return Util.SetDeviceLocalCode.ERROR_PARAMETER_VER_MALFORMED;
                }
            } else {
                return Util.SetDeviceLocalCode.ERROR_PARAMETER_VER_MISSING;
            }
            if (model != null) {
                if (!model.matches("^[^\\p{Cntrl}]{1,50}$")) {
                    return Util.SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MALFORMED;
                }
            } else {
                return Util.SetDeviceLocalCode.ERROR_PARAMETER_MODEL_MISSING;
            }
            return null;
        }

        @Override
        public void SetDeviceResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetDeviceLocalCode localCode = Util.SetDeviceLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetDeviceLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.SetPostLocalCode SetPostParameterRegex(String rest_id, String movie_name, String category_id, String value, String memo, String cheer_flag) {
            if (rest_id != null) {
                if (!rest_id.matches("^\\d{1,9}$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.SetPostLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            if (movie_name != null) {
                if (!movie_name.matches("^\\d{4}(-\\d{2}){5}_\\d{1,9}$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MALFORMED;
                }
            } else {
                return Util.SetPostLocalCode.ERROR_PARAMETER_MOVIE_NAME_MISSING;
            }
            if (category_id != null) {
                if (!category_id.matches("^\\d$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED;
                }
            }
            if (value != null) {
                if (!value.matches("^\\d{0,8}$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_VALUE_MALFORMED;
                }
            }
            if (memo != null) {
                if (!memo.matches("^\\S{1,140}$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_MEMO_MALFORMED;
                }
            }
            if (cheer_flag != null) {
                if (!cheer_flag.matches("^0$|^1$")) {
                    return Util.SetPostLocalCode.ERROR_PARAMETER_CHEER_FLAG_MALFORMED;
                }
            }
            return null;
        }

        @Override
        public void SetPostResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.SetPostLocalCode localCode = Util.SetPostLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.SetPostLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.AuthPasswordLocalCode AuthPasswordParameterRegex(String username, String password) {
            if (username != null) {
                if (!username.matches("^[^\\p{Cntrl}]{1,20}$")) {
                    return Util.AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthPasswordLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            if (password != null) {
                if (!password.matches("^[^\\p{Cntrl}]{6,25}$")) {
                    return Util.AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MALFORMED;
                }
            } else {
                return Util.AuthPasswordLocalCode.ERROR_PARAMETER_PASSWORD_MISSING;
            }
            return null;
        }

        @Override
        public void AuthPasswordResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthPasswordLocalCode localCode = Util.AuthPasswordLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.AuthPasswordLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.AuthSignupLocalCode AuthSignupParameterRegex(String username) {
            if (username != null) {
                if (!username.matches("^[^\\p{Cntrl}]{1,20}$")) {
                    return Util.AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MALFORMED;
                }
            } else {
                return Util.AuthSignupLocalCode.ERROR_PARAMETER_USERNAME_MISSING;
            }
            return null;
        }

        @Override
        public void AuthSignupResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthSignupLocalCode localCode = Util.AuthSignupLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.AuthSignupLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.AuthLoginLocalCode AuthLoginParameterRegex(String identity_id) {
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
        public void AuthLoginResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.AuthLoginLocalCode localCode = Util.AuthLoginLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.AuthLoginLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetFollowlineLocalCode GetFollowlineParameterRegex(String page, String category_id, String value_id) {
            if (page != null) {
                if (!page.matches("^\\d{1,9}$")) {
                    return Util.GetFollowlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED;
                }
            }
            if (category_id != null) {
                if (!category_id.matches("^\\d{1,9}$")) {
                    return Util.GetFollowlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED;
                }
            }
            if (value_id != null) {
                if (!value_id.matches("^\\d{1,9}$")) {
                    return Util.GetFollowlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED;
                }
            }
            return null;
        }

        @Override
        public void GetFollowlineResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetFollowlineLocalCode localCode = Util.GetFollowlineLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetFollowlineLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetFollowLocalCode GetFollowParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetFollowLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetFollowResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetFollowLocalCode localCode = Util.GetFollowLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetFollowLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetNearlineLocalCode GetNearlineParameterRegex(String lat, String lon, String page, String category_id, String value_id) {
            if (lat != null) {
                if (!lat.matches("^\\d{1,3}\\.\\d{1,20}$")) {
                    return Util.GetNearlineLocalCode.ERROR_PARAMETER_LAT_MALFORMED;
                }
            } else {
                return Util.GetNearlineLocalCode.ERROR_PARAMETER_LAT_MISSING;
            }
            if (lon != null) {
                if (!lon.matches("^\\d{1,3}\\.\\d{1,20}$")) {
                    return Util.GetNearlineLocalCode.ERROR_PARAMETER_LON_MALFORMED;
                }
            } else {
                return Util.GetNearlineLocalCode.ERROR_PARAMETER_LON_MISSING;
            }
            if (page != null) {
                if (!page.matches("^\\d{1,9}$")) {
                    return Util.GetNearlineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED;
                }
            }
            if (category_id != null) {
                if (!category_id.matches("^\\d{1,9}$")) {
                    return Util.GetNearlineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED;
                }
            }
            if (value_id != null) {
                if (!value_id.matches("^\\d{1,9}$")) {
                    return Util.GetNearlineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED;
                }
            }
            return null;
        }

        @Override
        public void GetNearlineResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetNearlineLocalCode localCode = Util.GetNearlineLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetNearlineLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetCommentLocalCode GetCommentParameterRegex(String post_id) {
            if (post_id != null) {
                if (!post_id.matches("^\\d{1,9}$")) {
                    return Util.GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MALFORMED;
                }
            } else {
                return Util.GetCommentLocalCode.ERROR_PARAMETER_POST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetCommentResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetCommentLocalCode localCode = Util.GetCommentLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetCommentLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetHeatmapLocalCode GetHeatmapParameterRegex() {
            return null;
        }

        @Override
        public void GetHeatmapResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetHeatmapLocalCode localCode = Util.GetHeatmapLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetHeatmapLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetRest_CheerLocalCode GetRest_CheerParameterRegex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^\\d{1,9}$")) {
                    return Util.GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.GetRest_CheerLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetRest_CheerResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetRest_CheerLocalCode localCode = Util.GetRest_CheerLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetRest_CheerLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetUser_CheerLocalCode GetUser_CheerParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetUser_CheerLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetUser_CheerResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetUser_CheerLocalCode localCode = Util.GetUser_CheerLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetUser_CheerLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetWantLocalCode GetWantParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.GetWantLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetWantLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetWantResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetWantLocalCode localCode = Util.GetWantLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetWantLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetNoticeLocalCode GetNoticeParameterRegex() {
            return null;
        }

        @Override
        public void GetNoticeResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetNoticeLocalCode localCode = Util.GetNoticeLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetNoticeLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetNearLocalCode GetNearParameterRegex(String lat, String lon) {
            if (lat != null) {
                if (!lat.matches("^\\d{1,3}.\\d{1,20}$")) {
                    return Util.GetNearLocalCode.ERROR_PARAMETER_LAT_MALFORMED;
                }
            } else {
                return Util.GetNearLocalCode.ERROR_PARAMETER_LAT_MISSING;
            }
            if (lon != null) {
                if (!lon.matches("^\\d{1,3}.\\d{1,20}$")) {
                    return Util.GetNearLocalCode.ERROR_PARAMETER_LON_MALFORMED;
                }
            } else {
                return Util.GetNearLocalCode.ERROR_PARAMETER_LON_MISSING;
            }
            return null;
        }

        @Override
        public void GetNearResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetNearLocalCode localCode = Util.GetNearLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetNearLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetRestLocalCode GetRestParameterRegex(String rest_id) {
            if (rest_id != null) {
                if (!rest_id.matches("^\\d{1,9}$")) {
                    return Util.GetRestLocalCode.ERROR_PARAMETER_REST_ID_MALFORMED;
                }
            } else {
                return Util.GetRestLocalCode.ERROR_PARAMETER_REST_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetRestResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetRestLocalCode localCode = Util.GetRestLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetRestLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetTimelineLocalCode GetTimelineParameterRegex(String page, String category_id, String value_id) {
            if (page != null) {
                if (!page.matches("^\\d{1,9}$")) {
                    return Util.GetTimelineLocalCode.ERROR_PARAMETER_PAGE_MALFORMED;
                }
            }
            if (category_id != null) {
                if (!category_id.matches("^\\d{1,9}$")) {
                    return Util.GetTimelineLocalCode.ERROR_PARAMETER_CATEGORY_ID_MALFORMED;
                }
            }
            if (value_id != null) {
                if (!value_id.matches("^\\d{1,9}$")) {
                    return Util.GetTimelineLocalCode.ERROR_PARAMETER_VALUE_ID_MALFORMED;
                }
            }
            return null;
        }

        @Override
        public void GetTimelineResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetTimelineLocalCode localCode = Util.GetTimelineLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetTimelineLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetFollowerLocalCode GetFollowerParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetFollowerLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetFollowerResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetFollowerLocalCode localCode = Util.GetFollowerLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetFollowerLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

        @Override
        public Util.GetUserLocalCode GetUserParameterRegex(String user_id) {
            if (user_id != null) {
                if (!user_id.matches("^\\d{1,9}$")) {
                    return Util.GetUserLocalCode.ERROR_PARAMETER_USER_ID_MALFORMED;
                }
            } else {
                return Util.GetUserLocalCode.ERROR_PARAMETER_USER_ID_MISSING;
            }
            return null;
        }

        @Override
        public void GetUserResponse(JSONObject jsonObject, PayloadResponseCallback cb) {
            try {
                String version = jsonObject.getString("version");
                String uri = jsonObject.getString("uri");
                String code = jsonObject.getString("code");
                String message = jsonObject.getString("message");

                Util.GlobalCode globalCode = Util.GlobalCodeReverseLookupTable(code);
                if (globalCode != null) {
                    if (globalCode == Util.GlobalCode.SUCCESS) {
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        cb.onSuccess(payload);
                    } else {
                        cb.onGlobalError(globalCode);
                    }
                } else {
                    Util.GetUserLocalCode localCode = Util.GetUserLocalCodeReverseLookupTable(code);
                    if (localCode != null) {
                        String errorMessage = Util.GetUserLocalCodeMessageTable(localCode);
                        cb.onLocalError(errorMessage);
                    } else {
                        cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                }
            } catch (JSONException e) {
                cb.onGlobalError(Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        }

    }
}
