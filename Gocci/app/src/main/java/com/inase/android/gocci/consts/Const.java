package com.inase.android.gocci.consts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.regions.Regions;
import com.andexert.library.RippleView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.inase.android.gocci.R;
import com.inase.android.gocci.ui.view.SquareExoVideoView;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 定数定義クラス
 * Created by kmaeda on 2015/01/22.
 */
public class Const {

    //AWS SN　ログイン　エンドポイント
    public static final String ENDPOINT_FACEBOOK = "graph.facebook.com";
    public static final String ENDPOINT_TWITTER = "api.twitter.com";
    public static final String ENDPOINT_INASE = "test.login.gocci";
//    public static final String ENDPOINT_INASE = "login.gocci";

    public static final String IDENTITY_POOL_ID = "us-east-1:b563cebf-1de2-4931-9f08-da7b4725ae35";
//    public static final String IDENTITY_POOL_ID = "us-east-1:b0252276-27e1-4069-be84-3383d4b3f897";

    public static final String ANALYTICS_ID = "9cc17c8116ae40738002f10d907bbd16";

    public static final Regions REGION = Regions.US_EAST_1;

    public static final String OS = "android";

    public static final String POST_MOVIE_BUCKET_NAME = "gocci.movies.bucket.jp-test";
    public static final String GET_MOVIE_BUCKET_NAME = "gocci.movies.provider.jp-test";
    public static final String POST_PHOTO_BUCKET_NAME = "gocci.imgs.provider.jp-test";
//    public static final String POST_MOVIE_BUCKET_NAME = "gocci.movies.bucket.jp";
//    public static final String GET_MOVIE_BUCKET_NAME = "gocci.movies.provider.jp";
//    public static final String POST_PHOTO_BUCKET_NAME = "gocci.imgs.provider.jp";

    public enum TimelineCategory {
        NEARLINE, FOLLOWLINE, TIMELINE, GOCHILINE
    }

    public enum PostCallback {
        SUCCESS, LOCALERROR, GLOBALERROR
    }

    public enum ActivityCategory {
        SPLASH, TUTORIAL, TIMELINE,
        MY_PAGE, USER_PAGE, REST_PAGE,
        COMMENT_PAGE, LIST, SETTING,
        CAMERA, CAMERA_PREVIEW,
        CAMERA_PREVIEW_ALREADY,
        MAP_SEARCH, MAP_PROFILE,
        WEBVIEW_LICENSE, WEBVIEW_POLICY,
        LOGIN_SESSION, POST_PAGE, USER_SEARCH
    }

    public enum ListCategory {
        FOLLOW,
        FOLLOWER,
        USER_CHEER,
    }

    public enum APICategory {
        AUTH_LOGIN,
        AUTH_SIGNUP,
        AUTH_FACEBOOK_LOGIN,
        AUTH_TWITTER_LOGIN,
        AUTH_PASS_LOGIN,
        GET_TIMELINE_FIRST,
        GET_TIMELINE_REFRESH,
        GET_TIMELINE_ADD,
        GET_TIMELINE_FILTER,
        GET_NEARLINE_FIRST,
        GET_NEARLINE_REFRESH,
        GET_NEARLINE_ADD,
        GET_NEARLINE_FILTER,
        GET_FOLLOWLINE_FIRST,
        GET_FOLLOWLINE_REFRESH,
        GET_FOLLOWLINE_ADD,
        GET_FOLLOWLINE_FILTER,
        GET_GOCHILINE_FIRST,
        GET_GOCHILINE_REFRESH,
        GET_GOCHILINE_ADD,
        GET_GOCHILINE_FILTER,
        GET_USER_FIRST,
        GET_USER_REFRESH,
        GET_REST_FIRST,
        GET_REST_REFRESH,
        GET_COMMENT_FIRST,
        GET_COMMENT_REFRESH,
        GET_FOLLOW_FIRST,
        GET_FOLLOW_REFRESH,
        GET_FOLLOWER_FIRST,
        GET_FOLLOWER_REFRESH,
        GET_USER_CHEER_FIRST,
        GET_USER_CHEER_REFRESH,
        GET_NOTICE_FIRST,
        GET_HEATMAP_FIRST,
        GET_NEAR_FIRST,
        GET_USERNAME,
        SET_FACEBOOK_LINK,
        SET_TWITTER_LINK,
        UNSET_FACEBOOK_LINK,
        UNSET_TWITTER_LINK,
        SET_GOCHI,
        UNSET_GOCHI,
        SET_POST_BLOCK,
        SET_COMMENT_BLOCK,
        SET_FOLLOW,
        UNSET_FOLLOW,
        SET_FEEDBACK,
        SET_PASSWORD,
        SET_COMMENT,
        SET_COMMENT_EDIT,
        SET_MEMO_EDIT,
        UNSET_COMMENT,
        SET_USERNAME,
        SET_PROFILEIMG,
        SET_RESTADD,
        SET_POST,
        GET_POST,
        UNSET_POST,
        SET_DEVICE,
        UNSET_DEVICE,
        GET_FOLLOWER_RANK_FIRST,
        GET_FOLLOWER_RANK_ADD,
    }

