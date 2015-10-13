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
import com.inase.android.gocci.R;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class UserProfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_PHOTO = 1;

    private Context mContext;
    private HeaderData mUserData;
    private ArrayList<PostData> mPostData = new ArrayList<>();

    private int cellSize;
    private boolean lockedAnimations = false;
    private long profileHeaderAnimationStartTime = 0;
    private int lastAnimatedItem = 0;

    private UserProfCallback mCallback;

    public UserProfAdapter(Context context, HeaderData userData, ArrayList<PostData> postData) {
        this.mContext = context;
        this.mUserData = userData;
        this.mPostData = postData;
        this.cellSize = Util.getScreenWidth(context) / 3;
    }

    public void setUserProfCallback(UserProfCallback callback) {
        mCallback = callback;
    }

    public void setData(HeaderData headerData) {
        mUserData = headerData;
        this.notifyDataSetChanged();
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
            final View view = LayoutInflater.from(mContext).inflate(R.layout.view_header_userprof, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new UserProfHeaderViewHolder(view);
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
            bindHeader((UserProfHeaderViewHolder) holder);
        } else {
            PostData users = mPostData.get(position - 1);
            bindPhoto((GridViewHolder) holder, position, users);
        }
    }

    private void bindHeader(final UserProfHeaderViewHolder holder) {
        holder.mUserprofUsername.setText(mUserData.getUsername());
        Picasso.with(mContext)
                .load(mUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mUserprofPicture);

        holder.mFollowNum.setText(String.valueOf(mUserData.getFollow_num()));
        holder.mFollowerNum.setText(String.valueOf(mUserData.getFollower_num()));
        holder.mUsercheerNum.setText(String.valueOf(mUserData.getCheer_num()));

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

        if (mUserData.getFollow_flag() == 0) {
            holder.mFollowText.setText(mContext.getString(R.string.do_follow));
        } else {
            holder.mFollowText.setText(mContext.getString(R.string.do_unfollow));
        }

        if (mUserData.getUsername().equals(SavedData.getServerName(mContext))) {
            holder.mFollowText.setText(mContext.getString(R.string.do_yours));
        }

        holder.mUserprof_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //お気に入りするときの処理
                switch (holder.mFollowText.getText().toString()) {
                    case "フォローする":
                        Util.followAsync(mContext, mUserData);
                        holder.mFollowText.setText(mContext.getString(R.string.do_unfollow));
                        break;
                    case "フォロー解除する":
                        Util.unfollowAsync(mContext, mUserData);
                        holder.mFollowText.setText(mContext.getString(R.string.do_follow));
                        break;
                    case "これはあなたです":
                        break;
                }
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

        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    @Override
    public int getItemCount() {
        return mPostData.size() + 1;
    }

    static class UserProfHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.userprof_background)
        ImageView mUserprofBackground;
        @Bind(R.id.userprof_picture)
        ImageView mUserprofPicture;
        @Bind(R.id.location)
        ImageView mLocationButton;
        @Bind(R.id.userprof_username)
        TextView mUserprofUsername;
        @Bind(R.id.userprof_follow)
        RippleView mUserprof_Follow;
        @Bind(R.id.follow_num)
        TextView mFollowNum;
        @Bind(R.id.follower_num)
        TextView mFollowerNum;
        @Bind(R.id.usercheer_num)
        TextView mUsercheerNum;
        @Bind(R.id.follow_text)
        TextView mFollowText;
        @Bind(R.id.follow_ripple)
        RippleView mFollowRipple;
        @Bind(R.id.follower_ripple)
        RippleView mFollowerRipple;
        @Bind(R.id.usercheer_ripple)
        RippleView mUsercheerRipple;

        public UserProfHeaderViewHolder(View view) {
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

    public interface UserProfCallback {
        void onFollowListClick(int user_id);

        void onFollowerListClick(int user_id);

        void onUserCheerClick(int user_id);

        void onImageClick(int post_id);

        void onLocationClick(ArrayList<PostData> postData);
    }
}
