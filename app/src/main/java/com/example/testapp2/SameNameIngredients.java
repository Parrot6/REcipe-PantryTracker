package com.example.testapp2;

import java.io.Serializable;
import java.util.ArrayList;

public class SameNameIngredients implements Serializable {
    private ArrayList<Ingredient> mData = new ArrayList<>();
    public String ingredName;
    public SameNameIngredients(Ingredient ingr){
        ingredName = ingr.getName().toLowerCase();
        ingredName = ingredName.substring(0, 1).toUpperCase() + ingredName.substring(1);
        mData.add(ingr);
    }
    public boolean checkNameMatch(String str){
        if(ingredName.toLowerCase().equals(str.toLowerCase())){
            return true;
        }
        return false;
    }

    private void incOrAddIngredientVariant(Ingredient ingr){
        boolean already = false;
        for (int i = 0; i < mData.size(); i++){
            if(mData.get(i).getMeasurementType().toLowerCase().equals(ingr.getMeasurementType().toLowerCase())) {
                mData.get(i).setQuantity(mData.get(i).getQuantity() + ingr.getQuantity());
                already = true;
                break;
            }

        }
        if(!already) mData.add(ingr);
    }
    public int size(){
        return mData.size();
    }
    public String getIngredName(){
        return ingredName;
    }
    public ArrayList<Ingredient> getIngredientSummary(){
        return mData;
    }

    public boolean handleNewIngredient(Ingredient ingr){
        if(checkNameMatch(ingr.getName())){
            incOrAddIngredientVariant(ingr);
            return true;
        }
        return false;
    }
    public boolean isStocked(Ingredient ing){
        boolean inStock = false;
        if(!checkNameMatch(ing.getName())) return false;
        for (Ingredient ingred: mData
             ) {
            if(ingred.getMeasurementType().toLowerCase().equals(ing.getMeasurementType().toLowerCase())){
                if(ingred.getQuantity() >= ing.getQuantity()){
                    inStock = true;
                }
            }
        }
        return inStock;
    }
    public double getStocked(Ingredient ing){
        if(!checkNameMatch(ing.getName())) return 0;
        for (Ingredient ingred: mData
        ) {
            if(ingred.getMeasurementType().toLowerCase().equals(ing.getMeasurementType().toLowerCase())){
                return ingred.getQuantity();
            }
        }
        return 0;
    }
}
