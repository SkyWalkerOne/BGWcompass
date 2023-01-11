package com.example.bgwcompass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class HomePage extends AppCompatActivity implements messageAdapter.onUserClick{

    ArrayList<chatMessage> messages;
    messageAdapter.onUserClick cn;
    encoder e;
    final int maxMessagesValue = 20;
    static ArrayList<savedAvatar> avs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        hideSystemBars();

        messages = new ArrayList<>();
        avs = new ArrayList<>();
        cn = this;
        e = new encoder();

        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.facebook_sms);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                MainActivity.foundUsers.clear();
                boolean found = false;
                int storage = 0;
                for (DataSnapshot dataStorage : dataSnapshot.getChildren()) {
                    storage++;
                    for (DataSnapshot messageBlock : dataStorage.getChildren()) {
                        ArrayList<String> data = new ArrayList<>();
                        for (DataSnapshot messageAttribute : messageBlock.getChildren()) {
                            data.add(Objects.requireNonNull(messageAttribute.getValue()).toString());
                        }
                        if (storage == 2) {
                            messages.add(new chatMessage(e.decode(data.get(4)), e.decode(data.get(2)), e.decode(data.get(3)), data.get(1), data.get(0)));
                            if (!found && dataStorage.getChildrenCount() >= maxMessagesValue + 1) {
                                messageBlock.getRef().removeValue();
                                found = true;
                            }
                        } else if (storage == 1) {
                            MainActivity.foundUsers.add(new userDescription(e.decode(data.get(3)), e.decode(data.get(2)), e.decode(data.get(1)), data.get(0)));
                        }
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.list);
                messageAdapter adapter = new messageAdapter(getApplicationContext(), messages, cn);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);


                if (!mp.isPlaying()) mp.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к чату, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void sendMessage(View view) {
        EditText enterField = findViewById(R.id.enter);
        if (!enterField.getText().toString().trim().isEmpty()) {
            Date date = new Date();
            String time = date.getHours() + ":" + ((date.getMinutes() >= 10) ? date.getMinutes() : ("0" + date.getMinutes()));

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("messages").push().setValue(new chatMessage(e.encode(Objects.requireNonNull(MainActivity.user.getDisplayName())), e.encode(enterField.getText().toString().trim()), e.encode(time), MainActivity.user.getUid(), Objects.requireNonNull(MainActivity.user.getPhotoUrl()).toString()));
            enterField.setText("");
        }
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

    @Override
    public void onClick(int index, View v) {
        if (v.getTag() == "delete") {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int storage = 0;
                    for (DataSnapshot dataStorage : dataSnapshot.getChildren()) {
                        storage++;
                        for (DataSnapshot messageBlock : dataStorage.getChildren()) {
                            ArrayList<String> data = new ArrayList<>();
                            for (DataSnapshot messageAttribute : messageBlock.getChildren()) {
                                data.add(Objects.requireNonNull(messageAttribute.getValue()).toString());
                            }
                            if (storage == 2 &&
                                    data.get(1).equals(messages.get(index).getId()) &&
                                    e.decode(data.get(2)).equals(messages.get(index).getMessage()) &&
                                    e.decode(data.get(3)).equals(messages.get(index).getTime())) {
                                messageBlock.getRef().removeValue();
                            }
                        }
                    }

                    ref.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        } else {
            userDescription description = null;
            for (userDescription ud: MainActivity.foundUsers) {
                if (ud.getId().equals(messages.get(index).getId())) {
                    description = ud;
                    break;
                }
            }
            if (description != null) {
                userInfo.cm = messages.get(index);
                userInfo.ud = description;
                finish();
                startActivity(new Intent(HomePage.this, userInfo.class));
            }
        }
    }

    public void goToGame(View view) {
        finish();
        startActivity(new Intent(HomePage.this, games_activity.class));
    }
}