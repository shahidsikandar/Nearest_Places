package com.example.administrator.nearestplaces.modal;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Administrator on 1/10/2018.
 */

public class NearestPlaces {
    private String places = null;
    private String vicinity = null;


    public NearestPlaces(String places,String vicinity) {
        this.places = places;
        this.vicinity = vicinity;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }
}
