package com.inase.android.gocci.domain.usecase;

import com.google.android.gms.maps.model.LatLng;
import com.inase.android.gocci.datasource.repository.HeatmapRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public class HeatmapUseCaseImpl extends UseCase<String> implements HeatmapUseCase, HeatmapRepository.HeatmapRepositoryCallback {
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
    public void onHeatmapLoaded(final ArrayList<LatLng> heatData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onHeatmapLoaded(heatData);
                }
            }
        });
    }

    @Override
    public void onError() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onError();
                }
            }
        });
    }

    @Override
    public void execute(String url, HeatmapUseCaseCallback callback) {
        mCallback = callback;
        this.start(url);
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
    protected void call(String params) {
        mHeatmapRepository.getHeatmap(params, this);
    }
}
