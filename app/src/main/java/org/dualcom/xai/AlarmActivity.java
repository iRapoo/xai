package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;

import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import org.dualcom.xai.MyClass.Storage;

@SuppressWarnings("deprecation")
public class AlarmActivity extends Activity {

    Context context = this;
    private AlarmManagerBroadcastReceiver alarm;

    private static final int SELECT_SOUND = 1;
    private String selectedSoundPath;
    public TextView file_path;

    final String LOG_TAG = "myLogs";

    MediaPlayer mediaPlayer;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        alarm = new AlarmManagerBroadcastReceiver();
        boolean StateSwich = (Storage.loadData(context, "SWITCH").equals("true")) ? true : false;

        Switch toggle = (Switch) findViewById(R.id.on_alarm);
        toggle.setChecked(StateSwich);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Storage.saveData(context, "SWITCH", isChecked + "");
                if (isChecked) {
                    alarm.SetAlarm(context);
                } else {
                    alarm.CancelAlarm(context);
                }
            }
        });

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMaxValue(120);
        np.setMinValue(10);

        int StatePicker = (Storage.emptyData(context,"NumberPicker")) ? 30 : Integer.parseInt(Storage.loadData(context,"NumberPicker"));
        np.setValue(StatePicker);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Storage.saveData(context, "NumberPicker", newVal + "");
            }
        });

        final LinearLayout ChangeMusic = (LinearLayout) findViewById(R.id.ChangeMusic);
        file_path = (TextView) findViewById(R.id.file_path);
        if(!Storage.emptyData(context,"SoundPath"))
            file_path.setText(Storage.loadData(context, "SoundPath"));

        //file_path.setText("alarm.mp3"); //Временный файл

        ChangeMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Sound"), SELECT_SOUND);

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SOUND) {
                Uri selectedSoundUri = data.getData();
                selectedSoundPath = getPath(selectedSoundUri);
                file_path.setText(selectedSoundPath);
                Storage.saveData(context, "SoundPath", selectedSoundPath);
                Storage.saveData(context,"SoundPathUri", selectedSoundUri+"");
                //SoundPlay.start(context);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
