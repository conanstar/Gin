package com.studio.conan.gin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class GinActivity extends Activity {

    private static final int REQUEST_CODE_STAFF_LOGIN = 0;
//    private static final int REQUEST_CODE_EMPLOYEE_LOGIN = 1;
    private Gin application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gin);

        application = Gin.getInstance();

        checkLoginType();
    }

    private void checkLoginType() {
        int loginType = application.getLoginType();
        switch (loginType) {
            case Gin.LOGIN_TYPE_NOT_LOGIN:
                break;
            case Gin.LOGIN_TYPE_STAFF:
                if (!TextUtils.isEmpty(application.getPSK())) {
                    launchStaffMainActivity();
                }
                break;
//            case Gin.LOGIN_TYPE_EMPLOYEE:
//                String psid = application.getPSID();
//                String sn = application.getSN();
//                if (!TextUtils.isEmpty(psid) && !TextUtils.isEmpty(sn)) {
//                    launchEmployeeMain();
//                }
//                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_STAFF_LOGIN:
                if (resultCode == RESULT_OK) {
                    application.setLoginType(Gin.LOGIN_TYPE_STAFF);
                    launchStaffMainActivity();
                }
                break;
//            case REQUEST_CODE_EMPLOYEE_LOGIN:
//                if (resultCode == RESULT_OK) {
//                    application.setLoginType(Gin.LOGIN_TYPE_EMPLOYEE);
//                    launchEmployeeMain();
//                }
//                break;
        }
    }

    private void launchStaffMainActivity() {
        Intent intent = new Intent(this, StaffMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchEmployeeMain(View view) {
        Intent intent = new Intent(this, EmployeeMainActivity.class);
        startActivity(intent);
    }

    public void staffLogin(View view) {
        Intent intent = new Intent(this, StaffLoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_STAFF_LOGIN);
    }
}
