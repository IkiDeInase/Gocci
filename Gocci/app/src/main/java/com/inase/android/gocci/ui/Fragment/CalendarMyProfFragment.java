package com.inase.android.gocci.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.ProfJsonEvent;
import com.inase.android.gocci.ui.activity.MyprofActivity;
import com.inase.android.gocci.utils.calendar.LifelogDecorator;
import com.squareup.otto.Subscribe;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 16/02/08.
 */
public class CalendarMyProfFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.calendar_view)
    CalendarPickerView mCalendar;

    private AppBarLayout appBarLayout;

    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();

    private ArrayList<Date> dateList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> thumbnailMap = new HashMap<>();
    private HashMap<String, ArrayList<PostData>> postDataMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> postIdMap = new HashMap<>();

    private MyprofActivity activity;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);

        applicationGocci = (Application_Gocci) getActivity().getApplication();
        activity = (MyprofActivity) getActivity();

        final Calendar nextYear = Calendar.getInstance();

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        ViewCompat.setNestedScrollingEnabled(mCalendar, true);
        mCalendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        BusHolder.get().unregister(this);
        appBarLayout.removeOnOffsetChangedListener(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void subscribe(ProfJsonEvent event) {
        mUsers.clear();
        mUsers.addAll(event.mData);
        mPost_ids.clear();
        mPost_ids.addAll(event.mPost_Ids);
        thumbnailMap.clear();
        dateList.clear();

        if (mUsers.isEmpty()) {
            mCalendar.setVisibility(View.INVISIBLE);
        } else {
            mCalendar.setVisibility(View.VISIBLE);
        }

        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String post_date = null;
        ArrayList<String> thumbnails = null;
        ArrayList<PostData> postDatas = null;
        for (PostData data : mUsers) {
            try {
                dateList.add(dateTimeFormat.parse(data.getPost_date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (post_date == null) {
                post_date = data.getPost_date();
                thumbnails = new ArrayList<>();
                postDatas = new ArrayList<>();
            }

            if (post_date.equals(data.getPost_date())) {
                thumbnails.add(data.getThumbnail());
                postDatas.add(data);
            } else {
                thumbnailMap.put(data.getPost_date(), thumbnails);
                postDataMap.put(data.getPost_date(), postDatas);
                post_date = null;
            }
        }
        if (post_date != null) {
            thumbnailMap.put(post_date, thumbnails);
            postDataMap.put(post_date, postDatas);
        }
        mCalendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new LifelogDecorator(getActivity(), thumbnailMap)));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        try {
            mCalendar.init(mUsers.isEmpty() ? Calendar.getInstance().getTime() : dateTimeFormat.parse(mUsers.get(mUsers.size() - 1).getPost_date()), calendar.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mCalendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (dateList.contains(date)) {
                    LifelogDialogFragment fragment = LifelogDialogFragment.newInstance(postDataMap.get(dateTimeFormat.format(date)));
                    fragment.show(getActivity().getSupportFragmentManager(), "lifelog");
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                if (dateList.contains(date)) {
                    LifelogDialogFragment fragment = LifelogDialogFragment.newInstance(postDataMap.get(dateTimeFormat.format(date)));
                    fragment.show(getActivity().getSupportFragmentManager(), "lifelog");
                }
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
}
