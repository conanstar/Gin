package com.studio.conan.gin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class CustomDialog extends AlertDialog {

    private Context mContext;

    protected CustomDialog(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = mContext.getResources();
        final int red = res.getColor(android.R.color.holo_red_dark);

        // Custom title
        final int titleId = res.getIdentifier("alertTitle", "id", "android");
        final View title = findViewById(titleId);
        if (title != null) {
            ((TextView) title).setTextColor(red);
        }

        // Custom title divider
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(red);
        }

    }
}
