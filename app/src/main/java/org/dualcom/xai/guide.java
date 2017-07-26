package org.dualcom.xai;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Created by Виталий on 06.04.2015.
 */
public class guide extends FragmentActivity {

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;

    Context context = this;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        mTabHost.setup();

        mViewPager = (ViewPager) findViewById(R.id.guider);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("guide").setIndicator(""), guide_run.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("guide").setIndicator(""), guide1.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("guide").setIndicator(""), guide2.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("guide").setIndicator(""), guide3.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("guide").setIndicator(""), guide4.class, null);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {

    }

}
