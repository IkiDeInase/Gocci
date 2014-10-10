package com.example.kinagafuji.gocci.Base;

import android.app.Dialog;
import android.content.Context;

import com.example.kinagafuji.gocci.R;

public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context) {
        super(context, R.style.Theme_CustomProgressDialog);

        setContentView(R.layout.custom_progress_dialog);
    }
}
