package com.inase.android.gocci.datasource.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/19.
 */
public class API3PostUtil {

    public static void postFeedbackAsync(final Context context, final String feedback) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetFeedbackLocalCode localCode = API3.Impl.getRepository().SetFeedbackParameterRegex(feedback);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetFeedbackAPI(feedback), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetFeedbackResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_FEEDBACK, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_FEEDBACK, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postBlockAsync(final Context context, final String post_id) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetBlockLocalCode localCode = API3.Impl.getRepository().SetBlockParameterRegex(post_id);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetBlockAPI(post_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetBlockResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "この投稿を違反報告しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_BLOCK, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_BLOCK, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postDeleteAsync(final Context context, final String post_id, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetPostLocalCode localCode = API3.Impl.getRepository().UnsetPostParameterRegex(post_id);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getUnsetPostAPI(post_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().UnsetPostResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.UNSET_POST, post_id));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_POST, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_POST, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postPasswordAsync(final Context context, final String password, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetPasswordLocalCode localCode = API3.Impl.getRepository().SetPasswordParameterRegex(password);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetPasswordAPI(password), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetPasswordResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "パスワードを設定しました", Toast.LENGTH_SHORT).show();
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, password));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PASSWORD, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PASSWORD, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postRestAddAsync(final Context context, final Const.ActivityCategory activityCategory, final String restname, String lon, String lat) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetRestLocalCode localCode = API3.Impl.getRepository().SetRestParameterRegex(restname, lat, lon);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetRestAPI(restname, lat, lon), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetRestResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                try {
                                    String rest_id = payload.getString("rest_id");
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_RESTADD, rest_id));
                                } catch (JSONException e) {
                                    Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_RESTADD, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                }
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_RESTADD, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_RESTADD, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postSnsLinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetSns_LinkLocalCode localCode = API3.Impl.getRepository().SetSns_LinkParameterRegex(provider, sns_token);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postSnsUnlinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetSns_LinkLocalCode localCode = API3.Impl.getRepository().UnsetSns_LinkParameterRegex(provider, sns_token);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getUnsetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().UnsetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postDeviceAsync(final Context context, final String regId, String os, String ver, String model) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetDeviceLocalCode localCode = API3.Impl.getRepository().SetDeviceParameterRegex(regId, os, ver, model);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetDeviceAPI(regId, os, ver, model), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetDeviceResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_DEVICE, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_DEVICE, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postUnDeviceAsync(final Context context, final String regId) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetDeviceLocalCode localCode = API3.Impl.getRepository().UnsetDeviceParameterRegex(regId);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getUnsetDeviceAPI(regId), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().UnsetDeviceResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_DEVICE, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_DEVICE, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postUsernameAsync(final Context context, final String username, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetUsernameLocalCode localCode = API3.Impl.getRepository().SetUsernameParameterRegex(username);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetUsernameAPI(username), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetUsernameResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                try {
                                    String username = payload.getString("username");
                                    SavedData.setServerName(Application_Gocci.getInstance().getApplicationContext(), username);
                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_USERNAME, username));
                                } catch (JSONException e) {
                                    Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_USERNAME, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                }
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_USERNAME, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_USERNAME, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postProfileImgAsync(final Context context, final String post_date, File file, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        API3.Util.SetProfile_ImgLocalCode localCode = API3.Impl.getRepository().SetProfile_ImgParameterRegex(post_date + "png");
                        if (localCode == null) {
                            Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date), new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    API3.Impl.getRepository().SetProfile_ImgResponse(response, new API3.PayloadResponseCallback() {
                                        @Override
                                        public void onSuccess(JSONObject payload) {
                                            try {
                                                String profile_img = payload.getString("profile_img");
                                                SavedData.setServerPicture(Application_Gocci.getInstance().getApplicationContext(), profile_img);
                                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_PROFILEIMG, post_date));
                                            } catch (JSONException e) {
                                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                            }
                                        }

                                        @Override
                                        public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                            Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, globalCode);
                                        }

                                        @Override
                                        public void onLocalError(String errorMessage) {
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                }
                            });
                        } else {
                            Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postProfileImgAsync(final Context context, final String post_date, String url, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            Picasso.with(context).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File file = null;
                    try {
                        file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), post_date + ".png");
                        file.getParentFile().mkdirs();
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
                    transferObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                API3.Util.SetProfile_ImgLocalCode localCode = API3.Impl.getRepository().SetProfile_ImgParameterRegex(post_date + "png");
                                if (localCode == null) {
                                    Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date), new JsonHttpResponseHandler() {

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            API3.Impl.getRepository().SetProfile_ImgResponse(response, new API3.PayloadResponseCallback() {
                                                @Override
                                                public void onSuccess(JSONObject payload) {
                                                    try {
                                                        String profile_img = payload.getString("profile_img");
                                                        SavedData.setServerPicture(Application_Gocci.getInstance().getApplicationContext(), profile_img);
                                                        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_PROFILEIMG, post_date));
                                                    } catch (JSONException e) {
                                                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                                    }
                                                }

                                                @Override
                                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                                    Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, globalCode);
                                                }

                                                @Override
                                                public void onLocalError(String errorMessage) {
                                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
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

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postMovieAsync(final Context context, final Const.ActivityCategory activityCategory, final String rest_id, String movie_name, String category_id, String value, final String memo, String cheer_flag) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetPostLocalCode localCode = API3.Impl.getRepository().SetPostParameterRegex(rest_id, movie_name, category_id, value, memo, cheer_flag);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetPostAPI(rest_id, movie_name, category_id, value, memo, cheer_flag), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetPostResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_POST, memo));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST, globalCode);
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.SET_POST, memo));
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postWantAsync(final Context context, final String rest_id) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetWantLocalCode localCode = API3.Impl.getRepository().SetWantParameterRegex(rest_id);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getSetWantAPI(rest_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().SetWantResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_WANT, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_WANT, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void postUnWantAsync(final Context context, final String rest_id) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetWantLocalCode localCode = API3.Impl.getRepository().UnsetWantParameterRegex(rest_id);
            if (localCode == null) {
                Application_Gocci.getJsonAsync(API3.Util.getUnsetWantAPI(rest_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        API3.Impl.getRepository().UnsetWantResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_WANT, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_WANT, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.cheat_input), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}
