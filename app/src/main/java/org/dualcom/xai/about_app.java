package org.dualcom.xai;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.dualcom.xai.MyClass.DATE;

public class about_app extends Activity {

   String version = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final TextView NAME_TEXT = (TextView) findViewById(R.id.name_text);
        final TextView VERSION_TEXT = (TextView) findViewById(R.id.version_text);
        final Button CLOSE_ABOUT = (Button) findViewById(R.id.close_about);
        final TextView YEAR_TEXT = (TextView) findViewById(R.id.year_text);

        NAME_TEXT.setText(getString(R.string.app_name));
        VERSION_TEXT.setText(getString(R.string.app_version) + " " + version);
        YEAR_TEXT.setText("(Quenix™ Software © 2015-" + DATE.getYear() + ")");

        CLOSE_ABOUT.setOnClickListener(new View.OnClickListener() {
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
