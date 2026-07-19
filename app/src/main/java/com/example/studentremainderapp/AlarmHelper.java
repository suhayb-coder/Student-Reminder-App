package com.example.studentremainderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.Calendar;

public class AlarmHelper {

    public static void scheduleTaskAlarms(Context context, Task task, boolean showToast) {
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
                setAlarm(context, task, dueTimeMillis, task.getId(), context.getString(R.string.due_now_msg, task.getTitle()), true);
            }

            // 2. Reminder 1 day before
            Calendar oneDayBefore = (Calendar) dueCalendar.clone();
            oneDayBefore.add(Calendar.DAY_OF_YEAR, -1);
            if (oneDayBefore.getTimeInMillis() > currentTimeMillis) {
                setAlarm(context, task, oneDayBefore.getTimeInMillis(), task.getId() * 10 + 1, context.getString(R.string.reminder_1day, task.getTitle()), false);
            }

            // 3. Reminder 2 days before
            Calendar twoDaysBefore = (Calendar) dueCalendar.clone();
            twoDaysBefore.add(Calendar.DAY_OF_YEAR, -2);
            if (twoDaysBefore.getTimeInMillis() > currentTimeMillis) {
                setAlarm(context, task, twoDaysBefore.getTimeInMillis(), task.getId() * 10 + 2, context.getString(R.string.reminder_2days, task.getTitle()), false);
            }

            // 4. Reminder 3 days before
            Calendar threeDaysBefore = (Calendar) dueCalendar.clone();
            threeDaysBefore.add(Calendar.DAY_OF_YEAR, -3);
            if (threeDaysBefore.getTimeInMillis() > currentTimeMillis) {
                setAlarm(context, task, threeDaysBefore.getTimeInMillis(), task.getId() * 10 + 3, context.getString(R.string.reminder_3days, task.getTitle()), false);
            }

            if (showToast) {
                Toast.makeText(context, R.string.reminders_scheduled, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (showToast) {
                Toast.makeText(context, R.string.reminders_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void setAlarm(Context context, Task task, long timeMillis, int requestCode, String customTitle, boolean isFinal) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("TASK_TITLE", customTitle);
        intent.putExtra("TASK_DESC", task.getDescription());
        intent.putExtra("TASK_ID", task.getId());
        intent.putExtra("IS_FINAL", isFinal);

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

        int[] requestCodes = {taskId, taskId * 10 + 1, taskId * 10 + 2, taskId * 10 + 3};
        for (int rc : requestCodes) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, rc, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi);
        }
    }
}