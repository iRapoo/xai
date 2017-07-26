package org.dualcom.xai.MyClass;

import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Created by Виталий on 05.02.2015.
 */
public class JSON {

    public static String getJSON(Context context, String name, String param, String group) {
        String strJson = Storage.loadData(context, group).toString()+"";
        //Storage.loadData(context, "NOW_GROUP")
        String res = "0";

        /*strJson = "{\"paramsArray: [\"first\", 100], "
                + "\"paramsObj\": {\"one\": \"two\", \"three\": \"four\"}, "
                + "\"paramsStr\": \"some string\"}";*/

        //Windows.alert(context,name,param);

        try {
            JSONParser parser = new JSONParser();

            Object obj = parser.parse(strJson);
            JSONObject jsonObj = (JSONObject) obj;

            JSONObject days = (JSONObject) jsonObj.get(name);
            res = days.get(param)+"";
            //JSONObject day = days.getJSONObject(0);
            //Windows.alert(context,"Ошибка!!!",days.get(param)+"");
            /*for (int i = 0; i < days.length(); i++) {
                day = days.getJSONObject(i);

                //JSONObject first = day.getJSONObject("3");

                res = day.getString(param);
                
            }*/

        } catch (ParseException e) {
            res = "ERROR 404"; //Не найдено
            //Windows.alert(context,"ERROR 404",e.toString());
        }

        return (res.length() < 5 || res.equals("Нет пары")) ? "0" : res;
    }

}
