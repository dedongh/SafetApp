package com.engineerskasa.safetapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engineerskasa.safetapp.Adapter.ShopperRecyclerAdapter;
import com.engineerskasa.safetapp.Adapter.ShoppingListAdapter;
import com.engineerskasa.safetapp.Model.PantryListObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShoppingListActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, user_items, shopping_list;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;
    private TextView no_pantry_text;
    private String selectedKey;

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

                holder.shop_list_name.setTitle(getSnapshots().getSnapshot(position).getKey());

                holder.my_shop_recycler.setLayoutManager(new LinearLayoutManager(ShoppingListActivity.this));


               selectedKey = getSnapshots().getSnapshot(position).getKey();
               holder.item_layout.setTag(selectedKey);

                FirebaseRecyclerOptions<PantryListObject> pantryOptions = new FirebaseRecyclerOptions.Builder<PantryListObject>()
                        .setQuery(shopping_list.child(user.getUid())
                                        .child(selectedKey),
                                PantryListObject.class)
                        .build();

                FirebaseRecyclerAdapter<PantryListObject, ShopperRecyclerAdapter> shopperAdapter
                        = new FirebaseRecyclerAdapter<PantryListObject, ShopperRecyclerAdapter>(pantryOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ShopperRecyclerAdapter pantryHolder, int pantryPosition, @NonNull PantryListObject pantryModel) {

                        String update_selected = getSnapshots().getSnapshot(pantryPosition).getKey();
                        pantryHolder.txtItemName.setText(pantryModel.getItemName());
                        pantryHolder.edt_quantity.setText(pantryModel.getQuantity());
                        pantryHolder.edt_price.setText(pantryModel.getUnit_price());
                        pantryHolder.txtUnits.setText(pantryModel.getUnit());

                        pantryHolder.save_to_cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PantryListObject pantryListObject = new PantryListObject();
                                pantryListObject.setItemName(pantryHolder.txtItemName.getText().toString());
                                pantryListObject.setUnit(pantryModel.getUnit());
                                pantryListObject.setUnit_price(pantryHolder.edt_price.getText().toString());
                                pantryListObject.setQuantity(pantryHolder.edt_quantity.getText().toString());

                                Log.e("mnjk", "SelectedKey: "+ selectedKey+
                                        " childKey: "+ update_selected + " jk "+ holder.item_layout.getTag());
                                shopping_list.child(user.getUid())
                                        .child((String) holder.item_layout.getTag())
                                        .child(update_selected)
                                        .setValue(pantryListObject)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ShoppingListActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ShoppingListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //Toast.makeText(ShoppingListActivity.this, ""+ update_selected, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ShopperRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.shopping_list_item_layout, parent, false);
                        return new ShopperRecyclerAdapter(view);
                    }
                };
                shopperAdapter.startListening();
                holder.my_shop_recycler.setAdapter(shopperAdapter);
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
