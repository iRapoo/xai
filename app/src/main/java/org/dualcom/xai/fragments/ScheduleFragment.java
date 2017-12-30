package org.dualcom.xai.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.dualcom.xai.MainActivity;
import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;
import org.dualcom.xai.MyClass.isInternet;
import org.dualcom.xai.R;
import org.dualcom.xai.SlidingTabLayout;
import org.dualcom.xai.ViewPagerAdapter;

import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    private View rootView;
    private Context context;

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        context = container.getContext();

        LinearLayout frameSchedule = rootView.findViewById(R.id.frameSchedule);
        RelativeLayout frameStart = rootView.findViewById(R.id.frameStart);
        ConstraintLayout NoInternet = rootView.findViewById(R.id.NoInternet);

        bottomSheetLayout = rootView.findViewById(R.id.bottomsheet);

        if(Storage.emptyData(context, "NOW_GROUP")) {
            frameSchedule.setVisibility(View.GONE);
            if(isInternet.active(context))
                frameStart.setVisibility(View.VISIBLE);
            else
                NoInternet.setVisibility(View.VISIBLE);
        }

            //TIME LINE******************************
            if (DATE.getNowTime() > -1) {
                final ImageView this_time = rootView.findViewById(LIST.times(DATE.getNowTime()));
                this_time.setBackgroundResource(LIST.times_draw(DATE.getNowTime()));
            }
            //***************************************

            String[] DAY = getResources().getStringArray(R.array.DAYS_FULL);

            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), DAY, DAY.length);

            // Assigning ViewPager View and setting the adapter
            pager = rootView.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            //display
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();  // deprecated
            Boolean tab_long = (width < 500) ? false : true;
            tab_long = true;


            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
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


        return rootView;
    }

}
