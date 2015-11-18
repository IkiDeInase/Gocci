package com.inase.android.gocci.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class NoticeAdapter extends ArrayAdapter<HeaderData> {

    private LayoutInflater layoutInflater;

    private Const.NoticeHolder holder;

    private NoticeCallback mCallback;

    public void setNoticeCallback(NoticeCallback callback) {
        mCallback = callback;
    }

    public void setData() {
        this.notifyDataSetChanged();
    }

    public NoticeAdapter(Context context, int viewResourceId, ArrayList<HeaderData> list) {
        super(context, viewResourceId, list);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.cell_notification_list, null);
            holder = new Const.NoticeHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Const.NoticeHolder) convertView.getTag();
        }

        final HeaderData data = this.getItem(position);

        holder.mNoticeUsername.setText(data.getUsername());
        holder.mDateTime.setText(data.getNotice_date());

        Picasso.with(getContext())
                .load(data.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(holder.mCircleImage);

        //コメントのViewをクリックすると、その人のプロフィールに飛ぶ
        holder.mNoticeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        holder.mCircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onUserClick(data.getUser_id(), data.getUsername());
            }
        });

        switch (data.getNotice()) {
            case "like":
                holder.mNoticeSubText.setText(getContext().getString(R.string.notice_from_gochi));
                break;
            case "follow":
                holder.mNoticeSubText.setText(getContext().getString(R.string.notice_from_follow));
                break;
            case "comment":
                holder.mNoticeSubText.setText(getContext().getString(R.string.notice_from_comment));
                break;
        }
        return convertView;
    }

    public interface NoticeCallback {
        void onUserClick(String user_id, String username);
    }
}
