package com.example.calorietracker;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    final private float DEFAULT_ZOOM = 13.0f;

    private GoogleMap map;
    private LatLng mDefaultLocation;
    private LatLng mLastKnownLocation;
    private boolean mLocationPermissionGranted;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);

        getHomeLocation("7/15 Royal Avenue, Glen Huntly, 3163");
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location loc = (Location) task.getResult();
                            mLastKnownLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastKnownLocation, DEFAULT_ZOOM));

                            getNearbyParks();
                        } else {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private class MapAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection conn = null;

            try {
                URL apiUrl = new URL(strings[0]);
                conn = (HttpURLConnection) apiUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNextLine()) {
                    result += scanner.nextLine();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray respArray = obj.getJSONArray("results");
                JSONObject nextObj = respArray.getJSONObject(0);
                JSONObject geoObj = nextObj.getJSONObject("geometry");
                JSONObject locObj = geoObj.getJSONObject("location");

                LatLng latLng = new LatLng(locObj.getDouble("lat"), locObj.getDouble("lng"));

                map.addMarker(new MarkerOptions().position(latLng).title("Home Address").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            catch (Exception ex) {

            }
        }
    }

    public void getHomeLocation(String address) {
        try {
            String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(address, "UTF-8") + "&key=AIzaSyC2eNZl7J4S-MkCcAlUexmp5AdstDzWLzk";
            MapAPI mapAPI = new MapAPI();
            mapAPI.execute(apiUrl);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void getNearbyParks() {
        try {
            String apiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    mLastKnownLocation.latitude + "," + mLastKnownLocation.longitude +
                    "&radius=5000&type=park&key=AIzaSyC2eNZl7J4S-MkCcAlUexmp5AdstDzWLzk";
            ParkAPI parkAPI = new ParkAPI();
            parkAPI.execute(apiUrl);
        }
        catch (Exception ex) {

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private class ParkAPI extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        HttpURLConnection conn = null;

        try {
            URL apiUrl = new URL(strings[0]);
            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()) {
                result += scanner.nextLine();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String response) {
        System.out.print(response);
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray respArray = obj.getJSONArray("results");
            for(int i = 0; i < respArray.length(); i++)
            {
                JSONObject nextObj = respArray.getJSONObject(i);
                JSONObject geoObj = nextObj.getJSONObject("geometry");
                JSONObject locObj = geoObj.getJSONObject("location");

                LatLng latLng = new LatLng(locObj.getDouble("lat"), locObj.getDouble("lng"));
                String parkName = nextObj.getString("name");

                map.addMarker(new MarkerOptions().position(latLng).title(parkName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }

        catch (Exception ex) {

        }

    }
}
}
