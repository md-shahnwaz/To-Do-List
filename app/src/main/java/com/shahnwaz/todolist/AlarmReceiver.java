package com.shahnwaz.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("task_title");
        showNotification(context, taskTitle);
//        playAlarm(context);
    }

    private void showNotification(Context context, String taskTitle) {
        // Build your notification here
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "todo_channel")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Task Reminder")
                .setContentText(taskTitle + " is due tomorrow!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(1, builder.build());
    }

//    private void playAlarm(Context context) {
//        // Play custom audio here (example)
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
//        mediaPlayer.start();
//    }
}
