package com.robotmitya.robo_face;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.hash.HashCode;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * Выражение лица робота.
 * @author Дмитрий Дзахов
 *
 */
enum FaceType { ftUnknown, ftOk, ftHappy, ftBlue, ftAngry, ftIll };

/**
 * Класс для управления лицом робота.
 * @author Дмитрий Дзахов
 *
 */
final class FaceHelper {
	private ImageView mImageView;

	private AnimationDrawable mAnimation = null;
	
	private FaceType mCurrentFace = FaceType.ftOk;

	private HashMap<StateKey, Integer> mStateMatrix = new HashMap<>();

	private void startRandomIdleAnimation() {
	    int resource = 0;
		switch (mCurrentFace) {
			case ftOk:
				final int choice = ThreadLocalRandom.current().nextInt(5);
				switch (choice) {
					case 0: // вероятность 20%
						resource = R.drawable.face_anim_idle_2;
						break;
					case 1: // вероятность 20%
						resource = R.drawable.face_anim_idle_3;
						break;
					default: // вероятность 60%
						resource = R.drawable.face_anim_idle_1;
						break;
				}
				break;
			default:
				return;
		}
		startAnimation(resource);
	}

	/**
	 * Конструктор класса.
	 * @param imageView контрол для вывода анимации.
	 */
	FaceHelper(final Activity activity, final ImageView imageView) {
		mImageView = imageView;

		fillAnimationStateMatrix();

		// Thread для генерации Idle-событий. События генерируются с переменной периодичностью.
		// Промежутки составляют 4 сек + random(4 сек).
        new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    final int constDelay = 3000;
                    final int variableMaxDelay = 3000;
                    final int variableDelay = (int) (variableMaxDelay * Math.random());
                    try {
                        Thread.sleep(constDelay + variableDelay);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startRandomIdleAnimation();
                        }
                    });
                }
            }
        }.start();
	}

	private void fillAnimationStateMatrix() {
        mStateMatrix.put(new StateKey(FaceType.ftAngry, FaceType.ftAngry), R.drawable.face_anim_angry_to_angry);
        mStateMatrix.put(new StateKey(FaceType.ftAngry, FaceType.ftBlue), R.drawable.face_anim_angry_to_blue);
        mStateMatrix.put(new StateKey(FaceType.ftAngry, FaceType.ftHappy), R.drawable.face_anim_angry_to_happy);
        mStateMatrix.put(new StateKey(FaceType.ftAngry, FaceType.ftIll), R.drawable.face_anim_angry_to_ill);
        mStateMatrix.put(new StateKey(FaceType.ftAngry, FaceType.ftOk), R.drawable.face_anim_angry_to_ok);

        mStateMatrix.put(new StateKey(FaceType.ftBlue, FaceType.ftAngry), R.drawable.face_anim_blue_to_angry);
        mStateMatrix.put(new StateKey(FaceType.ftBlue, FaceType.ftBlue), R.drawable.face_anim_blue_to_blue);
        mStateMatrix.put(new StateKey(FaceType.ftBlue, FaceType.ftHappy), R.drawable.face_anim_blue_to_happy);
        mStateMatrix.put(new StateKey(FaceType.ftBlue, FaceType.ftIll), R.drawable.face_anim_blue_to_ill);
        mStateMatrix.put(new StateKey(FaceType.ftBlue, FaceType.ftOk), R.drawable.face_anim_blue_to_ok);

        mStateMatrix.put(new StateKey(FaceType.ftHappy, FaceType.ftAngry), R.drawable.face_anim_happy_to_angry);
        mStateMatrix.put(new StateKey(FaceType.ftHappy, FaceType.ftBlue), R.drawable.face_anim_happy_to_blue);
        mStateMatrix.put(new StateKey(FaceType.ftHappy, FaceType.ftHappy), R.drawable.face_anim_happy_to_happy);
        mStateMatrix.put(new StateKey(FaceType.ftHappy, FaceType.ftIll), R.drawable.face_anim_happy_to_ill);
        mStateMatrix.put(new StateKey(FaceType.ftHappy, FaceType.ftOk), R.drawable.face_anim_happy_to_ok);

        mStateMatrix.put(new StateKey(FaceType.ftIll, FaceType.ftAngry), R.drawable.face_anim_ill_to_angry);
        mStateMatrix.put(new StateKey(FaceType.ftIll, FaceType.ftBlue), R.drawable.face_anim_ill_to_blue);
        mStateMatrix.put(new StateKey(FaceType.ftIll, FaceType.ftHappy), R.drawable.face_anim_ill_to_happy);
        mStateMatrix.put(new StateKey(FaceType.ftIll, FaceType.ftIll), R.drawable.face_anim_ill_to_ill);
        mStateMatrix.put(new StateKey(FaceType.ftIll, FaceType.ftOk), R.drawable.face_anim_ill_to_ok);

        mStateMatrix.put(new StateKey(FaceType.ftOk, FaceType.ftAngry), R.drawable.face_anim_ok_to_angry);
        mStateMatrix.put(new StateKey(FaceType.ftOk, FaceType.ftBlue), R.drawable.face_anim_ok_to_blue);
        mStateMatrix.put(new StateKey(FaceType.ftOk, FaceType.ftHappy), R.drawable.face_anim_ok_to_happy);
        mStateMatrix.put(new StateKey(FaceType.ftOk, FaceType.ftIll), R.drawable.face_anim_ok_to_ill);
        mStateMatrix.put(new StateKey(FaceType.ftOk, FaceType.ftOk), R.drawable.face_anim_ok_to_ok);
	}

	/**
	 * Получение текущего состояния лица.
	 * @return тип установленного выражения лица.
	 */
	@SuppressWarnings("unused")
    FaceType getFace() {
		return mCurrentFace;
	}
	
	/**
	 * Установка выражения лица.
	 * @param face новое выражение лица.
	 */
	void setFace(final FaceType face) {
		final StateKey transformation = new StateKey(mCurrentFace, face);
		Integer resource = mStateMatrix.get(transformation);
		if (resource == null) return;
		mCurrentFace = face;
		startAnimation(resource);
	}
	
	private void startAnimation(int resource) {
        if (mAnimation != null) {
            mAnimation.stop();
            mAnimation = null;
            System.gc();
        }
		mImageView.setBackgroundResource(resource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
	}

	private class StateKey {
		FaceType FaceFrom;
		FaceType FaceTo;

        StateKey(FaceType faceFrom, FaceType faceTo) {
            FaceFrom = faceFrom;
            FaceTo = faceTo;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof StateKey)) return false;
            StateKey otherStateKey = (StateKey) other;
            return FaceFrom == otherStateKey.FaceFrom && FaceTo == otherStateKey.FaceTo;
        }

        @Override
        public int hashCode() {
            return (FaceFrom.ordinal() << 16) + FaceTo.ordinal();
        }
    }
}
