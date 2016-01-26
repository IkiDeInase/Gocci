package com.inase.android.gocci.utils;

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
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.utils.share.FacebookUtil;
import com.inase.android.gocci.utils.share.TwitterUtil;
import com.twitter.sdk.android.core.TwitterAuthToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 便利メソッド群クラス
 * Created by kmaeda on 2015/01/23.
 */
public class Util {

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

    public static int getScreenHeightInPx(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels);
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

    public static String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.US);
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

    public static void setFeedbackDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.advice_title))
                .titleColorRes(R.color.namegrey)
                .content(context.getString(R.string.advice_message))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        String message = charSequence.toString();

                        if (!message.isEmpty()) {
                            API3PostUtil.setFeedbackAsync(context, message);
                        } else {
                            Toast.makeText(context, context.getString(R.string.advice_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(context.getString(R.string.advice_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    public static void setPostBlockDialog(final Context context, final String post_id) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.violate_title))
                .titleColorRes(R.color.namegrey)
                .content(context.getString(R.string.violate_message))
                .positiveText(context.getString(R.string.violate_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(context.getString(R.string.violate_no))
                .negativeColorRes(R.color.gocci_header)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        API3PostUtil.setPostBlockAsync(context, post_id);
                    }
                }).show();
    }

    public static void facebookVideoShare(final Context context, final String message, final String key, final String token) {
        new MaterialDialog.Builder(context)
                .content("Facebookでは動画がシェアされます。\nメッセージを追加してシェアしましょう！")
                .contentColorRes(R.color.namegrey)
                .positiveText("シェアする")
                .positiveColorRes(R.color.gocci_header)
                .negativeText("いいえ")
                .negativeColorRes(R.color.gocci_header)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.facebook_background)
                .input("", message, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, final CharSequence charSequence) {
                        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
                        TransferObserver transferObserver = Application_Gocci.getTransfer(context).download(Const.GET_MOVIE_BUCKET_NAME, "mp4/" + key + ".mp4", file);
                        transferObserver.setTransferListener(new TransferListener() {
                            @Override
                            public void onStateChanged(int id, TransferState state) {
                                if (state == TransferState.COMPLETED) {
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            FacebookUtil.performShare(context, token, file, charSequence.toString());
                                            return null;
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                }).show();
    }

    public static void twitterVideoShare(final Context context, final String message, final String key, final TwitterAuthToken authToken) {
        new MaterialDialog.Builder(context)
                .content("Twitter動画がシェアされます。\nメッセージを追加してシェアしましょう！")
                .contentColorRes(R.color.namegrey)
                .positiveText("シェアする")
                .positiveColorRes(R.color.gocci_header)
                .negativeText("いいえ")
                .negativeColorRes(R.color.gocci_header)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.twitter_background)
                .inputRangeRes(6, 115, R.color.gocci_header)
                .input("", message, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, final CharSequence charSequence) {
                        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
                        TransferObserver transferObserver = Application_Gocci.getTransfer(context).download(Const.GET_MOVIE_BUCKET_NAME, "mp4/" + key + ".mp4", file);
                        transferObserver.setTransferListener(new TransferListener() {
                            @Override
                            public void onStateChanged(int id, TransferState state) {
                                if (state == TransferState.COMPLETED) {
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            TwitterUtil.performShare(context, authToken.token, authToken.secret, file, charSequence.toString());
                                            return null;
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                })
                .show();
    }

    public static void instaVideoShare(final Context context, String key) {
        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        TransferObserver transferObserver = Application_Gocci.getTransfer(context).download(Const.GET_MOVIE_BUCKET_NAME, "mp4/" + key + ".mp4", file);
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
}
