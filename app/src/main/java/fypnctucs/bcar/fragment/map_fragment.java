package fypnctucs.bcar.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fypnctucs.bcar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class map_fragment extends Fragment implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

    public map_fragment() {
        // Required empty public constructor
    }

    private View layout;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;

    private MapView mapView;
    private GoogleMap map;

    private Location currentLocation;

    private Marker currentLoactionMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_map, container, false);

        configGoogleApiClient();
        configLocationRequest();
        locationServiceInitial();

        if (!googleApiClient.isConnected())
            googleApiClient.connect();

        MapInti(savedInstanceState);

        ((Button) (layout.findViewById(R.id.Test))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = lms.getLastKnownLocation(bestProvider);
                if (location != null) {
                    currentLocation = location;
                    showLocation(currentLocation);
                }
            }
        });

        // Inflate the layout for this fragment
        return layout;
    }

    private void MapInti(Bundle state) {
        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.onCreate(state);
        mapView.onResume();

        MapsInitializer.initialize(getActivity());

        map = mapView.getMap();

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void mapFocus(Location location) {
        if (currentLoactionMarker == null) {
            MarkerOptions tmp = new MarkerOptions();
            tmp.title("now");
            tmp.draggable(false);
            tmp.position(new LatLng(location.getLatitude(), location.getLongitude()));

            currentLoactionMarker = map.addMarker(tmp);

            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18)
                    .bearing(90)
                    .tilt(30)
                    .build()));
        } else {
            currentLoactionMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(90)
                    .tilt(30)
                    .build()));
        }



    }

    private void locationServiceInitial() {
        lms = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = lms.getBestProvider(criteria, true);
    }

    private void showLocation(Location location) {
        mapFocus(location);
        Toast.makeText(getActivity(), location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }


    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
    }

    // LocationListener implements
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        showLocation(location);
    }

    // Connection Callback
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("DEBUG", "google services Connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Google Services連線中斷. Code:"+i, Toast.LENGTH_LONG).show();
    }

    // OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        if (errorCode == ConnectionResult.SERVICE_MISSING)
            Toast.makeText(getActivity(), "裝置沒有Google Services", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "Google Services連線失敗. Code:"+errorCode, Toast.LENGTH_LONG).show();
    }

}
