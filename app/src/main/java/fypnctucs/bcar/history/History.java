package fypnctucs.bcar.history;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class History {
    private long id;
    private String btaddress;
    private String date;
    private double lng, lat;

    public History() {

    }
    public History(String btaddress, String date, double lat, double lng) {
        this.btaddress = btaddress;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getBtaddress() {
        return btaddress;
    }

    public long getId() {
        return id;
    }
}
