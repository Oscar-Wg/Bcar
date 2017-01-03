package fypnctucs.bcar.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import fypnctucs.bcar.DataFormat;
import fypnctucs.bcar.MainActivity;
import fypnctucs.bcar.Map_Controller;
import fypnctucs.bcar.R;
import fypnctucs.bcar.device.BleDevice;
import fypnctucs.bcar.device.BleDeviceDAO;
import fypnctucs.bcar.history.HistoryDAO;

/**
 * A simple {@link Fragment} subclass.
 */
public class map_fragment extends Fragment{

    public map_fragment() {
        // Required empty public constructor
    }

    private View layout;

    private Map_Controller map;
    private MapView mapView;

    private BleDeviceDAO bleDeviceDAO;
    private ArrayList<BleDevice> devicesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_map, container, false);

        if (map==null) {
            bleDeviceDAO = new BleDeviceDAO(getActivity().getApplicationContext());
        }
        devicesList = null;
        devicesList = bleDeviceDAO.getAll();

        mapView = (MapView)layout.findViewById(R.id.map);
        map = null;
        map = new Map_Controller(getActivity(), mapView);

        for (int i=0; i<devicesList.size(); i++) {
            map.InsertMarker(new LatLng(devicesList.get(i).getLast_lat(), devicesList.get(i).getLast_lng()),
                    devicesList.get(i).getName() + " 停在這裡",
                    DataFormat.CONNECT_DEVICE_ICON[devicesList.get(i).getType()],
                    true);
        }

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.mylocation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getCurrentLocation(0, 0, MapOneTimeLocationListener);
            }
        });

        ((MainActivity)getActivity()).getCurrentLocation(0, 0, MapOneTimeLocationListener);

        // Inflate the layout for this fragment
        return layout;
    }

    // LocationListener
    private android.location.LocationListener MapOneTimeLocationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                map.showCurrentLocation(location);
                ((MainActivity)getActivity()).StopLocationListener(this);
            } else {
                Log.d("onLocationChanged", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

}
