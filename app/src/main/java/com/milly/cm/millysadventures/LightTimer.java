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
    private ImageView flowerImage;
    private long timeLeft;
    private int timeSkips;
    private SensorManager mSensorManager;
    private Sensor lightSensor;
    private TextView debugText;
    private CountDownTimer globalTimer;
    private ImageView bulbView;
    private int score;
    private Activity level;
    private boolean ended = false;
    //constants
    private final int TIME_LEFT = 30000;
    private final int TIME_SKIPS = 1000;
    private final int BEE_OFFSET = 160;

    public LightTimer (Activity level){
        this.level = level;
        this.timeLeft = TIME_LEFT;
        this.timeSkips = TIME_SKIPS;
        mSensorManager = (SensorManager) level.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.flowerImage = (ImageView)level.findViewById(R.id.flowerImage);
        this.debugText = (TextView)level.findViewById(R.id.secondText);
        this.bulbView = (ImageView)level.findViewById(R.id.bulbImg);
    }

    public void playBee() {
        ImageView beeView =  (ImageView)level.findViewById(R.id.beeImg);
        int centerScreen = level.getResources().getDisplayMetrics().widthPixels/2 + ( BEE_OFFSET / 2);
        beeView.setX(beeView.getX() + BEE_OFFSET);
        TranslateAnimation beeAnimation = new TranslateAnimation(0.0f, -centerScreen + BEE_OFFSET,
                0.0f, 0.0f);
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
        //Calculate time bonus
        private boolean penalty = false;
        private boolean mod1 = false;
        private boolean mod2 = false;
        //change bulb tick rate
        private boolean lowF = false;
        private boolean midF = false;
        private boolean highF = false;
        private boolean vHighF = false;
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
                timeLeft += 5000;
                penalty = true;
                mod1 = false;
                mod2 = false;
                score = 1;
                start();
            }
            if(event.values[0] < 350 && event.values[0] >= 150 && !mod1){
                stop();
                timeLeft -= 4000;
                penalty = false;
                mod2 = false;
                mod1 = true;
                score = 2;
                start();
            }
            if(event.values[0] < 1000 && event.values[0] >= 350 && !mod2){
                stop();
                timeLeft -= 5000;
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
        globalTimer = new CountDownTimer(timeLeft, timeSkips) {
            public void onTick(long millisUntilFinished) {
                Log.d("Test","Started!");
                if(flowerImage != null){
                    if (millisUntilFinished <= TIME_LEFT*0.75 && millisUntilFinished > TIME_LEFT*0.5){
                        flowerImage.setImageResource(R.drawable.flower_stage_2);
                    }else if(millisUntilFinished <= TIME_LEFT*0.5 && millisUntilFinished > TIME_LEFT*0.25){
                        flowerImage.setImageResource(R.drawable.flower_stage_3);
                    }else if(millisUntilFinished <= 10000){
                        flowerImage.setImageResource(R.drawable.flower_stage_4);
                        if(!ended) {
                            playBee();
                            stopSensor();
                            ended = true;
                        }
                    }
                } else{
                    flowerImage = (ImageView)level.findViewById(R.id.flowerImage);
                }
                timeLeft = millisUntilFinished;
            }
            public void onFinish() {
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
