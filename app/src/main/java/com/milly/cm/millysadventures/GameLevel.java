package com.milly.cm.millysadventures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Diogo on 08/12/2017.
 */
@Deprecated
public class GameLevel extends View {

    private Paint p;
    private Rect sprite1Bounds = new Rect(0,0,0,0);
    private Rect sprite2Bounds = new Rect(0,0,0,0);
    private Point sprite1;
    private Point sprite2;
    private Bitmap bm1 = null;
    private Bitmap bm2 = null;
    //Collision flag and point
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1,-1);
    private int sprite1Rotation = 0;


    public GameLevel(Context context, AttributeSet aSet) {
        super(context, aSet);
        p = new Paint();
        //load our bitmaps and set the bounds for the controller
        sprite1 = new Point(-1,-1);
        sprite2 = new Point(-1,-1);
        //Define a matrix so we can rotate the asteroid
        p = new Paint();
        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.carrot80);
        bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.basket80);
        sprite1Bounds = new Rect(0,0, bm1.getWidth(), bm1.getHeight());
        sprite2Bounds = new Rect(0,0, bm2.getWidth(), bm2.getHeight());
    }

    private boolean checkForCollision() {

        if (sprite1.x<0 && sprite2.x<0 && sprite1.y<0 && sprite2.y<0) {
            return false;
        }
        Rect r1 = new Rect(sprite1.x, sprite1.y, sprite1.x + sprite1Bounds.width(),  sprite1.y + sprite1Bounds.height());
        Rect r2 = new Rect(sprite2.x, sprite2.y, sprite2.x + sprite2Bounds.width(), sprite2.y + sprite2Bounds.height());
        Rect r3 = new Rect(r1);
        if(r1.intersect(r2)) {
            for (int i = r1.left; i<r1.right; i++) {
                for (int j = r1.top; j<r1.bottom; j++) {
                    if (bm1.getPixel(i-r3.left, j-r3.top)!= Color.TRANSPARENT) {
                        if (bm2.getPixel(i-r2.left, j-r2.top) != Color.TRANSPARENT) {
                            lastCollision = new Point(sprite2.x + i-r2.left, sprite2.y + j-r2.top);
                            return true;
                        }
                    }
                }
            }
        }
        lastCollision = new Point(-1,-1);
        return false;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        //The last order of business is to check for a collision
        collisionDetected = checkForCollision();
        if (collisionDetected ) {
            //if there is one lets draw a red X
            p.setColor(Color.RED);
            p.setAlpha(255);
            p.setStrokeWidth(5);
            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
                    lastCollision.x + 5, lastCollision.y + 5, p);
            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
                    lastCollision.x - 5, lastCollision.y + 5, p);
        }
    }
}
