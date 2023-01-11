package com.example.bgwcompass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<chatMessage> messages;
    private final onUserClick cn;

    messageAdapter(Context context, List<chatMessage> states, onUserClick cn) {
        this.messages = states;
        this.inflater = LayoutInflater.from(context);
        this.cn = cn;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_layout, parent, false);
        return new ViewHolder(view, cn);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        chatMessage message = messages.get(position);
        holder.time.setText(message.getTime());
        holder.nick.setText(message.getUserName());
        holder.messs.setText(message.getMessage());
        if (MainActivity.user.getUid().equals(message.getId())) {
            holder.messs.setBackgroundColor(Color.parseColor("#6E6728"));
            holder.del.setAlpha(0.5f);
            holder.del.setClickable(true);
        } else {
            holder.del.setAlpha(0.0f);
            holder.del.setClickable(false);
        }
        for (savedAvatar sa: HomePage.avs) {
            if (Objects.equals(sa.getUrl(), message.getAvatarURL())) {
                holder.img.setImageBitmap(sa.getBimage());
                return;
            }
        }
        new DownloadImageFromInternet(holder.img).execute(message.getAvatarURL());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView time, nick, messs;
        final ImageView img, del;
        onUserClick cn;

        ViewHolder(View view, onUserClick cn) {
            super(view);
            time = view.findViewById(R.id.timeText);
            nick = view.findViewById(R.id.usernameText);
            messs = view.findViewById(R.id.messagetext);
            img = view.findViewById(R.id.logo);
            del = view.findViewById(R.id.delete);
            this.cn = cn;
            messs.setOnClickListener(this);
            img.setOnClickListener(this);
            del.setTag("delete");
            del.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cn.onClick(getAdapterPosition(), view);
        }
    }

    public interface onUserClick {
        void onClick(int index, View v);
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
            HomePage.avs.add(new savedAvatar(bimage, imageURL));
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}