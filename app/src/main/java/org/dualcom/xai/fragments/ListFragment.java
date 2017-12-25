package org.dualcom.xai.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dualcom.xai.GetList;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.WrapContentViewPager;
import org.dualcom.xai.MyClass.isInternet;
import org.dualcom.xai.R;
import org.dualcom.xai.StartActivity;
import org.dualcom.xai.TabsAdapter;
import org.dualcom.xai.list_group;
import org.dualcom.xai.list_teach;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends BottomSheetFragment {

    private View rootView;

    private TabHost mTabHost;
    private WrapContentViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private String response;

    public String GetList[];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        Storage.saveData(getContext(), "TabId", "Tab1");

        mTabHost = rootView.findViewById(android.R.id.tabhost);

        TabWidget tabWidget = rootView.findViewById(android.R.id.tabs);

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

        return rootView;
    }

}
