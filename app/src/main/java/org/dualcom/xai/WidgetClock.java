package org.dualcom.xai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Lesson;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WidgetClock extends AppWidgetProvider {

    public static String group = null;
    public static SharedPreferences sp;
    private PendingIntent service = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, WidgetUpdateServiceClock.class);
        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        manager.setRepeating(AlarmManager.RTC,startTime.getTime().getTime(),30000, service);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        //stopTimer();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        // Удаляем Preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_GROUP + widgetID);
        }
        editor.apply();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_clock);

        //обновляем все экземпляры
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, sp, id, remoteViews);
        }
        //******************

    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp, int widgetID, RemoteViews remoteViews) {

        Intent intent = new Intent(context, ScheduleActivity.class);
        intent.setAction(group);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, pending);

        // Читаем параметры Preferences
        WidgetToday.sp = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        group = WidgetToday.sp.getString(ConfigActivity.WIDGET_GROUP + widgetID, null);
        if (group == null) return;

        //Получение пары
        String lesson = null; int num = 0;  int j;
        int getWeek; int weekType; String json;
        for(int u = 0; u < 5; u++){ j = 1; num = 0;
            for(int i = 0; i < 4; i++){
                getWeek = ((DATE.getNowTimeLite()+1+u)>i) ? ((DATE.getWeek()+u > 5) ? 1 : (DATE.getWeek()+u)) : DATE.getWeek();
                weekType = ((DATE.getNowTimeLite()+1+u)>i && getWeek == 5) ? ((DATE.getWeekType() == 0) ? 1 : 0) : DATE.getWeekType();
                json = JSON.getJSON(context, "day"+getWeek, j+"-"+weekType, group);

                j++;

                if(json=="0") continue;

                if(json != "0" && ((DATE.getNowTimeLite()>2) || (DATE.getNowTimeLite()==-1))){
                    lesson = json;
                    //num = j;
                    break;
                }

                if(json != "0" && (DATE.getNowTimeLite()+1+u)==i && (DATE.getNowTimeLite()+1+u) < i+1) {
                    lesson = json;
                    //num = j;
                }

            }

            if(lesson!=null) break;
        }
        //**************

        /*DateFormat formatter = new SimpleDateFormat("hh-mm");
        Date date = null;
        try {
            date = (Date) formatter.parse(Lesson.times(num-1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = System.currentTimeMillis() - date.getTime();
        long minute = (diff / (1000 * 60)) % 60;
        long hour = (diff / (1000 * 60 * 60)) % 24;
        String TIME = String.format("%02d:%02d", hour, minute);*/

        remoteViews.setTextViewText(R.id.textGroup, group);
        remoteViews.setTextViewText(R.id.textLesson, lesson.split("//")[0]);
        remoteViews.setTextViewText(R.id.textTeacher, lesson.split("//")[1]);
        remoteViews.setTextViewText(R.id.textAfter, "Начнется через " + DATE.getWeek() + DATE.getNowTimeLite() + " / " + Lesson.times(num-1));
        appWidgetManager.updateAppWidget(widgetID, remoteViews);
    }

}

