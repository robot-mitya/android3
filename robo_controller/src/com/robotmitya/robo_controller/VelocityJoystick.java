package com.robotmitya.robo_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

import static com.robotmitya.robo_controller.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 08.04.18.
 */

public class VelocityJoystick extends View {
    private int mWidth = 0;
    private int mHeight = 0;

    private boolean mIsTouched = false;
    private int mOnTouchDownY = 0;
    private int mOnMoveCurrentY = 0;

    private Paint mPaintZeroLine;
    private Paint mPaintCurrentLine;

    public VelocityJoystick(Context context) {
        super(context);
        init();
    }

    public VelocityJoystick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VelocityJoystick(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public VelocityJoystick(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaintZeroLine = new Paint();
        mPaintZeroLine.setColor(getResources().getColor(R.color.velocity_joystick_zero_line));
        mPaintZeroLine.setStrokeWidth(10); //todo Get value from resources.

        mPaintCurrentLine = new Paint(mPaintZeroLine);
        mPaintCurrentLine.setColor(getResources().getColor(R.color.velocity_joystick_current_line));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.d(TAG, String.format(Locale.ENGLISH, "mWidth=%d mHeight=%d", mWidth, mHeight));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        String text = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouched = true;
                mOnTouchDownY = y;
                mOnMoveCurrentY = y;
                invalidate();
                text += "Down";
                break;
            case MotionEvent.ACTION_MOVE:
                if (y < 0)
                    mOnMoveCurrentY = 0;
                else if (y >= mHeight)
                    mOnMoveCurrentY = mHeight - 1;
                else
                    mOnMoveCurrentY = y;
                invalidate();
                text += "Move";
                break;
            case MotionEvent.ACTION_UP:
                mIsTouched = false;
                invalidate();
                text += "Up";
                break;
        }
        text += String.format(Locale.ENGLISH, " (%d, %d)", x, y);
//        Log.d(TAG, text);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsTouched) {
            canvas.drawLine(10, mOnTouchDownY, mWidth-10, mOnTouchDownY, mPaintZeroLine);
            canvas.drawLine(10, mOnMoveCurrentY, mWidth-10, mOnMoveCurrentY, mPaintCurrentLine);
        }
    }
}
