package com.skander.uberclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    private Button phoneButton,googleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        phoneButton=findViewById(R.id.phonebtn);
        googleButton=findViewById(R.id.googlebtn);
    }

    public void phoneLoginclick(View view) {
        Intent intent = new Intent(LoginActivity.this , PhoneLoginActivity.class);
        startActivity(intent);
    }
}