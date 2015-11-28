package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/25.
 */
public interface NearDataUseCase {
    interface NearDataUseCaseCallback {
        void onLoaded(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray);

        void onEmpty(Const.APICategory api);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, NearDataUseCaseCallback callback);

    void setCallback(NearDataUseCaseCallback callback);

    void removeCallback();
}
