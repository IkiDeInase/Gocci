package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.CommentActionRepository;
import com.inase.android.gocci.datasource.repository.CommentActionRepositoryImpl;
import com.inase.android.gocci.datasource.repository.CommentDataRepository;
import com.inase.android.gocci.datasource.repository.CommentDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.CommentPageUseCase;
import com.inase.android.gocci.domain.usecase.CommentPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.CommentPostUseCase;
import com.inase.android.gocci.domain.usecase.CommentPostUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.presenter.ShowCommentPagePresenter;
import com.inase.android.gocci.ui.adapter.CommentAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks,
        ShowCommentPagePresenter.ShowCommentView, CommentAdapter.CommentCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.list)
    ObservableRecyclerView mCommentRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.comment_edit)
    EditText mCommentEdit;

    @OnClick(R.id.send_button)
    public void onSend(ImageButton button) {
        String comment = mCommentEdit.getText().toString().replace(mNoticeUser_name + "\n", "");
        if (!comment.isEmpty()) {
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            button.requestFocus();
            mCommentEdit.setText("");
            mPresenter.postComment(Const.getPostCommentWithNoticeAPI(mPost_id, comment, mNoticeUser_id), Const.getCommentAPI(mPost_id));
        }
    }

    //mPresenter.postComment(Const.getPostCommentAPI(mPost_id, input.toString()), Const.getCommentAPI(mPost_id));

    private LinearLayoutManager mLayoutManager;
    private ArrayList<HeaderData> mCommentusers = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    private CommentActivity self = this;

    private String mPost_id;
    private String title;

    private String mNoticeUser_name;
    private String mNoticeUser_id;

    private static MobileAnalyticsManager analytics;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ShowCommentPagePresenter mPresenter;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommentActivity activity
                    = (CommentActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    break;
                case Const.INTENT_TO_MYPAGE:
                    GocciMyprofActivity.startMyProfActivity(activity);
                    break;
                case Const.INTENT_TO_ADVICE:
                    Util.setAdviceDialog(activity);
                    break;
                case Const.INTENT_TO_SETTING:
                    SettingActivity.startSettingActivity(activity);
                    break;
            }
        }
    };

    public static void startCommentActivity(int post_id, int user_id, String username, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CommentActivity.class);
        intent.putExtra("title", startingActivity.getLocalClassName());
        intent.putExtra("post_id", post_id);
        intent.putExtra("user_id", user_id);
        intent.putExtra("username", username);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void startCommentActivityOnContext(int post_id, int user_id, String username, Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("title", context.getString(R.string.comment));
        intent.putExtra("post_id", post_id);
        intent.putExtra("user_id", user_id);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        CommentDataRepository commentDataRepositoryImpl = CommentDataRepositoryImpl.getRepository();
        CommentActionRepository commentActionRepositoryImpl = CommentActionRepositoryImpl.getRepository();
        CommentPageUseCase commentPageUseCaseImpl = CommentPageUseCaseImpl.getUseCase(commentDataRepositoryImpl, UIThread.getInstance());
        CommentPostUseCase commentPostUseCaseImpl = CommentPostUseCaseImpl.getUseCase(commentActionRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCommentPagePresenter(commentPageUseCaseImpl, commentPostUseCaseImpl);
        mPresenter.setCommentView(this);

        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        mPost_id = String.valueOf(intent.getIntExtra("post_id", 0));
        mNoticeUser_id = String.valueOf(intent.getIntExtra("user_id", 0));
        mNoticeUser_name = "@" + intent.getStringExtra("username") + " ";

        mCommentEdit.setText(mNoticeUser_name + "\n");
        mCommentEdit.setSelection(mCommentEdit.getText().length());

        setSupportActionBar(mToolBar);
        if (title.equals("Activity.GocciMyprofActivity")) {
            getSupportActionBar().setTitle(getString(R.string.mypage));
        } else {
            getSupportActionBar().setTitle(getString(R.string.comment));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mCommentRecyclerView.setScrollViewCallbacks(this);

        mPresenter.getCommentData(Const.COMMENT_FIRST, Const.getCommentAPI(mPost_id));

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(CommentActivity.this) != Util.NetworkStatus.OFF) {
                    mPresenter.getCommentData(Const.COMMENT_REFRESH, Const.getCommentAPI(mPost_id));
                } else {
                    Toast.makeText(CommentActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

        mCommentEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        mCommentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    // スクロールしていない
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //mBusy = false;
                        break;
                    // スクロール中
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //mBusy = true;
                        break;
                    // はじいたとき
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //mBusy = true;
                        break;
                }

                visibleItemCount = mCommentRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    loading = true;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }

        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);

        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }

        mPresenter.pause();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        //Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        // お知らせ未読件数バッジ表示
        final MenuItem reply = menu.findItem(R.id.comment_reply);
        final MenuItem reply_all = menu.findItem(R.id.comment_reply_all);
        final MenuItem action = menu.findItem(R.id.comment_action);
        final MenuItem delete = menu.findItem(R.id.comment_delete);

        reply.setVisible(false);
        reply_all.setVisible(false);
        action.setVisible(false);
        delete.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onUserClick(int user_id, String user_name) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, user_name, this);
    }

    @Override
    public void onCommentClick(String username, String user_id) {
        mNoticeUser_id = user_id;
        mNoticeUser_name = username;
        mCommentEdit.setText(username + "\n");
        mCommentEdit.setSelection(mCommentEdit.getText().length());
        mCommentEdit.requestFocus();
    }

    @Override
    public void onCommentLongClick(String user_id) {
        if (user_id.equals(SavedData.getServerUserId(this))) {
            //自分の投稿　削除
            new MaterialDialog.Builder(this)
                    .content("このコメントを削除しますか？")
                    .contentColorRes(R.color.nameblack)
                    .positiveText("削除する")
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText("いいえ")
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        }
                    }).show();
        } else {
            //他人の　不適切
            new MaterialDialog.Builder(this)
                    .content("このコメントを不適切なコメントとして報告しますか？")
                    .contentColorRes(R.color.nameblack)
                    .positiveText("報告する")
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText("いいえ")
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        }
                    }).show();
        }
    }

    @Override
    public void showLoading() {
        mSwipeContainer.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void showNoResultCase(int api, PostData postData) {
        switch (api) {
            case Const.COMMENT_FIRST:
                mCommentAdapter = new CommentAdapter(this, mPost_id, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                mCommentRecyclerView.scrollVerticallyToPosition(mCommentusers.size() - 1);
                break;
            case Const.COMMENT_REFRESH:
                mCommentusers.clear();
                mCommentAdapter.setData();
                break;
        }
    }

    @Override
    public void hideNoResultCase() {

    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(int api, PostData postData, ArrayList<HeaderData> commentData) {
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        switch (api) {
            case Const.COMMENT_FIRST:
                mCommentAdapter = new CommentAdapter(this, mPost_id, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                mCommentRecyclerView.scrollVerticallyToPosition(mCommentusers.size() - 1);
                break;
            case Const.COMMENT_REFRESH:
                mCommentAdapter.setData();
                break;
        }
    }

    @Override
    public void postCommented(PostData postData, ArrayList<HeaderData> commentData) {
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        mCommentAdapter.setData();
        mCommentRecyclerView.scrollVerticallyToPosition(mCommentusers.size() - 1);
    }

    @Override
    public void postCommentEmpty(PostData postData) {
        mCommentusers.clear();
        mCommentAdapter.setData();
    }

    @Override
    public void postCommentFailed() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }
}
