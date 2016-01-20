package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.NoticeRepository;
import com.inase.android.gocci.datasource.repository.NoticeRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.usecase.NoticeDataUseCase;
import com.inase.android.gocci.domain.usecase.NoticeDataUseCaseImpl;
import com.inase.android.gocci.presenter.ShowNoticePresenter;
import com.inase.android.gocci.ui.activity.PostActivity;
import com.inase.android.gocci.ui.activity.UserProfActivity;
import com.inase.android.gocci.ui.adapter.NoticeAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/05/12.
 */
public class NotificationListView extends RelativeLayout implements
        ShowNoticePresenter.ShowNoticeView, NoticeAdapter.NoticeCallback {

    private ListView mNotificationList;
    private ProgressWheel mNotificationProgress;
    private NoticeAdapter mNoticeAdapter;
    private ArrayList<HeaderData> mNotificationUsers = new ArrayList<>();

    private TextView mEmpty_text;
    private ImageView mEmpty_image;

    private ShowNoticePresenter mPresenter;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    public NotificationListView(final Context context) {
        super(context);

        final API3 api3Impl = API3.Impl.getRepository();
        NoticeRepository noticeRepositoryImpl = NoticeRepositoryImpl.getRepository(api3Impl);
        NoticeDataUseCase noticeDataUseCaseImpl = NoticeDataUseCaseImpl.getUseCase(noticeRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowNoticePresenter(noticeDataUseCaseImpl);
        mPresenter.setNoticeView(this);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_notification_list, this);

        applicationGocci = (Application_Gocci) context;

        mNotificationProgress = (ProgressWheel) inflateView.findViewById(R.id.progress);
        mNotificationList = (ListView) inflateView.findViewById(R.id.notification_list);

        mEmpty_image = (ImageView) inflateView.findViewById(R.id.empty_image);
        mEmpty_text = (TextView) inflateView.findViewById(R.id.empty_text);

        mNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HeaderData user = mNotificationUsers.get(position);

                switch (user.getNotice()) {
                    case "like":
                        PostActivity.startPostActivityOnContext(user.getNotice_post_id(), getContext());
                        break;
                    case "follow":
                        Intent intent = new Intent(getContext(), UserProfActivity.class);
                        intent.putExtra("user_id", user.getUser_id());
                        intent.putExtra("user_name", user.getUsername());
                        getContext().startActivity(intent);
                        break;
                    case "comment":
                        PostActivity.startPostActivityOnContext(user.getNotice_post_id(), getContext());
                        break;
                }
            }
        });

        mNotificationList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_TOUCH_SCROLL:
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("Notice");
                        mTracker.send(new HitBuilders.EventBuilder().setAction("ScrollCount").setCategory("Public").setLabel(SavedData.getServerUserId(context)).build());
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        API3.Util.GetNoticeLocalCode localCode = api3Impl.GetNoticeParameterRegex();
        if (localCode == null) {
            mPresenter.getNoticeData(Const.APICategory.GET_NOTICE_FIRST, API3.Util.getGetNoticeAPI());
        } else {
            Toast.makeText(context, API3.Util.GetNoticeLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public void resume() {
        mPresenter.resume();
    }

    public void pause() {
        mPresenter.pause();
    }

    //xmlからの生成用
    public NotificationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onUserClick(String user_id, String username) {
        Intent intent = new Intent(getContext(), UserProfActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("user_name", username);
        getContext().startActivity(intent);
    }

    @Override
    public void showLoading() {
        mNotificationProgress.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoading() {
        mNotificationProgress.setVisibility(INVISIBLE);
    }

    @Override
    public void showNoResultCase(Const.APICategory api) {
        mEmpty_image.setVisibility(View.VISIBLE);
        mEmpty_text.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoResultCase() {
        mEmpty_image.setVisibility(View.INVISIBLE);
        mEmpty_text.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(getContext(), api, globalCode);
        mNotificationProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        mNotificationProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<HeaderData> list) {
        mNotificationUsers.addAll(list);
        mNoticeAdapter = new NoticeAdapter(getContext(), 0, mNotificationUsers);
        mNoticeAdapter.setNoticeCallback(this);
        mNotificationList.setAdapter(mNoticeAdapter);
    }
}
