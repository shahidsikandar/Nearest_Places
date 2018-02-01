package com.example.administrator.nearestplaces;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.example.administrator.nearestplaces.modal.NearestPlaces;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

import static com.example.administrator.nearestplaces.AppConfig.GEOMETRY;
import static com.example.administrator.nearestplaces.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.administrator.nearestplaces.AppConfig.ICON;
import static com.example.administrator.nearestplaces.AppConfig.LATITUDE;
import static com.example.administrator.nearestplaces.AppConfig.LOCATION;
import static com.example.administrator.nearestplaces.AppConfig.LONGITUDE;
import static com.example.administrator.nearestplaces.AppConfig.NAME;
import static com.example.administrator.nearestplaces.AppConfig.OK;
import static com.example.administrator.nearestplaces.AppConfig.OPENING_HOURS;
import static com.example.administrator.nearestplaces.AppConfig.PLACE_ID;
import static com.example.administrator.nearestplaces.AppConfig.PROXIMITY_RADIUS;
import static com.example.administrator.nearestplaces.AppConfig.REFERENCE;
import static com.example.administrator.nearestplaces.AppConfig.STATUS;
import static com.example.administrator.nearestplaces.AppConfig.SUPERMARKET_ID;
import static com.example.administrator.nearestplaces.AppConfig.VICINITY;
import static com.example.administrator.nearestplaces.AppConfig.ZERO_RESULTS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    public static final int MULTIPLE_PERMISSIONS = 10;
    Button btn_location = null;
    TextView tv_Location = null;
    TextView tv_header = null;
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    Spinner places_spinner;
    String[] mPlaceType=null;
    String[] mPlaceTypeName=null;

    private ArrayList<NearestPlaces> nearestPlacesArrayList;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    private boolean flag = false;
    String[] permissions= new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        nearestPlacesArrayList=new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(this,nearestPlacesArrayList);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        places_spinner = (Spinner)findViewById(R.id.spinner_list);
        tv_Location = (TextView) findViewById(R.id.text_location);
        btn_location = (Button) findViewById(R.id.btn_location);
        tv_header = (TextView)findViewById(R.id.tv_header);

        mPlaceType = getResources().getStringArray(R.array.places_type);
        mPlaceTypeName = getResources().getStringArray(R.array.places_array_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);
        places_spinner.setAdapter(adapter);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_View);

        LinearLayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        btn_location.setOnClickListener(this);
        places_spinner.setOnItemSelectedListener(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

    }


    @Override
    public void onClick(View v) {
        flag = displayGpsStatus();
        if (flag) {
            tv_header.setText("Please!! move your device to" +
                    " see the changes in coordinates." + "\nWait..");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                checkPermissions();

            }else {

                locationManager.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 30000, 10, locationListener);

            }

        } else {
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }

        /*loadNearByPlaces("31.51269666666667","74.34202666666667");*/

    }


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ActivityCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }



    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadNearByPlaces(String latitude, String longitude){

        nearestPlacesArrayList.clear();
        int selectedPosition = places_spinner.getSelectedItemPosition();
        String type = mPlaceType[selectedPosition];
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        /*String type = "nearbysearch";
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");*/
            googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_API_KEY);


        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*Toast.makeText(getBaseContext(),"On Response: Result Positive"+ response.toString(),Toast.LENGTH_LONG).show();*/
                        Log.i(AppConfig.TAG, "onResponse: Result= " + response.toString());
                        parseLocationResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),"On Response: Result Error"+error.toString(),Toast.LENGTH_LONG).show();
                        Log.e(AppConfig.TAG,"onErrorResponse: Error= " + error);
                        Log.e(AppConfig.TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {

        String id, place_id,placeName = null, vicinity = null;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    if (place.has(NAME)&&!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);

                    LatLng latLng = new LatLng(latitude, longitude);

                    nearestPlacesArrayList.add(new NearestPlaces(placeName,vicinity));

                }

                recyclerAdapter.setNearestPlacesArrayList(nearestPlacesArrayList);
                recyclerAdapter.notifyDataSetChanged();

                Toast.makeText(getBaseContext(), jsonArray.length() + " Places found!",
                        Toast.LENGTH_LONG).show();
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Toast.makeText(getBaseContext(), "No Places found in 3KM radius!!!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(AppConfig.TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            tv_Location.setText("");

            Toast.makeText(getBaseContext(),"Location changed : Lat: " +
                            location.getLatitude()+ " Lng: " + location.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " +location.getLongitude();
            String latitude = "Latitude: " +location.getLatitude();

            String cityName=null;
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location
                        .getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName=addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = longitude+"\n"+latitude +
                    "\n\nMy Currrent City is: "+cityName;
            tv_Location.setText(s);

            loadNearByPlaces(location.getLatitude()+"",location.getLongitude()+"");
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
}
