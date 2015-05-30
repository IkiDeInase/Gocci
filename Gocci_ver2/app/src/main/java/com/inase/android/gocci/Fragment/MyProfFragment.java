package com.inase.android.gocci.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Activity.CameraActivity;
import com.inase.android.gocci.Activity.FlexibleTenpoActivity;
import com.inase.android.gocci.Base.BaseFragment;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CommentView;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

public class MyProfFragment extends BaseFragment implements ObservableScrollViewCallbacks, AbsListView.OnScrollListener, CacheManager.ICacheManagerListener {

    private String mProfUrl;
    private String mEncodeUser_name;

    private String clickedRestname;
    private String clickedLocality;
    private double clickedLat;
    private double clickedLon;
    private String clickedPhoneNumber;
    private String clickedHomepage;
    private String clickedCategory;
    private int clickedWant_flag;
    private int clickedTotal_cheer_num;

    private ProgressWheel myprofprogress;
    private ObservableListView mProfListView;
    private ArrayList<UserData> mProfusers = new ArrayList<>();
    private MyProfAdapter mProfAdapter;
    private SwipyRefreshLayout mProfSwipe;

    private FloatingActionButton fab;

    private ImageView mEmptyView;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private AsyncHttpClient httpClient3;
    private AsyncHttpClient httpClient4;
    private RequestParams goodParam;
    private RequestParams deleteParam;

    private AttributeSet mVideoAttr;

    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<ViewHolder, String> mViewHolderHash;  // Value: PosterId

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private ImageView edit_background;
    private ImageView edit_picture;
    private TextView edit_username;
    private EditText edit_username_edit;

    private boolean isBackground = false;
    private boolean isPicture = false;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e("DEBUG", "onGlobalLayout called: " + mPlayingPostId);
            changeMovie();
            Log.e("DEBUG", "onGlobalLayout  changeMovie called: " + mPlayingPostId);
            if (mPlayingPostId != null) {
                mProfListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheManager = CacheManager.getInstance(getActivity().getApplicationContext());
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(MyProfFragment.this);

        Fabric.with(getActivity(), new TweetComposer());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_myprof,
                container, false);

