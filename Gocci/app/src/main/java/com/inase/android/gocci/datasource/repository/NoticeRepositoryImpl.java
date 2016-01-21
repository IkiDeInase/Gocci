package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class NoticeRepositoryImpl implements NoticeRepository {
    private static NoticeRepositoryImpl sNoticeRepository;
    private final API3 mAPI3;
    private long startTime;

    public NoticeRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static NoticeRepositoryImpl getRepository(API3 api3) {
        if (sNoticeRepository == null) {
            sNoticeRepository = new NoticeRepositoryImpl(api3);
        }
        return sNoticeRepository;
    }

    @Override
    public void getNotice(final Const.APICategory api, String url, final NoticeRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            startTime = System.currentTimeMillis();
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                    tracker.send(new HitBuilders.TimingBuilder()
                            .setCategory("System")
                            .setVariable(api.name())
                            .setLabel(SavedData.getServerUserId(Application_Gocci.getInstance()))
                            .setValue(System.currentTimeMillis() - startTime).build());
                    mAPI3.GetNoticeResponse(response, new API3.PayloadResponseCallback() {

                        @Override
                        public void onSuccess(JSONObject payload) {
                            try {
                                final ArrayList<HeaderData> mListData = new ArrayList<>();

                                JSONArray notices = payload.getJSONArray("notices");
                                if (notices.length() != 0) {
                                    for (int i = 0; i < notices.length(); i++) {
                                        JSONObject listData = notices.getJSONObject(i);
                                        mListData.add(HeaderData.createNoticeHeaderData(listData));
                                    }
                                    cb.onSuccess(api, mListData);
                                } else {
                                    cb.onEmpty(api);
                                }
                            } catch (JSONException e) {
                                cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                            }
                        }

                        @Override
                        public void onGlobalError(API3.Util.GlobalCode globalCode) {
                            cb.onFailureCausedByGlobalError(api, globalCode);
                        }

                        @Override
                        public void onLocalError(String errorMessage) {
                            cb.onFailureCausedByLocalError(api, errorMessage);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}
