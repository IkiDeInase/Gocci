package com.inase.android.gocci.utils.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by kinagafuji on 15/12/03.
 */
public class HeatmapLog implements ClusterItem {
    public final String mRest_id;
    public final String mRestname;
    private final LatLng mPosition;

    public HeatmapLog(String rest_id, String restname, double lat, double lng) {
        mRest_id = rest_id;
        mRestname = restname;
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
