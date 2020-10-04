package com.example.testapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class ViewRecipesFragment extends Fragment implements RecipesAdapter.MyClickListener  {


    private ArrayList<Recipe> recipes;
    private static int REQUEST_CODE = 50;
    private RecipesAdapter adapter;
    private RecyclerView recyclerView;
    private RecipesAdapter.MyClickListener thislistener;

    private Context context;

    public ViewRecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context mcontext) {
        super.onAttach(mcontext);
        context = mcontext;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
            recipes = MainActivity.getRecipes();
            context = getActivity().getApplicationContext();
            thislistener = this;
           // mParam2 = getArguments().getString(ARG_PARAM2);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_recipes, container, false);

        adapter = new RecipesAdapter(context, recipes, this);
        recyclerView = view.findViewById(R.id.mainList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Button addRecipe = view.findViewById(R.id.button_addRecipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipe(view);
            }
        });
        View sortBarView = view.findViewById(R.id.view_recipes_sortby);
        SortBar sortBar = new SortBar(sortBarView);

        return view;
    }
    private class SortBar{
        View sortBar;
        ImageButton sort_rt_alcohol;
        ImageButton sort_rt_desert;
        ImageButton sort_rt_drink;
        ImageButton sort_rt_entree;
        ImageButton sort_rt_side;

        public SortBar(View view){
            sortBar = view;
            sort_rt_alcohol = sortBar.findViewById(R.id.sortby_beer);
            sort_rt_alcohol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(Recipe.RecipeType.AlcoholicDrink);;
                }
            });
            sort_rt_desert = sortBar.findViewById(R.id.sortby_desert);
            sort_rt_desert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(Recipe.RecipeType.Desert);
                }
            });
            sort_rt_drink = sortBar.findViewById(R.id.sortby_beverage);
            sort_rt_drink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(Recipe.RecipeType.Drink);
                }
            });
            sort_rt_entree = sortBar.findViewById(R.id.sortby_entree);
            sort_rt_entree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(Recipe.RecipeType.Entree);
                }
            });
            sort_rt_side = sortBar.findViewById(R.id.sortby_side);
            sort_rt_side.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(Recipe.RecipeType.Side);
                }
            });
            updateSelectedImage();
        }
        private void clicked(Recipe.RecipeType thisClick){
            MainActivity.recipeSortPress(thisClick);
            adapter = new RecipesAdapter(context, recipes, thislistener);
            recyclerView.setAdapter(adapter);
            updateSelectedImage();
        }
        public void updateSelectedImage(){
            int selected = getResources().getColor(R.color.activeSort);
            switch(MainActivity.CURRENT_SORT){
                case AlcoholicDrink:
                    sort_rt_alcohol.setColorFilter(selected);
                    sort_rt_desert.setColorFilter(R.color.black);
                    sort_rt_drink.setColorFilter(R.color.black);
                    sort_rt_entree.setColorFilter(R.color.black);
                    sort_rt_side.setColorFilter(R.color.black);
                    break;
                case Drink:
                    sort_rt_alcohol.setColorFilter(R.color.black);
                    sort_rt_desert.setColorFilter(R.color.black);
                    sort_rt_drink.setColorFilter(selected);
                    sort_rt_entree.setColorFilter(R.color.black);
                    sort_rt_side.setColorFilter(R.color.black);
                    break;
                case Side:
                    sort_rt_alcohol.setColorFilter(R.color.black);
                    sort_rt_desert.setColorFilter(R.color.black);
                    sort_rt_drink.setColorFilter(R.color.black);
                    sort_rt_entree.setColorFilter(R.color.black);
                    sort_rt_side.setColorFilter(selected);
                    break;
                case Desert:
                    sort_rt_alcohol.setColorFilter(R.color.black);
                    sort_rt_desert.setColorFilter(selected);
                    sort_rt_drink.setColorFilter(R.color.black);
                    sort_rt_entree.setColorFilter(R.color.black);
                    sort_rt_side.setColorFilter(R.color.black);
                    break;
                case Entree:
                    sort_rt_alcohol.setColorFilter(R.color.black);
                    sort_rt_desert.setColorFilter(R.color.black);
                    sort_rt_drink.setColorFilter(R.color.black);
                    sort_rt_entree.setColorFilter(selected);
                    sort_rt_side.setColorFilter(R.color.black);
                    break;
                case NONE:
                    sort_rt_alcohol.setColorFilter(R.color.black);
                    sort_rt_desert.setColorFilter(R.color.black);
                    sort_rt_drink.setColorFilter(R.color.black);
                    sort_rt_entree.setColorFilter(R.color.black);
                    sort_rt_side.setColorFilter(R.color.black);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void onView(int layoutPosition) {
        Intent intent = new Intent(getActivity(), DisplayRecipe.class);
        intent.putExtra("Recipe", recipes.get(layoutPosition));
        intent.putExtra("Index", layoutPosition);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void addRecipe(View view) {
        EditText tv = (EditText) getActivity().findViewById(R.id.EditText_RecipeName);
        String temp = tv.getText().toString();
        if(temp.equals("")) return;
        MainActivity.addRecipe(0, new Recipe(temp));
        //SaveRecipes(recipes);
        tv.setText("");
        adapter.notifyItemInserted(0);
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
        recyclerView.scrollToPosition(0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Recipe passedItem = (Recipe) data.getExtras().get("passed_item");
            String action = (String) data.getExtras().get("Action");
            int index = (int) data.getExtras().get("Index");

            switch(action){
                case "Save":
                    MainActivity.removeRecipe(index);
                    MainActivity.addRecipe(index, passedItem);
                    //recyclerView.notifyAll();
                    break;
                case "Delete":
                    MainActivity.removeRecipe(index);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                    break;
                default:
                    break;
            }


        }
    }

    @Override
    public void onResume(){
        super.onResume();

        adapter = new RecipesAdapter(context, recipes, this);
        recyclerView.setAdapter(adapter);
    }
}