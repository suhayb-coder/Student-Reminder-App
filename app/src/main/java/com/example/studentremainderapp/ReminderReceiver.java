package com.example.studentremainderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("TASK_TITLE");
        String description = intent.getStringExtra("TASK_DESC");
        int taskId = intent.getIntExtra("TASK_ID", 0);

        // Show Notification
        NotificationHelper.showNotification(context, title, description, taskId);

        // Mark task as completed if it's the main alarm (due now)
        if (title != null && title.startsWith("Due Now:")) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            Task task = dbHelper.getTaskById(taskId);
            if (task != null) {
                task.setCompleted(true);
                dbHelper.updateTask(task);
            }
        }

        // One-shot vibration for confirmation
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500);
        }
    }

}