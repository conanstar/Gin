package com.studio.conan.gin;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class EmployeeMainActivity extends Activity implements EmployeeQRCodeFragment.OnEmployeeQRCodeCreatedListener {

    private String mPSID;
    private String mSN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main);

        Gin app = Gin.getInstance();
        mPSID = app.getPSID();
        mSN = app.getSN();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, EmployeeQRCodeFragment.newInstance(mPSID, mSN))
                    .commit();
        }
    }

    @Override
    public void onEmployeeQRCodeCreated(String psid, String sn) {
        Gin app = Gin.getInstance();
        app.setPSIDAndSN(psid, sn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
