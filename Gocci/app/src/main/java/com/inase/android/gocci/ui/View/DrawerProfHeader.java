package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.SavedData;
import com.squareup.picasso.Picasso;

public class DrawerProfHeader extends RelativeLayout {

    public DrawerProfHeader(final Context context) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.header_drawer_prof, this);

        TextView username = (TextView) inflateView.findViewById(R.id.header_username);
        ImageView userpicture = (ImageView) inflateView.findViewById(R.id.header_userpicture);
        ImageView userbackground = (ImageView) inflateView.findViewById(R.id.header_userbackground);

        username.setText(SavedData.getServerName(context));
        Picasso.with(context)
                .load(SavedData.getServerPicture(context))
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(userpicture);

        /*
        Picasso.with(context)
                .load(SavedData.getServerBackground(context))
                .fit()
                .centerCrop()
                .into(userbackground);
                */

    }

    //xmlからの生成用
    public DrawerProfHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerProfHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
