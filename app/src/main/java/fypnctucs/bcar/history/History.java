package fypnctucs.bcar.history;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class History {
    private long id;
    private String address;
    private String btaddress;
    private String date;
    private double lng, lat;
    public boolean busy = false;

    public History() {

    }
    public History(String btaddress, String date, double lat, double lng, String address) {
        this.btaddress = btaddress;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAddress() {
        return address;
    }
}
