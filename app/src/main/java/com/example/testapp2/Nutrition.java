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
    public static String getFormatted(String fieldName) throws IllegalAccessException {
        String[] parts = fieldName.split("_",2);
        parts[0].replace("_"," ");
        StringBuilder sb = new StringBuilder();
        sb.append(parts[1].substring(0,1).toUpperCase());
        sb.append(parts[1].substring(1) + " ("+parts[0]+")");
        return sb.toString();
    };

    public enum QueryResults {
        NOT_QUERIED {
            public String asString(){
            return "Not Queried";
        }}, NO_CONVERSION {
            @Override
            public String asString() {
                return "No Conversion";
            }
        }, SUCCESS {
            @Override
            public String asString() {
                return "Success";
            }
        };

        public abstract String asString();
        }

    private int combinedCount = 1;
    public String nf_ingredient_statement;
    private QueryResults queryResults = QueryResults.NOT_QUERIED;
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

    public void setKcal_calories(Double kcal_calories) {
        this.kcal_calories = kcal_calories;
    }

    public void setG_total_fat(Double g_total_fat) {
        this.g_total_fat = g_total_fat;
    }

    public void setMg_cholesterol(Double mg_cholesterol) {
        this.mg_cholesterol = mg_cholesterol;
    }

    public void setMg_sodium(Double mg_sodium) {
        this.mg_sodium = mg_sodium;
    }

    public void setG_total_carbohydrate(Double g_total_carbohydrate) {
        this.g_total_carbohydrate = g_total_carbohydrate;
    }

    public void setNf_dietary_fiber(Double nf_dietary_fiber) {
        this.nf_dietary_fiber = nf_dietary_fiber;
    }

    public void setG_sugars(Double g_sugars) {
        this.g_sugars = g_sugars;
    }

    public void setG_protein(Double g_protein) {
        this.g_protein = g_protein;
    }

    public void setNf_calories_from_fat(Double nf_calories_from_fat) {
        this.nf_calories_from_fat = nf_calories_from_fat;
    }

    public void setNf_saturated_fat(Double nf_saturated_fat) {
        this.nf_saturated_fat = nf_saturated_fat;
    }

    public void setNf_monounsaturated_fat(Double nf_monounsaturated_fat) {
        this.nf_monounsaturated_fat = nf_monounsaturated_fat;
    }

    public void setNf_polyunsaturated_fat(Double nf_polyunsaturated_fat) {
        this.nf_polyunsaturated_fat = nf_polyunsaturated_fat;
    }

    public void setNf_trans_fatty_acid(Double nf_trans_fatty_acid) {
        this.nf_trans_fatty_acid = nf_trans_fatty_acid;
    }

    public void setNf_vitamin_a_dv(Double nf_vitamin_a_dv) {
        this.nf_vitamin_a_dv = nf_vitamin_a_dv;
    }

    public void setNf_vitamin_c_dv(Double nf_vitamin_c_dv) {
        this.nf_vitamin_c_dv = nf_vitamin_c_dv;
    }

    public void setNf_calcium_dv(Double nf_calcium_dv) {
        this.nf_calcium_dv = nf_calcium_dv;
    }

    public void setNf_iron_dv(Double nf_iron_dv) {
        this.nf_iron_dv = nf_iron_dv;
    }

    public void setNf_potassium(Double nf_potassium) {
        this.nf_potassium = nf_potassium;
    }

    public void setNf_servings_per_container(Double nf_servings_per_container) {
        this.nf_servings_per_container = nf_servings_per_container;
    }

    public void setNf_serving_size_qty(Double nf_serving_size_qty) {
        this.nf_serving_size_qty = nf_serving_size_qty;
    }

    public void setNf_serving_size_unit(String nf_serving_size_unit) {
        this.nf_serving_size_unit = nf_serving_size_unit;
    }

    public void setG_serving_weight_grams(Double g_serving_weight_grams) {
        this.g_serving_weight_grams = g_serving_weight_grams;
    }

    public void setMetric_qty(Double metric_qty) {
        this.metric_qty = metric_qty;
    }

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
            queryResults = QueryResults.SUCCESS;
            scaleNutrition(mult);
        } else if(ing.getMeasurementType().toLowerCase().equals("g")) {
            double mult = ing.getQuantity()/g_serving_weight_grams;
            queryResults = QueryResults.SUCCESS;
            scaleNutrition(mult);
        } else
            {
            Log.e("NotEqualMeasureTypes", ing.getMeasurementType() + " vs " + nf_serving_size_unit);
            boolean found = false;

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
                        queryResults = QueryResults.SUCCESS;
                        Log.e("found matching conv",ing.getQuantity()+ing.getMeasurementType() + " to " +nf_serving_size_qty+ nf_serving_size_unit + " " + finalMulti);
                        scaleNutrition(finalMulti);
                        nf_serving_size_unit = ing.getMeasurementType();
                        nf_serving_size_qty = ing.getQuantity();
                        break;
                    }
                }
                if(unc.hasConversion(ing.getMeasurementType())){
                    Log.e("Found secondary FIRST", nf_serving_size_qty + " "+ nf_serving_size_unit);

                    if(unc.hasNameMatch(nf_serving_size_unit)){
                        Log.e("Found first type 2ND", nf_serving_size_qty + " "+ nf_serving_size_unit);
                        found = true;
                        Double multi = unc.getConversionMulti(ing.getMeasurementType());
                        //multi = multi/nf_serving_size_qty;
                        Double finalMulti = (nf_serving_size_qty/ing.getQuantity())*multi;
                        queryResults = QueryResults.SUCCESS;
                        Log.e("found matching conv",+nf_serving_size_qty+ nf_serving_size_unit  + " to "  + ing.getQuantity()+ing.getMeasurementType() + " " + finalMulti);
                        scaleNutrition(finalMulti);
                        nf_serving_size_unit = ing.getMeasurementType();
                        nf_serving_size_qty = ing.getQuantity();
                        break;
                    }
                }

            }
            if(!found) queryResults = QueryResults.NO_CONVERSION;
            if(!found) Log.e("No conversion available", ing.getMeasurementType() + " vs " + nf_serving_size_unit);
        }
    }
    public QueryResults getQueryResults(){
        return queryResults;
    }
    public Nutrition scaleNutrition(double amt){
        for (Field f: getClass().getDeclaredFields()) {
            try {
                if(f.get(this) instanceof Double){

                    Double temp = (Double) f.get(this);
                    //Log.e("scaling", f.getName() + ":" + String.valueOf(temp) + " to " + temp*amt);
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
    interface command{
        public void set(Double d);
    }
}

