package com.blstream.as;


import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpAsync extends AsyncTask<String,Void,Integer> {
    public static final int CONNECT_TIMEOUT = 10000;

   protected Integer doInBackground(String... params){
       try {
           if (params.length > 0) {
               URL url = new URL(params[0]);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestProperty("Content-Type", "application/json");
               urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
               urlConnection.setRequestMethod("POST");
               if (params.length > 1) {
                   urlConnection.setRequestProperty("Preferred", params[1]);
               }
               return urlConnection.getResponseCode();
           }
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return null;
    }

    protected void onPostExecute(Integer response){

    }
}
