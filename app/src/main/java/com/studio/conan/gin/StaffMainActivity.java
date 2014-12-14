package com.studio.conan.gin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class StaffMainActivity extends Activity implements TextView.OnEditorActionListener {

    private static final int REQUEST_CODE_CHECK_IN = 0;

    private CheckInTrenderTask mAuthTask = null;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    ImageScanner scanner;

    private boolean previewing = true;

    private View mProgressView;
    private View mCheckInForm;
    private EditText mInputPSIDEdit;


    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);

        mProgressView = findViewById(R.id.check_in_progress);
        mCheckInForm = findViewById(R.id.check_in_form);
        mInputPSIDEdit = (EditText) findViewById(R.id.input_psid);
        mInputPSIDEdit.setOnEditorActionListener(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mInputPSIDEdit.getText().clear();

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCB, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                Gin application = Gin.getInstance();
                if (application.cleanPreferences()) {
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CHECK_IN:
                /*
                mCamera.setPreviewCallback(previewCB);
                mCamera.startPreview();
                previewing = true;
                mCamera.autoFocus(autoFocusCB);
                */

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public static Camera getCameraInstance() {
        Camera c;
        c = Camera.open();
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        @Override
        public void run() {
            if (previewing) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    Camera.PreviewCallback previewCB = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String psid = sym.getData();
                    validatePSID(psid);
                }
            }
        }
    };

    public void checkIn(View view) {
        String psid = mInputPSIDEdit.getText().toString();
        validatePSID(psid);
    }

    public void validatePSID(String psid) {
        if (mAuthTask == null) {
            showProgress(true);
            mAuthTask = new CheckInTrenderTask(psid);
            mAuthTask.execute((Void) null);
        }
    }

    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCheckInForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mCheckInForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCheckInForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCheckInForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEND:
                checkIn(null);
                return true;
        }

        return false;
    }

    private class CheckInTrenderTask extends AsyncTask<Void, Void, String> {

        private final String psid;

        public CheckInTrenderTask(String psid) {
            this.psid = psid;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return null;
            }

            // TODO:

            return null;
        }

        @Override
        protected void onPostExecute(final String data) {
            mAuthTask = null;
            showProgress(false);

            Bundle bundle = new Bundle();
            bundle.putString("data", data);
            Intent intent = new Intent(getApplicationContext(), CheckInResultActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_CHECK_IN);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
