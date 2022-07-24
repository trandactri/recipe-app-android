package com.prmproject.recipeapp.Adapters.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.prmproject.recipeapp.Fragment.MemberRecipesFragment;
import com.prmproject.recipeapp.Fragment.ProfileFragment;

public class ProfileViewPagerAdapter extends FragmentStateAdapter {


    public ProfileViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new MemberRecipesFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
