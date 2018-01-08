package org.dualcom.xai.MyClass;

import com.google.android.gms.maps.model.LatLng;
import org.dualcom.xai.R;

public class LIST {

    public static int TOP(int value){
        int list[] = {
                R.id.TOP1,R.id.TOP2,R.id.TOP3,R.id.TOP4,R.id.TOP5,R.id.TOP6,R.id.TOP7, R.id.TOP8,
                R.id.TOP9,R.id.TOP10,R.id.TOP11,R.id.TOP12,R.id.TOP13,R.id.TOP14,R.id.TOP15,R.id.TOP16,
                R.id.TOP17,R.id.TOP18,R.id.TOP19,R.id.TOP20
        };

        return list[value];
    }

    public static int BOT(int value){
        int list[] = {
                R.id.BOT1,R.id.BOT2,R.id.BOT3,R.id.BOT4,R.id.BOT5,R.id.BOT6,R.id.BOT7,R.id.BOT8,
                R.id.BOT9,R.id.BOT10,R.id.BOT11,R.id.BOT12,R.id.BOT13,R.id.BOT14,R.id.BOT15,R.id.BOT16,
                R.id.BOT17,R.id.BOT18,R.id.BOT19,R.id.BOT20
        };

        return list[value];
    }

    public static int TOPt(int value){
        int list[] = {
                R.id.TOPt1,R.id.TOPt2,R.id.TOPt3,R.id.TOPt4,R.id.TOPt5,R.id.TOPt6,R.id.TOPt7,R.id.TOPt8,
                R.id.TOPt9,R.id.TOPt10,R.id.TOPt11,R.id.TOPt12,R.id.TOPt13,R.id.TOPt14,R.id.TOPt15,R.id.TOPt16,
                R.id.TOPt17,R.id.TOPt18,R.id.TOPt19,R.id.TOPt20
        };

        return list[value];
    }

    public static int BOTt(int value){
        int list[] = {
                R.id.BOTt1,R.id.BOTt2,R.id.BOTt3,R.id.BOTt4,R.id.BOTt5,R.id.BOTt6,R.id.BOTt7,R.id.BOTt8,
                R.id.BOTt9,R.id.BOTt10,R.id.BOTt11,R.id.BOTt12,R.id.BOTt13,R.id.BOTt14,R.id.BOTt15,R.id.BOTt16,
                R.id.BOTt17,R.id.BOTt18,R.id.BOTt19,R.id.BOTt20
        };

        return list[value];
    }

    public static int times(int value){
        int list[] = {
                R.id.time1,R.id.time2,R.id.time3,R.id.time4,R.id.time5,R.id.time6,R.id.time7,R.id.time8
        };

        return list[value];
    }

    public static int times_draw_def(int value){
        int list[] = {
                R.drawable.time1_top,R.drawable.time1_bot,R.drawable.time2_top,R.drawable.time2_bot,
                R.drawable.time3_top,R.drawable.time3_bot,R.drawable.time4_top,R.drawable.time4_bot
        };

        return list[value];
    }

    public static int times_draw(int value){
        int list[] = {
                R.drawable.time1_top_sel,R.drawable.time1_bot_sel,R.drawable.time2_top_sel,R.drawable.time2_bot_sel,
                R.drawable.time3_top_sel,R.drawable.time3_bot_sel,R.drawable.time4_top_sel,R.drawable.time4_bot_sel
        };

        return list[value];
    }

    /*public static int LESSON(int value){
        int list[] = {
                R.id.lesson1,R.id.lesson2,R.id.lesson3,R.id.lesson4
        };

        return list[value];
    }*/

    public static int getHousing(String line){
        int value = 7; //Манеж default

        String housing[] = {
                " лк. ", " г. ", " с ", " к-2 ", " р. ", " м. ", " им. "
        };

        for(int i = 0; i < housing.length; i++){
            if(line.contains(housing[i]))
                value = i;
        }

        return value;
    }

    public static LatLng getLatLng(String line){
        int value = getHousing(line);

        LatLng latLng[] = {
                new LatLng(50.043368, 36.289603), new LatLng(50.042790, 36.284777), new LatLng(50.041822, 36.285847),
                new LatLng(50.044507, 36.286963), new LatLng(50.042882, 36.291340), new LatLng(50.043269, 36.287467),
                new LatLng(50.044248, 36.291805), /*Манеж*/new LatLng(50.045120, 36.283847)/**/
        };
        return latLng[value];
    }

}
