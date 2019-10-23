package com.engineerskasa.safetapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Tools;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Tools.setSystemBarColor(this, R.color.grey_20);

    }

    public void verifyPhone(View view) {
        startActivity(new Intent(LoginActivity.this, VerifyPhoneActivity.class));
    }
}
