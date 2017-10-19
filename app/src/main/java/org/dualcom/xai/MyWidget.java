package org.dualcom.xai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.LIST;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MyWidget extends AppWidgetProvider {

    public static String group = null;
    public static SharedPreferences sp;
    private PendingIntent service = null;
    //Timer myTimer = new Timer();

    //Применение ситля приложения
    public static int colors[] = {
            Color.argb(200, 55, 71, 79),Color.argb(200, 150, 45, 34),Color.argb(200, 16, 115, 96),Color.argb(200, 160, 64, 0),
            Color.argb(200, 102, 114, 115),Color.argb(200, 112, 54, 136),Color.argb(200, 200, 127, 10),Color.argb(200, 34, 48, 61)
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, WidgetUpdateService.class);
        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        manager.setRepeating(AlarmManager.RTC,startTime.getTime().getTime(),30000,service);
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
        Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_GROUP + widgetID);
        }
        editor.commit();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        //обновляем все экземпляры
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, sp, id, remoteViews);
            //TimeLine(context, appWidgetManager, sp, id, remoteViews);
        }
        //******************

    }

    /*static void TimeLine(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp, int widgetID, RemoteViews remoteViews){

        for(int i = 0; i < 8; i++)
            remoteViews.setInt(LIST.times(i), "setBackgroundResource", LIST.times_draw_def(i));
        if(DATE.getNowTime()>-1) {
            remoteViews.setInt(LIST.times(DATE.getNowTime()), "setBackgroundResource", LIST.times_draw(DATE.getNowTime()));
        }
    }*/

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp, int widgetID, RemoteViews remoteViews) {

        // Читаем параметры Preferences
        MyWidget.sp = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        group = MyWidget.sp.getString(ConfigActivity.WIDGET_GROUP + widgetID, null);
        if (group == null) return;

        //RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setViewVisibility(R.id.loadWidget, View.GONE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(group.toString());
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, pending);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);
        int dow = (c.get(Calendar.DAY_OF_WEEK)-1);

        //TIME LINE******************************
        for(int i = 0; i < 8; i++)
            remoteViews.setInt(LIST.times(i), "setBackgroundResource", LIST.times_draw_def(i));
        if(DATE.getNowTime()>-1) {
            remoteViews.setInt(LIST.times(DATE.getNowTime()), "setBackgroundResource", LIST.times_draw(DATE.getNowTime()));
        }
        //***************************************

        for(int i = 1; i < 5; i++){
            final String json_t = JSON.getJSON(context, "day"+DATE.getWeek(), i + "-0", group);
            final String json_b = JSON.getJSON(context, "day"+DATE.getWeek(), i+"-1", group);

            remoteViews.setTextViewText(LIST.TOPt(i - 1), context.getResources().getString(R.string.no_less));
            remoteViews.setTextViewText(LIST.BOTt(i - 1), context.getResources().getString(R.string.no_less));
            remoteViews.setInt(LIST.TOP(i - 1),"setBackgroundResource", android.R.color.transparent);
            remoteViews.setInt(LIST.BOT(i - 1), "setBackgroundResource", android.R.color.transparent);

            if(json_t != "0") {
                remoteViews.setTextViewText(LIST.TOPt(i - 1), json_t.split("//")[0]);
            }

            if (json_b != "0") {
                remoteViews.setTextViewText(LIST.BOTt(i - 1), json_b.split("//")[0]);
            }

            if(json_t.equals(json_b))
                remoteViews.setViewVisibility(LIST.BOT(i - 1),View.GONE);
                //((LinearLayout) view.findViewById(LIST.BOT(i - 1))).setVisibility(View.GONE);
            else{
                remoteViews.setViewVisibility(LIST.BOT(i - 1),View.VISIBLE);

                if(DATE.getWeekType() == 0)
                    remoteViews.setInt(LIST.TOP(i - 1),"setBackgroundResource",R.drawable.less_now);
                    //((LinearLayout) view.findViewById(LIST.TOP(i - 1))).setBackgroundResource(R.drawable.less_now);
                else
                    remoteViews.setInt(LIST.BOT(i - 1), "setBackgroundResource", R.drawable.less_now);
                    //((LinearLayout) view.findViewById(LIST.BOT(i - 1))).setBackgroundResource(R.drawable.less_now);
            }

        }

        String[] DAY = context.getResources().getStringArray(R.array.DAYS);
        String tmp_ml = context.getResources().getString(R.string.SELECT);
        String[] TMP_WEEK = context.getResources().getStringArray(R.array.TYPE_WEEK);
        String TYPE_WEEK = (DATE.getWeekType() == 0) ? TMP_WEEK[0] : TMP_WEEK[1];

        //Тикущее число
        int month = c.get(Calendar.MONTH);
        switch (dow){

            case 5: //Пятница +3 дня
                if(c.get(Calendar.HOUR_OF_DAY) > 16)
                    c.add(Calendar.DAY_OF_MONTH,3);
                break;
            case 6: //Суббота +2 дня
                c.add(Calendar.DAY_OF_MONTH,2);
                break;
            case 0: //Воскресенье +1 день
                c.add(Calendar.DAY_OF_MONTH,1);
                break;
            default: //Остальные дни
                if(c.get(Calendar.HOUR_OF_DAY) > 16)
                    c.add(Calendar.DAY_OF_MONTH,1);
                break;

        }
        int day_of_month = c.get(Calendar.DAY_OF_MONTH);
        String[] monthes = context.getResources().getStringArray(R.array.MONTHES);
        //************//

        remoteViews.setTextViewText(R.id.LabelGroup, group);
        remoteViews.setTextViewText(R.id.LabelType, TYPE_WEEK);
        remoteViews.setTextViewText(R.id.LabelDate, monthes[month]+" "+day_of_month+", "+DAY[DATE.getWeek()]);
        remoteViews.setTextViewText(R.id.LabelWeek, DATE.getStudWeek() + context.getResources().getString(R.string.stud_week));
        //remoteViews.setTextViewText(R.id.LabelWidget, tmp_ml + ": " + group + " | " + TYPE_WEEK + " | " + DAY[DATE.getWeek()]);
        appWidgetManager.updateAppWidget(widgetID, remoteViews);
    }

}