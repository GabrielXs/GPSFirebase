package com.example.gpsfirebase.Helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchURL extends AsyncTask<String, Void, String> {
    Context mContext;
    String directionMode = "driving";

    public FetchURL(Context mContext){
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        directionMode = strings[1];

        try{

            data = downloadUrl(strings[0]);

        }catch (Exception e){
            Log.e("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    private String downloadUrl(String strurl) throws IOException
    {
        String data = "";

        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strurl);
            //Criando a conexão para a url do google
            urlConnection = (HttpURLConnection) url.openConnection();
            //Conectando para url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";

            while((line = br.readLine())!= null){
                sb.append(line);
            }

            data = sb.toString();
            Log.d("download url", "Downloaded URL" + data.toString());

        }catch (Exception e){
            Log.e("download url",String.format("Exception downloading URL: %s",e.toString()));
        }finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();

        }

        return  data;

    }
}
