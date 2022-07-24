package com.prmproject.recipeapp.Listener;

import com.prmproject.recipeapp.Models.InstructionResponse;

import java.util.List;

public interface InstructionListener {
    void didFetch(List<InstructionResponse> response, String message);
    void didError(String message);
}
