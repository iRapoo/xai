package org.dualcom.xai;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;

import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;

import java.util.concurrent.ExecutionException;

public class ConfigActivity extends Activity {

    private String tmp_gr = "";
    private String tmp_th = "";
    private String group = "";
    public String[] data;
    private String group_teach = "";
    public String[] data_teach;
    public String[] newdata;
    private String schedule = "";
    public String S_GROUP = "";
    public String[] tmp_s_group;
    Context context = this;
    public boolean flag = false;
    private String response;

    public ListView listGroup;
    public EditText SearchGroup;
    public ArrayAdapter<String> adapter;
    public LinearLayout loadGroup;

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

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    SharedPreferences sp;

    final String LOG_TAG = "myLogs";

    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_GROUP = "widget_text_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate config");

        if(Storage.emptyData(context, "S_GROUP")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
            builder.setTitle(getString(R.string.no_inet1))
                    .setMessage(getString(R.string.no_widget))
                    .setCancelable(false)
                    .setPositiveButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    setResult(RESULT_CANCELED, resultValue);
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.activity_config);

        //******************************************************************************************

        String type = "1";

        final ListView listGroup = (ListView) findViewById(R.id.listGroup);
        final EditText SearchGroup = (EditText) findViewById(R.id.SearchGroup);

        try {
            tmp_gr = new MyPHP().execute("list_group2.php").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        group = (isNetworkAvailable() && type.equals("0")) ? tmp_gr : Storage.loadData(context,"S_GROUP");
        data = group.substring(2,group.length()).split(":,");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, data);

        listGroup.setAdapter(adapter);

        //Поиск
        SearchGroup.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        //Выбор группы
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tmp_s_group = Storage.loadData(context, "S_GROUP").split(":,");

                try {
                    if (isNetworkAvailable() && Storage.emptyData(context, adapter.getItem(position) + "") == true) {
                        schedule = new MyPHP().execute("schedule2.php",
                                "group=" + adapter.getItem(position)).get();

                        Storage.saveData(context, adapter.getItem(position) + "", schedule);

                        for (int i = 0; i < tmp_s_group.length; i++) {
                            if (tmp_s_group[i].equals(adapter.getItem(position) + "")) {
                                flag = true;
                            }
                        }

                        if (flag != true) {
                            S_GROUP = (Storage.emptyData(context, "S_GROUP")) ? ":," + adapter.getItem(position) : Storage.loadData(context, "S_GROUP") + ":," + adapter.getItem(position);
                            Storage.saveData(context, "S_GROUP", S_GROUP);
                        }

                    }
                    /*Storage.saveData(context,"NOW_GROUP",adapter.getItem(position)+"");

                    Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                    startActivity(intent);*/

                    // Записываем значения с экрана в Preferences
                    sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString(WIDGET_GROUP + widgetID, adapter.getItem(position));
                    editor.commit();

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_today);
                    WidgetToday.updateWidget(context, appWidgetManager, sp, widgetID, remoteViews);

                    //Storage.saveData(context, "id" + widgetID, adapter.getItem(position) + "");

                    // положительный ответ
                    setResult(RESULT_OK, resultValue);

                    Log.d(LOG_TAG, "finish config " + widgetID);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        //********************************************************************
    }

    /*public void onClick(View v) {

        EditText etText = (EditText) findViewById(R.id.etText);

        // Записываем значения с экрана в Preferences
        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(WIDGET_GROUP + widgetID, etText.getText().toString());
        editor.commit();

        // положительный ответ
        setResult(RESULT_OK, resultValue);

        Log.d(LOG_TAG, "finish config " + widgetID);
        finish();
    }*/
}