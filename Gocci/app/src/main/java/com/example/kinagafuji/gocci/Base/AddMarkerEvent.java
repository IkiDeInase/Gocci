package com.example.kinagafuji.gocci.Base;

public class AddMarkerEvent {
    public double lat;
    public double lon;
    public String restname;

    public AddMarkerEvent(final Double lat, final Double lon, final String restname) {
        super();
        this.lat = lat;
        this.lon = lon;
        this.restname = restname;

    }
}
