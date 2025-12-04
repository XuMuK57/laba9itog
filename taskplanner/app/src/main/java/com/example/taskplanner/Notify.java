package com.example.taskplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notify {
    public static void send(Context c, String text) {
        String id = "task_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    id, "Tasks", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager nm = c.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(c, id)
                        .setContentTitle("Напоминание")
                        .setContentText(text)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyId = (int) (System.currentTimeMillis() & 0x7fffffff);
        if (nm != null) nm.notify(notifyId, b.build());
    }
}
