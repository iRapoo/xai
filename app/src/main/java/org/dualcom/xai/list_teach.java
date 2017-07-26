package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;

import java.util.concurrent.ExecutionException;

public class list_teach extends Fragment {

    Context context;

    private String tmp_gr = "";
    private String group = "";
    public String[] data;
    public String[] newdata;
    private String schedule = "";
    public String S_GROUP = "";
    public String[] tmp_s_group;
    public boolean flag = false;
    private String response;

    public ListView listGroup;
    public EditText SearchGroup;
    public ArrayAdapter<String> adapter;
    public LinearLayout loadGroup;
    final String ATTRIBUTE_NAME_TEXT = "text";

    //Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo == null) return false;
        boolean res = (!activeNetworkInfo.isConnected())?false:true;
        res = (!activeNetworkInfo.isAvailable())?false:true;
        return res;
    }
    /*************************/

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_list_group, container, false);

        context = container.getContext();

        Intent intent = getActivity().getIntent();
        String type = intent.getStringExtra("type");

        final GridView listGroup = (GridView) view.findViewById(R.id.listGroup);
        final EditText SearchGroup = (EditText) getActivity().findViewById(R.id.SearchGroup);
        //final TextView LabelWin = (TextView) view.findViewById(R.id.labelwin);

        String labeltext = (type.equals("0")) ? getResources().getString(R.string.SELECT_GROUP) : getResources().getString(R.string.LastSel);
        //LabelWin.setText(labeltext);

        group = (isNetworkAvailable() && type.equals("0")) ? Storage.loadData(context,"TEACH_LIST") : Storage.loadData(context,"S_GROUP");

        data = group.substring(2,group.length()).split(":,");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, data);

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

        //Выбор группы
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tmp_s_group = Storage.loadData(context,"S_GROUP").split(":,");

                try {
                    if (isNetworkAvailable() && Storage.emptyData(context,adapter.getItem(position)+"") == true){
                        if(Storage.loadData(context,"translate").equals("true"))
                            schedule = new MyPHP().execute("schedule2.php?t=true",
                                    "group=" + adapter.getItem(position), "translate=" + true).get();
                        else
                            schedule = new MyPHP().execute("schedule2.php?t=true",
                                    "group=" + adapter.getItem(position)).get();
                        /*schedule = new MyPHP().execute("schedule2.php?t=true",
                                "group=" + adapter.getItem(position)).get();*/

                        Storage.saveData(context, adapter.getItem(position)+"", schedule);

                        for(int i = 0; i < tmp_s_group.length; i++){
                            if(tmp_s_group[i].equals(adapter.getItem(position)+"")){ flag = true; }
                        }

                        if(flag != true) {
                            S_GROUP = (Storage.emptyData(context, "S_GROUP")) ? ":," + adapter.getItem(position) : Storage.loadData(context, "S_GROUP") + ":," + adapter.getItem(position);
                            Storage.saveData(context, "S_GROUP", S_GROUP);
                            Storage.saveData(context, "S_TEACH", "true");
                        }

                    }
                    Storage.saveData(context,"NOW_GROUP",adapter.getItem(position)+"");

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

}
