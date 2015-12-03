package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.HeatmapRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.utils.map.HeatmapLog;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public class HeatmapUseCaseImpl extends UseCase2<Const.APICategory, String> implements HeatmapUseCase, HeatmapRepository.HeatmapRepositoryCallback {
    private static HeatmapUseCaseImpl sUseCase;
    private final HeatmapRepository mHeatmapRepository;
    private PostExecutionThread mPostExecutionThread;
    private HeatmapUseCaseCallback mCallback;

    public static HeatmapUseCaseImpl getUseCase(HeatmapRepository heatmapRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new HeatmapUseCaseImpl(heatmapRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public HeatmapUseCaseImpl(HeatmapRepository heatmapRepository, PostExecutionThread postExecutionThread) {
        mHeatmapRepository = heatmapRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, HeatmapUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    public void setCallback(HeatmapUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mHeatmapRepository.getHeatmap(param1, param2, this);
    }

    @Override
    public void onSuccess(final Const.APICategory api, final ArrayList<HeatmapLog> heatData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onHeatmapLoaded(api, heatData);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByGlobalError(api, globalCode);
                }
            }
        });
    }
}
