package com.friox.kazehikarufinder;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class CustomAlertDialog extends Dialog {

    private TextView alertTitle, alertContents;
    private Button mPositiveButton, mNegativeButton;
    private View.OnClickListener mPositiveListener, mNegativeListener;
    private String stringTitle, stringContents;
    private int type, color;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.custom_alert_dialog);
        alertTitle = findViewById(R.id.custom_alert_title);
        alertContents = findViewById(R.id.custom_alert_contents);
        mPositiveButton = findViewById(R.id.custom_alert_positive_button);
        mNegativeButton = findViewById(R.id.custom_alert_negative_button);
        alertTitle.setText(stringTitle);
        alertContents.setText(stringContents);
        View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        mPositiveButton.setBackgroundColor(color);
        Configuration configuration = context.getResources().getConfiguration();
        if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            alertTitle.setTextColor(Color.WHITE);
        } else {
            alertTitle.setTextColor(color);
        }
        if (type == 1) {
            if (mPositiveListener == null) mPositiveButton.setOnClickListener(dismissListener);
            else mPositiveButton.setOnClickListener(mPositiveListener);

            if (mNegativeListener == null) mNegativeButton.setOnClickListener(dismissListener);
            else mNegativeButton.setOnClickListener(mNegativeListener);
        } else {
            mNegativeButton.setVisibility(View.INVISIBLE);
            mPositiveButton.setOnClickListener(dismissListener);
        }
    }

    public CustomAlertDialog(@NonNull Context context, String title, String contents, int pointColor, View.OnClickListener mPositiveListener, View.OnClickListener mNegativeListener) {
        // Custom Action
        super(context);
        this.context = context;
        this.stringTitle = title;
        this.stringContents = contents;
        this.mPositiveListener = mPositiveListener;
        this.mNegativeListener = mNegativeListener;
        this.type = 1;
        this.color = pointColor;
    }

    public CustomAlertDialog(@NonNull Context context, String title, String contents, int pointColor) {
        // only notification
        super(context);
        this.context = context;
        this.stringTitle = title;
        this.stringContents = contents;
        this.type = 2;
        this.color = pointColor;
    }
}
