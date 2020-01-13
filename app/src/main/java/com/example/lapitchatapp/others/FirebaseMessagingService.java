package com.example.lapitchatapp.others;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lapitchatapp.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notification_title=remoteMessage.getNotification().getTitle();
        String notificaton_message=remoteMessage.getNotification().getBody();
        String notification_action=remoteMessage.getNotification().getClickAction();
        String fromuser=remoteMessage.getData().get("From_user_id");
        Log.d("From",fromuser);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)// this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle(notification_title)
                .setContentText(notificaton_message);

        Intent resultintent=new Intent(notification_action);
        resultintent.putExtra("userid",fromuser);
        PendingIntent resultpendindintent=PendingIntent.getActivity(this,0,resultintent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultpendindintent);
        int notification_id=(int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notificationBuilder.build());
            }
}
