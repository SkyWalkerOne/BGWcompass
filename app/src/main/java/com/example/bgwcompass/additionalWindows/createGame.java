package com.example.bgwcompass.additionalWindows;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bgwcompass.R;
import com.example.bgwcompass.dataClasses.opengame;
import com.example.bgwcompass.mainWindows.MainActivity;
import com.example.bgwcompass.mainWindows.games_activity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;

public class createGame extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMap;
    private EditText place;
    String curDate = "Не указано";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        CalendarView time = findViewById(R.id.timeGame);
        time.setMinDate(new Date().getTime());

        mMap = findViewById(R.id.mapView);
        mMap.getMapAsync(this);
        mMap.onCreate(savedInstanceState);

        place = findViewById(R.id.placeGame);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        time.setOnDateChangeListener((view, year, month, dayOfMonth) -> curDate = dayOfMonth + "." + month + "." + year);
    }

    public void goBack(View view) {
        finish();
        startActivity(new Intent(createGame.this, games_activity.class));
    }

    public void creategame(View view) {
        EditText name = findViewById(R.id.nameGame);
        EditText desc = findViewById(R.id.descGame);
        TextView hint = findViewById(R.id.hint2);

        if (desc.getText().toString().trim().isEmpty() ||
                place.getText().toString().trim().isEmpty() ||
                name.getText().toString().trim().isEmpty()) {
            hint.setAlpha(0.6f);
            return;
        }

        opengame og = new opengame(MainActivity.user.getDisplayName(), MainActivity.user.getUid(), name.getText().toString().trim(), curDate, desc.getText().toString().trim(), place.getText().toString().trim());
        og.addMember(MainActivity.user.getUid());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("opengames").push().setValue(og);

        goBack(null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnMapClickListener(point -> {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Your game");
            googleMap.clear();
            googleMap.addMarker(marker);
            double lat = (double)Math.round(marker.getPosition().latitude * 100000d) / 100000d;
            double lon = (double)Math.round(marker.getPosition().longitude * 100000d) / 100000d;
            place.setText(lat + ", " + lon);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMap.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMap.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }
}