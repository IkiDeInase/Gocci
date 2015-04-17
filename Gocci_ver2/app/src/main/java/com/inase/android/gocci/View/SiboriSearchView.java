package com.inase.android.gocci.View;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.SiboriNumberEvent;
import com.inase.android.gocci.R;

public class SiboriSearchView extends LinearLayout {

    private EditText numberEdit;
    private Button finishButton;

    public SiboriSearchView(Context context) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_sibori_tenpo, this);
        numberEdit = (EditText) inflateView.findViewById(R.id.numberEdit);
        finishButton = (Button) inflateView.findViewById(R.id.finishButton);

        finishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = String.valueOf(numberEdit.getText());
                int Number = Integer.parseInt(number);
                BusHolder.get().post(new SiboriNumberEvent(Number));
            }
        });

    }

    public SiboriSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SiboriSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
