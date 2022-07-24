package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.R;

import java.util.List;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.AddIngredientViewHolder> {

    private Context context;
    private List<String> mListIngredient;

    public AddIngredientAdapter(Context context, List<String> mListIngredient) {
        this.context = context;
        this.mListIngredient = mListIngredient;
    }

    @NonNull
    @Override
    public AddIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ingredient, parent, false);
        return new AddIngredientViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientViewHolder holder, int position) {
        holder.etIngredient.setText(mListIngredient.get(position));
    }

    @Override
    public int getItemCount() {
        if (mListIngredient != null) return mListIngredient.size();
        return 0;
    }

    public class AddIngredientViewHolder extends RecyclerView.ViewHolder {

        EditText etIngredient;
        private AddIngredientAdapter adapter;

        public AddIngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            etIngredient = itemView.findViewById(R.id.et_ingredient);

            itemView.findViewById(R.id.imageView_removeIngredient).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mListIngredient.remove(getAbsoluteAdapterPosition());
                    adapter.notifyItemRemoved(getAbsoluteAdapterPosition());
                }
            });
        }

        public AddIngredientViewHolder linkAdapter(AddIngredientAdapter adapter) {
            this.adapter = adapter;
            return this;
        }


    }

}
