package com.example.testapp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class EditRecipe extends AppCompatActivity implements AdapterView.OnItemSelectedListener, IngredientsAdapter.MyClickListener{
    TextView name;
    RecyclerView ingredients;
    RecyclerView nutritionResultRV;
    TextView newIngredient;
    TextView addNote;
    TextView instructions;
    int recipeIndex = 999;
    Button cancelButton;
    Button saveButton;
    Button addPhoto;
    Button addIngredient;
    ImageView recipeTypeImg;
    Button recipeType;
    Recipe recipe;
    ImageView icon;
    Button getNutrition;
    TextView nutritionAttribution;
    LinearLayout hideForNutrition;

    IngredientsAdapter adapter;
    Context context;
    private ArrayList<Ingredient> currentIngredients = new ArrayList<Ingredient>();
    private Spinner measureType;
    private Spinner quantity;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //private static final String[] types = {"tsp.","tbsp.","fl oz","cup","Other Value","fl pt","ft qt","gal","mL","L"};
    //private static String[] quantities = {"1","2","3","4","Other Value"};
    public ArrayList<String> quantitiesArr = new ArrayList<>();
    public ArrayList<String> typesArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_recipe);
        context = this;
        Intent intent = getIntent();
        recipe = (Recipe) getIntent().getSerializableExtra("Recipe");
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        recipeIndex = (int) getIntent().getSerializableExtra("Index");

        quantitiesArr.addAll(Arrays.asList(MainActivity.getQuantities()));
        typesArr.addAll(Arrays.asList(MainActivity.getTypes()));
        currentIngredients.addAll(recipe.getIngredients());

        name = findViewById(R.id.text_RecipeTitle);
        name.setText(recipe.getRecipeTitle());

        addPhoto = findViewById(R.id.button_addPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                addPhoto.setText("new");
            }
        });

        icon = findViewById(R.id.editRecipe_icon);
        if(recipe.getRecipeIcon() == null){
            icon.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        } else {
            icon.setImageBitmap(recipe.getRecipeIcon());
            addPhoto.setText("new");
        }


        ingredients = findViewById(R.id.recycleView_Ingredients);
        ingredients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientsAdapter(this, currentIngredients, this);

        ingredients.setAdapter(adapter);

        newIngredient = findViewById(R.id.text_newIngredient);
        //addIngredient.setEnabled(false);
        //addIngredient.setClickable(false);
        //addIngredient.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        newIngredient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean bl = (newIngredient.getText().length() > 0);
                addIngredient.setEnabled(bl);
                addIngredient.setClickable(bl);
                if(!bl) addIngredient.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY); else addIngredient.getBackground().setColorFilter(null);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addNote = findViewById(R.id.text_Note);

        instructions = findViewById(R.id.text_editRecipeInstructions);
        instructions.setText("test");

        if(recipe.getRecipeInstructions() != null) {
            instructions.setText(recipe.getRecipeInstructions());
        } else instructions.setText("");

        saveButton = findViewById(R.id.button_saveEdits);

        nutritionResultRV = findViewById(R.id.recyclerView_nutrition_info);
        nutritionResultRV.setLayoutManager(new LinearLayoutManager(this));
        NutritionAdapter nutritionAdapter = new NutritionAdapter(context, currentIngredients);
        nutritionResultRV.setAdapter(nutritionAdapter);
        nutritionResultRV.setVisibility(View.GONE);

        hideForNutrition = findViewById(R.id.layout_edit_recipe_toggleLayout);
        nutritionAttribution = findViewById(R.id.text_nutrition_attribution);
        nutritionAttribution.setVisibility(View.GONE);
        
        getNutrition = findViewById(R.id.button_edit_recipe_getNutrition);
        Boolean Fetch = false;
        for(Ingredient ing : currentIngredients){
            if(ing.getNutrition() == null) {
                Fetch = true;
                getNutrition.setText(R.string.nutritionButtonLoad);
            }
        }
        if(!Fetch) getNutrition.setText("View Nutrition");
        getNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentIngredients.size() == 0) return;
                toggleView(hideForNutrition);
                toggleView(nutritionResultRV);
                toggleView(nutritionAttribution);
                String state = getNutrition.getText().toString();
                if(state.equals("View Nutrition")) {
                    getNutrition.setText("Back To Recipe");
                    return;
                } else if(state.equals("Back To Recipe")){
                    getNutrition.setText("View Nutrition");
                    return;
                }
                getNutrition.setEnabled(false);

                NutritionQuery query;
                Nutrition totalNutritionSoFar = null;
                for (Ingredient ing: currentIngredients
                     ) {
                    if(ing.getNutrition() != null){
                        if(totalNutritionSoFar == null) totalNutritionSoFar = Nutrition.newNutrition(ing.getNutrition());
                        else totalNutritionSoFar.getCombined(ing.getNutrition());
                        continue;
                    }
                    query = new NutritionQuery(ing);
                    while(!query.finished){
                       /* try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                    getNutrition.setText("Back To Recipe");
                    ing.setNutrition(query.getBestResult());
                    if(totalNutritionSoFar == null) totalNutritionSoFar = Nutrition.newNutrition(ing.getNutrition());
                    else totalNutritionSoFar.getCombined(ing.getNutrition());
                }

                getNutrition.setEnabled(true);
                nutritionResultRV.setAdapter(new NutritionAdapter(context, currentIngredients));

                recipe.setNutritionSummary(totalNutritionSoFar);
            }
        });


        cancelButton = findViewById(R.id.button_cancelEdits);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipe.setRecipeTitle(name.getText().toString());
                recipe.setRecipeInstructions(instructions.getText().toString());
                recipe.setIngredients(currentIngredients);
                Nutrition recipeSummary = null;
                for (Ingredient ing :
                        currentIngredients) {
                    if(ing.getNutrition() == null) continue;
                    if(recipeSummary == null) recipeSummary = Nutrition.newNutrition(ing.getNutrition());
                    else recipeSummary.getCombined(ing.getNutrition());
                }
                recipe.setNutritionSummary(recipeSummary);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("passed_item", recipe);
                returnIntent.putExtra("Action", "Save");
                returnIntent.putExtra("Index", recipeIndex);
                setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        quantity = findViewById(R.id.spinner_quantity);
        final ArrayAdapter<String> adapterSpinnerQuantity = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, quantitiesArr);
        adapterSpinnerQuantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantity.setAdapter(adapterSpinnerQuantity);
        quantity.setOnItemSelectedListener(this);

        measureType = findViewById(R.id.spinner_measurement);
        ArrayAdapter<String> adapterSpinnerType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, typesArr);
        adapterSpinnerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measureType.setAdapter(adapterSpinnerType);
        measureType.setOnItemSelectedListener(this);
        addIngredient = findViewById(R.id.button_addIngredient);
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentIngredients.add(new Ingredient("Test"));
                currentIngredients.add( new Ingredient(newIngredient.getText().toString() , Double.parseDouble(quantity.getSelectedItem().toString()), measureType.getSelectedItem().toString(), addNote.getText().toString()));
                adapter.notifyDataSetChanged();
                ingredients.scrollToPosition(currentIngredients.size()-1);
                newIngredient.setText("");
                quantity.setSelection(0);
                measureType.setSelection(0);
                addNote.setText("Note");
                getNutrition.setText(R.string.nutritionButtonLoad);
            }
        });
        recipeType = findViewById(R.id.button_edit_recipe_recipeType);
        recipeTypeImg = findViewById(R.id.edit_recipe_recipeType);
        recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
        if(recipe.getRecipeType() == Recipe.RecipeType.NONE) recipeType.setText("choose");

        recipeType.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                MenuBuilder menuBuilder = new MenuBuilder(context);
                MenuInflater inflater = new MenuInflater(context);
                inflater.inflate(R.menu.recipe_type_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
                optionsMenu.setForceShowIcon(true);

// Set Item Click Listener
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rt_Alcohol: // Handle option1 Click
                                recipe.setRecipeType(Recipe.RecipeType.AlcoholicDrink);
                                recipeType.setText("change");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return true;
                            case R.id.rt_Desert: // Handle option2 Click
                                recipe.setRecipeType(Recipe.RecipeType.Desert);
                                recipeType.setText("change");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return true;
                            case R.id.rt_Drink: // Handle option2 Click
                                recipe.setRecipeType(Recipe.RecipeType.Drink);
                                recipeType.setText("change");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return true;
                            case R.id.rt_Entree: // Handle option2 Click
                                recipe.setRecipeType(Recipe.RecipeType.Entree);
                                recipeType.setText("change");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return true;
                            case R.id.rt_Side: // Handle option2 Click
                                recipe.setRecipeType(Recipe.RecipeType.Side);
                                recipeType.setText("change");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return true;
                            default:
                                recipe.setRecipeType(Recipe.RecipeType.NONE);
                                recipeType.setText("choose");
                                recipeTypeImg.setImageResource(recipe.getRecipeType().getDrawable());
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {}
                });

                optionsMenu.show();

            }
        });


    }
    public void toggleView(View view){
        if(view.getVisibility() == View.GONE) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.spinner_quantity) {
           if(adapterView.getSelectedItem().toString().equals("Other Value")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("Quantity");
                    builder1.setMessage("Write your custom quantity below");
                    builder1.setCancelable(true);
// Set up the input
                    final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                    builder1.setView(input);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String inputText = input.getText().toString();
                                    quantitiesArr.add(inputText);

                                    quantity.setSelection(quantitiesArr.size() - 1);
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
        if(adapterView.getId() == R.id.spinner_measurement) {
            if( adapterView.getSelectedItem().toString().equals("Other Value")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("Measurement");
                    builder1.setMessage("Write your custom measurement below");
                    builder1.setCancelable(true);
// Set up the input
                    final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                    builder1.setView(input);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String inputText = input.getText().toString();
                                    typesArr.add(inputText);

                                    measureType.setSelection(typesArr.size() - 1);
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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            icon.setVisibility(View.VISIBLE);
            icon.setImageBitmap(imageBitmap);
            recipe.setIcon(imageBitmap);
            String location = saveToInternalStorage(imageBitmap);
            recipe.addImage(location);
        }
    }

    @Override
    public void onBackPressed()
    {
       /* Intent returnIntent = new Intent();
        returnIntent.putExtra("passed_item", recipe);
        returnIntent.putExtra("Action", "Back");
        returnIntent.putExtra("Index", recipeIndex);
        setResult(RESULT_OK, returnIntent);*/
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        setResult(RESULT_CANCELED);
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        setResult(RESULT_CANCELED);
        super.onDestroy();
    }
    @Override
    public void deleteIngredient(int layoutPosition) {
        currentIngredients.remove(layoutPosition);
        adapter.notifyItemRemoved(layoutPosition);
    }

    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        Date now = new Date();
        File mypath = new File(directory,now.getTime() + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.toString();
    }

    @Override
    public void updateIngredient(int layoutPosition) {
      //  currentIngredients.remove(layoutPosition);
      //  adapter.notifyItemRemoved(layoutPosition);
    }
}