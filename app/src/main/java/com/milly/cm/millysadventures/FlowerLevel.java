package com.milly.cm.millysadventures;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class FlowerLevel extends AppCompatActivity {
    private LightTimer gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_flower_level);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        gameTimer = new LightTimer(this);
        gameTimer.start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameTimer.registerSensor();
    }

    @Override
    protected void onStop(){
        gameTimer.stopSensor();
        super.onStop();
    }
}





