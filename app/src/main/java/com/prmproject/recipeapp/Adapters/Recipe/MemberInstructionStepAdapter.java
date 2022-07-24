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

public class MemberInstructionStepAdapter extends RecyclerView.Adapter<MemberInstructionStepViewHolder> {
    Context context;
    List<Step> list;

    public MemberInstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MemberInstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberInstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instruction_steps, parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInstructionStepViewHolder holder, int position) {
        holder.textView_instruction_steps_number.setText(String.valueOf(list.get(position).getNumber()));
        holder.textView_instruction_steps_title.setText(list.get(position).getStep());
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }
}
class MemberInstructionStepViewHolder extends RecyclerView.ViewHolder {
    TextView textView_instruction_steps_number, textView_instruction_steps_title;
    public MemberInstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instruction_steps_number=itemView.findViewById(R.id.textView_instruction_steps_number);
        textView_instruction_steps_title=itemView.findViewById(R.id.textView_instruction_steps_title);
    }
}
