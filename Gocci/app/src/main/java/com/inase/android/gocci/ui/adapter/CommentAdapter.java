package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inase.android.gocci.R;
import com.inase.android.gocci.domain.model.CommentUserData;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<HeaderData> mCommentData = new ArrayList<>();

    private String mPost_id;

    private CommentCallback mCallback;

    public void setCommentCallback(CommentCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public CommentAdapter(Context context, String post_id, ArrayList<HeaderData> commentData) {
        this.mContext = context;
        this.mPost_id = post_id;
        this.mCommentData = commentData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HeaderData users = mCommentData.get(position);
        bindComment((CommentViewHolder) holder, users);
    }

    private void bindComment(final CommentViewHolder holder, final HeaderData users) {
        holder.mReUser.removeAllViews();

        Picasso.with(mContext)
                .load(users.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCommentUserImage);
        holder.mUserName.setText(users.getUsername());
        holder.mDateTime.setText(users.getComment_date());

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

//        holder.mReplyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context, Arrays.asList(users.getComment_user_data()).toString(), Toast.LENGTH_SHORT).show();
//                final StringBuilder user_name = new StringBuilder();
//                final StringBuilder user_id = new StringBuilder();
//                user_name.append("@" + users.getUsername() + " ");
//                user_id.append(users.getComment_user_id());
//                for (CommentUserData data : users.getComment_user_data()) {
//                    user_name.append("@" + data.getUserName() + " ");
//                    user_id.append("," + data.getUser_id());
//                }
//                new MaterialDialog.Builder(mContext)
//                        .title(mContext.getString(R.string.comment))
//                        .titleColorRes(R.color.namegrey)
//                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
//                        .inputMaxLength(140)
//                        .input(null, user_name.toString(), false, new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(MaterialDialog dialog, CharSequence input) {
//                                // Do something
//                                String comment = input.toString().replace(user_name.toString(), "");
//                                mCallback.onCommentPostClick(Const.getPostCommentWithNoticeAPI(mPost_id, comment, user_id.toString()));
//                            }
//                        })
//                        .widgetColorRes(R.color.gocci_header)
//                        .positiveText(mContext.getString(R.string.post_comment))
//                        .positiveColorRes(R.color.gocci_header)
//                        .show();
//            }
//        });

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

        TextView comment = new TextView(mContext);
        comment.setText(users.getComment());
        comment.setTextColor(mContext.getResources().getColor(R.color.nameblack));
        comment.setTextSize(12);
        comment.setPadding(8, 0, 0, 0);
        holder.mReUser.addView(comment, LinearLayout.LayoutParams.WRAP_CONTENT);

        holder.mCommentCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder user_name = new StringBuilder();
                final StringBuilder user_id = new StringBuilder();
                user_name.append("@" + users.getUsername() + " ");
                user_id.append(users.getComment_user_id());
                for (CommentUserData data : users.getComment_user_data()) {
                    user_name.append("@" + data.getUserName() + " ");
                    user_id.append("," + data.getUser_id());
                }
                mCallback.onCommentClick(user_name.toString(), user_id.toString());
            }
        });

        holder.mCommentCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onCommentLongClick(String.valueOf(users.getComment_user_id()));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentData.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.comment_cell)
        RelativeLayout mCommentCell;
        @Bind(R.id.comment_user_image)
        ImageView mCommentUserImage;
        @Bind(R.id.user_name)
        TextView mUserName;
        @Bind(R.id.date_time)
        TextView mDateTime;
        @Bind(R.id.horizon_bar)
        LinearLayout mReUser;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface CommentCallback {
        void onUserClick(String user_id, String user_name);

        void onCommentClick(String username, String user_id);

        void onCommentLongClick(String user_id);

    }
}
