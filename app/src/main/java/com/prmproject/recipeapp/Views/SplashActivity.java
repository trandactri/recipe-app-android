package com.prmproject.recipeapp.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Admin.AdminHomeActivity;
import com.prmproject.recipeapp.Views.Authenticate.LoginActivity;
import com.prmproject.recipeapp.Views.Home.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (user == null) {
            intent = new Intent(this, LoginActivity.class);
        } else if (user.getEmail().equals("admin@gmail.com")) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}