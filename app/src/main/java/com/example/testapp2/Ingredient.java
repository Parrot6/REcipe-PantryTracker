package com.example.testapp2;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

public class Ingredient implements Serializable
{
    private String name = "";
    private double quantity = 0;
    private String measurementType = "none";
    private String additionalNote;
    private Nutrition nutrition = null;

    public Ingredient(String name, double quantity, String measurementType){
        this.name = name;
        this.quantity = quantity;
        this.measurementType = measurementType;
        this.additionalNote = "";
    }
    public Ingredient(String name){
        this.name = name;
        this.quantity = 1;
        this.measurementType = "Unknown";
        this.additionalNote = "";
    }
    public Ingredient(String name, double quantity){
        this.name = name;
        this.quantity = quantity;
        this.measurementType = "Unknown";
        this.additionalNote = "";
    }
    public Ingredient(String name, double quantity, String measurementType, String additionalNote){
        this.name = name;
        this.quantity = quantity;
        this.measurementType = measurementType;
        if(additionalNote.equals("Note")){
            this.additionalNote = "";
        } else this.additionalNote = additionalNote;
    }
    public Ingredient(String name, String quantity, String measurementType, String additionalNote){
        this.name = name;
        this.quantity = Double.parseDouble(quantity);
        this.measurementType = measurementType;
        if(additionalNote.equals("Note")){
            this.additionalNote = "";
        } else this.additionalNote = additionalNote;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String toString(){
        DecimalFormat df = new DecimalFormat("0.#");
        String Str = df.format(this.quantity) + " " + this.measurementType + " " +  this.name;
        if(additionalNote != "") Str += " -" + this.additionalNote;
        return Str;
    }

    public String getName() {
        return name;
    }
    public static String formatName(String oldname){
        String s = oldname.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0,1).toUpperCase());
        sb.append(s.substring(1));
        return sb.toString();
    }

    public Nutrition getNutrition() {
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }
}
