package org.dualcom.xai;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.Storage;

@SuppressWarnings("deprecation")
public class GoAlarmActivity extends Activity {

    final String LOG_TAG = "myLogs";
    Context context = this;

    private AlarmManagerBroadcastReceiver alarm;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_go_alarm);

        final Button AlarmOff = (Button) findViewById(R.id.AlarmOff);
        final RelativeLayout Sleep5 = (RelativeLayout) findViewById(R.id.Sleep5);
        final TextView firstLesson = (TextView) findViewById(R.id.firstLesson);
        final TextView firstTeacher = (TextView) findViewById(R.id.firstTeacher);
        alarm = new AlarmManagerBroadcastReceiver();

        //SoundPlay.start(context);

        //Получение пары
        String lesson = null;  int j = 4;
        for(int i = 0; i < 4; i++){
            final String json = JSON.getJSON(context, "day"+ DATE.getWeek(), j+"-"+DATE.getWeekType(), Storage.loadData(context, "NOW_GROUP"));
            if(json != "0")
                lesson = json;
            j--;
        }
        //**************

        firstLesson.setText(lesson.split("//")[0]);
        firstTeacher.setText(lesson.split("//")[1]);

        context.startService(new Intent(context, MyService.class));

        AlarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.CancelAlarm(context);
                alarm.ReSetAlarm(context);

                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
                onExitAlarm(context);

                context.stopService(new Intent(context, MyService.class));

                Toast.makeText(context, getString(R.string.alarm_off), Toast.LENGTH_SHORT).show();
            }
        });

        Sleep5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.CancelAlarm(context);
                alarm.SetAlarmSleep5(context);

                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
                onExitAlarm(context);

                context.stopService(new Intent(context, MyService.class));

                Toast.makeText(context, getString(R.string.realarm), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void onExitAlarm(Context context) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("OnAlarm", "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

}
