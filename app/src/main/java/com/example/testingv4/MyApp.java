package com.example.testingv4;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyApp  extends Application {
    private  static MyApp singleton;

    private List<Location> myLocations;

    List<Location> getMyLocation(){
        return myLocations;
    }
    public void onCreate(){
        super.onCreate();
        singleton =this;
        myLocations = new ArrayList<>();
    }
    //Create myLocations such that it can be instantiated only once
}
