package com.nebrasapps.otlb.components;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nebrasapps.otlb.R;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;


/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private double lat = 0.0;
    private double lon = 0.0;
    private LatLng locationLatLng = null;
    private Marker pickupMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle extras = getIntent().getExtras();
        //Getting user location from the RiderHomeFragment class
        if (getIntent().getExtras().containsKey("lat")) {
            lat = extras.getDouble("lat", 0.0);
            lon = extras.getDouble("lon", 0.0);
            locationLatLng = new LatLng(lat, lon);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationLatLng != null) {
                    //passing user selected location lat and lon to RiderHomeFragment result
                    Intent intent = getIntent();
                    intent.putExtra("lat", locationLatLng.latitude);
                    intent.putExtra("lon", locationLatLng.longitude);
                    setResult(20, intent);
                    finish();
                }else
                {
                    Toast.makeText(MapsActivity.this,"Cannot get location",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near UserLocation.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in current user location and move the camera
        LatLng sydney = new LatLng(lat, lon);
        pickupMarker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        if (mMap != null) {
            mMap.setOnMapClickListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Updating marker position when user clicked on map
        if (latLng != null) {
            locationLatLng = latLng;
            if (locationLatLng != null && pickupMarker != null) {
                pickupMarker.setPosition(locationLatLng);
            }
        }
    }
}
