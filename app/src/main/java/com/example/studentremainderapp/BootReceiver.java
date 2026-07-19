package com.example.studentremainderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            List<Task> pendingTasks = dbHelper.getAllPendingTasks();

            for (Task task : pendingTasks) {
                AlarmHelper.scheduleTaskAlarms(context, task, false);
            }
        }
    }

}