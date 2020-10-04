package com.example.testapp2;

import java.io.Serializable;
import java.util.ArrayList;

public class UnitConversion implements Serializable
{
    ArrayList<String> names = new ArrayList();
    final Double quantity;
    ArrayList<UnitConversion> equalTo = new ArrayList<>();
    public UnitConversion(Double quant, String name){
        quantity = quant;
        names.add(name);
    }
    public UnitConversion(Double quant, UnitConversion name){
        quantity = quant;
        names = name.getNames();
    }
    public ArrayList<String> getNames(){
        return names;
    }
    public boolean hasNameMatch(String name){
        for (String str :
                names) {
            if(name.toLowerCase().equals(str.toLowerCase())) return true;
        }
        return false;
    }
    public Double getQuantity() {
        return quantity;
    }

    public boolean hasConversion(String name){
        for (UnitConversion uc:equalTo
             ) {
            for (String s :
                    uc.getNames()) {
                if(name.toLowerCase().contains(s.toLowerCase())) return true;
            }
        }
        return false;
    }
    private UnitConversion getConversion(String name){
        for (UnitConversion uc:equalTo
        ) {
            for (String s :
                    uc.getNames()) {
                if(name.toLowerCase().contains(s.toLowerCase())) return uc;
            }
        }
        return null;
    }
    public Double getConversionMulti(String name){
        if(hasConversion(name)) {
            Double temp = getConversion(name).getQuantity();
            return (temp/quantity);
        }
        return null;
    }
    public UnitConversion addEquivUnitConversion(UnitConversion newEquals){
        equalTo.add(newEquals);
        return this;
    }
    public UnitConversion addVariantNames(ArrayList<String> str){
        names.addAll(str);
        return this;
    }
    public void addVariantNames(String str){
        names.add(str);
    }

}
