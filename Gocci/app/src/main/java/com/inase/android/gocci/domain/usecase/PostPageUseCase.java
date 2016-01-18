package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.PostData;

/**
 * Created by kinagafuji on 16/01/18.
 */
public interface PostPageUseCase {
    interface PostPageUseCaseCallback {
        void onPostLoaded(Const.APICategory api, PostData mPostData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, PostPageUseCaseCallback callback);

    void setCallback(PostPageUseCaseCallback callback);

    void removeCallback();
}
