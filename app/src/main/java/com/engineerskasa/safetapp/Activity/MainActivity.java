package com.engineerskasa.safetapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Tools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SafetApp");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*if (item.getItemId() == R.id.action_settings) {
        }*/
        Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


}
