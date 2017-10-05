package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class StartActivity extends Activity {

    public final static String FILE_NAME = "filename";

    Context context = this;
    private String group = "";
    private String group_teach = "";
    private String groups = "";
    private String schedule = "";
    private String update = "";
    private String device = "";
    private String tmp_gr = "";
    private String versionName = "";
    private String _char = "";
    private String response;
    private String G_NRESULT;
    public String UID = "";
    public String from = "0";
    public JSONObject messages;
    public JSONObject count_message;
    public ArrayAdapter<String> adapterGroup;

    public String[] data;
    public String[] data_list;
    public String[] data_teach;
    public String[] tmp_data;
    public String[] tmp_update;
    public String S_GROUP = "";
    public String[] tmp_s_group;
    public boolean flag = false;
    public int flag_gr = 0;
    private WebView mWebView;
    private ProgressBar progress;
    private static final int NOTIFY_ID = 120;
    private static final boolean BETA = false;
    private static final String url_wall = "http://rapoo.mysit.ru/api?module=news"
            +((BETA) ? "&beta=true" : ""); //URL стены

    public static String getDATA;

    ArrayAdapter<String> adapter;

    //Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo == null) return false;
        boolean res = (!activeNetworkInfo.isConnected())?false:true;
        res = (!activeNetworkInfo.isAvailable())?false:true;
        return res;
    }
    /*************************/

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Intent intent_start = new Intent(StartActivity.this, NewsActivity.class);
            startActivity(intent_start);
            this.finish();
        }else {

            if (BETA)
                Windows.Open(context, "Версия для BETA тестирования",
                        "В данной версии необходимо пронаблюдать за работоспособностью профелей преподавателей " +
                                "- при нажатии на предмет будет показан профиль, так же можно внести свои изменения или просто добавить новые данные.");

            //Получение текущей даты
            Storage.saveData(context, "YEAR", DATE.getYear() + "");
            Storage.saveData(context, "MONTH", DATE.getMonth() + "");
            Storage.saveData(context, "DAY", DATE.getDay() + "");
            /**********************/

        /*Intent intent2 = new Intent(StartActivity.this, GoAlarmActivity.class);
        startActivity(intent2);*/

            //Будильник
            //startService(new Intent(context, AlarmService.class));
            //***********

            if (Storage.emptyData(context, "WELCOME")) {
                Intent intent = new Intent(StartActivity.this, WelcomeActivity.class);
                //startActivity(intent);
            }

            //Выбор языка
        /*if(!Storage.emptyData(context, "Language")) {
            Locale locale = new Locale(Storage.loadData(context, "Language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }*/
            //**********//

            setContentView(R.layout.activity_start);

            MobileAds.initialize(getApplicationContext(), "ca-app-pub-7148094931915684/9163886655");

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            //Сообщение серверу о новом устройстве
        /*if(Storage.emptyData(context,"INFO_DEVICE") && isNetworkAvailable()) {
            try {
                device = new MyPHP().execute("device.php",
                        "UID=" + Build.SERIAL,
                        "BRAND=" + Build.BRAND,
                        "MANUFACTURER=" + Build.MANUFACTURER,
                        "PRODUCT=" + Build.PRODUCT ).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(device.equals("true"))
                Storage.saveData(context,"INFO_DEVICE",Build.SERIAL);
        }*/
            //Новый алгоритм
            if (Storage.emptyData(context, "INFO_DEVICE") && isNetworkAvailable()) {
                new MyAsyncTask().execute("device",
                        "UID=" + Build.SERIAL,
                        "BRAND=" + Build.BRAND,
                        "MANUFACTURER=" + Build.MANUFACTURER,
                        "PRODUCT=" + Build.PRODUCT);
            }
            //************//

            //Проверка ответов админа
            //Идентификация устройства
            final String SERIAL = Build.SERIAL;
            final String BRAND = Build.BRAND;
            final String MANUFACTURER = Build.MANUFACTURER;
            final String PRODUCT = Build.PRODUCT;
            UID = SERIAL + BRAND + MANUFACTURER + PRODUCT;
            //************************
            if (isNetworkAvailable())
                new GetIncorrect().execute("get_incorrect", "uid=" + UID);
            //***********************

            //Получение списка групп
            if (isNetworkAvailable()) {
                new GetGroups().execute("list_group");
                new GetTeachers().execute("list_teach");
            }
            /*************************/

            if (!isNetworkAvailable() && !Storage.emptyData(context, "S_GROUP")) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }

            //Проверка обновлений
            //Storage.saveData(context,"UPDATE",null);
            try {
                versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        /*if(isNetworkAvailable())
            new MyAsyncTask().execute("update2.php",
                    "ver=" + versionName );*/
            //************//

            final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search);
            final Button GO_LESS = (Button) findViewById(R.id.GO_LESS);
            //final Button get_groups = (Button) findViewById(R.id.get_groups);
            final ImageView about = (ImageView) findViewById(R.id.about);
            //final ImageView get_groups = (ImageView) findViewById(R.id.get_groups);
            //final Button ADD_GROUP = (Button) findViewById(R.id.ADD_GROUP);
            final LinearLayout nointernet = (LinearLayout) findViewById(R.id.nointernet);
            final TextView group_preview = (TextView) findViewById(R.id.group_preview);
            final Button open_group = (Button) findViewById(R.id.open_group);
            TextView type_week = (TextView) findViewById(R.id.type_week);

            String[] TMP_WEEK = getResources().getStringArray(R.array.TYPE_WEEK);
            String TYPE_WEEK = (DATE.getWeekType() == 0) ? TMP_WEEK[0] : TMP_WEEK[1];
            type_week.setText(TYPE_WEEK);

            if (Storage.emptyData(context, "TUTORIAL")) {

            /*TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle(getString(R.string.Welcome)).setDescription(getString(R.string.Welcomtext)))
                    .setOverlay(new Overlay())
                    .playOn(GO_LESS);*/
            }

            //final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

            //Веб форма
            mWebView = (WebView) findViewById(R.id.webView);
            mWebView.setWebChromeClient(new MyWebViewClient());
            //mWebView.setWebViewClient(new CustomWebViewClient());
            progress = (ProgressBar) findViewById(R.id.progressView);
            // включаем поддержку JavaScript
            mWebView.getSettings().setJavaScriptEnabled(true);
            // указываем страницу загрузки
            if (isNetworkAvailable()) {
                mWebView.loadUrl(url_wall);
            } else
                nointernet.setVisibility(nointernet.VISIBLE);

        /*swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        mWebView.loadUrl(url_wall);
                    }
                }, 4000);
            }
        });
        //**********/

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

            //Получение списка групп в массив
            group = (isNetworkAvailable()) ? Storage.loadData(context, "GROUPS_LIST") : Storage.loadData(context, "S_GROUP");
            data = group.split(":,");

            group_teach = (isNetworkAvailable()) ? Storage.loadData(context, "TEACH_LIST") : Storage.loadData(context, "S_GROUP");
            data_teach = group_teach.split(":,");

            if (!Storage.emptyData(context, "NOW_GROUP"))
                group_preview.setText(Storage.loadData(context, "NOW_GROUP"));
            else {
                int month = c.get(Calendar.MONTH);
                int day_of_month = c.get(Calendar.DAY_OF_MONTH);
                String[] DAY = context.getResources().getStringArray(R.array.DAYS);
                String[] monthes = context.getResources().getStringArray(R.array.MONTHES);

                group_preview.setText(monthes[month] + " " + day_of_month + ", " + DAY[DATE.getWeek()]);
            }

        /*CH_LG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        });*/

            open_group.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StartActivity.this, GetList.class);
                    intent.putExtra("type", "0");
                    startActivity(intent);
                }
            });

        /*group_preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                group_preview.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
            }
        });*/

            about.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StartActivity.this, about_app.class);
                    //startActivity(intent);
                }
            });

            tmp_s_group = Storage.loadData(context, "S_GROUP").split(":,");

        /*mWebView.setOnTouchListener(new OnSwipeTouchListener(StartActivity.this) {
            public void onSwipeLeft() {
                GoLesson(search);
            }
        });*/

            GO_LESS.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoLesson(search);
                }
            });

            //Получение списка групп
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, data);
            search.setAdapter(adapter);

            if (!Storage.emptyData(context, "NOW_GROUP"))
                search.setText(Storage.loadData(context, "NOW_GROUP"));

            if (Storage.emptyData(context, "S_GROUP") && !isNetworkAvailable()) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
            builder.setTitle(getString(R.string.no_inet1))
                    .setMessage(getString(R.string.no_inet2))
                    .setCancelable(false)
                    .setPositiveButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.exit(0);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();*/
                Windows.Open(context, getString(R.string.no_inet1), getString(R.string.no_inet2));
            }
        }
    }

    public void GoLesson(TextView search){
        try {
            int no_select = search.length();

            if (no_select > 2) {
                for (int i = 0; i < data.length; i++) {
                    if ((data[i].toLowerCase()).equals((search.getText() + "").toLowerCase())) {
                        flag_gr = 1;
                    }
                }
                for (int i = 0; i < data_teach.length; i++) {
                    if ((data_teach[i].toLowerCase()).equals((search.getText() + "").toLowerCase())) {
                        flag_gr = 1;
                    }
                }

                if (flag_gr == 1) {
                    if (isNetworkAvailable() && Storage.emptyData(context, search.getText() + "") == true) {
                        //Windows.alert(context, "test", data[1]);
                        schedule = new MyPHP().execute("schedule",
                                "group=" + search.getText()).get();

                        Storage.saveData(context, search.getText() + "", schedule);

                        for (int i = 0; i < tmp_s_group.length; i++) {
                            if (tmp_s_group[i].equals(search.getText() + "")) {
                                flag = true;
                            }
                        }

                        if (flag != true) {
                            S_GROUP = (Storage.emptyData(context, "S_GROUP")) ? ":," + search.getText() : Storage.loadData(context, "S_GROUP") + ":," + search.getText();
                            Storage.saveData(context, "S_GROUP", S_GROUP);
                        }
                    }
                    Storage.saveData(context, "NOW_GROUP", search.getText().toString());

                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    String G_Name = search.getText() + "";

                    if (G_Name.length() > 0) {
                        try {
                            G_NRESULT = new MyPHP().execute("add_group",
                                    "val=" + G_Name + "").get();

                            if (G_NRESULT.equals("1"))
                                Windows.alert(context, getString(R.string.label_GN), getString(R.string.GNTRUE));
                            else {
                                Intent intent = new Intent(StartActivity.this, windows.class);
                                    intent.putExtra("title", getString(R.string.label_GN));
                                    intent.putExtra("text", getString(R.string.GNFALE));
                                    intent.putExtra("yes_btn", getString(R.string.open_list));
                                startActivityForResult(intent, 2);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else
                        Windows.alert(context, getString(R.string.label_GN), getString(R.string.GNCLEAR));
                }
            } else {
                Intent intent = new Intent(StartActivity.this, windows.class);
                    intent.putExtra("title", getString(R.string.no_inet1));
                    intent.putExtra("text", getString(R.string.no_group));
                    intent.putExtra("yes_btn", getString(R.string.open_list));
                startActivityForResult(intent, 2);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2: //Выбор группы
                    if(!isNetworkAvailable())
                        Windows.Open(context, getString(R.string.no_inet1), getString(R.string.no_inet2));
                    else {
                        Intent intent = new Intent(StartActivity.this, GetList.class);
                        intent.putExtra("type", "0");
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //do whatever you want with the url that is clicked inside the webview.
            //for example tell the webview to load that url.
            view.loadUrl(url);
            //return true if this method handled the link event
            //or false otherwise
            return true;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        StartActivity.super.onBackPressed();
        moveTaskToBack(true);
        StartActivity.super.onBackPressed();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/api?module=";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

                if(response.equals("true"))
                    Storage.saveData(context,"INFO_DEVICE",Build.SERIAL);

                /*tmp_update = response.split(",");

                if((tmp_update[0]+"").equals("true")){
                    Storage.saveData(context,"UPDATE",tmp_update[1]+"");
                    startService(new Intent(context, MyService.class));
                }*/

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetGroups extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/api?module=";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

                Storage.saveData(context,"GROUPS_LIST",response);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetTeachers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/api?module=";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

                Storage.saveData(context,"TEACH_LIST",response);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetIncorrect extends AsyncTask<String, String, String> {

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/api?module=";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

            }catch(Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        @SuppressLint("NewApi")
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if(!res.equals("false")) {

                JSONParser parser = new JSONParser();

                Object obj = null;
                try {
                    obj = parser.parse(res);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObj = (JSONObject) obj;

                count_message = (JSONObject) jsonObj.get("count");
                int _count = Integer.parseInt(count_message.get("value").toString());

                messages = (JSONObject) jsonObj.get("message" + (_count-1));
                from = messages.get("from") + "";

                if(!(_count+"").equals(Storage.loadData(context,"incorrectCount")) &&
                        from.equals("admin")){

                    Intent notificationIntent = new Intent(context, incorrect.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context,
                            0, notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    Resources res2 = context.getResources();
                    Notification.Builder builder = new Notification.Builder(context);

                    builder.setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.notific_response)
                            // большая картинка
                            .setLargeIcon(BitmapFactory.decodeResource(res2, R.drawable.incorrect_response))
                            //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                            .setTicker(getString(R.string.inc_notific_title))
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                            .setContentTitle(getString(R.string.inc_notific_title))
                            //.setContentText(res.getString(R.string.notifytext))
                            .setContentText(getString(R.string.inc_notific_text)); // Текст уведомления

                    // Notification notification = builder.getNotification(); // до API 16
                    Notification notification = builder.build();

                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFY_ID, notification);

                }

            }
        }
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            StartActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);

        if(progress == 100){ //По окончанию загрузки пропадает
            this.progress.setVisibility(View.GONE);
        }
    }

}
