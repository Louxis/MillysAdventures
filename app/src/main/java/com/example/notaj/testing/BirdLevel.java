package com.example.notaj.testing;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Random;

/**
 * Bird Level Activity
 *
 * Initializes Bird level with all components.
 */
public class BirdLevel extends AppCompatActivity implements View.OnTouchListener{


    private ImageView birb1, birb2, birb3;
    private ImageView worm, worm2, worm3, mammaBirb;
    private TranslateAnimation anim;
    private Point displaySize;
    private CountDownTimer wormTimer, wormTimer2, wormTimer3, birbsTimer;
    private int score, carrots;
    private SharedPrefs prefs;
    private LocationTracker location;
    private GameScoreView scoreFrame;
    /**
     * In this method are created all needed components and their interactions.
     * @param savedInstanceState
     */
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
    //private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.birb_layout);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        findViewById(R.id.score_frame).setVisibility(View.GONE);

        prefs = new SharedPrefs(this);
        location = new LocationTracker(this, prefs, "birdPositionInfo", "Bird");

        mammaBirb = findViewById(R.id.mamma_birb);

        worm = findViewById(R.id.worm);
        worm2 = findViewById(R.id.worm2);
        worm3 = findViewById(R.id.worm3);

        birb1 = findViewById(R.id.baby_birb1_closed);
        birb2 = findViewById(R.id.baby_birb2_closed);
        birb3 = findViewById(R.id.baby_birb3_closed);

        wormTimer = wormTimer(worm, birb1, 40000, 66);
        wormTimer2 = wormTimer(worm2, birb2, 40000, 66);
        wormTimer3 = wormTimer(worm3, birb3, 40000, 66);

        mammaBirb.setVisibility(View.GONE);
        worm.setVisibility(View.GONE);
        worm2.setVisibility(View.GONE);
        worm3.setVisibility(View.GONE);

        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);



        clickListener(birb1, worm, displaySize.x/40, wormTimer);
        clickListener(birb2, worm2, displaySize.x/4, wormTimer2);
        clickListener(birb3, worm3, displaySize.x/2, wormTimer3);


        birbsTimer = initTimer(birb1, birb2, birb3,40000,3000);

        birbsTimer.start();

    }


    /**
     * Worms Animation Method.
     * This method is responsible for all the worm falling animations.
     * @param wormView worm's view.
     */
    public void wormAnimation(final ImageView wormView){
        final int amountToMoveDown = Math.round(displaySize.y/3-40);
        final int duration = 1500;
        anim = new TranslateAnimation(0, 0, 0, amountToMoveDown);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) wormView.getLayoutParams();
                lparams.topMargin += amountToMoveDown;
                wormView.setLayoutParams(lparams);
                wormView.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * This method is an onClick Listener for baby birds.
     *
     * If baby bird is clicked, it will trigger this method.
     * @param birb the baby bird
     * @param wormView the worm that is related to that bird
     * @param mammaLeftMargin the mamma bird
     * @param animationTimer the timer that is related to worm's animation
     */
    public void clickListener(ImageView birb, final ImageView wormView, final int mammaLeftMargin, final CountDownTimer animationTimer){
        birb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ConstraintLayout.LayoutParams wormParams = (ConstraintLayout.LayoutParams) wormView.getLayoutParams();
                wormParams.topMargin = displaySize.y/4;
                wormParams.leftMargin = 0;
                wormView.setLayoutParams(wormParams);
                wormView.setVisibility(View.VISIBLE);

                wormAnimation(wormView);
                wormView.startAnimation(anim);
                animationTimer.start();


                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mammaBirb.getLayoutParams();
                params.leftMargin = mammaLeftMargin;
                if(mammaBirb.getVisibility() != View.VISIBLE){
                    mammaBirb.setVisibility(View.VISIBLE);
                }
                mammaBirb.setLayoutParams(params);
            }



        });

    }

    /**
     * This timer will check if the worm touched the bird while animating
     * @param image worm
     * @param target baby bird
     * @param timeleft total animation time
     * @param timeskips ticks
     * @return timer
     */
    public CountDownTimer wormTimer (final ImageView image, final ImageView target, int timeleft, int timeskips){

        CountDownTimer timer = new CountDownTimer(timeleft, timeskips) {
            public void onTick(long millisUntilFinished) {
                if((image.getBottom() >= target.getTop() + 40) && target.getContentDescription().equals("opened")){
                    image.setVisibility(View.INVISIBLE);
                    score += 1;
                    this.cancel();
                    this.onFinish();
                }

            }
            public void onFinish() {
            }
        };
        return timer;
    }

    /**
     * This timer initializes the birds hunger action.
     *
     * @param birb1
     * @param birb2
     * @param birb3
     * @param timeleft
     * @param timeskips
     * @return timer
     */
    public CountDownTimer initTimer(final ImageView birb1, final ImageView birb2, final ImageView birb3, int timeleft, int timeskips){
        CountDownTimer timer = new CountDownTimer(timeleft, timeskips) {
            ImageView[] birbs = new ImageView[]{birb1, birb2, birb3};
            Random rnd = new Random();


            public void onTick(long millisUntilFinished) {
                int value = rnd.nextInt(3);
                openCloseMouths(birbs, value);
            }
            public void onFinish() {
                for(int i = 0; i < 3; i++){
                        birbs[i].setImageResource(R.drawable.baby_bird_closed);
                        birbs[i].setContentDescription("closed");
                        birbs[i].setClickable(false);
                }
                calculateCarrots();
                //scoreview && nextLevel
                scoreFrame = new GameScoreView(BirdLevel.this, carrots, Testing.class);
                prefs.getSharedPrefsEditor().putInt("BirdScore", carrots);

                prefs.getSharedPrefsEditor().commit();

                location.turnOffListener();

            }
        };
        return timer;
    }

    /**
     * This method is a helper for the birds timer.
     *
     * Sets the passed bird's mouth to opened, closing the other birds.
     * @param birbs array of all birds
     * @param mouthToOpen position of the bird to open mouth
     */
    public void openCloseMouths(ImageView[] birbs, int mouthToOpen){
        birbs[mouthToOpen].setImageResource(R.drawable.baby_birb);
        birbs[mouthToOpen].setContentDescription("opened");
        for(int i = 0; i < 3; i++){
            if(i != mouthToOpen){
                birbs[i].setImageResource(R.drawable.baby_bird_closed);
                birbs[i].setContentDescription("closed");

            }
        }

    }

    /**
     * Method that calculates the number of carrots based on the points gain.
     */
    public void calculateCarrots(){
        if(score <= 5){
            carrots = 1;
        }else if(score > 5 && score <= 10){
            carrots = 2;
        }else{
            carrots = 3;
        }
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
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


}
