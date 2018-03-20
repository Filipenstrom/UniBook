package com.example.filip.unibook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Ludvig on 2018-03-14.
 */

public final class Notification{

    public String CHANNEL_ID = "1";
    Context context;
    public NotificationCompat.Builder mBuilder;
    public NotificationManagerCompat notificationManagerCompat;


    public Notification(Context c, String program, String text) {
        context = c;
        setChannel(c);
        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.schjool)
                .setContentTitle("En bok för programmet " + program + " har lagts till.")
                .setContentText("Databasteknik av Johan Lindhal")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    public void setChannel(Context c){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "UniBook";
            String description = "Favoriserad bok har lagts upp";
            //int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager =  c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
