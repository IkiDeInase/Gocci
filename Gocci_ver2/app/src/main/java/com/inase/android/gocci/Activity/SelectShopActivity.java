package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.github.ppamorim.library.DraggerActivity;
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.UserData;

import java.util.ArrayList;

public class SelectShopActivity extends DraggerActivity {

    private ListView selectList;
    private Search_tenpoAdapter mSearch_tenpoAdapter;

    private String clickedRestname;
    private String clickedLocality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);

        selectList = (ListView) findViewById(R.id.shopList);
        selectList.setDivider(null);
        // スクロールバーを表示しない
        selectList.setVerticalScrollBarEnabled(false);

        TextView subscribeText = new TextView(this);
        subscribeText.setText("撮影するお店を選択してください");
        subscribeText.setBackgroundResource(R.color.backgroung_grey);
        subscribeText.setTextSize(18);
        subscribeText.setPadding(8, 16, 8, 16);
        subscribeText.setGravity(Gravity.CENTER_HORIZONTAL);
        selectList.addHeaderView(subscribeText);

        // カード部分をselectorにするので、リストのselectorは透明にする
        selectList.setSelector(android.R.color.transparent);

        mSearch_tenpoAdapter = new Search_tenpoAdapter(this, 0, CameraActivity.users);

        selectList.setAdapter(mSearch_tenpoAdapter);
    }

    public static class Search_tenpoHolder {
        public RippleView searchRipple;
        public TextView restname;
        public TextView distance;
        public TextView subscribeText;
        public LinearLayout paintBackground;
    }

    public class Search_tenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public Search_tenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> search_mapusers) {
            super(context, viewResourceId, search_mapusers);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Search_tenpoHolder tenpoHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.cell_search, null);
                tenpoHolder = new Search_tenpoHolder();
                tenpoHolder.searchRipple = (RippleView) convertView.findViewById(R.id.searchRipple);
                tenpoHolder.restname = (TextView) convertView.findViewById(R.id.restname);
                tenpoHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                tenpoHolder.subscribeText = (TextView) convertView.findViewById(R.id.subscribeText);
                tenpoHolder.paintBackground = (LinearLayout) convertView.findViewById(R.id.paintBackground);

                convertView.setTag(tenpoHolder);
            } else {
                tenpoHolder = (Search_tenpoHolder) convertView.getTag();
            }
            final UserData user = this.getItem(position);

            tenpoHolder.restname.setText(user.getRest_name());
            tenpoHolder.distance.setText(user.getDistance());
            tenpoHolder.subscribeText.setText("この店舗を撮影する");

            tenpoHolder.searchRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedRestname = user.getRest_name();
                    clickedLocality = user.getLocality();

                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("restname", clickedRestname);
                    data.putExtras(bundle);

                    setResult(RESULT_OK, data);
                    finish();
                }
            });

            return convertView;
        }
    }
}
