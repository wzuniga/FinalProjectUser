package com.wz.www.finalproject;

import java.util.Calendar;

/**
 * Esta clase se usa para alamcenar lso datos obtenidos del Sensor.
 */

public class Data {
    String id;
    String latitude;
    String longitude;
    String altitude;
    String speed;
    String hour;
    String date;

    Data(){
        latitude = -16.406282+"";
        longitude = -71.524863+"";
        altitude = 0.0+"";
        speed = "0";
        hour = Calendar.HOUR+"";
        date = System.currentTimeMillis()+"";
    }
    Data(String lat, String lon, String alt, String spe){
        latitude = lat;
        longitude = lon;
        altitude = alt;
        speed = spe;

        hour = Calendar.HOUR+"";
        date = System.currentTimeMillis()+"";
    }

    @Override
    public String toString() {
        return latitude+"-"+longitude+".."+altitude+".."+speed+".."+hour+".."+date;
    }
}
