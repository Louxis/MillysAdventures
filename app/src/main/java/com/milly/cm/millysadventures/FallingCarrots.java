package com.milly.cm.millysadventures;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FallingCarrots extends AppCompatActivity implements View.OnTouchListener{

    private ImageView basket, pickup, touchView;
    private Random rand = null;
    private ConstraintLayout.LayoutParams params;
    private TranslateAnimation anim;
    private Point displaySize;
    private int carrotsCaugt = 0, totalCarrots = 0, totalWorms = 0, totalWormsCaught = 0;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_falling_carrots);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        basket = (ImageView) findViewById(R.id.basketImageView);
        pickup = (ImageView) findViewById(R.id.pickupImageView);
        ConstraintLayout.LayoutParams basketparams = (ConstraintLayout.LayoutParams) pickup.getLayoutParams();
        basketparams.verticalBias = 0;
        pickup.setLayoutParams(basketparams);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Force app to remain in landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Set touchListener on basket
        basket.setOnTouchListener(this);

        // Set displaysize
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        //Animation paramaters
        final int amountToMoveDown = (displaySize.y - 140);//displaySize.y/4);
        final int duration = 2000;

        anim = new TranslateAnimation(0, 0, 0, amountToMoveDown);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator() );
        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) pickup.getLayoutParams();
                lparams.topMargin += amountToMoveDown;
                pickup.setLayoutParams(lparams);

            }
        });
        timer(60000, 66);

    }


    private void timer(int totalTime, int timeInterval){
        pickup.startAnimation(anim);
        pickup.setContentDescription("carrot");
        new CountDownTimer(totalTime, timeInterval) {

            public void onTick(long millisUntilFinished) {

                if (basket.getRight() >= pickup.getLeft() && basket.getLeft() <= pickup.getRight() && basket.getTop() <= pickup.getBottom()) {

                    if(pickup.getContentDescription().equals("carrot") ){
                        totalCarrots+=1;
                        carrotsCaugt+=1;
                        Log.d("Carrot",totalCarrots + " Total carrots. \n" + carrotsCaugt + " Caugt");
                    }else if(pickup.getContentDescription().equals("worm") ){
                        totalWorms+=1;
                        totalWormsCaught+=1;
                    }
                    randomisePickupPosition();

                }
                else if ( basket.getTop() <= pickup.getBottom()){
                    if(pickup.getContentDescription().equals("carrot") ){
                        totalCarrots+=1;
                    }
                    else if(pickup.getContentDescription().equals("worm") ){
                        totalWorms+=1;
                    }
                    randomisePickupPosition();
                }

            }

            public void onFinish() {
            }
        }.start();

    }

    private void randomisePickupPosition(){
        rand = new Random();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pickup.getLayoutParams();
        //doesn't take into account screen size
        params.horizontalBias = (float) (rand.nextInt(9) / 10.0 + 0.1);
        //params.verticalBias = 0;
        params.topMargin = 0;
        int r = rand.nextInt(7);
        if(r == 6){
            pickup.setImageResource(R.drawable.wormvertical80);
            pickup.setContentDescription("worm");
        }else if(pickup.getContentDescription().equals("worm")){
            pickup.setImageResource(R.drawable.carrot80);
            pickup.setContentDescription("carrot");
        }
        pickup.setLayoutParams(params);
        pickup.startAnimation(anim);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        touchView = (ImageView) v;
        float x, dx = pickup.getWidth()*2;
        params = (ConstraintLayout.LayoutParams) touchView.getLayoutParams();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN :
            {

            }
            break;
            case MotionEvent.ACTION_MOVE :
            {
                x = event.getRawX();
                params.leftMargin = (int) (x-100);
                touchView.setLayoutParams(params);
            }
            break;
            case MotionEvent.ACTION_UP: {

            }
        }
        return true; // indicate event was handled
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
