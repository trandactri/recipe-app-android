package com.prmproject.recipeapp.Fragment;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {
    private View mView;
    private TextInputEditText titleEdt, summaryEdt, servingEdt, readyInMinutesEdt, ingredientEdt, instructionEdt;
    private ImageView imgEdit, imgRemove, imgRecipe;
    private Button saveBtn, publicBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference, fileReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Uri imageUri;
    private TextView tvChoose;
    private CardView cvAdjust;
    ActivityResultLauncher<String> mTakePhoto;
    private StorageTask mUploadTask;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_SUMMARY = "summary_key";
    private static final String KEY_SERVING = "serving_key";
    private static final String KEY_TIME = "time_key";
    List<String> ingredientNames, instructionNames;
    RecyclerView rcvIngredients, rcvInstructions;
    String recID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_add_recipe, null);

        initFirebase();
        initUI();
        showSavedInfo();
        initListener();
        return mView;
    }

    private void initFirebase() {
        // Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Recipes");
        storageReference = FirebaseStorage.getInstance().getReference();
        fileReference = storageReference.child("images/");
    }

    private void showSavedInfo() {
        pref = getActivity().getSharedPreferences(user.getUid(), 0);
        if(pref != null){
            String saveTitle = pref.getString(KEY_TITLE, "");
            titleEdt.setText(saveTitle);
            String saveSummary = pref.getString(KEY_SUMMARY, "");
            summaryEdt.setText(saveSummary);
            String saveServing = pref.getString(KEY_SERVING, "");
            servingEdt.setText(saveServing);
            String saveTime = pref.getString(KEY_TIME, "");
            readyInMinutesEdt.setText(saveTime);
//            Uri saveImg = Uri.parse(pref.getString(KEY_IMAGE, ""));
//            Picasso.get().load(saveImg).into(imgRecipe);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {

        summaryEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (summaryEdt.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        imgRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage();
            }
        });

        AddIngredientAdapter addIngredientAdapter = new AddIngredientAdapter(getActivity(), ingredientNames);
        rcvIngredients.setAdapter(addIngredientAdapter);
        mView.findViewById(R.id.btn_addIngredient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientNames.add("");
                addIngredientAdapter.notifyItemInserted(ingredientNames.size()-1);
            }
        });

        AddInstructionAdapter addInstructionAdapter = new AddInstructionAdapter(getActivity(), instructionNames);
        rcvInstructions.setAdapter(addInstructionAdapter);
        mView.findViewById(R.id.btn_addInstruction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionNames.add("");
                addInstructionAdapter.notifyItemInserted(instructionNames.size()-1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        publicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPublic();
                editor = pref.edit();
                editor.clear();
                editor.commit();
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

    private void saveData() {
        editor = pref.edit();

        editor.putString(KEY_TITLE, titleEdt.getText().toString().trim());
        editor.putString(KEY_SUMMARY, summaryEdt.getText().toString().trim());
        editor.putString(KEY_SERVING, servingEdt.getText().toString().trim());
        editor.putString(KEY_TIME, readyInMinutesEdt.getText().toString().trim());

        editor.commit();
    }

    private void onClickImage() {
        mTakePhoto.launch("image/*");
    }

    private void onClickPublic() {
        loadingPB.setVisibility(View.VISIBLE);
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
        } else {
            if (!uploadImageSuccess()) return;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    uploadRecipe();
                }
            }, 5000);
        }
    }

    private void initUI() {
        recID = "recID" + System.currentTimeMillis();
        ingredientEdt = mView.findViewById(R.id.et_ingredient);
        instructionEdt = mView.findViewById(R.id.et_instruction);
        imgRecipe = mView.findViewById(R.id.firebaseimage);
        titleEdt = mView.findViewById(R.id.et_title);
        summaryEdt = mView.findViewById(R.id.et_summary);
        servingEdt = mView.findViewById(R.id.et_serving);
        readyInMinutesEdt = mView.findViewById(R.id.et_readyInMinutes);
        saveBtn = mView.findViewById(R.id.btn_saveRecipe);
        publicBtn = mView.findViewById(R.id.btn_publicRecipe);
        loadingPB = mView.findViewById(R.id.progress_loading);
        tvChoose = mView.findViewById(R.id.textView_Choose);
        cvAdjust = mView.findViewById(R.id.cardView_adjust);
        imgEdit = mView.findViewById(R.id.img_edit);
        imgRemove = mView.findViewById(R.id.img_remove);




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
        rcvIngredients = mView.findViewById(R.id.rcv_ingredients);
        rcvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));


        instructionNames = new ArrayList<>();
        rcvInstructions = mView.findViewById(R.id.rcv_instructions);
        rcvInstructions.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private boolean uploadImageSuccess() {
        if (imageUri != null) {
            mUploadTask = fileReference.child(recID).putFile(imageUri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingPB.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
            return false;
    }

    private void uploadRecipe() {
        fileReference.child(recID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addRecipe(uri);
            }
        });
    }

    private void addRecipe(Uri uri) {
        Recipe recipe = setRecipeValue(uri);

        if (recipe == null || recipe.getImage() == null || recipe.getSteps().isEmpty() || recipe.getExtendedIngredients().isEmpty()) {
            loadingPB.setVisibility(View.GONE);
            return;
        }

        databaseReference.child(recipe.getId()).setValue(recipe);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                imgRecipe.setImageResource(R.drawable.background_image);
                Toast.makeText(getActivity(), "Recipe Added. Please wait for approval", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error is" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Recipe setRecipeValue(Uri uri) {
        int counter;
        String recipeID = recID;
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
            Toast.makeText(getActivity(), "Please add ingredient(s)", Toast.LENGTH_SHORT).show();
        }

        String title = titleEdt.getText().toString().trim();
        if (textEmpty(title, titleEdt)) return null;
        String readyInMinutes = readyInMinutesEdt.getText().toString().trim();
        String servings = servingEdt.getText().toString();
        //source
        //like
        String recipeImage = uri.toString();
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
            Toast.makeText(getActivity(), "Please add instruction(s)", Toast.LENGTH_SHORT).show();
        }

        String memberId = mAuth.getCurrentUser().getUid();

        Recipe recipe = new Recipe(recipeID, "", ingredients, title, readyInMinutes,
                servings, "", recipeImage, summary, steps,
                memberId, false);
        return recipe;
    }

    private boolean textEmpty(String label, EditText editText) {
        if (TextUtils.isEmpty(label)) {
            loadingPB.setVisibility(View.GONE);
            editText.setError("Cannot be blank");
            editText.requestFocus();
            return true;
        }
        return false;
    }
}