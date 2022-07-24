package com.prmproject.recipeapp.Adapters.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.prmproject.recipeapp.Fragment.HomeFragment;
import com.prmproject.recipeapp.Fragment.RecipeDashboardFragment;
import com.prmproject.recipeapp.Fragment.UserDashboardFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new RecipeDashboardFragment();
            case 2:
                return new UserDashboardFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
