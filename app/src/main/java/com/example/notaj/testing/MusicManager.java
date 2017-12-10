package com.example.notaj.testing;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Created by Louxis on 10/12/2017.
 */


public class MusicManager {

    public static void start(Activity activity){
        MediaPlayer player = MediaPlayer.create(activity, R.raw.milly_soundtrack);
        player.setLooping(true);
        player.start();

    }


}
