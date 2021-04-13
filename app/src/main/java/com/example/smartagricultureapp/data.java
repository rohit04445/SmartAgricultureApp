package com.example.smartagricultureapp;

import org.json.JSONArray;
import org.json.JSONObject;

public class data {
    public static int time=120*1000;//2mins
    public static String url ="https://project-iotdata.herokuapp.com/get";
    public static JSONObject allData,sensor_data;
    public static JSONArray weather_data;
    public  static String motor_status,motor_suggested;

}
