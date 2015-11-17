package com.inase.android.gocci.utils.map;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.inase.android.gocci.domain.model.PostData;

/**
 * Created by kinagafuji on 15/06/30.
 */
public class PhotoLog implements ClusterItem {

    public final String mRestname;
    public final String mDatetime;
    //public final int mWant_flag;
    public final int mCheer_num;
    public final PostData userdata;
    public final Drawable mDrawable;
    private final LatLng mPosition;

    public PhotoLog(PostData user, Drawable drawable) {
        mRestname = user.getRestname();
        mPosition = new LatLng(user.getLat(), user.getLon());
        mDatetime = user.getPost_date();
        //mWant_flag = user.getWant_flag();
        mCheer_num = user.getCheer_flag();
        userdata = user;
        mDrawable = drawable;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
