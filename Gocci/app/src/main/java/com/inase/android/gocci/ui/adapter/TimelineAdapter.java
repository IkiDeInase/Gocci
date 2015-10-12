package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class TimelineAdapter extends RecyclerView.Adapter<Const.TwoCellViewHolder> {

    private Context mContext;
    private int mCellSize;

    private ArrayList<PostData> mData = new ArrayList<>();

    private TimelineCallback mCallback;

    public TimelineAdapter(Context context, ArrayList<PostData> data) {
        mContext = context;
        mData = data;
        this.mCellSize = Util.getScreenWidth(mContext) / 2;
    }

    public void setTimelineCallback(TimelineCallback callback) {
        mCallback = callback;
    }

    public void setData(ArrayList<PostData> postData) {
        mData.clear();
        mData.addAll(postData);
        this.notifyDataSetChanged();
    }

    public PostData getItem(int position) {
        return mData.get(position);
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public Const.TwoCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.cell_search_grid, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
        layoutParams.height = mCellSize;
        layoutParams.width = mCellSize;
        layoutParams.setFullSpan(false);
        v.setLayoutParams(layoutParams);
        return new Const.TwoCellViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Const.TwoCellViewHolder holder, final int position) {
        final PostData user = mData.get(position);

        Picasso.with(mContext)
                .load(user.getThumbnail())
                .resize(mCellSize, mCellSize)
                .centerCrop()
                .into(holder.mSquareImage);

        holder.mSquareImage.setVisibility(View.VISIBLE);

        holder.mRestname.setText(user.getRestname());
        holder.mDistance.setText(getDist(user.getDistance()));

        holder.mAspectFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onVideoFrameClick(user);
            }
        });

        holder.mAspectFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onVideoFrameLongClick(user.getPost_id());
                return false;
            }
        });

        mCallback.onHashHolder(holder, user.getPost_id());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String getDist(int distance) {
        String dist = null;
        if (distance > 1000) {
            dist = distance / 1000 + "km";
        } else {
            dist = distance + "m";
        }
        return dist;
    }

    public interface TimelineCallback {

        void onVideoFrameClick(PostData data);

        void onVideoFrameLongClick(String post_id);

        void onHashHolder(Const.TwoCellViewHolder holder, String post_id);
    }
}
