package com.inase.android.gocci.common;

/**
 * 定数定義クラス
 * Created by kmaeda on 2015/01/22.
 */
public class Const {
    public static final int TIMELINE_LIMIT = 30;
    public static final int VERSION_NUMBER = 1;

    // 動画ファイルのキャッシュファイルの接頭辞
    public static final String URL_SIGNUP_API = "http://api-gocci.jp/login/";
    public static final String URL_TIMELINE_API = "http://api-gocci.jp/timeline/?limit=" + String.valueOf(TIMELINE_LIMIT);
    public static final String URL_GOOD_API = "http://api-gocci.jp/goodinsert/";
    public static final String URL_DELETE_API = "http://api-gocci.jp/delete/";
    public static final String URL_VIOLATE_API = "http://api-gocci.jp/violation/";
    public static final String URL_FAVORITE_API = "http://api-gocci.jp/favorites/";
    public static final String URL_UNFAVORITE_API = "http://api-gocci.jp/unfavorites/";
    public static final String URL_ADVICE_API = "http://api-gocci.jp/feedback/";

    // 動画ファイルのキャッシュファイルの接頭辞
    public static final String MOVIE_CACHE_PREFIX = "movie_cache_";

    // HTTP Status Not Modified
    public static final int HTTP_STATUS_NOT_MODIFIED = 304;

    // 動画取得リトライ上限回数
    public static final int GET_MOVIE_MAX_RETRY_COUNT = 5;

}
