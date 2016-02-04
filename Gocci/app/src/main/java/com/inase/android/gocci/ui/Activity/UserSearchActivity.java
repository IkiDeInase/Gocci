package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.FollowRepository;
import com.inase.android.gocci.datasource.repository.FollowRepositoryImpl;
import com.inase.android.gocci.datasource.repository.UserSearchRepository;
import com.inase.android.gocci.datasource.repository.UserSearchRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.SearchUserData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.FollowUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserSearchUseCase;
import com.inase.android.gocci.domain.usecase.UserSearchUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowUserSearchPresenter;
import com.inase.android.gocci.ui.adapter.UserSearchAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserSearchActivity extends AppCompatActivity implements ShowUserSearchPresenter.ShowUserSearchView,
        UserSearchAdapter.SearchUserCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgress;
    @Bind(R.id.search_view)
    MaterialSearchView mSearchView;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private UserSearchAdapter mUserSearchAdapter;
    private LinearLayoutManager mLayoutManager;

    private ArrayList<SearchUserData> mSearchList = new ArrayList<>();
    private ArrayList<String> mSearchUser_idList = new ArrayList<>();

    private ArrayList<SearchUserData> mRankusers = new ArrayList<>();
    private ArrayList<String> mRankUser_idList = new ArrayList<>();

    private ShowUserSearchPresenter mPresenter;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int mNextCount = 1;
    private boolean isEndScrioll = false;

    public static void startUserSearchActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserSearchActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final API3 api3Impl = API3.Impl.getRepository();
        UserSearchRepository userSearchRepositoryImpl = UserSearchRepositoryImpl.getRepository(api3Impl);
        UserSearchUseCase userSearchUseCaseImpl = UserSearchUseCaseImpl.getUseCase(userSearchRepositoryImpl, UIThread.getInstance());
        FollowRepository followRepository = FollowRepositoryImpl.getRepository(api3Impl);
        FollowUseCase followUseCase = FollowUseCaseImpl.getUseCase(followRepository, UIThread.getInstance());
        mPresenter = new ShowUserSearchPresenter(userSearchUseCaseImpl, followUseCase);
        mPresenter.setUserSearchView(this);

        setContentView(R.layout.activity_user_search);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        mToolBar.setTitle(getString(R.string.setting_friend_search));
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("UserSearch");
                        mTracker.send(new HitBuilders.EventBuilder().setCategory("Public").setAction("ScrollCount").setLabel(SavedData.getServerUserId(UserSearchActivity.this)).build());
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mSearchView.isSearchOpen()) {
                    visibleItemCount = mRecyclerView.getChildCount();
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
                        if (!isEndScrioll) {
                            API3.Util.GetFollower_RankLocalCode localCode = API3.Impl.getRepository().GetFollower_RankParameterRegex(String.valueOf(mNextCount));
                            if (localCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_FOLLOWER_RANK_ADD, API3.Util.getGetFollowerRankAPI(String.valueOf(mNextCount)));
                            } else {
                                Toast.makeText(UserSearchActivity.this, API3.Util.GetFollower_RankLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        mUserSearchAdapter = new UserSearchAdapter(this, mRankusers);
        mUserSearchAdapter.setSearchUserCallback(this);
        mRecyclerView.setAdapter(mUserSearchAdapter);

        API3.Util.GetFollower_RankLocalCode localCode = API3.Impl.getRepository().GetFollower_RankParameterRegex(null);
        if (localCode == null) {
            mPresenter.getListData(Const.APICategory.GET_FOLLOWER_RANK_FIRST, API3.Util.getGetFollowerRankAPI(null));
        } else {
            Toast.makeText(UserSearchActivity.this, API3.Util.GetFollower_RankLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }

        mSearchView.setHint("ユーザー名");
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (!newText.isEmpty()) {
                    API3.Util.GetUsernameLocalCode getUsernameLocalCode = api3Impl.GetUsernameParameterRegex(newText);
                    if (getUsernameLocalCode == null) {
                        mPresenter.getListData(Const.APICategory.GET_USERNAME, API3.Util.getGetUsernameAPI(newText));
                    } else {
                        Toast.makeText(UserSearchActivity.this, API3.Util.GetUsernameLocalCodeMessageTable(getUsernameLocalCode), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSearchList.clear();
                    mSearchUser_idList.clear();
                    mUserSearchAdapter.setData(mSearchList);
                }
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //検索
                mUserSearchAdapter.setData(mSearchList);
            }

            @Override
            public void onSearchViewClosed() {
                //おすすめ
                mUserSearchAdapter.setData(mRankusers);
            }
        });
    }

    @Override
    public final void onPause() {
        BusHolder.get().unregister(this);
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        mPresenter.pause();
        super.onPause();
    }

    @Override
    public final void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("UserSearch");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(this);
        mPresenter.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_user, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
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
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
    }

    @Override
    public void onUserClick(String user_id, String username) {
        UserProfActivity.startUserProfActivity(user_id, username, this);
    }

    @Override
    public void onFollowClick(Const.APICategory api, String user_id) {
        if (api == Const.APICategory.SET_FOLLOW) {
            API3.Util.SetFollowLocalCode postFollowLocalCode = API3.Impl.getRepository().SetFollowParameterRegex(user_id);
            if (postFollowLocalCode == null) {
                mPresenter.postFollow(api, API3.Util.getSetFollowAPI(user_id), user_id);
            } else {
                Toast.makeText(this, API3.Util.SetFollowLocalCodeMessageTable(postFollowLocalCode), Toast.LENGTH_SHORT).show();
            }
        } else if (api == Const.APICategory.UNSET_FOLLOW) {
            API3.Util.UnsetFollowLocalCode postUnfollowLocalCode = API3.Impl.getRepository().UnsetFollowParameterRegex(user_id);
            if (postUnfollowLocalCode == null) {
                mPresenter.postFollow(api, API3.Util.getUnsetFollowAPI(user_id), user_id);
            } else {
                Toast.makeText(this, API3.Util.UnsetFollowLocalCodeMessageTable(postUnfollowLocalCode), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgress.setVisibility(View.INVISIBLE);
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
    public void followSuccess(Const.APICategory api, String user_id) {

    }

    @Override
    public void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);

        if (mSearchView.isSearchOpen()) {
            if (api == Const.APICategory.SET_FOLLOW) {
                mSearchList.get(mSearchUser_idList.indexOf(user_id)).setFollow_flag(false);
            } else if (api == Const.APICategory.UNSET_FOLLOW) {
                mSearchList.get(mSearchUser_idList.indexOf(user_id)).setFollow_flag(true);
            }
            mUserSearchAdapter.notifyItemChanged(mSearchUser_idList.indexOf(user_id));
        } else {
            if (api == Const.APICategory.SET_FOLLOW) {
                mRankusers.get(mRankUser_idList.indexOf(user_id)).setFollow_flag(false);
            } else if (api == Const.APICategory.UNSET_FOLLOW) {
                mRankusers.get(mRankUser_idList.indexOf(user_id)).setFollow_flag(true);
            }
            mUserSearchAdapter.notifyItemChanged(mRankUser_idList.indexOf(user_id));
        }

        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        if (mSearchView.isSearchOpen()) {
            if (api == Const.APICategory.SET_FOLLOW) {
                mSearchList.get(mSearchUser_idList.indexOf(user_id)).setFollow_flag(false);
            } else if (api == Const.APICategory.UNSET_FOLLOW) {
                mSearchList.get(mSearchUser_idList.indexOf(user_id)).setFollow_flag(true);
            }
            mUserSearchAdapter.notifyItemChanged(mSearchUser_idList.indexOf(user_id));
        } else {
            if (api == Const.APICategory.SET_FOLLOW) {
                mRankusers.get(mRankUser_idList.indexOf(user_id)).setFollow_flag(false);
            } else if (api == Const.APICategory.UNSET_FOLLOW) {
                mRankusers.get(mRankUser_idList.indexOf(user_id)).setFollow_flag(true);
            }
            mUserSearchAdapter.notifyItemChanged(mRankUser_idList.indexOf(user_id));
        }

        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());

    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<SearchUserData> list, ArrayList<String> user_ids) {
        switch (api) {
            case GET_USERNAME:
                mSearchList.clear();
                mSearchList.addAll(list);
                mSearchUser_idList.clear();
                mSearchUser_idList.addAll(user_ids);
                mUserSearchAdapter.setData(mSearchList);
                break;
            case GET_FOLLOWER_RANK_FIRST:
                mRankusers.clear();
                mRankusers.addAll(list);
                mRankUser_idList.clear();
                mRankUser_idList.addAll(user_ids);
                mUserSearchAdapter.setData(mRankusers);
                break;
            case GET_FOLLOWER_RANK_ADD:
                if (list.size() != 0) {
                    mRankusers.addAll(list);
                    mRankUser_idList.addAll(user_ids);
                    mUserSearchAdapter.setData(mRankusers);
                    mNextCount++;
                } else {
                    isEndScrioll = true;
                }
                break;
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_FOLLOWER_RANK_FIRST:
                mPresenter.getListData(Const.APICategory.GET_FOLLOWER_RANK_FIRST, API3.Util.getGetFollowerRankAPI(null));
                break;
            case GET_FOLLOWER_RANK_ADD:
                mPresenter.getListData(Const.APICategory.GET_FOLLOWER_RANK_ADD, API3.Util.getGetFollowerRankAPI(String.valueOf(mNextCount)));
                break;
            default:
                break;
        }
    }
}
