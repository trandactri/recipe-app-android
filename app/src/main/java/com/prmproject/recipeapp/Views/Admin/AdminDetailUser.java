package com.prmproject.recipeapp.Views.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.R;

public class AdminDetailUser extends AppCompatActivity {
    private TextView tvID, tvEmail, tvPhone, tvRole, tvName, tvDesc;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_admin_delete_user);

        Bundle b = getIntent().getExtras();
        member = (Member) b.getParcelable("memberObj");
        Log.d("Member", member.getEmail());
        initUI();
        setMemberInformation(member);
    }

    private void setMemberInformation(Member member) {
        tvID.setText(String.format("ID: %s", member.getId()));
        tvEmail.setText(String.format("Email: %s", member.getEmail()));
        tvPhone.setText(String.format("Phone: %s", member.getPhone()));
        tvRole.setText(String.format("Role: %s", member.getRole()));
        tvName.setText(String.format("Name: %s", member.getMemberName()));
        tvDesc.setText(String.format("Description: %s", member.getDescription()));
    }

    private void initUI() {
        tvID = findViewById(R.id.tv_id);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvRole = findViewById(R.id.tv_role);
        tvName = findViewById(R.id.tv_name);
        tvDesc = findViewById(R.id.tv_description);

    }
}