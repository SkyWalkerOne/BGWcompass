package com.example.bgwcompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        games = new ArrayList<>();
        cn = this;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                games.clear();
                MainActivity.foundUsers.clear();
                int storage = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    storage++;
                    for (DataSnapshot child2 : child.getChildren()) {
                        ArrayList<String> data = new ArrayList<>();
                        ArrayList<String> allMembers = new ArrayList<>();
                        int count = 0;
                        DataSnapshot memberList = null;
                        for (DataSnapshot child3 : child2.getChildren()) {
                            data.add(Objects.requireNonNull(child3.getValue()).toString());

                            if (count == 6) memberList = child3;
                            count++;
                        }
                        if (storage == 3) {
                            String id = data.get(0);
                            String creator = data.get(1);
                            String desc = data.get(2);
                            String time = data.get(3);
                            String name = data.get(4);
                            String place = data.get(5);
                            assert memberList != null;
                            for (DataSnapshot member : memberList.getChildren())
                                allMembers.add(Objects.requireNonNull(member.getValue()).toString());
                            games.add(new opengame(creator, id, name, time, desc, allMembers, place));
                        }
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.list);
                gameAdaptor adapter = new gameAdaptor(getApplicationContext(), games, cn);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к чату, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void goToChat(View view) {
        finish();
        startActivity(new Intent(games_activity.this, HomePage.class));
    }

    public void createGame(View view) {
        finish();
        startActivity(new Intent(games_activity.this, createGame.class));
    }

    @Override
    public void onClick(int index, View v) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainActivity.foundUsers.clear();
                int storage = 0;
                for (DataSnapshot dataStorage : dataSnapshot.getChildren()) {

                    storage++;
                    int gameCount = 0;

                    for (DataSnapshot gameBlock : dataStorage.getChildren()) {

                        int count = 0;
                        DataSnapshot memberList = null;

                        for (DataSnapshot gameAttribute : gameBlock.getChildren()) {
                            if (storage == 3 && gameCount == index && count == 1 && v.getTag().equals("delete") && MainActivity.user.getUid().equals(games.get(index).getCreatorID())) {
                                ref.removeEventListener(this);
                                gameBlock.getRef().removeValue();
                                return;
                            }
                            if (count == 6) memberList = gameAttribute;
                            count++;
                        }
                        if (storage == 3 && gameCount == index) {
                            assert memberList != null;
                            boolean alreadyExists = false;
                            for (DataSnapshot member : memberList.getChildren())
                                if (Objects.requireNonNull(member.getValue()).toString().equals(MainActivity.user.getDisplayName())) alreadyExists = true;
                            if (!alreadyExists) memberList.getRef().push().setValue(MainActivity.user.getDisplayName());
                        }

                        gameCount++;
                    }
                }

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Не удалось подключиться к чату, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}