package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ListGetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Const.ListCategory mCategory;
    private ArrayList<ListGetData> mList = new ArrayList<>();

    private ListGetCallback mCallback;

    public void setListGetCallback(ListGetCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public ListGetAdapter(Context context, Const.ListCategory category, ArrayList<ListGetData> list) {
        this.mContext = context;
        this.mCategory = category;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (mCategory) {
            case FOLLOW:
            case FOLLOWER:
                v = LayoutInflater.from(mContext).inflate(R.layout.cell_follow_follower, parent, false);
                return new Const.FollowFollowerViewHolder(v);
            case WANT:
                v = LayoutInflater.from(mContext).inflate(R.layout.cell_want, parent, false);
                return new Const.WantViewHolder(v);
            case USER_CHEER:
                v = LayoutInflater.from(mContext).inflate(R.layout.cell_cheer, parent, false);
                return new Const.UserCheerViewHolder(v);
            case REST_CHEER:
                v = LayoutInflater.from(mContext).inflate(R.layout.cell_tenpo_cheer, parent, false);
                return new Const.RestCheerViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ListGetData data = mList.get(position);
        switch (mCategory) {
            case FOLLOW:
            case FOLLOWER:
                bindFollowFollower((Const.FollowFollowerViewHolder) viewHolder, data);
                break;
            case WANT:
                bindWant((Const.WantViewHolder) viewHolder, data);
                break;
            case USER_CHEER:
                bindUserCheer((Const.UserCheerViewHolder) viewHolder, data);
                break;
            case REST_CHEER:
                bindRestCheer((Const.RestCheerViewHolder) viewHolder, data);
                break;
            default:
                break;
        }
    }

    private void bindFollowFollower(final Const.FollowFollowerViewHolder holder, final ListGetData data) {
        holder.mUserName.setText(data.getUsername());

        Picasso.with(mContext)
                .load(data.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mFollowFollowerPicture);

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        holder.mFollowFollowerPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        if (data.getFollow_flag() == 0) {
            holder.mAddFollowButton.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteFollowButton.setVisibility(View.VISIBLE);
        }
        holder.mAccountRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mDeleteFollowButton.isShown()) {
                    holder.mDeleteFollowButton.setVisibility(View.INVISIBLE);
                    holder.mAddFollowButton.setVisibility(View.VISIBLE);
                    mCallback.onFollowClick(Const.APICategory.UNSET_FOLLOW, data.getUser_id());
                } else {
                    holder.mDeleteFollowButton.setVisibility(View.VISIBLE);
                    holder.mAddFollowButton.setVisibility(View.INVISIBLE);
                    mCallback.onFollowClick(Const.APICategory.SET_FOLLOW, data.getUser_id());
                }
            }
        });
    }

    private void bindWant(final Const.WantViewHolder holder, final ListGetData data) {
        holder.mRestName.setText(data.getRestname());
        holder.mLocality.setText(data.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

        holder.mRestName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRestClick(data.getRest_id(), data.getRestname());
            }
        });

        holder.mWantPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRestClick(data.getRest_id(), data.getRestname());
            }
        });

        holder.mDeleteWantButton.setVisibility(View.VISIBLE);
        holder.mWantRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mDeleteWantButton.isShown()) {
                    holder.mDeleteWantButton.setVisibility(View.INVISIBLE);
                    holder.mAddWantButton.setVisibility(View.VISIBLE);
                    API3PostUtil.setWantAsync(mContext, data.getRest_id());
                } else {
                    holder.mDeleteWantButton.setVisibility(View.VISIBLE);
                    holder.mAddWantButton.setVisibility(View.INVISIBLE);
                    API3PostUtil.unsetWantAsync(mContext, data.getRest_id());
                }
            }
        });
    }

    private void bindUserCheer(Const.UserCheerViewHolder holder, final ListGetData data) {
        holder.mRestName.setText(data.getRestname());
        holder.mLocality.setText(data.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

        holder.mRestName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRestClick(data.getRest_id(), data.getRestname());
            }
        });

        holder.mCheerPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRestClick(data.getRest_id(), data.getRestname());
            }
        });
    }

    private void bindRestCheer(final Const.RestCheerViewHolder holder, final ListGetData data) {
        holder.mUserName.setText(data.getUsername());

        Picasso.with(mContext)
                .load(data.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mTenpoCheerPicture);

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        holder.mTenpoCheerPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        if (data.getFollow_flag() == 0) {
            holder.mAddFollowButton.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteFollowButton.setVisibility(View.VISIBLE);
        }
        holder.mAccountRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mAddFollowButton.isShown()) {
                    holder.mAddFollowButton.setVisibility(View.INVISIBLE);
                    holder.mDeleteFollowButton.setVisibility(View.VISIBLE);
                    mCallback.onFollowClick(Const.APICategory.SET_FOLLOW, data.getUser_id());
                } else {
                    holder.mAddFollowButton.setVisibility(View.VISIBLE);
                    holder.mDeleteFollowButton.setVisibility(View.INVISIBLE);
                    mCallback.onFollowClick(Const.APICategory.UNSET_FOLLOW, data.getUser_id());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface ListGetCallback {
        void onUserClick(String user_id, String username);

        void onRestClick(String rest_id, String restname);

        void onFollowClick(Const.APICategory api, String user_id);
    }
}
