package com.Revealit.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewDraw extends View {

    private Rect rectangle;
    private Paint paint;
    private float x;
    private float y;

    public CustomViewDraw(Context context, float x, float v) {
        super(context);
        this.x = x;
        this.y = y;
        int sideLength = 200;

        // create a rectangle that we'll draw later
        rectangle = new Rect(Math.round(x),Math.round(y), sideLength, sideLength);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(350, 250,20, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //Check if the x and y position of the touch is inside the bitmap
                Log.e("TOUCHED", "X: " + x + " Y: " + y);
                return true;
        }
        return false;
    }
}