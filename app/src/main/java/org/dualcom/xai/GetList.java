package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.WrapContentViewPager;

import java.util.ArrayList;
import java.util.List;

public class GetList extends FragmentActivity {

    private TabHost mTabHost;
    private WrapContentViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private String response;

    public EditText SearchGroup;

    Context context = this;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_list);

        Storage.saveData(context, "TabId", "Tab1");

        if(Storage.emptyData(context, "GROUPS_LIST") || Storage.emptyData(context,"TEACH_LIST")){
            Intent intent = new Intent(GetList.this, StartActivity.class);
            startActivity(intent);
            this.finish();
        }

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        mTabHost.setup();

        mViewPager = (WrapContentViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        String[] GetList = getResources().getStringArray(R.array.GetList);

        mTabsAdapter.addTab(mTabHost.newTabSpec("Tab1").setIndicator(GetList[0]), list_group.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("Tab2").setIndicator(GetList[1]), list_teach.class, null);

        for(int i = 0; i < 2; i++) {
            TextView textView = (TextView) mTabHost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title);
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
                Storage.saveData(context,"TabId",tabId);
            }
        });

    }

    class GetGroups extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/android/";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

                Storage.saveData(context,"GROUPS_LIST",response);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetTeachers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/android/";

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

                Storage.saveData(context,"TEACH_LIST",response);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
