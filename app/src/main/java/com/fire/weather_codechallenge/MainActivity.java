package com.fire.weather_codechallenge;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.fire.weather_codechallenge.tools.OpenWeatherData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

public class MainActivity extends AppCompatActivity implements LocationListener {

    static double lat, lon;
    TextView textCityName, textCurrentTemp, textMinMaxTemp, textHumidity, textWindSpeed, textSunsetSunrise;
    ImageView imageCurrentIcon;
    LocationManager locationManager;
    String provider;
    OpenWeatherData openWeatherData = new OpenWeatherData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCityName = (TextView) findViewById(R.id.textCityName);
        textCurrentTemp = (TextView) findViewById(R.id.textCurrentTemp);
        textMinMaxTemp = (TextView) findViewById(R.id.textMinMaxTemp);
        textHumidity = (TextView) findViewById(R.id.textHumidity);
        textWindSpeed = (TextView) findViewById(R.id.textWindSpeed);
        textSunsetSunrise = (TextView) findViewById(R.id.textSunetSunRise);
        imageCurrentIcon = (ImageView) findViewById(R.id.imageCurrentIcon);

        //Get Coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG","No Location");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        new getWeather().execute(openWeatherData.openWeatherRequest(String.valueOf(lat),String.valueOf(lon)));
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

    private class getWeather extends AsyncTask<String,Void,JSONObject> {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject stream = null;
            String urlString = params[0];

            stream = openWeatherData.getOpenWeatherData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(JSONObject jsonWeatherData) {

            try {
                if(jsonWeatherData != null) {
                    JSONObject weather = jsonWeatherData.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = jsonWeatherData.getJSONObject("main");
                    JSONObject wind = jsonWeatherData.getJSONObject("wind");
                    JSONObject sys = jsonWeatherData.getJSONObject("sys");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    textCityName.setText(String.format("%s", jsonWeatherData.getString("name")));
                    textCurrentTemp.setText(String.format("%.2f °C", main.getDouble("temp")));
                    textMinMaxTemp.setText(String.format("Min.T°: %.2f Max.T°: %.2f", main.getDouble("temp_min"), main.getDouble("temp_max")));
                    textHumidity.setText(String.format("Humidity: %d%% ", main.getInt("humidity")));
                    textWindSpeed.setText(String.format("Wind Speed: %.2f", wind.getDouble("speed")));
                    textSunsetSunrise.setText(String.format("Sunrise: %s Sunset: %s", OpenWeatherData.fromLongToDateTime(sys.getDouble("sunrise")), OpenWeatherData.fromLongToDateTime(sys.getDouble("sunset"))));
                    Picasso.with(MainActivity.this)
                                .load(String.format("http://openweathermap.org/img/w/%s.png", weather.getString("icon")))
                                .into(imageCurrentIcon);
                }

            } catch (JSONException e) {

            }
        }

    }

}
