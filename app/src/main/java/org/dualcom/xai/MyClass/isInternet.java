package org.dualcom.xai.MyClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class isInternet {

    private static String HOST = "http://quenix.zzz.com.ua/"; //"http://rapoo.mysit.ru/";
    public static String API = HOST + "api?module=";

    public static Boolean active(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

}
