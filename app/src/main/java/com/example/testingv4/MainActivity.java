package com.example.testingv4;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        LocationDetails.places = gson.fromJson(json, type);

        if (LocationDetails.places == null) {
            LocationDetails.places = new ArrayList<>();
        }
    }// Load data from memory

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    } // Return back to home screen

   ListView waypointListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        waypointListView = findViewById(R.id.waypointListView);
        Button btn = findViewById(R.id.locDetailbtn);
        Button showMapbtn = findViewById(R.id.showMapBtn); // initialization

        waypointListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,LocationDetails.places)); //listview

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationDetails();
            }

            public void openLocationDetails() {
                Intent intent = new Intent(MainActivity.this, LocationDetails.class);
                finish();
                startActivity(intent);
            }
        }); //what location details button when pressed

        showMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        //what show map button does while pressed
    }

}