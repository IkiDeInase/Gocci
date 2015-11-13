package com.inase.android.gocci.datasource.repository;

/**
 * Created by kinagafuji on 15/11/10.
 */
public interface CheckRegIdRepository {

    void checkRegId(String url, CheckRegIdRepositoryCallback cb);

    interface CheckRegIdRepositoryCallback {
        void onSuccess();

        void onFailureCausedByLocalError(String id, String errorMessage);

        void onFailureCausedByGlobalError(API3.Util.GlobalCode globalCode);
    }
}
