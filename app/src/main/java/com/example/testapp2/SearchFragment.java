package com.example.testapp2;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.nio.charset.StandardCharsets.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView searchTerm;
    private Button search;
    private TextView searchResults;


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchTerm = view.findViewById(R.id.text_search_input);
        search = view.findViewById(R.id.button_search_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OkHTTP
                  // OkHttpHandler okHttpHandler = new OkHttpHandler(searchTerm.getText().toString());
                  // okHttpHandler.execute(API_URL);
                NutritionQuery nq = new NutritionQuery(new Ingredient(searchTerm.getText().toString(),1,"Serving"));
                while(!nq.isFinished()){

                }
                searchResults.setText(nq.getNutrition().toString());
                searchResults.setText(nq.getNutrition().toStringVar());
            }
        });

        searchResults = view.findViewById(R.id.text_search_results);
        return view;
    }

    private static final String API_URL = "https://reqres.in/api/users/2";
    private static final String USDA_API_KEY = "U0r6uOGQDq4xfdUQ565RKsgGSfQfhmyPjuQFFQxy";
    private static final String Nutritionix_API_KEY = "7a33fb023dmsh26f191c59816e64p1168dfjsn5e1e67646390";
    private static final String Nutritionix_API_URL = "nutritionix-api.p.rapidapi.com";

    public class OkHttpHandler extends AsyncTask {

        OkHttpClient client = new OkHttpClient();
        String query = "";
        public OkHttpHandler(String toQuery){
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
                Nutrition currentSearch = new Nutrition(fields, new Ingredient("fake",1,"serving"));


                double cals = fields.getDouble("nf_calories");
                double servSize = fields.getDouble("nf_serving_weight_grams");

                String temp = request.toString() + "\n"
                        + fields.toString() + "\n"
                        + "Calories: " + cals + "\n"
                        + "Serving Size (g): " + servSize;
                searchResults.setText(temp);

                return s;

            }catch (Exception e){
                Log.e("API CALL", "EXCEPTION");
                e.printStackTrace();
            }
            return null;
        }
    }
}