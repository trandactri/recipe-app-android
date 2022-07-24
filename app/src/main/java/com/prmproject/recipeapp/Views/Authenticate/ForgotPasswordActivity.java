package com.prmproject.recipeapp.Views.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.prmproject.recipeapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText etEmail;
    private Button btnResetPwd;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initUI();
        initListener();
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordActivity.super.onBackPressed();
            }
        });

        btnResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setStatusBarWhite(ForgotPasswordActivity.this);

        etEmail = findViewById(R.id.et_email);
        btnResetPwd = findViewById(R.id.button_send);
        progressBar = findViewById(R.id.progress_loading);

        mAuth = FirebaseAuth.getInstance();
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email!");
            etEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Try again! Something wrong happened", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStatusBarWhite(AppCompatActivity activity) {
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}
