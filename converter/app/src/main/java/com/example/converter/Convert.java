package com.example.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Convert {

    public Map<String, Double> time = new HashMap<String, Double>();
    public Map<String, Double> distance = new HashMap<String, Double>();
    public Map<String, Double> weight = new HashMap<String, Double>();
    public Map<String, Double> temperature = new HashMap<String, Double>();

    public Convert(){
        distance.put("millimeter", 1000.);
        distance.put("centimeter", 100.);
        distance.put("decimeter", 10.);
        distance.put("meter", 1.);
        distance.put("kilometer", 1./1000);

        temperature.put("celsius", 1.);
        temperature.put("kelvins", 1. + 273.);
        temperature.put("rankin", (1. + 273.)*9/5);
        temperature.put("delisle", (100.-1.)*3/2);

        time.put("milliseconds", 1000.);
        time.put("seconds", 1.);
        time.put("hours", 1./3600);
        time.put("days", 1./86400);

        weight.put("centners",1./100);
        weight.put("kilograms",1.);
        weight.put("grams",1000.);
        weight.put("milligrams",1000000.);
    }


    public double getCoefficient(String categoryName, String name){
        double result = 1.;
        if(categoryName == "Time"){
            result = time.get(name);
        }
        if(categoryName == "Distance"){
            result = distance.get(name);
        }
        if (categoryName == "Weight"){
            result = weight.get(name);
        }
        if (categoryName == "Temperature"){
            result = temperature.get(name);
        }
        return result;
    }


    public ArrayList<String> getNames(String category){
        ArrayList<String> names;
        if(category == "Distance"){
            names = new ArrayList<String>(distance.keySet());
        }
        else if(category == "Weight"){
            names = new ArrayList<String>(weight.keySet());
        }
        else if(category == "Temperature"){
            names = new ArrayList<String>(temperature.keySet());
        }
        else if(category == "Time"){
            names = new ArrayList<String>(time.keySet());
        }
        else names = new ArrayList<String>();
        return names;
    }

    public String Converting(String data, double co_one, double co_two){
        double result = 1.;
        if(data.equals("")){
            return "0";
        }
        try{
            result = Double.parseDouble(data);
        }
        catch (Exception exp){
            return "not correct input";
        }
        result = result * co_two / co_one;
        return ""+result;
    }

}
