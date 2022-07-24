package com.prmproject.recipeapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.Adapters.Recipe.MemberRecipeAdapter;
import com.prmproject.recipeapp.Listener.RecipeClickListener;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Recipe.DetailRecipeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberRecipesFragment extends Fragment {

    private View MemberRecipesView;
    private RecyclerView rcvRecipe;
    private MemberRecipeAdapter mMemberRecipeAdapter;
    private Context mContext;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Recipe> mListRecipes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MemberRecipesView = inflater.inflate(R.layout.fragment_member_recipes, container, false);
        initUI();

        getListRecipesFromRealTimDatabase();

        return MemberRecipesView;
    }

    private void initUI() {
        mContext = MemberRecipesView.getContext();
        rcvRecipe = MemberRecipesView.findViewById(R.id.rcv_recipe);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        rcvRecipe.setLayoutManager(gridLayoutManager);
    }

    private void getListRecipesFromRealTimDatabase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Recipes");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearAll();
                Map<String, Object> recipes = (Map<String, Object>) snapshot.getValue();
                for (Object recipeObj : recipes.values()) {
                    Map<String, Object> recipe = (Map<String, Object>) recipeObj;
                    if (recipe.get("memberId").toString().equals(user.getUid())) {
                        Recipe memberRecipe = new Recipe();
                        memberRecipe.setId((String) recipe.get("id"));
                        memberRecipe.setTitle((String) recipe.get("title"));
                        memberRecipe.setServings((String) recipe.get("servings"));
                        memberRecipe.setImage((String) recipe.get("image"));
                        mListRecipes.add(memberRecipe);
                    }
                }

                if (mListRecipes.size() == 0) {
                    Toast.makeText(mContext, "Your recipes are empty. Please add one.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mMemberRecipeAdapter = new MemberRecipeAdapter(mContext, mListRecipes, recipeClickListener);
                rcvRecipe.setAdapter(mMemberRecipeAdapter);
                mMemberRecipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Failed to get recipes list", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void ClearAll() {
        if (mListRecipes != null) {
            mListRecipes.clear();

            if (mMemberRecipeAdapter != null) {
                mMemberRecipeAdapter.notifyDataSetChanged();
            }
        }

        mListRecipes = new ArrayList<>();
    }

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(mContext, DetailRecipeActivity.class)
                    .putExtra("id",id));
//            Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
        }
    };
}
