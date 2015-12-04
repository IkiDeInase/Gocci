package com.inase.android.gocci.datasource.api;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/19.
 */
public class API3PostUtil {

    public static void postFeedbackAsync(final Context context, final String feedback) {
        API3.Util.PostFeedbackLocalCode localCode = API3.Impl.getRepository().post_feedback_parameter_regex(feedback);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostFeedbackAPI(feedback), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_feedback_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FEEDBACK, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FEEDBACK, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FEEDBACK, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FEEDBACK, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postFeedbackLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postBlockAsync(final Context context, final String post_id) {
        API3.Util.PostBlockLocalCode localCode = API3.Impl.getRepository().post_block_parameter_regex(post_id);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostBlockAPI(post_id), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_block_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "この投稿を違反報告しました", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_BLOCK, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_BLOCK, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_BLOCK, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_BLOCK, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postBlockLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postDeleteAsync(final Context context, final String post_id, final Const.ActivityCategory activityCategory) {
        API3.Util.PostDeleteLocalCode localCode = API3.Impl.getRepository().post_delete_parameter_regex(post_id);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostDeleteAPI(post_id), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_delete_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_DELETE, post_id));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_DELETE, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_DELETE, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_DELETE, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_DELETE, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postDeleteLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postPasswordAsync(final Context context, final String password) {
        API3.Util.PostPasswordLocalCode localCode = API3.Impl.getRepository().post_password_parameter_regex(password);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostPasswordAPI(password), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_password_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "パスワードを設定しました", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PASSWORD, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PASSWORD, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PASSWORD, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PASSWORD, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postPasswordLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postRestAddAsync(final Context context, final Const.ActivityCategory activityCategory, final String restname, double lon, double lat) {
        API3.Util.PostRestAddLocalCode localCode = API3.Impl.getRepository().post_restadd_parameter_regex(restname, lon, lat);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostRestAddAPI(restname, lon, lat), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_restadd_response(response, new API3.PostRestAddResponseCallback() {
                                @Override
                                public void onSuccess(String rest_id) {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_RESTADD, rest_id));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_RESTADD, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_RESTADD, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_RESTADD, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_RESTADD, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postRestAddLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postSnsLinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        API3.Util.PostSnsLinkLocalCode localCode = API3.Impl.getRepository().post_sns_link_parameter_regex(provider, sns_token);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_sns_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postSnsLinkLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postSnsUnlinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        API3.Util.PostSnsUnlinkLocalCode localCode = API3.Impl.getRepository().post_sns_unlink_parameter_regex(provider, sns_token);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostSnsUnlinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_sns_unlink_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postSnsUnlinkLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postDeviceAsync(final Context context, final String regId, String os, String ver, String model) {
        API3.Util.PostRegisterDeviceTokenLocalCode localCode = API3.Impl.getRepository().post_register_device_token_parameter_regex(regId, os, ver, model);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostRegisterDeviceTokenAPI(regId, os, ver, model), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_register_device_token_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_REGISTER_DEVICE_TOKEN, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_REGISTER_DEVICE_TOKEN, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_REGISTER_DEVICE_TOKEN, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_REGISTER_DEVICE_TOKEN, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postRegisterDeviceTokenLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUnDeviceAsync(final Context context, final String regId) {
        API3.Util.PostUnregisterDeviceTokenLocalCode localCode = API3.Impl.getRepository().post_unregister_device_token_parameter_regex(regId);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostUnregisterDeviceTokenAPI(regId), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_unregister_device_token_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNREGISTER_DEVICE_TOKEN, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNREGISTER_DEVICE_TOKEN, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNREGISTER_DEVICE_TOKEN, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNREGISTER_DEVICE_TOKEN, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postUnregisterDeviceTokenLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUsernameAsync(final Context context, final String username, final Const.ActivityCategory activityCategory) {
        API3.Util.PostUsernameLocalCode localCode = API3.Impl.getRepository().post_username_parameter_regex(username);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostUsernameAPI(username), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_username_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_USERNAME, username));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_USERNAME, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_USERNAME, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_USERNAME, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_USERNAME, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postUsernameLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postProfileImgAsync(final Context context, final String post_date, File file, final Const.ActivityCategory activityCategory) {
        TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    API3.Util.PostProfileImgLocalCode localCode = API3.Impl.getRepository().post_profileImg_parameter_regex();
                    if (localCode == null) {
                        API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
                        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                            try {
                                Application_Gocci.getJsonAsync(API3.Util.getPostProfileImg(post_date), new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        API3.Impl.getRepository().post_profileImg_response(response, new API3.PostResponseCallback() {
                                            @Override
                                            public void onSuccess() {
                                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_PROFILEIMG, post_date));
                                            }

                                            @Override
                                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, globalCode);
                                            }

                                            @Override
                                            public void onLocalError(String errorMessage) {
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                                    }
                                });
                            } catch (SocketTimeoutException e) {
                                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                            }
                        } else {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, globalCode);
                        }
                    } else {
                        Toast.makeText(context, API3.Util.postProfileImgLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void postMovieAsync(final Context context, final Const.ActivityCategory activityCategory, final String rest_id, String movie_name, int category_id, String value, final String memo, int cheer_flag) {
        API3.Util.PostPostLocalCode localCode = API3.Impl.getRepository().post_post_parameter_regex(rest_id, movie_name, category_id, value, memo, cheer_flag);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostMovieAPI(rest_id, movie_name, category_id, value, memo, cheer_flag), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_post_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_POST, memo));
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_POST, globalCode);
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.POST_POST, memo));
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.POST_POST, memo));
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_POST, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                            BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.POST_POST, memo));
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_POST, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.POST_POST, memo));
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_POST, globalCode);
                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.POST_POST, memo));
            }
        } else {
            Toast.makeText(context, API3.Util.postPostLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
            BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.POST_POST, memo));
        }
    }
}
