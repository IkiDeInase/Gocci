package com.inase.android.gocci.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.inase.android.gocci.Base.BaseFragment;
import com.inase.android.gocci.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.timessquare.CalendarPickerView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LifelogFragment extends BaseFragment {

    //ライフログ画面　カレンダーベースで自分の食事の記録ができる。

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TAG_USER_NAME = "name";

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sLifelogUrl = "http://api-gocci.jp/lifelogs/";

    private ArrayList<String> users = new ArrayList<>();

    public String mName;
    public String pictureImageUrl;

    private String updateYear;

    private Calendar memory = Calendar.getInstance();

    private static final String TAG_YEAR = "year";
    private static final String TAG_MONTH = "month";
    private static final String TAG_DAY = "day";
    private static final String TAG_HOUR = "hour";
    private static final String TAG_MINUTE = "minute";

    private CalendarPickerView calendar;

    private SyncHttpClient httpClient;
    private RequestParams loginParam;

    private Calendar thisYear = Calendar.getInstance();
    private Calendar lastYear = Calendar.getInstance();

    public LifelogFragment newIntent(String name, String imageUrl) {
        LifelogFragment fragment = new LifelogFragment();
        Bundle args = new Bundle();
        args.putString(TAG_USER_NAME, name);
        if (imageUrl != null) {
            args.putString(KEY_IMAGE_URL, imageUrl);
        }
        fragment.setArguments(args);
        return fragment;
    }

    CalendarPickerView.OnInvalidDateSelectedListener invalid = new CalendarPickerView.OnInvalidDateSelectedListener() {
        @Override
        public void onInvalidDateSelected(Date date) {

        }
    };

    CalendarPickerView.DateSelectableFilter filter = new CalendarPickerView.DateSelectableFilter() {
        @Override
        public boolean isDateSelectable(Date date) {

            String newMonth = null;
            String newDay = null;
            int day = date.getDate();
            int month = date.getMonth() + 1;
            int year = date.getYear();
            if (year == 114) {
                updateYear = "2014";
            } else if (year == 115) {
                updateYear = "2015";
            }

            if (month > 0 && month < 10) {
                newMonth = "0" + month;
            } else {
                newMonth = String.valueOf(month);
            }
            if (day > 0 && day < 10) {
                newDay = "0" + day;
            } else {
                newDay = String.valueOf(day);
            }

            String lifelog_date = updateYear + "-" + newMonth + "-" + newDay + " ";

            if (users.indexOf(lifelog_date) != -1) {

                int number = users.indexOf(lifelog_date);

                LifelogVideoFragment2 fragment
                        = LifelogVideoFragment2.newInstance(
                        2,
                        4.0f,
                        true,
                        false,
                        false,
                        mName,
                        users.get(number)
                );
                fragment.show(getActivity().getSupportFragmentManager(), "blur_sample");
            }

            return false;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View view2 = getActivity().getLayoutInflater().inflate(R.layout.fragment_lifelog,
                container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        mName = pref.getString("name", null);

        loginParam = new RequestParams("user_name", mName);

        thisYear.add(Calendar.YEAR, 0);
        thisYear.add(Calendar.MONTH, 0);
        thisYear.add(Calendar.DAY_OF_MONTH, 1);

        lastYear.clear();
        lastYear.add(Calendar.YEAR, 44);
        lastYear.add(Calendar.MONTH, 8);
        lastYear.add(Calendar.DAY_OF_MONTH, 8);

        new LifelogAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        calendar = (CalendarPickerView) view2.findViewById(R.id.calendar_view);

        calendar.init(lastYear.getTime(), thisYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
                .withSelectedDate(new Date());

        return view2;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        //Bundle args = getArguments();
        //mName = args.getString(TAG_USER_NAME);
        //pictureImageUrl = args.getString(KEY_IMAGE_URL);

    }

    class LifelogAsyncTask extends AsyncTask<String, String, ArrayList<Date>> {

        private ArrayList<Date> dates = new ArrayList<>();
        private int newYear;
        private int newDay;

        @Override
        protected ArrayList<Date> doInBackground(String... params) {
            httpClient = new SyncHttpClient();
            httpClient.post(getActivity(), sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    httpClient.get(getActivity(), sLifelogUrl, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                            users.clear();
                            try {
                                for (int i = 0; i < timeline.length(); i++) {
                                    JSONObject jsonObject = timeline.getJSONObject(i);

                                    Integer year = jsonObject.getInt(TAG_YEAR);
                                    Integer month = jsonObject.getInt(TAG_MONTH);
                                    Integer day = jsonObject.getInt(TAG_DAY);
                                    Integer hour = jsonObject.getInt(TAG_HOUR);
                                    Integer minute = jsonObject.getInt(TAG_MINUTE);
                                    String stringYear = jsonObject.getString(TAG_YEAR);
                                    String stringMonth = jsonObject.getString(TAG_MONTH);
                                    String stringDay = jsonObject.getString(TAG_DAY);

                                    String jsonDate = stringYear + "-" + stringMonth + "-" + stringDay;

                                    if (users.indexOf(jsonDate) == -1) {
                                        users.add(jsonDate);

                                        //YEAR　今年　０　来年　１　去年　−１
                                        //MONTH　今月　０　来月　１
                                        //DAY　JSON日にちー今日
                                        switch (year) {
                                            case 2014:
                                                newYear = 2014 - 1970;
                                                break;
                                            case 2015:
                                                newYear = 2015 - 1970;
                                                break;
                                        }

                                        newDay = day - 1;

                                        memory.clear();
                                        memory.add(Calendar.YEAR, newYear);
                                        memory.add(Calendar.MONTH, month - 1);
                                        memory.add(Calendar.DAY_OF_MONTH, newDay);
                                        //memory.add(Calendar.HOUR_OF_DAY, hour);
                                        //memory.add(Calendar.MINUTE, minute);
                                        //memory.add(Calendar.SECOND,i);
                                        dates.add(memory.getTime());
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                            Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
                }
            });

            return dates;
        }

        @Override
        protected void onPostExecute(ArrayList<Date> result) {

            calendar.init(lastYear.getTime(), thisYear.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
                    .withSelectedDates(result);

            calendar.setOnInvalidDateSelectedListener(invalid);
            calendar.setDateSelectableFilter(filter);
        }
    }


}
