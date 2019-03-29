package com.example.gpsfirebase.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask <String, Integer , List<List<HashMap<String,String>>>> {

    private TaskLoadedCallback taskCallback;
    private String directionMode = "driving";

    public PointsParser(Context mContext, String directionMode){
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jsonObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jsonObject = new JSONObject(jsonData[0]);
            Log.d("JsonObject",jsonData[0]);
            DataParser parser = new DataParser();
            Log.d("DataParser", parser.toString());

            //Iniciando parsing data
            routes = parser.parse(jsonObject);
            Log.d("routes", "Executando Rotas");
            Log.d("routes", routes.toString());
        }catch (Exception e){
            Log.d("Erro de Parser", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result){
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Percorrendo todas as rotas
        for(int i = 0; i < result.size(); i++){
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);
            for(int j = 0 ; j < path.size(); j++){
                HashMap<String,String>  point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat,lng);
                points.add(position);
            }
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")){
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            }else{
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }
            Log.d("onPostExecute", "onPostExecute lineoptions decoded");
        }

        //Desenhando polyline no Google Maps
        if(lineOptions != null){
            taskCallback.onTaskdone(lineOptions);
        }else{
            Log.d("mylog", "Sem polilinhas desenhadas");
        }
    }
}
