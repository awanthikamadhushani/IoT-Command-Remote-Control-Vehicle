package com.example.car_application;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Temperature extends AppCompatActivity {

    private ProgressBar progressbar_t;
    private ProgressBar progressbar_h;
    DatabaseReference drf;
    TextView TempValue;
    TextView HumValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        progressbar_t = findViewById(R.id.progressBar_t);
        progressbar_h = findViewById(R.id.progressBar_h);
        TempValue = findViewById(R.id.TempTextValue);
        HumValue = findViewById(R.id.HumTextValue);
        drf = FirebaseDatabase.getInstance().getReference();

        drf.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        String statustem = (String) snapshot.child("sensor1/Temperature").getValue(String.class);
                        TempValue.setText(statustem + "°C");
                        progressbar_t.setProgress((int) Float.parseFloat(statustem));

                        String statushum = (String) snapshot.child("sensor1/Humidity").getValue(String.class);
                        progressbar_h.setProgress((int) Float.parseFloat(statushum));
                        HumValue.setText(statushum+"%");

                    } else {
                        Toast.makeText(getBaseContext(), "NULL", Toast.LENGTH_SHORT).show();
                    }
                }catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button btnmain = findViewById(R.id.btn_main_t);
        btnmain.setOnClickListener(v -> switchToMain());
    }
    private void switchToMain() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}