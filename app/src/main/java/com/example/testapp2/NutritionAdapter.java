package com.example.testapp2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.ViewHolderIng> {
    public static DecimalFormat df = new DecimalFormat("0.00");

    private static ArrayList<Ingredient> mData;
    private static ArrayList<viewState> currentState = new ArrayList<>();
    private LayoutInflater mInflater;
    Context context;
    private static int cursorSpot = 0;

    // data is passed into the constructor
    public NutritionAdapter(Context context, ArrayList<Ingredient> data) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        mData = data;
        for (Ingredient ingredient : mData
             ) {
            currentState.add(new viewState());
        }
       // this.mOnClickListener = listener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolderIng onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.nutrition_info_item, parent, false);

        return new ViewHolderIng(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolderIng holder, int position) {
        Ingredient ingr = mData.get(position);
        holder.ingredientName.setText(ingr.getName());
        holder.amtAndType.setText(String.format("%s %s", ingr.getQuantity(), ingr.getMeasurementType()));

        if(currentState.get(position).isExpanded) holder.nutrientHolder.setVisibility(View.VISIBLE);
        else holder.nutrientHolder.setVisibility(View.GONE);

        if(ingr.getNutrition() == null) {
            holder.resultSuccess.setText(Nutrition.QueryResults.NOT_QUERIED.asString());
            return;
        }

        holder.editNutrition.setTag(position);
        holder.scaleAll.setTag(position);
        holder.scaleAll.setChecked(currentState.get(position).isScaleAllChecked);

        holder.resultSuccess.setText(ingr.getNutrition().getQueryResults().asString());
        Nutrition thisNut = ingr.getNutrition();
        
        String temp = "Nutrition based on " + df.format(ingr.getNutrition().nf_serving_size_qty) + " " + ingr.getNutrition().nf_serving_size_unit + " of "+ ingr.getNutrition().item_name;
        holder.nutritionBasedOn.setText(temp);

        holder.et_calories.setText(df.format(thisNut.kcal_calories));
        holder.et_calories.setTag(position);
        holder.et_totalFat.setText(df.format(thisNut.g_total_fat));
        holder.et_totalFat.setTag(position);
        holder.et_cholesterol.setText(df.format(thisNut.mg_cholesterol));
        holder.et_cholesterol.setTag(position);
        holder.et_sodium.setText(df.format(thisNut.mg_sodium));
        holder.et_sodium.setTag(position);
        holder.et_totalCarbs.setText(df.format(thisNut.g_total_carbohydrate));
        holder.et_totalCarbs.setTag(position);
        holder.et_sugars.setText(df.format(thisNut.g_sugars));
        holder.et_sugars.setTag(position);
        holder.et_protein.setText(df.format(thisNut.g_protein));
        holder.et_protein.setTag(position);
        holder.et_servingWeight.setText(df.format(thisNut.g_serving_weight_grams));
        holder.et_servingWeight.setTag(position);
        holder.et_servingSize.setText(df.format(thisNut.nf_serving_size_qty));
        holder.et_servingSize.setTag(position);
        //if(!holder.areListenersAdded) {
       /*     holder.etl_servingSize.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_servingSize, thisNut::setNf_serving_size_qty, position));
            holder.etl_calories.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_calories, thisNut, thisNut::setKcal_calories, position));
            holder.etl_totalFat.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_totalFat, thisNut, thisNut::setG_total_fat, position));
            holder.etl_cholesterol.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_cholesterol, thisNut, thisNut::setMg_cholesterol, position));
            holder.etl_sodium.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_sodium, thisNut, thisNut::setMg_sodium, position));
            holder.etl_totalCarbs.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_totalCarbs, thisNut, thisNut::setG_total_carbohydrate, position));
            holder.etl_sugars.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_sugars, thisNut, thisNut::setG_sugars, position));
            holder.etl_protein.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_protein, thisNut, thisNut::setG_protein, position));
            holder.etl_servingWeight.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll, holder.et_servingWeight, thisNut, thisNut::setG_serving_weight_grams, position));
            holder.editNutrition.setOnClickListener(view -> {
                if(!holder.isExpanded) {
                    holder.isExpanded = true;
                    holder.editNutrition.setText("hide");
                    holder.nutrientHolder.setVisibility(View.VISIBLE);
                } else {
                    holder.isExpanded = false;
                    holder.editNutrition.setText("edit");
                    holder.nutrientHolder.setVisibility(View.GONE);
                }
            });
            holder.scaleAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    holder.isScaleAllChecked = b; //so databind remembers
                }
            });
            holder.areListenersAdded = true;*/
        //}
    }

    private void scaleAll(Nutrition nut, double multiplier, int position){
        nut.scaleNutrition(multiplier);
        notifyItemChanged(position);
        //notifyDataSetChanged();
        //RefreshEditTexts(editTexts, nut, from);
    };

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolderIng extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView amtAndType;
        TextView ingredientName;
        TextView resultSuccess;
        Button editNutrition;
        View itemView;
        ConstraintLayout nutrientHolder;
        TextView nutritionBasedOn;
        Switch scaleAll;
        TextView calories;
        TextView totalFat;
        TextView cholesterol;
        TextView sodium;
        TextView totalCarbs;
        TextView sugars;
        TextView protein;
        TextView servingWeight;
        TextView servingSize;
        EditText et_calories;
        EditText et_totalFat;
        EditText et_cholesterol;
        EditText et_sodium;
        EditText et_totalCarbs;
        EditText et_sugars;
        EditText et_protein;
        EditText et_servingWeight;
        EditText et_servingSize;


        ViewHolderIng(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nutrientHolder = itemView.findViewById(R.id.layout_nutrition_details);
            //nutrientHolder.setVisibility(View.GONE);
            ingredientName = itemView.findViewById(R.id.text_nutrition_info_name);
            editNutrition = itemView.findViewById(R.id.button_nutrition_info_edit);
            editNutrition.setOnClickListener(this);
            amtAndType = itemView.findViewById(R.id.text_nutrtition_info_qtyAndMeas);
            resultSuccess = itemView.findViewById(R.id.text_nutrition_info_result);
            editNutrition.setText("edit");
            //editNutrition.setOnClickListener(this);
            nutritionBasedOn = itemView.findViewById(R.id.text_nutrition_info_basedOn);
            scaleAll = itemView.findViewById(R.id.switch_scaleNutrition);
            scaleAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   if(scaleAll.getTag() != null) currentState.get((Integer) scaleAll.getTag()).isScaleAllChecked = b; //so databind remembers
                }
            });
            calories = itemView.findViewById(R.id.text_nutrition_title_calories);
            totalFat = itemView.findViewById(R.id.text_nutrition_title_total_fat);
            cholesterol = itemView.findViewById(R.id.text_nutrition_title_cholesterol);
            sodium = itemView.findViewById(R.id.text_nutrition_title_sodium);
            totalCarbs = itemView.findViewById(R.id.text_nutrition_title_totalCarbs);
            sugars = itemView.findViewById(R.id.text_nutrition_title_sugars);
            protein = itemView.findViewById(R.id.text_nutrition_title_protein);
            servingWeight = itemView.findViewById(R.id.text_nutrition_title_servingWeight);
            servingSize = itemView.findViewById(R.id.text_nutrition_title_servingSize);

            et_calories = itemView.findViewById(R.id.et_nutrition_info_calories);
            et_calories.addTextChangedListener(new CustomTextWatcher(et_calories){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_calories.getTag()).getNutrition()::setKcal_calories;
                }
            });
            et_totalFat = itemView.findViewById(R.id.et_nutrition_info_totalFat);
            et_totalFat.addTextChangedListener(new CustomTextWatcher(et_totalFat){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_totalFat.getTag()).getNutrition()::setG_total_fat;
                }
            });
            et_cholesterol = itemView.findViewById(R.id.et_nutrition_info_cholesterol);
            et_cholesterol.addTextChangedListener(new CustomTextWatcher(et_cholesterol){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_cholesterol.getTag()).getNutrition()::setMg_cholesterol;
                }
            });
            et_sodium = itemView.findViewById(R.id.et_nutrition_info_sodium);
            et_sodium.addTextChangedListener(new CustomTextWatcher(et_sodium){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_sodium.getTag()).getNutrition()::setMg_sodium;
                }
            });
            et_totalCarbs = itemView.findViewById(R.id.et_nutrition_info_totalCarbs);
            et_totalCarbs.addTextChangedListener(new CustomTextWatcher(et_totalCarbs){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_totalCarbs.getTag()).getNutrition()::setG_total_carbohydrate;
                }
            });
            et_sugars = itemView.findViewById(R.id.et_nutrition_info_sugars);
            et_sugars.addTextChangedListener(new CustomTextWatcher(et_sugars){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_sugars.getTag()).getNutrition()::setG_sugars;
                }
            });
            et_protein = itemView.findViewById(R.id.et_nutrition_info_protein);
            et_protein.addTextChangedListener(new CustomTextWatcher(et_protein){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_protein.getTag()).getNutrition()::setG_protein;
                }
            });
            et_servingWeight = itemView.findViewById(R.id.et_nutrition_info_servingWeight);
            et_servingWeight.addTextChangedListener(new CustomTextWatcher(et_servingWeight){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_servingWeight.getTag()).getNutrition()::setG_serving_weight_grams;
                }
            });
            et_servingSize = itemView.findViewById(R.id.et_nutrition_info_servingSize);
            et_servingSize.addTextChangedListener(new CustomTextWatcher(et_servingSize){
                @Override
                Nutrition.command mySetter() {
                    return mData.get((Integer) et_servingSize.getTag()).getNutrition()::setNf_serving_size_qty;
                }
            });

        }
        private class CustomTextWatcher implements TextWatcher {
            private EditText et;
            boolean ignoreRecursiveCalls = false;
            String beforeValue;

            CustomTextWatcher(EditText et){
                this.et = et;
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeValue = charSequence.toString();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et.hasFocus()) {
                    if (!ignoreRecursiveCalls) {
                        ignoreRecursiveCalls = true;
                        cursorSpot = i + i2;
                        Log.e("update cursor Pos:", String.valueOf(cursorSpot));
                        if (et.getTag() != null) {
                            afterTextChangedMethod(et, beforeValue, mySetter());
                        }

                        /*if(et_servingSize.hasFocus()) */
                        ignoreRecursiveCalls = false; //any calls between flags will not make it into here
                        //et_servingSize.setSelection(Math.min(cursorSpot, charSequence.toString().length()));
                        //Log.e("set cursor Pos yo:", String.valueOf(Math.min(cursorSpot, charSequence.toString().length())));
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                et.setSelection(Math.min(cursorSpot, editable.toString().length()));

            }
            Nutrition.command mySetter() {
                return null;
            }
        }
        public void afterTextChangedMethod(EditText et, String beforeValue, Nutrition.command setter){
                if (!et.hasFocus()) return; //stop setText from triggering loop
                if (scaleAll.isChecked()) {
                    if (beforeValue.equals(et.getText().toString())) {
                        return; //do nothing if no change
                    }
                    double before = Double.parseDouble(beforeValue);
                    double differenceMultiplier = 1.0;
                    if (before != 0.0)
                        differenceMultiplier = Double.parseDouble(et.getText().toString()) / before;
                    if (differenceMultiplier > 0.00001 && differenceMultiplier != 1.0) {
                        setter.set(Double.parseDouble(beforeValue)); //revert to original value and then scale all values
                        scaleAll(mData.get((Integer) et.getTag()).getNutrition(), differenceMultiplier, (Integer) et.getTag());
                    }
                } else
                  setter.set(Double.parseDouble(et.getText().toString()));
        }
       @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_nutrition_info_edit:
                    if(view.getTag() != null) {
                        int pos = (int) view.getTag();
                        if (!currentState.get(pos).isExpanded) {
                            currentState.get(pos).isExpanded = true;
                            editNutrition.setText("hide");
                            nutrientHolder.setVisibility(View.VISIBLE);
                        } else {
                            currentState.get(pos).isExpanded = false;
                            editNutrition.setText("edit");
                            nutrientHolder.setVisibility(View.GONE);
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private class viewState{
        public boolean isExpanded = false;
        public boolean isScaleAllChecked = false;
        viewState(){

        }
    }
    // convenience method for getting data at click position
    Ingredient getItem(int id) {
        return mData.get(id);
    }

}