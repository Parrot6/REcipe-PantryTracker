package com.example.testapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PantryCanMakeAdapter extends RecyclerView.Adapter<PantryCanMakeAdapter.ViewHolderPantryCanMake> {
    private LayoutInflater mInflater;
    private Context context;
    ArrayList<Recipe> mdata;

    public PantryCanMakeAdapter(Context mcontext, ArrayList<Recipe> data) {
        context = mcontext;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mdata = data;
    }

    @NonNull
    @Override
    public ViewHolderPantryCanMake onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_recipe_canmake, parent, false);
        ViewHolderPantryCanMake holder = new ViewHolderPantryCanMake(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPantryCanMake holder, int position) {
        double x = 0;
        Recipe data = mdata.get(position);
        holder.recipeName.setText(data.getRecipeTitle());
        RecipeStockProfile recProfile = new RecipeStockProfile(data);

        holder.ingredientsHave.setText(recProfile.getStockedText());
        holder.ingredientsDontHave.setText(recProfile.getUnstockedText());
        if(data.getRecipeType() == Recipe.RecipeType.NONE) holder.recipeType.setVisibility(View.GONE);
        else holder.recipeType.setImageResource(data.getRecipeType().getDrawable());
        holder.recipeCompletion.setText(String.valueOf((int)recProfile.percentStocked) + "%");
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }


    public static class ViewHolderPantryCanMake extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recipeName;
        TextView recipeCompletion;
        TextView ingredientsHaveTitle;
        TextView ingredientsHave;
        TextView getIngredientsDontHaveTitle;
        TextView ingredientsDontHave;
        Button viewButton;
        ConstraintLayout viewmoreblock;
        ArrayList<Ingredient> allIngred;
        ImageView recipeType;
        ViewHolderPantryCanMake(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.text_pantry_canmake_recipeName);
            recipeCompletion = itemView.findViewById(R.id.pantry_canmake_numMissingIngr);
            ingredientsHave = itemView.findViewById(R.id.text_pantry_canmake_ingredientHave);
            ingredientsDontHave = itemView.findViewById(R.id.text_pantry_canmake_ingredientDontHave);
            viewButton = itemView.findViewById(R.id.button_pantry_canmake_view);
            viewmoreblock = itemView.findViewById(R.id.pantry_canmake_viewmoreblock);
            viewmoreblock.setVisibility(View.GONE);
            viewButton.setOnClickListener(this);
            recipeType = itemView.findViewById(R.id.pantry_canmake_recipeType);
            //PantryViewMoreAdapter adapter = new PantryViewMoreAdapter(itemView.getContext(), null, this);
            //nestedRV.setAdapter(adapter);
            //add more listeners here
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_pantry_canmake_view:
                    if(viewButton.getText().equals("view")) {
                        viewmoreblock.setVisibility(View.VISIBLE);
                        viewButton.setText("hide");
                    } else {
                        viewmoreblock.setVisibility(View.GONE);
                        viewButton.setText("view");
                    }
                break;
            }
            }
    }

    private class RecipeStockProfile {
        private Recipe thisRec;
        private ArrayList<Double> inStock = new ArrayList<>();
        ArrayList<Ingredient> stocked;
        ArrayList<Ingredient> unstocked;
        double percentStocked;
        RecipeStockProfile(Recipe rec){
            this.thisRec = rec;
            for (Ingredient ing: rec.getIngredients()
            ) {
                inStock.add(PantryFragment.checkIngredientStock(ing));
            }
            stocked = PantryFragment.getRecipeStocked(rec);
            unstocked = PantryFragment.getRecipeUnstocked(rec);
            double stockD = stocked.size();
            double unstockD = unstocked.size();
            percentStocked = (stockD / (unstockD + stockD));
            if(stockD + unstockD == 0) percentStocked = 1;
            percentStocked = percentStocked * 100;
        }
        public String getStockedText(){
            String textSoFar = "";
            for(int i = 0; i < stocked.size(); i++){
                textSoFar += stocked.get(i).toString();
               // if(inStock.get(i) > 0) textSoFar += " (have " + inStock.get(i) + ")";
                if((i + 1) < stocked.size()) textSoFar += " \n";
            }
            if(textSoFar.equals("")) textSoFar = "None";
            return textSoFar;
        }
        public String getUnstockedText(){
            String textSoFar = "";
            DecimalFormat format = new DecimalFormat("0.#");
            for(int i = 0; i < unstocked.size(); i++){
                textSoFar += unstocked.get(i).toString();
                if(inStock.get(i) > 0) textSoFar += " (have " + format.format(inStock.get(i)) + ")";
                if((i + 1) < unstocked.size()) textSoFar += " \n";
            }
            if(textSoFar.equals("")) textSoFar = "None";
            return textSoFar;
        }
    }

    public static String ingredArraytoString(ArrayList<Ingredient> ings){
        String ingredientsSoFar = "";
        for(int i = 0; i < ings.size(); i++){
            ingredientsSoFar += ings.get(i).toString();
            if((i + 1) < ings.size()) ingredientsSoFar += " \n";
        }
        if(ingredientsSoFar.equals("")) ingredientsSoFar = "None";
        return ingredientsSoFar;
    }
}
