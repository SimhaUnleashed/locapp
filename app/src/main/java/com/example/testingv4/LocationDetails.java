package com.example.testingv4;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationDetails extends AppCompatActivity {


    Switch gpsSwitch;
    TextView latitudeContent, longitudeContent, altitudeContent, accuracyContent, sensorContent, updateContent;
    FusedLocationProviderClient fusedLocationProviderClient; //main component of the whole app
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    Location currentLocation;
    List<Location> savedLocation;
    String address = "";
    static List<String> places = new ArrayList<String>(); //intitalization

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(places);
        editor.putString("task list", json);
        editor.apply();
    } //function to save data

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        places = gson.fromJson(json, type);

        if (places == null) {
            places = new ArrayList<>();
        }
    } //function to load data



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        loadData();
        latitudeContent = findViewById(R.id.latitudeContent);
        longitudeContent = findViewById(R.id.longitudeContent);
        altitudeContent = findViewById(R.id.altitudeContent);
        accuracyContent = findViewById(R.id.accuracyContent);
        sensorContent = findViewById(R.id.sensorContent);
        updateContent = findViewById(R.id.updateContent);
       Button deleteWayPointbtn = (Button) findViewById(R.id.deleteWaypointbtn);
        Button homebtn = (Button) findViewById(R.id.homeBtn);
        Button waypointBtn = (Button) findViewById(R.id.waypointBtn);
        gpsSwitch = findViewById(R.id.gpsSwitch);
        //intializing the UI elements


        waypointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp myApp = (MyApp)getApplicationContext();
                savedLocation = myApp.getMyLocation();
                    savedLocation.add(currentLocation);
                    places.add(address);
                    saveData();
                Toast.makeText(LocationDetails.this,"Waypoint is added!",Toast.LENGTH_SHORT).show();


            }

        });//what add waypoint button does

        homebtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(LocationDetails.this, MainActivity.class);
                                           startActivity(intent);

                                       }
                                   }); //what home button does when clicked


        deleteWayPointbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (places.size() > 0){
                    places.remove(places.size() - 1);
                    saveData();
                    Toast.makeText(LocationDetails.this,"Waypoint is deleted!",Toast.LENGTH_SHORT).show();

            }
            else{
                    Intent intent = new Intent(LocationDetails.this, MainActivity.class);
                    startActivity(intent);
                }
            }

                                  }); //what delete button does while clicked (check if the array is empty... if yes return to home if not delete last element)
        locationRequest = LocationRequest.create()
                .setInterval(500)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //asking for location from API


        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //save location
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updateValues(location);

                    }
                }
            }
        }; //update the location value when we receive an update from API


        gpsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsSwitch.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    sensorContent.setText("Using GPS");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    sensorContent.setText("Using Towers + WiFi");
                }
            }
        }); //Helps to save battery



        updateGPS();
    } //oncreate
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            } else {
                Toast.makeText(this, "This app requires Location permission to work properly", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    } //Check if the user has granted location permission

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationDetails.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){



           fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                                                   @Override
                                                                                   public void onComplete(@NonNull Task<Location> task) {
                                                                                       Location location = task.getResult();
                                                                                       if (location != null) {
                                                                                           updateValues(location);
                                                                                           currentLocation = location;

                                                                                       }
                                                                                   }
                                                                               });

        } //Update location using fusedLocationProvider API
        else{
            if(Build.VERSION.SDK_INT>=23){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    public void updateValues(Location location) {
        TextView addressContent = findViewById(R.id.addressContent);
        //update all of the textViews
        latitudeContent.setText(String.valueOf(location.getLatitude()));
        longitudeContent.setText(String.valueOf(location.getLongitude()));
        accuracyContent.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            altitudeContent.setText(String.valueOf(location.getAltitude()));
        }
        else{
            altitudeContent.setText("N/A");
        }

        //Set content in the UI

       Geocoder geocoder = new Geocoder(LocationDetails.this);

        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            address+=listAddresses.get(0).getAddressLine(0);
            addressContent.setText(address);
        }

        catch (Exception e) {
            addressContent.setText("Somewhere in the world");
            address+="Somewhere in the world";
        }
    }
    //Used to set address in the UI when geocoder returns a NON null value
}

