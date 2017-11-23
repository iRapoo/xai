package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.startapp.android.publish.adsCommon.StartAppSDK;

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

public class NewsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public Context context = this;
    public CollapsingToolbarLayout toolbarLayout;
    public TextView startDate;
    public TextView startType;
    public FloatingActionButton fab;
    public AutoCompleteTextView search;
    public AppBarLayout appBar;

    private WebView mWebView;
    private ProgressBar progress;
    private LinearLayout nointernet;

    private String group = "";
    private String group_teach = "";
    private String schedule = "";
    private String G_NRESULT;
    public String[] data;
    public String[] data_teach;
    public String UID = "";
    private String response;
    public String from = "0";
    public JSONObject messages;
    public JSONObject count_message;
    public ArrayAdapter<String> adapterGroup;
    private static final int NOTIFY_ID = 120;
    public boolean flag = false;
    public int flag_gr = 0;
    public String S_GROUP = "";
    public String[] tmp_s_group;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String url_wall = "http://rapoo.mysit.ru/api?module=news"; //URL стены

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*MobileAds.initialize(getApplicationContext(), "ca-app-pub-7148094931915684/9163886655");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        //StartAppSDK.init(context, appID, false);
        //StartAppAd.showAd(context);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.top_color, R.color.main_color, R.color.extra_color);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        startDate = (TextView) findViewById(R.id.start_date);
        startType = (TextView) findViewById(R.id.start_type);
        nointernet = (LinearLayout) findViewById(R.id.nointernet);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        search = (AutoCompleteTextView) findViewById(R.id.search);
        appBar = (AppBarLayout) findViewById(R.id.app_bar);

        switch (DATE.getNowTime()){

            case 0:
                toolbarLayout.setBackgroundResource(R.drawable.morning);
                break;
            case 1:
                toolbarLayout.setBackgroundResource(R.drawable.morning);
                break;
            case 2:
                toolbarLayout.setBackgroundResource(R.drawable.morning);
                break;
            case 3:
                toolbarLayout.setBackgroundResource(R.drawable.morning);
                break;
            case 4:
                toolbarLayout.setBackgroundResource(R.drawable.sun);
                break;
            case 5:
                toolbarLayout.setBackgroundResource(R.drawable.sun);
                break;
            case 6:
                toolbarLayout.setBackgroundResource(R.drawable.sun);
                break;
            case 7:
                toolbarLayout.setBackgroundResource(R.drawable.sun);
                break;
            default:
                toolbarLayout.setBackgroundResource(R.drawable.evening);
                break;
        }

        toolbarLayout.setBackgroundResource(R.drawable.header);

        //aq.id(R.id.ImageBg).image(HOST+"getImage.php", true, false, 0, R.color.main_color, null, AQuery.FADE_IN);

        //Отправка информации о новом устройстве
        if (Storage.emptyData(context, "INFO_DEVICE") && isNetworkAvailable()) {
            new MyAsyncTask().execute("device",
                    "UID=" + Build.SERIAL,
                    "BRAND=" + Build.BRAND,
                    "MANUFACTURER=" + Build.MANUFACTURER,
                    "PRODUCT=" + Build.PRODUCT);
        }
        //**************************************

        //Проверка ответов админа
        //Идентификация устройства
        final String SERIAL = Build.SERIAL;
        final String BRAND = Build.BRAND;
        final String MANUFACTURER = Build.MANUFACTURER;
        final String PRODUCT = Build.PRODUCT;
        UID = SERIAL+BRAND+MANUFACTURER+PRODUCT;
        //************************
        /*if (isNetworkAvailable())
            new GetIncorrect().execute("get_incorrect.php", "uid=" + UID);*/
        //***********************

        //Получение списка групп
        if (isNetworkAvailable()) {
            new GetGroups().execute("list_group");
            new GetTeachers().execute("list_teach");
        }
        //***********************

        if(!isNetworkAvailable() && !Storage.emptyData(context, "S_GROUP")){
            Intent intent = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(intent);
        }

        group = (isNetworkAvailable()) ? Storage.loadData(context, "GROUPS_LIST") : Storage.loadData(context, "S_GROUP");
        data = group.split(":,");

        group_teach = (isNetworkAvailable()) ? Storage.loadData(context, "TEACH_LIST") : Storage.loadData(context, "S_GROUP");
        data_teach = group_teach.split(":,");

        getStartInfo(); //Инициализация получения первичных данных

        if (Storage.emptyData(context, "TUTORIAL")) {

            ViewTarget target = new ViewTarget(R.id.fab, this);
            new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle(getString(R.string.Welcome))
                    .setContentText(getString(R.string.Welcomtext))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .build();

            /*TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle(getString(R.string.Welcome)).setDescription(getString(R.string.Welcomtext)))
                    .setOverlay(new Overlay())
                    .playOn(fab);*/
        }

        //Веб форма
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebChromeClient(new NewsActivity.MyWebViewClient());
        //mWebView.setWebViewClient(new CustomWebViewClient());
        progress = (ProgressBar) findViewById(R.id.progressView);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        if (isNetworkAvailable()) {
            mWebView.loadUrl(url_wall);
        }else
            nointernet.setVisibility(nointernet.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, data);
        search.setAdapter(adapter);
        if(!Storage.emptyData(context, "NOW_GROUP"))
            search.setText(Storage.loadData(context, "NOW_GROUP"));

        if(Storage.emptyData(context, "S_GROUP") && !isNetworkAvailable()){
            Windows.Open(context, getString(R.string.no_inet1), getString(R.string.no_inet2));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Storage.emptyData(context, "TUTORIAL"))
                    Storage.saveData(context, "TUTORIAL", "true");

                GoLesson(search);

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    void getStartInfo(){
        /*if(!Storage.emptyData(context,"NOW_GROUP"))
            toolbarLayout.setTitle(Storage.loadData(context, "NOW_GROUP"));
        else*/
            toolbarLayout.setTitle("      " + getString(R.string.news_label));

        //Установка даты и типа недели
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

        int dow = (c.get(Calendar.DAY_OF_WEEK)-1);
        int month = c.get(Calendar.MONTH);

        switch (dow){

            case 5: //Пятница +3 дня
                if(c.get(Calendar.HOUR_OF_DAY) > 16)
                    c.add(Calendar.DAY_OF_MONTH,3);
                break;
            case 6: //Суббота +2 дня
                c.add(Calendar.DAY_OF_MONTH,2);
                break;
            case 0: //Воскресенье +1 день
                c.add(Calendar.DAY_OF_MONTH,1);
                break;
            default: //Остальные дни
                if(c.get(Calendar.HOUR_OF_DAY) > 16)
                    c.add(Calendar.DAY_OF_MONTH,1);
                break;

        }

        int day_of_month = c.get(Calendar.DAY_OF_MONTH);
        String[] DAY = context.getResources().getStringArray(R.array.DAYS);
        String[] monthes = context.getResources().getStringArray(R.array.MONTHES);
        startDate.setText(monthes[month]+" "+day_of_month+", "+DAY[DATE.getWeek()]);

        String[] TMP_WEEK = getResources().getStringArray(R.array.TYPE_WEEK);
        String TYPE_WEEK = (DATE.getWeekType() == 0) ? TMP_WEEK[0] : TMP_WEEK[1];
        startType.setText(TYPE_WEEK);
        //****************************
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

                    Intent intent = new Intent(NewsActivity.this, MainActivity.class);
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
                                Intent intent = new Intent(NewsActivity.this, windows.class);
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
                Intent intent = new Intent(NewsActivity.this, windows.class);
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
                        Intent intent = new Intent(NewsActivity.this, GetList.class);
                        intent.putExtra("type", "0");
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                mSwipeRefreshLayout.setRefreshing(false);

                progress.setProgress(0);
                progress.setVisibility(View.VISIBLE);

                if (isNetworkAvailable()) {
                    mWebView.loadUrl(url_wall);
                }else
                    nointernet.setVisibility(nointernet.VISIBLE);

            }
        }, 2000);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        NewsActivity.super.onBackPressed();
        moveTaskToBack(true);
        NewsActivity.super.onBackPressed();
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
            NewsActivity.this.setValue(newProgress);
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
