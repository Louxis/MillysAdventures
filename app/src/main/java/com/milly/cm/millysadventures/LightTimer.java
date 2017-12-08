package com.milly.cm.millysadventures;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jcper on 12/3/2017.
 */

public class LightTimer {
    private ImageView image;
    private long timeleft;
    private int timeskips;
    private SensorManager mSensorManager;
    private Sensor lightSensor;
    private TextView debugText;
    private CountDownTimer globalTimer;
    private ImageView bulbView;

    //Used to calculate time offset
    private long baseTime;
    //Calculate time bonus
    private boolean penalty = false;
    private boolean mod1 = false;
    private boolean mod2 = false;
    private boolean insta = false;

    public LightTimer (ImageView image, long timeleft, int timeskips, SensorManager manager, TextView debugText){
        this.image = image;
        this.timeleft = timeleft;
        this.baseTime = timeleft;
        this.timeskips = timeskips;
        mSensorManager = manager;
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.debugText = debugText;
    }

    public void setBulb(ImageView bulb){
        this.bulbView = bulb;
    }

    private void changeBulbTicking(int delay){
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(delay);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        if(bulbView != null){
            bulbView.startAnimation(animation);
        }
    }

    private boolean lowF = false;
    private boolean midF = false;
    private boolean highF = false;
    private final SensorEventListener mListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Log.d("Light", event.values[0]/10 + "");
            if (debugText != null){
                debugText.setText("Normal: " + event.values[0] + "Shortened: " + event.values[0]/10);
            }
            //bulb test
            if(event.values[0] < 150 && !lowF){
                changeBulbTicking(100);
                lowF = true;
                midF = false;
                highF = false;
            } else if (event.values[0] < 350 & event.values[0] >= 150 && !midF){
                changeBulbTicking(500);
                midF = true;
                lowF = false;
                highF = false;
            } else if(event.values[0] < 1000 && event.values[0] >= 350 && !highF){
                changeBulbTicking(1000);
                highF = true;
                lowF = false;
                midF = false;
            }
            if(event.values[0] < 150 && !penalty){
                Log.d("Flower","Penalty");
                stop();
                timeleft += 3000;
                penalty = true;
                mod1 = false;
                start();
            }
            if(event.values[0] < 350 && event.values[0] >= 150 && !mod1){
                Log.d("Flower","Mod1");
                stop();
                timeleft -= 4000;
                penalty = false;
                mod1 = true;
                start();
            }
            if(event.values[0] < 1000 && event.values[0] >= 350 && !mod2){
                Log.d("Flower","Mod2");
                stop();
                timeleft -= 3000;
                start();
            }
            if(event.values[0] > 1500){
                //insta = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void registerSensor(){
        mSensorManager.registerListener(mListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopSensor(){
        mSensorManager.unregisterListener(mListener);
    }

    public void start(){
        globalTimer = new CountDownTimer(timeleft, timeskips) {
            public void onTick(long millisUntilFinished) {
                if(image != null){
                    if (millisUntilFinished <= baseTime*0.75 && millisUntilFinished > baseTime*0.5){
                        image.setImageResource(R.drawable.flower_stage_2);
                    }else if(millisUntilFinished <= baseTime*0.5 && millisUntilFinished > baseTime*0.25){
                        image.setImageResource(R.drawable.flower_stage_3);
                    }else if(millisUntilFinished <= 10000){
                        image.setImageResource(R.drawable.flower_stage_4);
                    }
                }
                timeleft = millisUntilFinished;
            }
            public void onFinish() {
                stopSensor();
                //enable next level
            }
        };
        globalTimer.start();
    }

    private void stop(){
        if(globalTimer != null){
            globalTimer.cancel();
        }
    }
}
