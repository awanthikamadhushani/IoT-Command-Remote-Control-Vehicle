package com.example.car_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longdo.mjpegviewer.MjpegView;

import de.nitri.gauge.Gauge;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickActivity extends AppCompatActivity {

    MjpegView viewer;
    String lastCommand = "";
    boolean light_status;
    Gauge gauge;
    Boolean ligthOn = false;
    String ipaddress;

    DatabaseReference drf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        gauge = findViewById(R.id.gauge);
        drf = FirebaseDatabase.getInstance().getReference();

        drf.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        float speed = Float.parseFloat((String.valueOf(snapshot.child("map/speed").getValue())));
                        gauge.moveToValue(speed);

                    } else {
                        Toast.makeText(getBaseContext(), "NULL", Toast.LENGTH_SHORT).show();
                    }
                }catch (NumberFormatException ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        JoystickView joystick = findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                moveCar(joystick.getNormalizedX(), joystick.getNormalizedY());
            }
        });

        Switch light = findViewById(R.id.j_light);
        light.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference drf = database.getReference("Switch");
            if(isChecked){
                drf.setValue("1");
            }else {
                drf.setValue("0");
            }
        });

        ImageButton home = findViewById(R.id.j_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMain();
            }
        });

        ImageButton btnmap = findViewById(R.id.j_map);
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMap();
            }
        });

        ImageButton settings = findViewById(R.id.setting);
        settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    final Dialog dialog = new Dialog(JoystickActivity.this);
                    dialog.setContentView(R.layout.activity_ip);

                    EditText ip = dialog.findViewById(R.id.editip);
                    Button submit = dialog.findViewById(R.id.btnSubmit);

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!ip.getText().toString().equals("")) {
                                ipaddress = new String(String.valueOf(ip.getText()));
                                Toast.makeText(JoystickActivity.this, "IP address : "+ipaddress, Toast.LENGTH_SHORT).show();
                                showCam();
                            } else {
                                Toast.makeText(JoystickActivity.this, "Please enter IP to get camera view", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }catch (Exception e){
                    //exception
                }
            }
        });
    }

    private void switchToMain() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
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

                    String uri = "geo:0,0?q=" + Latitude + "," + Longitude;
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

    private void showCam() {
        String mjpegSource = "http://" + ipaddress +":81/stream";
        Toast.makeText(JoystickActivity.this, "IP address set: "+ipaddress, Toast.LENGTH_SHORT).show();
        Log.i("Execute command", mjpegSource);
        viewer = findViewById(R.id.mjpegview);
        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
        viewer.setAdjustHeight(true);
        viewer.setSupportPinchZoomAndPan(true);
        viewer.setUrl(mjpegSource);
        viewer.startStream();
    }

    private void moveCar(Integer x, Integer y) {
        String command = "";

        // Determine movement based on the index of movement of axes
        if (x > 75) {
            command = "right";
        } else if (x < 25) {
            command = "left";
        } else if (y > 75) {
            command = "backward";
        } else if (y < 25) {
            command = "forward";
        } else {
            command = "stop";
        }

        // Execute action only when the command changes
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference drf = database.getReference("car");
        if (!command.equals(lastCommand)) {
            drf.setValue(command);
            Log.i("Execute command", command);
        }

        // Set the last command
        lastCommand = command;
    }
}