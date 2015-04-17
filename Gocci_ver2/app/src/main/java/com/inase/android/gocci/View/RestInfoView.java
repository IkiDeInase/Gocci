package com.inase.android.gocci.View;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.inase.android.gocci.R;

public class RestInfoView extends LinearLayout {

    private TextView restnameText;
    private TextView localityText;
    private RippleView goMapRipple;

    private double mLat;
    private double mLon;

    public RestInfoView(Context context, String restname, String locality, Double lat, Double lon) {
        super(context);
        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_restinfo, this);
        mLat = lat;
        mLon = lon;

        restnameText = (TextView) inflateView.findViewById(R.id.info_restname);
        localityText = (TextView) inflateView.findViewById(R.id.info_locality);
        goMapRipple = (RippleView) inflateView.findViewById(R.id.goMapRipple);

        restnameText.setText(restname);
        localityText.setText(locality);

        goMapRipple.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler();
                handler.postDelayed(new GoMapClickHandler(), 750);
            }
        });

    }

    public RestInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RestInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    class GoMapClickHandler implements Runnable {
        public void run() {
            String intentUrl = "google.navigation:q=" + mLat + "," + mLon + "&mode=w";
            Uri gmmIntentUri = Uri.parse(intentUrl);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            getContext().startActivity(mapIntent);

        }
    }
}
