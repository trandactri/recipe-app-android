package com.prmproject.recipeapp.Listener;

import com.prmproject.recipeapp.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);
    void didError(String message);
}
