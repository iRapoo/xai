package org.dualcom.xai;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class vk extends Activity {

    private WebView mWebView3;
    Context context = this;

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
        setContentView(R.layout.activity_vk);

        final LinearLayout nointernet = (LinearLayout) findViewById(R.id.nointernet);
        final Button GO_BACK = (Button) findViewById(R.id.GO_BACK);

        GO_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Storage.saveData(context, "GUIDE", "true");
                Intent intent = new Intent(vk.this, MainActivity.class);
                startActivity(intent);*/
                closeActivity();
            }
        });

        //Веб форма
        mWebView3 = (WebView) findViewById(R.id.webView3);
        // включаем поддержку JavaScript
        mWebView3.getSettings().setJavaScriptEnabled(true);
        mWebView3.setWebViewClient(new CustomWebViewClient());
        // указываем страницу загрузки
        if (isNetworkAvailable())
            //mWebView3.loadUrl("http://m.vk.com/xai_quenix");
            mWebView3.loadUrl("http://0s.ozvs4y3pnu.nblz.ru/xai_quenix");
        else
            nointernet.setVisibility(nointernet.VISIBLE);
        //**********/

    }

    private void closeActivity() {
        this.finish();
    }

    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //do whatever you want with the url that is clicked inside the webview.
            //for example tell the webview to load that url.
            view.loadUrl(url);
            //return true if this method handled the link event
            //or false otherwise
            return true;
        }
    }

}
