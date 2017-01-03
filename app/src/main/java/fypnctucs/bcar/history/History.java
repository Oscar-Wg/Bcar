package fypnctucs.bcar.history;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class History {
    private long id;
    private String address;
    private String btaddress;
    private String date;
    private String status;
    private double lng, lat;
    public boolean busy = false;

    public History() {

    }
    public History(String btaddress, String date, double lat, double lng, String address, String status) {
        this.btaddress = btaddress;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}
