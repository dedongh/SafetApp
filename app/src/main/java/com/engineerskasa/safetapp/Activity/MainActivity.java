package com.engineerskasa.safetapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Tools;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;

    private FabSpeedDial fabSpeedDial;

    private LinearLayout open_my_kitchen, open_my_shopping_list, open_available_shops, open_recipe_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.grocery_icon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shelf Stacker");

        auth = FirebaseAuth.getInstance();

        open_my_kitchen = (LinearLayout) findViewById(R.id.open_my_kitchen);
        open_my_shopping_list = (LinearLayout) findViewById(R.id.open_shopping_list);
        open_available_shops = (LinearLayout) findViewById(R.id.open_available_shops);
        open_recipe_page = (LinearLayout) findViewById(R.id.open_recipes);

        open_my_kitchen.setOnClickListener(this);
        open_my_shopping_list.setOnClickListener(this);
        open_available_shops.setOnClickListener(this);
        open_recipe_page.setOnClickListener(this);

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabSpeed);

        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_new_item)
                    startActivity(new Intent(MainActivity.this, AddNewItemActivity.class));
                if (menuItem.getItemId() == R.id.action_new_cat)
                    startActivity(new Intent(MainActivity.this, AddCategoryActivity.class));
                if (menuItem.getItemId() == R.id.action_shopping_list)
                    startActivity(new Intent(MainActivity.this, ShoppingListActivity.class));
                if (menuItem.getItemId() == R.id.action_shops)
                    startActivity(new Intent(MainActivity.this, AvailableShopsActivity.class));
                if (menuItem.getItemId() == R.id.action_expire_list)
                    startActivity(new Intent(MainActivity.this, AboutToExpireActivity.class));
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        //Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == open_my_kitchen) {
            startActivity(new Intent(MainActivity.this, MyKitchenActivity.class));
        }
        if (v == open_my_shopping_list) {
            startActivity(new Intent(MainActivity.this, ShoppingListActivity.class));
        }
        if (v == open_available_shops) {
            startActivity(new Intent(MainActivity.this, AvailableShopsActivity.class));
        }
        if (v == open_recipe_page) {
            startActivity(new Intent(MainActivity.this, RecipeActivity.class));
        }
    }
}
