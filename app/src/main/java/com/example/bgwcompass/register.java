package com.example.bgwcompass;

import static com.example.bgwcompass.MainActivity.foundUsers;
import static com.example.bgwcompass.MainActivity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class register extends AppCompatActivity {

    EditText desc, time, place;
    TextView hint;
    boolean wasFound;
    static String descstr = "", timestr = "", placestr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        hideSystemBars();

        desc = findViewById(R.id.desc);
        time = findViewById(R.id.time);
        place = findViewById(R.id.place);
        hint = findViewById(R.id.hint2);

        desc.setText(descstr);
        time.setText(timestr);
        place.setText(placestr);

        desc.setFocusable(false);
        time.setFocusable(false);
        place.setFocusable(false);

        new DownloadImageFromInternet(findViewById(R.id.logo)).execute(Objects.requireNonNull(user.getPhotoUrl()).toString());

        hint.setAlpha(0.0f);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                encoder e = new encoder();
                foundUsers.clear();
                int storage = 0;
                for (DataSnapshot dataStorage : dataSnapshot.getChildren()) {
                    storage++;
                    for (DataSnapshot userDescription : dataStorage.getChildren()) {
                        ArrayList<String> data = new ArrayList<>();
                        for (DataSnapshot descriptionAttributes : userDescription.getChildren()) {
                            data.add(Objects.requireNonNull(descriptionAttributes.getValue()).toString());
                        }
                        if (storage == 1) {
                            userDescription description = new userDescription(e.decode(data.get(3)), e.decode(data.get(2)), e.decode(data.get(1)), data.get(0));
                            foundUsers.add(description);
                            if (!wasFound && description.getId().equals(user.getUid())) {
                                wasFound = true;
                                ref.removeEventListener(this);
                                finish();
                                startActivity(new Intent(register.this, HomePage.class));
                            }
                        }
                    }
                    if (!wasFound) {
                        RelativeLayout rl = findViewById(R.id.loadingTemp);
                        rl.setVisibility(View.GONE);

                        desc.setFocusableInTouchMode(true);
                        time.setFocusableInTouchMode(true);
                        place.setFocusableInTouchMode(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к чату, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void goNext(View view) {
        if (desc.getText().toString().trim().isEmpty() &&
                time.getText().toString().trim().isEmpty() &&
                place.getText().toString().trim().isEmpty()) {
            hint.setAlpha(0.6f);
            return;
        }

        encoder e = new encoder();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("description").push().setValue(new userDescription(e.encode(place.getText().toString().trim()), e.encode(time.getText().toString().trim()), e.encode(desc.getText().toString().trim()), user.getUid()));

        startActivity(new Intent(register.this, HomePage.class));
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage = null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}