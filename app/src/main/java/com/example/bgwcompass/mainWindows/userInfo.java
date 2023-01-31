package com.example.bgwcompass.mainWindows;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bgwcompass.R;
import com.example.bgwcompass.constants;
import com.example.bgwcompass.dataClasses.chatMessage;
import com.example.bgwcompass.dataClasses.userDescription;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class userInfo extends AppCompatActivity {

    public static userDescription ud;
    public static chatMessage cm;
    ImageView editButton;

    TextView desc, time, place, nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        hideSystemBars();

        desc = findViewById(R.id.desc);
        time = findViewById(R.id.time);
        place = findViewById(R.id.place);
        nick = findViewById(R.id.nickname);

        desc.setText(ud.getMyDesc());
        time.setText(ud.getMyTime());
        place.setText(ud.getPlace());
        nick.setText(cm.getUserName());

        editButton = findViewById(R.id.buttonEditProfile);
        if (!MainActivity.user.getUid().equals(cm.getId())) {
            editButton.setAlpha(0.0f);
            editButton.setClickable(false);
        }

        new DownloadImageFromInternet(findViewById(R.id.logo)).execute(cm.getAvatarURL());
    }

    public void goBack(View view) {
        finish();
        startActivity(new Intent(userInfo.this, HomePage.class));
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

    public void editProfile(View view) {
        register.descstr = ud.getMyDesc();
        register.timestr = ud.getMyTime();
        register.placestr = ud.getPlace();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int storage = 0;
                for (DataSnapshot storages : dataSnapshot.getChildren()) {
                    storage++;
                    if (storage == constants.STORAGE_DESCRIPTIONS) {
                        for (DataSnapshot description : storages.getChildren()) {
                            ArrayList<String> data = new ArrayList<>();

                            for (DataSnapshot descAttrs : description.getChildren()) {
                                data.add(Objects.requireNonNull(descAttrs.getValue()).toString());
                            }

                            if (data.get(0).equals(ud.getId())) {
                                description.getRef().removeValue();
                                ref.removeEventListener(this);
                            }
                        }
                    }
                }

                ref.removeEventListener(this);

                finish();
                startActivity(new Intent(userInfo.this, register.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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