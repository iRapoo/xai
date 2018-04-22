package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.dualcom.xai.MyClass.*;
import org.dualcom.xai.fragments.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class ScheduleActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static MoreFragment moreFragment = new MoreFragment();

    //private StartAppAd startAppAd = new StartAppAd(this);

    public Context context = this;
    public long back_pressed = 0;

    public String schedule;
    public Boolean translate = false; //Автоперевод

    public String VERSION, SERIAL, BRAND, MANUFACTURER, PRODUCT, UID;

    public ProgressDialog progressDoalog;

    public TourGuide mTourGuideHandler;

    public BottomNavigationView navigation;
    public View navigationBadge;
    public int countBage = 0;
    public Badge theBadge;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_news:
                    addFragment(new NewsFragment());
                    return true;
                case R.id.navigation_schedule:
                    addFragment(new ScheduleFragment());
                    return true;
                case R.id.navigation_save:
                    addFragment(new OtherFragment());
                    Storage.saveData(context, "_STATE_LIST", "1");
                    new ListFragment().show(getSupportFragmentManager(), R.id.bottomsheet);
                    navigation.setSelectedItemId(R.id.navigation_other);
                    return true;
                case R.id.navigation_other:
                    if(countBage>0){
                        Intent intent_incorrect = new Intent(ScheduleActivity.this, incorrect.class);
                        countBage = 0;
                        navigation.getMenu().findItem(R.id.navigation_other).setTitle(R.string.title_other);
                        navigation.getMenu().findItem(R.id.navigation_other).setIcon(R.drawable.ic_menu_black_24dp);
                        theBadge.hide(true);

                        startActivity(intent_incorrect);
                    }else {
                        if (Storage.emptyData(context, "NOW_GROUP"))
                            mTourGuideHandler.cleanUp();
                        addFragment(new OtherFragment());
                    }
                    return true;
            }
            return false;
        }
    };

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(android.R.anim.fade_in, R.anim.design_bottom_sheet_slide_out)
                .replace(R.id.content, fragment, fragment.getTag())
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*StartAppSDK.init(this, "210748147", true);
        StartAppAd.disableSplash();*/
        setContentView(R.layout.activity_schedule);

        SERIAL = Build.SERIAL;
        BRAND = Build.BRAND;
        MANUFACTURER = Build.MANUFACTURER;
        PRODUCT = Build.PRODUCT;
        UID = SERIAL+BRAND+MANUFACTURER+PRODUCT;

        /*
         * Временная стиралка памяти
         *
        if(Storage.emptyData(context, "TMP_RESET")){
            if(Storage.clearData(context)){
                Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                Storage.saveData(context, "TMP_RESET", "OK");
                startActivity(intent);
                ScheduleActivity.this.finish();
            }
        }
        /*
         * -------------------------
         */

        try {
            VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_schedule);
        navigation.getMenu().findItem(R.id.navigation_schedule).setTitle(Storage.loadData(context, "NOW_GROUP"));

        navigationBadge = navigation.getChildAt(0);

        addFragment(new ScheduleFragment());

        translate = Storage.loadData(context, "translate").equals("true");

        if(Storage.emptyData(context, "NOW_GROUP")) {
            navigation.getMenu().findItem(R.id.navigation_schedule).setTitle(getString(R.string.Welcome));

            if(isInternet.active(context)) {
                mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                        .setPointer(new Pointer())
                        .setOverlay(new Overlay())
                        .playOn(findViewById(R.id.navigation_other));
            }

            progressDoalog = new ProgressDialog(context);
            progressDoalog.setMessage("Получаем список групп");
            progressDoalog.setCancelable(false);
            progressDoalog.show();

            if(!isInternet.active(context)) {
                navigation.setVisibility(View.GONE);
                progressDoalog.dismiss();
            }
        }

        if (!Storage.loadData(context, "INFO_VERSION").equals(VERSION) && isInternet.active(context)) {
            getGroups();
            //getTeachers();
        }

        if (isInternet.active(context)) {
            CheckMD5(); //Проверка обновлений расписания

            if(!Storage.emptyData(context, "NOW_GROUP"))
                getIncorrect(); //Проверка сообщений обратной связи
        }

        if(!Storage.emptyData(context, "NOW_GROUP")) {
            String data[] = Storage.loadData(context, "S_GROUP").substring(2, Storage.loadData(context, "S_GROUP").length()).split(":,");
            if (data.length < 2)
                navigation.getMenu().findItem(R.id.navigation_save).setEnabled(false);
        }else{
            navigation.getMenu().findItem(R.id.navigation_save).setEnabled(false);
        }

        getMoney();

        BottomNavigationViewHelper.disableShiftMode(navigation); //Отключение сдвига
    }

    private void CheckMD5() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = isInternet.API + "md5_device";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(!response.equals("")) {

                            if(Storage.emptyData(context, Storage.loadData(context, "NOW_GROUP")+"md5")){
                                Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5", response);
                            }else if(!response.equals(Storage.loadData(context, Storage.loadData(context, "NOW_GROUP")+"md5"))){
                                getSchedule();
                                //Snackbar.make(findViewById(R.id.navigation), "hash: " + response, Snackbar.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(R.id.navigation), getResources().getString(R.string.download_new_schedule), Snackbar.LENGTH_SHORT).show();
                                Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5", response);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.navigation), "Ошибка проверки обновления", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("group", Storage.loadData(context, "NOW_GROUP"));

                params.put("UID", SERIAL);
                params.put("BRAND", BRAND);
                params.put("MANUFACTURER", MANUFACTURER);
                params.put("PRODUCT", PRODUCT);
                params.put("VERSION", VERSION);

                return params;
            }
        };

        queue.add(stringRequest);

    }

    private void getSchedule() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String _translate = (translate) ? "&translate=" + true : "";
        String url = isInternet.API + "schedule" + _translate;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context, Storage.loadData(context, "NOW_GROUP") + "", response);
                        addFragment(new ScheduleFragment());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.navigation), "Ошибка загрузки списка групп", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("group", Storage.loadData(context, "NOW_GROUP"));

                return params;
            }
        };

        queue.add(stringRequest);

    }

    private void getIncorrect() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = isInternet.API + "get_incorrect";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("{false")) return;

                        int _count = 0;
                        String from = "";

                        JSONParser parser = new JSONParser();
                        try {
                            Object obj = parser.parse(response);
                            JSONObject jsonObj = (JSONObject) obj;

                            JSONObject count_message = (JSONObject) jsonObj.get("count");
                            _count = Integer.parseInt(count_message.get("value").toString());

                            JSONObject messages = (JSONObject) jsonObj.get("message" + (_count-1));
                            from = messages.get("from") + "";
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(!(_count+"").equals(Storage.loadData(context,"incorrectCount")) &&
                                from.equals("admin")){

                            int save_count = (Storage.emptyData(context,"incorrectCount")) ? 0 : Integer.parseInt(Storage.loadData(context,"incorrectCount"));
                            countBage = (save_count>-1) ? (_count - save_count) : 0;

                            if(countBage>0) {
                                theBadge = new QBadgeView(context).bindTarget(navigationBadge).setBadgeNumber(countBage).setGravityOffset(25,2,true);
                                navigation.getMenu().findItem(R.id.navigation_other).setTitle(R.string.message);
                                navigation.getMenu().findItem(R.id.navigation_other).setIcon(R.drawable.ic_message_black_24dp);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.navigation), "Ошибка загрузки обратной связи", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("uid", UID);

                return params;
            }
        };

        queue.add(stringRequest);

    }

    private void getGroups() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = isInternet.API + "list_group";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context,"GROUPS_LIST", response);
                        getTeachers();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.navigation), "Ошибка загрузки списка преподавателей", Snackbar.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    private void getTeachers() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = isInternet.API + "list_teach";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context,"TEACH_LIST", response);

                        if(Storage.emptyData(context, "NOW_GROUP"))
                            progressDoalog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.navigation), "Ошибка перевода расписания", Snackbar.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    private void getMoney() {

        if(Storage.emptyData(context, "MONEY_BALANCE"))
            Storage.saveData(context,"MONEY_BALANCE", "0.00 UAH");

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = isInternet.API + "money";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context,"MONEY_BALANCE", response);

                        if(Storage.emptyData(context, "NOW_GROUP"))
                            progressDoalog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        if(back_pressed + 2000 > System.currentTimeMillis()) {
            //startAppAd.onBackPressed();
            super.onBackPressed();
        } else {
            Snackbar.make(findViewById(R.id.navigation), R.string.double_press, Snackbar.LENGTH_SHORT).show();
        }

        back_pressed = System.currentTimeMillis();
    }

}
