package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class MyProfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_PHOTO = 1;

    private Context mContext;
    private HeaderData mUserData;
    private ArrayList<PostData> mPostData = new ArrayList<>();

    private int cellSize;
    private boolean lockedAnimations = false;
    private long profileHeaderAnimationStartTime = 0;
    private int lastAnimatedItem = 0;

    private MyProfCallback mCallback;

    public MyProfAdapter(Context context, HeaderData userData, ArrayList<PostData> postData) {
        this.mContext = context;
        this.mUserData = userData;
        this.mPostData = postData;
        this.cellSize = Util.getScreenWidth(context) / 3;
    }

    public void setMyProfCallback(MyProfCallback callback) {
        mCallback = callback;
    }

    public void setData(HeaderData headerData, ArrayList<PostData> postData) {
        mUserData = headerData;
        mPostData = postData;
        this.notifyDataSetChanged();
    }

    public void setHeaderData(HeaderData headerData) {
        mUserData = headerData;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PROFILE_HEADER;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_PROFILE_HEADER == viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.view_header_myprof, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new MyProfHeaderViewHolder(view);
        } else {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_grid, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            return new GridViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_PROFILE_HEADER == viewType) {
            bindHeader((MyProfHeaderViewHolder) holder);
        } else {
            PostData users = mPostData.get(position - 1);
            bindPhoto((GridViewHolder) holder, position, users);
        }
    }

    private void bindHeader(final MyProfHeaderViewHolder holder) {
        holder.mMyprofUsername.setText(mUserData.getUsername());
        Picasso.with(mContext)
                .load(mUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mMyprofPicture);

        holder.mFollowNum.setText(String.valueOf(mUserData.getFollow_num()));
        holder.mFollowerNum.setText(String.valueOf(mUserData.getFollower_num()));
        holder.mUsercheerNum.setText(String.valueOf(mUserData.getCheer_num()));
        holder.mWantNum.setText(String.valueOf(mUserData.getWant_num()));

        holder.mFollowRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onFollowListClick(mUserData.getUser_id());
            }
        });

        holder.mFollowerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onFollowerListClick(mUserData.getUser_id());
            }
        });

        holder.mUsercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onUserCheerClick(mUserData.getUser_id());
            }
        });

        holder.mWantRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onWantClick(mUserData.getUser_id());
            }
        });

        holder.mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEditProfileClick();
            }
        });

        holder.mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onLocationClick(mPostData);
            }
        });
    }

    private void bindPhoto(final GridViewHolder holder, final int position, final PostData users) {
        Picasso.with(mContext)
                .load(users.getThumbnail())
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.mSquareImage);

        holder.mSquareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onImageClick(Integer.parseInt(users.getPost_id()));
            }
        });

        holder.mSquareImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onImageLongClick(users.getPost_id(), position - 1);
                return false;
            }
        });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    @Override
    public int getItemCount() {
        return mPostData.size() + 1;
    }

    static class MyProfHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.myprof_background)
        ImageView mMyprofBackground;
        @Bind(R.id.myprof_picture)
        ImageView mMyprofPicture;
        @Bind(R.id.location)
        ImageView mLocationButton;
        @Bind(R.id.myprof_username)
        TextView mMyprofUsername;
        @Bind(R.id.edit_profile)
        RippleView mEditProfile;
        @Bind(R.id.follow_num)
        TextView mFollowNum;
        @Bind(R.id.follower_num)
        TextView mFollowerNum;
        @Bind(R.id.usercheer_num)
        TextView mUsercheerNum;
        @Bind(R.id.want_num)
        TextView mWantNum;
        @Bind(R.id.follow_ripple)
        RippleView mFollowRipple;
        @Bind(R.id.follower_ripple)
        RippleView mFollowerRipple;
        @Bind(R.id.usercheer_ripple)
        RippleView mUsercheerRipple;
        @Bind(R.id.want_ripple)
        RippleView mWantRipple;

        public MyProfHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.square_image)
        SquareImageView mSquareImage;

        public GridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface MyProfCallback {
        void onFollowListClick(int user_id);

        void onFollowerListClick(int user_id);

        void onUserCheerClick(int user_id);

        void onWantClick(int user_id);

        void onEditProfileClick();

        void onImageClick(int post_id);

        void onImageLongClick(String post_id, int position);

        void onLocationClick(ArrayList<PostData> postData);
    }
}
