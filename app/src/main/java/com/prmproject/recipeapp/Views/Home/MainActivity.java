package com.prmproject.recipeapp.Views.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.prmproject.recipeapp.Adapters.ViewPager.ViewPagerAdapter;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Authenticate.LoginActivity;

public class MainActivity extends AppCompatActivity {
        // ViewPager2
        private ViewPager2 mViewPager2;
        private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initListener();
    }

    private void initListener() {
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menuHome){
                    mViewPager2.setCurrentItem(0);
                } else if (id == R.id.menuAddRecipe){
                    mViewPager2.setCurrentItem(1);
                } else if (id == R.id.menuProfile){
                    mViewPager2.setCurrentItem(2);
                } else {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return true;
            }
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.menuHome).setChecked(true);
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.menuAddRecipe).setChecked(true);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.menuProfile).setChecked(true);
                        break;
                }
            }
        });
    }

    private void initUI() {
        //ViewPager2
        mViewPager2 = findViewById(R.id.view_pager2);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        //End_ViewPager2

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(viewPagerAdapter);
    }
}