package com.ilma.testing.ilmaapp.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


import com.google.android.gms.gcm.GcmListenerService;
import com.ilma.testing.ilmaapp.Activities.AdminActivity;
import com.ilma.testing.ilmaapp.Activities.HomeActivity;
import com.ilma.testing.ilmaapp.R;

import java.util.Date;

/**
 * Created by bbb on 11/12/2017.
 */

public class GCMPushRecieverService extends GcmListenerService {

    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 0;


    @Override
    public void onMessageReceived(String s, Bundle data) {
        sendNotification1(data.getString("message"), data.getString("title"));
    }

    private void sendNotification1(String message, String title) {

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //Custom View for Notification
        /*RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.customnotification);*/
        Intent intent = null;

        //Notification Authorization
            intent = new Intent(this, AdminActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("role", "admin");
        intent.putExtra("status", "popup");
        intent.putExtra("msg", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) ((new Date().getTime() /
                1000L) % Integer.MAX_VALUE), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(getNotificationIcon())
                .setColor(Color.GREEN)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

       /* remoteViews.setTextViewText(R.id.title,message);
        remoteViews.setImageViewResource(R.id.image,R.mipmap.icon);*/

        //Notification Draging code
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(message);

        notificationBuilder.setStyle(bigTextStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify( (int) ((new Date().getTime() / 1000L) %
                Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}
