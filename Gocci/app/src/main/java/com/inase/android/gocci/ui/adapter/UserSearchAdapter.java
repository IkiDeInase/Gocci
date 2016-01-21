package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.SearchUserData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/01/21.
 */
public class UserSearchAdapter extends RecyclerView.Adapter<Const.FollowFollowerViewHolder> {

    private Context mContext;
    private ArrayList<SearchUserData> mList = new ArrayList<>();

    private SearchUserCallback mCallback;

    public void setSearchUserCallback(SearchUserCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public UserSearchAdapter(Context context, ArrayList<SearchUserData> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public Const.FollowFollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cell_follow_follower, parent, false);
        return new Const.FollowFollowerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Const.FollowFollowerViewHolder holder, final int position) {
        final SearchUserData data = mList.get(position);
        holder.mUserName.setText(data.getUsername());
        holder.mGochiCount.setText(String.valueOf(data.getGochi_num()));

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

        if (!data.isFollow_flag()) {
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

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface SearchUserCallback {
        void onUserClick(String user_id, String username);

        void onFollowClick(Const.APICategory apiCategory, String user_id);
    }
}
