package com.inase.android.gocci.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.Performer;
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 便利メソッド群クラス
 * Created by kmaeda on 2015/01/23.
 */
public class Util {

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CODE = "code";

    private static final String KEY_USER_ID = "user_id";

    /**
     * ファイルの拡張子を返す(ex: hoge.html -> .html)
     *
     * @param path ファイルパス
     * @return 拡張子
     */
    public static String getFileExtension(String path) {
        final int lastDotPosition = path.lastIndexOf(".");
        return lastDotPosition == -1 ? "" : path.substring(lastDotPosition);
    }

    public enum NetworkStatus {
        OFF,
        MOBILE,
        WIFI,
        WIMAX,
        LTE,
    }

    public static NetworkStatus getConnectedState(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkStatus status = NetworkStatus.OFF;

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            // OFF
            return status;
        }

        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:         // Wifi
                status = NetworkStatus.WIFI;
                break;
            case ConnectivityManager.TYPE_MOBILE_DUN:   // Mobile 3G
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        status = NetworkStatus.LTE;         // LTE
                        break;
                    default:
                        status = NetworkStatus.MOBILE;      // Mobile 3G
                        break;
                }
                break;
            case ConnectivityManager.TYPE_WIMAX:        // Wimax
                status = NetworkStatus.WIMAX;
                break;
        }
        return status;
    }

    public static boolean isMovieAutoPlay(Context context) {
        //0 Wi-Fi + Mobile / 1 Wi-Fi / 2 none
        int setting = SavedData.getSettingAutoPlay(context);
        if (setting == 2) {
            return false;
        }
        switch (getConnectedState(context)) {
            case OFF:
                return false;
            case WIFI:
            case WIMAX:
                return true;
            case MOBILE:
            case LTE:
                switch (setting) {
                    case 0:
                        return true;
                    case 1:
                        return false;
                }
            default:
                return true;
        }
    }

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static File getLocalBitmapFile(ImageView imageView, String post_date) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), post_date + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /*
    public static File getFile(String url, String post_date) {
        // Extract Bitmap from ImageView drawable
        Bitmap bmp = ImageLoader.getInstance().loadImageSync(url);
        // Store image to default external storage directory
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), post_date + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    */

    public static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        return dateTimeFormat.format(now.getTime());
    }

    public static Uri getUri(String path) {
        // Extract Bitmap from ImageView drawable
        ThumbnailUtils tu = new ThumbnailUtils();
        Bitmap bmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static void postRefreshRegId(Context context, String regId) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostRefreshRegId(regId, SavedData.getServerUserId(context), Build.VERSION.RELEASE), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void setAdviceDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.advice_title))
                .titleColorRes(R.color.namegrey)
                .content(context.getString(R.string.advice_message))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(context.getString(R.string.advice_yeah))
                .positiveColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String message = dialog.getInputEditText().getText().toString();

                        if (!message.isEmpty()) {
                            postAdviceAsync(context, message);
                        } else {
                            Toast.makeText(context, context.getString(R.string.advice_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private static void postAdviceAsync(final Context context, final String message) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostFeedbackAPI(message), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        Toast.makeText(context, context.getString(R.string.advice_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setViolateDialog(final Context context, final String post_id) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.violate_title))
                .titleColorRes(R.color.namegrey)
                .content(context.getString(R.string.violate_message))
                .positiveText(context.getString(R.string.violate_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(context.getString(R.string.violate_no))
                .negativeColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        violateSignupAsync(context, post_id);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    private static void violateSignupAsync(final Context context, final String post_id) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostViolateAPI(post_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //mMaterialDialog.dismiss();
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void postGochiAsync(final Context context, final PostData headerData) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostGochiAPI(headerData.getPost_id()), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //配列のpushed_atを１にする
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code != 200) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        headerData.setGochi_flag(0);
                        headerData.setGochi_num(headerData.getGochi_num() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                headerData.setGochi_flag(0);
                headerData.setGochi_num(headerData.getGochi_num() - 1);
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void followAsync(final Context context, final HeaderData headerData) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostFollowAPI(headerData.getUser_id()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        headerData.setFollow_flag(1);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void unfollowAsync(final Context context, final HeaderData headerData) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostUnFollowAPI(headerData.getUser_id()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        headerData.setFollow_flag(0);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void wantAsync(final Context context, final HeaderData headerData) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostWantAPI(headerData.getRest_id()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        headerData.setWant_flag(1);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void unwantAsync(final Context context, final HeaderData headerData) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostUnWantAPI(headerData.getRest_id()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);

                    if (code == 200) {
                        headerData.setWant_flag(0);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void passwordAsync(final Context context, final String password) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostPasswordAPI(password), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void facebookVideoShare(final Context context, final ShareDialog dialog, String key) {
        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        TransferObserver transferObserver = Application_Gocci.getTransfer(context).download(Const.GET_MOVIE_BUCKET_NAME, key, file);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Uri uri = Uri.fromFile(file);
                    if (ShareDialog.canShow(ShareVideoContent.class)) {
                        ShareVideo video = new ShareVideo.Builder()
                                .setLocalUrl(uri)
                                .build();
                        ShareVideoContent content = new ShareVideoContent.Builder()
                                .setVideo(video)
                                .build();
                        dialog.show(content);
                    } else {
                        // ...sharing failed, handle error
                        Toast.makeText(context, context.getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, context.getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void twitterShare(Context context, ImageView thumbnail, String restname) {
        Uri bmpUri = Util.getLocalBitmapUri(thumbnail);
        if (bmpUri != null) {
            TweetComposer.Builder builder = new TweetComposer.Builder(context)
                    .text("#" + restname.replaceAll("\\s+", "") + " #Gocci #FoodPorn")
                    .image(bmpUri);

            builder.show();
        } else {
            // ...sharing failed, handle error
            Toast.makeText(context, context.getString(R.string.error_share), Toast.LENGTH_SHORT).show();
        }
    }

    public static void instaVideoShare(final Context context, final String restname, String key) {
        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        TransferObserver transferObserver = Application_Gocci.getTransfer(context).download(Const.GET_MOVIE_BUCKET_NAME, key, file);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Uri uri = Uri.fromFile(file);
                    // Create the new Intent using the 'Send' action.
                    Intent share = new Intent(Intent.ACTION_SEND);
                    // Set the MIME type
                    share.setType("video/*");
                    // Add the URI and the caption to the Intent.
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setPackage("com.instagram.android");
                    share.putExtra(Intent.EXTRA_TEXT, "#" + restname + " #Gocci #FoodPorn");
                    // Broadcast the Intent.
                    context.startActivity(Intent.createChooser(share, "Share to"));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, context.getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void searchUserPost(final Activity activiy, final Context context, final String username) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostSearchUser(username), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString(KEY_MESSAGE);
                    int code = response.getInt(KEY_CODE);
                    int user_id = response.getInt(KEY_USER_ID);

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    if (code == 200) {
                        FlexibleUserProfActivity.startUserProfActivity(user_id, username, activiy);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static final long DEFAULT_DURATION = 200L;
    private static final String ROTATION = "rotation";
    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    public static void rotateToSelect(final FloatingActionButton fab) {
        rotate(fab, 45f);
    }

    public static void rotateToUnSelect(final FloatingActionButton fab) {
        rotate(fab, 0f);
    }

    private static void rotate(final FloatingActionButton fab, float value) {
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat(ROTATION, value);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fab, holder).setDuration(150L);
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.start();
    }

    public static void animateIn(final FloatingActionButton fab) {
        animateIn(fab, DEFAULT_DURATION, null);
    }

    public static void animateInFast(final FloatingActionButton fab, final AnimateCallback callback) {
        animateIn(fab, 100L, callback);
    }

    public static void animateIn(final FloatingActionButton fab, long duration, final AnimateCallback callback) {
        fab.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(fab)
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .alpha(1.0F)
                    .setDuration(duration)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            if (callback != null) callback.onAnimationStart();
                        }

                        public void onAnimationCancel(View view) {
                        }

                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.VISIBLE);
                            if (callback != null) callback.onAnimationEnd();
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(fab.getContext(), R.anim.fab_in);
            anim.setDuration(duration);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public void onAnimationStart(Animation animation) {
                    if (callback != null) callback.onAnimationStart();
                }

                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.VISIBLE);
                    if (callback != null) callback.onAnimationEnd();
                }
            });
            fab.startAnimation(anim);
        }
    }

    public static void animateOut(final FloatingActionButton fab) {
        animateOut(fab, DEFAULT_DURATION, null);
    }

    public static void animateOutFast(final FloatingActionButton fab, final AnimateCallback callback) {
        animateOut(fab, 30L, callback);
    }

    public static void animateOut(final FloatingActionButton fab, final AnimateCallback callback) {
        animateOut(fab, DEFAULT_DURATION, callback);
    }

    public static void animateOut(final FloatingActionButton fab, long duration, final AnimateCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(fab)
                    .scaleX(0.0F)
                    .scaleY(0.0F).alpha(0.0F)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(duration)
                    .withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            if (callback != null) callback.onAnimationStart();
                        }

                        public void onAnimationCancel(View view) {
                        }

                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.INVISIBLE);
                            if (callback != null) callback.onAnimationEnd();
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(fab.getContext(), R.anim.fab_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(duration);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public void onAnimationStart(Animation animation) {
                    if (callback != null) callback.onAnimationStart();
                }

                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.INVISIBLE);
                    if (callback != null) callback.onAnimationEnd();
                }
            });
            fab.startAnimation(anim);
        }
    }

    public static void moveIn(final FloatingActionButton fab, View view, ViewPropertyAnimatorListener listener) {
        int marginRight;
        int marginBottom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            marginRight = 16;
            marginBottom = 16;
        } else {
            marginRight = 8;
            marginBottom = 0;
        }
        ViewCompat.animate(fab)
                .translationX(-(view.getWidth() / 2) + (fab.getWidth() / 2) + dpToPx(view.getContext(), marginRight))
                .translationY(-(view.getHeight() / 2) + (fab.getHeight() / 2) + dpToPx(view.getContext(), marginBottom))
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(150L)
                .withLayer()
                .setListener(listener)
                .start();
    }

    public static void moveOut(final FloatingActionButton fab, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(fab)
                .translationX(0)
                .translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150L)
                .withLayer()
                .setListener(listener)
                .start();
    }

    public static float dpToPx(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }

    public interface AnimateCallback {
        public void onAnimationStart();

        public void onAnimationEnd();
    }

    public interface RevealCallback {
        public void onRevealStart();

        public void onRevealEnd();
    }

    static class AnimationListenerAdapter implements Animation.AnimationListener {
        AnimationListenerAdapter() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private static class Destroyer implements SpringListener {

        public int mMin, mMax;

        protected ViewGroup mViewGroup;
        protected View mViewToRemove;

        private Destroyer(ViewGroup viewGroup, View viewToRemove, int min,
                          int max) {
            mViewGroup = viewGroup;
            mViewToRemove = viewToRemove;

            mMin = min;
            mMax = max;
        }

        public boolean shouldClean(Spring spring) {
            // these are arbitrary values to keep the view from disappearing before it is
            // fully off the screen
            return spring.getCurrentValue() < mMin || spring.getCurrentValue() > mMax;
        }

        public void clean(Spring spring) {
            if (mViewGroup != null && mViewToRemove != null) {
                mViewGroup.removeView(mViewToRemove);
            }
            if (spring != null) {
                spring.destroy();
            }
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            if (shouldClean(spring)) {
                clean(spring);
            }
        }

        @Override
        public void onSpringAtRest(Spring spring) {

        }

        @Override
        public void onSpringActivate(Spring spring) {

        }

        @Override
        public void onSpringEndStateChange(Spring spring) {

        }
    }

    public static void createCircle(Context context, ViewGroup rootView,
                                     SpringSystem springSystem,
                                     SpringConfig coasting,
                                     SpringConfig gravity,
                                     int diameter,
                                     Drawable backgroundDrawable) {

        final Spring xSpring = springSystem.createSpring().setSpringConfig(coasting);
        final Spring ySpring = springSystem.createSpring().setSpringConfig(gravity);

        // create view
        View view = new View(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(diameter, diameter);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(params);
        view.setBackgroundDrawable(backgroundDrawable);

        rootView.addView(view);

        // generate random direction and magnitude
        double magnitude = Math.random() * 1000 + 1500;
        double angle = Math.random() * Math.PI / 2 + Math.PI / 4;

        xSpring.setVelocity(magnitude * Math.cos(angle));
        ySpring.setVelocity(-magnitude * Math.sin(angle));

        int maxX = rootView.getMeasuredWidth() / 2 + diameter;
        xSpring.addListener(new Destroyer(rootView, view, -maxX, maxX));

        int maxY = rootView.getMeasuredHeight() / 2 + diameter;
        ySpring.addListener(new Destroyer(rootView, view, -maxY, maxY));

        xSpring.addListener(new Performer(view, View.TRANSLATION_X));
        ySpring.addListener(new Performer(view, View.TRANSLATION_Y));

        // set a different end value to cause the animation to play
        xSpring.setEndValue(2);
        ySpring.setEndValue(9001);
    }
}
