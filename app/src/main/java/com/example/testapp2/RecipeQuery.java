package com.example.testapp2;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RecipeQuery {

    private static final String API_APP_ID = "a7925b0c";
    private static final String EDAMAM_API_KEY = "16c8e83702e352fcdf951082fa668c10";
    private static final String Nutritionix_API_URL = "http://api.yummly.com/v1";
    ArrayList<Nutrition> lastQuery = new ArrayList<>();
    int saveXqueries = 3;
    Boolean finished;
    Ingredient ing;
    public boolean isFinished(){
        return finished;
    }
    public ArrayList<Nutrition> getNutrition(){
        return lastQuery;
    }

    //check if any have successful conversion
    public Nutrition getBestResult(){
        for (Nutrition nut :
                lastQuery) {
            if(nut.getQueryResults() == Nutrition.QueryResults.SUCCESS) return nut;
        }
        return lastQuery.get(0);
    }
    public RecipeQuery(Ingredient ingredient){
        finished = false;
        ing = ingredient;
        OkHttpHandler okHttpHandler = new OkHttpHandler(ingredient.getName());
     //   okHttpHandler.execute(API_URL);
    }
    public void queryAnother(String ingredient){
        finished = false;
        OkHttpHandler okHttpHandler = new OkHttpHandler(ingredient);
     //   okHttpHandler.execute(API_URL);
    }

    public class OkHttpHandler extends AsyncTask {

        OkHttpClient client = new OkHttpClient();
        String query = "";

        public OkHttpHandler(String toQuery) {
            query = URLEncoder.encode(toQuery);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Object doInBackground(Object[] objects) {

            //builder.url("https://api.nal.usda.gov/fdc/v1/foods/search?query=" + searchTerm.getText().toString() + "&limit=1&api_key="+USDA_API_KEY)
            //        .get();

            Request request = new Request.Builder()
                    .url("https://nutritionix-api.p.rapidapi.com/v1_1/search/" + query)
                    .get()
                    .addHeader("x-rapidapi-host", Nutritionix_API_URL)
                 //   .addHeader("x-rapidapi-key", Food2Fork_API_KEY)
                    .build();

            //.addHeader("x-rapidapi-host", "the-cocktail-db.p.rapidapi.com");
            //.addHeader("X-Api-Key", USDA_API_KEY);
            //Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                String s;
                if (response.body() == null) throw new AssertionError();
                s = response.body().string();

                JSONObject json = new JSONObject(s);
                JSONArray jsonArray = json.getJSONArray("hits");
                for(int i = 0; i < saveXqueries; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i); // top result
                    JSONObject fields = jsonObject.getJSONObject("fields");
                    lastQuery.add(new Nutrition(fields, ing));
                }
                finished = true;

                return s;

            } catch (Exception e) {
                Log.e("API CALL", "EXCEPTION");
                e.printStackTrace();
                finished = true;
            }
            return null;
        }
    }
}