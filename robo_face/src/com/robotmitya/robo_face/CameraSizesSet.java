package com.robotmitya.robo_face;

import android.hardware.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by dmitrydzz on 5/17/14.
 */
@SuppressWarnings("deprecation")
class CameraSizesSet {
    private ArrayList<CameraSizes> mCameraSizesSet = new ArrayList<>();

    int length() {
        return mCameraSizesSet.size();
    }
    CameraSizes get(int index) {
        return mCameraSizesSet.get(index);
    }
    @SuppressWarnings("WeakerAccess")
    void clear() {
        mCameraSizesSet.clear();
    }
    @SuppressWarnings("WeakerAccess")
    void add(CameraSizes cameraSizes) {
        mCameraSizesSet.add(cameraSizes);
    }

    void load() {
        mCameraSizesSet.clear();

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            CameraSizes cameraSizes = new CameraSizes();
            cameraSizes.mCameraIndex = i;
            cameraSizes.mSizes.clear();
            Camera camera = Camera.open(i);
            try {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (supportedPreviewSizes != null) {
                    for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                        Size size = new Size();
                        size.width = supportedPreviewSize.width;
                        size.height = supportedPreviewSize.height;
                        cameraSizes.add(size);
                    }
                }
            } finally {
                camera.release();
            }
            mCameraSizesSet.add(cameraSizes);
        }
    }

//    public void load() {
//        mCameraSizesSet.clear();
//        for (int i = 0; i < 3; i++) {
//            CameraSizes cameraSizes = new CameraSizes();
//            cameraSizes.mCameraIndex = i;
//            cameraSizes.mSizes.clear();
//            for (int j = 0; j < 2; j++) {
//                CameraSizesSet.Size size = new Size();
//                size.width = 100 + j;
//                size.height = 10 + j;
//                cameraSizes.add(size);
//            }
//            mCameraSizesSet.add(cameraSizes);
//        }
//    }

    String toJson() throws JSONException {
        JSONArray jsonCameraSizesSet = new JSONArray();
        for (CameraSizes cameraSizes : mCameraSizesSet) {
            JSONObject jsonCameraSizes = new JSONObject();
            jsonCameraSizes.put("CameraIndex", cameraSizes.getCameraIndex());

            JSONArray jsonSizes = new JSONArray();
            for (int i = 0; i < cameraSizes.getSizesLength(); i++) {
                Size size = cameraSizes.getSize(i);

                JSONObject jsonSize = new JSONObject();
                jsonSize.put("Width", size.width);
                jsonSize.put("Height", size.height);

                jsonSizes.put(jsonSize);
            }
            jsonCameraSizes.put("Sizes", jsonSizes);

            jsonCameraSizesSet.put(jsonCameraSizes);
        }
        return jsonCameraSizesSet.toString().replaceAll("\\\\", "");
    }

    void fromJson(String json) throws JSONException {
        clear();
        JSONArray jsonCameraSizesSet = new JSONArray(json);
        for (int i = 0; i < jsonCameraSizesSet.length(); i++) {
            JSONObject jsonCameraSizes = jsonCameraSizesSet.getJSONObject(i);

            CameraSizes cameraSizes = new CameraSizes();
            add(cameraSizes);

            cameraSizes.setCameraIndex(jsonCameraSizes.getInt("CameraIndex"));
            JSONArray jsonSizes = jsonCameraSizes.getJSONArray("Sizes");
            for (int j = 0; j < jsonSizes.length(); j++) {
                JSONObject jsonSize = jsonSizes.getJSONObject(j);

                Size size = new Size();
                cameraSizes.add(size);

                size.width = jsonSize.getInt("Width");
                size.height = jsonSize.getInt("Height");
            }
        }
    }

    public static class CameraSizes {
        private int mCameraIndex;
        private ArrayList<Size> mSizes = new ArrayList<>();

        int getCameraIndex() {
            return mCameraIndex;
        }
        void setCameraIndex(int cameraIndex) {
            mCameraIndex = cameraIndex;
        }

        int getSizesLength() {
            return mSizes.size();
        }
        Size getSize(int index) {
            return mSizes.get(index);
        }

        @SuppressWarnings("unused")
        public void clear() {
            mSizes.clear();
        }

        public void add(Size size) {
            mSizes.add(size);
        }
    }

    public static class Size {
        public int width;
        public int height;
    }
}
