package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.CommentUserData;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COMMENT_HEADER = 0;
    private static final int TYPE_COMMENT = 1;

    private Context mContext;
    private PostData mPostData;
    private ArrayList<HeaderData> mCommentData = new ArrayList<>();

    private String mPost_id;

    private CommentCallback mCallback;

    public void setCommentCallback(CommentCallback callback) {
        mCallback = callback;
    }

    public void setData(PostData postData) {
        mPostData = postData;
        this.notifyDataSetChanged();
    }

    public CommentAdapter(Context context, String post_id, PostData postData, ArrayList<HeaderData> commentData) {
        this.mContext = context;
        this.mPost_id = post_id;
        this.mPostData = postData;
        this.mCommentData = commentData;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_COMMENT_HEADER;
        } else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_COMMENT_HEADER == viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment_header, parent, false);
            return new Const.ExoViewHolder(view);
        } else {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment_activity, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_COMMENT_HEADER == viewType) {
            bindHeader((Const.ExoViewHolder) holder, mPostData);
        } else {
            HeaderData users = mCommentData.get(position - 1);
            bindComment((CommentViewHolder) holder, users);
        }
    }

    private void bindHeader(final Const.ExoViewHolder holder, final PostData user) {
        holder.mUserName.setText(user.getUsername());
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
                //なんかするか？
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

    private void bindComment(final CommentViewHolder holder, final HeaderData users) {
        Picasso.with(mContext)
                .load(users.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCommentUserImage);
        holder.mUserName.setText(users.getUsername());
        holder.mDateTime.setText(users.getComment_date());
        holder.mUserComment.setText(users.getComment());

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(users.getComment_user_id(), users.getUsername());
            }
        });

        holder.mCommentUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(users.getComment_user_id(), users.getUsername());
            }
        });

        holder.mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, Arrays.asList(users.getComment_user_data()).toString(), Toast.LENGTH_SHORT).show();
                final StringBuilder user_name = new StringBuilder();
                final StringBuilder user_id = new StringBuilder();
                user_name.append("@" + users.getUsername() + " ");
                user_id.append(users.getComment_user_id());
                for (CommentUserData data : users.getComment_user_data()) {
                    user_name.append("@" + data.getUserName() + " ");
                    user_id.append("," + data.getUser_id());
                }
                new MaterialDialog.Builder(mContext)
                        .title(mContext.getString(R.string.comment))
                        .titleColorRes(R.color.namegrey)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
                        .inputMaxLength(140)
                        .input(null, user_name.toString(), false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                String comment = input.toString().replace(user_name.toString(), "");
                                mCallback.onCommentPostClick(Const.getPostCommentWithNoticeAPI(mPost_id, comment, user_id.toString()));
                            }
                        })
                        .widgetColorRes(R.color.gocci_header)
                        .positiveText(mContext.getString(R.string.post_comment))
                        .positiveColorRes(R.color.gocci_header)
                        .show();
            }
        });

        if (!users.getComment_user_data().isEmpty()) {
            for (final CommentUserData data : users.getComment_user_data()) {
                TextView userText = new TextView(mContext);
                userText.setText(" @" + data.getUserName());
                userText.setSingleLine();
                userText.setTextSize(12);
                userText.setTextColor(mContext.getResources().getColor(R.color.gocci_header));
                userText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onUserClick(data.getUser_id(), data.getUserName());
                    }
                });
                holder.mReUser.addView(userText, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCommentData.size() + 1;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.comment_user_image)
        ImageView mCommentUserImage;
        @Bind(R.id.user_name)
        TextView mUserName;
        @Bind(R.id.date_time)
        TextView mDateTime;
        @Bind(R.id.user_comment)
        TextView mUserComment;
        @Bind(R.id.re_user)
        LinearLayout mReUser;
        @Bind(R.id.reply_button)
        ImageButton mReplyButton;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface CommentCallback {
        void onUserClick(int user_id, String user_name);

        void onRestClick(int rest_id, String rest_name);

        void onCommentPostClick(String postUrl);

        void onVideoFrameClick();

        void onFacebookShare(String share);

        void onTwitterShare(SquareImageView view, String rest_name);

        void onInstaShare(String share, String rest_name);

        void onHashHolder(Const.ExoViewHolder holder, String post_id);
    }
}
