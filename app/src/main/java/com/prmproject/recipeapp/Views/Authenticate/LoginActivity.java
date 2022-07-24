package com.prmproject.recipeapp.Views.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Admin.AdminHomeActivity;
import com.prmproject.recipeapp.Views.Home.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEdt;
    private EditText pwdEdt;
    private Button loginBtn, signupBtn, forgot_pwdBtn;
    private ProgressBar loadingPB;
    private FirebaseAuth mAuth;
    private String currentUserUID;
    private DatabaseReference roleRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
        initListener();
    }

    private void initListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        forgot_pwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void onClickLogin() {
        loadingPB.setVisibility(View.VISIBLE);
        String email = emailEdt.getText().toString();
        String pwd = pwdEdt.getText().toString();
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "Please enter your credentials..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    try {
                        currentUserUID = mAuth.getCurrentUser().getUid();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    roleRef = database.child("Members").child(currentUserUID).child("role");
                    if (roleRef == null) {
                        Toast.makeText(LoginActivity.this, "Sorry, your account has been deleted", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (task.isSuccessful()) {
                        loadingPB.setVisibility(View.GONE);
                        roleRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    String userRole = snapshot.getValue().toString();
                                    if (userRole.equals("admin")) {
                                        Toast.makeText(LoginActivity.this, "Login Successfully..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login Successfully..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                } catch (Throwable e) {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Fail to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initUI() {
        emailEdt = findViewById(R.id.et_email);
        pwdEdt = findViewById(R.id.et_password);
        loginBtn = findViewById(R.id.button_signin);
        signupBtn = findViewById(R.id.button_signup);
        forgot_pwdBtn = findViewById(R.id.button_forgot_password);
        loadingPB = findViewById(R.id.progress_loading);
        mAuth = FirebaseAuth.getInstance();
        setStatusBarTransparent(LoginActivity.this);
    }

    private void setStatusBarTransparent(AppCompatActivity activity) {
        //Make Status bar transparent
        //Make Status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}
