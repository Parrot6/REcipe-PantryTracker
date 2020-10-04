package com.example.testapp2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolderIng> {

    final private MyClickListener mOnClickListener;
    private ArrayList<Ingredient> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public IngredientsAdapter(Context context, ArrayList<Ingredient> data, MyClickListener listener) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mData = data;
        this.mOnClickListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolderIng onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.edit_ingredient, null);
        ViewHolderIng holder = new ViewHolderIng(view, mOnClickListener);

        return holder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolderIng holder, int position) {
        Ingredient ingr = mData.get(position);
        holder.note.setText(ingr.getAdditionalNote());
        holder.ingredientName.setText(ingr.getName());
        holder.amtAndType.setText(ingr.getQuantity() + " " + ingr.getMeasurementType());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolderIng extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView amtAndType;
        TextView ingredientName;
        TextView note;
        Button addIngredient;
        MyClickListener listener;

        ViewHolderIng(View itemView, MyClickListener myClickListener) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.text_mainListRecipe);
            addIngredient = itemView.findViewById(R.id.button_expandRecipe);
            amtAndType = itemView.findViewById(R.id.text_edit_ingredient_quantityAndType);
            note = itemView.findViewById(R.id.text_edit_ingredient_note);
            this.listener = myClickListener;
            addIngredient.setOnClickListener(this);
            //add more listeners here
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_expandRecipe:
                    //if (listener != null) {
                        listener.deleteIngredient(this.getLayoutPosition());
                   // }
                    break;
                default:
                    break;
            }
        }
    }

    // convenience method for getting data at click position
    Ingredient getItem(int id) {
        return mData.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface MyClickListener {

        void deleteIngredient(int layoutPosition);
        void updateIngredient(int layoutPosition);
    }
}