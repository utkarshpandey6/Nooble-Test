package com.example.nooble;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import xyz.hanks.library.bang.SmallBangView;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    static private List<AudioItems> audioItems;
    public AudioAdapter(List<AudioItems> audioItems) {
        this.audioItems = audioItems;
    }
    public void setAudioItems(List<AudioItems> audioItems)
    {
        this.audioItems = audioItems;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AudioViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_audio,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.setAudioData(audioItems.get(position));
    }

    @Override
    public int getItemCount() {
        return audioItems.size();
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {


        TextView name;
        TextView album;
        TextView artist;
        ImageView image;

        public static Bitmap getBitmapFromURL(String src) {
            try {

                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
        }


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.audio_title);
            album = itemView.findViewById(R.id.audio_description);
            artist = itemView.findViewById(R.id.speaker);
            image = itemView.findViewById(R.id.profile_image);
            SmallBangView likeView;
            likeView = itemView.findViewById(R.id.likeView);
            likeView.setOnClickListener(
                    v -> {
                        if (likeView.isSelected()) {
                            likeView.setSelected(false);
                        } else {
                            likeView.setSelected(true);
                            likeView.likeAnimation();
                        }
                    });
        }

        void setAudioData(AudioItems item)
        {
            name.setText(item.name);
            album.setText(item.album);
            artist.setText(item.artist);
            Picasso.get().load(item.image).placeholder(R.drawable.ic_baseline_account_circle_24).error(R.drawable.ic_baseline_account_circle_24).into(image);

        }
    }
}
