package com.engineerskasa.safetapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.engineerskasa.safetapp.Adapter.CategoryAdapter;
import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.Model.CaterygoryObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class AddCategoryActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    EditText edt_category;
    Button btn_save, btn_update, btn_delete;

    private GradientDrawable mInitialsBackground;

    CaterygoryObject selectedCategory;
    String selectedKey;

    FirebaseRecyclerOptions<CaterygoryObject> options;
    FirebaseRecyclerAdapter<CaterygoryObject, CategoryAdapter> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Category");

        edt_category = (EditText)findViewById(R.id.category);

        btn_save = (Button) findViewById(R.id.btn_add_cat);
        btn_update = (Button) findViewById(R.id.btn_update_cat);
        btn_delete = (Button) findViewById(R.id.btn_delete_cat);

        recyclerView = (RecyclerView) findViewById(R.id.category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);



        btn_save.setOnClickListener(v -> postCategory());

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(Constants.CATEGORY).child(selectedKey)
                        .setValue(new CaterygoryObject(edt_category.getText().toString()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddCategoryActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(Constants.CATEGORY).child(selectedKey)
                       .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                edt_category.getText().clear();
                                Toast.makeText(AddCategoryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        displayComment();
    }

    private void postCategory() {
        String category_name = edt_category.getText().toString();
        if (TextUtils.isEmpty(category_name)) {
            edt_category.setError("Enter Category Name");
            edt_category.requestFocus();
            return;
        }

        CaterygoryObject caterygoryObject = new CaterygoryObject();
        caterygoryObject.setCategoryTitle(category_name);

        databaseReference.child(Constants.CATEGORY).push()
                .setValue(caterygoryObject)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AddCategoryActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddCategoryActivity.this, "Couldn't save... try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void displayComment() {
         options =
                new FirebaseRecyclerOptions.Builder<CaterygoryObject>()
                .setQuery(databaseReference.child(Constants.CATEGORY), CaterygoryObject.class)
                .build();

        adapter =
                new FirebaseRecyclerAdapter<CaterygoryObject, CategoryAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryAdapter holder, int position, @NonNull CaterygoryObject model) {
                        int colors[] = {
                                ContextCompat.getColor(AddCategoryActivity.this, android.R.color.holo_purple),
                                ContextCompat.getColor(AddCategoryActivity.this, R.color.light_blue_800),
                                ContextCompat.getColor(AddCategoryActivity.this, R.color.colorAccent),
                                ContextCompat.getColor(AddCategoryActivity.this, R.color.colorPrimary),
                                ContextCompat.getColor(AddCategoryActivity.this, R.color.colorPrimaryDark),
                                ContextCompat.getColor(AddCategoryActivity.this, android.R.color.holo_orange_light),
                                ContextCompat.getColor(AddCategoryActivity.this, android.R.color.holo_blue_dark),
                                ContextCompat.getColor(AddCategoryActivity.this, android.R.color.holo_red_light),
                                ContextCompat.getColor(AddCategoryActivity.this, android.R.color.holo_green_light)
                        };
                        holder.categoryName.setText(model.getCategoryTitle());
                        mInitialsBackground = (GradientDrawable)holder.initialsTextView.getBackground();
                        holder.initialsTextView.setText(holder.categoryName.getText().toString().toUpperCase().substring(0,1));
                        mInitialsBackground.setColor(colors[new Random().nextInt(colors.length)]);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                selectedCategory = model;
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                Log.e("CATKEYS", "onClick: ItemKey: "+ selectedKey );

                                edt_category.setText(model.getCategoryTitle());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.category_item_layout, parent, false);

                        return new CategoryAdapter(view);
                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        displayComment();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
