package com.example.testapp2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.ViewHolderIng> {
    public static DecimalFormat df = new DecimalFormat("#.00");
    //final private MyClickListener mOnClickListener;
    private ArrayList<Ingredient> mData;
    private LayoutInflater mInflater;
    Context context;
    // data is passed into the constructor
    public NutritionAdapter(Context context, ArrayList<Ingredient> data) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.mData = data;
       // this.mOnClickListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolderIng onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.nutrition_info_item, parent, false);
        ViewHolderIng holder = new ViewHolderIng(view);

        return holder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolderIng holder, final int position) {
        Ingredient ingr = mData.get(position);

        holder.ingredientName.setText(ingr.getName());
        holder.amtAndType.setText(ingr.getQuantity() + " " + ingr.getMeasurementType());
        if(ingr.getNutrition() == null) {
            holder.resultSuccess.setText("Not Queried Yet");
            return;
        }
        holder.resultSuccess.setText(ingr.getNutrition().getQueryResults());
        final Nutrition thisNut = ingr.getNutrition();
        LinearLayout ll = (LinearLayout)holder.itemView.findViewById(R.id.LL_addFields);
        if(holder.itemView.findViewWithTag("tag") != null) {
            RefreshEditTexts(holder.myEditTexts, thisNut);
            return;
        }
        String temp = "Nutrition based on " + df.format(ingr.getNutrition().nf_serving_size_qty) + " " + ingr.getNutrition().nf_serving_size_unit + " of "+ ingr.getNutrition().item_name;
        holder.nutrtionBasedOn.setText(temp);

        for (final Field f: thisNut.getClass().getDeclaredFields()) {
            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setTag("tag");
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            //relativeLayout.getLayoutParams().height = 25;
            ll.addView(relativeLayout);
            try {
                if(f.get(thisNut) instanceof Double && f.get(thisNut) != null) {
                    TextView tv = new TextView(context);
                    tv.setText(f.getName());
                    final EditText et = new EditText(context);
                    holder.myEditTexts.add(et);
                    et.addTextChangedListener(new MyCustomEditTextListener(holder.scaleAll,thisNut, f, holder.myEditTexts, et, position));
                    et.setText(f.get(thisNut).toString());
                    //LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    RelativeLayout.LayoutParams lpl = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    lpl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    lpl.addRule(RelativeLayout.CENTER_VERTICAL);
                    RelativeLayout.LayoutParams lpr = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lpr.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    lpr.addRule(RelativeLayout.CENTER_VERTICAL);
                    tv.setLayoutParams(lpl);
                    et.setLayoutParams(lpr);
                    relativeLayout.addView(tv);
                    relativeLayout.addView(et);

                }
            }
            catch (IllegalStateException ise) {
            }
            // nope
            catch (IllegalAccessException iae) {}
        }
    }

    private void RefreshEditTexts(ArrayList<EditText> myEditTexts, Nutrition thisNut) {
        int premadeFieldIndex = 0;
        for (Field f : thisNut.getClass().getDeclaredFields()) {

            try {
                if (f.get(thisNut) instanceof Double && f.get(thisNut) != null) {
                    myEditTexts.get(premadeFieldIndex).setText(f.get(thisNut).toString());
                }
            }
            catch (IllegalStateException ise) {
            }
            // nope
            catch (IllegalAccessException iae) {
                //Skip private
            }
            premadeFieldIndex++;
        }
        return; //PREVENT DUPLICATIONS
    }
    private void RefreshEditTexts(ArrayList<EditText> myEditTexts, Nutrition thisNut, EditText exclude) {
        int premadeFieldIndex = 0;
        for (Field f : thisNut.getClass().getDeclaredFields()) {

            try {
                if(myEditTexts.get(premadeFieldIndex).equals(exclude)) continue;
                if (f.get(thisNut) instanceof Double && f.get(thisNut) != null) {
                    myEditTexts.get(premadeFieldIndex).setText(f.get(thisNut).toString());
                }
            }
            catch (IllegalStateException ise) {
            }
            // nope
            catch (IllegalAccessException iae) {
                //Skip private
            }
            premadeFieldIndex++;
        }
        return; //PREVENT DUPLICATIONS
    }

    private void scaleAll(ArrayList<EditText> editTexts, EditText from, Nutrition nut, double multiplier) throws IllegalAccessException {

        nut.scaleNutrition(multiplier);
        RefreshEditTexts(editTexts, nut, from);
    };
    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private EditText from;
        private int position;
        private Switch scaleAll;
        Nutrition nut;
        Field thisField;
        private ArrayList<EditText> editTexts;

        MyCustomEditTextListener(Switch scaleAll, Nutrition thisNut, Field f, ArrayList<EditText> editTexts, EditText from, int position) {
            this.scaleAll = scaleAll;
            nut = thisNut;
            thisField = f;
            this.editTexts = editTexts;
            this.from = from;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!from.hasFocus()) return; //stop setText from triggering loop
            try {
                if(editable.length() == 0) thisField.set(nut, 0.0);
                else {

                    try {
                        Log.e("onFocusChance", "in here");
                        Log.e("onFocusChance", "isChecked?:" +scaleAll.isChecked());
                        Log.e("onFocusChance", "isAccess?:" +thisField.isAccessible());
                        if(scaleAll.isChecked()) {
                            if (thisField.get(nut).equals(from.getText().toString())) return; //do nothing if no change
                                Double before = (Double) thisField.get(nut);
                                Double differenceMultiplier = 1.0;
                                if (before != 0.0)
                                    differenceMultiplier = Double.parseDouble(from.getText().toString()) / before;
                                scaleAll(editTexts, from, nut, differenceMultiplier);
                                Log.e("onFocusChance", "scaleAllCalled");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    thisField.set(nut, Double.parseDouble(editable.toString()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolderIng extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView amtAndType;
        TextView ingredientName;
        TextView resultSuccess;
        Button editNutrition;
        View itemView;
        ConstraintLayout nutrientHolder;
        ArrayList<EditText> myEditTexts = new ArrayList<>();
        TextView nutrtionBasedOn;
        Switch scaleAll;
        ViewHolderIng(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nutrientHolder = itemView.findViewById(R.id.layout_nutrition_details);
            nutrientHolder.setVisibility(View.GONE);
            ingredientName = itemView.findViewById(R.id.text_nutrition_info_name);
            editNutrition = itemView.findViewById(R.id.button_nutrition_info_edit);
            amtAndType = itemView.findViewById(R.id.text_nutrtition_info_qtyAndMeas);
            resultSuccess = itemView.findViewById(R.id.text_nutrition_info_result);
            editNutrition.setText("edit");
            editNutrition.setOnClickListener(this);
            nutrtionBasedOn = itemView.findViewById(R.id.text_nutrition_info_basedOn);
            scaleAll = itemView.findViewById(R.id.switch_scaleNutrition);
            //add more listeners here
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_nutrition_info_edit:
                    //if (listener != null) {
                        //listener.deleteIngredient(this.getLayoutPosition());
                   // }
                    if(editNutrition.getText().equals("edit")) {
                        editNutrition.setText("hide");
                        nutrientHolder.setVisibility(View.VISIBLE);
                    } else {
                        editNutrition.setText("edit");
                        nutrientHolder.setVisibility(View.GONE);
                    }
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

}