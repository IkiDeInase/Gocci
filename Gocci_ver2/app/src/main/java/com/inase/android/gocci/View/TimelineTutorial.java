package com.inase.android.gocci.View;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.inase.android.gocci.R;

public class TimelineTutorial extends LinearLayout {
    public TimelineTutorial(Context context) {
        super(context);
        //チュートリアル・使い方ガイドを実装しようとしているクラス(未完成)

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_tutorial, this);

    }

    public TimelineTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimelineTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
