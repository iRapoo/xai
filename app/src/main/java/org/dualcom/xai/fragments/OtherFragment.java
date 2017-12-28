package org.dualcom.xai.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.dualcom.xai.AlarmActivity;
import org.dualcom.xai.GetGroups;
import org.dualcom.xai.GetList;
import org.dualcom.xai.MainActivity;
import org.dualcom.xai.MapActivity;
import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;
import org.dualcom.xai.MyClass.isInternet;
import org.dualcom.xai.R;
import org.dualcom.xai.ScheduleActivity;
import org.dualcom.xai.StartActivity;
import org.dualcom.xai.about_app;
import org.dualcom.xai.incorrect;
import org.dualcom.xai.vk;

import java.util.HashMap;
import java.util.Map;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.TourGuide;

public class OtherFragment extends Fragment {

    private View rootView;
    private Context context;

    public String VERSION = "";
    public Boolean translate = true;

    public Drawer drawer = null;
    ProgressDialog progressDoalog;

    protected BottomSheetLayout bottomSheetLayout;

    public TourGuide mTourGuideHandler;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_other, container, false);
        context = container.getContext();

        bottomSheetLayout = rootView.findViewById(R.id.bottomsheet);

        try {
            VERSION = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        translate = Storage.loadData(context, "translate").equals("true");
        Boolean trans_active = getResources().getConfiguration().locale.getLanguage().equals("ru");

        Resources res = getResources();
        drawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withTranslucentStatusBar(false)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[1]).withIcon(FontAwesome.Icon.faw_download).withSelectable(false).withTag("download"),
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[2]).withIcon(FontAwesome.Icon.faw_clock_o).withSelectable(false).withEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN),
                        new SwitchDrawerItem().withName(res.getStringArray(R.array.drawers)[6]).withIcon(FontAwesome.Icon.faw_language).withSelectable(false).withChecked(translate).withOnCheckedChangeListener(onCheckedChangeListener).withEnabled(trans_active),
                        new PrimaryDrawerItem().withName(res.getStringArray(R.array.drawers)[7]).withIcon(FontAwesome.Icon.faw_map).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.support),
                        new SecondaryDrawerItem().withName(res.getStringArray(R.array.drawers)[3]).withIcon(FontAwesome.Icon.faw_vk).withSelectable(false),
                        new SecondaryDrawerItem().withName(res.getStringArray(R.array.drawers)[4]).withIcon(FontAwesome.Icon.faw_life_ring).withIdentifier(6).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1:
                                new ListFragment().show(getActivity().getSupportFragmentManager(), R.id.bottomsheet);
                                break;
                            case 2:
                                Intent intent_alarm = new Intent(getActivity(), AlarmActivity.class);
                                startActivity(intent_alarm);
                                break;
                            case 4:
                                Intent intent_map = new Intent(getActivity(), MapActivity.class);
                                startActivity(intent_map);
                                break;
                            case 6:
                                Intent intent_social = new Intent(getActivity(), vk.class);
                                startActivity(intent_social);
                                break;
                            case 7:
                                Intent intent_incorrect = new Intent(getActivity(), incorrect.class);
                                startActivity(intent_incorrect);
                                break;
                        }

                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .buildView();

        ((ViewGroup) rootView.findViewById(R.id.drawerFrame)).addView(drawer.getSlider());

        TextView appVersion = drawer.getHeader().findViewById(R.id.appVersion);
        appVersion.setText(getString(R.string.app_version) + " " + VERSION);

        TextView appCopyright = drawer.getHeader().findViewById(R.id.appCopyright);
        appCopyright.setText("© 2015-" + DATE.getYear() + ", Quenix Software");

        Button btn_clear_data = drawer.getHeader().findViewById(R.id.btn_clear_data);
        btn_clear_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Внимание!");
                builder.setMessage("Вы уверены что хотите очистить данные приложения?");
                builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Storage.clearData(context)){
                            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        drawer.setSelection(1, false);

        if(Storage.emptyData(context, "NOW_GROUP"))
            new ListFragment().show(getActivity().getSupportFragmentManager(), R.id.bottomsheet);


        return rootView;
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                if(isInternet.active(context)) {
                    progressDoalog = new ProgressDialog(getActivity());
                    progressDoalog.setMessage(getString(R.string.translate_schedule));
                    progressDoalog.setCancelable(false);
                    progressDoalog.show();

                    Storage.saveData(context, "translate", isChecked + "");
                    translate = isChecked;
                    getSchedule();
                }else {
                    Snackbar.make(rootView.findViewById(R.id.navigation), "Не найдено подключение к Интернету", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    private void getSchedule() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String _translate = (translate) ? "&translate=" + true : "";
        String url = isInternet.API + "schedule" + _translate;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context, Storage.loadData(context, "NOW_GROUP") + "", response);
                        progressDoalog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Snackbar.make(rootView.findViewById(R.id.navigation), "Ошибка перевода расписания", Snackbar.LENGTH_SHORT).show();
                progressDoalog.dismiss();
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

}
