package fypnctucs.bcar;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import fypnctucs.bcar.device.BleDevice;
import fypnctucs.bcar.device.BleDeviceDAO;

/**
 * Created by kamfu.wong on 28/10/2016.
 */

public class Service_ble_connection extends Service implements LocationListener {

    public final static String COMMAND_KEY = "COMMAND_KEY";
    public final static String COMMAND_START_SERVICE = "START_SERVICE";
    public final static int NOTIFICATION_ID = 57416;

    private boolean isNotification[];

    private LocationManager locationManager;
    private String provider;
    private Location location;

    private NotificationManager manager;
    private NotificationCompat.Builder builder;

    public static boolean is_start = false;

    private BleDeviceDAO bleDeviceDAO;

    private IMainActivity iMainActivity;
    private boolean isBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BCAR Service", "location tracking onCreate()");

        isNotification = new boolean[1000];

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        builder.setSmallIcon(R.drawable.ic_small_icon)
                .setLargeIcon(largeIcon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Bcar");
        int defaults = 0;
        //defaults |= Notification.DEFAULT_VIBRATE;
        defaults |= Notification.DEFAULT_SOUND;
        defaults |= Notification.DEFAULT_LIGHTS;
        builder.setDefaults(defaults);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        updateLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BCAR Service", "location tracking onDestroy()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BCAR Service", "location tracking onStartCommand()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_STICKY;
        }
        if (!is_start) {
            is_start = true;
            locationManager.requestLocationUpdates(provider, 30000, 0, this);
        }

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BCAR Service", "location tracking onBind()");
        isBound = true;
        return binder;
    }

    // binder controller
    private final IService_ble_connection.Stub binder = new IService_ble_connection.Stub() {

        public void setBinder(IBinder activity) throws RemoteException {
            iMainActivity = IMainActivity.Stub.asInterface(activity);
            iMainActivity.callStatus("Service connected!");
        }


    };

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        iMainActivity = null;
        return true;
    }

    // 計算兩點距離
    private final double EARTH_RADIUS = 6378137.0;

    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public void updateLocation() {
        Log.d("updateLocation", location.toString());

        if (bleDeviceDAO == null)
            bleDeviceDAO = new BleDeviceDAO(getApplicationContext());
        List<BleDevice> devices = bleDeviceDAO.getAll();

        for (int i=0; i<devices.size(); i++) {
            BleDevice device = devices.get(i);
            double distance = gps2m(device.getLast_lat(), device.getLast_lng(), location.getLatitude(), location.getLongitude());
            if (distance < 50) {
                if (iMainActivity == null) {
                    if (!isNotification[i]) {
                        isNotification[i] = true;
                        builder.setContentText(device.getName() + "在你附近，可以打開APP找它喔!");
                        Notification notification = builder.build();
                        manager.notify(NOTIFICATION_ID + i, notification);
                    } else {
                        isNotification[i] = false;
                        manager.cancel(NOTIFICATION_ID + i);
                    }
                } else {
                    // if bledevice is not connect, than open scan and help find -- don't finish
                }
            }
        }
    }

    // Location Listener
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        updateLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
