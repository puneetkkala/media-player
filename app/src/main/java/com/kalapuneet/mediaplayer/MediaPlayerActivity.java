package com.kalapuneet.mediaplayer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.kalapuneet.mediaplayer.database.MediaFilesDatabaseHelper;
import com.kalapuneet.mediaplayer.objects.MediaFile;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by puneetkkala on 12/01/17.
 */

public class MediaPlayerActivity extends AppCompatActivity implements TextWatcher{

    private RecyclerView mediaFilesRecyclerView;
    private TreeMap<String,MediaFile> allMediaFiles;
    private TreeMap<String,MediaFile> mediaFilesTreeMap;
    private ArrayList<MediaFile> mediaFiles;
    private MediaPlayerAdapter mediaPlayerAdapter;
    private EditText editQueryText;

    private void prepareList() {
        allMediaFiles = new TreeMap<>();
        mediaFilesTreeMap = new TreeMap<>();
        MediaFilesDatabaseHelper helper = new MediaFilesDatabaseHelper(MediaPlayerActivity.this);
        mediaFiles = helper.getAllMediaFiles();
        helper.close();
        for(MediaFile mediaFile: mediaFiles) {
            allMediaFiles.put(mediaFile.getTitle(),mediaFile);
        }
        this.mediaFilesTreeMap = allMediaFiles;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_activity);
        prepareList();
        mediaFilesRecyclerView = (RecyclerView) findViewById(R.id.media_files_recycler_view);
        editQueryText = (EditText) findViewById(R.id.edit_query_text);
        editQueryText.addTextChangedListener(this);
        mediaPlayerAdapter = new MediaPlayerAdapter(MediaPlayerActivity.this,mediaFilesTreeMap);
        mediaFilesRecyclerView.setAdapter(mediaPlayerAdapter);
        mediaFilesRecyclerView.setLayoutManager(new LinearLayoutManager(MediaPlayerActivity.this));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if(s.toString().length() == 0) {
            mediaFilesTreeMap = new TreeMap<>(allMediaFiles);
        } else {
            mediaFilesTreeMap = new TreeMap<>();
            for (String name: allMediaFiles.keySet()) {
                if(name.toLowerCase().startsWith(s.toString().toLowerCase())) {
                    mediaFilesTreeMap.put(name,allMediaFiles.get(name));
                }
            }
        }
        mediaPlayerAdapter = new MediaPlayerAdapter(MediaPlayerActivity.this,mediaFilesTreeMap);
        mediaFilesRecyclerView.setAdapter(mediaPlayerAdapter);
        mediaFilesRecyclerView.setLayoutManager(new LinearLayoutManager(MediaPlayerActivity.this));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MEDIA_PLAYER_KALA",MODE_PRIVATE);
        MediaPlayerAdapter.currentTrack = sharedPreferences.getString("currentTrack","");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MEDIA_PLAYER_KALA",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentTrack",MediaPlayerAdapter.currentTrack != null ? MediaPlayerAdapter.currentTrack : "");
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MediaPlayerAdapter.mediaPlayer != null) {
            MediaPlayerAdapter.mediaPlayer.release();
        }
    }
}
