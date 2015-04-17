package com.inase.android.gocci.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 便利メソッド群クラス
 * Created by kmaeda on 2015/01/23.
 */
public class Util {

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
}
