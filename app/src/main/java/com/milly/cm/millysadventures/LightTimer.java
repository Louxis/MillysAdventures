package com.milly.cm.millysadventures;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jcper on 12/3/2017.
 */

public class LightTimer {
    private ImageView image;
    private int timeleft;
    private int timeskips;
    private SensorManager mSensorManager;
    private Sensor lightSensor;
    private TextView debugText;

    public LightTimer (ImageView image, int timeleft, int timeskips, SensorManager manager, TextView debugText){
        this.image = image;
        this.timeleft = timeleft;
        this.timeskips = timeskips;
        mSensorManager = manager;
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.debugText = debugText;
    }

    private final SensorEventListener mListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("Light", event.values[0]/10 + "");
            if (debugText != null){
                debugText.setText("Normal: " + event.values[0] + "Shortened: " + event.values[0]/10);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void registerSensor(){
        mSensorManager.registerListener(mListener,lightSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopSensor(){
        mSensorManager.unregisterListener(mListener);
    }

    public void start(){
        new CountDownTimer(timeleft, timeskips) {
            public void onTick(long millisUntilFinished) {
                if(image != null){
                    if (millisUntilFinished <= 30000 && millisUntilFinished > 20000){
                        image.setImageResource(R.drawable.flower_stage_2);
                    }else if(millisUntilFinished <= 20000 && millisUntilFinished > 10000){
                        image.setImageResource(R.drawable.flower_stage_3);
                    }else if(millisUntilFinished <= 10000){
                        image.setImageResource(R.drawable.flower_stage_4);
                    }
                }
            }
            public void onFinish() {
            }
        }.start();
    }
}
