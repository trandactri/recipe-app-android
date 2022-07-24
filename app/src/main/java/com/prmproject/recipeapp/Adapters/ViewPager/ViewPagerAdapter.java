package com.prmproject.recipeapp.Adapters.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.prmproject.recipeapp.Fragment.AddRecipeFragment;
import com.prmproject.recipeapp.Fragment.HomeFragment;
import com.prmproject.recipeapp.Views.User.ProfileActivity;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new AddRecipeFragment();
            case 2:
                return new ProfileActivity();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
