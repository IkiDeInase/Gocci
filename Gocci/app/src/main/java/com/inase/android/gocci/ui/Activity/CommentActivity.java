package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks,
        ShowCommentPagePresenter.ShowCommentView, CommentAdapter.CommentCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.overlay)
    View mOverlay;
    @Bind(R.id.list)
    ObservableRecyclerView mCommentRecyclerView;
    @Bind(R.id.comment_edit)
    EditText mCommentEdit;
    @Bind(R.id.send_button)
    ImageButton mSendButton;

    @OnClick(R.id.comment_edit)
    public void onEdit() {
        if (mOverlay.getVisibility() != View.VISIBLE) {
            mOverlay.setVisibility(View.VISIBLE);
            isNotice = false;
        }
    }

    @OnClick(R.id.send_button)
    public void onSend(ImageButton button) {
        String comment = null;
        if (isNotice) {
            comment = mCommentEdit.getText().toString().replace(mNoticeUser_name, "");
        } else {
            comment = mCommentEdit.getText().toString();
        }
        if (!comment.isEmpty()) {
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            button.requestFocus();
            mCommentEdit.setText("");
            if (isNotice) {
                mPresenter.postComment(Const.getPostCommentWithNoticeAPI(mPost_id, comment, mNoticeUser_id), Const.getCommentAPI(mPost_id));
            } else {
                mPresenter.postComment(Const.getPostCommentAPI(mPost_id, comment), Const.getCommentAPI(mPost_id));
            }
            if (mOverlay.getVisibility() == View.VISIBLE) {
                mOverlay.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.overlay)
    public void onOutside() {
        if (mOverlay.getVisibility() == View.VISIBLE) {
            mOverlay.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mCommentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private LinearLayoutManager mLayoutManager;
    private ArrayList<HeaderData> mCommentusers = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    private CommentActivity self = this;

    private String mPost_id;

    private boolean isMyPage = false;
    private boolean isNotice = false;

    private String mNoticeUser_name;
    private String mNoticeUser_id;

    private static MobileAnalyticsManager analytics;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ShowCommentPagePresenter mPresenter;

    public static void startCommentActivity(int post_id, boolean isMyPage, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CommentActivity.class);
        intent.putExtra("post_id", post_id);
        intent.putExtra("judge", isMyPage);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void startCommentActivityOnContext(int post_id, boolean isMyPage, Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("post_id", post_id);
        intent.putExtra("judge", isMyPage);
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
        isMyPage = intent.getBooleanExtra("judge", false);
        mPost_id = String.valueOf(intent.getIntExtra("post_id", 0));

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getString(R.string.comment));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCommentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNotice) {
                    mSendButton.setAlpha(s.toString().length() <= mNoticeUser_name.length() ? 0.4f : 1.0f);
                    mSendButton.setClickable(s.toString().length() > mNoticeUser_name.length());
                } else {
                    mSendButton.setAlpha(s.toString().length() == 0 ? 0.4f : 1.0f);
                    mSendButton.setClickable(s.toString().length() != 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mCommentRecyclerView.setScrollViewCallbacks(this);

        mPresenter.getCommentData(Const.COMMENT_FIRST, Const.getCommentAPI(mPost_id));

        //mPresenter.getCommentData(Const.COMMENT_REFRESH, Const.getCommentAPI(mPost_id));

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
        mNoticeUser_name = username + "\n";
        isNotice = true;
        mCommentEdit.setText(mNoticeUser_name);
        mCommentEdit.setSelection(mCommentEdit.getText().length());
        mCommentEdit.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mCommentEdit, 0);
        mOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCommentLongClick(String user_id) {
        if (user_id.equals(SavedData.getServerUserId(this)) || isMyPage) {
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

    }

    @Override
    public void hideLoading() {

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
