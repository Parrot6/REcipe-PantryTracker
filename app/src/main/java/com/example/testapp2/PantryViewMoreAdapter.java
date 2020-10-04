package com.example.testapp2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryViewMoreAdapter extends RecyclerView.Adapter<PantryViewMoreAdapter.ViewHolderNestedPantry> {

    private final LayoutInflater mInflater;
    Context context;
    ArrayList<Ingredient> mdata;
    PantryViewMoreClickListener listener;
    private int parentPosition;

    public PantryViewMoreAdapter(Context mcontext, ArrayList<Ingredient> thisSameNameIngredient, int ParentsListPosition,PantryViewMoreClickListener listener){
        context = mcontext;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentPosition = ParentsListPosition;
        this.mdata = thisSameNameIngredient;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderNestedPantry onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_item_subitem, parent, false);
        ViewHolderNestedPantry holder = new ViewHolderNestedPantry(view, listener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNestedPantry holder, int position) {
        holder.quantity.setText(String.valueOf(mdata.get(position).getQuantity()));
        holder.quantity.addTextChangedListener(new ViewMoreEditTextListener(position));
        holder.ingredient.setText(mdata.get(position).getName());
        holder.measurementType.setText(mdata.get(position).getMeasurementType());
        holder.parentPos = parentPosition;
        holder.merData = mdata;
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }
    public static class ViewHolderNestedPantry extends RecyclerView.ViewHolder implements View.OnClickListener {
        ArrayList<Ingredient> merData;
        ImageButton pantryMinus;
        TextView quantity;
        ImageButton pantryPlus;
        TextView ingredient;
        TextView measurementType;
        int parentPos;
        PantryViewMoreAdapter.PantryViewMoreClickListener listener;

        ViewHolderNestedPantry(View itemView, PantryViewMoreAdapter.PantryViewMoreClickListener myClickListener) {
            super(itemView);
            pantryMinus = (ImageButton) itemView.findViewById(R.id.button_pantryNestedMinus);
            pantryMinus.setOnClickListener(this);
            quantity = itemView.findViewById(R.id.text_pantryNestedQuantity);
            pantryPlus = (ImageButton) itemView.findViewById(R.id.button_pantryNestedPlus);
            pantryPlus.setOnClickListener(this);
            ingredient = itemView.findViewById(R.id.text_pantryNestedIngredientName);
            measurementType = itemView.findViewById(R.id.pantryNested_item_measurement_Type);

            this.listener = myClickListener;

            //PantryViewMoreAdapter adapter = new PantryViewMoreAdapter(itemView.getContext(), null, this);
            //nestedRV.setAdapter(adapter);
            //add more listeners here
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_pantryNestedMinus:
                    if(merData.get(this.getLayoutPosition()).getQuantity() > 0) {
                        merData.get(this.getLayoutPosition()).setQuantity(merData.get(this.getLayoutPosition()).getQuantity() - 1);
                        quantity.setText(String.valueOf(merData.get(this.getLayoutPosition()).getQuantity()));
                        //listener.pantryViewMoreUpdateIngredient(parentPos ,this.getLayoutPosition(), -1);
                    }
                    break;
                case R.id.button_pantryNestedPlus:
                    merData.get(this.getLayoutPosition()).setQuantity(merData.get(this.getLayoutPosition()).getQuantity()+1);
                    quantity.setText(String.valueOf(merData.get(this.getLayoutPosition()).getQuantity()));
                    //listener.pantryViewMoreUpdateIngredient(parentPos ,this.getLayoutPosition(), 1);
                    break;
                default:
                    break;
            }
        }
    }
    private class ViewMoreEditTextListener implements TextWatcher {
        private int position;

        ViewMoreEditTextListener(int position) {
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
            }else d = Double.parseDouble(s);
            if(d < 0) d = 0;
            mdata.get(position).setQuantity(d);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
    public interface PantryViewMoreClickListener {
        void pantryViewMoreDeleteIngredient(int layoutPosition);
        void pantryViewMoreUpdateIngredient(int parentPosition, int layoutPosition, int incrementBy);
    }
}
