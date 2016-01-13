package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocosw.bottomsheet.BottomSheet;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class TimelineAdapter extends RecyclerView.Adapter<Const.TwoCellViewHolder> {

    private Context mContext;
    private int mCellSize;

    private Const.TimelineCategory mCategory;

    private ArrayList<TwoCellData> mData = new ArrayList<>();

    private TimelineCallback mCallback;

    public TimelineAdapter(Context context, Const.TimelineCategory category, ArrayList<TwoCellData> data) {
        mContext = context;
        mData = data;
        mCategory = category;
        this.mCellSize = Util.getScreenWidth(mContext) / 2;
    }

    public void setTimelineCallback(TimelineCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public TwoCellData getItem(int position) {
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
        final TwoCellData user = mData.get(position);

        Picasso.with(mContext)
                .load(user.getThumbnail())
                .resize(mCellSize, mCellSize)
                .centerCrop()
                .into(holder.mSquareImage);

        Picasso.with(mContext)
                .load(user.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCircleImage);

        holder.mOverlay.setMinimumHeight(mCellSize / 3);

        holder.mSquareImage.setVisibility(View.VISIBLE);

        holder.mRestname.setText(user.getRestname());

        if (mCategory == Const.TimelineCategory.NEARLINE) {
            holder.mDistance.setText(getDist(user.getDistance()));
        } else {
            holder.mDistance.setText(user.getPost_date());
            holder.mDistance.setTextSize(12);
        }

        holder.mOtherAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_cell_timeline).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.move_to_userpage:
                                mCallback.onUserClick(user.getPost_user_id(), user.getUsername());
                                break;
                            case R.id.move_to_restpage:
                                mCallback.onRestClick(user.getPost_rest_id(), user.getRestname());
                                break;
                            case R.id.move_to_comment:
                                mCallback.onCommentClick(user.getPost_id());
                                break;
                            case R.id.violation:
                                Util.setBlockDialog(mContext, user.getPost_id());
                                break;
                            case R.id.close:
                                dialog.dismiss();
                        }
                    }
                }).show();
            }
        });

        holder.mAspectFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onVideoFrameClick(user);
            }
        });

        if (user.getGochi_flag() == 0) {
            holder.mGochiImage.setImageResource(R.drawable.ic_icon_beef);

            holder.mGochiAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getGochi_flag() == 0) {
                        mCallback.onGochiTap();
                        mCallback.onGochiClick(user.getPost_id());
                        user.setGochi_flag(1);
                        holder.mGochiImage.setImageResource(R.drawable.ic_icon_beef_orange);
                    }
                }
            });
        } else {
            holder.mGochiImage.setImageResource(R.drawable.ic_icon_beef_orange);
            holder.mGochiAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCallback.onGochiTap();
                }
            });
        }
        mCallback.onHashHolder(holder, user.getPost_id());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewRecycled(Const.TwoCellViewHolder holder) {
        mCallback.onViewRecycled(holder);
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

        void onUserClick(String user_id, String user_name);

        void onRestClick(String rest_id, String rest_name);

        void onCommentClick(String post_id);

        void onGochiTap();

        void onGochiClick(String post_id);

        void onViewRecycled(Const.TwoCellViewHolder holder);

        void onVideoFrameClick(TwoCellData data);

        void onHashHolder(Const.TwoCellViewHolder holder, String post_id);
    }
}
