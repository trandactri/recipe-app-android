package com.prmproject.recipeapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Admin.AdminDetailUser;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<Member> mListUser = new ArrayList<>();
    private Context context;

    public UserAdapter(List<Member> mListUser, Context context) {
        this.mListUser = mListUser;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        Member member = mListUser.get(position);
        if (member == null) return;
        userViewHolder.tvName.setText( (position+1) + " " + member.getEmail());

        userViewHolder.imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_delete) {
                            onClickDelete();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.member_option_menu);
                popupMenu.show();
            }

            private void onClickDelete() {
                userViewHolder.builder = new AlertDialog.Builder(context);
                userViewHolder.builder.setMessage("Do you really want to delete this employee?");
                userViewHolder.builder.setCancelable(true);

                dialogDelete();


                userViewHolder.alert = ((UserViewHolder) holder).builder.create();
                userViewHolder.alert.show();
            }

            private void dialogDelete() {
                userViewHolder.builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Members").child(member.getId());
                                database.removeValue();
                                Toast.makeText(context, "Successfully delete " + member.getEmail(), Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                userViewHolder.builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }
        });

        userViewHolder.llUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("memberObj", member);
                Intent i = new Intent(context, AdminDetailUser.class);
                i.putExtras(b);
                context.startActivity(i);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListUser != null){
            return mListUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private LinearLayout llUser;
        private ImageButton imgOption;
        private AlertDialog.Builder builder;
        private AlertDialog alert;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_Name);
            llUser = itemView.findViewById(R.id.user_item);
            imgOption = itemView.findViewById(R.id.img_option);
        }

    }
}
