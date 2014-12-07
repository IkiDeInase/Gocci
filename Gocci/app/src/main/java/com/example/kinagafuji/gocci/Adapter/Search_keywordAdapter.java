package com.example.kinagafuji.gocci.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.LayoutHolder;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Search_keywordAdapter extends ArrayAdapter<UserData> {
    private LayoutInflater layoutInflater;
    private LayoutHolder.Search_mapHolder search_mapHolder;

    Search_mapFragment fragment = new Search_mapFragment();

    public Search_keywordAdapter(Context context, int viewResourceId, ArrayList<UserData> search_keywordusers) {
        super(context, viewResourceId, search_keywordusers);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.maplist, null);
            search_mapHolder = new LayoutHolder.Search_mapHolder(convertView);
            convertView.setTag(search_mapHolder);
        } else {
            search_mapHolder = (LayoutHolder.Search_mapHolder) convertView.getTag();
        }

        final UserData user = this.getItem(position);

        search_mapHolder.restname.setText(user.getRest_name());
        search_mapHolder.category.setText(user.getCategory());
        search_mapHolder.distance.setText(user.getDistance());

        LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
        fragment.mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));

        return convertView;
    }
}
