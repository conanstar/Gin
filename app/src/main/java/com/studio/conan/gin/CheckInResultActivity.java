package com.studio.conan.gin;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CheckInResultActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener {

    private ImageButton prizeTag1;
    private ImageButton prizeTag2;
    private ImageButton prizeTag3;
    private ImageButton prizeTag4;
    private Button sendPrizeTagButton;
    private View checkInSuccessView;
    private View checkInFailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_result);
        initWidget();

        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("data");
        if (TextUtils.isEmpty(data)) {
            checkInFailView.setVisibility(View.VISIBLE);
            checkInSuccessView.setVisibility(View.GONE);
        } else {
            checkInSuccessView.setVisibility(View.VISIBLE);
            checkInFailView.setVisibility(View.GONE);
        }
    }

    private void initWidget() {
        checkInSuccessView = findViewById(R.id.check_in_success_view);
        checkInFailView = findViewById(R.id.check_in_fail_view);
        prizeTag1 = (ImageButton) findViewById(R.id.prize_tag_1);
        prizeTag1.setOnClickListener(this);
        prizeTag2 = (ImageButton) findViewById(R.id.prize_tag_2);
        prizeTag2.setOnClickListener(this);
        prizeTag3 = (ImageButton) findViewById(R.id.prize_tag_3);
        prizeTag3.setOnClickListener(this);
        prizeTag4 = (ImageButton) findViewById(R.id.prize_tag_4);
        prizeTag4.setOnClickListener(this);

        sendPrizeTagButton = (Button) findViewById(R.id.btn_send);
        sendPrizeTagButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_in_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v) {
        cleanAllTag();

        int id = v.getId();
        switch (id) {
            case R.id.prize_tag_1:
                prizeTag1.setBackgroundResource(R.drawable.custom_prize_1_btn_selected);
                prizeTag1.setImageResource(R.drawable.ic_checked);
                sendPrizeTagButton.setBackgroundResource(R.drawable.custom_prize_1_btn_selected);
                break;
            case R.id.prize_tag_2:
                prizeTag2.setBackgroundResource(R.drawable.custom_prize_2_btn_selected);
                prizeTag2.setImageResource(R.drawable.ic_checked);
                sendPrizeTagButton.setBackgroundResource(R.drawable.custom_prize_2_btn_selected);
                break;
            case R.id.prize_tag_3:
                prizeTag3.setBackgroundResource(R.drawable.custom_prize_3_btn_selected);
                prizeTag3.setImageResource(R.drawable.ic_checked);
                sendPrizeTagButton.setBackgroundResource(R.drawable.custom_prize_3_btn_selected);
                break;
            case R.id.prize_tag_4:
                prizeTag4.setBackgroundResource(R.drawable.custom_prize_4_btn_selected);
                prizeTag4.setImageResource(R.drawable.ic_checked);
                sendPrizeTagButton.setBackgroundResource(R.drawable.custom_prize_4_btn_selected);
                break;
            case R.id.btn_send:
                showConfirmDialog();
                break;
        }
    }

    private void showConfirmDialog() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(R.string.title_dialog_confirm_send);

        String msgFormat = getString(R.string.message_dialog_confirm_send);
        dialog.setMessage(String.format(msgFormat, "Conan", 21900));

        String strSend = getString(R.string.action_send);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, strSend, this);

        String strCancel = getString(R.string.action_cancel);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, strCancel, this);

        dialog.show();
    }

    private void cleanAllTag() {
        prizeTag1.setBackgroundResource(R.drawable.custom_prize_1_btn_release);
        prizeTag1.setImageResource(0);
        prizeTag2.setBackgroundResource(R.drawable.custom_prize_2_btn_release);
        prizeTag2.setImageResource(0);
        prizeTag3.setBackgroundResource(R.drawable.custom_prize_3_btn_release);
        prizeTag3.setImageResource(0);
        prizeTag4.setBackgroundResource(R.drawable.custom_prize_4_btn_release);
        prizeTag4.setImageResource(0);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                // TODO: Send Prize Tag to Server
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }
}
