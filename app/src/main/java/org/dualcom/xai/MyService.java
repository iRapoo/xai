package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.Notification;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.IBinder;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.Storage;


public class MyService extends Service {

    NotificationManager nm;

    @Override

    public void onCreate() {

        super.onCreate();

        //nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Получение пары
        String lesson = null;  int j = 4;
        for(int i = 0; i < 4; i++){
            final String json = JSON.getJSON(this, "day" + DATE.getWeek(), j + "-" + DATE.getWeekType(), Storage.loadData(this, "NOW_GROUP"));
            if(json != "0")
                lesson = json;
            j--;
        }
        //**************

        Context context = getApplicationContext();
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long[] vibrate = new long[] { 1000, 600000 };

        Uri selectedSoundUri = (Storage.emptyData(context,"SoundPath")) ?
                Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm) :
                Uri.parse("file://" + Storage.loadData(this, "SoundPath"));

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Скоро начнется пара")
                .setContentText(lesson.split("//")[0])
                .setTicker("Будильник")
                .setContentIntent(pendingIntent)
                .setSound(selectedSoundUri)
                .setVibrate(vibrate)
                //.setOngoing(true)
                .setSmallIcon(R.drawable.audio);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_INSISTENT;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        return super.onStartCommand(intent, flags, startId);

    }



    @Override

    public IBinder onBind(Intent arg0) {

        return null;

    }

}