package org.dualcom.xai.MyClass;

import android.content.Context;
import android.net.Uri;
import android.media.MediaPlayer;

import org.dualcom.xai.R;

/**
 * Created by Виталий on 31.01.2016.
 */
public class SoundPlay {

    static MediaPlayer mediaPlayer;

    public static void start(Context context){

        Uri selectedSoundUri;
        selectedSoundUri = Uri.parse(Storage.loadData(context, "SoundPathUri"));
        //Windows.alert(context,"",selectedSoundUri+"");

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        /*mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        try {
            mediaPlayer.setDataSource(context, selectedSoundUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.start();*/
    }

    public static void stop(){
        mediaPlayer.stop();
    }

}
