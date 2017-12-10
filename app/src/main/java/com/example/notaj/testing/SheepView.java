package com.example.notaj.testing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class contains the logic of the Sheep Level;
 */

public class SheepView extends View {

    private AppCompatActivity activity;

    private GameScoreView scoreFrame;

    private ImageView scissor;
    private ImageView glove;

    private TextView time;
    private int timeLeft;

    private Bitmap emptyBitmap;
    private Bitmap vBitmap;
    private Canvas vCanvas = new Canvas();
    private Paint vPaint = new Paint();
    private Path vPath = new Path();
    private int score;



    /**
     *
     * @param activity the current Activity;
     * @param size the size of the screen in which the game is displayed;
     */
    public SheepView(AppCompatActivity activity, Point size){
        super(activity);
        this.activity = activity;
        this.scissor = (ImageView)activity.findViewById(R.id.scissor);
        this.glove = (ImageView)activity.findViewById(R.id.glove);
        this.time = (TextView)activity.findViewById(R.id.timer);


        startTime();

        Bitmap rawBitmap = BitmapFactory.decodeResource(this.activity.getResources(), R.drawable.ribeiro);

        vBitmap = Bitmap.createBitmap(size.x,size.y, Bitmap.Config.ARGB_8888);
        this.emptyBitmap = Bitmap.createBitmap(vBitmap.getWidth(), vBitmap.getHeight(), vBitmap.getConfig());

        vCanvas.setBitmap(vBitmap);
        vCanvas.drawBitmap(rawBitmap, (vCanvas.getWidth() - rawBitmap.getWidth()) /2,(vCanvas.getHeight() - rawBitmap.getHeight()) /2, null);

        vPaint.setAlpha(0);
        vPaint.setAntiAlias(true);
        vPaint.setStyle(Paint.Style.STROKE);
        vPaint.setStrokeJoin(Paint.Join.ROUND);
        vPaint.setStrokeCap(Paint.Cap.ROUND);
        vPaint.setStrokeWidth(150);
        vPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    /**
     * This method will allow the player to erase the Sheep's wool;
     * @param canvas where will erase the wool;
     */
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(vBitmap, 0,0, null);
        super.onDraw(canvas);
        if(timeLeft != 0) {
            vCanvas.drawPath(vPath, vPaint);
        }
    }

    /**
     * This method applies the different gameplay operations depending on the kind of event it records;
     * @param event kind of event that's being recorded;
     * @return bool depending on the touch event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float xPos = event.getX();
        float yPos = event.getY();

        switch ((event.getAction())){
            case MotionEvent.ACTION_DOWN:
                vPath.moveTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_MOVE:
                vPath.lineTo(xPos, yPos);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    /**
     * This method starts a timer that limits the time that the player has to complete the minigame;
     */
    private void startTime(){
        timeLeft = 45;
        new CountDownTimer(45 * 1000, 1000){

            public void onTick(long timeUntilFinished){
                time.setText("" + timeUntilFinished/1000);
                if(timeLeft % 2 == 0){
                    scissor.setImageResource(R.drawable.open_scissor);
                    gloveAnimation();
                } else{
                    scissor.setImageResource(R.drawable.closed_scissor);
                }
                timeLeft--;
                if (vBitmap.sameAs(emptyBitmap)) {
                    if(timeLeft <= 15){
                        score = 1;
                    } else if (timeLeft <= 30 && timeLeft > 15 ){
                        score = 2;
                    } else if (timeLeft > 30){
                        score = 3;
                    }
                    scoreFrame = new GameScoreView(activity, score, FlowerLevel.class);
                    ((SheepLevel)activity).getPrefs().getSharedPrefsEditor().putInt("SheepScore", score);

                    ((SheepLevel)activity).getPrefs().getSharedPrefsEditor().commit();

                    ((SheepLevel)activity).getLocation().turnOffListener();
                    this.cancel();
                }
            }
            @Override
            public void onFinish(){
                timeLeft--;
                score = 1;
                time.setText("" + timeLeft);
                scoreFrame = new GameScoreView(activity, score, FlowerLevel.class);
                ((SheepLevel)activity).getPrefs().getSharedPrefsEditor().putInt("SheepScore", score);

                ((SheepLevel)activity).getPrefs().getSharedPrefsEditor().commit();

                ((SheepLevel)activity).getLocation().turnOffListener();
            }
        }.start();
    }

    /**
     * This method animates the motion of the instruction glove;
     */
    private void gloveAnimation(){
        TranslateAnimation gloveAnimation = new TranslateAnimation(0.0f, 200f, 0,0);
        gloveAnimation.setDuration(1500);
        gloveAnimation.setFillAfter(true);
        glove.startAnimation(gloveAnimation);
    }
}
