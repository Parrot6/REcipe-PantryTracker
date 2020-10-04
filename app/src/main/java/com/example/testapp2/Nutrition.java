package com.example.testapp2;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class Nutrition implements Serializable {
    public static String getFormatted(Field f, Object o) throws IllegalAccessException {
      StringBuilder sb = new StringBuilder();
        DecimalFormat df2 = new DecimalFormat("#.##");
        String[] strs = f.getName().split("_",2);
      String name = strs[1].replace('_',' ');
      String measure;
      if(strs[0].equals("nf")) measure = "";
      else measure = strs[0];
      sb.append(name + ": " + df2.format(Double.parseDouble(f.get(o).toString())) + " " + measure + "\n");
      return sb.toString();
    };
    public static String getFormatted(Double d) throws IllegalAccessException {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    };

    private int combinedCount = 1;
    public String nf_ingredient_statement;
    private String queryResults = "Not Queried";
    public Double kcal_calories;
    public Double g_total_fat;
    public Double mg_cholesterol; //mg
    public Double mg_sodium; //mg
    public Double g_total_carbohydrate;
    public Double nf_dietary_fiber = null;
    public Double g_sugars;
    public Double g_protein;
    public Double nf_calories_from_fat = null;
    public Double nf_saturated_fat = null;
    public Double nf_monounsaturated_fat = null;
    public Double nf_polyunsaturated_fat = null;
    public Double nf_trans_fatty_acid = null;
    public Double nf_vitamin_a_dv = null;
    public Double nf_vitamin_c_dv = null;
    public Double nf_calcium_dv = null;
    public Double nf_iron_dv = null;
    public Double nf_potassium = null;
    public Double nf_servings_per_container;
    public Double nf_serving_size_qty;
    public String nf_serving_size_unit;
    public Double g_serving_weight_grams;
    public Double metric_qty;
    public String item_name;
    private String metric_uom;
    transient JSONObject source;
    private transient Ingredient ing;
    public static Nutrition newNutrition(Nutrition n){
        Nutrition nn = new Nutrition();
        for (Field f :
                n.getClass().getDeclaredFields()) {
            try {
                f.set(nn, f.get(n));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return nn;
    }
    public Nutrition(){

    }
    public Nutrition(JSONObject vals, Ingredient ingredient){
        source = vals;
        ing = ingredient;
        item_name = vals.optString("item_name", "");
        g_serving_weight_grams = vals.optDouble("nf_serving_weight_grams", 0);
        nf_servings_per_container = vals.optDouble("nf_servings_per_container", 0);
        nf_serving_size_qty = vals.optDouble("nf_serving_size_qty", 0);
        nf_serving_size_unit = vals.optString("nf_serving_size_unit", "");
        metric_qty = vals.optDouble("metric_qty", 0);
        metric_uom = vals.optString("metric_uom", "");
        kcal_calories = vals.optDouble("nf_calories", 0);
        g_total_fat = vals.optDouble("nf_total_fat", 0);
        mg_cholesterol = vals.optDouble("nf_cholesterol", 0);
        mg_sodium = vals.optDouble("nf_sodium", 0);
        g_total_carbohydrate = vals.optDouble("nf_total_carbohydrate", 0);
        g_sugars = vals.optDouble("nf_sugars", 0);
        g_protein = vals.optDouble("nf_protein", 0);
        nf_ingredient_statement = vals.optString("nf_ingredient_statement", "");
        if(nf_serving_size_unit.toLowerCase().contains( ing.getMeasurementType().toLowerCase())){
            double mult = ing.getQuantity()/nf_serving_size_qty;
            Log.e("EqualMeasureTypes", nf_serving_size_unit + "&" +ing.getMeasurementType() +" scale: " + String.valueOf(mult));
            queryResults = "Success";
            scaleNutrition(mult);
        } else {
            Log.e("NotEqualMeasureTypes", ing.getMeasurementType() + " vs " + nf_serving_size_unit);
            Boolean found = false;

            for (UnitConversion unc :
                    MainActivity.conversions) {
                if(unc.hasNameMatch(ing.getMeasurementType())){
                    Log.e("Found initial type", ing.getMeasurementType());

                    if(unc.hasConversion(nf_serving_size_unit)){
                        Log.e("Found secondary type", nf_serving_size_qty + " "+ nf_serving_size_unit);
                        found = true;
                        Double multi = unc.getConversionMulti(nf_serving_size_unit);
                        //multi = multi/nf_serving_size_qty;
                        Double finalMulti = (ing.getQuantity()/nf_serving_size_qty)*multi;
                        queryResults = "Success";
                        Log.e("found matching conv",ing.getQuantity()+ing.getMeasurementType() + " to " +nf_serving_size_qty+ nf_serving_size_unit + " " + finalMulti);
                        scaleNutrition(finalMulti);
                        nf_serving_size_unit = ing.getMeasurementType();
                        nf_serving_size_qty = ing.getQuantity();
                    }
                    break;
                }
            }
            if(!found) queryResults = "No conversion";
            if(!found) Log.e("No conversion available", ing.getMeasurementType() + " vs " + nf_serving_size_unit);
        }
    }
    public String getQueryResults(){
        return queryResults;
    }
    public Nutrition scaleNutrition(double amt){
        for (Field f: getClass().getDeclaredFields()) {
            try {
                if(f.get(this) instanceof Double){

                    Double temp = (Double) f.get(this);
                    Log.e("scaling", f.getName() + ":" + String.valueOf(temp) + " to " + temp*amt);
                    f.set(this, temp*amt);
                }
            }
            catch (IllegalStateException ise) {

            }
            // nope
            catch (IllegalAccessException iae) {}
        }
        return this;
    }
    public void getCombined(Nutrition other){
        combinedCount += other.combinedCount;
        kcal_calories += other.kcal_calories;
        //nf_calcium_dv += other.nf_calcium_dv;
        g_serving_weight_grams += other.g_serving_weight_grams;
        nf_servings_per_container += other.nf_servings_per_container;
        //nut.nf_serving_size_qty += other.nf_serving_size_qty;
        //nut.nf_serving_size_unit += other.nf_serving_size_unit;
        metric_qty += other.metric_qty;
        metric_uom += other.metric_uom;
        g_total_fat += other.g_total_fat;
        mg_cholesterol += other.mg_cholesterol;
        mg_sodium += other.mg_sodium;
        g_total_carbohydrate += other.g_total_carbohydrate;
        g_sugars += other.g_sugars;
        g_protein += other.g_protein;
        //nut.nf_ingredient_statement += other.nf_ingredient_statement;
    }
    public String toStringVar(){
        StringBuilder result = new StringBuilder();
        for (Field f: getClass().getDeclaredFields()) {
            try {
                result
                        .append(f.getName())
                        .append(" : ")
                        .append(f.get(this))
                        .append(System.getProperty("line.separator"));
            }
            catch (IllegalStateException ise) {
                result
                        .append(f.getName())
                        .append(" : ")
                        .append("[cannot retrieve value]")
                        .append(System.getProperty("line.separator"));
            }
            // nope
            catch (IllegalAccessException iae) {}
        }
        return result.toString();
    }
}

