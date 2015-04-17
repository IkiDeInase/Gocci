package com.inase.android.gocci.View;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inase.android.gocci.Activity.FollowerFolloweeCheerListActivity;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItem;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;
import com.squareup.picasso.Picasso;

public class DrawerProfHeader extends LinearLayout {

    private String mName;
    private String mPictureImageUrl;

    private ViewPager viewPager;
    private ViewPagerItemAdapter adapter;

    private int followeeNumber;
    private int followerNumber;
    private int cheerNumber;

    private boolean isFirst = true;

    public DrawerProfHeader(final Context context, String name, String picture, final Integer follower, Integer followee, Integer cheer) {
        super(context);

        mName = name;
        mPictureImageUrl = picture;
        followerNumber = follower;
        followeeNumber = followee;
        cheerNumber = cheer;

        View inflateView = LayoutInflater.from(context).inflate(R.layout.header_drawer_prof, this);

        TextView username = (TextView) inflateView.findViewById(R.id.header_username);
        ImageView userpicture = (ImageView) inflateView.findViewById(R.id.header_userpicture);

        username.setText(mName);
        Picasso.with(context)
                .load(mPictureImageUrl)
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(userpicture);

        adapter = new ViewPagerItemAdapter(ViewPagerItems.with(context)
                .add(ViewPagerItem.of("フォロー", R.layout.viewpager_prof_header))
                .add(ViewPagerItem.of("フォロワー", R.layout.viewpager_prof_header))
                .add(ViewPagerItem.of("応援店", R.layout.viewpager_prof_header))
                .create());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isFirst && position == 0) {
                    View page = adapter.getPage(position);
                    final TextView text = (TextView) page.findViewById(R.id.numberTani);
                    text.setText(followerNumber + "人");
                    text.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent followerIntent = new Intent(context, FollowerFolloweeCheerListActivity.class);
                            followerIntent.putExtra("name", mName);
                            followerIntent.putExtra("category", "follower");
                            context.startActivity(followerIntent);
                        }
                    });
                    isFirst = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                View page = adapter.getPage(position);
                final TextView text = (TextView) page.findViewById(R.id.numberTani);

                switch (position) {
                    case 0:
                        text.setText(followerNumber + "人");
                        text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent followerIntent = new Intent(context, FollowerFolloweeCheerListActivity.class);
                                followerIntent.putExtra("name", mName);
                                followerIntent.putExtra("category", "follower");
                                context.startActivity(followerIntent);
                            }
                        });
                        break;
                    case 1:
                        text.setText(followeeNumber + "人");
                        text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent followeeIntent = new Intent(context, FollowerFolloweeCheerListActivity.class);
                                followeeIntent.putExtra("name", mName);
                                followeeIntent.putExtra("category", "followee");
                                context.startActivity(followeeIntent);
                            }
                        });
                        break;
                    case 2:
                        text.setText(cheerNumber + "店");
                        text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent cheerIntent = new Intent(context, FollowerFolloweeCheerListActivity.class);
                                cheerIntent.putExtra("name", mName);
                                cheerIntent.putExtra("category", "cheer");
                                context.startActivity(cheerIntent);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //xmlからの生成用
    public DrawerProfHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerProfHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
