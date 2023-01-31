package com.example.bgwcompass.recyclerAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgwcompass.mainWindows.HomePage;
import com.example.bgwcompass.R;
import com.example.bgwcompass.dataClasses.memberAppearance;
import com.example.bgwcompass.dataClasses.savedAvatar;

import java.io.InputStream;
import java.util.List;

public class memberAdapter extends RecyclerView.Adapter<memberAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<memberAppearance> members;

    memberAdapter(Context context, List<memberAppearance> states) {
        this.members = states;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public memberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.member_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(memberAdapter.ViewHolder holder, int position) {
        memberAppearance ma = members.get(position);
        holder.name.setText(ma.getNickName());
        new memberAdapter.DownloadImageFromInternet(holder.avatar).execute(ma.getAvatarURL());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView avatar;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            avatar = view.findViewById(R.id.logo);
        }
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

