package com.milly.cm.millysadventures;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FlowerLevel extends AppCompatActivity {
    private LightTimer gameTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_flower_level);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        gameTimer = new LightTimer((ImageView)findViewById(R.id.flowerImage),40000,1000, (SensorManager) getSystemService(Context.SENSOR_SERVICE), (TextView)findViewById(R.id.secondText));
        gameTimer.start();
    }

    @Override
    protected void onResume(){
        Log.d("Sensor","OnResume");
        super.onResume();
        gameTimer.registerSensor();
    }

    @Override
    protected void onStop(){
        Log.d("Sensor","OnStop");
        gameTimer.stopSensor();
        super.onStop();
    }
}





