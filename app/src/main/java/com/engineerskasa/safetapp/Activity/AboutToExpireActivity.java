package com.engineerskasa.safetapp.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.engineerskasa.safetapp.Adapter.PantryAdapter;
import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.Model.PantryListObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.engineerskasa.safetapp.Utility.GridSpacingItemDecoration;
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

import java.util.ArrayList;
import java.util.Random;

public class AboutToExpireActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items;

    private FirebaseAuth auth;
    private FirebaseUser user;

    PantryListObject selectedPantryItem;
    String selectedKey;

    private GradientDrawable mInitialsBackground;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;
    private TextView no_pantry_text;

    private ArrayList<String> userListArray = new ArrayList<>();
    private ArrayAdapter<String> userListAdapter;

    FirebaseRecyclerOptions<PantryListObject> options;
    FirebaseRecyclerAdapter<PantryListObject, PantryAdapter> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_to_expire);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);
        user_items = databaseReference.child(Constants.PANTRY);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About to expire");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        no_pantry_text = (TextView) findViewById(R.id.no_item_text);

        recyclerView = (RecyclerView) findViewById(R.id.expiry_list);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));

        int spanCount = 3; // 3 columns
        int spacing = 15; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        display_expired_items();


    }

    private void display_expired_items() {
        Query query = user_items.child(user.getUid())
                .orderByChild("days_left_to_expire")
                //.startAt(7)
                .endAt(7);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("GHJF", "onDataChange: "+ dataSnapshot.getChildrenCount() );
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.e("GHJF", "onDataChange: "+ dataSnapshot.getChildrenCount() );
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

        adapter = new FirebaseRecyclerAdapter<PantryListObject, PantryAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PantryAdapter holder, int position, @NonNull PantryListObject model) {
                int colors[] = {
                        ContextCompat.getColor(AboutToExpireActivity.this, android.R.color.holo_purple),
                        ContextCompat.getColor(AboutToExpireActivity.this, R.color.light_blue_800),
                        ContextCompat.getColor(AboutToExpireActivity.this, R.color.colorAccent),
                        ContextCompat.getColor(AboutToExpireActivity.this, R.color.colorPrimary),
                        ContextCompat.getColor(AboutToExpireActivity.this, R.color.colorPrimaryDark),
                        ContextCompat.getColor(AboutToExpireActivity.this, android.R.color.holo_orange_light),
                        ContextCompat.getColor(AboutToExpireActivity.this, android.R.color.holo_blue_dark),
                        ContextCompat.getColor(AboutToExpireActivity.this, android.R.color.holo_red_light),
                        ContextCompat.getColor(AboutToExpireActivity.this, android.R.color.holo_green_light)
                };

                float percentage = (Float.parseFloat(model.getQuantity_threshold()) / Float.parseFloat(model.getQuantity())) * 100;
                holder.txtReadingPercentage.setText((int)percentage + "%");

                if (percentage > 90)
                    holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

                holder.progressBar.setProgress((int)percentage);
                holder.txtItemName.setText(model.getItemName());
                holder.txtCategory.setText(model.getCategory());

                if (getSnapshots().getSnapshot(position).hasChild("item_image") && !model.getItem_image().isEmpty())
                    Glide.with(getApplicationContext()).load(model.getItem_image())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageCover);
                else
                    holder.initialsTextView.setVisibility(View.VISIBLE);

                mInitialsBackground = (GradientDrawable)holder.initialsTextView.getBackground();
                holder.initialsTextView.setText(holder.txtItemName.getText().toString().toUpperCase().substring(0,1));
                mInitialsBackground.setColor(colors[new Random().nextInt(colors.length)]);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(AboutToExpireActivity.this, +model.getDays_left_to_expire()+" Days left to expire", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                });
            }

            @NonNull
            @Override
            public PantryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.pantry_item_layout, parent, false);

                return new PantryAdapter(view);
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
