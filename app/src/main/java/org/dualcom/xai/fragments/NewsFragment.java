package org.dualcom.xai.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.dualcom.xai.MyClass.*;
import org.dualcom.xai.R;

public class NewsFragment extends Fragment {

    private View rootView;
    private Context context;

    private WebView mWebView;
    private ProgressBar progress;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_news, container, false);
        context = container.getContext();

        if(!isInternet.active(context))
        {
            rootView.findViewById(R.id.NoInternet).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.NewsContent).setVisibility(View.GONE);
        }

        //Веб форма
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebView.setWebChromeClient(new MyWebViewClient());
        //mWebView.setWebViewClient(new CustomWebViewClient());
        progress = (ProgressBar) rootView.findViewById(R.id.progressView);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(isInternet.API + "news");
        
        // Inflate the layout for this fragment
        return rootView;
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            NewsFragment.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);

        if(progress == 100){ //По окончанию загрузки пропадает
            this.progress.setVisibility(View.GONE);
        }
    }

}
