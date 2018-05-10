package com.robotmitya.robo_controller;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.robotmitya.robo_common.Constants;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;

import sensor_msgs.CompressedImage;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 06.05.18.
 */

public class ControllerFragment extends Fragment {
    private SettingsFragment mSettingsFragment;
    private ControllerNode mControllerNode;
    private Orientation mOrientation;
    private boolean mSendingOrientation = false;

    private RosImageView<CompressedImage> mVideoView;
    private CheckBox mCheckBoxSendOrientation;
    private VelocityJoystick mVelocityJoystick;
    private TextView mTextOutput;

    //region Face buttons' handlers
    private boolean mFacesVisible = false;
    private View.OnClickListener mButtonClickListenerFaces = null;
    private View.OnClickListener mButtonClickListenerFaceOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "FaceOk press");
            if (mButtonClickListenerFaces != null)
                mButtonClickListenerFaces.onClick(v);
        }
    };
    private View.OnClickListener mButtonClickListenerFaceHappy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "FaceHappy press");
            if (mButtonClickListenerFaces != null)
                mButtonClickListenerFaces.onClick(v);
        }
    };
    private View.OnClickListener mButtonClickListenerFaceBlue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "FaceBlue press");
            if (mButtonClickListenerFaces != null)
                mButtonClickListenerFaces.onClick(v);
        }
    };
    private View.OnClickListener mButtonClickListenerFaceAngry = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "FaceAngry press");
            if (mButtonClickListenerFaces != null)
                mButtonClickListenerFaces.onClick(v);
        }
    };
    private View.OnClickListener mButtonClickListenerFaceIll = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "FaceIll press");
            if (mButtonClickListenerFaces != null)
                mButtonClickListenerFaces.onClick(v);
        }
    };
    //endregion

    //region LED buttons' handlers
    private boolean mLedsVisible = false;
    private View.OnClickListener mButtonClickListenerLeds = null;
    private View.OnClickListener mButtonClickListenerLed1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "LED 1 press");
            mControllerNode.switchLed1();
            if (mButtonClickListenerLeds != null)
                mButtonClickListenerLeds.onClick(v);
        }
    };
    private View.OnClickListener mButtonClickListenerLed2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "LED 2 press");
            mControllerNode.switchLed2();
            if (mButtonClickListenerLeds != null)
                mButtonClickListenerLeds.onClick(v);
        }
    };
    //endregion

    public ControllerFragment() {
    }

    public void setSettingsFragment(final SettingsFragment settingsFragment) {
        mSettingsFragment = settingsFragment;
    }

    public RosImageView<CompressedImage> getVideoView() {
        return mVideoView;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public ControllerNode getControllerNode() {
        return mControllerNode;
    }

    public interface OnStartFragmentListener {
        void OnStartFragment();
    }

    public interface OnStopFragmentListener {
        void OnStopFragment();
    }

    private OnStartFragmentListener mOnStartFragmentListener;
    private OnStopFragmentListener mOnStopFragmentListener;

    public void setOnStartFragmentListener(OnStartFragmentListener onStartFragmentListener) {
        mOnStartFragmentListener = onStartFragmentListener;
    }

    public void setOnStopFragmentListener(OnStopFragmentListener onStopFragmentListener) {
        mOnStopFragmentListener = onStopFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View result = inflater.inflate(R.layout.controller_fragment, container, false);
        if (result == null)
            return null;

        final Context context = getActivity();

        //noinspection unchecked
        mVideoView = (RosImageView<CompressedImage>) result.findViewById(R.id.imageViewVideo);
        mVideoView.setTopicName(Constants.TopicName.Camera);
        mVideoView.setMessageType(sensor_msgs.CompressedImage._TYPE);
        mVideoView.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        mTextOutput = (TextView) result.findViewById(R.id.textOutput);

        mControllerNode = new ControllerNode();

        mOrientation = new Orientation(context);
        mOrientation.setOnOrientationListener(new Orientation.OnOrientationListener() {
            public void onOrientation(long timestamp, float x, float y, float z, float w) {
                if (mSendingOrientation)
                    mControllerNode.sendOrientation(timestamp, x, y, z, w);
            }
        });

        //region Face buttons animation
        final ViewGroup facesGroup = (ViewGroup) result.findViewById(R.id.scene_buttonFace_container);
        final Scene sceneButtonFacesVisible = Scene.getSceneForLayout(facesGroup, R.layout.face_buttons_scene_visible, context);
        final Scene sceneButtonFacesInvisible = Scene.getSceneForLayout(facesGroup, R.layout.face_buttons_scene_invisible, context);
        final TransitionSet transitionSetFaces = new TransitionSet();
        transitionSetFaces.addTransition(new ChangeBounds());
        transitionSetFaces.setInterpolator(new AccelerateInterpolator());
        transitionSetFaces.setDuration(40 * 5);
        final TransitionManager transitionManagerFaces = new TransitionManager();
        transitionManagerFaces.setTransition(sceneButtonFacesVisible, sceneButtonFacesInvisible, transitionSetFaces);
        transitionManagerFaces.setTransition(sceneButtonFacesInvisible, sceneButtonFacesVisible, transitionSetFaces);
        transitionManagerFaces.transitionTo(sceneButtonFacesInvisible);

        final Button buttonFaces = (Button) result.findViewById(R.id.buttonFaces);
        mButtonClickListenerFaces = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFacesVisible) {
                    transitionManagerFaces.transitionTo(sceneButtonFacesInvisible);
                } else {
                    transitionManagerFaces.transitionTo(sceneButtonFacesVisible);

                    final ImageButton buttonFaceOk = (ImageButton) result.findViewById(R.id.buttonFaceOk);
                    buttonFaceOk.setOnClickListener(mButtonClickListenerFaceOk);

                    final ImageButton buttonFaceHappy = (ImageButton) result.findViewById(R.id.buttonFaceHappy);
                    buttonFaceHappy.setOnClickListener(mButtonClickListenerFaceHappy);

                    final ImageButton buttonFaceBlue = (ImageButton) result.findViewById(R.id.buttonFaceBlue);
                    buttonFaceBlue.setOnClickListener(mButtonClickListenerFaceBlue);

                    final ImageButton buttonFaceAngry = (ImageButton) result.findViewById(R.id.buttonFaceAngry);
                    buttonFaceAngry.setOnClickListener(mButtonClickListenerFaceAngry);

                    final ImageButton buttonFaceIll = (ImageButton) result.findViewById(R.id.buttonFaceIll);
                    buttonFaceIll.setOnClickListener(mButtonClickListenerFaceIll);
                }
                mFacesVisible = !mFacesVisible;
            }
        };
        buttonFaces.setOnClickListener(mButtonClickListenerFaces);
        //endregion

        //region LED buttons animation
        final ViewGroup ledsGroup = (ViewGroup) result.findViewById(R.id.scene_buttonLeds_container);
        final Scene sceneButtonLedsVisible = Scene.getSceneForLayout(ledsGroup, R.layout.led_buttons_scene_visible, context);
        final Scene sceneButtonLedsInvisible = Scene.getSceneForLayout(ledsGroup, R.layout.led_buttons_scene_invisible, context);
        final TransitionSet transitionSetLeds = new TransitionSet();
        transitionSetLeds.addTransition(new ChangeBounds());
        transitionSetLeds.setInterpolator(new AccelerateInterpolator());
        transitionSetLeds.setDuration(40 * 2);
        final TransitionManager transitionManagerLeds = new TransitionManager();
        transitionManagerLeds.setTransition(sceneButtonLedsVisible, sceneButtonLedsInvisible, transitionSetLeds);
        transitionManagerLeds.setTransition(sceneButtonLedsInvisible, sceneButtonLedsVisible, transitionSetLeds);
        transitionManagerLeds.transitionTo(sceneButtonLedsInvisible);

        final Button buttonLeds = (Button) result.findViewById(R.id.buttonLeds);
        mButtonClickListenerLeds = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLedsVisible) {
                    transitionManagerLeds.transitionTo(sceneButtonLedsInvisible);
                } else {
                    transitionManagerLeds.transitionTo(sceneButtonLedsVisible);

                    final ImageButton buttonLed1 = (ImageButton) result.findViewById(R.id.buttonLed1);
                    buttonLed1.setOnClickListener(mButtonClickListenerLed1);

                    final ImageButton buttonLed2 = (ImageButton) result.findViewById(R.id.buttonLed2);
                    buttonLed2.setOnClickListener(mButtonClickListenerLed2);
                }
                mLedsVisible = !mLedsVisible;
            }
        };
        buttonLeds.setOnClickListener(mButtonClickListenerLeds);
        //endregion

        mCheckBoxSendOrientation = (CheckBox) result.findViewById(R.id.checkboxSendOrientation);
        mCheckBoxSendOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mControllerNode.setPointingMode(isChecked);
                mSendingOrientation = isChecked;
                mVelocityJoystick.setEnabled(isChecked);
                if (isChecked) {
                    mOrientation.center();
                    mControllerNode.centerHead();
                }
            }
        });

        mVelocityJoystick = (VelocityJoystick) result.findViewById(R.id.velocityJoystick);
        mVelocityJoystick.setEnabled(false);
        mVelocityJoystick.setOnChangeVelocityListener(new VelocityJoystick.OnChangeVelocityListener() {
            @Override
            public void onChangeVelocity(byte velocity) {
                Log.d(TAG, "Velocity = " + velocity);
                mControllerNode.sendDriveTowards(velocity);
            }
        });

        ImageButton imageButton = (ImageButton) result.findViewById(R.id.settingsButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSettingsFragment != null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mSettingsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(20);
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String text1 = mCheckBoxSendOrientation.isChecked() ?
                                        mOrientation.getFrameSensorText() : "";
                                mTextOutput.setText(text1);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }.start();

        setFullscreen();

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mOnStartFragmentListener != null)
            mOnStartFragmentListener.OnStartFragment();
    }

    @Override
    public void onStop() {
        if (mOnStopFragmentListener != null)
            mOnStopFragmentListener.OnStopFragment();
        super.onStop();
    }

    public void setFullscreen() {
        if (mVideoView != null)
            mVideoView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
