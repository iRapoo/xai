package org.dualcom.xai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.dualcom.xai.MyClass.Storage;

/**
 * Created by Виталий on 01.02.2015.
 */
public class guide4 extends Fragment {

    Context context;
    //private WebView mWebView2;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.guide4, container, false);

        context = container.getContext();

        Button guide_btn = (Button) view.findViewById(R.id.guide_btn);

        guide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.saveData(context, "GUIDE", "true");

                Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Веб форма
        /*mWebView2 = (WebView) view.findViewById(R.id.webView2);
        // включаем поддержку JavaScript
        mWebView2.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebView2.loadUrl("http://m.vk.com/e_xai");*/
        //**********/

        return view;
    }
}
