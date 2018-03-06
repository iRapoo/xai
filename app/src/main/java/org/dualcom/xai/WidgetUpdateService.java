package org.dualcom.xai;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

public class WidgetUpdateService extends Service {

    public static SharedPreferences sp;

    public WidgetUpdateService() {
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        updateInfoWidget();
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateInfoWidget()
    {//Обновление виджета
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int ids[] = appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext().getPackageName(), WidgetToday.class.getName()));
        //int ids2[] = appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext().getPackageName(), MyWidget2.class.getName()));
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_today);
        for (int id : ids) {
            WidgetToday.updateWidget(this.getApplicationContext(),appWidgetManager, sp, id, remoteViews);
        }
        //MyWidget2.updateWidget(this.getApplicationContext(),appWidgetManager, sp, ids2, remoteViews);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
