package com.delipackport.delipackmobi.Model;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.delipackport.delipackmobi.R;

public class CustomerLocalPushNotification {

    private Context context;
    NotificationCompat.Builder builder;
    private String notificationChannelID = "delivertnotifications";
    private int notificationID = 001;

    @TargetApi(Build.VERSION_CODES.O)
    public CustomerLocalPushNotification(Context context){
        this.context = context;
    }

    public void publishNotification(String notificationTitle, String [] notificationMessages, String notificationMessage){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannelID);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        builder.setSmallIcon(R.drawable.deli_s);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationMessage);
        Intent notificationIntent = new Intent(context, context.getClass());

        if (notificationMessages != null){
            for (int i = 0; i < notificationMessages.length; i++){
                inboxStyle.addLine(notificationMessages[i]);
            }
            builder.setStyle(inboxStyle);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel  deliveryNotificationChannel = new NotificationChannel(notificationChannelID, "deliveries", NotificationManager.IMPORTANCE_DEFAULT);
            deliveryNotificationChannel.setLightColor(Color.parseColor("#1565c0"));
//            deliveryNotificationChannel.setShowBadge(true);
            deliveryNotificationChannel.enableLights(true);
            deliveryNotificationChannel.enableVibration(true);
            deliveryNotificationChannel.setDescription("Deliveries description");
            deliveryNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setChannelId(notificationChannelID);
            notificationManager.createNotificationChannel(deliveryNotificationChannel);
            notificationManager.notify(notificationID, builder.build());

            System.out.println("In something notification");

        } else {
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setVibrate(new long[]{0,100,1000,1000,1000,100,100});
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());
        }


    }
}
