package com.prmproject.recipeapp.Views.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Home.MainActivity;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText emailEdt, phoneEdt;
    private EditText pwdEdt, cnfPwdEdt;
    private Button registerBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Member member;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initUI();
        initListener();
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegister();
            }
        });
    }

    private void onClickRegister() {
        String email = emailEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String pwd = pwdEdt.getText().toString().trim();
        String cnfPwd = cnfPwdEdt.getText().toString().trim();
        String memberName = emailEdt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEdt.setError("Email cannot be blank");
            emailEdt.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdt.setError("Invalid email");
            emailEdt.requestFocus();
            return;
        }
        else {
            member.setEmail(email);
            member.setMemberName(memberName);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEdt.setError("Phone number cannot be blank");
            phoneEdt.requestFocus();
            return;
        } else if (phone.length() != 10) {
            phoneEdt.setError("Invalid phone number");
            phoneEdt.requestFocus();
            return;
        }
        else {
            member.setPhone(phone);
        }

        if (TextUtils.isEmpty(pwd)) {
            pwdEdt.setError("Password cannot be blank");
            pwdEdt.requestFocus();
            return;
        } else if (pwd.length() < 6) {
            pwdEdt.setError("Password should be at least 6 characters");
            pwdEdt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cnfPwd)) {
            cnfPwdEdt.setError("Confirm password cannot be blank");
            cnfPwdEdt.requestFocus();
            return;
        }

        if (!pwd.equals(cnfPwd)) {
            cnfPwdEdt.setError("Password doesn't match");
            cnfPwdEdt.requestFocus();
            return;
        }

        member.setDescription("");
        member.setMemberName(email);

        member = new Member(null, email, "member", memberName, "", phone);

        loadingPB.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(member.getEmail(), pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    member.setId(id);
                    databaseReference.child(id).setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Fail to register member", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mAuth.signInWithEmailAndPassword(member.getEmail(), pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingPB.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(i);
                                finishAffinity();
                            } else {
                                Toast.makeText(SignupActivity.this, "Fail to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        emailEdt = findViewById(R.id.et_email);
        phoneEdt = findViewById(R.id.et_phone);
        pwdEdt = findViewById(R.id.et_password);
        cnfPwdEdt = findViewById(R.id.et_confirm_password);
        registerBtn = findViewById(R.id.button_signup);
        loadingPB = findViewById(R.id.progress_loading);
        member = new Member();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Members");
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setStatusBarTransparent(SignupActivity.this);
    }

    private void setStatusBarTransparent(AppCompatActivity activity) {
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}
