package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class RestPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TENPO_HEADER = 0;
    private static final int TYPE_POST = 1;

    private Context mContext;
    private HeaderData mRestData;
    private ArrayList<PostData> mPostData = new ArrayList<>();

    private RestPageCallback mCallback;

    private MapView mMapView;

    public RestPageAdapter(Context context, HeaderData restData, ArrayList<PostData> postData) {
        this.mContext = context;
        this.mRestData = restData;
        this.mPostData = postData;
    }

    public void setRestPageCallback(RestPageCallback callback) {
        mCallback = callback;
    }

    public void setData(HeaderData headerData) {
        mRestData = headerData;
        this.notifyDataSetChanged();
    }

    public MapView getMapView() {
        return mMapView;
    }

    public PostData getItem(int position) {
        return mPostData.get(position);
    }

    public boolean isEmpty() {
        return mPostData.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TENPO_HEADER;
        } else {
            return TYPE_POST;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_TENPO_HEADER == viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_tenpo_header, parent, false);
            return new TenpoHeaderViewHolder(view);
        } else {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_stream_rest_list, parent, false);
            return new Const.StreamRestViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_TENPO_HEADER == viewType) {
            bindHeader((TenpoHeaderViewHolder) holder);
        } else {
            PostData users = mPostData.get(position - 1);
            bindPost((Const.StreamRestViewHolder) holder, users);
        }
    }

    private void bindHeader(final TenpoHeaderViewHolder holder) {
        holder.mCallRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onCallClick(Uri.parse("tel:" + mRestData.getTell()));
            }
        });

        holder.mGoHereRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onGoHereClick(Uri.parse("google.navigation:q=" + mRestData.getLat() + "," + mRestData.getLon() + "&mode=w"));
            }
        });

        holder.mEtcRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!mRestData.getHomepage().equals("none")) {
                    new MaterialDialog.Builder(mContext)
                            .items(R.array.list_tenpo_menu)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    if (charSequence.toString().equals(mContext.getString(R.string.seeHomepage))) {
                                        mCallback.onHomePageClick(Uri.parse(mRestData.getHomepage()));
                                    }
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.nothing_etc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (holder.mGoogleMap == null) {
            holder.mGoogleMap = holder.mMap.getMap();
        }
        if (holder.mGoogleMap != null) {
            //move map to the 'location'
            LatLng lng = new LatLng(mRestData.getLat(), mRestData.getLon());
            holder.mGoogleMap.getUiSettings().setCompassEnabled(false);
            holder.mGoogleMap.addMarker(new MarkerOptions().position(lng).title(mRestData.getRestname()));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lng)
                    .zoom(15)
                    .tilt(50)
                    .build();
            holder.mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        holder.mTenpoCategory.setText(mRestData.getRest_category());
    }

    private void bindPost(final Const.StreamRestViewHolder holder, final PostData user) {
        holder.mName.setText(user.getUsername());

        holder.mTimeText.setText(user.getPost_date());

        if (!user.getMemo().equals("none")) {
            holder.mComment.setText(user.getMemo());
        } else {
            holder.mComment.setText("");
        }

        Picasso.with(mContext)
                .load(user.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCircleImage);

        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(user.getPost_user_id(), user.getUsername());
            }
        });

        holder.mCircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(user.getPost_user_id(), user.getUsername());
            }
        });

        holder.mMenuRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.violation:
                                Util.setPostBlockDialog(mContext, user.getPost_id());
                                break;
                            case R.id.close:
                                dialog.dismiss();
                        }
                    }
                }).show();
            }
        });
        Picasso.with(mContext)
                .load(user.getThumbnail())
                .placeholder(R.color.videobackground)
                .into(holder.mVideoThumbnail);
        holder.mVideoThumbnail.setVisibility(View.VISIBLE);

        holder.mVideoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onVideoFrameClick();
            }
        });

        if (!user.getCategory().equals(mContext.getString(R.string.nothing_tag))) {
            holder.mCategory.setText(user.getCategory());
        } else {
            holder.mCategory.setText("　　　　");
        }
        if (!user.getValue().equals("0")) {
            holder.mValue.setText(user.getValue() + "円");
        } else {
            holder.mValue.setText("　　　　");
        }

        holder.mLikesNumber.setText(String.valueOf(user.getGochi_num()));
        holder.mCommentsNumber.setText(String.valueOf(user.getComment_num()));

        if (!user.isGochi_flag()) {
            holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef);
        } else {
            holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
        }
        holder.mLikesRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.isGochi_flag()) {
                    mCallback.onGochiTap();
                    mCallback.onGochiClick(user.getPost_id(), Const.APICategory.SET_GOCHI);
                    user.setGochi_flag(true);
                    user.setGochi_num(user.getGochi_num() + 1);
                    holder.mLikesNumber.setText(String.valueOf((user.getGochi_num())));
                    holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                } else {
                    mCallback.onGochiClick(user.getPost_id(), Const.APICategory.UNSET_GOCHI);
                    user.setGochi_flag(false);
                    user.setGochi_num(user.getGochi_num() - 1);
                    holder.mLikesNumber.setText(String.valueOf((user.getGochi_num())));
                    holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef);
                }
            }
        });

        holder.mCommentsRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onCommentClick(user.getPost_id());
            }
        });

        holder.mShareRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Application_Gocci.getShareTransfer() != null) {
                    new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.facebook_share:
                                    Toast.makeText(mContext, mContext.getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    mCallback.onFacebookShare(user.getMovie(), user.getRestname());
                                    break;
                                case R.id.twitter_share:
                                    mCallback.onTwitterShare(user.getMovie(), mRestData.getRestname());
                                    break;
                                case R.id.other_share:
                                    Toast.makeText(mContext, mContext.getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    mCallback.onInstaShare(user.getMovie(), user.getRestname());
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.preparing_share_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCallback.onHashHolder(holder, user.getPost_id());
    }

    @Override
    public int getItemCount() {
        return mPostData.size() + 1;
    }

    public class TenpoHeaderViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        @Bind(R.id.category)
        TextView mTenpoCategory;
        @Bind(R.id.call_ripple)
        RippleView mCallRipple;
        @Bind(R.id.go_here_ripple)
        RippleView mGoHereRipple;
        @Bind(R.id.etc_ripple)
        RippleView mEtcRipple;
        @Bind(R.id.map)
        MapView mMap;
        private GoogleMap mGoogleMap;

        public TenpoHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            if (mMap != null) {
                mMap.onCreate(null);
                mMap.onResume();
                mMapView = mMap;
                mMap.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            mGoogleMap = googleMap;

            LatLng lng = new LatLng(mRestData.getLat(), mRestData.getLon());
            mGoogleMap.getUiSettings().setCompassEnabled(false);
            mGoogleMap.addMarker(new MarkerOptions().position(lng).title(mRestData.getRestname()));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lng)
                    .zoom(15)
                    .tilt(50)
                    .build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public interface RestPageCallback {
        void onCallClick(Uri tel);

        void onGoHereClick(Uri uri);

        void onHomePageClick(Uri uri);

        void onUserClick(String user_id, String user_name);

        void onCommentClick(String post_id);

        void onGochiTap();

        void onGochiClick(String post_id, Const.APICategory apiCategory);

        void onVideoFrameClick();

        void onFacebookShare(String share, String rest_name);

        void onTwitterShare(String share, String rest_name);

        void onInstaShare(String share, String rest_name);

        void onHashHolder(Const.StreamRestViewHolder holder, String post_id);
    }
}
