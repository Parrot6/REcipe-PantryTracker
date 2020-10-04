package com.example.testapp2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.view.View.GONE;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    final private MyClickListener mOnClickListener;
    private ArrayList<Recipe> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public RecipesAdapter(Context context, ArrayList<Recipe> data, MyClickListener listener) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mData = data;
        this.mOnClickListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipe_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view, mOnClickListener);

        return holder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = mData.get(position);
        holder.myTextView.setText(recipe.getRecipeTitle());
        if(recipe.getRecipeIcon() != null) holder.recipeIcon.setImageBitmap(recipe.getRecipeIcon());
        else holder.recipeIcon.setVisibility(GONE);

        if(recipe.getRecipeType() == Recipe.RecipeType.NONE){
            holder.recipeType.setVisibility(GONE);
        }
        holder.recipeType.setImageResource(recipe.getRecipeType().getDrawable());


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        Button viewButton;
        MyClickListener listener;
        ImageView recipeType;
        ImageView recipeIcon;
        ViewHolder(View itemView, MyClickListener myClickListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.text_mainListRecipe);
            viewButton = itemView.findViewById(R.id.button_expandRecipe);
            recipeIcon = itemView.findViewById(R.id.recipe_listitem_image);
            recipeType = itemView.findViewById(R.id.recips_listitem_type);
            this.listener = myClickListener;
            viewButton.setOnClickListener(this);
            //add more listeners here
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_expandRecipe:
                    //if (listener != null) {
                        listener.onView(this.getLayoutPosition());
                   // }
                    break;
                default:
                    break;
            }
        }
    }

    // convenience method for getting data at click position
    Recipe getItem(int id) {
        return mData.get(id);
    }
    public void refreshRecipes(ArrayList<Recipe> newData){
        mData = newData;
        notifyDataSetChanged();
    }

    // parent activity will implement this method to respond to click events
    public interface MyClickListener {
        void onView(int layoutPosition);
    }
}