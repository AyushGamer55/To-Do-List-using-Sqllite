package com.example.todolistapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderReciever extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");

        // Initialize the NotificationManager system service
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel only once for Android O and above
        if (manager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for scheduled tasks");
            manager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Task Reminder")
                .setContentText("Don't forget: " + title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Automatically remove the notification when tapped

        // Show the notification
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
