package com.inase.android.gocci.datasource.api;

import android.content.Context;
import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static void postGochiAsync(final Context context, final PostData data) {
        API3.Util.PostGochiLocalCode localCode = API3.Impl.getRepository().post_gochi_parameter_regex(data.getPost_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostGochiAPI(data.getPost_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_gochi_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "いいねしました！", Toast.LENGTH_SHORT).show();
                                    //headerData.setGochi_flag(0);
                                    //headerData.setGochi_num(headerData.getGochi_num() - 1);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postGochiLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postGochiAsync(final Context context, final TwoCellData data) {
        API3.Util.PostGochiLocalCode localCode = API3.Impl.getRepository().post_gochi_parameter_regex(data.getPost_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostGochiAPI(data.getPost_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_gochi_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "いいねしました！", Toast.LENGTH_SHORT).show();
                                    //headerData.setGochi_flag(0);
                                    //headerData.setGochi_num(headerData.getGochi_num() - 1);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_GOCHI, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postGochiLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postFollowAsync(final Context context, final HeaderData data) {
        API3.Util.PostFollowLocalCode localCode = API3.Impl.getRepository().post_follow_parameter_regex(data.getUser_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostFollowAPI(data.getUser_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_follow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "フォローしました！", Toast.LENGTH_SHORT).show();
                                    //headerData.setFollow_flag(0);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postFollowLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postFollowAsync(final Context context, final ListGetData data) {
        API3.Util.PostFollowLocalCode localCode = API3.Impl.getRepository().post_follow_parameter_regex(data.getUser_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostFollowAPI(data.getUser_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_follow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "フォローしました！", Toast.LENGTH_SHORT).show();
                                    //headerData.setFollow_flag(0);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_FOLLOW, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postFollowLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUnfollowAsync(final Context context, final HeaderData data) {
        API3.Util.PostUnfollowLocalCode localCode = API3.Impl.getRepository().post_unFollow_parameter_regex(data.getUser_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostUnfollowAPI(data.getUser_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_unFollow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "フォローを解除しました", Toast.LENGTH_SHORT).show();
                                    //headerData.setFollow_flag(0);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postUnfollowLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUnfollowAsync(final Context context, final ListGetData data) {
        API3.Util.PostUnfollowLocalCode localCode = API3.Impl.getRepository().post_unFollow_parameter_regex(data.getUser_id());
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().check_global_error();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getPostUnfollowAPI(data.getUser_id()), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().post_unFollow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "フォローを解除しました", Toast.LENGTH_SHORT).show();
                                    //headerData.setFollow_flag(0);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
                }
            } else {
                Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_UNFOLLOW, globalCode);
            }
        } else {
            Toast.makeText(context, API3.Util.postUnfollowLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
                                    //headerData.setFollow_flag(0);
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
}
