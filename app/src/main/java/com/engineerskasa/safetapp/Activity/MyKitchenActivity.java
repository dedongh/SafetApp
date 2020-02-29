package com.engineerskasa.safetapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.engineerskasa.safetapp.Adapter.CategoryAdapter;
import com.engineerskasa.safetapp.Adapter.PantryAdapter;
import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.Model.PantryListObject;
import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Constants;
import com.engineerskasa.safetapp.Utility.GridSpacingItemDecoration;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MyKitchenActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items;

    private FirebaseAuth auth;
    private FirebaseUser user;

    PantryListObject selectedPantryItem;
    String selectedKey;

    private GradientDrawable mInitialsBackground;

    private FabSpeedDial fabSpeedDial;

    private RecyclerView recyclerView;

    FirebaseRecyclerOptions<PantryListObject> options;
    FirebaseRecyclerAdapter<PantryListObject, PantryAdapter> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_kitchen);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.DATABASE_REFERENCE);
        user_items = databaseReference.child(Constants.PANTRY);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_close_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Kitchen");

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabSpeed);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_new_item)
                    startActivity(new Intent(MyKitchenActivity.this, AddNewItemActivity.class));
                if (menuItem.getItemId() == R.id.action_new_cat)
                    startActivity(new Intent(MyKitchenActivity.this, AddCategoryActivity.class));
                if (menuItem.getItemId() == R.id.action_shopping_list)
                    startActivity(new Intent(MyKitchenActivity.this, ShoppingListActivity.class));
                if (menuItem.getItemId() == R.id.action_shops)
                    startActivity(new Intent(MyKitchenActivity.this, AvailableShopsActivity.class));
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.pantry_list);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));

        int spanCount = 3; // 3 columns
        int spacing = 15; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));        displayMyPantryItems();
    }

    private void displayMyPantryItems() {
        options =
                new FirebaseRecyclerOptions.Builder<PantryListObject>()
                .setQuery(user_items.child(user.getUid()), PantryListObject.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<PantryListObject, PantryAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PantryAdapter holder, int position, @NonNull PantryListObject model) {
                int colors[] = {
                        ContextCompat.getColor(MyKitchenActivity.this, android.R.color.holo_purple),
                        ContextCompat.getColor(MyKitchenActivity.this, R.color.light_blue_800),
                        ContextCompat.getColor(MyKitchenActivity.this, R.color.colorAccent),
                        ContextCompat.getColor(MyKitchenActivity.this, R.color.colorPrimary),
                        ContextCompat.getColor(MyKitchenActivity.this, R.color.colorPrimaryDark),
                        ContextCompat.getColor(MyKitchenActivity.this, android.R.color.holo_orange_light),
                        ContextCompat.getColor(MyKitchenActivity.this, android.R.color.holo_blue_dark),
                        ContextCompat.getColor(MyKitchenActivity.this, android.R.color.holo_red_light),
                        ContextCompat.getColor(MyKitchenActivity.this, android.R.color.holo_green_light)
                };
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
                        selectedKey = getSnapshots().getSnapshot(position).getKey();
                        final Dialog dialog = new Dialog(MyKitchenActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                        dialog.setContentView(R.layout.quick_edit_dialog);
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        ((TextView) dialog.findViewById(R.id.txt_item_name)).setText(model.getItemName());
                        ((TextView) dialog.findViewById(R.id.txt_notes)).setText(model.getCategory());
                        ((TextView) dialog.findViewById(R.id.txt_exp_date)).setText(model.getExpiration_date());
                        ((TextView) dialog.findViewById(R.id.spn_units)).setText(model.getUnit());
                        TextView quantity = (TextView) dialog.findViewById(R.id.txt_qty_left);

                        quantity.setText(model.getQuantity());

                        EditText eat_how_many = (EditText) dialog.findViewById(R.id.edt_qty_eat);

                        ((ImageButton)dialog.findViewById(R.id.btn_update_item)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String eat_now = eat_how_many.getText().toString();
                                String qty_left = model.getQuantity();

                                if (TextUtils.isEmpty(eat_now)) {
                                    eat_how_many.setError("Enter quantity you eating today");
                                    eat_how_many.requestFocus();
                                    return;
                                }
                                if (eat_now.equals("0")) {
                                    eat_how_many.setError("Enter a value greater than zero");
                                    eat_how_many.requestFocus();
                                    return;
                                }
                                String updatedQuantity = String.valueOf(Integer.parseInt(qty_left) - Integer.parseInt(eat_now));

                                if (Integer.parseInt(eat_now) > Integer.parseInt(qty_left)) {
                                    eat_how_many.setError("You can't eat more than you have");
                                    eat_how_many.requestFocus();
                                    return;
                                }



                                user_items.child(user.getUid()).child(selectedKey)
                                        .child("quantity").setValue(updatedQuantity)
                                        .addOnSuccessListener(aVoid -> {
                                            quantity.setText(updatedQuantity);
                                            Toast.makeText(MyKitchenActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(e -> Toast.makeText(MyKitchenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

                            }
                        });

                        ((ImageButton)dialog.findViewById(R.id.btn_edit)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MyKitchenActivity.this, AddNewItemActivity.class);
                                intent.putExtra(Constants.EDIT_ITEM_KEY, selectedKey);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        holder.delete_layout.setVisibility(View.VISIBLE);
                        holder.btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.delete_layout.setVisibility(View.GONE);
                            }
                        });

                        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                user_items.child(user.getUid()).child(selectedKey)
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MyKitchenActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MyKitchenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
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
