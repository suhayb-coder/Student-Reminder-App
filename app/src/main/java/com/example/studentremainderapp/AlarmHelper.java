package com.example.studentremainderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.Calendar;

public class AlarmHelper {

    public static void scheduleTaskAlarms(Context context, Task task) {
        try {
            String[] d = task.getDueDate().split("-");
            String[] t = task.getDueTime().split(":");
            
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[2]),
                    Integer.parseInt(t[0]), Integer.parseInt(t[1]), 0);

            long dueTimeMillis = dueCalendar.getTimeInMillis();
            long currentTimeMillis = System.currentTimeMillis();

            // 1. Main Alarm (At due time)
            if (dueTimeMillis > currentTimeMillis) {
                setAlarm(context, task, dueTimeMillis, task.getId(), "Due Now: " + task.getTitle());
            }

            // 2. Reminder 1 day before
            Calendar oneDayBefore = (Calendar) dueCalendar.clone();
            oneDayBefore.add(Calendar.DAY_OF_YEAR, -1);
            if (oneDayBefore.getTimeInMillis() > currentTimeMillis) {
                setAlarm(context, task, oneDayBefore.getTimeInMillis(), task.getId() * 10 + 1, "Reminder (1 day left): " + task.getTitle());
            }

            // 3. Reminder 2 days before
            Calendar twoDaysBefore = (Calendar) dueCalendar.clone();
            twoDaysBefore.add(Calendar.DAY_OF_YEAR, -2);
            if (twoDaysBefore.getTimeInMillis() > currentTimeMillis) {
                setAlarm(context, task, twoDaysBefore.getTimeInMillis(), task.getId() * 10 + 2, "Reminder (2 days left): " + task.getTitle());
            }

            Toast.makeText(context, "Reminders scheduled successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to schedule reminders", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setAlarm(Context context, Task task, long timeMillis, int requestCode, String customTitle) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("TASK_TITLE", customTitle);
        intent.putExtra("TASK_DESC", task.getDescription());
        intent.putExtra("TASK_ID", task.getId());

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi);
        }
    }

    public static void cancelTaskAlarms(Context context, int taskId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        int[] requestCodes = {taskId, taskId * 10 + 1, taskId * 10 + 2};
        for (int rc : requestCodes) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, rc, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi);
        }
    }
}