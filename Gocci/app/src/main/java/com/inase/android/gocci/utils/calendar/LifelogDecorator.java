package com.inase.android.gocci.utils.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.maps.android.ui.IconGenerator;
import com.inase.android.gocci.R;
import com.inase.android.gocci.utils.map.MultiDrawable;
import com.squareup.picasso.Picasso;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by kinagafuji on 16/02/10.
 */
public class LifelogDecorator implements CalendarCellDecorator {

    private final IconGenerator mClusterIconGenerator;
    private final ImageView mImageView;
    private final int mDimension;
    private Context mContext;
    private HashMap<String, ArrayList<String>> mThumbnailMap;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public LifelogDecorator(Context context, HashMap<String, ArrayList<String>> thumbnailMap) {
        this.mContext = context;
        this.mThumbnailMap = thumbnailMap;

        mClusterIconGenerator = new IconGenerator(context);
        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.photolog_size);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) context.getResources().getDimension(R.dimen.photolog_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mClusterIconGenerator.setContentView(mImageView);
    }

    @Override
    public void decorate(final CalendarCellView cellView, Date date) {
        final String post_date = dateTimeFormat.format(date.getTime());
        if (mThumbnailMap.containsKey(post_date)) {
            final ArrayList<String> thumbnailMap = mThumbnailMap.get(post_date);
            final List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, thumbnailMap.size()));
            final int width = mDimension;
            final int height = mDimension;

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    for (String thumbnail : thumbnailMap) {
                        if (profilePhotos.size() == 4) break;
                        try {
                            Drawable drawable = new BitmapDrawable(mContext.getResources(), Picasso.with(mContext).load(thumbnail).get());
                            drawable.setBounds(0, 0, width, height);
                            profilePhotos.add(drawable);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mThumbnailMap.remove(post_date);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (profilePhotos.size() != 0) {
                        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
                        multiDrawable.setBounds(0, 0, width, height);

                        mImageView.setImageDrawable(multiDrawable);
                        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(thumbnailMap.size()));
                        cellView.setBackground(new BitmapDrawable(mContext.getResources(), icon));
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
