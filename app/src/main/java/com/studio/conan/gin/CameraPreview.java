package com.studio.conan.gin;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;
    private Camera.AutoFocusCallback mAutoFocusCallback;

    public CameraPreview(Context context, Camera camera, Camera.PreviewCallback previewCallback, Camera.AutoFocusCallback autoFocusCallback) {
        super(context);
        mCamera = camera;
        mPreviewCallback = previewCallback;
        mAutoFocusCallback = autoFocusCallback;

        mHolder = getHolder();
        mHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }

        mCamera.stopPreview();

        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(mPreviewCallback);
        mCamera.startPreview();
        mCamera.autoFocus(mAutoFocusCallback);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
