package com.engineerskasa.safetapp.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engineerskasa.safetapp.Adapter.ShoppingListAdapter;
import com.engineerskasa.safetapp.Model.PantryListObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShoppingListActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items, shopping_list;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;
    private TextView no_pantry_text;

    FirebaseRecyclerOptions<PantryListObject> options;
    FirebaseRecyclerAdapter<PantryListObject, ShoppingListAdapter> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        //setContentView(R.layout.shopping_list_main_layout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);
        user_items = databaseReference.child(Constants.PANTRY);
        shopping_list = databaseReference.child(Constants.SHOPPING_LIST);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Shopping List");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        no_pantry_text = (TextView) findViewById(R.id.no_item_text);

        recyclerView = (RecyclerView) findViewById(R.id.shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        show_shopping_list();
    }

    private void show_shopping_list() {
        Query query = shopping_list.child(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    no_pantry_text.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options =
                new FirebaseRecyclerOptions.Builder<PantryListObject>()
                        .setQuery(query, PantryListObject.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<PantryListObject, ShoppingListAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShoppingListAdapter holder, int position, @NonNull PantryListObject model) {

            }

            @NonNull
            @Override
            public ShoppingListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.shopping_list_main_layout, parent, false);
                return new ShoppingListAdapter(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
