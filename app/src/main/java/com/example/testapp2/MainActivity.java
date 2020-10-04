package com.example.testapp2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity{

    private static final String API_URL = "https://reqres.in/api/users/2";
    private static final String USDA_API_KEY = "U0r6uOGQDq4xfdUQ565RKsgGSfQfhmyPjuQFFQxy";
    BottomNavigationView bottomNavigationView;

    static ArrayList<CartItem> currentCart = new ArrayList<>();
    static ArrayList<Recipe> recipes = new ArrayList<>();
    static ArrayList<SameNameIngredients> pantry = new ArrayList<>();
    Context context;
    //Fragments
    public static Recipe.RecipeType CURRENT_SORT = Recipe.RecipeType.NONE;
    private static final String[] types = {"tsp","tbsp","fl oz","cup","Other Value","fl pt","ft qt","gal","mL","L"};
    private static String[] quantities = {"1","2","3","4","Other Value"};
    private String fileName = "pantryTest19";
    public static ArrayList<UnitConversion> conversions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        readFile(context, true);
            //OkHTTP
     //   OkHttpHandler okHttpHandler= new OkHttpHandler();
    //   okHttpHandler.execute(API_URL);

        UnitConversion tbsp = new UnitConversion(1.0,"tablespoon").addVariantNames(new ArrayList<String>(Arrays.asList("tbsp","tbsp.","tblspn","tbsp.")));
        ;
        UnitConversion tspn = new UnitConversion(1.0,"teaspoon").addVariantNames(new ArrayList<String>(Arrays.asList("tspn","tspn.","tsp","tsp.")));
        UnitConversion cup = new UnitConversion(1.0,"cup").addVariantNames(new ArrayList<String>(Arrays.asList("cup.")));
        UnitConversion pint = new UnitConversion(1.0,"pint").addVariantNames(new ArrayList<String>(Arrays.asList("pint.","pt")));
        UnitConversion quart = new UnitConversion(1.0,"quart").addVariantNames(new ArrayList<String>(Arrays.asList("quart.","qt")));
        UnitConversion gal = new UnitConversion(1.0,"gallon").addVariantNames(new ArrayList<String>(Arrays.asList("gal.","gal","gall")));
        UnitConversion L = new UnitConversion(1.0,"Liter").addVariantNames(new ArrayList<String>(Arrays.asList("L.","L","lit.")));
        UnitConversion mL = new UnitConversion(1.0,"mL").addVariantNames(new ArrayList<String>(Arrays.asList("mL.","milliLiter")));
        UnitConversion ounce = new UnitConversion(1.0, "ounce").addVariantNames(new ArrayList<String>(Arrays.asList("oz.","oz")));
        UnitConversion fluidounce = new UnitConversion(1.0, "fluid ounce").addVariantNames(new ArrayList<String>(Arrays.asList("fl oz.","fl oz")));
        UnitConversion pound = new UnitConversion(1.0, "pound").addVariantNames(new ArrayList<String>(Arrays.asList("lb.","lb")));
        UnitConversion gram = new UnitConversion(1.0, "gram").addVariantNames(new ArrayList<String>(Arrays.asList("g","g.")));
        UnitConversion dash = new UnitConversion(1.0, "dash");
        dash.addEquivUnitConversion(new UnitConversion(1.0/8.0, tspn)).addEquivUnitConversion(new UnitConversion(1.0/24.0, tbsp));
        fluidounce.addEquivUnitConversion(new UnitConversion(30.0, mL)).addEquivUnitConversion(new UnitConversion(.125, cup));
        ounce.addEquivUnitConversion(new UnitConversion(28.0, gram));
        tspn.addEquivUnitConversion(new UnitConversion(5.0, mL)).addEquivUnitConversion(new UnitConversion(.005, L)).addEquivUnitConversion(new UnitConversion(3.0, tbsp));
        tspn.addEquivUnitConversion(new UnitConversion(8.0, dash));
        pound.addEquivUnitConversion(new UnitConversion(113.0*4, gram));
        tbsp.addEquivUnitConversion(new UnitConversion(3.0, tspn)).addEquivUnitConversion(new UnitConversion(15.0, mL)).addEquivUnitConversion(new UnitConversion(.015, L));
        tbsp.addEquivUnitConversion(new UnitConversion(1.0/16.0, cup)).addEquivUnitConversion(new UnitConversion(240.0, mL)).addEquivUnitConversion(new UnitConversion(.24,L));
        tbsp.addEquivUnitConversion(new UnitConversion(24.0, dash));
        L.addEquivUnitConversion(new UnitConversion(1000.0, mL));
        cup.addEquivUnitConversion(new UnitConversion(250.0, mL)).addEquivUnitConversion(new UnitConversion(.25, L)).addEquivUnitConversion(new UnitConversion(16.0, tbsp));
        pint.addEquivUnitConversion(new UnitConversion(500.0, mL)).addEquivUnitConversion(new UnitConversion(.5, L));;
        quart.addEquivUnitConversion(new UnitConversion(.95, L)).addEquivUnitConversion(new UnitConversion(950.0, mL));;
        gal.addEquivUnitConversion(new UnitConversion(3.8, L)).addEquivUnitConversion(new UnitConversion(3800.0, mL));
        conversions.addAll(new ArrayList<UnitConversion>(Arrays.asList(tspn,cup,pint,quart,gal,L,mL,ounce,fluidounce,pound,gram)));
        if(recipes.size() == 0) {
            //examples of data
            Ingredient ingred1 = new Ingredient("Chicken", 10, "oz");
            Ingredient ingred2 = new Ingredient("Turkey", 10, "oz");
            Ingredient ingred3 = new Ingredient("Chicken", 2, "lb");
            Ingredient ingred4 = new Ingredient("Milk", 10, "cup");
            Ingredient ingred5 = new Ingredient("Milk", 6, "fl oz");
            Ingredient ingred6 = new Ingredient("Parmesan", 2, "cup");
            Ingredient ingred7 = new Ingredient("Parmesan", 100, "g");
            Ingredient ingred8 = new Ingredient("Salt", 10, "tsp");
            Ingredient ingred9 = new Ingredient("Pepper", 10, "tsp");
            ArrayList<Ingredient> ingredients1 = new ArrayList<>(Arrays.asList(ingred1, ingred2, ingred3, ingred4, ingred5));
            ArrayList<Ingredient> ingredients2 = new ArrayList<>(Arrays.asList(ingred2, ingred3, ingred7, ingred5, ingred6));
            ArrayList<Ingredient> ingredients3 = new ArrayList<>(Arrays.asList(ingred4, ingred5, ingred6, ingred7, ingred8));
            ArrayList<Ingredient> ingredients4 = new ArrayList<>(Arrays.asList(ingred5, ingred2, ingred6, ingred7, ingred9));
            ArrayList<Ingredient> ingredients5 = new ArrayList<>(Arrays.asList(ingred2, ingred3, ingred5, ingred7, ingred9));

            addRecipe(0, new Recipe("ChickenRand", ingredients1), 1);
            addRecipe(0, new Recipe("TurkeyBrand", ingredients2), 2);
            addRecipe(0, new Recipe("Seasoned Milk", ingredients3), 3);
            addRecipe(0, new Recipe("Spicy Milk", ingredients4), 4);
            addRecipe(0, new Recipe("Spicy Turkey", ingredients4), 4);
            makeSummaryRecipeIngredients();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment = null;
                        switch (item.getItemId()) {
                            case R.id.nav_Home:
                                fragment = new ViewRecipesFragment();
                                break;
                            case R.id.nav_shopping_list:
                                fragment = new EditCartFragment();
                                break;
                            case R.id.nav_pantry:
                                fragment = new PantryFragment();
                                break;
                            case R.id.nav_search:
                                fragment = new SearchFragment();
                                break;
                    }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                        return true;
                    }
                });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ViewRecipesFragment()).commit();


    }
    public class OkHttpHandler extends AsyncTask {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected Object doInBackground(Object[] objects) {
            Request.Builder builder = new Request.Builder();
	                builder.url("https://api.nal.usda.gov/fdc/v1/?limit=1&api_key="+USDA_API_KEY)
                    .get();
                    //.addHeader("x-rapidapi-host", "the-cocktail-db.p.rapidapi.com");
                    //.addHeader("X-Api-Key", USDA_API_KEY);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                String s;
                if (response.body() == null) throw new AssertionError();
                s = response.body().string();
                Log.e("API CALL", s);
                return s;

            }catch (Exception e){
                Log.e("API CALL", "EXCEPTION");
                e.printStackTrace();
            }
            return null;
        }
    }
    private static void makeSummaryRecipeIngredients() {
        ArrayList<SameNameIngredients> ingredientSummary = new ArrayList<>();
        //add pantry from file
        for (SameNameIngredients sni: pantry
        ) {
            ArrayList<Ingredient> temp1 = sni.getIngredientSummary();
            boolean Added1 = false;
            for (int i = 0; i < temp1.size(); i++) {
                Ingredient ingred = temp1.get(i);
                if (ingred.getQuantity() <= 0) continue;
                for (int k = 0; k < ingredientSummary.size(); k++) {
                    if (ingredientSummary.size() == 0) {
                        ingredientSummary.add(new SameNameIngredients(ingred));
                        Added1 = true;
                        break;
                    } else if (Added1 = ingredientSummary.get(k).handleNewIngredient(ingred)) {
                        break;
                    }
                }
                if (!Added1) ingredientSummary.add(new SameNameIngredients(ingred));

                //pantry = ingredientSummary;
            }
        }
        //add ingredients from recipes
        for (Recipe rec: recipes
             ) {
            ArrayList<Ingredient> temp = rec.getIngredients();
            //allRecipeIngred.add(new )
            //ArrayList<Ingredient> temp = cartData.get(outer).getIngredientsTimesCart();
            boolean Added = false;
            for (int i = 0; i < temp.size(); i++) { //for EACH INGREDIENT PER CART ITEM
                Ingredient ingred = new Ingredient(temp.get(i).getName(), 0, temp.get(i).getMeasurementType(), temp.get(i).getAdditionalNote());

                for (int k = 0; k < ingredientSummary.size(); k++) {
                    if(ingredientSummary.size() == 0){
                        ingredientSummary.add(new SameNameIngredients(ingred));
                        Added = true;
                        break;
                    } else if(Added = ingredientSummary.get(k).handleNewIngredient(ingred)){ break;}
                }
                if(!Added) ingredientSummary.add(new SameNameIngredients(ingred));
            }
            pantry = ingredientSummary;
        }
    }

    public static class SavePackage implements Serializable {
        public ArrayList<Recipe> recipes1 = new ArrayList<>();
        public ArrayList<CartItem> currentCart1 = new ArrayList<>();
        public ArrayList<SameNameIngredients> pantry1 = new ArrayList<>();
        public String Current_Sort;
        public SavePackage(ArrayList<Recipe> rec, ArrayList<CartItem> cart, ArrayList<SameNameIngredients> pant, Recipe.RecipeType current_Sort){
            recipes1.addAll(rec);
            currentCart1.addAll(cart);
            pantry1.addAll(pant);
            Current_Sort = current_Sort.toString();
        }
    }
    public void Save(){
        createFile(context, true);
        writeFile(context, true);
    }

    private void createFile(Context context, boolean isPersistent) {
        File file;
        if (isPersistent) {
            file = new File(context.getFilesDir(), fileName);
        } else {
            file = new File(context.getCacheDir(), fileName);
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                Toast.makeText(context, String.format("File %s has been created", fileName), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, String.format("File %s creation failed", fileName), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, String.format("File %s already exists", fileName), Toast.LENGTH_SHORT).show();
        }
    }

    //src: https://github.com/learntodroid/FileIOTutorial/blob/master/app/src/main/java/com/learntodroid/fileiotutorial/InternalStorageActivity.java
    private void writeFile(Context context, boolean isPersistent) {
        try {
            FileOutputStream fileOutputStream;
            ObjectOutputStream oos;
            if (isPersistent) {
                fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fileOutputStream);
            } else {
                File file = new File(context.getCacheDir(), fileName);
                fileOutputStream = new FileOutputStream(file);
                oos = new ObjectOutputStream(fileOutputStream);
            }
            SavePackage sp = new SavePackage(recipes, currentCart, pantry, CURRENT_SORT);
            oos.writeObject(sp);
            Toast.makeText(context, String.format("Write to %s successful", fileName), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format("Write to file %s failed", fileName), Toast.LENGTH_SHORT).show();
        }
    }

    private void readFile(Context context, boolean isPersistent) {
        try {
            FileInputStream fileInputStream;
            if (isPersistent) {
                fileInputStream = context.openFileInput(fileName);
            } else {
                File file = new File(context.getCacheDir(), fileName);
                fileInputStream = new FileInputStream(file);
            }
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            SavePackage sp = (SavePackage) ois.readObject();
            recipes = sp.recipes1;
            currentCart = sp.currentCart1;
            pantry = sp.pantry1;
            if(sp.Current_Sort != null) CURRENT_SORT = Recipe.RecipeType.valueOf(sp.Current_Sort);
            ois.close();
            Toast.makeText(context, String.format("Read from file %s successful", fileName), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format("Read from file %s failed", fileName), Toast.LENGTH_SHORT).show();

        }
    }

    private void deleteFile(Context context, boolean isPersistent) {
        File file;
        if (isPersistent) {
            file = new File(context.getFilesDir(), fileName);
        } else {
            file = new File(context.getCacheDir(), fileName);
        }
        if (file.exists()) {
            file.delete();
            Toast.makeText(context, String.format("File %s has been deleted", fileName), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, String.format("File %s doesn't exist", fileName), Toast.LENGTH_SHORT).show();
        }
    }


    public static ArrayList<Recipe>  getRecipes(){
        return recipes;
    }
    public static ArrayList<CartItem>  getCart(){
        return currentCart;
    }
    public static ArrayList<SameNameIngredients>  getPantry(){
        return pantry;
    }
    @Override
    public void onPause() {
        Save();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        Save();
        super.onDestroy();
    }
    public static void removeRecipe(int index){
        recipes.remove(index);
        currentCart.remove(index);
        makeSummaryRecipeIngredients();
    }
    public static void recipeSortPress(Recipe.RecipeType pressed){
        if(pressed != CURRENT_SORT){
            CURRENT_SORT = pressed;
        } else {
            CURRENT_SORT = Recipe.RecipeType.NONE;
        }
        sortRecipeData();
    }
    private static void sortRecipeData(){
        ArrayList<Recipe> sortedList = new ArrayList<>();
        ArrayList<Recipe> offType = new ArrayList<>();
        if(MainActivity.CURRENT_SORT == Recipe.RecipeType.NONE) {
            Collections.sort(recipes, new Comparator<Recipe>() {
                @Override
                public int compare(Recipe recipe, Recipe t1) {
                    return recipe.getID() - t1.getID();
                }
            });
            return;
        }
        for (Recipe rec: recipes) {
            if(rec.getRecipeType() == MainActivity.CURRENT_SORT) sortedList.add(rec);
            else offType.add(rec);
        }
        sortedList.addAll(offType);
        recipes.clear();
        recipes.addAll(sortedList);
        sortCartData(); //KEEP CART DATA ARRANGED SAME AS RECIPES
    }
    private static void sortCartData(){
        ArrayList<CartItem> sortedList = new ArrayList<>();
        ArrayList<CartItem> offType = new ArrayList<>();
        if(MainActivity.CURRENT_SORT == Recipe.RecipeType.NONE) {
            Collections.sort(currentCart, new Comparator<CartItem>() {
                @Override
                public int compare(CartItem cart1, CartItem cart2) {
                    return cart1.getRecipe().getID() - cart2.getRecipe().getID();
                }
            });
            return;
        }
        for (CartItem ci: currentCart) {
            if(ci.getRecipe().getRecipeType() == MainActivity.CURRENT_SORT) sortedList.add(ci);
            else offType.add(ci);
        }
        sortedList.addAll(offType);
        currentCart.clear();
        currentCart.addAll(sortedList);
    }
    public static void addRecipe(int index, Recipe rec){
        recipes.add(index, rec);
        currentCart.add(index, new CartItem(0, rec));
        makeSummaryRecipeIngredients();
    }
    public static void addRecipe(int index, Recipe rec, int cartQuant){
        recipes.add(index, rec);
        currentCart.add(index, new CartItem(cartQuant, rec));
        makeSummaryRecipeIngredients();
    }
    public static void addRecipes(int index, ArrayList<Recipe> rec, int cartQuant){
        for (Recipe recipe: rec
             ) {
        recipes.add(index, recipe);
        currentCart.add(index, new CartItem(cartQuant, recipe));
        }
        makeSummaryRecipeIngredients();
    }

    public static String[] getTypes(){
        return types;
    }
    public static String[] getQuantities(){
        return quantities;
    }

    public static Bitmap loadImageFromStorage(String filename)
    {
        //File directory =  context.getApplicationContext().getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f= new File(filename);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}