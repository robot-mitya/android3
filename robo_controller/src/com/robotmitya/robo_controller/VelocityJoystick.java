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

    private OnChangeVelocityListener mOnChangeVelocityListener;

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

    public void setOnChangeVelocityListener(OnChangeVelocityListener onChangeVelocityListener) {
        mOnChangeVelocityListener = onChangeVelocityListener;
    }

    private void init() {
        mPaintZeroLine = new Paint();
        mPaintZeroLine.setColor(
                getResources().getColor(R.color.velocity_joystick_zero_line));
        mPaintZeroLine.setStrokeWidth(
                getResources().getDimension(R.dimen.velocity_joystick_line_width));

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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int colorId = enabled ?
                R.color.velocity_joystick_enabled_background :
                R.color.velocity_joystick_disabled_background;
        setBackgroundColor(getResources().getColor(colorId));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return false;

        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouched = true;
                mOnTouchDownY = y;
                mOnMoveCurrentY = y;
                if (mOnChangeVelocityListener != null)
                    mOnChangeVelocityListener.onChangeVelocity(0);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (y < 0)
                    mOnMoveCurrentY = 0;
                else if (y >= mHeight)
                    mOnMoveCurrentY = mHeight - 1;
                else
                    mOnMoveCurrentY = y;
                if (mOnChangeVelocityListener != null)
                    mOnChangeVelocityListener.onChangeVelocity(
                            getVelocity(mHeight, mOnTouchDownY, mOnMoveCurrentY));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsTouched = false;
                if (mOnChangeVelocityListener != null)
                    mOnChangeVelocityListener.onChangeVelocity(0);
                invalidate();
                break;
        }
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

    private static int getVelocity(int height, int zeroY, int currentY) {
        //Log.d(TAG, String.format("height=%d, zeroY=%d, currentY=%d", height, zeroY, currentY));
        if (currentY < 0)
            return 0;
        if (currentY >= height)
            return 100;
        if (zeroY > height / 2) {
            if (zeroY == 0)
                return -100 * currentY / height;
            return 100 * (zeroY - currentY) / zeroY;
        } else {
            if (height - zeroY == 1)
                return 100 * (zeroY - currentY) / (height - 1);
            return 100 * (zeroY - currentY) / (height - zeroY - 1);
        }
    }

    public interface OnChangeVelocityListener {
        void onChangeVelocity(int velocity);
    }
}
