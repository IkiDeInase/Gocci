package com.inase.android.gocci.datasource.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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

    private static long startTime;

    public static void setFeedbackAsync(final Context context, final String feedback) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetFeedbackLocalCode localCode = API3.Impl.getRepository().SetFeedbackParameterRegex(feedback);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetFeedbackAPI(feedback), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_FEEDBACK.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetFeedbackResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_FEEDBACK, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_FEEDBACK.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_FEEDBACK.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_FEEDBACK, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_FEEDBACK.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetFeedbackLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_FEEDBACK.name()).
                        setLabel(API3.Util.SetFeedbackLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setPostBlockAsync(final Context context, final String post_id) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetPost_BlockLocalCode localCode = API3.Impl.getRepository().SetPost_BlockParameterRegex(post_id);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetPostBlockAPI(post_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_POST_BLOCK.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetPost_BlockResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "この投稿を違反報告しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST_BLOCK, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_POST_BLOCK.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_POST_BLOCK.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST_BLOCK, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_POST_BLOCK.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetPost_BlockLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_POST_BLOCK.name()).
                        setLabel(API3.Util.SetPost_BlockLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setCommentBlockAsync(final Context context, final String comment_id) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetComment_BlockLocalCode localCode = API3.Impl.getRepository().SetComment_BlockParameterRegex(comment_id);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetCommentBlockAPI(comment_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_COMMENT_BLOCK.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetComment_BlockResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "このコメントを違反報告しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_COMMENT_BLOCK, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_COMMENT_BLOCK.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_COMMENT_BLOCK.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_COMMENT_BLOCK, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_COMMENT_BLOCK.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetComment_BlockLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_COMMENT_BLOCK.name()).
                        setLabel(API3.Util.SetComment_BlockLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void unsetPostAsync(final Context context, final String post_id, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetPostLocalCode localCode = API3.Impl.getRepository().UnsetPostParameterRegex(post_id);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getUnsetPostAPI(post_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.UNSET_POST.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().UnsetPostResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.UNSET_POST, post_id));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_POST, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_POST.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_POST.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_POST, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.UNSET_POST.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.UnsetPostLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.UNSET_POST.name()).
                        setLabel(API3.Util.UnsetPostLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void unsetCommentAsync(final Context context, final String comment_id, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetCommentLocalCode localCode = API3.Impl.getRepository().UnsetCommentParameterRegex(comment_id);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getUnsetCommentAPI(comment_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.UNSET_COMMENT.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().UnsetCommentResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.UNSET_COMMENT, comment_id));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_COMMENT, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_COMMENT.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_COMMENT.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_COMMENT, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.UNSET_COMMENT.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.UnsetCommentLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.UNSET_COMMENT.name()).
                        setLabel(API3.Util.UnsetCommentLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setCommentEditAsync(final Context context, final String comment_id, final String comment, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetComment_EditLocalCode localCode = API3.Impl.getRepository().SetComment_EditParameterRegex(comment_id, comment);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetCommentEditAPI(comment_id, comment), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_COMMENT_EDIT.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetComment_EditResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_COMMENT_EDIT, comment_id));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_COMMENT_EDIT, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_COMMENT_EDIT.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_COMMENT_EDIT.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_COMMENT_EDIT, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_COMMENT_EDIT.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetComment_EditLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_COMMENT_EDIT.name()).
                        setLabel(API3.Util.SetComment_EditLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setMemoEditAsync(final Context context, final String post_id, final String memo, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetMemo_EditLocalCode localCode = API3.Impl.getRepository().SetMemo_EditParameterRegex(post_id, memo);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetMemoEditAPI(post_id, memo), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_MEMO_EDIT.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetMemo_EditResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_MEMO_EDIT, post_id));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_MEMO_EDIT, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_MEMO_EDIT.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_MEMO_EDIT.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_MEMO_EDIT, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_MEMO_EDIT.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetMemo_EditLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_MEMO_EDIT.name()).
                        setLabel(API3.Util.SetMemo_EditLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setPasswordAsync(final Context context, final String password, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetPasswordLocalCode localCode = API3.Impl.getRepository().SetPasswordParameterRegex(password);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetPasswordAPI(password), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_PASSWORD.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetPasswordResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                Toast.makeText(context, "パスワードを設定しました", Toast.LENGTH_SHORT).show();
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, password));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PASSWORD, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_PASSWORD.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_PASSWORD.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PASSWORD, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_PASSWORD.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetPasswordLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_PASSWORD.name()).
                        setLabel(API3.Util.SetPasswordLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setRestAsync(final Context context, final Const.ActivityCategory activityCategory, final String restname, String lon, String lat) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetRestLocalCode localCode = API3.Impl.getRepository().SetRestParameterRegex(restname, lat, lon);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetRestAPI(restname, lat, lon), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_RESTADD.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
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
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_RESTADD.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_RESTADD.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_RESTADD, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_RESTADD.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetRestLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_RESTADD.name()).
                        setLabel(API3.Util.SetRestLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setSnsLinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetSns_LinkLocalCode localCode = API3.Impl.getRepository().SetSns_LinkParameterRegex(provider, sns_token);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(api.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, api, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(api.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(api.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(api.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetSns_LinkLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(api.name()).
                        setLabel(API3.Util.SetSns_LinkLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void unsetSnsLinkAsync(final Context context, final String provider, final String sns_token, final Const.ActivityCategory activityCategory, final Const.APICategory api) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetSns_LinkLocalCode localCode = API3.Impl.getRepository().UnsetSns_LinkParameterRegex(provider, sns_token);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getUnsetSnsLinkAPI(provider, sns_token), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(api.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().UnsetSns_LinkResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, api, sns_token));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, api, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(api.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(api.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(api.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.UnsetSns_LinkLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(api.name()).
                        setLabel(API3.Util.UnsetSns_LinkLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setDeviceAsync(final Context context, final String regId, String os, String ver, String model) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetDeviceLocalCode localCode = API3.Impl.getRepository().SetDeviceParameterRegex(regId, os, ver, model);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetDeviceAPI(regId, os, ver, model), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_DEVICE.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetDeviceResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_DEVICE, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_DEVICE.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_DEVICE.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_DEVICE, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_DEVICE.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetDeviceLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_DEVICE.name()).
                        setLabel(API3.Util.SetDeviceLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void unsetDeviceAsync(final Context context) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.UnsetDeviceLocalCode localCode = API3.Impl.getRepository().UnsetDeviceParameterRegex();
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getUnsetDeviceAPI(), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.UNSET_DEVICE.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().UnsetDeviceResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {

                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_DEVICE, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_DEVICE.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.UNSET_DEVICE.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.UNSET_DEVICE, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.UNSET_DEVICE.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.UnsetDeviceLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.UNSET_DEVICE.name()).
                        setLabel(API3.Util.UnsetDeviceLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setUsernameAsync(final Context context, final String username, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetUsernameLocalCode localCode = API3.Impl.getRepository().SetUsernameParameterRegex(username);
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetUsernameAPI(username), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_USERNAME.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetUsernameResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                SavedData.setServerName(Application_Gocci.getInstance().getApplicationContext(), username);
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_USERNAME, username));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_USERNAME, globalCode);
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_USERNAME.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_USERNAME.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_USERNAME, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_USERNAME.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetUsernameLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_USERNAME.name()).
                        setLabel(API3.Util.SetUsernameLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setProfileImgAsync(final Context context, final String post_date, File file, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            startTime = System.currentTimeMillis();
            TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + "_img.png", file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable("ProfileImgUpload")
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Util.SetProfile_ImgLocalCode localCode = API3.Impl.getRepository().SetProfile_ImgParameterRegex(post_date + "_img");
                        if (localCode == null) {
                            startTime = System.currentTimeMillis();
                            Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date + "_img"), new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                    tracker.send(new HitBuilders.TimingBuilder()
                                            .setCategory("System")
                                            .setVariable(Const.APICategory.SET_PROFILEIMG.name())
                                            .setLabel(SavedData.getServerUserId(context))
                                            .setValue(System.currentTimeMillis() - startTime).build());
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
                                            Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                            tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                                    setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                                    setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                                        }

                                        @Override
                                        public void onLocalError(String errorMessage) {
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                            Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                            tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                                    setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                                    setLabel(errorMessage).build());
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                    tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                            setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                            setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                                }
                            });
                        } else {
                            Toast.makeText(context, API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                            tracker = Application_Gocci.getInstance().getDefaultTracker();
                            tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                    setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                    setLabel(API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode)).build());
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                    tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                            setAction(Const.APICategory.SET_PROFILEIMG.name()).
                            setLabel("Profile_imgUploadFailure").build());
                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    public static void setProfileImgAsync(final Context context, final String post_date, String url, final Const.ActivityCategory activityCategory) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            Picasso.with(context).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File file = null;
                    try {
                        file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), post_date + "_img.png");
                        file.getParentFile().mkdirs();
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTime = System.currentTimeMillis();
                    TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + "_img.png", file);
                    transferObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.TimingBuilder()
                                        .setCategory("System")
                                        .setVariable("ProfileImgUpload")
                                        .setLabel(SavedData.getServerUserId(context))
                                        .setValue(System.currentTimeMillis() - startTime).build());
                                API3.Util.SetProfile_ImgLocalCode localCode = API3.Impl.getRepository().SetProfile_ImgParameterRegex(post_date + "_img");
                                if (localCode == null) {
                                    startTime = System.currentTimeMillis();
                                    Application_Gocci.getJsonAsync(API3.Util.getSetProfileImgAPI(post_date + "_img"), new JsonHttpResponseHandler() {

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                            tracker.send(new HitBuilders.TimingBuilder()
                                                    .setCategory("System")
                                                    .setVariable(Const.APICategory.SET_PROFILEIMG.name())
                                                    .setLabel(SavedData.getServerUserId(context))
                                                    .setValue(System.currentTimeMillis() - startTime).build());
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
                                                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                                    tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                                            setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                                            setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                                                }

                                                @Override
                                                public void onLocalError(String errorMessage) {
                                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                                    tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                                            setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                                            setLabel(errorMessage).build());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_PROFILEIMG, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                            Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                            tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                                    setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                                    setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                                    tracker = Application_Gocci.getInstance().getDefaultTracker();
                                    tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                            setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                            setLabel(API3.Util.SetProfile_ImgLocalCodeMessageTable(localCode)).build());
                                }
                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                            tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                    setAction(Const.APICategory.SET_PROFILEIMG.name()).
                                    setLabel("Profile_imgUploadFailure").build());
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

    public static void setPostAsync(final Context context, final Const.ActivityCategory activityCategory, final String rest_id, String movie_name, int category_id, String value, final String memo, int cheer_flag) {
        if (Util.getConnectedState(context) != Util.NetworkStatus.OFF) {
            API3.Util.SetPostLocalCode localCode = API3.Impl.getRepository().SetPostParameterRegex(rest_id, movie_name, category_id == 1 ? null : String.valueOf(category_id), value.isEmpty() ? null : value, memo.isEmpty() ? null : memo, cheer_flag == 0 ? null : String.valueOf(cheer_flag));
            if (localCode == null) {
                startTime = System.currentTimeMillis();
                Application_Gocci.getJsonAsync(API3.Util.getSetPostAPI(rest_id, movie_name, category_id == 1 ? null : String.valueOf(category_id), value.isEmpty() ? null : value, memo.isEmpty() ? null : memo, cheer_flag == 0 ? null : String.valueOf(cheer_flag)), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.TimingBuilder()
                                .setCategory("System")
                                .setVariable(Const.APICategory.SET_POST.name())
                                .setLabel(SavedData.getServerUserId(context))
                                .setValue(System.currentTimeMillis() - startTime).build());
                        API3.Impl.getRepository().SetPostResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, activityCategory, Const.APICategory.SET_POST, memo));
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST, globalCode);
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_POST.name()).
                                        setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                        setAction(Const.APICategory.SET_POST.name()).
                                        setLabel(errorMessage).build());
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Application_Gocci.resolveOrHandleGlobalError(context, Const.APICategory.SET_POST, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                        Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                                setAction(Const.APICategory.SET_POST.name()).
                                setLabel(API3.Util.GlobalCodeMessageTable(API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR)).build());
                    }
                });
            } else {
                Toast.makeText(context, API3.Util.SetPostLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, activityCategory, Const.APICategory.SET_POST, memo));
                Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").
                        setAction(Const.APICategory.SET_POST.name()).
                        setLabel(API3.Util.SetPostLocalCodeMessageTable(localCode)).build());
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}
