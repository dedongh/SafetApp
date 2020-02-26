package com.engineerskasa.safetapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Tools;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private FabSpeedDial fabSpeedDial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_add_shopping_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shelf Stacker");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);*/

        auth = FirebaseAuth.getInstance();

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


}
