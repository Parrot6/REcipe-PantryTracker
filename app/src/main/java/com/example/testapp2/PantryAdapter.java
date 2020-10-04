package com.example.testapp2;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.Visibility;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolderPantry> implements PantryViewMoreAdapter.PantryViewMoreClickListener {
    final private PantryClickListener mOnClickListener;
    private ArrayList<SameNameIngredients> sameNameIngredients;
    private LayoutInflater mInflater;
    private Context context;
    public RecyclerView.Adapter nestedPantryList;

    public PantryAdapter(Context mcontext, ArrayList<SameNameIngredients> data, PantryClickListener listener) {
        context = mcontext;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.sameNameIngredients = MainActivity.pantry;
        this.mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolderPantry onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_item, parent, false);
        ViewHolderPantry holder = new ViewHolderPantry(view, mOnClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPantry holder, int position) {
        ArrayList<Ingredient> thisSameNameIngredient = sameNameIngredients.get(position).getIngredientSummary();
        holder.quantity.setText(String.valueOf(thisSameNameIngredient.get(0).getQuantity()));
        holder.quantity.addTextChangedListener(new MyCustomEditTextListener(position));
        holder.ingredient.setText(thisSameNameIngredient.get(0).getName());
        holder.measurementType.setText(thisSameNameIngredient.get(0).getMeasurementType());
        holder.nestedRV.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL, false ));
        holder.nestedRV.setVisibility(View.GONE);
        ArrayList<Ingredient> childNameIngredients = new ArrayList<>();
        for(int i = 1; i < thisSameNameIngredient.size(); i++){
            childNameIngredients.add(thisSameNameIngredient.get(i));
        }
        if(childNameIngredients.size() == 0) holder.viewMoreTypes.setVisibility(View.GONE);
        nestedPantryList = new PantryViewMoreAdapter(context, childNameIngredients, position,(PantryViewMoreAdapter.PantryViewMoreClickListener) this);
        holder.nestedRV.setAdapter(nestedPantryList);
        holder.allIngred = thisSameNameIngredient;
    }

    @Override
    public int getItemCount() {
        return MainActivity.pantry.size();
    }

    @Override
    public void pantryViewMoreDeleteIngredient(int layoutPosition) {
            //mOnClickListener.pantryUpdateIngredient(layoutPosition, );
    }

    @Override
    public void pantryViewMoreUpdateIngredient(int parent, int layoutPosition, int inc) {
        mOnClickListener.pantryUpdateIngredient(parent, layoutPosition+1, inc);
    }

    public static class ViewHolderPantry extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton pantryMinus;
        TextView quantity;
        ImageButton pantryPlus;
        TextView ingredient;
        TextView measurementType;
        Button viewMoreTypes;
        RecyclerView nestedRV;
        PantryClickListener listener;
        LinearLayout Layout;
        ArrayList<Ingredient> allIngred;
        int buttonvis = View.GONE;
        ViewHolderPantry(View itemView, PantryClickListener myClickListener) {
            super(itemView);
            pantryMinus = (ImageButton) itemView.findViewById(R.id.button_pantryMinus);
            pantryMinus.setOnClickListener(this);
            quantity = itemView.findViewById(R.id.text_pantryQuantity);
            pantryPlus = (ImageButton) itemView.findViewById(R.id.button_pantryPlus);
            pantryPlus.setOnClickListener(this);
            ingredient = itemView.findViewById(R.id.text_pantryIngredientName);
            measurementType = itemView.findViewById(R.id.pantry_item_measurement_Type);
            viewMoreTypes = itemView.findViewById(R.id.button_viewMoreTypes);
            this.listener = myClickListener;
            viewMoreTypes.setOnClickListener(this);
            nestedRV = itemView.findViewById(R.id.pantry_subItems);
            Layout = itemView.findViewById(R.id.pantry_linearLayout);

            //PantryViewMoreAdapter adapter = new PantryViewMoreAdapter(itemView.getContext(), null, this);
            //nestedRV.setAdapter(adapter);
            //add more listeners here
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_viewMoreTypes:
                    if(nestedRV.getVisibility() == View.VISIBLE){
                        viewMoreTypes.setText("Other Types");

                        buttonvis = View.GONE;
                        nestedRV.setVisibility(View.GONE);

                    } else {
                        viewMoreTypes.setText("Hide");
                        nestedRV.setVisibility(View.VISIBLE);
                        buttonvis = View.VISIBLE;
                        //Layout.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    }
                    break;
                case R.id.button_pantryMinus:
                    if(allIngred.get(0).getQuantity() > 0) {
                        //quantity.setText(Integer.parseInt((String) quantity.getText())-1);
                        allIngred.get(0).setQuantity(allIngred.get(0).getQuantity()-1);
                        quantity.setText(String.valueOf(allIngred.get(0).getQuantity()));
                        //listener.pantryUpdateIngredient(this.getLayoutPosition(), 0, -1);
                    }
                    break;
                case R.id.button_pantryPlus:
                    //quantity.setText(Integer.parseInt((String) quantity.getText())+1);
                    allIngred.get(0).setQuantity(allIngred.get(0).getQuantity()+1);
                    quantity.setText(String.valueOf(allIngred.get(0).getQuantity()));
                    //listener.pantryUpdateIngredient(this.getLayoutPosition(),0, 1);

                    break;
                default:
                    break;
            }
        }
    }
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        MyCustomEditTextListener(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String s = charSequence.toString();
            double d;
            if(s.equals("")) {
                d = 0;
            }else d = Double.parseDouble(charSequence.toString());
            if(d < 0) d = 0;
            sameNameIngredients.get(position).getIngredientSummary().get(0).setQuantity(d);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
    public interface PantryClickListener {

        void pantryDeleteIngredient(int layoutPosition);
        void pantryUpdateIngredient(int layoutPosition, int secondPosition, int incrementBy);
    }
}
