package com.example.myapplica;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button button_get_started = findViewById(R.id.button_get_started);

        button_get_started.setOnClickListener(v -> {Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);

        });
    }
}