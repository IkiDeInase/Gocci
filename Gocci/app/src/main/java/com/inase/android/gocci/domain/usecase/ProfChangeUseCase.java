package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.data.PostData;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/01.
 */
public interface ProfChangeUseCase {

    interface ProfChangeUseCaseCallback {
        void onProfChanged(String userName, String profile_img);

        void onProfChangeFailed(String message);

        void onError();
    }

    void execute(String post_date, File file, String url, ProfChangeUseCaseCallback callback);

    void setCallback(ProfChangeUseCaseCallback callback);

    void removeCallback();
}
