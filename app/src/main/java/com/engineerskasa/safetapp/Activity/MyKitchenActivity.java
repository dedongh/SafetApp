package com.engineerskasa.safetapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MyKitchenActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, user_items, shopping_list;

    private FirebaseAuth auth;
    private FirebaseUser user;

    PantryListObject selectedPantryItem;
    String selectedKey;

    private GradientDrawable mInitialsBackground;

    private FabSpeedDial fabSpeedDial;

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
        setContentView(R.layout.activity_my_kitchen);

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
        getSupportActionBar().setTitle("My Kitchen");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        no_pantry_text = (TextView) findViewById(R.id.no_item_text);

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
                if (menuItem.getItemId() == R.id.action_autofill_list){
                    move_item_to_list();
                }
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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        displayMyPantryItems();
    }

    private void move_item_to_list() {
        user_items.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot shopper : dataSnapshot.getChildren()) {
                        String quantity = shopper.child("quantity").getValue(String.class);
                        String threshold = shopper.child("quantity_threshold").getValue(String.class);
                        if (quantity.equals(threshold) || Integer.parseInt(quantity) < Integer.parseInt(threshold)) {
                            Log.e("DFGS", "Name: " + shopper.child("itemName").getValue(String.class));

                            Date now = new Date();
                            int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));

                            PantryListObject listObject = new PantryListObject();

                            listObject.setUnit_price(shopper.child("unit_price").getValue(String.class));
                            listObject.setItemName(shopper.child("itemName").getValue(String.class));
                            listObject.setQuantity(shopper.child("quantity").getValue(String.class));
                            listObject.setUnit_price(shopper.child("unit_price").getValue(String.class));
                            shopping_list.child(user.getUid())
                                    .child("auto_generated"+id)
                                    .push().setValue(listObject);
                        }
                        //Log.e("DFGS", "onDataChange: " + quantity + " threshold: " + threshold);
                    }
                } else {
                    Toast.makeText(MyKitchenActivity.this, "You have nothing in your pantry about to finish", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMyPantryItems() {

        Query query = user_items.child(user.getUid());

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

                float percentage = (Float.parseFloat(model.getQuantity_threshold()) / Float.parseFloat(model.getQuantity())) * 100;
                holder.txtReadingPercentage.setText((int)percentage + "%");

                if (percentage > 90)
                    //holder.progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
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
                        ((ImageButton)dialog.findViewById(R.id.btn_add_cat)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog add_to_list_dialog = new Dialog(MyKitchenActivity.this);
                                add_to_list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                add_to_list_dialog.setContentView(R.layout.add_to_list);
                                add_to_list_dialog.setCancelable(true);
                                add_to_list_dialog.setCanceledOnTouchOutside(true);

                                WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
                                lps.copyFrom(add_to_list_dialog.getWindow().getAttributes());
                                lps.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lps.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                ((TextView) add_to_list_dialog.findViewById(R.id.item_units)).setText(model.getUnit());
                                ((TextView) add_to_list_dialog.findViewById(R.id.txt_item_name_add)).setText(model.getItemName());

                                Spinner my_list_spinner = (Spinner)add_to_list_dialog.findViewById(R.id.my_list);
                                EditText shopping_list_name = (EditText)add_to_list_dialog.findViewById(R.id.shop_list_ref);
                                EditText qty_to_buy = (EditText)add_to_list_dialog.findViewById(R.id.edt_qty_cart);

                               shopping_list.child(user.getUid())
                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                               if (dataSnapshot.exists()) {
                                                   for (DataSnapshot myListItems : dataSnapshot.getChildren()){
                                                       Log.e("GIS", "onDataChange: "+ myListItems.getKey() );
                                                       String listItems = myListItems.getKey();
                                                       userListArray.add(listItems);
                                                   }
                                                   Log.e("GIS", "List Size: "+ userListArray.size() );
                                                   userListAdapter = new ArrayAdapter<String>(
                                                           getApplicationContext(), R.layout.network_spinner_layout, R.id.network_spn, userListArray);

                                                   my_list_spinner.setAdapter(userListAdapter);
                                               }


                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });

                               my_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                   @Override
                                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                       shopping_list_name.setText(userListArray.get(position));
                                   }

                                   @Override
                                   public void onNothingSelected(AdapterView<?> parent) {

                                   }
                               });


                                ((ImageButton)add_to_list_dialog.findViewById(R.id.btn_cancel_cart)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        add_to_list_dialog.dismiss();
                                    }
                                });

                                ((ImageButton)add_to_list_dialog.findViewById(R.id.btn_add_cart)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String qty_to_add = qty_to_buy.getText().toString();
                                        String shop_ref = shopping_list_name.getText().toString();
                                        shop_ref = shop_ref.replaceAll("\\s+", "_").toLowerCase();
                                        if (TextUtils.isEmpty(qty_to_add)) {
                                            qty_to_buy.setError("This field is required");
                                            qty_to_buy.requestFocus();
                                            return;
                                        }
                                        if (qty_to_add.equals("0")) {
                                            qty_to_buy.setError("Enter a value greater than zero");
                                            qty_to_buy.requestFocus();
                                            return;
                                        }
                                        if (TextUtils.isEmpty(shop_ref)) {
                                            shopping_list_name.setError("This field is required");
                                            shopping_list_name.requestFocus();
                                            return;
                                        }

                                        PantryListObject listObject = new PantryListObject();

                                        listObject.setUnit(model.getUnit());
                                        listObject.setItemName(model.getItemName());
                                        listObject.setQuantity(qty_to_add);
                                        listObject.setUnit_price(model.getUnit_price());

                                        shopping_list.child(user.getUid())
                                                .child(shop_ref)
                                                .push()
                                                .setValue(listObject)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MyKitchenActivity.this, "Added to shopping list ", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MyKitchenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        //Toast.makeText(MyKitchenActivity.this, "Clicked: "+ selectedKey, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                add_to_list_dialog.show();
                                add_to_list_dialog.getWindow().setAttributes(lps);

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
