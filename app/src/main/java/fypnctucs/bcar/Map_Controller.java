package fypnctucs.bcar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class Map_Controller {

    private MapView mapView;
    private GoogleMap googleMap;

    private Marker currentLocationMarker = null;
    private List<Marker> markers;

    private Activity activity;

    public Map_Controller(Activity activity, MapView mapView) {
        this.activity = activity;
        this.mapView = mapView;
        init();
    }

    private void init() {
        markers = new ArrayList<Marker>();

        mapView.onCreate(new Bundle());
        mapView.onResume();

        MapsInitializer.initialize(activity);
        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public Bitmap resizeMapIcons(int res, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(activity.getResources(), res);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void clearMarkers() {
        for (int i=0; i<markers.size(); i++)
            markers.get(i).remove();
        markers.clear();
    }

    public void InsertMarker(LatLng latlng, String title, int icon ,boolean focus){
        MarkerOptions tmp = new MarkerOptions();
        tmp.title(title);
        tmp.draggable(false);
        tmp.position(latlng);
        if (icon != -1)
            tmp.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(icon, 100, 100)));

        markers.add(googleMap.addMarker(tmp));
        markers.get(markers.size()-1).showInfoWindow();

        if (focus)
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(18)
                    .bearing(90)
                    .build()));
    }

    public void showCurrentLocation(Location location) {
        if (currentLocationMarker == null) {
            MarkerOptions tmp = new MarkerOptions();
            tmp.draggable(false);
            tmp.position(new LatLng(location.getLatitude(), location.getLongitude()));
            tmp.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.ic_currect_location, 50, 50)));

            currentLocationMarker = googleMap.addMarker(tmp);

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18)
                    .bearing(90)
                    .build()));
        } else {
            currentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18)
                    .bearing(90)
                    .build()));
        }
    }

}
