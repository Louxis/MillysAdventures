package com.milly.cm.millysadventures;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
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
        gameTimer = new LightTimer(this,(ImageView)findViewById(R.id.flowerImage),30000,1000, (SensorManager) getSystemService(Context.SENSOR_SERVICE), (TextView)findViewById(R.id.secondText));
        ImageView bulbImage = (ImageView)findViewById(R.id.bulbImg);
        ImageView beeImage = (ImageView)findViewById(R.id.beeImg);
        if(bulbImage != null){
            gameTimer.setBulb(bulbImage);
        }
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





