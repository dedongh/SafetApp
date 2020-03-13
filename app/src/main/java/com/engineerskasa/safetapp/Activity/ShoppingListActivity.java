package com.engineerskasa.safetapp.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.engineerskasa.safetapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShoppingListActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items, shopping_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
    }
}
