package fypnctucs.bcar;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class dataFormat {
    final static public int SCOOTER = 1;
    final static public int CAR = 2;
    final static public int MOTORCYCLE = 3;
    final static public int OTHER = 0;

    final static public int[] CONNECT_DEVICE_ICON = {R.drawable.ic_ble,
            R.drawable.ic_scooter,
            R.drawable.ic_car,
            R.drawable.ic_motorcycle};

    final static public int[] DISCONNECT_DEVICE_ICON = {R.drawable.ic_ble_disconnect,
            R.drawable.ic_scooter_disconnect,
            R.drawable.ic_car_disconnect,
            R.drawable.ic_motorcycle_disconnect};
}
