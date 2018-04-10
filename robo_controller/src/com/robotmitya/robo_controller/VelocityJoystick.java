package com.robotmitya.robo_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * Created by dmitrydzz on 08.04.18.
 */

public class VelocityJoystick extends View {
    private int mWidth = 0;
    private int mHeight = 0;
    private int mZeroY = 0;

    private boolean mIsTouched = false;
    private int mCurrentY = 0;

    private Paint mPaintZeroEnabledLine;
    private Paint mPaintZeroDisabledLine;
    private Paint mPaintCurrentLine;

    private byte mPreviousVelocity = 0;

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
        mPaintZeroEnabledLine = new Paint();
        mPaintZeroEnabledLine.setColor(
                getResources().getColor(R.color.velocity_joystick_zero_enabled_line));
        mPaintZeroEnabledLine.setStrokeWidth(
                getResources().getDimension(R.dimen.velocity_joystick_line_width));

        mPaintZeroDisabledLine = new Paint(mPaintZeroEnabledLine);
        mPaintZeroDisabledLine.setColor(getResources().getColor(R.color.velocity_joystick_zero_disabled_line));

        mPaintCurrentLine = new Paint(mPaintZeroEnabledLine);
        mPaintCurrentLine.setColor(getResources().getColor(R.color.velocity_joystick_current_line));
    }

    @Override
    protected void onSizeChanged(int w, int h, int previousW, int previousH) {
        super.onSizeChanged(w, h, previousW, previousH);
        mWidth = w;
        mHeight = h;
        mZeroY = h / 2;
//        Log.d(TAG, String.format(Locale.ENGLISH,
//                "mWidth=%d mHeight=%d mZeroY=%d", mWidth, mHeight, mZeroY));
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
                mCurrentY = y;
                processVelocity((byte) 0);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (y < 0)
                    mCurrentY = 0;
                else if (y >= mHeight)
                    mCurrentY = mHeight - 1;
                else
                    mCurrentY = y;
                processVelocity((byte) getVelocity(mHeight, mZeroY, mCurrentY));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsTouched = false;
                mCurrentY = 0;
                processVelocity((byte) 0);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean enabled = isEnabled();
        canvas.drawLine(10, mZeroY, mWidth-10, mZeroY,
                enabled ? mPaintZeroEnabledLine : mPaintZeroDisabledLine);
        if (enabled && mIsTouched)
            canvas.drawLine(10, mCurrentY, mWidth-10, mCurrentY, mPaintCurrentLine);
    }

    private static int getVelocity(int height, int zeroY, int currentY) {
        //Log.d(TAG, String.format("height=%d, zeroY=%d, currentY=%d", height, zeroY, currentY));
        if (currentY < zeroY) {
            if (zeroY == 0)
                return 0;
            return 100 * (zeroY - currentY) / zeroY;
        } else {
            if (height - zeroY - 1 == 0)
                return 0;
            return 100 * (zeroY - currentY) / (height - zeroY - 1);
        }
    }

    private void processVelocity(byte velocity) {
        if (velocity == mPreviousVelocity) return;
        if (mOnChangeVelocityListener != null)
            mOnChangeVelocityListener.onChangeVelocity(velocity);
        mPreviousVelocity = velocity;
    }

    public interface OnChangeVelocityListener {
        void onChangeVelocity(byte velocity);
    }
}
