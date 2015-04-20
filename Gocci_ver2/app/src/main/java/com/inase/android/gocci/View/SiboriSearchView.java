package com.inase.android.gocci.View;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.inase.android.gocci.R;

public class SiboriSearchView extends LinearLayout {

    public SiboriSearchView(Context context) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_sibori_tenpo, this);
    }

    public SiboriSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SiboriSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
