package com.example.wgutermtrackerjc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    // Set an int to allow the notification to be unique every single time
    static int notificationId;
    // Set the channel_id
    String channel_id;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get channel id
        channel_id = intent.getStringExtra("channel_id");
        // Create notification channel
        createNotificationChannel(context, channel_id);
        // Customize message to be displayed in notification
        Notification notification = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_priority_high)
                .setContentText(intent.getStringExtra("key"))
                .setContentTitle("Reminder WGU Term Tracker ").build();

        // Display the notification, passing it a unique Id
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, notification);
    }

    private void createNotificationChannel(Context context, String channel_id) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String description = "Notification Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
