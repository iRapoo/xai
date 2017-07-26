package org.dualcom.xai.MyClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Storage {
    public static void saveData(Context context, String key, String value){
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor ed = sPref.edit();
        ed.putString(key, value);
        ed.commit();
    }
    
    public static String loadData(Context context, String key){
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String loadScores = sPref.getString(key, "");
        return loadScores;
    }

    public static boolean emptyData(Context context, String key){
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String loadScores = sPref.getString(key, "");
        boolean res = (loadScores == "" || loadScores == null || loadScores == "null" || loadScores.length() < 1) ? true : false;
        return res;
    }
}
