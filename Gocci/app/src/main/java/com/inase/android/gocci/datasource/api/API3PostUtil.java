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
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.utils.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/19.
 */
public class API3PostUtil {

    public static void postFeedbackAsync(final Context context, final String feedback) {
        API3.Util.SetFeedbackLocalCode localCode = API3.Impl.getRepository().SetFeedbackParameterRegex(feedback);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetFeedbackAPI(feedback), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetFeedbackResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.SetFeedbackLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postBlockAsync(final Context context, final String post_id) {
        API3.Util.SetBlockLocalCode localCode = API3.Impl.getRepository().SetBlockParameterRegex(post_id);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetBlockAPI(post_id), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetBlockResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.SetBlockLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postDeleteAsync(final Context context, final String post_id, final Const.ActivityCategory activityCategory) {
        API3.Util.UnsetPostLocalCode localCode = API3.Impl.getRepository().UnsetPostParameterRegex(post_id);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getUnsetPostAPI(post_id), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().UnsetPostResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.UnsetPostLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postPasswordAsync(final Context context, final String password) {
        API3.Util.SetPasswordLocalCode localCode = API3.Impl.getRepository().SetPasswordParameterRegex(password);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetPasswordAPI(password), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetPasswordResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.SetPasswordLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postRestAddAsync(final Context context, final Const.ActivityCategory activityCategory, final String restname, String lon, String lat) {
        API3.Util.SetRestLocalCode localCode = API3.Impl.getRepository().SetRestParameterRegex(restname, lat, lon);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetRestAPI(restname, lat, lon), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetRestResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    try {
                                        JSONObject payload = jsonObject.getJSONObject("payload");
                                        String rest_id = payload.getString("rest_id");
                                        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_RESTADD, rest_id));
                                    } catch (JSONException e) {
                                        Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_RESTADD, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
                                    }
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
            Toast.makeText(context, API3.Util.SetRestLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postSnsLinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        API3.Util.SetSns_LinkLocalCode localCode = API3.Impl.getRepository().SetSns_LinkParameterRegex(provider, sns_token);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.SetSns_LinkLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postSnsUnlinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        API3.Util.UnsetSns_LinkLocalCode localCode = API3.Impl.getRepository().UnsetSns_LinkParameterRegex(provider, sns_token);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getUnsetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().UnsetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.UnsetSns_LinkLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postDeviceAsync(final Context context, final String regId, String os, String ver, String model) {
        API3.Util.SetDeviceLocalCode localCode = API3.Impl.getRepository().SetDeviceParameterRegex(regId, os, ver, model);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetDeviceAPI(regId, os, ver, model), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetDeviceResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {

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
            Toast.makeText(context, API3.Util.SetDeviceLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUnDeviceAsync(final Context context, final String regId) {
        API3.Util.UnsetDeviceLocalCode localCode = API3.Impl.getRepository().UnsetDeviceParameterRegex(regId);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getUnsetDeviceAPI(regId), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().UnsetDeviceResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {

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
            Toast.makeText(context, API3.Util.UnsetDeviceLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postUsernameAsync(final Context context, final String username, final Const.ActivityCategory activityCategory) {
        API3.Util.SetUsernameLocalCode localCode = API3.Impl.getRepository().SetUsernameParameterRegex(username);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetUsernameAPI(username), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetUsernameResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    try {
                                        JSONObject payload = jsonObject.getJSONObject("payload");
                                        String username = payload.getString("username");
                                        SavedData.setServerName(Application_Gocci.getInstance().getApplicationContext(), username);
                                        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_USERNAME, username));
                                    } catch (JSONException e) {
                                        Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_USERNAME, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
                                    }
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
            Toast.makeText(context, API3.Util.SetUsernameLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public static void postProfileImgAsync(final Context context, final String post_date, File file, final Const.ActivityCategory activityCategory) {
        TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    API3.Util.SetProfile_ImgLocalCode localCode = API3.Impl.getRepository().SetProfile_ImgParameterRegex(post_date + "png");
                    if (localCode == null) {
                        API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
                        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                            try {
                                Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date), new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        API3.Impl.getRepository().SetProfile_ImgResponse(response, new API3.PayloadResponseCallback() {
                                            @Override
                                            public void onSuccess(JSONObject jsonObject) {
                                                try {
                                                    JSONObject payload = jsonObject.getJSONObject("payload");
                                                    String profile_img = payload.getString("profile_img");
                                                    SavedData.setServerPicture(Application_Gocci.getInstance().getApplicationContext(), profile_img);
                                                    BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_PROFILEIMG, post_date));
                                                } catch (JSONException e) {
                                                    Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
                                                }
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
                        Toast.makeText(context, API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
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

    public static void postProfileImgAsync(final Context context, final String post_date, String url, final Const.ActivityCategory activityCategory) {
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
                                API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
                                if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                                    try {
                                        Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date), new JsonHttpResponseHandler() {

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                API3.Impl.getRepository().SetProfile_ImgResponse(response, new API3.PayloadResponseCallback() {
                                                    @Override
                                                    public void onSuccess(JSONObject jsonObject) {
                                                        try {
                                                            JSONObject payload = jsonObject.getJSONObject("payload");
                                                            String profile_img = payload.getString("profile_img");
                                                            SavedData.setServerPicture(Application_Gocci.getInstance().getApplicationContext(), profile_img);
                                                            BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.POST_PROFILEIMG, post_date));
                                                        } catch (JSONException e) {
                                                            Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.POST_PROFILEIMG, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
                                                        }
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
                                Toast.makeText(context, API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
    }

    public static void postMovieAsync(final Context context, final Const.ActivityCategory activityCategory, final String rest_id, String movie_name, String category_id, String value, final String memo, String cheer_flag) {
        API3.Util.SetPostLocalCode localCode = API3.Impl.getRepository().SetPostParameterRegex(rest_id, movie_name, category_id, value, memo, cheer_flag);
        if (localCode == null) {
            API3.Util.GlobalCode globalCode = API3.Impl.getRepository().CheckGlobalCode();
            if (globalCode == API3.Util.GlobalCode.SUCCESS) {
                try {
                    Application_Gocci.getJsonAsync(API3.Util.getSetPostAPI(rest_id, movie_name, category_id, value, memo, cheer_flag), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            API3.Impl.getRepository().SetPostResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
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
            Toast.makeText(context, API3.Util.SetPostLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
            BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.POST_POST, memo));
        }
    }
}
