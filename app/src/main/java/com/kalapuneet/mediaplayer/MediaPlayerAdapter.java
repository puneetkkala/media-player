package com.kalapuneet.mediaplayer;

import android.app.Activity;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kalapuneet.mediaplayer.notification.NotificationCreator;
import com.kalapuneet.mediaplayer.objects.MediaFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by puneetkkala on 12/01/17.
 */

public class MediaPlayerAdapter extends RecyclerView.Adapter<MediaPlayerAdapter.MediaItemViewHolder> {

    private Activity activity;
    private TreeMap<String,MediaFile> mediaFilesTreeMap;
    private ArrayList<String> mediaFiles;
    public static MediaPlayer mediaPlayer;
    public static String currentTrack = null;

    public MediaPlayerAdapter(Activity activity, TreeMap<String,MediaFile> mediaFilesTreeMap) {
        this.activity = activity;
        this.mediaFilesTreeMap = mediaFilesTreeMap;
        this.mediaFiles = new ArrayList<>(mediaFilesTreeMap.keySet());
    }

    @Override
    public MediaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item_row,parent,false);
        return new MediaItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MediaItemViewHolder holder, int position) {
        if(position > -1 && position < mediaFiles.size()) {
            String key = mediaFiles.get(position);
            holder.mediaItemTitle.setText(key);
            if(currentTrack != null && currentTrack.equalsIgnoreCase(key)) {
                holder.mediaItemPlayPause.setImageResource(R.drawable.ic_stop_black_24dp);
            } else {
                holder.mediaItemPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }

    public class MediaItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mediaItemTitle;
        private ImageView mediaItemPlayPause;
        private LinearLayout mediaItemLayout;

        public MediaItemViewHolder(View itemView) {
            super(itemView);
            mediaItemTitle = (TextView) itemView.findViewById(R.id.media_item_title);
            mediaItemPlayPause = (ImageView) itemView.findViewById(R.id.media_item_play_pause);
            mediaItemPlayPause.setOnClickListener(this);
            mediaItemLayout = (LinearLayout) itemView.findViewById(R.id.media_item_layout);
            mediaItemLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position > -1 && position < mediaFiles.size()) {
                String key = mediaFiles.get(position);
                MediaFile mediaFile = mediaFilesTreeMap.get(key);
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (currentTrack != null && currentTrack.equalsIgnoreCase(key)) {
                    currentTrack = null;
                } else {
                    currentTrack = key;
                    mediaPlayer = new MediaPlayer();
                    long id = mediaFile.getMediaId();
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setWakeMode(activity, PowerManager.PARTIAL_WAKE_LOCK);
                    try {
                        mediaPlayer.setDataSource(activity,contentUri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new NotificationCreator(key,activity.getApplicationContext());
                }
                notifyDataSetChanged();
            }
        }
    }
}
