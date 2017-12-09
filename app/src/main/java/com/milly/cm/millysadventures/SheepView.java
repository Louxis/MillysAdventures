package com.milly.cm.millysadventures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.CountDownTimer;
import android.provider.SyncStateContract;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dmpcr on 07-Dec-17.
 */

public class SheepView extends View {

    private ConstraintLayout frame;
    private ImageView carrot1;
    private ImageView carrot2;
    private ImageView carrot3;
    private Animation fadeInAnimation;

    private ImageView scissor;
    private ImageView glove;

    private TextView time;
    private CountDownTimer countDownTimer;
    private int timeLeft;

    private Bitmap emptyBitmap;
    private Bitmap vBitmap;
    private Canvas vCanvas = new Canvas();
    private Paint vPaint = new Paint();
    private Path vPath = new Path();

    private int score;


    public SheepView(Context context, int width, int height, TextView timeText, ImageView scissorImage, ImageView gloveImage, ConstraintLayout frameLayout, ImageView carrotImage1, ImageView carrotImage2, ImageView carrotImage3){
        super(context);
        this.time = timeText;
        this.scissor = scissorImage;
        this.glove = gloveImage;
        this.frame = frameLayout;
        this.carrot1 = carrotImage1;
        this.carrot2 = carrotImage2;
        this.carrot3 = carrotImage3;

        fadeInAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.enter_from_bottom);
        frame.setVisibility(INVISIBLE);
        startTime();

        Bitmap rawBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ribeiro);

        vBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
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

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(vBitmap, 0,0, null);
        super.onDraw(canvas);
        if(timeLeft != 0){
            vCanvas.drawPath(vPath, vPaint);
            if (vBitmap.sameAs(emptyBitmap)) {
                if(timeLeft <= 15){
                    this.score = 1;
                    carrot1.setVisibility(INVISIBLE);
                    carrot3.setVisibility(INVISIBLE);
                } else if (timeLeft <= 30 && timeLeft > 15 ){
                    this.score = 2;
                    carrot2.setVisibility(INVISIBLE);
                } else if (timeLeft > 30){
                    this.score = 3;
                }
                countDownTimer.cancel();
                this.setWillNotDraw(true);
                frame.setVisibility(VISIBLE);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.enter_from_bottom);
                frame.startAnimation(fadeInAnimation);
            }
        } else{
            this.setWillNotDraw(true);
        }
    }

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

    private void startTime(){
        timeLeft = 10;
        countDownTimer = new CountDownTimer(10 * 1000, 1000){
            @Override
            public void onTick(long timeUntilFinished){
                time.setText("" + timeUntilFinished/1000);
                if(timeLeft % 2 == 0){
                    scissor.setImageResource(R.drawable.open_scissor);
                    gloveAnimation();
                } else{
                    scissor.setImageResource(R.drawable.closed_scissor);
                }
                timeLeft--;
            }
            @Override
            public void onFinish(){
                timeLeft--;
                score = 1;
                frame.setVisibility(VISIBLE);
                carrot1.setVisibility(INVISIBLE);
                carrot3.setVisibility(INVISIBLE);
                frame.startAnimation(fadeInAnimation);
                time.setText("" + timeLeft);
            }
        };
        countDownTimer.start();
    }

    private void gloveAnimation(){
        TranslateAnimation gloveAnimation = new TranslateAnimation(0.0f, 200f, 0,0);
        gloveAnimation.setDuration(1500);
        gloveAnimation.setFillAfter(true);
        glove.startAnimation(gloveAnimation);
    }
}
