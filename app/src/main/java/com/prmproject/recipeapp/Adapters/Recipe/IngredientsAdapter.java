package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.Models.ExtendedIngredient;
import com.prmproject.recipeapp.R;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsViewHolder>{

    Context context;
    List<ExtendedIngredient> list;

    public IngredientsAdapter(Context context, List<ExtendedIngredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_meal_ingredients,parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        holder.textView_ingredients_name.setText(list.get(position).getName());
        holder.textView_ingredients_name.setSelected(true);
        if (list.get(position).getUnit().trim().length() != 0 || list.get(position).getAmount() != 0) {
            holder.textView_ingredients_quantity.setVisibility(View.VISIBLE);
            holder.textView_ingredients_quantity.setText(BigDecimal.valueOf(list.get(position).getAmount()).stripTrailingZeros().toPlainString() + " " + list.get(position).getUnit());
            holder.textView_ingredients_quantity.setSelected(true);
        }
        if (list.get(position).getImage() != null) {
            Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+list.get(position).getImage()).into(holder.imageView_ingredients);
        } else {
          holder.imageView_ingredients.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class IngredientsViewHolder extends RecyclerView.ViewHolder {
    TextView textView_ingredients_quantity,textView_ingredients_name;
    ImageView imageView_ingredients;
    public IngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_ingredients_quantity = itemView.findViewById(R.id.textView_ingredients_quantity);
        textView_ingredients_name = itemView.findViewById(R.id.textView_ingredients_name);
        imageView_ingredients = itemView.findViewById(R.id.imageView_ingredients);
    }
}
