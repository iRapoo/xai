package org.dualcom.xai.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;

import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.WrapContentViewPager;
import org.dualcom.xai.R;
import org.dualcom.xai.TabsAdapter;
import org.dualcom.xai.list_group;
import org.dualcom.xai.list_teach;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ListFragment extends BottomSheetFragment {

    private View rootView;
    public Context context;

    private TabHost mTabHost;
    private WrapContentViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private String response;

    public String GetList[];

    public EditText Search;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        context = rootView.getContext();

        Storage.saveData(getContext(), "TabId", "Tab1");

        mTabHost = rootView.findViewById(android.R.id.tabhost);

        TabWidget tabWidget = rootView.findViewById(android.R.id.tabs);
        TextView labelSave = rootView.findViewById(R.id.labelSave);
        ImageView VoiceBtn = rootView.findViewById(R.id.VoiceBtn);
        Search = rootView.findViewById(R.id.SearchGroup);

        if(Storage.emptyData(getContext(), "_STATE_LIST")) labelSave.setVisibility(View.GONE);
        else tabWidget.setVisibility(View.GONE);

        mTabHost.setup();

        mViewPager = rootView.findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

        GetList = getResources().getStringArray(R.array.GetList);

        mTabsAdapter.addTab(mTabHost.newTabSpec("Tab1").setIndicator(GetList[0]), list_group.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("Tab2").setIndicator(GetList[1]), list_teach.class, null);

        for(int i = 0; i < 2; i++) {
            TextView textView = mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            textView.setGravity(Gravity.CENTER);
            textView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        //Изменение табов
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bg_selector);
        }

        if (savedInstanceState != null)
        {

            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));

        }

        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.white));
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                int cru_tab = (tabId.equals("Tab2")) ? 1 : 0;

                mViewPager.setCurrentItem(cru_tab);
                Storage.saveData(getContext(),"TabId",tabId);
            }
        });

        VoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        return rootView;
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getResources().getString(R.string.say_anyware));
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String _group = Storage.loadData(context,"GROUPS_LIST").substring(2,Storage.loadData(context,"GROUPS_LIST").length());
        String _teach = Storage.loadData(context,"TEACH_LIST").substring(2,Storage.loadData(context,"TEACH_LIST").length());
        String _list_str = _group + ":," + _teach;

        String _list[] = _list_str.split(":,");

        int flag = 0;
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            for (String _list1 : _list) {
                for (String _match1 : matches){
                    if(_list1.contains(_match1)){
                        Search.setText(_match1);
                        flag = 1;
                    }
                }
            }

            if(flag==0){
                Snackbar.make(getActivity().findViewById(R.id.navigation), getResources().getString(R.string.say_not_found), Snackbar.LENGTH_LONG).show();
            }

        }
    }

}
