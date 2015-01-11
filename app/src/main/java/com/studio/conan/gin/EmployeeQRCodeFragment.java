package com.studio.conan.gin;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.studio.conan.gin.EmployeeQRCodeFragment.OnEmployeeQRCodeCreatedListener} interface
 * to handle interaction events.
 * Use the {@link EmployeeQRCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EmployeeQRCodeFragment extends Fragment {
    //private static final String LOG_TAG = "EmployeeQRCodeFragment";
    private static final String ARG_PSID = "psid";
    private static final String ARG_SN = "sn";

    private Activity mActivity;

    private String mPSID;
    private String mSN;

    private OnEmployeeQRCodeCreatedListener mListener;

    // Widget
    private View employeeQRView;
    private ImageView employeeQR;
    private View employeeInputView;
    private EditText employeePSID;
    private EditText employeeSN;
    private Button employeeButton;


    public static EmployeeQRCodeFragment newInstance(String psid, String sn) {
        EmployeeQRCodeFragment fragment = new EmployeeQRCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PSID, psid);
        args.putString(ARG_SN, sn);
        fragment.setArguments(args);
        return fragment;
    }
    public EmployeeQRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPSID = getArguments().getString(ARG_PSID);
            mSN = getArguments().getString(ARG_SN);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_qrcode, container, false);

        employeeQRView = view.findViewById(R.id.employee_qr_view);
        employeeQR = (ImageView) view.findViewById(R.id.employee_qr);
        employeeInputView = view.findViewById(R.id.employee_input_view);
        employeePSID = (EditText) view.findViewById(R.id.employee_psid);
        employeeSN = (EditText) view.findViewById(R.id.employee_sn);
        employeeButton = (Button) view.findViewById(R.id.generate_qr_code);
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset errors.
                employeePSID.setError(null);
                employeeSN.setError(null);

                boolean cancel = false;
                View focusView = null;

                mPSID = employeePSID.getText().toString().trim();
                mSN = employeeSN.getText().toString().trim();

                if (TextUtils.isEmpty(mSN)) {
                    employeeSN.setError(getString(R.string.error_empty_psk));
                    focusView = employeeSN;
                    cancel = true;
                }

                if (TextUtils.isEmpty(mPSID)) {
                    employeePSID.setError(getString(R.string.error_empty_psk));
                    focusView = employeePSID;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    String pathName = mActivity.getFilesDir().getAbsolutePath() + "/" + mPSID + mSN + ".png";
                    Bitmap bitmap = generateQRCode(mPSID, mSN, 256, 256, pathName);
                    if (bitmap != null) {
                        employeeInputView.setVisibility(View.GONE);
                        employeeQRView.setVisibility(View.VISIBLE);
                        employeeQR.setImageBitmap(bitmap);
                    }

                    // callback PS ID and Serial Number to store into preference
                    if (mListener != null) {
                        mListener.onEmployeeQRCodeCreated(mPSID, mSN);
                    }
                }
            }
        });

        if (TextUtils.isEmpty(mPSID) || TextUtils.isEmpty(mSN)) {
            employeeQRView.setVisibility(View.GONE);
            employeeInputView.setVisibility(View.VISIBLE);
        } else {
            employeeQRView.setVisibility(View.VISIBLE);
            employeeInputView.setVisibility(View.GONE);
            displayQRCode();
        }

        return view;
    }

    private void displayQRCode() {
        Log.e("conan debug", "displayQRCode: PSID=" + mPSID + " SN=" + mSN);

        String pathName = mActivity.getFilesDir().getAbsolutePath() + "/" + mPSID + mSN + ".png";
        //Log.e("conan debug", "get QRCode path: " + pathName + " canWrite: " + mActivity.getFilesDir().canWrite());

        // Load from internal storage first.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        if (bitmap == null) {
            Log.e("conan debug", "QR Code Not Found");
            // QRCode image is not found, generating a new one.
            bitmap = generateQRCode(mPSID, mSN, 256, 256, pathName);
            if (bitmap != null) {
                Log.e("conan debug", "Generate QR Failed");
                //employeeQR.setImageBitmap(bitmap);
            }
        }

        employeeQR.setImageBitmap(bitmap);
    }

    private Bitmap generateQRCode(String psid, String sn, int width, int height, String saveTo) {

        OutputStream os = null;
        Bitmap bitmap = null;
        String content = psid + "&" + sn;
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    //Resources resources = mActivity.getResources();
                    //int yellow = resources.getColor(R.color.yellow);
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            // Save personal QR code image
            os = new FileOutputStream(saveTo);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
        try {
            mListener = (OnEmployeeQRCodeCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEmployeeQRCodeCreatedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.employee_qr_code, menu);

//        ActionBar actionBar = getActivity().getActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(getString(R.string.title_activity_employee_main));
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_reset:
                employeeQRView.setVisibility(View.GONE);
                employeeInputView.setVisibility(View.VISIBLE);

                Gin app = Gin.getInstance();
                app.cleanPreferences();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public interface OnEmployeeQRCodeCreatedListener {
        public void onEmployeeQRCodeCreated(String psid, String sn);
    }

}
