package com.engineerskasa.safetapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.engineerskasa.safetapp.Model.CaterygoryObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    EditText edt_category;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_shopping_basket);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Category");

        edt_category = (EditText)findViewById(R.id.category);

        btn_save = (Button) findViewById(R.id.btn_add_cat);

        recyclerView = (RecyclerView) findViewById(R.id.category_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCategory();
            }
        });
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
                .setValue(caterygoryObject);
    }
}
