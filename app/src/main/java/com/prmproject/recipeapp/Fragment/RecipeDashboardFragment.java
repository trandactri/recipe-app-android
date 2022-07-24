package com.prmproject.recipeapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.Adapters.Recipe.RecipeAdapter;
import com.prmproject.recipeapp.Listener.RecipeClickListener;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeDashboardFragment extends Fragment {
    private View mView;
    private RecyclerView rcvRecipe;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> mListRecipe;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Context mContext;
    private EditText etSearch;
    String keyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_admin_dash_board_recipe, null);

        initUI();
        loadData(keyword);
        initListener();
        return mView;
    }

    private void initListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    final String value = s.toString().trim().toLowerCase(Locale.ROOT);
                    keyword = value;
                } else {
                    keyword = "";
                }
                mListRecipe.clear();
                loadData(keyword);
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUI() {
        mContext = mView.getContext();
        rcvRecipe = mView.findViewById(R.id.rcv_recipe);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        rcvRecipe.setLayoutManager(gridLayoutManager);
        mListRecipe = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(mListRecipe, mContext);
        rcvRecipe.setAdapter(recipeAdapter);
        etSearch = mView.findViewById(R.id.et_search);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Recipes");
    }

    private void loadData(String keyword){
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if(recipe != null) {
                    if (recipe.getTitle().toLowerCase(Locale.ROOT).contains(keyword)
                            || recipe.getSummary().toLowerCase(Locale.ROOT).contains(keyword)
                            || recipe.getServings().toLowerCase(Locale.ROOT).contains(keyword)) {
                        mListRecipe.add(recipe);
                    }
                    recipeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe == null || mListRecipe == null || mListRecipe.isEmpty()) return;

                for (int i = 0; i < mListRecipe.size(); i++) {
                    if (recipe.getId() == mListRecipe.get(i).getId()) {
                        mListRecipe.set(i, recipe);
                        break;
                    }
                }

                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe == null || mListRecipe == null || mListRecipe.isEmpty()) return;

                for (int i = 0; i < mListRecipe.size(); i++) {
                    if (recipe.getId() == mListRecipe.get(i).getId()) {
                        mListRecipe.remove(mListRecipe.get(i));
                        break;
                    }
                }

                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
