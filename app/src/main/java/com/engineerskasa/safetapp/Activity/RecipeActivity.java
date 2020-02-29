package com.engineerskasa.safetapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeActivity extends AppCompatActivity {

    EditText edt_title, edt_category, edt_quantity;
    Button btn_post;
    RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        edt_title = (EditText)findViewById(R.id.pantry_item);
        edt_category = (EditText)findViewById(R.id.category);
        edt_quantity = (EditText)findViewById(R.id.quantity);

        btn_post = (Button) findViewById(R.id.btn_post);

        recyclerView = (RecyclerView) findViewById(R.id.pantry_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);
        
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postGrocery();
            }
        });
    }

    private void postGrocery() {

    }
}
