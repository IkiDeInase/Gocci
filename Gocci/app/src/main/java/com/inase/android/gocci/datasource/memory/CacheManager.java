package com.inase.android.gocci.datasource.memory;

import android.content.Context;
import android.util.Log;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import cz.msebera.android.httpclient.Header;

/**
 * キャッシュ管理クラス
 * Created by kmaeda on 2015/01/22.
 */
public class CacheManager {
    private static final int CONNECTION_TIMEOUT = 10 * 1000;
    private static final int RESPONCE_TIMEOUT = 60 * 1000;

    /**
     * キャッシュマネージャーのイベント通知インタフェース
     */
    public interface ICacheManagerListener {
        void movieCacheCreated(boolean success, String postId);
    }


    /**
     * キャッシュマネージャーのキャッシュリクエストVO
     */
    private static class MovieCacheRequestVo {
        private Context mContext;
        private String mUrl;
        private String mPostId;
        private ICacheManagerListener mICacheManagerListener;
        private int mRetryCount;

        public MovieCacheRequestVo(Context context, String url, String postId, ICacheManagerListener ICacheManagerListener) {
            mContext = context;
            mUrl = url;
            mPostId = postId;
            mICacheManagerListener = ICacheManagerListener;
            mRetryCount = 0;
        }

        public Context getContext() {
            return mContext;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getPostId() {
            return mPostId;
        }

        public ICacheManagerListener getICacheManagerListener() {
            return mICacheManagerListener;
        }

        public int getRetryCount() {
            return mRetryCount;
        }

        public boolean isRetryWithCountUp() {
            mRetryCount++;
            return mRetryCount < Const.GET_MOVIE_MAX_RETRY_COUNT;
        }

        public String getCacheName() {
            return Const.MOVIE_CACHE_PREFIX.concat(getPostId()).concat(Util.getFileExtension(getUrl()));
        }

        public static String getCacheName(String postId, String url) {
            return Const.MOVIE_CACHE_PREFIX.concat(postId).concat(Util.getFileExtension(url));
        }

    }


    // 初期化フラグ
    private static boolean sInitFlag = false;

    // キャッシュディレクトリ
    private File mCacheDir;

    private ArrayList<String> mCachedIds;

    // 非同期通信クライアント
    private AsyncHttpClient mAsyncHttpClient;

    // 通信レスポンスハンドラー
    private BinaryHttpResponseHandler mBinaryHttpResponseHandler;
    private Stack<MovieCacheRequestVo> mMovieCacheRequestVos;

    // リクエスト中フラグ
    private boolean mRequestFlag;

    // リクエスト中Vo
    private MovieCacheRequestVo mRequestVo;

    // リクエスト中File
    private File mRequestFile;

    private int countNumber = 0;


    /**
     * Singletonの設定
     */
    private static CacheManager ourInstance = new CacheManager();

    public static CacheManager getInstance(Context context) {
        // 初回は初期化処理が走る
        if (!sInitFlag) {
            ourInstance.init(context);
            sInitFlag = true;
        }
        return ourInstance;
    }

    private CacheManager() {
    }

    /**
     * 初期化メソッド
     *
     * @param context コンテキスト
     */
    private void init(Context context) {
        mCacheDir = context.getCacheDir();
        mCachedIds = new ArrayList<>();
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.setConnectTimeout(CONNECTION_TIMEOUT);
        mAsyncHttpClient.setResponseTimeout(RESPONCE_TIMEOUT);
        mMovieCacheRequestVos = new Stack<MovieCacheRequestVo>();
        mRequestFlag = false;
    }

    /**
     * キャッシュファイルパスを返す
     *
     * @param postId ポストID
     * @param url    動画URL
     * @return キャッシュファイルパス(nullが返った場合はキャッシュが存在しない)
     */
    public String getCachePath(String postId, String url) {
        if (!mCachedIds.contains(postId)) {
            return null;
        }
        final File file = new File(mCacheDir, MovieCacheRequestVo.getCacheName(postId, url));
        return file.getPath();
    }

    public File getCacheFile(String postId, String url) {
        if (!mCachedIds.contains(postId)) {
            return null;
        }
        final File file = new File(mCacheDir, MovieCacheRequestVo.getCacheName(postId, url));
        return file;
    }

    /**
     * Movieキャッシュ作成要求
     *
     * @param url      動画URL
     * @param listener 完了時に呼ばれるリスナー
     */
    public void requestMovieCacheCreate(Context context, String url, String postId, ICacheManagerListener listener) {
        final MovieCacheRequestVo movieCacheRequestVo = new MovieCacheRequestVo(context, url, postId, listener);
        mMovieCacheRequestVos.push(movieCacheRequestVo);
        startMovieHttpReuest();
    }

    /**
     * キャッシュを全て削除する
     */
    public static void clearCache() {
        File[] lFiles = ourInstance.mCacheDir.listFiles();
        ourInstance.mCachedIds.clear();
        for (File file : lFiles) {
            file.delete();
        }
    }

    /**
     * キャッシュリクエスト開始メソッド
     */
    private void startMovieHttpReuest() {
        Log.d("DEBUG", "CacheManager::startMovieHttpReuest");
        if (!mRequestFlag && mMovieCacheRequestVos.size() > 0) {
            mRequestFlag = true;
            mRequestVo = mMovieCacheRequestVos.pop();
            mRequestFile = new File(mCacheDir, mRequestVo.getCacheName());
            Log.d("DEBUG", "CacheManager::startMovieHttpReuest DL指示開始 postId:" + mRequestVo.getPostId());
            if (mRequestFile.exists()) {
                Log.d("DEBUG", "CacheManager::startMovieHttpReuest NOT MODIFIED");
                mRequestVo.getICacheManagerListener().movieCacheCreated(true, mRequestVo.getPostId());
                mRequestFlag = false;
                startMovieHttpReuest();
            } else {
                Log.d("DEBUG", "CacheManager::startMovieHttpReuest DL開始");
                mAsyncHttpClient.get(mRequestVo.getContext(), mRequestVo.getUrl(), new FileAsyncHttpResponseHandler(mRequestFile) {
                            @Override
                            public void onProgress(final long bytesWritten, final long totalSize) {
                                int mProgressStatus = (totalSize > 0) ? (int) ((bytesWritten * 1.0 / totalSize) * 100) : -1;

                                if (mProgressStatus % 10 == 0 && mProgressStatus == countNumber) {
                                    countNumber += 10;
                                    //mRequestVo.getProgressBar().setProgress(mProgressStatus);
                                }
                                if (mProgressStatus == 100) {
                                    countNumber = 0;
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                Log.d("DEBUG", "HttpResponseHeader onCheckFailure: status:" + statusCode);
                                mRequestFlag = false;
                                if (mRequestVo.isRetryWithCountUp()) {
                                    mMovieCacheRequestVos.push(mRequestVo);
                                } else {
                                    // Listenerに通知
                                    mRequestVo.getICacheManagerListener().movieCacheCreated(false, mRequestVo.getPostId());
                                }
                                startMovieHttpReuest();

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, File file) {
                                Log.d("DEBUG", "HttpResponseHeader onCheckSuccess");
                                mCachedIds.add(mRequestVo.getPostId());
                                // Listenerに通知
                                mRequestVo.getICacheManagerListener().movieCacheCreated(true, mRequestVo.getPostId());
                                mRequestFlag = false;
                                startMovieHttpReuest();
                            }
                        }
                );
            }
        }
    }
}
