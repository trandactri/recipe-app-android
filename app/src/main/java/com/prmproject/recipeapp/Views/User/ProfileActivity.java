package com.prmproject.recipeapp.Views.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.Adapters.ViewPager.ProfileViewPagerAdapter;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.R;

public class ProfileActivity extends Fragment {
    private View mView;
    private TabLayout mTabLayout;

    private ViewPager2 mViewPager;
    private ProfileViewPagerAdapter profileViewPagerAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private ImageView imgAvatar;
    private TextView tvId, tvName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_profile, container ,false);
        initFirebase();
        initUI();
        initListener();
        showUserInformation();
        return mView;
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference("Members").child(user.getUid());
    }

    private void attachTabLayout() {
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Profile");
                    break;
                case 1:
                    tab.setText("Recipes");
                    break;
                case 2:
                    tab.setText("Favourite");
                    break;
            }
        }).attach();
    }

    private void initListener() {

        // Read from database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                tvName.setText(member.getMemberName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        mTabLayout = mView.findViewById(R.id.tab_layout);
        mViewPager = mView.findViewById(R.id.view_pager);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getActivity());
        mViewPager.setAdapter(profileViewPagerAdapter);
        attachTabLayout();

        imgAvatar = mView.findViewById(R.id.img_avatar);
        tvId = mView.findViewById(R.id.tv_id);
        tvName = mView.findViewById(R.id.tv_name);
    }

    private void showUserInformation() {
        if (user == null) {
            return;
        }

        String name = user.getDisplayName();
        Uri photoUrl = user.getPhotoUrl();
        String uid = user.getUid();

        tvId.setText("PID: "+ uid);


        if (name != null) setTextFor(tvName, name);
        else {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    setTextFor(tvName, snapshot.child("memberName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to load user name", Toast.LENGTH_SHORT).show();
                }
            });
        }


        Glide.with(getActivity()).load(photoUrl).error(R.drawable.ic_avatar).into(imgAvatar);
    }

    private void setTextFor(TextView textView, String data) {
        if (data == null) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(data);
        }
    }
}