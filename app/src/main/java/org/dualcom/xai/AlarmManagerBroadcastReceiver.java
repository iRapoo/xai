package org.dualcom.xai;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import org.dualcom.xai.MyClass.Lesson;
import org.dualcom.xai.MyClass.Storage;

@SuppressWarnings("deprecation")
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private static final String APP_TAG = "wakelock";
    private static PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, AlarmManagerBroadcastReceiver.APP_TAG);
        wl.acquire();

            //context.startService(new Intent(context, MyService.class));

            Intent newIntent = new Intent(context, GoAlarmActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);

        wl.release();

    }

    public void SetAlarm(Context context) {

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent p_intent = PendingIntent.getBroadcast(context, 0, intent, 0);

        String[] setLesson = Lesson.first(context).split(":");

        int h = Integer.parseInt(setLesson[0]);
        int m = Integer.parseInt(setLesson[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);

        if(!Storage.emptyData(context,"NumberPicker"))
            calendar.add(Calendar.MINUTE, -(Integer.parseInt(Storage.loadData(context, "NumberPicker"))));

        int dow = (calendar.get(Calendar.DAY_OF_WEEK)-1);
        int upDay = (dow == 5 || (calendar.get(Calendar.HOUR_OF_DAY) > 17 &&
                calendar.get(Calendar.MINUTE) > 0 &&
                        calendar.get(Calendar.SECOND) > 0) && dow == 5) ? 3 : 1;

        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            setLesson = Lesson.tomorrow(context).split(":");

            h = Integer.parseInt(setLesson[0]);
            m = Integer.parseInt(setLesson[1]);

            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_YEAR, upDay);
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.SECOND, 0);

            if(!Storage.emptyData(context,"NumberPicker"))
                calendar.add(Calendar.MINUTE, -(Integer.parseInt(Storage.loadData(context, "NumberPicker"))));
        }

        //Windows.alert(context,"",Lesson.tomorrow(context));
        //Windows.alert(context,"Значения",System.currentTimeMillis()+" - "+calendar.getTimeInMillis());

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), p_intent);

    }

    public void ReSetAlarm(Context context){
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent p_intent = PendingIntent.getBroadcast(context, 0, intent, 0);

        String[] setLesson = Lesson.tomorrow(context).split(":");

        int h = Integer.parseInt(setLesson[0]);
        int m = Integer.parseInt(setLesson[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int dow = (calendar.get(Calendar.DAY_OF_WEEK)-1);
        //int upDay = (dow == 5 || (calendar.get(Calendar.HOUR_OF_DAY) > 17) && dow == 5) ? 3 : 1;
        int upDay = (dow == 5 || (calendar.get(Calendar.HOUR_OF_DAY) > 17 &&
                calendar.get(Calendar.MINUTE) > 0 &&
                    calendar.get(Calendar.SECOND) > 0) && dow == 5) ? 3 : 1;

        calendar.add(Calendar.DAY_OF_YEAR, upDay);
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);

        if(!Storage.emptyData(context,"NumberPicker"))
            calendar.add(Calendar.MINUTE, -(Integer.parseInt(Storage.loadData(context, "NumberPicker"))));

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), p_intent);
    }

    public void SetAlarmSleep5(Context context) {

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent p_intent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), p_intent);

    }

    public void CancelAlarm(Context context) {

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(sender);

    }

}