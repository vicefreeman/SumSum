package sumsum.gates.vice.hiday;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AddGate.OnRadiusUpdateListener {

    private GoogleMap mMap;
    String radius;
    private Circle mCircle;
    ArrayList<String> gateData;
    CircleOptions circleOptions;
    LatLng latLng;
    Boolean hasMarker = true;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    Marker marker = null;

    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("shred" , Context.MODE_PRIVATE);
        edit = preferences.edit();
        radius = preferences.getString("radius", "200");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent =getIntent();
        gateData = intent.getStringArrayListExtra("gateData");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = new SupportMapFragment();
        AddGate addGate = new AddGate();
        if (gateData != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("gateData", gateData);
            addGate.setArguments(bundle);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame1, mapFragment)
                .replace(R.id.frame2, addGate)
                .commit();

        //tell me when the map is loaded
        mapFragment.getMapAsync(this);
        //client = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //addMarker(map);

        setMyLocation(map);
        setUpMap(map);

    }


    private void setUpMap(final GoogleMap map) {

        if (gateData == null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation == null) {
                    latLng = new LatLng(32.0844269,34.8029073);
                }else {
                    latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng).title("My Gate").snippet("Is here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .draggable(true));
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(Double.valueOf(radius))
                    .strokeColor(Color.YELLOW)
                    .fillColor(Color.argb(100, 0, 188, 212))
                    .strokeWidth(8).clickable(true);
            circleOptions.center(latLng);
            edit.putString("lat", String.valueOf(marker.getPosition().latitude));
            edit.putString("lng", String.valueOf(marker.getPosition().latitude));
            edit.commit();
            mCircle =  map.addCircle(circleOptions);
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    mCircle.setCenter(marker.getPosition());
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });
        }else {
            Double lat = Double.valueOf(gateData.get(1));
            Double lang = Double.valueOf(gateData.get(0));
            latLng = new LatLng(lat,lang);
            edit.putString("lat", String.valueOf(latLng.latitude));
            edit.putString("lng", String.valueOf(latLng.longitude));
            edit.commit();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng).title("My Gate").snippet("Is here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .draggable(true));
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(Double.valueOf(gateData.get(4)))
                    .strokeColor(Color.YELLOW)
                    .fillColor(Color.argb(100, 0, 188, 212))
                    .strokeWidth(8).clickable(true);
            circleOptions.center(latLng);
            mCircle = map.addCircle(circleOptions);
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    mCircle.setCenter(marker.getPosition());
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    edit.putString("lat", String.valueOf(marker.getPosition().latitude));
                    edit.putString("lng", String.valueOf(marker.getPosition().longitude));
                    edit.commit();
                }
            });

        }


        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marker != null) {
                    map.clear();
                    marker =null;
                    Toast.makeText(MapsActivity.this, "Cleared all markers", Toast.LENGTH_SHORT).show();
                } else {
                    marker = map.addMarker(new MarkerOptions()
                            .position(latLng).title("My Gate").snippet("Is here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                            .draggable(true));
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(Double.valueOf(radius))
                            .strokeColor(Color.YELLOW)
                            .fillColor(Color.argb(100, 0, 188, 212))
                            .strokeWidth(8).clickable(true);
                    circleOptions.center(latLng);
                    edit.putString("lat", String.valueOf(latLng.latitude));
                    edit.putString("lng", String.valueOf(latLng.longitude));
                    edit.commit();
                    mCircle = map.addCircle(circleOptions);
                    map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            mCircle.setCenter(marker.getPosition());
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {

                        }
                    });
                }
                hasMarker = true;
            }
        });

    }


    private void setMyLocation(GoogleMap map) {

        if (checkLocationPermission()) {
            map.setMyLocationEnabled(true);
        }
    }


    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );

            return false;
        }
        else
             return true;
    }

    @Override
    public void onRadiusUpdated(String radius) {
        mCircle.setRadius(Double.valueOf(radius));
    }

    private void animateMap (GoogleMap map, LatLng latLng){


    }
}
