package fypnctucs.bcar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import fypnctucs.bcar.ble.BLEClient;
import fypnctucs.bcar.fragment.about_fragment;
import fypnctucs.bcar.fragment.list_fragment;
import fypnctucs.bcar.fragment.map_fragment;
import fypnctucs.bcar.fragment.more_fragment;
import fypnctucs.bcar.fragment.notification_fragment;
import fypnctucs.bcar.fragment.setting_fragment;
import fypnctucs.bcar.fragment.warming_fragment;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private Fragment list_fragment, map_fragment, notification_fragment, more_fragment, setting_fragment, about_fragment;

    public BLEClient mBLEClient;

    private int fragmentPos;

    public ArrayList<BluetoothDevice> foundDevicesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        warming = false;
        foundDevicesArray = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        list_fragment = map_fragment = notification_fragment = more_fragment = setting_fragment = about_fragment = null;

        mBLEClient = new BLEClient(this);

        if (savedInstanceState == null) {
            fragmentPos = 0;
            selectItem(0);
        } else {

        }

        checkBluetooth();
    }

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
        } else if (id == R.id.navigation_more) {
            selectItem(3);
        } else if (id == R.id.navigation_setting) {
            selectItem(4);
        } else if (id == R.id.navigation_about) {
            selectItem(5);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clearScanList() {
        foundDevicesArray.clear();
    }

    private boolean test = false;

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
                if (more_fragment == null)
                    more_fragment = new more_fragment();
                fragment = more_fragment;
                break;
            case 4:
                if (setting_fragment == null)
                    setting_fragment = new setting_fragment();
                fragment = setting_fragment;
                break;
            case 5:
                if (about_fragment == null)
                    about_fragment = new about_fragment();
                fragment = about_fragment;
                break;
            default:
                if (list_fragment == null)
                    list_fragment = new list_fragment();
                fragment = list_fragment;
        }


        if (!warming || position == 4 || position == 5) {

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
                title = getResources().getString(R.string.navigation_more);
                break;
            case 4:
                title = "設定";
                break;
            case 5:
                title = "關於";
                break;
            default:
                title = getResources().getString(R.string.navigation_list);
        }

        ((Toolbar)findViewById(R.id.toolbar)).setTitle(title);

    }

    private boolean warming;
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

    public void status(String msg) {
        mHandler.obtainMessage(TOAST_STATUS, msg).sendToTarget();
    }
    protected final static int TOAST_STATUS = 103;
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST_STATUS:
                    Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
