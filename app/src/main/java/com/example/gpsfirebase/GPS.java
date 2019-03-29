package com.example.gpsfirebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;

public class GPS extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int REQUEST_PERMISSION_GPS = 0 ;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private LatLng currentLocationLatLong;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        startGettingLocations();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("GPS");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        //Add Marcador
        currentLocationLatLong = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.title("Localização Atual");
        currentLocationMarker = mMap.addMarker(markerOptions);

        //Move para nova Localização
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Gravando no Banco de dados do firebase
        LocationData locationData = new LocationData(location.getLatitude(),location.getLongitude());
        mDatabase.setValue(locationData);

        Toast.makeText(this,"Localização Atualizada", Toast.LENGTH_LONG).show();
    }



    private void startGettingLocations() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        long MIN_TIME_BW_UPDATES = 1000 * 10;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
            }, REQUEST_PERMISSION_GPS );
        }else{
            boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNETWORK = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPS){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
            }else if(isNETWORK){
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
            }else{
                Toast.makeText(this,"Não é possivel obter a localização", Toast.LENGTH_LONG).show();
            }



        }

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
