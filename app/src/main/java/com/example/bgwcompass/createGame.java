package com.example.bgwcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class createGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void goBack(View view) {
        finish();
        startActivity(new Intent(createGame.this, games_activity.class));
    }

    public void createGame(View view) {
        EditText name = findViewById(R.id.nameGame);
        EditText time = findViewById(R.id.timeGame);
        EditText desc = findViewById(R.id.descGame);
        EditText place = findViewById(R.id.placeGame);
        TextView hint = findViewById(R.id.hint2);

        if (desc.getText().toString().trim().isEmpty() &&
                time.getText().toString().trim().isEmpty() &&
                place.getText().toString().trim().isEmpty() &&
                name.getText().toString().trim().isEmpty()) {
            hint.setAlpha(0.6f);
            return;
        }

        opengame og = new opengame(MainActivity.user.getDisplayName(), MainActivity.user.getUid(), name.getText().toString().trim(), time.getText().toString().trim(), desc.getText().toString().trim(), place.getText().toString().trim());
        og.addMember(MainActivity.user.getDisplayName());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("opengames").push().setValue(og);

        goBack(null);
    }
}