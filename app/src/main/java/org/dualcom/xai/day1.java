package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.JSON;
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Storage;

import static org.dualcom.xai.ScheduleActivity.moreFragment;

public class day1 extends Fragment {

    public View rootView;
    public Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.day1, container, false);
        context = container.getContext();

        String group = Storage.loadData(context, "NOW_GROUP");

        for(int i = 1; i < 5; i++){
            final String json_t = JSON.getJSON(context, "day0", i + "-0", group);
            final TextView TextView_t = rootView.findViewById(LIST.TOPt(i - 1));

            final String json_b = JSON.getJSON(context, "day0", i+"-1", group);
            final TextView TextView_b = rootView.findViewById(LIST.BOTt(i - 1));

            if(!json_t.equals("0")) {
                TextView_t.setText(json_t.split("//")[0]);
                TextView_t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Storage.saveData(context, "_tmp_lesson", json_t.split("//")[0]);
                        Storage.saveData(context, "_tmp_teacher", json_t.split("//")[1]);
                        moreFragment.show(getActivity().getSupportFragmentManager(), R.id.bottomsheet);
                    }
                });
            }

            if(!json_b.equals("0")) {
                TextView_b.setText(json_b.split("//")[0]);
                TextView_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Storage.saveData(context, "_tmp_lesson", json_b.split("//")[0]);
                        Storage.saveData(context, "_tmp_teacher", json_b.split("//")[1]);
                        moreFragment.show(getActivity().getSupportFragmentManager(), R.id.bottomsheet);
                    }
                });
            }

            if(json_t.equals(json_b))
                rootView.findViewById(LIST.BOT(i - 1)).setVisibility(View.GONE);
            else{
                if(DATE.getWeekType() == 1)
                    TextView_t.setTextColor(getResources().getColor(R.color.silver));
                    //rootView.findViewById(LIST.TOP(i - 1)).setBackgroundResource(R.drawable.less_now);
                else
                    TextView_b.setTextColor(getResources().getColor(R.color.silver));
                    //rootView.findViewById(LIST.BOT(i - 1)).setBackgroundResource(R.drawable.less_now);
            }

        }

        return rootView;
    }
}
