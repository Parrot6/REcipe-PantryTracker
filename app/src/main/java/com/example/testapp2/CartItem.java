package com.example.testapp2;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class CartItem implements Serializable {
    private int quantity = 0;
    private Recipe recipe;
    public CartItem(int quant, Recipe rec){
        quantity = quant;
        this.recipe = rec;
    }
    public Recipe getRecipe(){
        return recipe;
    }
    void incrementQuant(){
        quantity++;
    }
    void decrementQuant(){
        if(quantity >= 1) quantity--;
    }
    public ArrayList<Ingredient> getIngredients(){
        return recipe.getIngredients();
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int newQuantity){
        if(newQuantity >= 0) quantity = newQuantity;
        else quantity = 0;
    }
    public ArrayList<Ingredient> getIngredientsTimesCart(){
/*
        Recipe rec = new Recipe(this.recipe.getRecipeTitle(), this.recipe.getIngredients());
        for (Ingredient ingr: rec.getIngredients()
             ) {
           // Log.e("quant going in = ", ingr.getQuantity()+"");
           // Log.e("getCardIng", quantity+"");
            double tq = ingr.getQuantity();
            tq =
            ingr.setQuantity();
           // Log.e("quant going out = ", ingr.getQuantity()+"");

        }
        return rec.getIngredients();
        */
        Recipe temp = new Recipe(this.recipe.getRecipeTitle(),  this.recipe.getIngredients());
        ArrayList<Ingredient> rec = new ArrayList<>();

        //rec.addAll(temp.getIngredients());

        for (Ingredient ingr : temp.getIngredients()) {
            Log.e("returnCartIngr", ingr.getName() + ingr.getQuantity());
            rec.add(new Ingredient(ingr.getName() ,ingr.getQuantity()*quantity, ingr.getMeasurementType(), ingr.getAdditionalNote()));
        }
        return rec;
    }
}
