package com.inase.android.gocci.event;

import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/21.
 */
public class ProfJsonEvent {

    public int mApi;
    public ArrayList<PostData> mData;
    public ArrayList<String> mPost_Ids;

    public ProfJsonEvent(int api, ArrayList<PostData> data, ArrayList<String> post_ids) {
        super();
        this.mApi = api;
        this.mData= data;
        this.mPost_Ids = post_ids;
    }
}
