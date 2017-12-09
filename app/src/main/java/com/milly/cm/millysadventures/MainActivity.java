package com.milly.cm.millysadventures;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private SheepView sheepView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        TextView timeText = (TextView)findViewById(R.id.timer);
        ImageView scissorImage = (ImageView)findViewById(R.id.scissor);
        ImageView gloveImage = (ImageView)findViewById(R.id.glove);
        ConstraintLayout frameLayout = (ConstraintLayout)findViewById(R.id.score_frame);
        ImageView carrot1Image = (ImageView)findViewById(R.id.carrot1);
        ImageView carrot2Image = (ImageView)findViewById(R.id.carrot2);
        ImageView carrot3Image = (ImageView)findViewById(R.id.carrot3);

        ImageButton homeButton = (ImageButton)findViewById(R.id.androidhome);

        sheepView = new SheepView(MainActivity.this, size.x, size.y, timeText, scissorImage, gloveImage, frameLayout, carrot1Image, carrot2Image, carrot3Image);

        ConstraintLayout rootLayout = (ConstraintLayout) findViewById(R.id.erasable);
        rootLayout.addView(sheepView);
    }
}
