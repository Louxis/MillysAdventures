package com.example.notaj.testing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HighScore extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener{

    private SharedPrefs prefs;
    private SupportMapFragment mapFragment;
    private LocationTracker location;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_high_score);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);


        prefs = new SharedPrefs(this);
        location = new LocationTracker(this, prefs, "userPositionInfo", "Player");


        ImageButton backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(HighScore.this, Testing.class));

            }
        });

        TextView bunnyScore = (TextView) findViewById(R.id.bunnyScore);
        TextView squirrelScore = (TextView) findViewById(R.id.squirleScore);
        TextView sheepScore = (TextView) findViewById(R.id.sheepScore);
        TextView flowerScore = (TextView) findViewById(R.id.flowerScore);
        TextView birdScore = (TextView) findViewById(R.id.birdScore);

        if (prefs.getSharedPrefs().getInt("BunnyScore", 0) == 0) {
            bunnyScore.setText("0");
        } else {
            bunnyScore.setText("" + prefs.getSharedPrefs().getInt("BunnyScore", 0));
        }
        if (prefs.getSharedPrefs().getInt("SquirrelScore", 0) == 0) {
            squirrelScore.setText("0");
        } else {
            squirrelScore.setText("" + prefs.getSharedPrefs().getInt("SquirrelScore", 0));
        }
        if (prefs.getSharedPrefs().getInt("SheepScore", 0) == 0) {
            sheepScore.setText("0");
        } else {
            sheepScore.setText("" + prefs.getSharedPrefs().getInt("SheepScore", 0));
        }
        if (prefs.getSharedPrefs().getInt("FlowerScore", 0) == 0) {
            flowerScore.setText("0");
        } else {
            flowerScore.setText("" + prefs.getSharedPrefs().getInt("FlowerScore", 0));
        }
        if (prefs.getSharedPrefs().getInt("BirdScore", 0) == 0) {
            birdScore.setText("0");
        } else {
            birdScore.setText("" + prefs.getSharedPrefs().getInt("BirdScore", 0));
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        backBtn.startAnimation(buttonAnimation);

        ConstraintLayout mapView = (ConstraintLayout) findViewById(R.id.mapFrame);
        Animation mapAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        mapView.startAnimation(mapAnimation);

        ConstraintLayout scoreView = (ConstraintLayout) findViewById(R.id.scoreFrame);
        Animation scoreAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right);
        scoreView.startAnimation(scoreAnimation);

    }

    @Override
    public void onResume(){

        super.onResume();
        if(getFragmentManager().findFragmentByTag("map") != null){
            Fragment currentFragment = getFragmentManager().findFragmentByTag("map");
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.detach(currentFragment);
            fragmentTransaction.attach(currentFragment);
            fragmentTransaction.commit();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        String[] list;
        String defaultInfo = prefs.getSharedPrefs().getString("userPositionInfo", "0.0,0.0,Error");

        if(prefs.getSharedPrefs().getString("GPS", "").equals("true")) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                        1);

            } else {
                list = prefs.getSharedPrefs().getString("userPositionInfo", defaultInfo).split(",");




                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.carrot2tiny))
                        .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                        .title(list[2]));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])), 0));
                if (prefs.getSharedPrefs().getInt("BunnyScore", 0) != 0) {
                    list = prefs.getSharedPrefs().getString("bunnyPositionInfo", defaultInfo).split(",");


                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bunny_small))
                            .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                            .title(list[2]));
                }


                if (prefs.getSharedPrefs().getInt("SquirrelScore", 0) != 0) {
                    list = prefs.getSharedPrefs().getString("squirrelPositionInfo", defaultInfo).split(",");


                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.squirrel_small))
                            .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                            .title(list[2]));
                }

                if (prefs.getSharedPrefs().getInt("SheepScore", 0) != 0) {
                    list = prefs.getSharedPrefs().getString("sheepPositionInfo", defaultInfo).split(",");


                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.sheep_small))
                            .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                            .title(list[2]));
                }

                if (prefs.getSharedPrefs().getInt("FlowerScore", 0) != 0) {
                    list = prefs.getSharedPrefs().getString("flowerPositionInfo", defaultInfo).split(",");


                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bee_small))
                            .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                            .title(list[2]));
                }

                if (prefs.getSharedPrefs().getInt("BirdScore", 0) != 0) {
                    list = prefs.getSharedPrefs().getString("birdPositionInfo", defaultInfo).split(",");


                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bird_small))
                            .position(new LatLng(Double.parseDouble(list[0]), Double.parseDouble(list[1])))
                            .title(list[2]));

                }
            }
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
