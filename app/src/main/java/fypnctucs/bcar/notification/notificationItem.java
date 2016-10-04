package fypnctucs.bcar.notification;

import fypnctucs.bcar.R;

/**
 * Created by kamfu.wong on 30/9/2016.
 */

public class notificationItem {

    private String msg;
    private String date;
    private int icon;

    private boolean read;

    public notificationItem() {
        this(R.drawable.ic_notification, "--- unknown ---", "unknown");
    }

    public notificationItem(int icon, String date, String msg) {
        this.icon = icon;
        this.date = date;
        this.msg = msg;
        read = false;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() {
        return date;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isRead() {
        return read;
    }

}