    public static final class TwoCellViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.video_thumbnail)
        public SquareImageView mSquareImage;
        @Bind(R.id.square_video_exo)
        public SquareExoVideoView mSquareExoVideo;
        @Bind(R.id.video_frame)
        public AspectRatioFrameLayout mAspectFrame;
        @Bind(R.id.progress)
        public DilatingDotsProgressBar mProgress;
        @Bind(R.id.overlay_footer)
        public ImageView mOverlay;
        @Bind(R.id.circle_image)
        public ImageView mCircleImage;
        @Bind(R.id.restname)
        public TextView mRestname;
        @Bind(R.id.distance)
        public TextView mDistance;
        @Bind(R.id.gochi_action)
        public RippleView mGochiAction;
        @Bind(R.id.other_action)
        public RippleView mOtherAction;
        @Bind(R.id.gochi_image)
        public ImageView mGochiImage;
        @Bind(R.id.other_image)
        public ImageView mOtherImage;

        public TwoCellViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static final class StreamUserViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.circle_image)
        public ImageView mCircleImage;
        @Bind(R.id.restname)
        public TextView mName;
        @Bind(R.id.locality)
        public TextView mLocality;
        @Bind(R.id.rest_time_text)
        public TextView mTimeText;
        @Bind(R.id.menu_ripple)
        public RippleView mMenuRipple;
        @Bind(R.id.video_thumbnail)
        public SquareImageView mVideoThumbnail;
        @Bind(R.id.square_video_exo)
        public SquareExoVideoView mSquareVideoExo;
        @Bind(R.id.progress)
        public DilatingDotsProgressBar mProgress;
        @Bind(R.id.category)
        public TextView mCategory;
        @Bind(R.id.value)
        public TextView mValue;
        @Bind(R.id.comment)
        public TextView mComment;
        @Bind(R.id.likes_number)
        public TextView mLikesNumber;
        @Bind(R.id.likes_Image)
        public ImageView mLikesImage;
        @Bind(R.id.comments_number)
        public TextView mCommentsNumber;
        @Bind(R.id.likes_ripple)
        public RippleView mLikesRipple;
        @Bind(R.id.comments_ripple)
        public RippleView mCommentsRipple;
        @Bind(R.id.share_ripple)
        public RippleView mShareRipple;
        @Bind(R.id.video_frame)
        public AspectRatioFrameLayout mVideoFrame;

        public StreamUserViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static final class StreamRestViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.circle_image)
        public ImageView mCircleImage;
        @Bind(R.id.username)
        public TextView mName;
        @Bind(R.id.user_time_text)
        public TextView mTimeText;
        @Bind(R.id.menu_ripple)
        public RippleView mMenuRipple;
        @Bind(R.id.video_thumbnail)
        public SquareImageView mVideoThumbnail;
        @Bind(R.id.square_video_exo)
        public SquareExoVideoView mSquareVideoExo;
        @Bind(R.id.progress)
        public DilatingDotsProgressBar mProgress;
        @Bind(R.id.category)
        public TextView mCategory;
        @Bind(R.id.value)
        public TextView mValue;
        @Bind(R.id.comment)
        public TextView mComment;
        @Bind(R.id.likes_number)
        public TextView mLikesNumber;
        @Bind(R.id.likes_Image)
        public ImageView mLikesImage;
        @Bind(R.id.comments_number)
        public TextView mCommentsNumber;
        @Bind(R.id.likes_ripple)
        public RippleView mLikesRipple;
        @Bind(R.id.comments_ripple)
        public RippleView mCommentsRipple;
        @Bind(R.id.share_ripple)
        public RippleView mShareRipple;
        @Bind(R.id.video_frame)
        public AspectRatioFrameLayout mVideoFrame;

        public StreamRestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class FollowFollowerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.follow_follower_picture)
        public ImageView mFollowFollowerPicture;
        @Bind(R.id.user_name)
        public TextView mUserName;
        @Bind(R.id.add_follow_button)
        public ImageView mAddFollowButton;
        @Bind(R.id.delete_follow_button)
        public ImageView mDeleteFollowButton;
        @Bind(R.id.account_button)
        public RippleView mAccountRipple;
        @Bind(R.id.gochi_count_text)
        public TextView mGochiCount;

        public FollowFollowerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class UserCheerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cheer_picture)
        public ImageView mCheerPicture;
        @Bind(R.id.rest_name)
        public TextView mRestName;
        @Bind(R.id.locality)
        public TextView mLocality;

        public UserCheerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class NoticeHolder {
        @Bind(R.id.circle_image)
        public ImageView mCircleImage;
        @Bind(R.id.notice_username)
        public TextView mNoticeUsername;
        @Bind(R.id.notice_sub_text)
        public TextView mNoticeSubText;
        @Bind(R.id.date_time)
        public TextView mDateTime;

        public NoticeHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
