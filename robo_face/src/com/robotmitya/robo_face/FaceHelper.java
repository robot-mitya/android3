package com.robotmitya.robo_face;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.ThreadLocalRandom;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * Выражение лица робота.
 * @author Дмитрий Дзахов
 *
 */
enum FaceType { ftUnknown, ftOk, ftHappy, ftBlue, ftAngry, ftIll, ftReadyToPlay };

/**
 * Класс для управления лицом робота.
 * @author Дмитрий Дзахов
 *
 */
final class FaceHelper {
	private ImageView mImageView;

	private AnimationDrawable mAnimation = null;
	
	private FaceType mCurrentFace = FaceType.ftOk;

	private int mResource = 0;

	private void startRandomIdleAnimation() {
		switch (mCurrentFace) {
			case ftOk:
				final int choice = ThreadLocalRandom.current().nextInt(5);
				switch (choice) {
					case 0: // вероятность 20%
						mResource = R.drawable.face_anim_idle_2;
						break;
					case 1: // вероятность 20%
						mResource = R.drawable.face_anim_idle_3;
						break;
					default: // вероятность 60%
						mResource = R.drawable.face_anim_idle_1;
						break;
				}
				break;
			default:
				return;
		}
		startAnimation();
	}

	/**
	 * Конструктор класса.
	 * @param imageView контрол для вывода анимации.
	 */
	FaceHelper(final Activity activity, final ImageView imageView) {
		mImageView = imageView;
		
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
	
//	/**
//	 * Получение текущего состояния лица.
//	 * @return тип установленного выражения лица.
//	 */
//	public FaceType getFace() {
//		return mCurrentFace;
//	}
	
//	/**
//	 * Установка выражения лица.
//	 * @param face новое выражение лица.
//	 * @return true если началась смена выражения лица, false если смена лица уже происходит.
//	 */
//	public boolean setFace(final FaceType face) {
//		int resource = 0;
//
//		switch (mCurrentFace) {
//		case ftAngry:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_angry_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_angry_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_angry_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_angry_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_angry_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_angry_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		case ftBlue:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_blue_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_blue_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_blue_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_blue_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_blue_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_blue_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		case ftHappy:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_happy_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_happy_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_happy_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_happy_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_happy_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_happy_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		case ftIll:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_ill_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_ill_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_ill_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_ill_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_ill_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_ill_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		case ftReadyToPlay:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_ready_to_play_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_ready_to_play_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_ready_to_play_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_ready_to_play_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_ready_to_play_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_ready_to_play_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		case ftOk:
//			if (face == FaceType.ftAngry) {
//				resource = R.drawable.face_ok_to_angry;
//			} else if (face == FaceType.ftBlue) {
//				resource = R.drawable.face_ok_to_blue;
//			} else if (face == FaceType.ftHappy) {
//				resource = R.drawable.face_ok_to_happy;
//			} else if (face == FaceType.ftIll) {
//				resource = R.drawable.face_ok_to_ill;
//			} else if (face == FaceType.ftOk) {
//				resource = R.drawable.face_ok_to_ok;
//			} else if (face == FaceType.ftReadyToPlay) {
//				resource = R.drawable.face_ok_to_ready_to_play;
//			} else {
//				return false;
//			}
//			break;
//		default:
//			return false;
//		}
//
//		mCurrentFace = face;
//		startAnimation(resource);
//
////		if (face == FaceType.ftReadyToPlay) {
////			final int readyToPlayPause = 5000;
////			mHandlerDelayedAction.sendEmptyMessageDelayed(0, readyToPlayPause);
////		}
//
//        return true;
//	}
	
	private void startAnimation() {
        if (mAnimation != null) {
            mAnimation.stop();
            mAnimation = null;
            System.gc();
        }
		mImageView.setBackgroundResource(mResource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
	}
}
