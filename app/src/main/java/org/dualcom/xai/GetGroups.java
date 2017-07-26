package org.dualcom.xai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;

import java.util.concurrent.ExecutionException;

public class GetGroups extends Activity {

    private String tmp_gr = "";
    private String group = "";
    public String[] data;
    public String[] newdata;
    private String schedule = "";
    public String S_GROUP = "";
    public String[] tmp_s_group;
    Context context = this;
    public boolean flag = false;
    private String response;
    public Toast toast = null;

    public ListView listGroup;
    public EditText SearchGroup;
    public ArrayAdapter<String> adapter;
    public LinearLayout loadGroup;
    final String ATTRIBUTE_NAME_TEXT = "text";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_get);

        final Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        final GridView listGroup = (GridView) findViewById(R.id.listGroup);
        final EditText SearchGroup = (EditText) findViewById(R.id.SearchGroup);
        final TextView LabelWin = (TextView) findViewById(R.id.labelwin);

        String labeltext = (type.equals("0")) ? getResources().getString(R.string.SELECT_GROUP) : getResources().getString(R.string.LastSel);
        LabelWin.setText(labeltext);

        toast = Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.notiseDel), Toast.LENGTH_SHORT);
        toast.show();

        /*try {
            tmp_gr = new MyPHP().execute("list_group.php").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        //group = (isNetworkAvailable() && type.equals("0")) ? tmp_gr : Storage.loadData(context,"S_GROUP");

        group = (isNetworkAvailable() && type.equals("0")) ? Storage.loadData(context,"GROUPS_LIST") : Storage.loadData(context,"S_GROUP");

        data = group.substring(2,group.length()).split(":,");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item2, data);

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
            public void afterTextChanged(Editable arg0) {}
        });

        //Удаление записи
        listGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                String result = "";
                Toast toastDel = null;

                if(data.length == 1) {

                    toastDel = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.notiseLast), Toast.LENGTH_SHORT);


                }else {
                    for (int i = 0; i < data.length; i++) {
                        if (!data[i].equals(adapter.getItem(position))) {
                            result += ":,"+data[i].toString();
                        }else{
                            Storage.saveData(context, "NOW_GROUP", ((i == 0) ? data[1] : data[i-1]));
                            Storage.saveData(context, data[i].toString()+"md5", null);
                            Storage.saveData(context, data[i].toString(), null);
                        }
                    }

                    Storage.saveData(context, "S_GROUP", result);
                    Storage.saveData(context, "delcine", "true");

                    toastDel = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.notiseDelOK), Toast.LENGTH_SHORT);
                    Intent intent = new Intent(GetGroups.this, MainActivity.class);
                    startActivity(intent);
                }

                toastDel.show();

                return true;
            }
        });

        //Выбор группы
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tmp_s_group = Storage.loadData(context, "S_GROUP").split(":,");
                    try {
                        if (isNetworkAvailable() && Storage.emptyData(context, adapter.getItem(position) + "") == true) {
                            Windows.alert(context,"","");
                            if(Storage.loadData(context,"translate").equals("true"))
                                schedule = new MyPHP().execute("schedule2.php",
                                        "group=" + adapter.getItem(position), "translate=" + true).get();
                            else
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
                        Storage.saveData(context, "NOW_GROUP", adapter.getItem(position) + "");

                        Intent intent = new Intent(GetGroups.this, MainActivity.class);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
        });



    }

}
