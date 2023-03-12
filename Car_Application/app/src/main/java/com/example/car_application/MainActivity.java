package com.example.car_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    DatabaseReference drf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drf = FirebaseDatabase.getInstance().getReference();


        TextView btnLogout = findViewById(R.id.mlogout);
        btnLogout.setOnClickListener(v -> switchToLogin());

        Button btnSen = findViewById(R.id.btnSensor);
        btnSen.setOnClickListener(v -> switchToSensor());

        Button btnCont = findViewById(R.id.btnCarCon);
        btnCont.setOnClickListener(v -> switchToCarCont());

        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(v -> switchToMap());

    }

    private void switchToSensor() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Temperature.class);
        startActivity(intent);
        finish();
    }
    private void switchToCarCont() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, JoystickActivity.class);
        startActivity(intent);
        finish();
    }
    private void switchToMap() {
        drf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    double Latitude = (Double) snapshot.child("map/lat").getValue();
                    double Longitude = (Double) snapshot.child("map/lon").getValue();

                    String uri = "http://maps.google.com/maps?q=" + Latitude + "," + Longitude;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }else {
                    Toast.makeText(getBaseContext(),"NULL",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void switchToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}