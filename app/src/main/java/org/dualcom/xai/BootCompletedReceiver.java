package org.dualcom.xai;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.dualcom.xai.MyClass.Storage;

public class BootCompletedReceiver extends BroadcastReceiver {
    public BootCompletedReceiver() {
    }
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) &&
                (Storage.loadData(context, "SWITCH").equals("true"))) {

            AlarmManagerBroadcastReceiver alarm;
            alarm = new AlarmManagerBroadcastReceiver();
            alarm.SetAlarm(context);

        }
    }
}
