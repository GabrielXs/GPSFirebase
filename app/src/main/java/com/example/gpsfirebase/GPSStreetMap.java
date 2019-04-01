package com.example.gpsfirebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class GPSStreetMap extends AppCompatActivity {

    private static final int REQUEST_GPS = 1;
    GeoPoint pontoinicial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsstreet_map);
        userRequestPermission();
        //Pegando Mapa OpenStreetMaps
        MapView mapa = (MapView) findViewById(R.id.gps_mapa);

        //Configurando userAgent
        Configuration.getInstance().setUserAgentValue(this.getPackageName());

        //Fontes de imagens
        mapa.setTileSource(TileSourceFactory.MAPNIK);

        //Cria um ponto de referencia com base na latitude e longitude
        pontoinicial = new GeoPoint(-22.875879, -43.445688);
        IMapController mapController = mapa.getController();

        //mapController.animateTo(pontoinicial);
        mapController.setZoom(15.0);

        //Centraliza o mapa no ponto de Referencia
        mapController.setCenter(pontoinicial);

        //Cria um Marcado no Mapa
        Marker startMarker = new Marker(mapa);
        startMarker.setPosition(pontoinicial);
        startMarker.setTitle("Ponto Inicial");

        //Posiçao do icone
        startMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        mapa.getOverlays().add(startMarker);




    }

    private void userRequestPermission() {
        ArrayList<String> requestPermission = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(GPSStreetMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermission .add(Manifest.permission.ACCESS_FINE_LOCATION);

        }
        if(ContextCompat.checkSelfPermission(GPSStreetMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        }
        if(ContextCompat.checkSelfPermission(GPSStreetMap.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            requestPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
        if(ContextCompat.checkSelfPermission(GPSStreetMap.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            requestPermission.add(Manifest.permission.INTERNET);

        }

        if(!requestPermission.isEmpty()){
            String [] permissao = new String[requestPermission.size()];
            for(int i = 0; i < requestPermission.size(); i++){
                permissao[i] = requestPermission.get(i);
            }
            ActivityCompat.requestPermissions(this,permissao,REQUEST_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_GPS:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.recreate();
                }
                break;
        }
    }
}
