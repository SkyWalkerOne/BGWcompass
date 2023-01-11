package com.example.bgwcompass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class gameAdaptor extends RecyclerView.Adapter<gameAdaptor.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<opengame> games;
    private final gameAdaptor.onUserJoin cn;

    gameAdaptor(Context context, List<opengame> states, gameAdaptor.onUserJoin cn) {
        this.games = states;
        this.inflater = LayoutInflater.from(context);
        this.cn = cn;
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

        StringBuilder members = new StringBuilder("Участники:");
        for (String str: game.getMembers())
            members.append("\n").append(str);
        holder.members.setText(members.toString());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name, creator, time, desc, members, place;
        final ImageView join, del;
        gameAdaptor.onUserJoin cn;

        ViewHolder(View view, gameAdaptor.onUserJoin cn) {
            super(view);
            name = view.findViewById(R.id.title);
            creator = view.findViewById(R.id.creator);
            time = view.findViewById(R.id.time);
            desc = view.findViewById(R.id.description);
            members = view.findViewById(R.id.members);
            join = view.findViewById(R.id.joinGame);
            del = view.findViewById(R.id.delete);
            place = view.findViewById(R.id.place);
            this.cn = cn;
            join.setTag("join");
            join.setOnClickListener(this);
            del.setTag("delete");
            del.setOnClickListener(this);
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
