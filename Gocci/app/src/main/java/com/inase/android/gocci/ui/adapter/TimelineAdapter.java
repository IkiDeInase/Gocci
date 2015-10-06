package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.domain.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class TimelineAdapter extends RecyclerView.Adapter<Const.ExoViewHolder> {

    private Context mContext;
    private ArrayList<PostData> mData = new ArrayList<>();

    private TimelineCallback mCallback;

    public TimelineAdapter(Context context, ArrayList<PostData> data) {
        mContext = context;
        mData = data;
    }

    public void setTimelineCallback(TimelineCallback callback) {
        mCallback = callback;
    }

    public PostData getItem(int position) {
        return mData.get(position);
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public Const.ExoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.cell_exo_timeline, parent, false);
        return new Const.ExoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Const.ExoViewHolder holder, final int position) {
        final PostData user = mData.get(position);

        holder.mUserName.setText(user.getUsername());

        holder.mTimeText.setText(user.getPost_date());

        if (!user.getMemo().equals("none")) {
            holder.mComment.setText(user.getMemo());
        } else {
            holder.mComment.setText("");
        }

        holder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCommentClick(user.getPost_id());
            }
        });

        Picasso.with(mContext)
                .load(user.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCircleImage);

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
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
                                Util.setViolateDialog(mContext, user.getPost_id());
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

        holder.mRestname.setText(user.getRestname());
        //viewHolder.locality.setText(user.getLocality());

        if (!user.getCategory().equals(mContext.getString(R.string.nothing_tag))) {
            holder.mCategory.setText(user.getCategory());
        } else {
            holder.mCategory.setText("　　　　");
        }
        if (!user.getTag().equals(mContext.getString(R.string.nothing_tag))) {
            holder.mMood.setText(user.getTag());
        } else {
            holder.mMood.setText("　　　　");
        }
        if (!user.getValue().equals("0")) {
            holder.mValue.setText(user.getValue() + "円");
        } else {
            holder.mValue.setText("　　　　");
        }

        //リップルエフェクトを見せてからIntentを飛ばす
        holder.mTenpoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onRestClick(user.getPost_rest_id(), user.getRestname());
            }
        });

        final int currentgoodnum = user.getGochi_num();
        final int currentcommentnum = user.getComment_num();

        holder.mLikesNumber.setText(String.valueOf(currentgoodnum));
        holder.mCommentsNumber.setText(String.valueOf(currentcommentnum));

        if (user.getGochi_flag() == 0) {
            holder.mLikesRipple.setClickable(true);
            holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef);

            holder.mLikesRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setGochi_flag(1);
                    user.setGochi_num(currentgoodnum + 1);

                    holder.mLikesNumber.setText(String.valueOf((currentgoodnum + 1)));
                    holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                    holder.mLikesRipple.setClickable(false);

                    Util.postGochiAsync(mContext, user);
                }
            });
        } else {
            holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
            holder.mLikesRipple.setClickable(false);
        }

        holder.mCommentsRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mCallback.onCommentClick(user.getPost_id());
            }
        });

        holder.mShareRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Application_Gocci.getTransfer(mContext) != null) {
                    new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.facebook_share:
                                    Toast.makeText(mContext, mContext.getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    mCallback.onFacebookShare(user.getShare());
                                    break;
                                case R.id.twitter_share:
                                    mCallback.onTwitterShare(holder.mVideoThumbnail, user.getRestname());
                                    break;
                                case R.id.other_share:
                                    Toast.makeText(mContext, mContext.getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    mCallback.onInstaShare(user.getShare(), user.getRestname());
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
        return mData.size();
    }

    public interface TimelineCallback {
        void onUserClick(int user_id, String user_name);

        void onRestClick(int rest_id, String rest_name);

        void onCommentClick(String post_id);

        void onVideoFrameClick();

        void onFacebookShare(String share);

        void onTwitterShare(SquareImageView view, String rest_name);

        void onInstaShare(String share, String rest_name);

        void onHashHolder(Const.ExoViewHolder holder, String post_id);
    }
}
