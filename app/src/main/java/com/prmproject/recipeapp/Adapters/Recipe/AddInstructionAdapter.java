package com.prmproject.recipeapp.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prmproject.recipeapp.R;

import java.util.List;

public class AddInstructionAdapter extends RecyclerView.Adapter<AddInstructionAdapter.AddInstructionViewHolder> {

    private Context context;
    private List<String> mListInstruction;

    public AddInstructionAdapter(Context context, List<String> mListInstruction) {
        this.context = context;
        this.mListInstruction = mListInstruction;
    }

    @NonNull
    @Override
    public AddInstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_instruction, parent, false);
        return new AddInstructionViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AddInstructionViewHolder holder, int position) {
        holder.etInstruction.setText(mListInstruction.get(position));
    }

    @Override
    public int getItemCount() {
        if (mListInstruction != null) return mListInstruction.size();
        return 0;
    }

    public class AddInstructionViewHolder extends RecyclerView.ViewHolder {

        EditText etInstruction;
        private AddInstructionAdapter adapter;

        public AddInstructionViewHolder(@NonNull View itemView) {
            super(itemView);

            etInstruction = itemView.findViewById(R.id.et_instruction);

            itemView.findViewById(R.id.imageView_removeInstruction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.mListInstruction.remove(getAbsoluteAdapterPosition());
                    adapter.notifyItemRemoved(getAbsoluteAdapterPosition());
                }
            });
        }

        public AddInstructionViewHolder linkAdapter (AddInstructionAdapter adapter) {
            this.adapter = adapter;
            return this;
        }
    }
}
