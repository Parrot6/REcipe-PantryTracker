package com.example.testapp2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.StringWriter;
import java.util.ArrayList;

public class EditCartFragment extends Fragment implements EditCartAdapter.CartClickListener  {

    RecyclerView rv;
    Button but1;
    Button but2;
    EditCartAdapter adapter;
    ArrayList<CartItem> cartData;
    ArrayList<SameNameIngredients> ingredientSummary = new ArrayList<>();
    TextView outputTextView;
    EditCartAdapter.CartClickListener thisListener = this;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context mcontext) {
        super.onAttach(mcontext);
        context=mcontext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the
        View view = inflater.inflate(R.layout.fragment_editcart, parent, false);

        cartData = MainActivity.getCart();

        adapter = new EditCartAdapter(context, cartData, this);
        rv = view.findViewById(R.id.recycleView_EditCart);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);

        outputTextView = view.findViewById(R.id.text_RESULTTEST);

        but1 = view.findViewById(R.id.button_EditCart1);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tring test = printCart(); //printCartTotalIngredients();
                //outputTextView.setText(printCartTotalIngredients());
                makeSummaryFromCartData();
                outputTextView.setText(printCart());
            }
        });
        but2 = view.findViewById(R.id.button_EditCart2);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", printCart());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context,"Copied to Clipboard!",Toast.LENGTH_SHORT).show();

            }
        });
        View sortBarHolder = view.findViewById(R.id.editcart_sortbarholder);
        SortBar editcartSortBar = new SortBar(sortBarHolder);
        return view;
    }

    private void makeSummaryFromCartData() {
        boolean Added = false;
        ingredientSummary = new ArrayList<>();
        for(int outer = 0; outer < cartData.size(); outer++) { //for EACH CART ITEM
            if(cartData.get(outer).getQuantity() == 0) continue;
            //Log.e("OnCartItem:", cartData.get(outer).getRecipe().getRecipeTitle());
            //Log.e("quantity is:", cartData.get(outer).getQuantity()+"");
            ArrayList<Ingredient> temp = cartData.get(outer).getIngredientsTimesCart();
            for (int i = 0; i < temp.size(); i++) { //for EACH INGREDIENT PER CART ITEM
                Ingredient ingred = temp.get(i);
                for (int k = 0; k < ingredientSummary.size(); k++) {
                    if(ingredientSummary.size() == 0){
                        ingredientSummary.add(new SameNameIngredients(ingred));
                        Added = true;
                        break;
                    } else if(Added = ingredientSummary.get(k).handleNewIngredient(ingred)){ break;}
                }
                if(!Added) ingredientSummary.add(new SameNameIngredients(ingred));
            }
        }
        Log.e("makeSummaryCartData", ingredientSummary.toString());
        }

    public String printCart(){
        StringWriter sm = new StringWriter();
        for(int i = 0; i < ingredientSummary.size(); i++){
            ArrayList<Ingredient> temp = new ArrayList<>();
            temp.addAll(ingredientSummary.get(i).getIngredientSummary());
            sm.write(ingredientSummary.get(i).getIngredName());
            StringBuffer sb = new StringBuffer();
            for(int h = 0; h < ingredientSummary.get(i).getIngredName().length() + 3; h++){sb.append(" ");} //make spaces equal to ingredient name length

            for(int k = 0; k < temp.size(); k++){
                if(k > 0) sm.write(sb.toString());
                double stocked = PantryFragment.checkIngredientStock(temp.get(k));
                sm.write(" " + temp.get(k).getQuantity() + " " + temp.get(k).getMeasurementType());
                if(stocked > 0) {
                    sm.write(" (" + PantryFragment.checkIngredientStock(temp.get(k)) + " " + temp.get(k).getMeasurementType() + " in pantry)");
                }
                sm.write("\n");
            }
        }
        return sm.toString();
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
            adapter = new EditCartAdapter(context, cartData, thisListener);
            rv.setAdapter(adapter);
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
    public void minusQuantity(int layoutPosition) {
        cartData.get(layoutPosition).decrementQuant();
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void plusQuantity(int layoutPosition) {
        cartData.get(layoutPosition).incrementQuant();
        //adapter.notifyDataSetChanged();
    }

}