        try {
            mEncodeUser_name = URLEncoder.encode(SavedData.getServerName(getActivity()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        EventDateRecorder profilerecorder = EventDateRecorder.load(getActivity(), "use_first_myprofile");
        if (!profilerecorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
            Effectstype effect = Effectstype.SlideBottom;
            dialogBuilder
                    .withTitle("プロフィール画面")
                    .withMessage("自分のプロフィール画面です")
                    .withDuration(500)
                    .withEffect(effect)
                    .isCancelableOnTouchOutside(true)
                    .show();

            profilerecorder.record();
        }

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        mProfUrl = "http://api-gocci.jp/mypage/?user_name=" + mEncodeUser_name;

        fab = (FloatingActionButton) view.findViewById(R.id.toukouButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        mEmptyView = (ImageView) view.findViewById(R.id.myprof_emptyView);
        myprofprogress = (ProgressWheel) view.findViewById(R.id.myprofprogress_wheel);
        mProfListView = (ObservableListView) view.findViewById(R.id.list);
        mProfListView.setOnScrollListener(this);
        mProfListView.setScrollViewCallbacks(this);
        mProfListView.setDivider(null);
        // スクロールバーを表示しない
        mProfListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mProfListView.setSelector(android.R.color.transparent);

        mProfAdapter = new MyProfAdapter(getActivity(), 0, mProfusers);

        mProfListView.addHeaderView(inflater.inflate(R.layout.view_header_myprof, null));

        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        final TextView myprof_username = (TextView) view.findViewById(R.id.myprof_username);
        final ImageView myprof_picture = (ImageView) view.findViewById(R.id.myprof_picture);
        final ImageView myprof_background = (ImageView) view.findViewById(R.id.myprof_background);
        RippleView editRipple = (RippleView) view.findViewById(R.id.editProfile);
        myprof_username.setText(SavedData.getServerName(getActivity()));
        Picasso.with(getActivity())
                .load(SavedData.getServerPicture(getActivity()))
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(myprof_picture);

        Picasso.with(getActivity())
                .load(SavedData.getServerBackground(getActivity()))
                .fit()
                .centerCrop()
                .into(myprof_background);

        editRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.afollestad.materialdialogs.MaterialDialog dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity())
                        .title("変えたい箇所を押してみよう")
                        .customView(R.layout.view_header_myprof_edit, false)
                        .positiveText("完了")
                        .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                                super.onPositive(dialog);

                                final RequestParams params = new RequestParams();

                                if (isBackground && isPicture) {
                                    //どっちも変更した
                                    try {
                                        params.put("user_name", edit_username.getText().toString());
                                        params.put("background", new File(getLocalBitmapUri(edit_background).getPath()));
                                        params.put("picture", new File(getLocalBitmapUri(edit_picture).getPath()));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    isBackground = false;
                                    isPicture = false;
                                } else if (isBackground) {
                                    //背景だけ変更
                                    try {
                                        params.put("user_name", edit_username.getText().toString());
                                        params.put("background", new File(getLocalBitmapUri(edit_background).getPath()));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    isBackground = false;
                                } else if (isPicture) {
                                    //写真だけ
                                    try {
                                        params.put("user_name", edit_username.getText().toString());
                                        params.put("picture", new File(getLocalBitmapUri(edit_picture).getPath()));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    isPicture = false;
                                } else {
                                    //どっちも変更なし
                                    params.put("user_name", edit_username.getText().toString());
                                }

                                final AsyncHttpClient client = new AsyncHttpClient();
                                client.setCookieStore(SavedData.getCookieStore(getActivity()));
                                client.post(getActivity(), Const.URL_POST_PROFILE_EDIT_API, params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        myprofprogress.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Toast.makeText(getActivity(), "プロフィール変更に失敗しました", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            String message = response.getString("message");

                                            if (message.equals("変更しました")) {
                                                Toast.makeText(getActivity(), "プロフィールを変更しました", Toast.LENGTH_SHORT).show();
                                                String background_image = response.getString("background_image");
                                                String name = response.getString("user_name");
                                                String picture = response.getString("picture");

                                                SavedData.changeProfile(getActivity(), name, picture, background_image);

                                                Intent intent = getActivity().getIntent();
                                                getActivity().overridePendingTransition(0, 0);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                getActivity().finish();

                                                getActivity().overridePendingTransition(0, 0);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(getActivity(), "プロフィール変更に失敗しました", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        myprofprogress.setVisibility(View.GONE);
                                    }
                                });
                            }
                        })
                        .build();

                edit_background = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_background);
                edit_picture = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_picture);
                edit_username = (TextView) dialog.getCustomView().findViewById(R.id.myprof_username);
                edit_username_edit = (EditText) dialog.getCustomView().findViewById(R.id.myprof_username_edit);

                Picasso.with(getActivity())
                        .load(SavedData.getServerPicture(getActivity()))
                        .fit()
                        .placeholder(R.drawable.ic_userpicture)
                        .transform(new RoundedTransformation())
                        .into(edit_picture);

                Picasso.with(getActivity())
                        .load(SavedData.getServerBackground(getActivity()))
                        .fit()
                        .centerCrop()
                        .into(edit_background);

                edit_username.setText(SavedData.getServerName(getActivity()));
                edit_username_edit.setHint(SavedData.getServerName(getActivity()));

                edit_background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, 0);
                    }
                });
                edit_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, 1);
                    }
                });
                edit_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_username.setVisibility(View.GONE);
                        edit_username_edit.setVisibility(View.VISIBLE);
                        edit_username_edit.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                    inputMethodManager.hideSoftInputFromWindow(edit_username_edit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                    edit_username.setText(edit_username_edit.getText().toString());
                                    edit_username.setVisibility(View.VISIBLE);
                                    edit_username_edit.setVisibility(View.GONE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });

                dialog.show();
            }
        });

        getSignupAsync(getActivity());//サインアップとJSON

        mProfSwipe = (SwipyRefreshLayout) view.findViewById(R.id.swipe_container);
        mProfSwipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    mProfSwipe.setRefreshing(true);
                    getRefreshAsync(getActivity());
                }
            }

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            switch (requestCode) {
                case 0:
                    Picasso.with(getActivity())
                            .load(uri)
                            .fit()
                            .into(edit_background);

                    isBackground = true;
                    break;
                case 1:
                    Picasso.with(getActivity())
                            .load(uri)
                            .fit()
                            .placeholder(R.drawable.ic_userpicture)
                            .transform(new RoundedTransformation())
                            .into(edit_picture);

                    isPicture = true;
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Subscriberとして登録する
        BusHolder.get().register(this);

        startMovie();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Subscriberの登録を解除する
        BusHolder.get().unregister(this);

        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {

            // スクロールしていない
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                //mBusy = false;
                Log.d("DEBUG", "SCROLL_STATE_IDLE");
                changeMovie();

                break;
            // スクロール中
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_TOUCH_SCROLL");

                break;
            // はじいたとき
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_FLING");

                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
// Translate overlay and image

    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (scrollState == ScrollState.UP) {
            if (fab.isVisible()) {
                fab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!fab.isVisible()) {
                fab.show();
            }
        }
    }

    private void setDeleteDialog(final String post_id, final int position) {
        new MaterialDialog.Builder(getActivity())
                .title("投稿の削除")
                .content("この投稿を削除しますか？")
                .positiveText("する")
                .negativeText("いいえ")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        deleteSignupAsync(getActivity(), post_id, position);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }


    @Override
    public void movieCacheCreated(boolean success, String postId) {
        if (success && mPlayingPostId.equals(postId) && getActivity() != null) {
            Log.d("DEBUG", "MOVIE::movieCacheCreated 動画再生処理開始 postId:" + mPlayingPostId);
            startMovie();
        }
    }

    private void getSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.get(context, mProfUrl, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mProfusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mProfListView.setAdapter(mProfAdapter);

                if (mProfusers.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                myprofprogress.setVisibility(View.GONE);
            }
        });
    }

    private void postSignupAsync(final Context context, final String post_id, final int position) {
        goodParam = new RequestParams("post_id", post_id);
        httpClient2 = new AsyncHttpClient();
        httpClient2.setCookieStore(SavedData.getCookieStore(context));
        httpClient2.post(context, Const.URL_GOOD_API, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //配列のpushedatを１にする
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mProfusers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(getActivity(), "いいねに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getRefreshAsync(final Context context) {
        httpClient3 = new AsyncHttpClient();
        httpClient3.setCookieStore(SavedData.getCookieStore(context));
        httpClient3.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                mProfusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mProfusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mProfAdapter.notifyDataSetChanged();

                if (mProfusers.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mProfSwipe.setRefreshing(false);
            }
        });
    }

    private void deleteSignupAsync(final Context context, final String post_id, final int position) {
        deleteParam = new RequestParams("post_id", post_id);
        httpClient4 = new AsyncHttpClient();
        httpClient4.setCookieStore(SavedData.getCookieStore(context));
        httpClient4.post(context, Const.URL_DELETE_API, deleteParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProfusers.remove(position);
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                Toast.makeText(getActivity(), "削除に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeMovie() {
        Log.d("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mProfListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        if (mProfAdapter.isEmpty()) {
            return;
        }
        final UserData userData = mProfAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            // 前回の動画再生停止処理
            final ViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                Log.d("DEBUG", "MOVIE::changeMovie 再生停止 postId:" + mPlayingPostId);
                Log.e("DEBUG", "changeMovie 動画再生停止");
                stopMovie(oldViewHolder);

                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final ViewHolder currentViewHolder = getPlayingViewHolder();

            if (!currentViewHolder.movie.isShown()) {
                Log.e("DEBUG", "バグだよ");
            }

            final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
            if (path != null) {
                // 動画再生開始
                Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
                startMovie();
            } else {
                // 動画DL開始
                Log.d("DEBUG", "MOVIE::changeMovie  [ProgressBar VISIBLE] 動画DL処理開始 postId:" + mPlayingPostId);
                currentViewHolder.movieProgress.setVisibility(View.VISIBLE);
                currentViewHolder.videoFrame.setClickable(false);
                mCacheManager.requestMovieCacheCreate(getActivity(), userData.getMovie(), userData.getPost_id(), MyProfFragment.this, currentViewHolder.movieProgress);

            }
        }
    }

    private void startMovie() {
        if (mPlayBlockFlag) {
            Log.d("DEBUG", "startMovie play block status");
            return;
        }
        final ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder == null) {
            Log.d("DEBUG", "startMovie viewHolder is null");
            return;
        }
        final int position = mProfListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        final UserData userData = mProfAdapter.getItem(position - 1);

        final String postId = userData.getPost_id();

        // 安定感が増すおまじないを試してみる
        refreshVideoView(viewHolder);

        final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        Log.e("DEBUG", "[ProgressBar GONE] cache Path: " + path);
        if (path == null) {
            Log.d("DEBUG", "startMovie path is null");
            return;
        }
        viewHolder.movieProgress.setVisibility(View.INVISIBLE);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                Log.e("DEBUG", "VideoView::Error what:" + what + " extra:" + extra);
                return true;
            }
        });
        viewHolder.movie.setVideoPath(path);
        viewHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("DEBUG", "MOVIE::onPrepared postId: " + postId);
                if (mPlayingPostId == postId && !mPlayBlockFlag) {
                    Log.d("DEBUG", "MOVIE::onPrepared 再生開始");
                    viewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    viewHolder.movie.start();
                    viewHolder.videoFrame.setClickable(true);
                    Log.e("DEBUG", "onPrepared 動画再生開始: " + userData.getMovie());

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.e("DEBUG", "onPrepared onCompletion 動画再生開始");
                            mp.seekTo(0);
                            mp.start();
                        }
                    });
                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                            Log.e("DEBUG", "動画再生OnError: what:" + what + " extra:" + extra);
                            if (mPlayingPostId == postId && !mPlayBlockFlag) {
                                Log.d("DEBUG", "MOVIE::onErrorListener 再生開始");
                                mPlayingPostId = null;
                                changeMovie();
                            }
                            return true;
                        }
                    });
                } else {
                    Log.e("DEBUG", "onPrepared 動画再生停止");
                    viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
                    stopMovie(viewHolder);

                }
            }
        });
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void stopMovie(ViewHolder viewHolder) {
        if (viewHolder == null) {
            viewHolder = getPlayingViewHolder();
        }
        viewHolder.movie.pause();
        viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
    }

    /**
     * 現在再生中のViewHolderを取得
     *
     * @return
     */
    private ViewHolder getPlayingViewHolder() {
        ViewHolder viewHolder = null;
        Log.d("DEBUG", "getPlayingViewHolder :" + mPlayingPostId);
        if (mPlayingPostId != null) {
            for (Map.Entry<ViewHolder, String> entry : mViewHolderHash.entrySet()) {
                if (entry.getValue().equals(mPlayingPostId)) {
                    viewHolder = entry.getKey();
                    break;
                }
            }
        }
        return viewHolder;
    }

    private void refreshVideoView(ViewHolder viewHolder) {
        viewHolder.movie.setOnPreparedListener(null);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                return true;
            }
        });

        viewHolder.movie.stopPlayback();
        ViewGroup viewgroup = (ViewGroup) viewHolder.movie.getParent();
        if (mVideoAttr == null) {
            mVideoAttr = viewHolder.movie.getAttributes();
        }
        viewHolder.movie.suspend();
        try {
            viewgroup.removeView(viewHolder.movie);
        } catch (RuntimeException runtimeexception) {
            try {
                viewgroup.removeView(viewHolder.movie);
            } catch (Exception exception) {
                Log.e("ERROR", "Weird things are happening.");
            }
        }
        viewHolder.movie = new SquareVideoView(getActivity(), mVideoAttr);
        viewHolder.movie.setId(R.id.videoView);
        viewgroup.addView(viewHolder.movie, 0);

    }

    public static class ViewHolder {
        public ImageView circleImage;
        public TextView user_name;
        public TextView datetime;
        public TextView comment;
        public RippleView menuRipple;
        public SquareVideoView movie;
        public RoundCornerProgressBar movieProgress;
        public ImageView mVideoThumbnail;
        //public ImageView restaurantImage;
        //public TextView locality;
        public TextView rest_name;
        public TextView category;
        public TextView value;
        public TextView atmosphere;
        public RippleView tenpoRipple;
        public TextView likes;
        public ImageView likes_Image;
        public TextView comments;
        public RippleView likes_ripple;
        public RippleView comments_ripple;
        public FrameLayout videoFrame;
    }

    public class MyProfAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater mLayoutInflater;

        public MyProfAdapter(Context context, int viewResourceId, ArrayList<UserData> profusers) {
            super(context, viewResourceId, profusers);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final UserData user = getItem(position);

            // ViewHolder 取得・作成処理
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mLayoutInflater.inflate(R.layout.cell_timeline2, null);

                viewHolder = new ViewHolder();
                viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.circleImage);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.time_text);
                viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
                viewHolder.menuRipple = (RippleView) convertView.findViewById(R.id.menuRipple);
                viewHolder.movie = (SquareVideoView) convertView.findViewById(R.id.videoView);
                viewHolder.movieProgress = (RoundCornerProgressBar) convertView.findViewById(R.id.video_progress);
                viewHolder.mVideoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                //viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
                viewHolder.rest_name = (TextView) convertView.findViewById(R.id.rest_name);
                //viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
                viewHolder.category = (TextView) convertView.findViewById(R.id.category);
                viewHolder.value = (TextView) convertView.findViewById(R.id.value);
                viewHolder.atmosphere = (TextView) convertView.findViewById(R.id.mood);
                viewHolder.tenpoRipple = (RippleView) convertView.findViewById(R.id.tenpoRipple);
                viewHolder.likes = (TextView) convertView.findViewById(R.id.likes_Number);
                viewHolder.likes_Image = (ImageView) convertView.findViewById(R.id.likes_Image);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments_Number);
                viewHolder.likes_ripple = (RippleView) convertView.findViewById(R.id.likes_ripple);
                viewHolder.comments_ripple = (RippleView) convertView.findViewById(R.id.comments_ripple);
                viewHolder.videoFrame = (FrameLayout) convertView.findViewById(R.id.videoFrame);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.user_name.setText(user.getUser_name());
            if (viewHolder.circleImage == null) {
                Log.d("DEBUG", "viewHolder.circleImage is null");
            }

            viewHolder.datetime.setText(user.getDatetime());

            viewHolder.comment.setText(user.getComment());

            final ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(getActivity(), R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_myprof).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.facebook_share:
                                    Uri bmpUri2 = getLocalBitmapUri(finalViewHolder1.mVideoThumbnail);
                                    if (ShareDialog.canShow(SharePhotoContent.class) && bmpUri2 != null) {
                                        SharePhoto photo = new SharePhoto.Builder()
                                                .setImageUrl(bmpUri2)
                                                .build();
                                        SharePhotoContent content = new SharePhotoContent.Builder()
                                                .addPhoto(photo)
                                                .build();
                                        shareDialog.show(content);
                                    } else {
                                        // ...sharing failed, handle error
                                        Toast.makeText(getActivity(), "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.twitter_share:
                                    Uri bmpUri = getLocalBitmapUri(finalViewHolder1.mVideoThumbnail);
                                    if (bmpUri != null) {
                                        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                                                .text(user.getRest_name() + "/" + user.getLocality())
                                                .image(bmpUri);

                                        builder.show();
                                    } else {
                                        // ...sharing failed, handle error
                                        Toast.makeText(getActivity(), "twitterシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.delete:
                                    setDeleteDialog(user.getPost_id(), position);
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                }
            });

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.circleImage);


            Picasso.with(getContext())
                    .load(user.getThumbnail())
                    .placeholder(R.color.videobackground)
                    .into(viewHolder.mVideoThumbnail);
            viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            viewHolder.movieProgress.setVisibility(View.INVISIBLE);
            viewHolder.movieProgress.setAlpha(0.5f);
            viewHolder.movieProgress.setProgress(0);
            if (viewHolder.movie.isPlaying()) {
                Log.e("DEBUG", "getView 動画再生停止");
                stopMovie(viewHolder);
            }

            final ViewHolder videoClickViewHolder = viewHolder;
            viewHolder.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoClickViewHolder.movie.isPlaying()) {
                        videoClickViewHolder.movie.pause();
                    } else {
                        videoClickViewHolder.movie.start();
                        videoClickViewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    }
                }
            });

            viewHolder.rest_name.setText(user.getRest_name());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getTagCategory().equals("none")) {
                viewHolder.category.setText(user.getTagCategory());
            } else {
                viewHolder.category.setText("タグなし");
            }
            if (!user.getAtmosphere().equals("none")) {
                viewHolder.atmosphere.setText(user.getAtmosphere());
            } else {
                viewHolder.atmosphere.setText("タグなし");
            }
            if (!user.getValue().equals("0")) {
                viewHolder.value.setText(user.getValue());
            } else {
                viewHolder.value.setText("タグなし");
            }

            //リップルエフェクトを見せてからIntentを飛ばす
            viewHolder.tenpoRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedRestname = user.getRest_name();
                    clickedLocality = user.getLocality();
                    clickedLat = user.getLat();
                    clickedLon = user.getLon();
                    clickedPhoneNumber = user.getTell();
                    clickedHomepage = user.getHomepage();
                    clickedCategory = user.getCategory();
                    clickedWant_flag = user.getWant_flag();
                    clickedTotal_cheer_num = user.getTotal_cheer_num();
                    Handler handler = new Handler();
                    handler.postDelayed(new restClickHandler(), 750);
                }
            });

            final int currentgoodnum = user.getgoodnum();
            final int currentcommentnum = user.getComment_num();

            viewHolder.likes.setText(String.valueOf(currentgoodnum));
            viewHolder.comments.setText(String.valueOf(currentcommentnum));

            if (user.getPushed_at() == 0) {
                viewHolder.likes_ripple.setClickable(true);
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_white);

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id());
                        final UserData user = mProfusers.get(position);
                        user.setPushed_at(1);
                        user.setgoodnum(currentgoodnum + 1);

                        finalViewHolder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        finalViewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                        finalViewHolder.likes_ripple.setClickable(false);

                        postSignupAsync(getActivity(), user.getPost_id(), position);
                    }
                });
            } else {
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                viewHolder.likes_ripple.setClickable(false);
            }

            viewHolder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                    //投稿に対するコメントが見れるダイアログを表示
                    View commentView = new CommentView(getActivity(), user.getPost_id());

                    new MaterialDialog.Builder(getActivity())
                            .customView(commentView, false)
                            .show();
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
            return convertView;
        }
    }

    class restClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getActivity(), FlexibleTenpoActivity.class);
            intent.putExtra("restname", clickedRestname);
            intent.putExtra("locality", clickedLocality);
            intent.putExtra("lat", clickedLat);
            intent.putExtra("lon", clickedLon);
            intent.putExtra("phone", clickedPhoneNumber);
            intent.putExtra("homepage", clickedHomepage);
            intent.putExtra("category", clickedCategory);
            intent.putExtra("want_flag", clickedWant_flag);
            intent.putExtra("total_cheer_num", clickedTotal_cheer_num);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}
