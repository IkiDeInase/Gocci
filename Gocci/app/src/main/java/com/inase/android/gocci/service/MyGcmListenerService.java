package com.inase.android.gocci.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.inase.android.gocci.R;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.ui.activity.SplashActivity;
import com.inase.android.gocci.utils.SavedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by kinagafuji on 15/08/12.
 */
public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String def = data.getString("default");
        if (def != null) {
            try {
                JSONObject jsonObject = new JSONObject(def);
                String type = jsonObject.getString("type");

                Integer[] list = SavedData.getSettingNotifications(this);
                int badge_num = SavedData.getNotification(getApplicationContext());

                String id = null;
                String username = null;
                switch (type) {
                    case "follow":
                        id = jsonObject.getString("id");
                        username = jsonObject.getString("username");
                        if (Arrays.asList(list).contains(2)) {
                            sendNotification(username + getString(R.string.notice_from_follow));
                        }
                        BusHolder.get().post(new NotificationNumberEvent(badge_num + 1, def));
                        SavedData.setNotification(getApplicationContext(), badge_num + 1);
                        break;
                    case "gochi":
                        id = jsonObject.getString("id");
                        username = jsonObject.getString("username");
                        if (Arrays.asList(list).contains(0)) {
                            sendNotification(username + getString(R.string.notice_from_gochi));
                        }
                        BusHolder.get().post(new NotificationNumberEvent(badge_num + 1, def));
                        SavedData.setNotification(getApplicationContext(), badge_num + 1);
                        break;
                    case "comment":
                        id = jsonObject.getString("id");
                        username = jsonObject.getString("username");
                        if (Arrays.asList(list).contains(1)) {
                            sendNotification(username + getString(R.string.notice_from_comment));
                        }
                        BusHolder.get().post(new NotificationNumberEvent(badge_num + 1, def));
                        SavedData.setNotification(getApplicationContext(), badge_num + 1);
                        break;
                    case "announce":
                        String message = jsonObject.getString("message");
                        if (Arrays.asList(list).contains(3)) {
                            sendNotification(message);
                        }
                        BusHolder.get().post(new NotificationNumberEvent(badge_num + 1, def));
                        SavedData.setNotification(getApplicationContext(), badge_num + 1);
                        break;
                    case "post_complete":
                        BusHolder.get().post(new NotificationNumberEvent(badge_num, def));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(String msg) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_gocci_push)
                .setContentTitle(getString(R.string.info_gocci))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
