package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.Models.InstructionResponse;
import com.prmproject.recipeapp.R;

import java.util.List;

public class InstructionAdapter extends RecyclerView.Adapter<InstructionViewHolder> {

    Context context ;
    List<InstructionResponse> list;

    public InstructionAdapter(Context context, List<InstructionResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
//        holder.textView_instruction_name.setText(list.get(position).name);
        holder.recycler_instructions_steps.setHasFixedSize(true);
        holder.recycler_instructions_steps.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        InstructionStepAdapter stepAdapter = new InstructionStepAdapter(context,list.get(position).steps);
        holder.recycler_instructions_steps.setAdapter(stepAdapter);
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }
}

class InstructionViewHolder extends RecyclerView.ViewHolder{

    TextView textView_instruction_name;
    RecyclerView recycler_instructions_steps;
    public InstructionViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instruction_name = itemView.findViewById(R.id.textView_instruction_name);
        recycler_instructions_steps = itemView.findViewById(R.id.recycler_instructions_steps);
    }
}