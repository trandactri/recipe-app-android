package com.prmproject.recipeapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prmproject.recipeapp.API.RandomRecipeApiResponse;
import com.prmproject.recipeapp.API.RequestManager;
import com.prmproject.recipeapp.Adapters.Recipe.RandomRecipeAdapter;
import com.prmproject.recipeapp.Adapters.Recipe.RecipeAdapter;
import com.prmproject.recipeapp.Listener.RandomRecipeResponseListener;
import com.prmproject.recipeapp.Listener.RecipeClickListener;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Recipe.DetailRecipeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private View mView;
    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView, rvMemberRecipes;
    Spinner spinner;
    List<String> tags = new ArrayList<>();
    SearchView searchView;
    private List<Recipe> mListRecipe;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String keyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_home, container, false);

        initUI();
        loadMemberRecipes(keyword);
        initListener();

        return mView;
    }

    public void loadMemberRecipes(String keyword) {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if(recipe != null && recipe.isActive()) {
                    if (recipe.getTitle().toLowerCase(Locale.ROOT).contains(keyword)
                            || recipe.getSummary().toLowerCase(Locale.ROOT).contains(keyword)
                            || recipe.getServings().toLowerCase(Locale.ROOT).contains(keyword)) {
                        mListRecipe.add(recipe);
                }
                randomRecipeAdapter.notifyDataSetChanged();
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

                randomRecipeAdapter.notifyDataSetChanged();
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

                randomRecipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mListRecipe.clear();
                loadMemberRecipes(query.trim().toLowerCase(Locale.ROOT));
                onQuerySubmit(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner.setOnItemSelectedListener(spinnerSelectedListener);


    }

    private void onQuerySubmit(String query) {
        tags.clear();
        tags.add(query);
        manager.getRandomRecipes(randomRecipeResponseListener, tags);
        dialog.show();
    }

    private void initUI() {

        dialog = new ProgressDialog(mView.getContext());
        dialog.setTitle("Loading...");

        searchView = mView.findViewById(R.id.searhView_Home);
        spinner = mView.findViewById(R.id.spinner_tags);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(mView.getContext(),R.array.tags,R.layout.spinner_text);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        manager = new RequestManager(mView.getContext());

        // Member Recipes Init
        rvMemberRecipes = mView.findViewById(R.id.recycler_user_recipes);
        rvMemberRecipes.setNestedScrollingEnabled(false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Recipes");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mView.getContext(), 2);
        rvMemberRecipes.setLayoutManager(gridLayoutManager);
        mListRecipe = new ArrayList<>();
        randomRecipeAdapter = new RandomRecipeAdapter(mView.getContext(), mListRecipe, recipeClickListener);
        rvMemberRecipes.setAdapter(randomRecipeAdapter);
    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recyclerView = mView.findViewById(R.id.recycler_random);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(mView.getContext(), 2));
            randomRecipeAdapter = new RandomRecipeAdapter(mView.getContext(), response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(mView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?>  adapterView, View view, int position, long id) {
            tags.clear();
            tags.add(adapterView.getSelectedItem().toString());
            manager.getRandomRecipes(randomRecipeResponseListener,tags);
            dialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(mView.getContext(), DetailRecipeActivity.class)
                    .putExtra("id",id));
        }
    };
}
