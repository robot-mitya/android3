package com.robotmitya.robo_controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 13.05.18.
 */

public class JoystickView extends RelativeLayout {
    private static final float EPSILON = 0.1f;
    private static final long PRESSED_STATE_DURATION = 500;
    private static final float CENTER_BUTTON_BORDER = 0.25f;

    private int mPointerId;

    private float mX;
    private float mY;
    private float mPreviousSentX;
    private float mPreviousSentY;

    private boolean mHandleDiagonals;
    private boolean mIsAnalog = true;

    private boolean mIsCenterButtonDown;
    private long mCenterButtonDownMillis;

    private ImageView mImageViewLeft;
    private ImageView mImageViewRight;
    private ImageView mImageViewUp;
    private ImageView mImageViewDown;
    private ImageView mImageViewCenter;

    private OnPositionedListener mOnPositionedListener;
    private OnCenterButtonListener mOnCenterButtonListener;

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public float getJoystickX() {
        return mX;
    }

    @SuppressWarnings("unused")
    public float getJoystickY() {
        return mY;
    }

    @SuppressWarnings("unused")
    public boolean getHandleDiagonals() {
        return mHandleDiagonals;
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    public void setHandleDiagonals(boolean value) {
        mHandleDiagonals = value;
    }

    @SuppressWarnings("unused")
    public boolean getIsAnalog() {
        return mIsAnalog;
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    public void setIsAnalog(boolean value) {
        mIsAnalog = value;
    }

    public void setOnPositionedListener(OnPositionedListener listener) {
        mOnPositionedListener = listener;
    }

    public void setOnCenterButtonListener(OnCenterButtonListener listener) {
        mOnCenterButtonListener = listener;
    }

    private void init() {
        inflate(getContext(), R.layout.joystick, this);

        mImageViewLeft = (ImageView) findViewById(R.id.head_left);
        mImageViewRight = (ImageView) findViewById(R.id.head_right);
        mImageViewUp = (ImageView) findViewById(R.id.head_up);
        mImageViewDown = (ImageView) findViewById(R.id.head_down);
        mImageViewCenter = (ImageView) findViewById(R.id.head_center);
    }

    private void fillJoystickPosition(MotionEvent event) {
        final float x = event.getX(event.findPointerIndex(mPointerId));
        final float y = event.getY(event.findPointerIndex(mPointerId));
        final float w = getWidth();
        final float h = getHeight();
        mX = 2f * x / w - 1f;
        if (mX < -1f) mX = -1f;
        else if (mX > 1f) mX = 1f;
        mY = 2f * y / h - 1f;
        if (mY < -1f) mY = -1f;
        else if (mY > 1f) mY = 1f;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int actionIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(actionIndex);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = pointerId;
                fillJoystickPosition(event);
                handleCenterButtonDown();
                handleNewJoystickPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (pointerId == mPointerId) {
                    fillJoystickPosition(event);
                    handleCenterButtonUp();
                    mX = 0f;
                    mY = 0f;
                    handleNewJoystickPosition();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerId == mPointerId) {
                    fillJoystickPosition(event);
                    handleCenterButtonIdle();
                    handleNewJoystickPosition();
                }
                break;
        }

        return true;
    }

    private void handleNewJoystickPosition() {
        float x = mX;
        float y = mY;
        if (!mHandleDiagonals) {
            if (inDiagonalDirections()) {
                x = 0;
                y = 0;
            } else {
                if (Math.abs(x) < Math.abs(y)) x = 0;
                else y = 0;
            }
        }

        if (!mIsAnalog) {
            if (x < -CENTER_BUTTON_BORDER) x = -1;
            else if (x > CENTER_BUTTON_BORDER) x = 1;
            else x = 0;
            if (y < -CENTER_BUTTON_BORDER) y = -1;
            else if (y > CENTER_BUTTON_BORDER) y = 1;
            else y = 0;
        }

        mImageViewLeft.setPressed(x < -EPSILON);
        mImageViewRight.setPressed(x > EPSILON);
        mImageViewUp.setPressed(y < -EPSILON);
        mImageViewDown.setPressed(y > EPSILON);

        if (x != mPreviousSentX || y != mPreviousSentY) {
            if (mOnPositionedListener != null)
                mOnPositionedListener.OnPositioned(x, y);

            mPreviousSentX = x;
            mPreviousSentY = y;
        }
    }

    private void handleCenterButtonDown() {
        mIsCenterButtonDown = inCenterButtonBounds();
        if (mIsCenterButtonDown) {
            mCenterButtonDownMillis = System.currentTimeMillis();
            mImageViewCenter.setPressed(true);
        }
    }

    private void handleCenterButtonUp() {
        mImageViewCenter.setPressed(false);
        if (!mIsCenterButtonDown) return;
        mIsCenterButtonDown = false;

        final long deltaTime = System.currentTimeMillis() - mCenterButtonDownMillis;
        if (inCenterButtonBounds() && deltaTime <= PRESSED_STATE_DURATION) {
            if (mOnCenterButtonListener != null)
                mOnCenterButtonListener.OnClick();
        }
    }

    private void handleCenterButtonIdle() {
        if (!inCenterButtonBounds()) {
            mIsCenterButtonDown = false;
            mImageViewCenter.setPressed(false);
        }
    }

    private boolean inCenterButtonBounds() {
        return Math.abs(mX) <= CENTER_BUTTON_BORDER && Math.abs(mY) <= CENTER_BUTTON_BORDER;
    }

    private boolean inDiagonalDirections() {
        return Math.abs(mX) > CENTER_BUTTON_BORDER && Math.abs(mY) > CENTER_BUTTON_BORDER;
    }

    public interface OnPositionedListener {
        void OnPositioned(float x, float y);
    }

    public interface OnCenterButtonListener {
        void OnClick();
    }
}
