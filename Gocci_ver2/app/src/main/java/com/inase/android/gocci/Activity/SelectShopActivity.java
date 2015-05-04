package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.github.ppamorim.library.DraggerActivity;
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.UserData;

import java.util.ArrayList;

public class SelectShopActivity extends DraggerActivity {

    private ListView selectList;
    private Search_tenpoAdapter mSearch_tenpoAdapter;

    private String clickedRestname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);

        selectList = (ListView) findViewById(R.id.shopList);
        selectList.setDivider(null);
        // スクロールバーを表示しない
        selectList.setVerticalScrollBarEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectList.addFooterView(inflater.inflate(R.layout.cell_no_exist_shop, null));

        RippleView noexistRipple = (RippleView) findViewById(R.id.noexistRipple);

        noexistRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SelectShopActivity.this)
                        .title("店舗追加")
                        .content("あなたのいるお店の名前を入力してください。※位置情報は現在の位置を使います")
                        .input("店舗名", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                            }
                        })
                        .positiveText("送信する")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                Toast.makeText(SelectShopActivity.this, dialog.getInputEditText().getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        // カード部分をselectorにするので、リストのselectorは透明にする
        selectList.setSelector(android.R.color.transparent);

        mSearch_tenpoAdapter = new Search_tenpoAdapter(this, 0, CameraActivity.users);

        selectList.setAdapter(mSearch_tenpoAdapter);
    }

    public static class Search_tenpoHolder {
        public RippleView searchRipple;
        public TextView restname;
        public TextView subscribeText;
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
                convertView = layoutInflater.inflate(R.layout.cell_select_shop, null);
                tenpoHolder = new Search_tenpoHolder();
                tenpoHolder.searchRipple = (RippleView) convertView.findViewById(R.id.searchRipple);
                tenpoHolder.restname = (TextView) convertView.findViewById(R.id.restname);
                tenpoHolder.subscribeText = (TextView) convertView.findViewById(R.id.subscribeText);

                convertView.setTag(tenpoHolder);
            } else {
                tenpoHolder = (Search_tenpoHolder) convertView.getTag();
            }
            final UserData user = this.getItem(position);

            tenpoHolder.restname.setText(user.getRest_name());
            tenpoHolder.subscribeText.setText("この店舗を撮影する");

            tenpoHolder.searchRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedRestname = user.getRest_name();

                    Handler handler = new Handler();
                    handler.postDelayed(new searchClickHandler(), 750);

                }
            });

            return convertView;
        }
    }

    class searchClickHandler implements Runnable {
        public void run() {
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("restname", clickedRestname);
            data.putExtras(bundle);

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
