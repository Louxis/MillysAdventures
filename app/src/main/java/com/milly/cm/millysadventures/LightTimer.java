package com.milly.cm.millysadventures;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Visibility;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
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
    private ImageView beeView;
    private TranslateAnimation beeAnimation;
    private int score;
    //Used to calculate time offset
    private long baseTime;
    //Calculate time bonus
    private boolean penalty = false;
    private boolean mod1 = false;
    private boolean mod2 = false;
    //change bulb tick rate
    private boolean lowF = false;
    private boolean midF = false;
    private boolean highF = false;
    private boolean vHighF = false;
    private Activity level;
    private boolean ended = false;

    public LightTimer (Activity level, ImageView image, long timeleft, int timeskips, SensorManager manager, TextView debugText){
        this.image = image;
        this.timeleft = timeleft;
        this.baseTime = timeleft;
        this.timeskips = timeskips;
        mSensorManager = manager;
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.level = level;
        this.debugText = debugText;
    }

    public void setBulb(ImageView bulb){
        this.bulbView = bulb;
    }

    public void playBee() {
        this.beeView =  (ImageView)level.findViewById(R.id.beeImg);
        int beeOffset = 160;
        int centerScreen = level.getResources().getDisplayMetrics().widthPixels/2 + ( beeOffset / 2);
        beeView.setX(beeView.getX() + beeOffset);
        beeAnimation = new TranslateAnimation(0.0f, -centerScreen + beeOffset,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        beeAnimation.setDuration(5000);
        beeAnimation.setFillAfter(true);
        beeView.setVisibility(View.VISIBLE);
        beeView.startAnimation(beeAnimation);
    }

    public int getScore(){
        return score;
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

    private final SensorEventListener mListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (debugText != null){
                debugText.setText("Normal: " + event.values[0] + "Shortened: " + event.values[0]/10);
            }
            if(event.values[0] < 150 && !lowF){
                changeBulbTicking(250);
                lowF = true;
                midF = false;
                vHighF = false;
                highF = false;
            } else if (event.values[0] < 350 && event.values[0] >= 150 && !midF) {
                changeBulbTicking(500);
                midF = true;
                lowF = false;
                highF = false;
                vHighF = false;
            } else if(event.values[0] < 750 && event.values[0] >= 350 && !highF){
                changeBulbTicking(750);
                lowF = false;
                midF = false;
                highF = true;
                vHighF = false;
            } else if(event.values[0] < 1000 && event.values[0] >= 350 && !vHighF){
                changeBulbTicking(1000);
                vHighF = true;
                lowF = false;
                midF = false;
                highF = false;
            }
            //-----
            if(event.values[0] < 150 && !penalty){
                stop();
                timeleft += 5000;
                penalty = true;
                mod1 = false;
                mod2 = false;
                score = 1;
                start();
            }
            if(event.values[0] < 350 && event.values[0] >= 150 && !mod1){
                stop();
                timeleft -= 4000;
                penalty = false;
                mod2 = false;
                mod1 = true;
                score = 2;
                start();
            }
            if(event.values[0] < 1000 && event.values[0] >= 350 && !mod2){
                stop();
                timeleft -= 5000;
                penalty = false;
                mod1 = false;
                mod2 = true;
                score = 3;
                start();
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
                        if(!ended) {
                            playBee();
                            stopSensor();
                            ended = true;
                        }
                    }
                }
                timeleft = millisUntilFinished;
            }
            public void onFinish() {
                Log.d("FinishTest","I finished!");
                if (debugText != null){
                    debugText.setText(score + "");
                }
                //TO-DO enable next level
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
