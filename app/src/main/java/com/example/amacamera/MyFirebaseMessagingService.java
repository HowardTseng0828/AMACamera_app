package com.example.amacamera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "1";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String Channel_id = "Notification";
            NotificationChannel notificationChannel = new NotificationChannel(Channel_id,
                    "Notification"
                    , NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
            Notification.Builder notification = new Notification.Builder(this,Channel_id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1,notification.build());
            super.onMessageReceived(remoteMessage);
        }
    }
}
