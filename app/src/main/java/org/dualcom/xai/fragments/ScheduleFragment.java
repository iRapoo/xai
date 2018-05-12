package org.dualcom.xai.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.github.vipulasri.timelineview.TimelineView;

import org.dualcom.xai.MyClass.DATE;
import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Storage;

import org.dualcom.xai.MyClass.isInternet;
import org.dualcom.xai.R;
import org.dualcom.xai.SlidingTabLayout;
import org.dualcom.xai.ViewPagerAdapter;

import java.util.Objects;

public class ScheduleFragment extends Fragment {

    private View rootView;
    private Context context;

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;

    protected BottomSheetLayout bottomSheetLayout;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        context = container.getContext();

        LinearLayout frameSchedule = rootView.findViewById(R.id.frameSchedule);
        RelativeLayout frameStart = rootView.findViewById(R.id.frameStart);
        ConstraintLayout NoInternet = rootView.findViewById(R.id.NoInternet);
        TextView numWeek = rootView.findViewById(R.id.numWeek);
        ImageView typeWeekImg = rootView.findViewById(R.id.typeWeekImg);
        TextView typeWeek = rootView.findViewById(R.id.typeWeek);

        numWeek.setText(DATE.getStudWeek() + getResources().getString(R.string.stud_week).toUpperCase());
        String[] TMP_WEEK = getResources().getStringArray(R.array.TYPE_WEEK);
        String TYPE_WEEK = (DATE.getWeekType() == 0) ? TMP_WEEK[0] : TMP_WEEK[1];
        typeWeek.setText(TYPE_WEEK.toUpperCase());

        if(DATE.getWeekType() == 0)
            typeWeekImg.setImageResource(R.drawable.ic_top_week);
        else
            typeWeekImg.setImageResource(R.drawable.ic_bottom_week);

        //bottomSheetLayout = rootView.findViewById(R.id.bottomsheet);

        if(Storage.emptyData(context, "NOW_GROUP")) {
            frameSchedule.setVisibility(View.GONE);
            if(isInternet.active(context))
                frameStart.setVisibility(View.VISIBLE);
            else
                NoInternet.setVisibility(View.VISIBLE);
        }

            //TIME LINE******************************
            /*if (DATE.getNowTime() > -1) {
                final ImageView this_time = rootView.findViewById(LIST.times(DATE.getNowTime()));
                this_time.setBackgroundResource(LIST.times_draw(DATE.getNowTime()));
            }*/


        //asd.setText("asd");

            if (DATE.getNowTimeLite() > -1) {

                for(int i = 0; i < 4; i++) {
                    TextView timeText = rootView.findViewById(LIST.times_text(DATE.getNowTimeLite())[i]);
                    timeText.setTextColor(getResources().getColor(R.color.time_color_now));
                }

                TimelineView timelineView = rootView.findViewById(LIST.times_line(DATE.getNowTimeLite()));
                timelineView.setMarker(getResources().getDrawable(R.drawable.ic_marker_active), getResources().getColor(R.color.time_color_now));
                timelineView.setStartLine(getResources().getColor(R.color.time_color_now), 0);
                timelineView.setEndLine(getResources().getColor(R.color.time_color_now), 0);
                timelineView.setMarkerSize((int) getResources().getDimension(R.dimen.size_circle));

                for(int i = DATE.getNowTimeLite()+1; i < 4; i++){

                    for(int j = 0; j < 4; j++) {
                        TextView timeText = rootView.findViewById(LIST.times_text(i)[j]);
                        timeText.setTextColor(getResources().getColor(R.color.time_color_coming));
                    }

                    TimelineView timelineView_ = rootView.findViewById(LIST.times_line(i));
                    timelineView_.setMarker(getResources().getDrawable(R.drawable.ic_marker), getResources().getColor(R.color.time_color_coming));
                    timelineView_.setStartLine(getResources().getColor(R.color.time_color_coming), 0);
                    timelineView_.setEndLine(getResources().getColor(R.color.time_color_coming), 0);
                }

            }else {

                for(int i = 0; i < 4; i++){

                    for(int j = 0; j < 4; j++) {
                        TextView timeText = rootView.findViewById(LIST.times_text(i)[j]);
                        timeText.setTextColor(getResources().getColor(R.color.dark_grey));
                    }

                    TimelineView timelineView_ = rootView.findViewById(LIST.times_line(i));
                    timelineView_.setMarker(getResources().getDrawable(R.drawable.ic_marker), getResources().getColor(R.color.dark_grey));
                    timelineView_.setStartLine(getResources().getColor(R.color.dark_grey), 0);
                    timelineView_.setEndLine(getResources().getColor(R.color.dark_grey), 0);
                }

            }

            //***************************************

            String[] DAY = getResources().getStringArray(R.array.DAYS_FULL);

            adapter = new ViewPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), DAY, DAY.length);

            // Assigning ViewPager View and setting the adapter
            pager = rootView.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            //display
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();  // deprecated
            Boolean tab_long = width >= 500;


            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.dark_grey);
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
