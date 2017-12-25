package org.dualcom.xai;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;
import org.dualcom.xai.MyClass.WrapContentViewPager;
import org.dualcom.xai.MyClass.isInternet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class list_group extends Fragment {

    private View view;
    Context context;

    private String group = "";
    public String[] data;

    private String schedule = "";
    public String S_GROUP = "";
    public String[] tmp_s_group;
    public boolean flag = false;
    public FragmentTabHost mTabHost;
    public EditText SearchGroup;
    public Boolean translate = true;
    public String getItem;
    ProgressDialog progressDoalog;

    private WrapContentViewPager mViewPager;

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_list_group, container, false);

        context = container.getContext();
        mTabHost = new FragmentTabHost(getActivity());

        mViewPager = view.findViewById(R.id.pager);

        String type = type = "0";

        final GridView listGroup = view.findViewById(R.id.listGroup);
        final EditText SearchGroup = getActivity().findViewById(R.id.SearchGroup);

        String labeltext = (type.equals("0")) ? getResources().getString(R.string.SELECT_GROUP) : getResources().getString(R.string.LastSel);

        group = (isInternet.active(context) && type.equals("0")) ? Storage.loadData(context,"GROUPS_LIST") : Storage.loadData(context,"S_GROUP");

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


                if (isInternet.active(context) && Storage.emptyData(context, adapter.getItem(position) + "")){
                    progressDoalog = new ProgressDialog(getActivity());
                    progressDoalog.setMessage(getString(R.string.loading));
                    progressDoalog.setCancelable(false);
                    progressDoalog.show();

                    getItem = adapter.getItem(position);
                    getSchedule();

                    for(int i = 0; i < tmp_s_group.length; i++){
                        if(tmp_s_group[i].equals(adapter.getItem(position)+"")){ flag = true; }
                    }

                    if(flag != true) {
                        S_GROUP = (Storage.emptyData(context, "S_GROUP")) ? ":," + adapter.getItem(position) : Storage.loadData(context, "S_GROUP") + ":," + adapter.getItem(position);
                        Storage.saveData(context, "S_GROUP", S_GROUP);
                        Storage.saveData(context, "S_TEACH", "false");
                    }

                }else {
                    Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                Storage.saveData(context,"NOW_GROUP",adapter.getItem(position)+"");

            }
        });

        return view;
    }

    private void getSchedule() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String _translate = (Storage.loadData(context,"translate").equals("true")) ? "&translate=" + true : "";
        String url = isInternet.API + "schedule" + _translate;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context, getItem + "", response);
                        progressDoalog.dismiss();

                        Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDoalog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("group", getItem);

                return params;
            }
        };

        queue.add(stringRequest);

    }

}
