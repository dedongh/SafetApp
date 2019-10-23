package com.engineerskasa.safetapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.engineerskasa.safetapp.R;
import com.engineerskasa.safetapp.Utility.Tools;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText txtPhone;
    private AppCompatButton btnVerify;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Tools.setSystemBarColor(this, R.color.grey_20);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        txtPhone = (TextInputEditText) findViewById(R.id.edt_phone);
        btnVerify = (AppCompatButton) findViewById(R.id.btnContinue);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = txtPhone.getText().toString().trim();
                if (mobile.isEmpty()  || mobile.length() < 10) {
                    txtPhone.setError("Enter a valid phone number");
                    txtPhone.requestFocus();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });

    }

}
