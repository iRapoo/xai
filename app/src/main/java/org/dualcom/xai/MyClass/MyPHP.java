package org.dualcom.xai.MyClass;

import android.content.Context;
import android.os.AsyncTask;

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

public class MyPHP extends AsyncTask<String, Integer, String> {

    Context context;
   /* private ProgressBar progressBar;
    int progress_status;


    public MyPHP(ProgressBar pb) {
        progressBar = pb;
    }

    @Override
    protected void onPreExecute() {
        // обновляем пользовательский интерфейс сразу после выполнения задачи
        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }*/

    @Override
    protected String doInBackground(String... params) {
        try{
            DefaultHttpClient hc = new DefaultHttpClient();
            HttpPost postMethod = new HttpPost(isInternet.API + params[0]);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            int count = params.length;
            String[] param = null;

            for(int i = 1; i < count; i++) {
                param = params[i].split("=");
                nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
            }

            /*while(progress_status<100){

                progress_status += 2;

                publishProgress(progress_status);
                SystemClock.sleep(300);

            }*/

            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            HttpResponse httpResponse = hc.execute(postMethod);
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity, "UTF-8");

            return response;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*@Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate( progress[0] );
        progressBar.setProgress( progress[0] );
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressBar.setVisibility(View.GONE);
    }*/
}
