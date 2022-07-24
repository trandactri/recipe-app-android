package com.prmproject.recipeapp.Views.Recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prmproject.recipeapp.API.RequestManager;
import com.prmproject.recipeapp.Adapters.Recipe.IngredientsAdapter;
import com.prmproject.recipeapp.Adapters.Recipe.InstructionAdapter;
import com.prmproject.recipeapp.Adapters.Recipe.MemberInstructionStepAdapter;
import com.prmproject.recipeapp.Listener.InstructionListener;
import com.prmproject.recipeapp.Listener.RecipeDetailsListener;
import com.prmproject.recipeapp.Models.ExtendedIngredient;
import com.prmproject.recipeapp.Models.InstructionResponse;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.Models.RecipeDetailsResponse;
import com.prmproject.recipeapp.Models.Step;
import com.prmproject.recipeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailRecipeActivity extends AppCompatActivity {
    String id, userID;
    TextView textView_meal_name,textView_meal_summary, tvUsername, tvGmail, tvTime;
    ImageView imageView_meal_image, imageViewBack, imgAvatar;
    RecyclerView recycler_meal_ingredients, recycler_meal_instructions, recycler_meal_instructions_member;
    CardView cvMemberInstruction;
    ImageButton imgRemove;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    InstructionAdapter instructionAdapter;
    MemberInstructionStepAdapter memberInstructionStepAdapter;
    LinearLayout linearUser;
    Button btnEdit;
    Recipe recipe;



    private void deleteRecipe() {
        myRef = database.getReference("Recipes");
        displayAlertDialog();
    }

    private void displayAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailRecipeActivity.this);
        builder1.setMessage("Do you really want to delete this recipe?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + id);
                        storageReference.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailRecipeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        String recName = textView_meal_name.getText().toString();
                        myRef.child(id).removeValue();
                        Toast.makeText(DetailRecipeActivity.this, "Successfully Delete Recipe " + recName, Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);
        database = FirebaseDatabase.getInstance();
        
        initUI();
        initListener();
    }

    private void initListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot.child("title").getValue() == null) {
                    int apiID = Integer.parseInt(id);
                    manager = new RequestManager(DetailRecipeActivity.this);
                    manager.getRecipeDetails(recipeDetailsListener, apiID);
                    manager.getInstructions(instructionListener, apiID);
                    return;
                }
                String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                userID = snapshot.child("memberId").getValue().toString();
                showRecipeDetail(snapshot);
                myRef = database.getReference("Members");
                if (currentUID == myRef.child(userID).getKey()) {
                    btnEdit.setVisibility(View.VISIBLE);
                    imgRemove.setVisibility(View.VISIBLE);
                }

                myRef.child(currentUID).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            String role = task.getResult().getValue().toString();
                            if (role.equals("admin")) imgRemove.setVisibility(View.VISIBLE);
                        }
                    }
                });
                showUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailRecipeActivity.this, "Failed to load recipe details", Toast.LENGTH_SHORT).show();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailRecipeActivity.super.onBackPressed();
            }
        });

        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(DetailRecipeActivity.this, v);

                // This activity implements OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.item_delete) {
                            deleteRecipe();
                            return true;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.recipe_details_menu);
                popup.show();
            }
        });
        
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("recipeObj", recipe);
                Intent i = new Intent(DetailRecipeActivity.this, EditRecipeActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    private void showUserInfo() {
        imgAvatar.setVisibility(View.VISIBLE);
        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String memberName, email;
                memberName = snapshot.child("memberName").getValue().toString();
                email = snapshot.child("email").getValue().toString();

                if (memberName != null) {
                    tvUsername.setVisibility(View.VISIBLE);
                    tvUsername.setText(memberName);
                }
                if (email != null) {
                    tvGmail.setVisibility(View.VISIBLE);
                    tvGmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailRecipeActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_summary = findViewById(R.id.textView_meal_summary);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);
        recycler_meal_instructions_member = findViewById(R.id.recycler_meal_instructions_member);
        imageViewBack = findViewById(R.id.img_back);
        imgRemove = findViewById(R.id.img_remove);
        id = getIntent().getStringExtra("id");
        myRef = database.getReference("Recipes").child(id);
        cvMemberInstruction = findViewById(R.id.cv_memberInstructions);
        imgAvatar = findViewById(R.id.img_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvGmail = findViewById(R.id.tv_gmail);
        linearUser = findViewById(R.id.ll_user);
        tvTime = findViewById(R.id.tv_time);
        btnEdit = findViewById(R.id.btn_edit);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();
    }

    private void showRecipeDetail(DataSnapshot snapshot) {
        textView_meal_name.setText(snapshot.child("title").getValue().toString());
        String time = snapshot.child("readyInMinutes").getValue().toString().trim();
        if (time.isEmpty()) tvTime.setVisibility(View.GONE);
        tvTime.setText("[" + time +"]");
        Spanned formattedSummary = Html.fromHtml(snapshot.child("summary").getValue().toString());
        textView_meal_summary.setText(formattedSummary);
        Picasso.get().load(snapshot.child("image").getValue().toString()).into(imageView_meal_image);

        List<Object> ingredients = (List<Object>)
                snapshot.child("extendedIngredients").getValue();
        ArrayList<ExtendedIngredient> ingredientList = new ArrayList<>();
        for (Object ingredientObj :
                ingredients) {
            Map<String, Object> ingredientMap = (Map<String, Object>) ingredientObj;
            ExtendedIngredient ingredient = new ExtendedIngredient(Math.toIntExact((Long) ingredientMap.get("id")),
                    (String) ingredientMap.get("image"),
                    (String) ingredientMap.get("name"),
                    (String) ingredientMap.get("unit"),
                    (Long) ingredientMap.get("amount"),
                    null);
            ingredientList.add(ingredient);
        }

        recycler_meal_ingredients.setHasFixedSize(true);
        recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(DetailRecipeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientsAdapter(DetailRecipeActivity.this, ingredientList);
        recycler_meal_ingredients.setAdapter(ingredientsAdapter);

        ArrayList<Step> stepList = new ArrayList<>();
        List<Object> steps = (List<Object>)
                snapshot.child("steps").getValue();
        for (Object stepObj :
                steps) {
            Map<String, Object> stepMap = (Map<String, Object>) stepObj;
            Step step = new Step(Integer.parseInt(stepMap.get("number").toString()),
                    String.valueOf(stepMap.get("step")),
                    null,
                    null);
            stepList.add(step);
        }

        recipe = new Recipe(snapshot.child("id").getValue().toString(),
                "",
                ingredientList,
                textView_meal_name.getText().toString(),
                snapshot.child("readyInMinutes").getValue().toString(),
                snapshot.child("servings").getValue().toString(),
                "",
                snapshot.child("image").getValue().toString(),
                textView_meal_summary.getText().toString(),
                stepList,
                snapshot.child("memberId").getValue().toString(),
                true);

        cvMemberInstruction.setVisibility(View.VISIBLE);
        recycler_meal_instructions_member.setHasFixedSize(true);
        recycler_meal_instructions_member.setLayoutManager(new LinearLayoutManager(DetailRecipeActivity.this, LinearLayoutManager.VERTICAL, false));
        memberInstructionStepAdapter = new MemberInstructionStepAdapter(DetailRecipeActivity.this, stepList);
        recycler_meal_instructions_member.setAdapter(memberInstructionStepAdapter);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textView_meal_name.setText(response.title);

            if (response.sourceName == null && response.sourceUrl == null) {
                linearUser.setVisibility(View.GONE);
            }
            if (response.sourceName != null) {
                tvUsername.setVisibility(View.VISIBLE);
                tvUsername.setText("Credit: " + response.sourceName);
            }
            if (response.sourceUrl != null) {
                tvGmail.setVisibility(View.VISIBLE);
                tvGmail.setText(response.sourceUrl);
            }


            Spanned formattedSummary = Html.fromHtml(response.summary);
            textView_meal_summary.setText(formattedSummary);
            Picasso.get().load(response.image).into(imageView_meal_image);

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(DetailRecipeActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(DetailRecipeActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(DetailRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final InstructionListener instructionListener = new InstructionListener() {
        @Override
        public void didFetch(List<InstructionResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(DetailRecipeActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionAdapter = new InstructionAdapter(DetailRecipeActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(DetailRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
}