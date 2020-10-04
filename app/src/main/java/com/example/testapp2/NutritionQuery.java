package com.example.testapp2;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NutritionQuery {

    private static final String API_URL = "https://reqres.in/api/users/2";
    private static final String USDA_API_KEY = "U0r6uOGQDq4xfdUQ565RKsgGSfQfhmyPjuQFFQxy";
    private static final String Nutritionix_API_KEY = "7a33fb023dmsh26f191c59816e64p1168dfjsn5e1e67646390";
    private static final String Nutritionix_API_URL = "nutritionix-api.p.rapidapi.com";
    Nutrition lastQuery;
    Boolean finished;
    Ingredient ing;
    public boolean isFinished(){
        return finished;
    }
    public Nutrition getNutrition(){
        return lastQuery;
    }
    public NutritionQuery(Ingredient ingredient){
        finished = false;
        ing = ingredient;
        OkHttpHandler okHttpHandler = new OkHttpHandler(ingredient.getName());
        okHttpHandler.execute(API_URL);
    }
    public void queryAnother(String ingredient){
        finished = false;
        OkHttpHandler okHttpHandler = new OkHttpHandler(ingredient);
        okHttpHandler.execute(API_URL);
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
            String encoded = "";
            try {
                encoded = URLEncoder.encode("nf_ingredient_statement," +
                        "item_name," +
                        "nf_calories," +
                        "nf_calories_from_fat," +
                        "nf_total_fat," +
                        "nf_saturated_fat," +
                        "nf_monounsaturated_fat," +
                        "nf_polyunsaturated_fat," +
                        "nf_trans_fatty_acid," +
                        "nf_cholesterol," +
                        "nf_sodium," +
                        "nf_total_carbohydrate," +
                        "nf_dietary_fiber," +
                        "nf_sugars," +
                        "nf_protein," +
                        "nf_vitamin_a_dv," +
                        "nf_vitamin_c_dv," +
                        "nf_calcium_dv," +
                        "nf_iron_dv," +
                        "nf_potassium," +
                        "nf_servings_per_container," +
                        "nf_serving_size_qty," +
                        "nf_serving_size_unit," +
                        "nf_serving_weight_grams," +
                        "metric_qty," +
                        "metric_uom", UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url("https://nutritionix-api.p.rapidapi.com/v1_1/search/" + query + "?fields=" + encoded)
                    .get()
                    .addHeader("x-rapidapi-host", Nutritionix_API_URL)
                    .addHeader("x-rapidapi-key", Nutritionix_API_KEY)
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
                JSONObject jsonObject = jsonArray.getJSONObject(0); // top result

                JSONObject fields = jsonObject.getJSONObject("fields");
                lastQuery = new Nutrition(fields, ing);
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