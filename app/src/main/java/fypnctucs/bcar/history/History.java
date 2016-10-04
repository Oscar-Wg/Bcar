package fypnctucs.bcar.history;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class History {
    private String date;
    private double lng, lat;

    public History() {

    }
    public History(String date, double lat, double lng) {
        this.date = date;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getDate() {
        return date;
    }
}
