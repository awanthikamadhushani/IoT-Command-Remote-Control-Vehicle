package com.example.car_application;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Temperature extends AppCompatActivity {

    private ProgressBar progressbar_t;
    DatabaseReference drf;
    TextView value;
    String temString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        progressbar_t = findViewById(R.id.progressBar_t);
        value = findViewById(R.id.text_Pro_t);
        drf = FirebaseDatabase.getInstance().getReference();

        drf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    double status = (Double) snapshot.child("sensor/Temperature").getValue();
                    value.setText(status+"°C");

                    String newValue = Integer.toString((int)status);
                    progressbar_t.setProgress(Integer.parseInt(newValue));

                } else {
                    Toast.makeText(getBaseContext(), "NULLL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button btnmain = findViewById(R.id.btn_main_t);
        btnmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMain();
            }
        });
    }
    private void switchToMain() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}