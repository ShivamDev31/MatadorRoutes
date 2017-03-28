package com.roots;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQ_PERMISSION = 0;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Bundle extras = getIntent().getExtras();
        mMap.setTrafficEnabled(true);
        if (extras != null) {
            LatLng from_stop = new LatLng(extras.getDouble("from_lat"), extras.getDouble("from_long"));
            LatLng to_stop = new LatLng(extras.getDouble("to_lat"), extras.getDouble("to_long"));
            mMap.addMarker(new MarkerOptions().position(from_stop).title("Catch Matador from here"))
                    .showInfoWindow();
            mMap.addMarker(new MarkerOptions().position(to_stop).title("Unboard the Matador here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(from_stop));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mMap.setMyLocationEnabled(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}