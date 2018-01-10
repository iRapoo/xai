package org.dualcom.xai.MyClass;

import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSON {

    public static String getJSON(Context context, String name, String param, String group) {

        String strJson = Storage.loadData(context, group);
        String res = "0";

        /*strJson = "{\"paramsArray: [\"first\", 100], "
                + "\"paramsObj\": {\"one\": \"two\", \"three\": \"four\"}, "
                + "\"paramsStr\": \"some string\"}";*/

        try {
            JSONParser parser = new JSONParser();

            Object obj = parser.parse(strJson);
            JSONObject jsonObj = (JSONObject) obj;

            JSONObject days = (JSONObject) jsonObj.get(name);
            res = days.get(param)+"";

        } catch (ParseException e) {
            res = "ERROR 404"; //Не найдено
        }

        return (res.length() < 5 || res.equals("Нет пары")) ? "0" : res;
    }

}
