package com.example.bgwcompass.recyclerAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgwcompass.constants;
import com.example.bgwcompass.mainWindows.MainActivity;
import com.example.bgwcompass.R;
import com.example.bgwcompass.dataClasses.memberAppearance;
import com.example.bgwcompass.dataClasses.opengame;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class gameAdaptor extends RecyclerView.Adapter<gameAdaptor.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<opengame> games;
    private final gameAdaptor.onUserJoin cn;
    private final Context windowContext;

    public gameAdaptor(Context context, List<opengame> states, gameAdaptor.onUserJoin cn, Context windowContext) {
        this.games = states;
        this.inflater = LayoutInflater.from(context);
        this.cn = cn;
        this.windowContext = windowContext;
    }
    @NonNull
    @Override
    public gameAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.game_example_layout, parent, false);
        return new ViewHolder(view, cn);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(gameAdaptor.ViewHolder holder, int position) {
        opengame game = games.get(position);
        holder.name.setText(game.getGameName());
        holder.time.setText("Время игры: "+game.getGameDime());
        holder.creator.setText("Создал: "+game.getCreatorName());
        holder.desc.setText(game.getGameDesc());
        holder.place.setText("Место игры: "+game.getGamePlace());

        if (MainActivity.user.getUid().equals(game.getCreatorID())) {
            holder.del.setAlpha(0.5f);
            holder.del.setClickable(true);
        } else {
            holder.del.setAlpha(0.1f);
            holder.del.setClickable(false);
        }

        ArrayList<memberAppearance> members = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                members.clear();
                int storage = 0;
                for (DataSnapshot storages : dataSnapshot.getChildren()) {
                    storage++;
                    if (storage == constants.STORAGE_PROFILES) {
                        for (DataSnapshot profile : storages.getChildren()) {
                            ArrayList<String> data = new ArrayList<>();

                            for (DataSnapshot profileAttribute : profile.getChildren())
                                data.add(Objects.requireNonNull(profileAttribute.getValue()).toString());

                            for (String id: game.getMembers()) {
                                if (id.equals(data.get(1)))
                                    members.add(new memberAppearance(data.get(1), data.get(0), data.get(2)));
                            }
                        }
                    }
                }

                memberAdapter adapter = new memberAdapter(windowContext, members);
                holder.members.setAdapter(adapter);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(inflater.getContext(),
                        "Не удалось подключиться к чату, попробуйте проверить интернет-соединение!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name, creator, time, desc, place;
        final ImageView join, del, search;
        RecyclerView members;
        gameAdaptor.onUserJoin cn;

        ViewHolder(View view, gameAdaptor.onUserJoin cn) {
            super(view);
            name = view.findViewById(R.id.title);
            creator = view.findViewById(R.id.creator);
            time = view.findViewById(R.id.time);
            desc = view.findViewById(R.id.description);
            members = view.findViewById(R.id.list);
            join = view.findViewById(R.id.joinGame);
            del = view.findViewById(R.id.delete);
            place = view.findViewById(R.id.place);
            search = view.findViewById(R.id.search);
            this.cn = cn;
            join.setTag("join");
            join.setOnClickListener(this);
            del.setTag("delete");
            del.setOnClickListener(this);
            search.setTag("search");
            search.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cn.onClick(getAdapterPosition(), view);
        }
    }

    public interface onUserJoin {
        void onClick(int index, View v);
    }
}
