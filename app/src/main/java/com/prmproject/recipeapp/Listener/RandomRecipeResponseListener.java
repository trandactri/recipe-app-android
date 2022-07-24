package com.prmproject.recipeapp.Listener;

import com.prmproject.recipeapp.API.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiResponse response, String message);
    void didError(String message);
}
