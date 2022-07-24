package com.prmproject.recipeapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prmproject.recipeapp.Adapters.UserAdapter;
import com.prmproject.recipeapp.Listener.MemberClickListener;
import com.prmproject.recipeapp.Listener.PaginationScrollListener;
import com.prmproject.recipeapp.Models.Member;
import com.prmproject.recipeapp.Models.User;
import com.prmproject.recipeapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class UserDashboardFragment extends Fragment {
    private View mView;
    private RecyclerView rcvUser;
    private UserAdapter userAdapter;
    private List<Member> mListMember;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Context mContext;
    private EditText etSearch;
    String keyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_admin_dash_board, null);

        initUI();
        loadData(keyword);
        initListener();
        return mView;
    }

    private void initListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    final String value = s.toString().trim().toLowerCase(Locale.ROOT);
                    keyword = value;
                } else {
                    keyword = "";
                }
                mListMember.clear();
                loadData(keyword);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUI() {
        mContext = mView.getContext();
        rcvUser = mView.findViewById(R.id.rcvUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rcvUser.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rcvUser.addItemDecoration(itemDecoration);
        rcvUser.setHasFixedSize(true);
        mListMember = new ArrayList<>();
        userAdapter = new UserAdapter(mListMember, mContext);
        rcvUser.setAdapter(userAdapter);
        etSearch = mView.findViewById(R.id.et_search);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Members");
    }

    private void loadData(String keyword){
//        get(key).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ClearAll();
//                ArrayList<Member> members = new ArrayList<>();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Member member = data.getValue(Member.class);
//                    if(!isCurrentMem(member))
//                        members.add(member);
//                    key = data.getKey();
//                }
//                userAdapter.setData(members);
//                isLoading = false;
//                swipeRefreshLayout.setRefreshing(false);
//                userAdapter.notifyDataSetChanged();
//            }
//
//            private boolean isCurrentMem(Member member) {
//                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                if (id.equals(member.getId())) return true;
//                return false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                swipeRefreshLayout.setRefreshing(false);
//                Toast.makeText(mContext, "Failed to get users list", Toast.LENGTH_SHORT).show();
//            }
//        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Member member = snapshot.getValue(Member.class);
                if(member != null && !isCurrentMem(member)) {
                    if (member.getEmail().toLowerCase(Locale.ROOT).contains(keyword)) {
                        mListMember.add(member);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Member member = snapshot.getValue(Member.class);
                if (member == null || mListMember == null || mListMember.isEmpty()) return;

                for (int i = 0; i < mListMember.size(); i++) {
                    if (member.getId() == mListMember.get(i).getId()) {
                        mListMember.set(i, member);
                        break;
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                if (member == null || mListMember == null || mListMember.isEmpty()) return;

                for (int i = 0; i < mListMember.size(); i++) {
                    if (member.getId() == mListMember.get(i).getId()) {
                        mListMember.remove(mListMember.get(i));
                        break;
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isCurrentMem(Member member) {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (id.equals(member.getId())) return true;
        return false;
    }
}
