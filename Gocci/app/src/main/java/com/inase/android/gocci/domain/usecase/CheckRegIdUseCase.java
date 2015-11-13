package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.API3;

/**
 * Created by kinagafuji on 15/11/10.
 */
public interface CheckRegIdUseCase {

    interface CheckRegIdUseCaseCallback {
        void onSuccess();

        void onFailureCausedByLocalError(String id, String errorMessage);

        void onFailureCausedByGlobalError(API3.Util.GlobalCode globalCode);
    }

    void execute(String url, CheckRegIdUseCaseCallback callback);

    void setCallback(CheckRegIdUseCaseCallback callback);

    void removeCallback();
}
