package com.prmproject.recipeapp.Views.Recipe;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.prmproject.recipeapp.Adapters.Recipe.AddIngredientAdapter;
import com.prmproject.recipeapp.Adapters.Recipe.AddInstructionAdapter;
import com.prmproject.recipeapp.Models.ExtendedIngredient;
import com.prmproject.recipeapp.Models.Recipe;
import com.prmproject.recipeapp.Models.Step;
import com.prmproject.recipeapp.R;
import com.prmproject.recipeapp.Views.Home.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeActivity extends AppCompatActivity {
    private TextInputEditText titleEdt, summaryEdt, servingEdt, readyInMinutesEdt, ingredientEdt, instructionEdt;
    private ImageView imgEdit, imgRemove, imgRecipe, imgBack;
    private Button saveBtn, publicBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference, fileReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Recipe recipe;
    private Uri imageUri;
    private TextView tvChoose;
    private CardView cvAdjust;
    ActivityResultLauncher<String> mTakePhoto;
    private StorageTask mUploadTask;
    private List<String> ingredientNames, instructionNames;
    private RecyclerView rcvIngredients, rcvInstructions;
    private AddInstructionAdapter addInstructionAdapter;
    private AddIngredientAdapter addIngredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_recipe);
        this.setFinishOnTouchOutside(false);

        Bundle b = getIntent().getExtras();
        recipe = (Recipe) b.getParcelable("recipeObj");
        initFirebase();
        initUI();
        setRecipeInformation(recipe);
        initListener();
    }

    private void initFirebase() {
        // Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Recipes");
        storageReference = FirebaseStorage.getInstance().getReference();
        fileReference = storageReference.child("images/" + recipe.getId());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditRecipeActivity.super.onBackPressed();
            }
        });

        summaryEdt.setOnTouchListener((v, event) -> {
            if (summaryEdt.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        imgRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage();
            }
        });

        findViewById(R.id.btn_addInstruction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInstructionItem();
            }
        });

        findViewById(R.id.btn_addIngredient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredientItem();
            }
        });

        publicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPublic();
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage();
            }
        });

        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                imgRecipe.setImageResource(R.drawable.background_image);
                tvChoose.setVisibility(View.VISIBLE);
                cvAdjust.setVisibility(View.GONE);
            }
        });
    }

    private void onClickPublic() {
        loadingPB.setVisibility(View.VISIBLE);
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(this, "Upload in Progress", Toast.LENGTH_SHORT).show();
        } else {
            updateRecipe();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            Uri recImg = Uri.parse(recipe.getImage());
            if (imageUri.equals(recImg)) return;
            mUploadTask = fileReference.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditRecipeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRecipe() {
        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                addRecipe(task);
            }
        });
    }

    private void addRecipe(Task<Uri> task) {
        Recipe recipe = setRecipeValue(task);
        if (recipe == null || recipe.getSteps().isEmpty() || recipe.getExtendedIngredients().isEmpty()) {
            loadingPB.setVisibility(View.GONE);
            return;
        }

        databaseReference.child(recipe.getId()).setValue(recipe);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                imgRecipe.setImageResource(R.drawable.background_image);
                Toast.makeText(EditRecipeActivity.this, "Recipe Updated.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
                EditRecipeActivity.this.finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(EditRecipeActivity.this, "Error is" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Recipe setRecipeValue(Task<Uri> task) {
        int counter;
        String recipeID = recipe.getId();
        //credit
        ArrayList<ExtendedIngredient> ingredients = new ArrayList();
        if (rcvIngredients.getChildCount() > 0){
            counter = 0;
            for(int index = 0; index < rcvIngredients.getChildCount(); index++) {
                View nextChild = rcvIngredients.getChildAt(index);
                ingredientEdt = nextChild.findViewById(R.id.et_ingredient);
                String ingredientName = ingredientEdt.getText().toString().trim();
                if (textEmpty(ingredientName, ingredientEdt)) return null;

                ExtendedIngredient extendedIngredient = new ExtendedIngredient(counter, null, ingredientName, "", 0, null);
                ingredients.add((extendedIngredient));
                counter++;
            }
        } else {
            Toast.makeText(this, "Please add ingredient(s)", Toast.LENGTH_SHORT).show();
        }

        String title = titleEdt.getText().toString().trim();
        if (textEmpty(title, titleEdt)) return null;
        String readyInMinutes = readyInMinutesEdt.getText().toString().trim();
        String servings = servingEdt.getText().toString();
        //source
        //like
        String recipeImage = task.getResult().toString();
        if (imageUri.equals(recipe.getImage())) recipeImage = null;

        String summary = summaryEdt.getText().toString();

        ArrayList<Step> steps = new ArrayList();
        if (rcvInstructions.getChildCount() > 0){
            counter = 1;
            for(int index = 0; index < rcvInstructions.getChildCount(); index++) {
                View nextChild = rcvInstructions.getChildAt(index);
                instructionEdt = nextChild.findViewById(R.id.et_instruction);
                String instruction = instructionEdt.getText().toString().trim();
                if (textEmpty(instruction, instructionEdt)) return null;

                Step step = new Step(counter, instruction, null, null);
                steps.add(step);
                counter++;
            }
        } else {
            Toast.makeText(this, "Please add instruction(s)", Toast.LENGTH_SHORT).show();
        }

        String memberId = mAuth.getCurrentUser().getUid();

        Recipe recipe = new Recipe(recipeID, "", ingredients, title, readyInMinutes,
                servings, "", recipeImage, summary, steps,
                memberId, false);
        return recipe;
    }

    private boolean textEmpty(String label, TextInputEditText editText) {
        if (TextUtils.isEmpty(label)) {
            loadingPB.setVisibility(View.GONE);
            editText.setError("Cannot be blank");
            editText.requestFocus();
            return true;
        }
        return false;
    }
    private void onClickImage() {
        mTakePhoto.launch("image/*");
    }

    private void addInstructionItem() {
        instructionNames.add("");
        addInstructionAdapter.notifyItemInserted(instructionNames.size()-1);
    }

    private void addIngredientItem() {
        ingredientNames.add("");
        addIngredientAdapter.notifyItemInserted(ingredientNames.size()-1);
    }

    private void initUI() {
        imgBack = findViewById(R.id.img_back);
        ingredientEdt = findViewById(R.id.et_ingredient);
        instructionEdt = findViewById(R.id.et_instruction);
        imgRecipe = findViewById(R.id.firebaseimage);
        titleEdt = findViewById(R.id.et_title);
        summaryEdt = findViewById(R.id.et_summary);
        servingEdt = findViewById(R.id.et_serving);
        readyInMinutesEdt = findViewById(R.id.et_readyInMinutes);
        saveBtn = findViewById(R.id.btn_saveRecipe);
        publicBtn = findViewById(R.id.btn_publicRecipe);
        loadingPB = findViewById(R.id.progress_loading);
        tvChoose = findViewById(R.id.textView_Choose);
        cvAdjust = findViewById(R.id.cardView_adjust);
        imgEdit = findViewById(R.id.img_edit);
        imgRemove = findViewById(R.id.img_remove);

        // Img Process
        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            imageUri = result;
                            tvChoose.setVisibility(View.GONE);
                            cvAdjust.setVisibility(View.VISIBLE);
                            imgRecipe.setImageURI(imageUri);
                        }
                    }
                }
        );

        ingredientNames = new ArrayList<>();
        rcvIngredients = findViewById(R.id.rcv_ingredients);
        rcvIngredients.setLayoutManager(new LinearLayoutManager(this));
        addIngredientAdapter = new AddIngredientAdapter(this, ingredientNames);
        rcvIngredients.setAdapter(addIngredientAdapter);


        instructionNames = new ArrayList<>();
        rcvInstructions = findViewById(R.id.rcv_instructions);
        rcvInstructions.setLayoutManager(new LinearLayoutManager(this));
        addInstructionAdapter = new AddInstructionAdapter(this, instructionNames);
        rcvInstructions.setAdapter(addInstructionAdapter);

    }

    private void setRecipeInformation(Recipe recipe) {
        titleEdt.setText(recipe.getTitle());
        summaryEdt.setText(recipe.getSummary());
        servingEdt.setText(recipe.getServings());
        readyInMinutesEdt.setText(recipe.getReadyInMinutes());

        Picasso.get().load(recipe.getImage()).into(imgRecipe);
        imageUri = Uri.parse(recipe.getImage());

        for (ExtendedIngredient ingredient:
             recipe.getExtendedIngredients()) {
            ingredientNames.add(ingredient.getName());
            addIngredientAdapter.notifyItemInserted(ingredientNames.size()-1);
        }

        for (Step step:
                recipe.getSteps()) {
            instructionNames.add(step.getStep());
            addInstructionAdapter.notifyItemInserted(instructionNames.size()-1);
        }
    }
}