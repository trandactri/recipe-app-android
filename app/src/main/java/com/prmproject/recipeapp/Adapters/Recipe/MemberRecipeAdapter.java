package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.Listener.RecipeClickListener;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemberRecipeAdapter extends  RecyclerView.Adapter<MemberRecipeAdapter.MemberRecipeViewHolder> {

    private Context mContext;
    private List<Recipe> mListRecipe;
    RecipeClickListener listener;

    public MemberRecipeAdapter(Context mContext, List<Recipe> mListRecipe, RecipeClickListener listener) {
        this.mContext = mContext;
        this.mListRecipe = mListRecipe;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_random_recipe, parent, false);
        return new MemberRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberRecipeViewHolder holder, int position) {
        String serving = mListRecipe.get(position).getServings().trim();
        if (serving.isEmpty()) {
            holder.viewBreak.setVisibility(View.GONE);
            holder.icServing.setVisibility(View.GONE);
            holder.tvServing.setVisibility(View.GONE);
        }
        holder.tvServing.setText(serving);
        holder.tvTitle.setText(mListRecipe.get(position).getTitle());
        holder.tvTitle.setSelected(true);
        Picasso.get().load(mListRecipe.get(position).getImage()).into(holder.imgRecipe);

        holder.cvMemberRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(mListRecipe.get(holder.getAbsoluteAdapterPosition()).getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListRecipe != null) {
            return mListRecipe.size();
        }
        return 0;
    }

    public class MemberRecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgRecipe;
        private final TextView tvTitle, tvServing;
        private final CardView cvMemberRecipe;
        private final View viewBreak;
        private final ImageView icServing;

        public MemberRecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            imgRecipe = itemView.findViewById(R.id.imageView_food);
            tvTitle = itemView.findViewById(R.id.textView_title);
            tvServing = itemView.findViewById(R.id.textView_servings);
            cvMemberRecipe = itemView.findViewById(R.id.random_list_container);
            viewBreak = itemView.findViewById(R.id.view_break);
            icServing = itemView.findViewById(R.id.ic_serving);
        }
    }
}
