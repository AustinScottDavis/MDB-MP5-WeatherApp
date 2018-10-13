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
    final static String DEGREE  = "\u00b0";
    public static String weekSummary = "";
    public static String low1 = "";
    public static String low2 = "";
    public static String low3 = "";
    public static String high1 = "";
    public static String high2 = "";
    public static String high3 = "";

    public static void parseJSON(String weatherData) {

        try {
            JSONObject obj = new JSONObject(weatherData);
            JSONObject currentObj = obj.getJSONObject("currently");
            JSONArray lst = (JSONArray) obj.getJSONObject("daily").get("data");
            JSONObject todayObj = (JSONObject) lst.get(0);
            JSONObject forecast1Obj = (JSONObject) lst.get(1);
            JSONObject forecast2Obj = (JSONObject) lst.get(2);
            JSONObject forecast3Obj = (JSONObject) lst.get(3);

            description = currentObj.getString("summary");
            double tempLowDouble = todayObj.getDouble("temperatureLow");
            tempLow =  Integer.toString((int) Math.round(tempLowDouble)) + DEGREE;
            double tempHighDouble = todayObj.getDouble("temperatureHigh");
            tempHigh =  Integer.toString((int) Math.round(tempHighDouble)) + DEGREE;
            double tempCurrentDouble = currentObj.getDouble("temperature");
            tempCurrent = Integer.toString((int) Math.round(tempCurrentDouble)) + DEGREE;
            double precip = currentObj.getDouble("precipIntensity");
            if (precip > 0) {
                isRaining = true;
            }
            double prob = currentObj.getDouble("precipProbability");
            prob = prob * 100;
            int probrain = (int) Math.round(prob);
            rainProb = Integer.toString(probrain) + "%";

            weekSummary = (String) obj.getJSONObject("daily").get("summary");
            double low1double = forecast1Obj.getDouble("temperatureLow");
            low1 = Integer.toString((int) Math.round(low1double)) + DEGREE;

            double low2double = forecast2Obj.getDouble("temperatureLow");
            low2 = Integer.toString((int) Math.round(low2double)) + DEGREE;

            double low3double = forecast3Obj.getDouble("temperatureLow");
            low3 = Integer.toString((int) Math.round(low3double)) + DEGREE;

            double high1double = forecast1Obj.getDouble("temperatureHigh");
            high1 = Integer.toString((int) Math.round(high1double)) + DEGREE;

            double high2double = forecast2Obj.getDouble("temperatureHigh");
            high2 = Integer.toString((int) Math.round(high2double)) + DEGREE;

            double high3double = forecast3Obj.getDouble("temperatureHigh");
            high3 = Integer.toString((int) Math.round(high3double)) + DEGREE;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
