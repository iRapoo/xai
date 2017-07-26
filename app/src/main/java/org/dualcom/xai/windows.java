package org.dualcom.xai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class windows extends Activity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windows);

        final Intent intent = getIntent();
        String Title = intent.getStringExtra("title");
        String Text = intent.getStringExtra("text");
        String YesBtn = intent.getStringExtra("yes_btn");

        final TextView title = (TextView) findViewById(R.id.title_win);
        final TextView text = (TextView) findViewById(R.id.text_win);
        final Button yes_btn = (Button) findViewById(R.id.YesBtn);
        final Button cancel = (Button) findViewById(R.id.cancel_win);

        title.setText(Title);
        text.setText(Text);
        yes_btn.setText(YesBtn);

        if(YesBtn!=null) {

            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent.putExtra("status", true);
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });

        }else{

            yes_btn.setVisibility(yes_btn.GONE);
            cancel.setText("OK");

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

    }

    private void closeActivity() {
        this.finish();
    }

}
