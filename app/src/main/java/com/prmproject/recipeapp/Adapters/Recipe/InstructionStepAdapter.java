package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.Models.Step;
import com.prmproject.recipeapp.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder> {

    Context context;
    List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instruction_steps, parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {
        holder.textView_instruction_steps_number.setText(String.valueOf(list.get(position).getNumber()));
        holder.textView_instruction_steps_title.setText(list.get(position).getStep());
//        holder.recycler_instructions_ingredients.setHasFixedSize(true);
//        holder.recycler_instructions_ingredients.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        //InstructionsIngredientsAdapter instructionsIngredientsAdapter = new InstructionsIngredientsAdapter(context, list.get(position).getIngredients());
        //holder.recycler_instructions_ingredients.setAdapter(instructionsIngredientsAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionStepViewHolder extends RecyclerView.ViewHolder{
    TextView textView_instruction_steps_number, textView_instruction_steps_title;
    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instruction_steps_number=itemView.findViewById(R.id.textView_instruction_steps_number);
        textView_instruction_steps_title=itemView.findViewById(R.id.textView_instruction_steps_title);
    }
}
