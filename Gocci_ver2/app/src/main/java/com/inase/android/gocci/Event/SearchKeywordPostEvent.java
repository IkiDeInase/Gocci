package com.inase.android.gocci.Event;


public class SearchKeywordPostEvent {
    public String searchWord;
    public double mLat;
    public double mLon;

    public SearchKeywordPostEvent(final String searchWord, double lat, double lon) {
        super();
        this.searchWord = searchWord;
        this.mLat = lat;
        this.mLon = lon;
    }
}
