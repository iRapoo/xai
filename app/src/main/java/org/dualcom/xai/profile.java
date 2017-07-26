package org.dualcom.xai;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class profile extends Activity {

    private WebView mWebView;
    private ProgressBar progress;
    private static final String url_profile = "http://rapoo.mysit.ru/android/profile.php";

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
        setContentView(R.layout.activity_profile);

        final LinearLayout nointernet = (LinearLayout) findViewById(R.id.nointernet);
        final Button GO_BACK = (Button) findViewById(R.id.GO_BACK);

        GO_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        //Веб форма
        mWebView = (WebView) findViewById(R.id.ProfileView);
        mWebView.setWebChromeClient(new MyWebViewClient());
        progress = (ProgressBar) findViewById(R.id.progressViewProfile);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        if (isNetworkAvailable()) {
            mWebView.loadUrl(url_profile);
        }else
            nointernet.setVisibility(nointernet.VISIBLE);

    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            profile.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);

        if(progress == 100){ //По окончанию загрузки пропадает
            this.progress.setVisibility(View.GONE);
        }
    }

    private void closeActivity() {
        this.finish();
    }

}
