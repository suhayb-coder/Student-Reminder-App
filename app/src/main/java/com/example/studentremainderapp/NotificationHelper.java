package com.example.studentremainderapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "task_reminder_channel_v2";
    public static final String CHANNEL_NAME = "Student Task Reminders";

    public static void showNotification(Context context, String title, String message, int taskId) {
        SessionManager sessionManager = new SessionManager(context);
        if (!sessionManager.isNotificationsEnabled()) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get user chosen sound
        String soundUriStr = sessionManager.getNotificationSound();
        Uri alarmSound;
        if (soundUriStr != null) {
            alarmSound = Uri.parse(soundUriStr);
        } else {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        // Create Channel for Android O+
        // We append the sound string to channel ID to ensure a "new" channel is created if the sound changes,
        // as channel properties (like sound) are immutable after creation.
        String dynamicChannelId = CHANNEL_ID + (soundUriStr != null ? soundUriStr.hashCode() : "default");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(dynamicChannelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for task reminders");
            channel.enableVibration(true);
            
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            channel.setSound(alarmSound, audioAttributes);
            
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to open app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("TASK_ID", taskId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, dynamicChannelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(taskId, builder.build());
    }

}