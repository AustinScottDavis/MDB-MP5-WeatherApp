package com.example.davis.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Utils {
    public static String json = "";
    public static String location = "";
    public static String description = "";
    public static String tempLow = "";
    public static String tempHigh = "";
    public static String tempCurrent = "";

    public static void parseJSON(String weatherData) {

        try {
            JSONObject obj = new JSONObject(weatherData);
            JSONObject todayObj = obj.getJSONObject("currently");

            description = todayObj.getString("summary");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
