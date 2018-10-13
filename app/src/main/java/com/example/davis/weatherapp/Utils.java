package com.example.davis.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class Utils {
    public static String json = "";
    public static String location = "";
    public static String description = "";
    public static String tempLow = "";
    public static String tempHigh = "";
    public static String tempCurrent = "";
    public static String cityName = "";
    public static boolean isRaining = false;
    public static String rainProb = "";

    public static void parseJSON(String weatherData) {

        try {
            JSONObject obj = new JSONObject(weatherData);
            JSONObject currentObj = obj.getJSONObject("currently");
            JSONArray lst = (JSONArray) obj.getJSONObject("daily").get("data");
            JSONObject todayObj = (JSONObject) lst.get(0);

            description = currentObj.getString("summary");
            tempLow = todayObj.getString("temperatureLow");
            tempHigh = todayObj.getString("temperatureHigh");
            tempCurrent = currentObj.getString("temperature");
            double precip = currentObj.getDouble("precipIntensity");
            if (precip > 0) {
                isRaining = true;
            }
            double prob = currentObj.getDouble("precipProbability");
            prob = prob * 100;
            int probrain = (int) Math.round(prob);
            rainProb = Integer.toString(probrain) + "%";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
