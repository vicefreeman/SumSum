package sumsum.gates.vice.hiday;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AddGate.OnRadiusUpdateListener {

    private GoogleMap mMap;

    private Circle mCircle;


    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame1, mapFragment)
                .replace(R.id.frame2, new AddGate())
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
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("shred" , Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                String radius = preferences.getString("radius", "200");
                edit.putString("lat", String.valueOf(latLng.latitude));
                edit.putString("lng", String.valueOf(latLng.longitude));
                edit.commit();
                final CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(200)
                        .strokeColor(Color.YELLOW)
                        .fillColor(Color.argb(100, 0, 188, 212))
                        .strokeWidth(8).clickable(true);


                mCircle = map.addCircle(circleOptions);

                final Circle finalMCircle = mCircle;

                map.addMarker(new MarkerOptions()
                        .position(latLng).title("My Gate").snippet("Is here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .draggable(true));


                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        finalMCircle.setCenter(marker.getPosition());
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                    }
                });


            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                Toast.makeText(MapsActivity.this, "Cleared all markers", Toast.LENGTH_SHORT).show();

                map.clear();
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
}
