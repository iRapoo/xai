package org.dualcom.xai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;

import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.dualcom.xai.MyClass.Storage;
import org.w3c.dom.Text;

@SuppressWarnings("deprecation")
public class AlarmActivity extends Activity {

    Context context = this;
    private AlarmManagerBroadcastReceiver alarm;

    private static final int SELECT_SOUND = 1;
    private String selectedSoundPath;
    public TextView AlarmState;
    public TextView CountTimer;
    public IconTextView ChangeMusicText;

    public int translatedProgress = 0;

    final String LOG_TAG = "myLogs";

    MediaPlayer mediaPlayer;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        int color = ContextCompat.getColor(context, R.color.extra_color);

        ChangeMusicText = (IconTextView) findViewById(R.id.ChangeMusicText);
        ChangeMusicText.setText("{fa-music} " + getResources().getString(R.string.select_audio));

        alarm = new AlarmManagerBroadcastReceiver();
        boolean StateSwich = (Storage.loadData(context, "SWITCH").equals("true")) ? true : false;

        AlarmState = (TextView) findViewById(R.id.AlarmState);
        AlarmState.setText((StateSwich) ? getResources().getString(R.string.alarm_on) : getResources().getString(R.string.alarm_off));

        Switch toggle = (Switch) findViewById(R.id.on_alarm);
        toggle.setChecked(StateSwich);
        toggle.getThumbDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Storage.saveData(context, "SWITCH", isChecked + "");
                if (isChecked) {
                    AlarmState.setText(getResources().getString(R.string.alarm_on));
                    alarm.SetAlarm(context);
                } else {
                    AlarmState.setText(getResources().getString(R.string.alarm_off));
                    alarm.CancelAlarm(context);
                }
            }
        });

        final int step = 10;
        final int max = 120;
        final int min = 30;

        CountTimer = (TextView) findViewById(R.id.CountTimer);
        SeekBar SeekSleep = (SeekBar) findViewById(R.id.SeekSleep);
        SeekSleep.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // полоска
        SeekSleep.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // кругляшок

        SeekSleep.setMax( (max - min) / step );

        int StatePicker = (Storage.emptyData(context,"NumberPicker")) ? min : Integer.parseInt(Storage.loadData(context,"NumberPicker"));
        StatePicker = (StatePicker-min)/step;
        SeekSleep.setProgress(StatePicker);

        CountTimer.setText(getResources().getString(R.string.alarm_per_minutes) + " " + (min + (StatePicker * step)) + " " + getResources().getString(R.string.alarm_minutes));

        SeekSleep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                translatedProgress = min + (progress * step);

                Storage.saveData(context, "NumberPicker", translatedProgress + "");
                CountTimer.setText(getResources().getString(R.string.alarm_per_minutes) + " " + translatedProgress + " " + getResources().getString(R.string.alarm_minutes));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /*np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Storage.saveData(context, "NumberPicker", newVal + "");
            }
        });*/

        final LinearLayout ChangeMusic = (LinearLayout) findViewById(R.id.ChangeMusic);
        if(!Storage.emptyData(context,"SoundPath"))
            ChangeMusicText.setText("{fa-music} " + Storage.loadData(context, "SoundPath")
                    .substring(Storage.loadData(context, "SoundPath").lastIndexOf('/') + 1));

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
                ChangeMusicText.setText("{fa-music} " + selectedSoundPath.substring(selectedSoundPath.lastIndexOf('/') + 1));
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
