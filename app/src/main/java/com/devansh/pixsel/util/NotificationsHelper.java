package com.devansh.pixsel.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.devansh.pixsel.R;
import com.devansh.pixsel.views.MainActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

// A Singleton
public class NotificationsHelper {

    private static final String CHANNEL_ID = "Images Channel Id";
    private static final int NOTIFICATION_ID = 123;
    private static NotificationsHelper instance;
    private Context context;

    private NotificationsHelper(Context context) {
        this.context = context;
    }

    public static NotificationsHelper getInstance(Context context) {
      if (instance == null) {
          instance = new NotificationsHelper(context);
      }
      return instance;
    }

    public void createNotification() {

        createNotificationChannel();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Bitmap icon = 
                BitmapFactory.decodeResource(context.getResources(), R.drawable.image_background);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(icon)
                .setContentTitle(context.getString(R.string.title_images_retrieved))
                .setContentText(context.getString(R.string.body_images_retrieved))
                .setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(null))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,notification);

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel),
                    importance);
            NotificationManager notificationManage = 
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManage.createNotificationChannel(channel);
        }
    }
}
