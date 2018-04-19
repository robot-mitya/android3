package com.robotmitya.robo_face;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaceFragment extends Fragment {

    private ImageView mFaceImage;
    private FaceHelper mFaceHelper;
    private FaceNode mFaceNode;

    public FaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.face_fragment, container, false);
        if (result == null)
            return null;

        mFaceImage = (ImageView) result.findViewById(R.id.faceImage);
        mFaceHelper = new FaceHelper(this.getActivity(), mFaceImage);
        mFaceNode = new FaceNode(mFaceHelper);

        ImageButton imageButton = (ImageButton) result.findViewById(R.id.settingsButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ...
                Log.d(TAG, "Settings dialog");
            }
        });

        return result;
    }

    public final FaceNode getFaceNode() {
        return mFaceNode;
    }

    public void setFaceFullscreen() {
        if (mFaceImage == null)
            return;
        mFaceImage.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
