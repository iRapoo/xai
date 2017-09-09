package org.dualcom.xai;

import android.app.Activity;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        PhotoView photoView = (PhotoView) findViewById(R.id.khai_map);
        photoView.setImageResource(R.drawable.khai_map);

    }
}
