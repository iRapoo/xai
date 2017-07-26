package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Storage;

/**
 * Created by Виталий on 01.02.2015.
 */
public class day3 extends Fragment {

    Context context;

    @Override

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.day3, container, false);

        context = container.getContext();
        String group = Storage.loadData(context, "NOW_GROUP");

        /*if (DATE.getWeekType() == 0) {
            for (int i = 8; i < 12; i++) {
                if(DATE.getNowTime()+8 == i && DATE.getWeek() == 2)
                    ((LinearLayout) view.findViewById(LIST.TOP(i))).setBackgroundColor(getResources().getColor(R.color.blue_dark_color_top));
                else
                    ((LinearLayout) view.findViewById(LIST.TOP(i))).setBackgroundColor(getResources().getColor(R.color.blue_dark_color));
                //((LinearLayout) view.findViewById(LIST.BOT(i))).setBackgroundColor(getResources().getColor(R.color.selection));//old
                ((TextView) view.findViewById(LIST.TOPt(i))).setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            for (int i = 8; i < 12; i++) {
                if(DATE.getNowTime()+8 == i && DATE.getWeek() == 2)
                    ((LinearLayout) view.findViewById(LIST.BOT(i))).setBackgroundColor(getResources().getColor(R.color.blue_dark_color_top));
                else
                    ((LinearLayout) view.findViewById(LIST.BOT(i))).setBackgroundColor(getResources().getColor(R.color.blue_dark_color));
                //((LinearLayout) view.findViewById(LIST.BOT(i))).setBackgroundColor(getResources().getColor(R.color.selection));//old
                ((TextView) view.findViewById(LIST.BOTt(i))).setTextColor(getResources().getColor(R.color.white));
            }
        }

        for(int i = 1; i < 5; i++){
            final String json = JSON.getJSON(context, "day2", i + "-0", group);
            final TextView TextView = (TextView) view.findViewById(LIST.TOPt(i+7));
            if(json != "0") {
                TextView.setText(json.split("//")[0]);
                TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(context,
                                json.split("//")[1], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }

        for(int i = 1; i < 5; i++){
            final String json = JSON.getJSON(context, "day2", i+"-1", group);
            final TextView TextView = (TextView) view.findViewById(LIST.BOTt(i + 7));
            if(json != "0") {
                TextView.setText(json.split("//")[0]);
                TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(context,
                                json.split("//")[1], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }*/

        for(int i = 1; i < 5; i++){
            final String json_t = JSON.getJSON(context, "day2", i + "-0", group);
            final TextView TextView_t = (TextView) view.findViewById(LIST.TOPt(i + 7));

            final String json_b = JSON.getJSON(context, "day2", i+"-1", group);
            final TextView TextView_b = (TextView) view.findViewById(LIST.BOTt(i + 7));

            if(json_t != "0") {
                TextView_t.setText(json_t.split("//")[0]);
                TextView_t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Toast toast = Toast.makeText(context,
                                json_t.split("//")[1], Toast.LENGTH_SHORT);
                        toast.show();*/
                        Intent intent = new Intent(getActivity(), TeacherActivity.class);
                        intent.putExtra("teacher", json_t.split("//")[1]);
                        startActivity(intent);
                    }
                });
            }

            if(json_b != "0") {
                TextView_b.setText(json_b.split("//")[0]);
                TextView_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Toast toast = Toast.makeText(context,
                                json_b.split("//")[1], Toast.LENGTH_SHORT);
                        toast.show();*/
                        Intent intent = new Intent(getActivity(), TeacherActivity.class);
                        intent.putExtra("teacher", json_b.split("//")[1]);
                        startActivity(intent);
                    }
                });
            }

            if(json_t.equals(json_b))
                ((LinearLayout) view.findViewById(LIST.BOT(i+7))).setVisibility(View.GONE);
            else{
                if(DATE.getWeekType() == 0)
                    ((LinearLayout) view.findViewById(LIST.TOP(i + 7))).setBackgroundResource(R.drawable.less_now);
                else
                    ((LinearLayout) view.findViewById(LIST.BOT(i + 7))).setBackgroundResource(R.drawable.less_now);
            }

        }

        return view;

    }



}
