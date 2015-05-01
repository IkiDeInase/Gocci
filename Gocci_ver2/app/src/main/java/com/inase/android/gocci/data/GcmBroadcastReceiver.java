package com.inase.android.gocci.data;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by kinagafuji on 15/05/01.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    public GcmBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // GCMを受信 メッセージ受信用のサービスを起動する
        ComponentName comp =
                new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
