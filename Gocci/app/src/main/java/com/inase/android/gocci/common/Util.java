package com.inase.android.gocci.common;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
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

    public static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        return dateTimeFormat.format(now.getTime());
    }

    public static Uri getUri(String path) {
        // Extract Bitmap from ImageView drawable
        ThumbnailUtils tu = new ThumbnailUtils();
        Bitmap bmp = tu.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
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

    public static void setAdviceDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title("アドバイスを送る")
                .content("コメントを入力してください")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        Log.e("えでぃっと", String.valueOf(charSequence));
                    }
                })
                .positiveText("Gocciに送る")
                .positiveColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String message = dialog.getInputEditText().getText().toString();

                        if (!message.isEmpty()) {
                            postAdviceAsync(context, message);
                        } else {
                            Toast.makeText(context, "文字を入力してください", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setViolateDialog(final Context context, final String post_id) {
        new MaterialDialog.Builder(context)
                .title("投稿の違反報告")
                .content("この投稿を違反報告しますか？")
                .positiveText("報告する")
                .negativeText("いいえ")
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
                Toast.makeText(context, "違反報告に失敗しました", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "いいねに失敗しました", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "フォローできませんでした", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "フォロー解除できませんでした", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "行きたい店リストに追加できませんでした", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "行きたい店リストから解除できませんでした", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void facebookVideoShare(final Context context, final ShareDialog dialog, String key) {
        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        TransferObserver transferObserver = Application_Gocci.transferUtility.download(Const.GET_MOVIE_BUCKET_NAME, key, file);
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
                        Toast.makeText(context, "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "twitterシェアに失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    public static void instaVideoShare(final Context context, final String restname, String key) {
        final File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        TransferObserver transferObserver = Application_Gocci.transferUtility.download(Const.GET_MOVIE_BUCKET_NAME, key, file);
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
                Toast.makeText(context, "instagramシェアに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
