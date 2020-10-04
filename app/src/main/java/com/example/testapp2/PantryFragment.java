package com.example.testapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class PantryFragment extends Fragment implements PantryAdapter.PantryClickListener, AdapterView.OnItemSelectedListener {

    PantryAdapter adapter;
    PantryCanMakeAdapter canMakeAdapter;
    RecyclerView recyclerView;
    RecyclerView recyclerViewCanMake;
    TextView newIng;
    Spinner quantity;
    Spinner measureType;
    Button addIngred;
    Button canMake;
    Button showAll;
    LinearLayout sortBarHolder;
    Boolean showAllRecipes = false;
    public ArrayList<String> quantitiesArr = new ArrayList<>();
    public ArrayList<String> typesArr = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private ArrayList<Recipe> recipes;
    private static ArrayList<SameNameIngredients> pantry  = MainActivity.getPantry();
    Context context;

    public PantryFragment() {
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
        recipes = MainActivity.getRecipes();
        context = getActivity().getApplicationContext();
        pantry = MainActivity.getPantry();
        quantitiesArr.addAll(Arrays.asList(MainActivity.getQuantities()));
        typesArr.addAll(Arrays.asList(MainActivity.getTypes()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pantry, container, false);
        recipes = MainActivity.getRecipes();

        recyclerViewCanMake = view.findViewById(R.id.rv_pantry_canMake);
        newIng = view.findViewById(R.id.text_pantryNewIngredient);
        sortBarHolder = view.findViewById(R.id.pantry_canmake_sortbarHolder);
        canMake = view.findViewById(R.id.button_pantry_canMake);
        canMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canMake.getText().equals("Back to View Pantry")){
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewCanMake.setVisibility(View.GONE);
                    sortBarHolder.setVisibility(View.GONE);
                    showAll.setText("show all");
                    showAll.setVisibility(View.INVISIBLE);
                    canMake.setText("What Can I Make?");
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    sortBarHolder.setVisibility(View.VISIBLE);
                    recyclerViewCanMake.setVisibility(View.VISIBLE);
                    canMakeAdapter = new PantryCanMakeAdapter(context, recipesIcanMake()); //refresh data
                    recyclerViewCanMake.setAdapter(canMakeAdapter);
                    showAll.setVisibility(View.VISIBLE);
                    canMake.setText("Back to View Pantry");
                }
            }
        });

        quantity = view.findViewById(R.id.spinner_pantryQuantity);
        final ArrayAdapter<String> adapterSpinnerQuantity = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, quantitiesArr);
        adapterSpinnerQuantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantity.setAdapter(adapterSpinnerQuantity);
        quantity.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        measureType = view.findViewById(R.id.spinner_pantryMeasurement);
        ArrayAdapter<String> adapterSpinnerType = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, typesArr);
        adapterSpinnerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measureType.setAdapter(adapterSpinnerType);
        measureType.setOnItemSelectedListener(this);
        addIngred = view.findViewById(R.id.button_pantryAdd);
        addIngred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentIngredients.add(new Ingredient("Test"));
                if(handleIngredient( new Ingredient(newIng.getText().toString() , Double.parseDouble(quantity.getSelectedItem().toString()), measureType.getSelectedItem().toString()))){
                   // adapter.notifyItemInserted(adapter.getItemCount());
                    PantryAdapter refreshAdapter = adapter;
                    recyclerView.setAdapter(refreshAdapter);
                } else         {
                    PantryAdapter refreshAdapter = adapter;
                recyclerView.setAdapter(refreshAdapter);
                }
                //.scrollToPosition(currentIngredients.size()-1);
                newIng.setText("");
                quantity.setSelection(0);
                measureType.setSelection(0);
            }
        });
        showAll = view.findViewById(R.id.button_pantry_showAll);
        showAll.setVisibility(View.INVISIBLE);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showAll.getText().toString().toLowerCase().equals("show all")) {
                    PantryCanMakeAdapter showAllAdapter = new PantryCanMakeAdapter(context, recipes);
                    showAllRecipes = true;
                    recyclerViewCanMake.setAdapter(showAllAdapter);
                    showAll.setText("undo");
                } else {
                    showAll.setText("show all");
                    showAllRecipes = false;
                    canMakeAdapter = new PantryCanMakeAdapter(context, recipesIcanMake());
                    recyclerViewCanMake.setAdapter(canMakeAdapter);
                }

            }
        });

        //canMakeAdapter = new PantryCanMakeAdapter(context, recipesIcanMake());
        recyclerViewCanMake.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerViewCanMake.setAdapter(canMakeAdapter);
        recyclerViewCanMake.setVisibility(View.GONE);

        View pantrySortBar = view.findViewById(R.id.pantry_canmake_sortbar);
        SortBar sortBar = new SortBar(pantrySortBar);

        adapter = new PantryAdapter(context , pantry , this);
        recyclerView = view.findViewById(R.id.rv_pantry_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return view;
    }
    public boolean handleIngredient(Ingredient ing){
            boolean newEntry = false;
            if(ing.getName().equals("")) return false;
                for (int k = 0; k < pantry.size(); k++) {
                    if (pantry.size() == 0) {
                        pantry.add(new SameNameIngredients(ing));
                        newEntry = true;
                        break;
                    } else if (newEntry = pantry.get(k).handleNewIngredient(ing)) {
                        break;
                    }
                }
                if (!newEntry) pantry.add(new SameNameIngredients(ing));
                return newEntry;
    }
    @Override
    public void pantryDeleteIngredient(int layoutPosition) {

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
            if(showAllRecipes) canMakeAdapter = new PantryCanMakeAdapter(context, recipes);
            else canMakeAdapter = new PantryCanMakeAdapter(context, recipesIcanMake());
            recyclerViewCanMake.setAdapter(canMakeAdapter);
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
    public void pantryUpdateIngredient(int parent, int layoutPosition, int increment) {
            SameNameIngredients ing = pantry.get(parent);
            ArrayList<Ingredient> list = ing.getIngredientSummary();
        list.get(layoutPosition).setQuantity(list.get(layoutPosition).getQuantity()+increment);
        PantryAdapter refreshAdapter = adapter;
        recyclerView.setAdapter(refreshAdapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.spinner_pantryQuantity) {
            if(adapterView.getSelectedItem().toString().equals("Other Value")){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Quantity");
                builder1.setMessage("Write your custom quantity below");
                builder1.setCancelable(true);
// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder1.setView(input);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String inputText = input.getText().toString();
                                quantitiesArr.add(0, inputText);

                                quantity.setSelection(0);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        if(adapterView.getId() == R.id.spinner_pantryMeasurement) {
            if( adapterView.getSelectedItem().toString().equals("Other Value")){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Measurement");
                builder1.setMessage("Write your custom measurement below");
                builder1.setCancelable(true);
// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                builder1.setView(input);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String inputText = input.getText().toString();
                                typesArr.add(0, inputText);

                                measureType.setSelection(0);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    public ArrayList<Recipe> recipesIcanMake(){
        ArrayList<Recipe> temprec = new ArrayList<>();
        for(Recipe rec: recipes){
            boolean addToCanMake = true;
            ArrayList<Ingredient> recIng = rec.getIngredients();
            for (Ingredient ing : recIng) {
                if(!isIngredientStocked(ing)){
                    addToCanMake = false;
                    break;
                }
            }
            if(addToCanMake) temprec.add(rec);
        }
       // Log.e("recipesCanMake", temprec.toString());
        return temprec;
    }
    public static boolean isIngredientStocked(Ingredient ing){
        for (SameNameIngredients sni: pantry
             ) {
            if(sni.isStocked(ing)) return true;
        }
        return false;
    }
    public static double checkIngredientStock(Ingredient ing){
        for (SameNameIngredients sni: pantry
        ) {
            if(sni.checkNameMatch(ing.getName())) return sni.getStocked(ing);
        }
        return 0;
    }

    public static ArrayList<Ingredient> getRecipeStocked(Recipe thisrec){
        ArrayList<Ingredient> haveStockedSoFar = new ArrayList<>();
        for (Ingredient ing: thisrec.getIngredients()
             ) {
            if(isIngredientStocked(ing)) haveStockedSoFar.add(ing);
        }
        return haveStockedSoFar;
    }
    public static ArrayList<Ingredient> getRecipeUnstocked(Recipe thisrec){
        ArrayList<Ingredient> haveStockedSoFar = new ArrayList<>();
        for (Ingredient ing: thisrec.getIngredients()
        ) {
            if(!isIngredientStocked(ing)) haveStockedSoFar.add(ing);
        }
        return haveStockedSoFar;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}