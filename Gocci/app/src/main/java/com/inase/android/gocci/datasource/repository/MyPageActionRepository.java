package com.inase.android.gocci.datasource.repository;

import java.io.File;

/**
 * Created by kinagafuji on 15/09/30.
 */
public interface MyPageActionRepository {
    void changeProfile(String post_date, File file, String url, MyPageActionRepositoryCallback cb);

    void deletePost(String post_id, int position, MyPageActionRepositoryCallback cb);

    interface MyPageActionRepositoryCallback {
        void onProfileChanged(String userName, String profile_img);

        void onProfileChangeFailed(String message);

        void onPostDeleted(int position);

        void onPostDeleteFailed();

        void onError();
    }
}
