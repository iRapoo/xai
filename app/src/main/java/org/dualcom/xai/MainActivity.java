package org.dualcom.xai;

import android.annotation.SuppressLint;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

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
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;

import org.dualcom.xai.MyClass.Windows;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    /*private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;*/

    private Drawer drawerResult = null;
    public IProfile profile;

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Home","Events"};
    int Numboftabs = 2;

    private String response = "";
    private String res = "";
    private String schedule = "";

    Context context = this;

    private LinearLayout TOP;
    private LinearLayout BOT;
    public TextView startDate;
    public TextView startType;
    public TextView drawerGroup;
    public ImageView drawerBg;

    public String UID = "";
    public String from = "0";
    public JSONObject messages;
    public JSONObject count_message;

    public String VERSION = "";

    public Boolean translate = true;

    private static final int NOTIFY_ID = 999;

    //public FlowingDrawer mDrawer;
    /*public ListView lvMain;

    public String[] drawers;
    public int[] drawerImg = { R.drawable.news, R.drawable.change, R.drawable.alarm,
            R.drawable.social, R.drawable.incorrect, R.drawable.about };;

    ArrayList<itemDrawer> Drawers = new ArrayList<itemDrawer>();
    BoxAdapter boxAdapter;*/

    /*LinearLayout TOP[] = new LinearLayout[20];
    LinearLayout BOT[] = new LinearLayout[20];*/

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

    @SuppressLint("NewApi")
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String SERIAL = Build.SERIAL;
        final String BRAND = Build.BRAND;
        final String MANUFACTURER = Build.MANUFACTURER;
        final String PRODUCT = Build.PRODUCT;
        UID = SERIAL+BRAND+MANUFACTURER+PRODUCT;

        try {
            VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!Storage.loadData(context, "INFO_VERSION").equals(VERSION) && isNetworkAvailable()) {
            new StatVersion().execute("device",
                    "UID=" + Build.SERIAL,
                    "BRAND=" + Build.BRAND,
                    "MANUFACTURER=" + Build.MANUFACTURER,
                    "PRODUCT=" + Build.PRODUCT,
                    "VERSION=" + VERSION);

            //Получение списка групп
            new GetGroupsUpd().execute("list_group");
            new GetTeachersUpd().execute("list_teach");
        }

        //mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);

        /*if (Storage.emptyData(context, "TUTORIAL"))
            Storage.saveData(context, "TUTORIAL", "true");*/

        Intent intent = getIntent();

        //Переход из будильника
        if (intent.getExtras()!=null) {
            Bundle extras = getIntent().getExtras();

            if (extras.getString("OnAlarm")!=null) {
                finish();
            }
        }
        //***********************/

        //drawers = getResources().getStringArray(R.array.drawers);

        //Получение группы из виджета
        String group = (intent.getAction() != null) ? intent.getAction() : Storage.loadData(context, "NOW_GROUP");
        if(Storage.emptyData(context, "NOW_GROUP")){
            if(!isNetworkAvailable()) {
                Intent intent_no_group = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent_no_group);
                startActivity(intent_no_group);
            }
            else {
                Intent intent_now_group = new Intent(MainActivity.this, GetList.class);
                intent_now_group.putExtra("type", "0");
                startActivity(intent_now_group);
            }
        }else {

            group = Storage.loadData(context, "NOW_GROUP");

            Storage.saveData(context, "TMP_NOW_GROUP", Storage.loadData(context, "NOW_GROUP"));
            Storage.saveData(context, "NOW_GROUP", group.toString());
        }
        //***************************

        //Кнопка возврата на главный экран
        Button GO_BACK = (Button) findViewById(R.id.GO_BACK);
        RelativeLayout GO_MENU = (RelativeLayout) findViewById(R.id.GO_MENU);
        Button fast_change_group = (Button) findViewById(R.id.fast_change_group);
        TextView main_label = (TextView) findViewById(R.id.main_label);
        TextView main_type = (TextView) findViewById(R.id.main_type);
        TextView label_week = (TextView) findViewById(R.id.LabelWeek);
        final Button GO_SETTING = (Button) findViewById(R.id.GO_SETTING);
        final LinearLayout ABOUT_BTN = (LinearLayout) findViewById(R.id.about_btn);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            GO_MENU.setVisibility(GO_MENU.GONE);
        }

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);
        int dow = (c.get(Calendar.DAY_OF_WEEK)-1);
        int month = c.get(Calendar.MONTH);

        String[] DAY = getResources().getStringArray(R.array.DAYS_FULL);

        int day_of_month = c.get(Calendar.DAY_OF_MONTH);
        String[] monthes = context.getResources().getStringArray(R.array.MONTHES);

        /*ABOUT_BTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, about_app.class);
                startActivity(intent);
            }
        });*/

        GO_SETTING.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        //Боковая панель ---------------------------------
        String g = Storage.loadData(context,"S_GROUP");
        final String now_g = Storage.loadData(context,"NOW_GROUP");
        final String old_g1 = (Storage.emptyData(context,"OLD_GROUP1")) ? "0" : Storage.loadData(context, "OLD_GROUP1");
        final String old_g2 = (Storage.emptyData(context,"OLD_GROUP2")) ? "0" : Storage.loadData(context, "OLD_GROUP2");
        final String[] data_g = g.split(":,");
        int count_g = 0;

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(now_g).withName(monthes[month] + " " + day_of_month + ", " + DAY[DATE.getWeek()]).withIdentifier(1)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        if(Storage.emptyData(context,"OLD_GROUP1"))
                            Storage.saveData(context,"OLD_GROUP1", now_g);
                        else{
                            if(data_g.length>2)
                                Storage.saveData(context,"OLD_GROUP2", old_g1);
                            Storage.saveData(context,"OLD_GROUP1", now_g);
                        }

                        if(!currentProfile){
                            Storage.saveData(context, "NOW_GROUP", profile.getEmail() + "");

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }

                        return true;
                    }
                })
                .build();

        if(!old_g1.equals("0")){
            count_g += 1;
            profile = new ProfileDrawerItem().withEmail(old_g1).withName(monthes[month] + " " + day_of_month + ", " + DAY[DATE.getWeek()]).withIdentifier(count_g);
            headerResult.addProfile(profile, count_g);
            if(!old_g2.equals("0")) {
                count_g += 1;
                profile = new ProfileDrawerItem().withEmail(old_g2).withName(monthes[month] + " " + day_of_month + ", " + DAY[DATE.getWeek()]).withIdentifier(count_g);
                headerResult.addProfile(profile, count_g);
            }
        }

        for(int i = 1; i < data_g.length; i++) {
            if( !data_g[i].equals(now_g) && !data_g[i].equals(old_g1) && !data_g[i].equals(old_g2) ) {
                count_g++;
                profile = new ProfileDrawerItem().withEmail(data_g[i]).withName(monthes[month] + " " + day_of_month + ", " + DAY[DATE.getWeek()]).withIdentifier(count_g);
                headerResult.addProfile(profile, count_g);
            }
        }

        translate = (Storage.loadData(context,"translate").equals("true")) ? true : false;
        Boolean trans_active = (getResources().getConfiguration().locale.getLanguage().equals("ru")) ? true : false;

        Resources res = getResources();
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                //.withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[0]).withIcon(FontAwesome.Icon.faw_newspaper_o).withSelectable(false),
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[1]).withIcon(FontAwesome.Icon.faw_download).withSelectable(false),
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[2]).withIcon(FontAwesome.Icon.faw_clock_o).withSelectable(false),
                        new SwitchDrawerItem().withName(res.getStringArray(R.array.drawers)[6]).withIcon(FontAwesome.Icon.faw_language).withSelectable(false).withChecked(translate).withOnCheckedChangeListener(onCheckedChangeListener).withEnabled(trans_active),
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[7]).withIcon(FontAwesome.Icon.faw_map).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.support),
                        new SecondaryDrawerItem().withName(res.getStringArray(R.array.drawers)[3]).withIcon(FontAwesome.Icon.faw_vk).withSelectable(false),
                        new SecondaryDrawerItem().withName(res.getStringArray(R.array.drawers)[4]).withIcon(FontAwesome.Icon.faw_question).withIdentifier(6).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(res.getStringArray(R.array.drawers)[5]).withIcon(FontAwesome.Icon.faw_info).withSelectable(false)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1:
                                Intent intent_news = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(intent_news);
                                break;
                            case 2:
                                if(isNetworkAvailable()) {
                                    Intent intent_change = new Intent(MainActivity.this, GetList.class);
                                    intent_change.putExtra("type", "0");
                                    startActivity(intent_change);
                                }else{
                                    Intent intent_change = new Intent(MainActivity.this, GetGroups.class);
                                    intent_change.putExtra("type", "0");
                                    startActivity(intent_change);
                                }
                                break;
                            case 3:
                                Intent intent_alarm = new Intent(MainActivity.this, AlarmActivity.class);
                                startActivity(intent_alarm);
                                break;
                            case 5:
                                Intent intent_map = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intent_map);
                                break;
                            case 7:
                                Intent intent_social = new Intent(MainActivity.this, vk.class);
                                startActivity(intent_social);
                                break;
                            case 8:
                                Intent intent_incorrect = new Intent(MainActivity.this, incorrect.class);
                                startActivity(intent_incorrect);
                                break;
                            case 10:
                                Intent intent_about = new Intent(MainActivity.this, about_app.class);
                                startActivity(intent_about);
                                break;
                        }
                        return true;
                    }
                })
                .build();

        drawerResult.setSelection(1, false);

        //Получение количества сообщений обратной связи
        if (isNetworkAvailable())
            new GetIncorrect().execute("get_incorrect", "uid=" + UID);
        //---------------------------------------------

        GO_BACK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerResult.openDrawer();
                //mDrawer.openMenu();

                /*if(!isNetworkAvailable()){
                    closeActivity();
                }else {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        intent = new Intent(MainActivity.this, NewsActivity.class);
                    }else{
                        intent = new Intent(MainActivity.this, StartActivity.class);
                    }
                    startActivity(intent);
                    //closeActivity();
                }*/
            }
        });

        /*startDate = (TextView) findViewById(R.id.start_date);
        startType = (TextView) findViewById(R.id.start_type);
        /*drawerGroup = (TextView) findViewById(R.id.drawerGroup);
        drawerBg = (ImageView) findViewById(R.id.drawerBg);*/

        /*group = (group.contains(".,")) ? group.split(".,")[1] : group;
        drawerGroup.setText((group.length()>5) ? group.substring(0,6) : group);

        switch (DATE.getNowTime()){

            case 0:
                drawerBg.setBackgroundResource(R.drawable.morning);
                break;
            case 1:
                drawerBg.setBackgroundResource(R.drawable.morning);
                break;
            case 2:
                drawerBg.setBackgroundResource(R.drawable.morning);
                break;
            case 3:
                drawerBg.setBackgroundResource(R.drawable.morning);
                break;
            case 4:
                drawerBg.setBackgroundResource(R.drawable.sun);
                break;
            case 5:
                drawerBg.setBackgroundResource(R.drawable.sun);
                break;
            case 6:
                drawerBg.setBackgroundResource(R.drawable.sun);
                break;
            case 7:
                drawerBg.setBackgroundResource(R.drawable.sun);
                break;
            default:
                drawerBg.setBackgroundResource(R.drawable.evening);
                break;
        }

        fillData();
        boxAdapter = new BoxAdapter(this, Drawers);
        lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position){
                    case 0:
                        Intent intent_news = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent_news);
                        break;
                    case 1:
                        if(isNetworkAvailable()) {
                            Intent intent_change = new Intent(MainActivity.this, GetList.class);
                            intent_change.putExtra("type", "0");
                            startActivity(intent_change);
                        }else{
                            Intent intent_change = new Intent(MainActivity.this, GetGroups.class);
                            intent_change.putExtra("type", "0");
                            startActivity(intent_change);
                        }
                        break;
                    case 2:
                        Intent intent_alarm = new Intent(MainActivity.this, AlarmActivity.class);
                        startActivity(intent_alarm);
                        break;
                    case 3:
                        Intent intent_social = new Intent(MainActivity.this, vk.class);
                        startActivity(intent_social);
                        break;
                    case 4:
                        Intent intent_incorrect = new Intent(MainActivity.this, incorrect.class);
                        startActivity(intent_incorrect);
                        break;
                    case 5:
                        Intent intent_about = new Intent(MainActivity.this, about_app.class);
                        startActivity(intent_about);
                        break;
                }
            }
        });

        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });*/
        //--------------------------------------------------

        //******************************//

        //Кнопка быстрой смены группы
        fast_change_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Intent intent = new Intent(MainActivity.this, GetGroups.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }else{
                    if(isNetworkAvailable()) {
                        Intent intent0 = new Intent(MainActivity.this, GetList.class);
                        intent0.putExtra("type", "0");
                        startActivity(intent0);
                    }else{
                        Intent intent0 = new Intent(MainActivity.this, GetGroups.class);
                        intent0.putExtra("type", "0");
                        startActivity(intent0);
                    }
                }
            }
        });
        //**************************//


        //TIME LINE******************************
        if(DATE.getNowTime()>-1){
            final ImageView this_time = (ImageView) findViewById(LIST.times(DATE.getNowTime()));
            this_time.setBackgroundResource(LIST.times_draw(DATE.getNowTime()));
        }
        //***************************************

        /*toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);*/

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.

        if(c.get(Calendar.HOUR_OF_DAY) >= 17 && dow < 5 && dow > 0)
        {
            DAY[DATE.getWeek()-1] = getResources().getString(R.string.today);
            DAY[DATE.getWeek()] = getResources().getString(R.string.tommorow);
        }

        if(c.get(Calendar.HOUR_OF_DAY) < 17 && dow < 5 && dow > 0)
        {
            DAY[DATE.getWeek()] = getResources().getString(R.string.today);
            DAY[DATE.getWeek()+1] = getResources().getString(R.string.tommorow);
        }

        if(c.get(Calendar.HOUR_OF_DAY) < 17 && dow == 5)
        {
            DAY[4] = getResources().getString(R.string.today);
        }

        if(c.get(Calendar.HOUR_OF_DAY) >= 17 && dow == 5)
        {
            DAY[4] = getResources().getString(R.string.today);
        }

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),DAY,DAY.length);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        //display
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();  // deprecated
        Boolean tab_long = (width<500) ? false : true;
        tab_long = true;


        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(tab_long); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


        /*mTabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        mTabHost.setup();

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        String[] DAY = getResources().getStringArray(R.array.DAYS);

        mTabsAdapter.addTab(mTabHost.newTabSpec("day1").setIndicator(DAY[0]), day1.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("day2").setIndicator(DAY[1]), day2.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("day3").setIndicator(DAY[2]), day3.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("day4").setIndicator(DAY[3]), day4.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("day5").setIndicator(DAY[4]), day5.class, null);

        for(int i = 0; i < 5; i++) {
            TextView textView = (TextView) mTabHost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title);
            textView.setGravity(Gravity.CENTER);
            textView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }*/
        //Изменение табов
        /*for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bg_selector);
        }
        /****************/
            /*/ Change background
            for(int i=0; i < tabWidget.getChildCount(); i++)
                tabWidget.getChildAt(i).setBackgroundResource(R.drawable.tab_indicator_holo);*/



        /*if (savedInstanceState != null)
        {

            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));

        }

        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.white));
        }*/

        //Windows.alert(context,"",DATE.getWeek(DATE.getYear(),DATE.getMonth(),DATE.getDay())+", "+DATE.getYear()+" - "+DATE.getMonth()+" - "+DATE.getDay());

        //Выбор текущего дня недели
        /*mTabHost.setCurrentTab(DATE.getWeek());
        /******************/

        //Windows.alert(context,"",Storage.loadData(context,"327ст"));
        //Windows.alert(context, "Проверка",JSON.getJSON(context, "day0", "1-1"));

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

        String tmp_ml = getString(R.string.SELECT);
        String[] TMP_WEEK = getResources().getStringArray(R.array.TYPE_WEEK);
        String TYPE_WEEK = (DATE.getWeekType() == 0) ? TMP_WEEK[0] : TMP_WEEK[1];
        main_label.setText(Storage.loadData(context, "NOW_GROUP")); //+" | "+TYPE_WEEK
        main_type.setText(TYPE_WEEK);
        label_week.setText(monthes[month] + " " + day_of_month + " / " + DATE.getStudWeek() + getResources().getString(R.string.stud_week));
        //startType.setText(TYPE_WEEK);

        //Проверка ХЕШ суммы
        if(isNetworkAvailable() && !Storage.emptyData(context, "NOW_GROUP")) {
            new GetMd5().execute("md5",
                    "group=" + Storage.loadData(context, "NOW_GROUP"));
        }
        //****************//

    }

    class GetGroupsUpd extends AsyncTask<String, String, String> {

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

    class GetTeachersUpd extends AsyncTask<String, String, String> {

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

    class StatVersion extends AsyncTask<String, String, String> {

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
                    Storage.saveData(context, "INFO_VERSION", VERSION);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {

                Storage.saveData(context,"translate",isChecked+"");

                String group = Storage.loadData(context, "NOW_GROUP");

                if(isNetworkAvailable()) {
                    if(isChecked)
                        new SetTranslate().execute("schedule",
                                "group=" + group, "translate=" + true);
                    else
                        new SetTranslate().execute("schedule",
                                "group=" + group);

                }

                /*Intent intent = new Intent(MainActivity.this, MainActivity.class);
                finish();
                startActivity(intent);*/

                // Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    class SetTranslate extends AsyncTask<String, String, String> {

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
                res = EntityUtils.toString(httpEntity, "UTF-8");

                String group = Storage.loadData(context, "NOW_GROUP");

                Storage.saveData(context, group + "", res);

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                finish();
                startActivity(intent);

            }catch(Exception e){
                e.printStackTrace();
            }

            return res;
        }

    }

    class GetMd5 extends AsyncTask<String, String, String> {

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
                res = EntityUtils.toString(httpEntity, "UTF-8");

                /*if(Storage.emptyData(context, Storage.loadData(context, "NOW_GROUP")+"md5")){
                    Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5",response);
                }else if(!response.equals(Storage.loadData(context, Storage.loadData(context, "NOW_GROUP")+"md5"))){
                    Storage.saveData(context,"tmp_md5",response);
                    Intent intent = new Intent(MainActivity.this, GetUpdate.class);
                    startActivity(intent);
                }*/

                //Возврат основной группы
                //Storage.saveData(context, "NOW_GROUP", Storage.loadData(context, "TMP_NOW_GROUP"));

            }catch(Exception e){
                e.printStackTrace();
            }

            return res;
        }

        @Override
        @SuppressLint("NewApi")
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            Toast toast = Toast.makeText(context, "hash: "+res, Toast.LENGTH_LONG);

            if(!res.equals("")) {

                if(Storage.emptyData(context, Storage.loadData(context, "NOW_GROUP")+"md5")){
                    Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5", res);
                }else if(!res.equals(Storage.loadData(context, Storage.loadData(context, "NOW_GROUP")+"md5"))){

                    String group = Storage.loadData(context, "NOW_GROUP");

                    if(isNetworkAvailable()) {
                        try {
                            if(!translate) {
                                schedule = new MyPHP().execute("schedule",
                                        "group=" + group).get();
                            }else{
                                schedule = new MyPHP().execute("schedule",
                                        "group=" + group, "translate=" + true).get();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        Storage.saveData(context, group + "", schedule);

                        Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5", res);

                    }

                    toast.show();

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);

                }

            }
        }

    }

    /*void fillData() {

        for (int i = 0; i < drawers.length; i++) {
            Drawers.add(new itemDrawer(drawers[i], drawerImg[i]));
        }
    }*/

    @SuppressLint("NewApi")
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Toast.makeText(PopupMenuDemoActivity.this,
                // item.toString(), Toast.LENGTH_LONG).show();
                // return true;
                switch (item.getItemId()) {

                    case R.id.menu0:
                        if(isNetworkAvailable()) {
                            Intent intent0 = new Intent(MainActivity.this, GetList.class);
                            intent0.putExtra("type", "0");
                            startActivity(intent0);
                        }else{
                            Intent intent0 = new Intent(MainActivity.this, GetGroups.class);
                            intent0.putExtra("type", "0");
                            startActivity(intent0);
                        }
                        return true;
                    case R.id.menu01:
                        Intent intent01 = new Intent(MainActivity.this, AlarmActivity.class);
                        startActivity(intent01);
                        return true;
                    case R.id.menu1:
                        Intent intent = new Intent(MainActivity.this, vk.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu2:
                        Intent intent2 = new Intent(MainActivity.this, incorrect.class);
                        startActivity(intent2);
                        return true;
                    case R.id.menu3:
                        Intent intent3 = new Intent(MainActivity.this, about_app.class);
                        startActivity(intent3);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                //Действие после сброса меню
                /*Toast.makeText(getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
        popupMenu.show();
    }

    private void closeActivity() {
        this.finish();
    }

    class GetIncorrect extends AsyncTask<String, String, String> {

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

                    int save_count = (Storage.emptyData(context,"incorrectCount")) ? 0 : Integer.parseInt(Storage.loadData(context,"incorrectCount"));
                    int nol = (save_count>-1) ? (_count - save_count) : 0;
                    //int _nol = (nol<0) ? 0 : nol;

                    if(nol>0) {

                        drawerResult.updateBadge(6, new StringHolder(nol + ""));

                        //*****-------------

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
                        Notification notification = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            notification = builder.build();
                            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                        }

                        NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFY_ID, notification);

                    }
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if(drawerResult.isDrawerOpen()){
            drawerResult.closeDrawer();
        }
        else{
            MainActivity.super.onBackPressed();
            moveTaskToBack(true);
            MainActivity.super.onBackPressed();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }

    }
}
