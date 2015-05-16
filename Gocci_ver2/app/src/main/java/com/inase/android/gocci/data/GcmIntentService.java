package com.inase.android.gocci.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inase.android.gocci.Activity.SplashActivity;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.MainThreadBus;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;

/**
 * Created by kinagafuji on 15/05/01.
 */
public class GcmIntentService extends IntentService {
    static final private String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(GcmIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.

                sendNotification(extras.get("message").toString());
                Log.e(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        int NOTIFICATION_ID = 1;

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        BusHolder.get().post(new NotificationNumberEvent(Integer.parseInt(msg)));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_gocci_push)
                        .setContentTitle("Gocci")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
