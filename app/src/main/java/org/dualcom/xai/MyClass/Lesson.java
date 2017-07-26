package org.dualcom.xai.MyClass;

import android.content.Context;

public class Lesson {

    public static String first(Context context){
        int time = 0;  int j = 4;
        for(int i = 0; i < 4; i++){
            final String json = JSON.getJSON(context, "day"+DATE.getWeek(), j+"-"+DATE.getWeekType(), Storage.loadData(context, "NOW_GROUP"));
            if(json != "0")
                time = i;
            j--;
        }

        String[] less_shedul = {"13:45","11:55","9:50","8:0"};

        return less_shedul[time];
    }

    public static String tomorrow(Context context){
        int day = ((DATE.getWeek()+1) == 5) ? 0 : (DATE.getWeek()+1);

        int time = 0;  int j = 4;
        for(int i = 0; i < 4; i++){
            final String json = JSON.getJSON(context, "day"+day, j+"-"+DATE.getWeekType(), Storage.loadData(context, "NOW_GROUP"));
            if(json != "0")
                time = i;
            j--;
        }

        String[] less_shedul = {"13:45","11:55","9:50","8:0"};

        return less_shedul[time];
    }

}
