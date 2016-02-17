package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/02/17.
 */
public class LifelogAdapter extends RecyclerView.Adapter<Const.StreamUserViewHolder> {

    private Context mContext;

    private ArrayList<PostData> mData = new ArrayList<>();

    private LifelogCallback mCallback;

    public LifelogAdapter(Context context, ArrayList<PostData> data) {
        mContext = context;
        mData = data;
    }

    public void setLifelogCallback(LifelogCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public PostData getItem(int position) {
        return mData.get(position);
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public Const.StreamUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_stream_user_list, parent, false);
        return new Const.StreamUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Const.StreamUserViewHolder holder, final int position) {
        final PostData user = mData.get(position);
        holder.mName.setText(user.getRestname());
        holder.mLocality.setText(user.getLocality());

        holder.mTimeText.setText(user.getPost_date());

        if (!user.getMemo().equals("none")) {
            holder.mComment.setText(user.getMemo());
        } else {
            holder.mComment.setText("");
        }

        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onStreamRestClick(user.getPost_rest_id(), user.getRestname());
            }
        });

        holder.mCircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onStreamRestClick(user.getPost_rest_id(), user.getRestname());
            }
        });

        holder.mMenuRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_mypage).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.delete:
                                mCallback.onStreamDeleteClick(user.getPost_id());
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
                mCallback.onStreamVideoFrameClick(user);
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
                mCallback.onStreamCommentClick(user.getPost_id());
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
                                    mCallback.onTwitterShare(user.getMovie(), user.getRestname());
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

        mCallback.onStreamHashHolder(holder, user.getPost_id());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface LifelogCallback {

        void onStreamRestClick(String rest_id, String rest_name);

        void onStreamCommentClick(String post_id);

        void onStreamVideoFrameClick(PostData data);

        void onStreamDeleteClick(String post_id);

        void onGochiTap();

        void onGochiClick(String post_id, Const.APICategory apiCategory);

        void onFacebookShare(String share, String rest_name);

        void onTwitterShare(String share, String rest_name);

        void onInstaShare(String share, String rest_name);

        void onStreamHashHolder(Const.StreamUserViewHolder holder, String post_id);
    }
}