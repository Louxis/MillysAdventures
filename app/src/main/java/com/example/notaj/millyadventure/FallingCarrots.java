package com.example.notaj.millyadventure;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
    private SharedPrefs prefs;
    private LocationTracker location;
    private int carrotsCaugt = 0, totalCarrots = 0, totalWorms = 0, totalWormsCaught = 0;
    private GameScoreView scoreFrame;
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
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        findViewById(R.id.score_frame).setVisibility(View.GONE);
        mContentView = findViewById(R.id.fullscreen_content);
        basket = (ImageView) findViewById(R.id.basketImageView);
        pickup = (ImageView) findViewById(R.id.pickupImageView);
        ConstraintLayout.LayoutParams basketparams = (ConstraintLayout.LayoutParams) pickup.getLayoutParams();
        basketparams.verticalBias = 0;
        pickup.setLayoutParams(basketparams);

        prefs = new SharedPrefs(this);
        location = new LocationTracker(this, prefs, "bunnyPositionInfo", "Bunny");

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        /* Upon interacting with UI controls, delay any scheduled hide()
           operations to prevent the jarring behavior of controls going away
           while interacting with the UI.
         findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Force app to remain in landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Set touchListener on basket
        basket.setOnTouchListener(this);

        // Set display size
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        // Falling animation settings
        int amountToMoveDown = (int) Math.ceil(displaySize.y*0.75);
        int duration = 2000;

        // Falling animation Creation
        createFallingAnimation(amountToMoveDown, duration);

        //Game Timer Start
        timer(45000, 66);

    }

    /**
     * Method that creates the pickup falling animation
     * @param amountToMoveDown pickup movement amount based on display size
     * @param duration animation duration - falling speed
     */
    private void createFallingAnimation (final int amountToMoveDown, int duration){
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
    }


    /**
     * Method that verifies the position of the pickup, updates the level's counters,
     * resets the pickup position and removes the pickup from the ground if the player doesn't
     * pick it up.
     * @param totalTime Level duration
     * @param timeInterval Frequency of pickup detection
     */
    private void timer(int totalTime, int timeInterval){
        pickup.startAnimation(anim);
        pickup.setContentDescription("carrot");
        new CountDownTimer(totalTime, timeInterval) {

            public void onTick(long millisUntilFinished) {

                if (basket.getRight() >= pickup.getLeft() && basket.getLeft() <= pickup.getRight() && basket.getTop() <= pickup.getBottom()) {

                    if(pickup.getContentDescription().equals("carrot") ){
                        totalCarrots+=1;
                        carrotsCaugt+=1;

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
                int finalScore = (carrotsCaugt - totalWormsCaught);

                location.turnOffListener();
                if(finalScore >= 20){
                    finalScore = 3;
                }else if(finalScore >= 10 && finalScore < 20){
                    finalScore = 2;
                }else{
                    finalScore = 1;
                }
                prefs.getSharedPrefsEditor().putInt("BunnyScore", finalScore);
                prefs.getSharedPrefsEditor().commit();
                scoreFrame = new GameScoreView(FallingCarrots.this, finalScore, SheepLevel.class);
            }
        }.start();
    }


    /**
     * Method that randomizes both the type of pickup and the next position that it appears
     * on the screen and restarts the falling animation
     */
    private void randomisePickupPosition(){

        rand = new Random();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pickup.getLayoutParams();
        params.horizontalBias = (float) (rand.nextInt(8) / 10.0 + 0.2);
        params.topMargin = 0;

        if(rand.nextInt(7) == 6){
            pickup.setImageResource(R.drawable.wormvertical80);
            pickup.setContentDescription("worm");
        }else if(pickup.getContentDescription().equals("worm")){
            pickup.setImageResource(R.drawable.carrot80);
            pickup.setContentDescription("carrot");
        }

        pickup.setLayoutParams(params);
        pickup.startAnimation(anim);
    }


    /**
     * Method that adds onTouch interface to an object
     * @param v Object that's going to have onTouch interface
     * @param event Type of event that the object suffered
     * @return
     */
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
                params.leftMargin = (int) (x-dx);
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
        //mControlsView.setVisibility(View.GONE);
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
     *
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
