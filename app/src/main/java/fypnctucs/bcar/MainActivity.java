package fypnctucs.bcar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fypnctucs.bcar.ble.BLEClient;
import fypnctucs.bcar.fragment.about_fragment;
import fypnctucs.bcar.fragment.list_fragment;
import fypnctucs.bcar.fragment.map_fragment;
import fypnctucs.bcar.fragment.notification_fragment;
import fypnctucs.bcar.fragment.setting_fragment;
import fypnctucs.bcar.fragment.warming_fragment;
import fypnctucs.bcar.history.History;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private Fragment list_fragment, map_fragment, notification_fragment, setting_fragment, about_fragment;

    public BLEClient mBLEClient;
    private LocationManager mLocationManager;

    public int fragmentPos;
    private boolean warming;

    IService_ble_connection iService_ble_connection;
    private BleServiceConnection bleServiceConnection = new BleServiceConnection();
    private boolean isBound = false;

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android M permission setting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionRequest();
        }

        Log.d("start?", isServiceRunning(Service_ble_connection.class)+"");

        if (!isServiceRunning(Service_ble_connection.class))
            startService(new Intent(this, Service_ble_connection.class));

        bindService(new Intent(this, Service_ble_connection.class), bleServiceConnection, Service.BIND_AUTO_CREATE);

        geocoder = new Geocoder(this);

        // location init
        LocationServiceInit();

        // ble client init
        warming = false;
        mBLEClient = new BLEClient(this);
        checkBluetooth();

        // action bar inti
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // drawer layout init
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // left nav init
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // fragment init
        list_fragment = map_fragment = notification_fragment = setting_fragment = about_fragment = null;
        if (savedInstanceState == null) {
            fragmentPos = 0;
            selectItem(0);
        }
    }

    public list_fragment getListFragment() {
        return (list_fragment)list_fragment;
    }

    // binder controller
    private final IMainActivity.Stub binder = new IMainActivity.Stub() {
        @Override
        public void callStatus(String msg) throws RemoteException {
            MainActivity.this.status(msg);
        }
    };

    @Override
    protected void onResume() {

        if (warming && mBLEClient.isEnabled()) {
            warming = false;
            selectItem(fragmentPos);
        }
        if (!mBLEClient.isEnabled() && fragmentPos != 4 && fragmentPos != 5)
            checkBluetooth();

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(bleServiceConnection);
            isBound = false;
        }
    }

    //  Ble Service Connection Listener
    private class BleServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iService_ble_connection = IService_ble_connection.Stub.asInterface(service);
            try {
                iService_ble_connection.setBinder(binder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isBound = true;
            Log.d("onServiceConnected", "Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService_ble_connection = null;
            isBound = false;
            Log.d("onServiceDisconnected", "Disconnected");
        }
    }

    // nav switch crl
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (warming && mBLEClient.isEnabled()) {
            warming = false;
        } else if (item.isChecked()) return true;

        int id = item.getItemId();

        if (id == R.id.navigation_list) {
            selectItem(0);
        } else if (id == R.id.navigation_notification) {
            selectItem(1);
        } else if (id == R.id.navigation_map) {
            selectItem(2);
        } else if (id == R.id.navigation_setting) {
            selectItem(3);
        } else if (id == R.id.navigation_about) {
            selectItem(4);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectItem(int position) {
        Fragment fragment;

        switch (position) {
            case 1:
                if (notification_fragment == null)
                    notification_fragment = new notification_fragment();
                fragment = notification_fragment;
                break;
            case 2:
                if (map_fragment == null)
                    map_fragment = new map_fragment();
                fragment = map_fragment;
                break;
            case 3:
                if (setting_fragment == null)
                    setting_fragment = new setting_fragment();
                fragment = setting_fragment;
                break;
            case 4:
                if (about_fragment == null)
                    about_fragment = new about_fragment();
                fragment = about_fragment;
                break;
            default:
                if (list_fragment == null)
                    list_fragment = new list_fragment();
                fragment = list_fragment;
        }


        if (!warming || position == 3 || position == 4) {

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment, fragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            setActionBarTitle(position);
        } else if (warming)
            checkBluetooth();

        fragmentPos = position;
    }

    private void setActionBarTitle(int position) {
        String title;
        switch (position) {
            case 1:
                title = getResources().getString(R.string.navigation_notification);
                break;
            case 2:
                title = getResources().getString(R.string.navigation_map);
                break;
            case 3:
                title = "設定";
                break;
            case 4:
                title = "關於";
                break;
            default:
                title = getResources().getString(R.string.navigation_list);
        }

        ((Toolbar)findViewById(R.id.toolbar)).setTitle(title);

    }

    //Location Manager
    private void LocationServiceInit() {
        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (googlePlayStatus != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
            finish();
        }
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    public void getCurrentLocation(int LOCATION_UPDATE_MIN_TIME, int LOCATION_UPDATE_MIN_DISTANCE, LocationListener mLocationListener) {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGPSEnabled || isNetworkEnabled))
            status("error_location_provider");
        else {
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;
            }
        }
    }

    public void StopLocationListener(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mLocationManager.removeUpdates(locationListener);
    }

    // bluetooth warming crl
    public void setWarming(boolean warming) {
        this.warming = warming;
    }

    public void checkBluetooth() {
        if (!mBLEClient.isEnabled()) {
            warming = true;
            ((Toolbar)findViewById(R.id.toolbar)).setTitle("請開啟藍牙");
            Fragment fragment = new warming_fragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment, fragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    protected Geocoder geocoder;

    public void findAddress(final History item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "";
                rwl.lock();
                try {
                    address = geocoder.getFromLocation(item.getLat(), item.getLng(), 1).get(0).getAddressLine(0);
                } catch (IOException e) {
                    address = "...";
                    e.printStackTrace();
                } finally {
                    item.setAddress(address);
                    ((list_fragment)list_fragment).History_update(item);
                }
                rwl.unlock();
                item.busy = false;
            }
        }).start();

    }
    // msg handler
    private class LocationTextView {
        protected TextView view;
        protected String address;
        LocationTextView(String address, TextView view) {
            this.address = address;
            this.view = view;
        }
    }
    public void status(String msg) {
        mHandler.obtainMessage(TOAST_STATUS, msg).sendToTarget();
    }
    public void getLocation(LocationListener locationListener) {
        mHandler.obtainMessage(BLEDEVICE_REFRESH_LOCATION, locationListener).sendToTarget();
    }

    protected final static int TOAST_STATUS = 103;
    protected final static int SET_TEXT = 99;
    final static int BLEDEVICE_REFRESH_LOCATION = 105;
    final static int POST = 106;
    static Lock rwl = new ReentrantLock();

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_TEXT:
                    ((LocationTextView)msg.obj).view.setText(((LocationTextView)msg.obj).address);
                    break;
                case TOAST_STATUS:
                    Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case BLEDEVICE_REFRESH_LOCATION:
                    getCurrentLocation(0, 0, (LocationListener) msg.obj);
                    break;
            }
        }
    };

    // Android M permission setting
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    @TargetApi(Build.VERSION_CODES.M)
    private void permissionRequest() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("應用程式需要存取你的位置資訊");
            builder.setPositiveButton(android.R.string.ok, null);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });

            builder.show();

        }
    }
}

