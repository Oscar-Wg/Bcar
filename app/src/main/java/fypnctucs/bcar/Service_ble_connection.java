package fypnctucs.bcar;

import android.Manifest;
import android.app.Activity;
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
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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

    private final IBinder serviceBinder = new ServiceBinder();

    private LocationManager locationManager;
    private String provider;
    private Location location;

    private NotificationManager manager;
    private NotificationCompat.Builder builder;

    public static boolean is_start = false;

    private BleDeviceDAO bleDeviceDAO;

    private Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BCAR Service", "location tracking onCreate()");

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BCAR Service", "location tracking onStartCommand()");
        Log.d("service flags", flags+" "+is_start);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_STICKY;
        }
        if (!is_start) {
            is_start = true;
            locationManager.requestLocationUpdates(provider, 30000, 0, this);

            bleDeviceDAO = new BleDeviceDAO(getApplicationContext());
        }

        List<BleDevice> devices = bleDeviceDAO.getAll();

        for (int i=0; i<devices.size(); i++) {
            BleDevice device = devices.get(i);
            Log.d("bleDevice", device.getDevice().getAddress() + " " + device.getLast_lng() + " " + device.getLast_lat());
        }

        return START_NOT_STICKY;
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
    public IBinder onBind(Intent intent) {
        Log.d("BCAR Service", "location tracking onBind()");
        return serviceBinder;
    }

    public class ServiceBinder extends Binder {
        Service_ble_connection getService() {
            return Service_ble_connection.this;
        }
    }

    public void updateLocation() {
        Log.d("updateLocation", location.toString());

        builder.setContentText("updateLocation " + location.getLongitude() + " " + location.getLatitude());
        Notification notification = builder.build();
        manager.notify(NOTIFICATION_ID, notification);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
