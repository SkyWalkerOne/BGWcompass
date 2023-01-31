package com.example.bgwcompass.mainWindows;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bgwcompass.R;
import com.example.bgwcompass.additionalWindows.createGame;
import com.example.bgwcompass.constants;
import com.example.bgwcompass.dataClasses.opengame;
import com.example.bgwcompass.recyclerAdapters.gameAdaptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class games_activity extends AppCompatActivity implements gameAdaptor.onUserJoin{

    gameAdaptor.onUserJoin cn;
    ArrayList<opengame> games;
    DatabaseReference ref;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        games = new ArrayList<>();
        cn = this;

        ref = FirebaseDatabase.getInstance().getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int storage = 0;
                listener = this;

                for (DataSnapshot storages : dataSnapshot.getChildren()) {
                    storage++;
                    if (storage == constants.STORAGE_GAMES) {
                        games.clear();

                        for (DataSnapshot opengames : storages.getChildren()) {
                            ArrayList<String> data = new ArrayList<>();
                            ArrayList<String> allMembers = new ArrayList<>();

                            int count = 0;
                            DataSnapshot memberList = null;

                            for (DataSnapshot gameAttributes : opengames.getChildren()) {
                                data.add(Objects.requireNonNull(gameAttributes.getValue()).toString());

                                if (count == 6) memberList = gameAttributes;
                                count++;
                            }

                            String id = data.get(0);
                            String creator = data.get(1);
                            String desc = data.get(2);
                            String time = data.get(3);
                            String name = data.get(4);
                            String place = data.get(5);

                            if (memberList != null) {
                                for (DataSnapshot member : memberList.getChildren())
                                    allMembers.add(Objects.requireNonNull(member.getValue()).toString());

                                games.add(new opengame(creator, id, name, time, desc, allMembers, place));
                            }
                        }
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.list);
                gameAdaptor adapter = new gameAdaptor(getApplicationContext(), games, cn, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к серверу, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void goToChat(View view) {
        ref.removeEventListener(listener);
        finish();
        startActivity(new Intent(games_activity.this, HomePage.class));
    }

    public void creategame(View view) {
        ref.removeEventListener(listener);
        finish();
        startActivity(new Intent(games_activity.this, createGame.class));
    }

    @Override
    public void onClick(int index, View v) {
        if (v.getTag().equals("search")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com.ua/maps/search/" + games.get(index).getGamePlace().replaceAll("\\s", "+"))));
            return;
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainActivity.foundUsers.clear();
                int storage = 0;

                for (DataSnapshot dataStorage : dataSnapshot.getChildren()) {
                    storage++;
                    if (storage == constants.STORAGE_GAMES) {
                        int gameCount = 0;

                        for (DataSnapshot gameBlock : dataStorage.getChildren()) {
                            int count = 0;
                            DataSnapshot memberList = null;

                            for (DataSnapshot gameAttribute : gameBlock.getChildren()) {
                                if (gameCount == index && count == 1 && v.getTag().equals("delete") && MainActivity.user.getUid().equals(games.get(index).getCreatorID())) {
                                    ref.removeEventListener(this);
                                    gameBlock.getRef().removeValue();
                                    return;
                                }
                                if (count == 6) memberList = gameAttribute;
                                count++;
                            }

                            if (gameCount == index) {
                                assert memberList != null;
                                boolean alreadyExists = false;
                                for (DataSnapshot member : memberList.getChildren())
                                    if (Objects.requireNonNull(member.getValue()).toString().equals(MainActivity.user.getUid())) alreadyExists = true;
                                if (!alreadyExists) memberList.getRef().push().setValue(MainActivity.user.getUid());
                            }

                            gameCount++;
                        }
                    }
                }

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к серверу, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}