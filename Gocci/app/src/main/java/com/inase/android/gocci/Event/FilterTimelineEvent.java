package com.inase.android.gocci.event;

/**
 * Created by kinagafuji on 15/08/30.
 */
public class FilterTimelineEvent {

    public int currentPage;
    public String filterUrl;

    public FilterTimelineEvent(int currentPage, String filterUrl) {
        super();
        this.currentPage = currentPage;
        this.filterUrl = filterUrl;
    }
}
