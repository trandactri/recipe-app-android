package com.prmproject.recipeapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.User.ProfileActivity;

public class ProfileFragment extends Fragment {
    private View ProfileFragmentView;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView tvDescription, tvPhone, tvEmail;
    private ImageView imgEdit;

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

//        String description = user
//        String phone = user.getPhoneNumber();
        String email = user.getEmail();

//        setTextFor(tvPhone, phone);
        setTextFor(tvEmail, email);
    }

    private void setTextFor(TextView textView, String data) {
        if (data == null) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(data);
        }
    }

    private void initListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                addTextViewVal(member);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileFragmentView.getContext(), "Failed to load profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextViewVal(Member mem) {
        tvDescription.setText(mem.getDescription());
        tvPhone.setText(mem.getPhone());
        tvEmail.setText(mem.getEmail());
    }

    private void initUI(View view) {
        tvDescription = view.findViewById(R.id.tv_description);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEmail = view.findViewById(R.id.tv_email);
        imgEdit = view.findViewById(R.id.img_edit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        initFirebase();
        initUI(ProfileFragmentView);
        initListener();
        showUserInformation();
        return ProfileFragmentView;
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference("Members").child(user.getUid());
    }
}
