package com.example.testapp2;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static com.example.testapp2.R.*;

public class Recipe implements Serializable {
    private static int UniqueID = 0; //global id count

    private int ID;
    private Date doc;
    private String recipeTitle = "";
    private ArrayList<Ingredient> Ingredients = new ArrayList<>();
    private String recipeInstructions = "";
    private Nutrition nutritionSummary;
    private double rating = 0; //0-10.0
    public void setNutritionSummary(Nutrition nut){
        nutritionSummary = nut;
    }
    public Nutrition getNutritionSummary(){
        return nutritionSummary;
    }
    public int getID() {
        return ID;
    }

    public enum RecipeType {
        AlcoholicDrink{
            @Override
            public int getDrawable() {
                return drawable.ic_iconmonstr_beer_3;
            }
        }, Desert {
            @Override
            public int getDrawable() {
                return drawable.ic_iconmonstr_candy_13;
            }
        }, Drink {
            @Override
            public int getDrawable() {
                return drawable.ic_iconmonstr_drink;
            }
        }, Entree {
            @Override
            public int getDrawable() {
                return drawable.ic_iconmonstr_entree;
            }
        }, NONE {
            @Override
            public int getDrawable() {
                return drawable.ic_baseline_library_none;
            }
        }, Side {
            @Override
            public int getDrawable() {
                return drawable.ic_iconmonstr_side;
            }
        };
        public abstract int getDrawable();
        }

    private RecipeType recipeType = RecipeType.NONE;
    private ArrayList<String> savedImageLocations = new ArrayList<>();
    private transient Bitmap recipeIcon;

    public void setRecipeType(RecipeType rt){
        recipeType = rt;
    }
    public RecipeType getRecipeType(){
        return recipeType;
    }
    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        Ingredients = ingredients;
        //Ingredients.addAll(ingredients);
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Recipe(String name) {
        recipeTitle = name;
        ID = UniqueID;
        UniqueID++;
        doc = new Date();
    }
    public Recipe(String name, ArrayList<Ingredient> ingr) {
        recipeTitle = name;
        Ingredients = ingr;
        ID = UniqueID;
        UniqueID++;
        doc = new Date();
    }
    public Recipe(String name, Ingredient ingr) {
        recipeTitle = name;
        Ingredients.add(ingr);
        ID = UniqueID;
        UniqueID++;
        doc = new Date();
    }
    public ArrayList<Ingredient> getIngredients(){
        return Ingredients;
    }

    public String getIngredientsAsStringList(){
        String ingredientsSoFar = "";
        for(int i = 0; i < getIngredients().size(); i++){
            ingredientsSoFar += getIngredients().get(i).toString();
            if(i < getIngredients().size() -1) ingredientsSoFar += " \n";
        }
        return ingredientsSoFar;
    }
    public void addImage(String location){
        savedImageLocations.add(0, location);
    }
    public void setIcon(Bitmap icon){
        recipeIcon = icon;
    }
    public Bitmap getRecipeIcon(){
        if(recipeIcon == null && savedImageLocations.size() > 0) recipeIcon = MainActivity.loadImageFromStorage(savedImageLocations.get(0));
        return recipeIcon;
    }
    public ArrayList<String> getSavedImageLocations(){
        return savedImageLocations;
    }
}
