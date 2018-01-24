package org.dualcom.xai;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

import org.dualcom.xai.MyClass.*;

public class incorrect extends AppCompatActivity {

    Context context = this;
    private String response;
    public ListView list_incorrect;
    public String from = "0";
    public String to = "0";
    public String text = "0";
    public String datetime = "0";
    public JSONObject count_message;
    public JSONObject messages;
    public String UID = "";
    public TextView incorrect_not_found;
    public EditText message;
    public Button btn_send;
    public String deviceManifest = "";

    public ArrayList<incorrect_const> incorrects = new ArrayList<incorrect_const>();
    public incorrect_box boxAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incorrect);

        final LinearLayout nointernet = (LinearLayout) findViewById(R.id.nointernet);
        btn_send = (Button) findViewById(R.id.btn_send);

        //Идентификация устройства
        final String SERIAL = Build.SERIAL;
        final String BRAND = Build.BRAND;
        final String MANUFACTURER = Build.MANUFACTURER;
        final String PRODUCT = Build.PRODUCT;
        UID = SERIAL+BRAND+MANUFACTURER+PRODUCT;
        //************************

        //Сбор данных ************************

        int sdkVersion = Build.VERSION.SDK_INT;
        String release = Build.VERSION.RELEASE;
        String VERSION = ""; int CODE = 0;
        try {
            VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            CODE = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String[] CashPosition = Storage.loadData(context,"S_GROUP").split(":,");

        deviceManifest += "Android/Sdk: "+release+"/"+sdkVersion+"\n";
        deviceManifest += "AppVersion/Code: "+VERSION+"/"+CODE+" \n";
        deviceManifest += "Device: "+MANUFACTURER+" "+PRODUCT+" \n";
        deviceManifest += "NowPosition: "+Storage.loadData(context, "NOW_GROUP")+" \n";
        deviceManifest += "CashPosition: [ \n";
        for(int i = 1; i < CashPosition.length; i++){
            deviceManifest += "------- "+CashPosition[i]+"\n";
        }
        deviceManifest += " ]";


        //Windows.alert(context, "Тест сообщения", deviceManifest);

        //************************************

        if(isNetworkAvailable())
            new GetIncorrect().execute("get_incorrect", "uid=" + UID);
        else
            nointernet.setVisibility(nointernet.VISIBLE);

        final TextView Labelincorrect = (TextView) findViewById(R.id.Labelincorrect);
        message = (EditText) findViewById(R.id.message);
        final Button GO_BACK = (Button) findViewById(R.id.GO_BACK);
        incorrect_not_found = (TextView) findViewById(R.id.incorrect_not_found);

        GO_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        Labelincorrect.setText(getString(R.string.incorrect_title)); //Storage.loadData(context, "NOW_GROUP")

        incorrect_not_found.setText(getString(R.string.loading));

        message.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView value, int actionId,
                                          KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    String text = value.getText().toString();

                    if(text.length() > 0){
                        message.setEnabled(false);
                        btn_send.setEnabled(false);

                        new SandIncorrect().execute("incorrectSend",
                                "uid=" + UID,
                                "text=" + text,
                                "manifest=" + deviceManifest);
                    }

                }
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = message.getText().toString();

                if(text.length() > 0){
                    message.setEnabled(false);
                    btn_send.setEnabled(false);

                    new SandIncorrect().execute("incorrectSend",
                            "uid=" + UID,
                            "text=" + text,
                            "manifest=" + deviceManifest);
                }

            }
        });

    }

    private void closeActivity() {
        this.finish();
    }

    class SandIncorrect extends AsyncTask<String, String, String> {

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

            }catch(Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if(res.equals("true"))
                new GetIncorrect().execute("get_incorrect",
                        "uid=" + UID);

        }

    }

    class GetIncorrect extends AsyncTask<String, String, String> {

        @SuppressWarnings("WrongThread")
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

            }catch(Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if(!res.equals("false")) {

                JSONParser parser = new JSONParser();

                Object obj = null;
                try {
                    obj = parser.parse(res);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObj = (JSONObject) obj;

                count_message = (JSONObject) jsonObj.get("count");
                int _count = Integer.parseInt(count_message.get("value").toString());

                Storage.saveData(context,"incorrectCount",_count+"");

                incorrects.clear();

                for (int i = 0; i < _count; i++) {

                    messages = (JSONObject) jsonObj.get("message" + i);
                    from = messages.get("from") + "";
                    to = messages.get("to") + "";
                    text = messages.get("message") + "";
                    datetime = messages.get("datetime") + "";

                    incorrects.add(new incorrect_const(text, datetime, from));

                }

                boxAdapter = new incorrect_box(context, incorrects);
                list_incorrect = (ListView) findViewById(R.id.list_incorrect);
                list_incorrect.setAdapter(null);
                list_incorrect.setAdapter(boxAdapter);

                message.setText("");
                incorrect_not_found.setVisibility(View.GONE);
                message.setEnabled(true);
                btn_send.setEnabled(true);
                //Windows.Open(context,"Тест JSON",_count+"");
            }else{
                incorrect_not_found.setText(getString(R.string.incoorect_not_found));
                incorrect_not_found.setVisibility(View.VISIBLE);
            }
        }
    }
}
