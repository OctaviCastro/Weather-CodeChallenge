package com.fire.weather_codechallenge.tools;

import com.fire.weather_codechallenge.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Octavi on 1/8/18.
 */

public class OpenWeatherData {

    public static String OPEN_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static String API_TOKEN = BuildConfig.API_TOKEN;

    public String openWeatherRequest(String lat, String lon) {
        StringBuilder sb = new StringBuilder(OPEN_WEATHER_URL);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", lat, lon, API_TOKEN));
        return sb.toString();
    }

    public JSONObject getOpenWeatherData(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            if(httpURLConnection.getResponseCode() == 200) // OK - 200
            {
                BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = r.readLine())!=null)
                    sb.append(line).append("\n");
                r.close();
                JSONObject data = new JSONObject(sb.toString());
                httpURLConnection.disconnect();

                return data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fromLongToDateTime(double longDate){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)longDate*1000);
        return dateFormat.format(date);
    }
}
