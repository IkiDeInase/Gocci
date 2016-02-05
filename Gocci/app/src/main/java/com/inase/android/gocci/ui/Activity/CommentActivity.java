package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.datasource.repository.CommentActionRepository;
import com.inase.android.gocci.datasource.repository.CommentActionRepositoryImpl;
import com.inase.android.gocci.datasource.repository.CommentDataRepository;
import com.inase.android.gocci.datasource.repository.CommentDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.usecase.CommentPageUseCase;
import com.inase.android.gocci.domain.usecase.CommentPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.CommentPostUseCase;
import com.inase.android.gocci.domain.usecase.CommentPostUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowCommentPagePresenter;
import com.inase.android.gocci.ui.adapter.CommentAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class CommentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks,
        ShowCommentPagePresenter.ShowCommentView, CommentAdapter.CommentCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.overlay)
    View mOverlay;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
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
            API3.Util.SetCommentLocalCode localCode = API3.Impl.getRepository().SetCommentParameterRegex(mPost_id, comment, mNoticeUser_id.isEmpty() ? null : mNoticeUser_id);
            if (localCode == null) {
                button.setFocusable(true);
                button.setFocusableInTouchMode(true);
                button.requestFocus();
                if (isNotice) {
                    mPresenter.postComment(Const.APICategory.SET_COMMENT, API3.Util.getSetCommentAPI(mPost_id, comment, mNoticeUser_id), API3.Util.getGetCommentAPI(mPost_id));
                } else {
                    mPresenter.postComment(Const.APICategory.SET_COMMENT, API3.Util.getSetCommentAPI(mPost_id, comment, null), API3.Util.getGetCommentAPI(mPost_id));
                }
            } else {
                Toast.makeText(this, API3.Util.SetCommentLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
    private ArrayList<String> mComment_ids = new ArrayList<>();
    private HeaderData mMemoData;
    private CommentAdapter mCommentAdapter;

    private CommentActivity self = this;

    private String mPost_id;

    private boolean isMyPage = false;
    private boolean isNotice = false;

    private String mNoticeUser_name;
    private String mNoticeUser_id = "";

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ShowCommentPagePresenter mPresenter;

    private String mEditedMemo;
    private String mEditedComment;

    public static void startCommentActivity(String post_id, boolean isMyPage, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CommentActivity.class);
        intent.putExtra("post_id", post_id);
        intent.putExtra("judge", isMyPage);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void startCommentActivityOnContext(String post_id, boolean isMyPage, Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("post_id", post_id);
        intent.putExtra("judge", isMyPage);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final API3 api3Impl = API3.Impl.getRepository();
        CommentDataRepository commentDataRepositoryImpl = CommentDataRepositoryImpl.getRepository(api3Impl);
        CommentActionRepository commentActionRepositoryImpl = CommentActionRepositoryImpl.getRepository(api3Impl);
        CommentPageUseCase commentPageUseCaseImpl = CommentPageUseCaseImpl.getUseCase(commentDataRepositoryImpl, UIThread.getInstance());
        CommentPostUseCase commentPostUseCaseImpl = CommentPostUseCaseImpl.getUseCase(commentActionRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCommentPagePresenter(commentPageUseCaseImpl, commentPostUseCaseImpl);
        mPresenter.setCommentView(this);

        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        Intent intent = getIntent();
        isMyPage = intent.getBooleanExtra("judge", false);
        mPost_id = intent.getStringExtra("post_id");

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

        mSwipeRefresh.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Util.getConnectedState(CommentActivity.this) != Util.NetworkStatus.OFF) {
                    API3.Util.GetCommentLocalCode localCode = api3Impl.GetCommentParameterRegex(mPost_id);
                    if (localCode == null) {
                        mPresenter.getCommentData(Const.APICategory.GET_COMMENT_REFRESH, API3.Util.getGetCommentAPI(mPost_id));
                    } else {
                        mSwipeRefresh.setRefreshing(false);
                        Toast.makeText(CommentActivity.this, API3.Util.GetCommentLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CommentActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefresh.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });
                }
            }
        });

        API3.Util.GetCommentLocalCode localCode = api3Impl.GetCommentParameterRegex(mPost_id);
        if (localCode == null) {
            mPresenter.getCommentData(Const.APICategory.GET_COMMENT_FIRST, API3.Util.getGetCommentAPI(mPost_id));
        } else {
            Toast.makeText(CommentActivity.this, API3.Util.GetCommentLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }

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
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("Comment");
                        mTracker.send(new HitBuilders.EventBuilder().setCategory("Public").setAction("ScrollCount").setLabel(SavedData.getServerUserId(CommentActivity.this)).build());
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
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(self);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("Comment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mPresenter.resume();
    }

    @Override
    public void onPause() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(self);
        mPresenter.pause();
        super.onPause();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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
    public void onUserClick(String user_id, String user_name) {
        UserProfActivity.startUserProfActivity(user_id, user_name, this);
    }

    @Override
    public void onCommentClick(String username, String user_id) {
        mNoticeUser_id = user_id;
        if (username.isEmpty()) {
            mNoticeUser_name = username;
            isNotice = false;
        } else {
            mNoticeUser_name = username + "\n";
            isNotice = true;
        }
        mCommentEdit.setText(mNoticeUser_name);
        mCommentEdit.setSelection(mCommentEdit.getText().length());
        mCommentEdit.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mCommentEdit, 0);
        mOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMemoLongClick(final String user_id, final String username, final String memo) {
        if (user_id.equals(SavedData.getServerUserId(this))) {
            new MaterialDialog.Builder(CommentActivity.this)
                    .content(getString(R.string.edit_comment))
                    .contentColorRes(R.color.nameblack)
                    .contentGravity(GravityEnum.CENTER)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .widgetColorRes(R.color.nameblack)
                    .positiveText(getString(R.string.complete))
                    .positiveColorRes(R.color.gocci_header)
                    .input(memo.equals("none") ? "ノーコメント" : "", memo.equals("none") ? "" : memo, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                            mEditedMemo = charSequence.toString();
                            API3PostUtil.setMemoEditAsync(CommentActivity.this, mPost_id, mEditedMemo, Const.ActivityCategory.COMMENT_PAGE);
                        }
                    }).show();
        } else {
            new MaterialDialog.Builder(this)
                    .items("このユーザーのページへ行く")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    UserProfActivity.startUserProfActivity(user_id, username, CommentActivity.this);
                                    break;
                            }
                        }
                    }).show();
        }
    }

    @Override
    public void onCommentLongClick(final String user_id, final String username, final String comment_id, final String comment) {
        if (user_id.equals(SavedData.getServerUserId(this))) {
            new MaterialDialog.Builder(this)
                    .items("コメントを編集する", "コメントを削除する")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            switch (which) {
                                case 0:
                                    new MaterialDialog.Builder(CommentActivity.this)
                                            .content(getString(R.string.edit_comment))
                                            .contentColorRes(R.color.nameblack)
                                            .contentGravity(GravityEnum.CENTER)
                                            .inputType(InputType.TYPE_CLASS_TEXT)
                                            .widgetColorRes(R.color.nameblack)
                                            .positiveText(getString(R.string.complete))
                                            .positiveColorRes(R.color.gocci_header)
                                            .input("", comment, false, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                                    mEditedComment = charSequence.toString();
                                                    API3PostUtil.setCommentEditAsync(CommentActivity.this, comment_id, mEditedComment, Const.ActivityCategory.COMMENT_PAGE);
                                                }
                                            }).show();
                                    break;
                                case 1:
                                    new MaterialDialog.Builder(CommentActivity.this)
                                            .content(getString(R.string.delete_comment_content))
                                            .contentColorRes(R.color.nameblack)
                                            .positiveText(getString(R.string.delete_comment_positive))
                                            .positiveColorRes(R.color.gocci_header)
                                            .negativeText(getString(R.string.delete_comment_negative))
                                            .negativeColorRes(R.color.gocci_header)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                                    API3PostUtil.unsetCommentAsync(CommentActivity.this, comment_id, Const.ActivityCategory.COMMENT_PAGE);
                                                }
                                            }).show();
                                    break;
                            }
                        }
                    }).show();
        } else {
            if (isMyPage) {
                new MaterialDialog.Builder(this)
                        .items("このユーザーのページへ行く", "コメントを削除する")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        UserProfActivity.startUserProfActivity(user_id, username, CommentActivity.this);
                                        break;
                                    case 1:
                                        new MaterialDialog.Builder(CommentActivity.this)
                                                .content(getString(R.string.delete_comment_content))
                                                .contentColorRes(R.color.nameblack)
                                                .positiveText(getString(R.string.delete_comment_positive))
                                                .positiveColorRes(R.color.gocci_header)
                                                .negativeText(getString(R.string.delete_comment_negative))
                                                .negativeColorRes(R.color.gocci_header)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                                        API3PostUtil.unsetCommentAsync(CommentActivity.this, comment_id, Const.ActivityCategory.COMMENT_PAGE);
                                                    }
                                                }).show();
                                        break;
                                }
                            }
                        }).show();
            } else {
                new MaterialDialog.Builder(this)
                        .items("このユーザーのページへ行く", "コメントを違反報告する")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        UserProfActivity.startUserProfActivity(user_id, username, CommentActivity.this);
                                        break;
                                    case 1:
                                        new MaterialDialog.Builder(CommentActivity.this)
                                                .content(getString(R.string.block_comment_content))
                                                .contentColorRes(R.color.nameblack)
                                                .positiveText(getString(R.string.block_comment_positive))
                                                .positiveColorRes(R.color.gocci_header)
                                                .negativeText(getString(R.string.block_comment_negative))
                                                .negativeColorRes(R.color.gocci_header)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                                        API3PostUtil.setCommentBlockAsync(CommentActivity.this, comment_id);
                                                    }
                                                }).show();
                                }
                            }
                        }).show();
            }
        }
    }

    @Override
    public void showLoading() {
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void showEmpty(Const.APICategory api, HeaderData memoData) {
        switch (api) {
            case GET_COMMENT_FIRST:
                mMemoData = memoData;
                mCommentAdapter = new CommentAdapter(this, mPost_id, mMemoData, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                break;
            case GET_COMMENT_REFRESH:
                mCommentusers.clear();
                mComment_ids.clear();
                mMemoData = memoData;
                mCommentAdapter.setData();
                break;
        }
    }

    @Override
    public void hideEmpty() {

    }

    @Override
    public void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Override
    public void showResult(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids) {
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        mComment_ids.clear();
        mComment_ids.addAll(comment_ids);
        mMemoData = memoData;
        switch (api) {
            case GET_COMMENT_FIRST:
                mCommentAdapter = new CommentAdapter(this, mPost_id, mMemoData, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.setAdapter(mCommentAdapter);

                mLayoutManager.scrollToPosition(mCommentusers.size());

                new MaterialIntroView.Builder(CommentActivity.this)
                        .dismissOnTouch(true)
                        .setTextColor(getResources().getColor(R.color.nameblack))
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.MINIMUM)
                        .setDelayMillis(200)
                        .enableFadeAnimation(true)
                        .performClick(true)
                        .setInfoText("コメントはタップで返信、長押しでアクションが選べます")
                        .setTarget(mCommentRecyclerView)
                        .setUsageId("comment_action") //THIS SHOULD BE UNIQUE ID
                        .show();
                break;
            case GET_COMMENT_REFRESH:
                mCommentAdapter.setData();
                break;
        }
    }

    @Override
    public void postCommented(Const.APICategory api, HeaderData postData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids) {
        mCommentEdit.setText("");
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        mComment_ids.clear();
        mComment_ids.addAll(comment_ids);
        mNoticeUser_id = "";
        mNoticeUser_name = "";
        mMemoData = postData;
        mCommentAdapter.setData();
        if (mOverlay.getVisibility() == View.VISIBLE) {
            mOverlay.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mCommentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void postCommentEmpty(Const.APICategory api, HeaderData postData) {
        mCommentusers.clear();
        mComment_ids.clear();
        mMemoData = postData;
        mCommentAdapter.setData();
    }

    @Override
    public void postFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void postFailureCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_COMMENT_FIRST:
            case GET_COMMENT_REFRESH:
                mPresenter.getCommentData(event.api, API3.Util.getGetCommentAPI(mPost_id));
                break;
        }
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.COMMENT_PAGE) {
            int position;
            switch (event.apiCategory) {
                case UNSET_COMMENT:
                    position = mComment_ids.indexOf(event.id);
                    mCommentusers.remove(position);
                    mComment_ids.remove(position);
                    mCommentAdapter.notifyItemRemoved(position + 1);
                    break;
                case SET_COMMENT_EDIT:
                    if (mEditedComment != null) {
                        position = mComment_ids.indexOf(event.id);
                        mCommentusers.get(position).setComment(mEditedComment);
                        mCommentAdapter.notifyItemChanged(position + 1);
                        mEditedComment = null;
                    }
                    break;
                case SET_MEMO_EDIT:
                    if (mEditedMemo != null) {
                        mMemoData.setMemo(mEditedMemo);
                        mCommentAdapter.notifyItemChanged(0);
                        mEditedMemo = null;
                    }
                    break;
            }
        }
    }
}
