package org.dualcom.xai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.dualcom.xai.MyClass.MyPHP;
import org.dualcom.xai.MyClass.Storage;

import java.util.concurrent.ExecutionException;

/**
 * Created by Виталий on 06.04.2015.
 */
public class GetUpdate extends Activity {

    Context context = this;
    private String schedule = "";

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
        setContentView(R.layout.update_get);

        Button GetMD5 = (Button) findViewById(R.id.GetMD5);

        GetMD5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = Storage.loadData(context, "NOW_GROUP");

                if(isNetworkAvailable()) {
                    try {
                        schedule = new MyPHP().execute("schedule2.php",
                                "group=" + group).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Storage.saveData(context, group + "", schedule);

                    Storage.saveData(context,Storage.loadData(context, "NOW_GROUP")+"md5",Storage.loadData(context, "tmp_md5"));

                }

                Intent intent = new Intent(GetUpdate.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
