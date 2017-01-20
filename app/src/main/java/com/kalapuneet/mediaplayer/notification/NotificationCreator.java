package com.kalapuneet.mediaplayer.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;

import com.kalapuneet.mediaplayer.MediaPlayerActivity;
import com.kalapuneet.mediaplayer.R;

/**
 * Created by puneetkkala on 20/01/17.
 */

public class NotificationCreator {
    public NotificationCreator(String key, Context context) {
        Intent openAppIntent = new Intent(context, MediaPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle("Now Playing")
                .setContentText(key)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(android.R.drawable.presence_audio_online,"OPEN APP",pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
