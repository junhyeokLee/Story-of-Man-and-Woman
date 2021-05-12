package com.dev_sheep.story_of_man_and_woman.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.dev_sheep.story_of_man_and_woman.R;
import com.dev_sheep.story_of_man_and_woman.view.activity.SignUpActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("FCM Log", "Refreshed token: " + token);
//        String TOPICK_Subscriber = "subscriber";
//        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
//        firebaseMessaging.subscribeToTopic(TOPICK_Subscriber);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences preferences_push = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        if (remoteMessage.getNotification() != null) {

                Log.d("FCM Log", "알림 메시지: " + remoteMessage.getNotification().getBody());
                String messageBody = remoteMessage.getNotification().getBody();
                String messageTitle = remoteMessage.getNotification().getTitle();
                Intent intent = new Intent(this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                String channelId = "Channel ID";
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(messageTitle)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelName = "Channel Name";
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
        }
    }


}