package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import org.dualcom.xai.MyClass.Storage;

/**
 * Created by Виталий on 03.02.2015.
 */
public class LoadActivity extends Activity {

    Context context = this;
    private String group = "";
    private String response;
    static final private int active = 0;
    private String versionName = "";
    public String[] tmp_update;
    public ProgressBar progressBar;
    int totalSecs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        progressBar = (ProgressBar) findViewById( R.id.progressBar);

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(isNetworkAvailable())
            new MyAsyncTask(progressBar).execute("update",
                    "ver=" + versionName );

    }

    class MyAsyncTask extends AsyncTask<String, Integer, String> {

        private ProgressBar progressBar;
        int progress_status;
        
        public MyAsyncTask(ProgressBar pb) {
            progressBar = pb;
        }

        @Override
        protected void onPreExecute() {
            // обновляем пользовательский интерфейс сразу после выполнения задачи
            super.onPreExecute();

            progress_status = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            String HOST = "http://rapoo.mysit.ru/api?module=";

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

                tmp_update = response.split(",");

                Storage.saveData(context,"UPDATE",null);
                if((tmp_update[0]+"").equals("true")){
                    Storage.saveData(context,"UPDATE",tmp_update[1]+"");
                    startService(new Intent(context, MyService.class));
                }

               /*while(progress_status<100){
                    progress_status++;
                    publishProgress(progress_status);
                    SystemClock.sleep(10);
                }*/

                /*totalSecs++;

                for (int i = 1; i <= totalSecs; i++) {
                    Thread.sleep(1000);

                    float percentage = ((float)i / (float)totalSecs) * 100;

                    publishProgress( new Float( percentage).intValue());
                }*/

                Intent intent = new Intent(LoadActivity.this, StartActivity.class);
                startActivity(intent);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate( progress );
            //progressBar.setProgress( progress[0] );
        }
    }

    //Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo == null) return false;
        boolean res = (!activeNetworkInfo.isConnected())?false:true;
        res = (!activeNetworkInfo.isAvailable())?false:true;
        return res;
    }
    /*************************/


    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        LoadActivity.super.onBackPressed();
        moveTaskToBack(true);
        LoadActivity.super.onBackPressed();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }
}
