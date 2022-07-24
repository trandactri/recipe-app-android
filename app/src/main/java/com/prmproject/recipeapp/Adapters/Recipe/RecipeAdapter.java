package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prmproject.recipeapp.Listener.RecipeClickListener;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    List<Recipe> mListRecipe = new ArrayList<>();
    private Context context;

    public RecipeAdapter(List<Recipe> mListRecipe, Context context) {
        this.mListRecipe = mListRecipe;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_display, parent, false);
        return new RecipeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe recipe = mListRecipe.get(position);
            if (recipe == null) return;
            holder.tvTitle.setText(recipe.getTitle());
            holder.tvTitle.setSelected(true);
            Picasso.get().load(recipe.getImage()).resize(800,300).centerCrop().into(holder.imgRecipe);

            holder.cvRecipe.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Recipes").child(mListRecipe.get(holder.getAbsoluteAdapterPosition()).getId()).child("active");
                    myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String active = task.getResult().getValue().toString();
                            if (active.equals("false")) {
                                myRef.setValue(true);
                                Toast.makeText(context, "Successfully Public Recipe " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                            } else {
                                myRef.setValue(false);
                                Toast.makeText(context, "Successfully Remove From Home Recipe " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    return true;
                }
            });
    }

    @Override
    public int getItemCount() {
        if(mListRecipe != null){
            return mListRecipe.size();
        }
        return 0;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgRecipe;
        private TextView tvTitle;
        private CardView cvRecipe;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.img_recipe);
            tvTitle = itemView.findViewById(R.id.tv_title);
            cvRecipe = itemView.findViewById(R.id.cv_memberRecipe);
        }

    }
}
