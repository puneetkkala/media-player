package com.kalapuneet.mediaplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kalapuneet.mediaplayer.database.MediaFilesDatabaseHelper;
import com.kalapuneet.mediaplayer.objects.MediaFile;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_WAKE_LOCK_PERMISSION = 101;
    private ProgressBar mediaLoadProgressBar;
    private TextView preparingTheApp;

    private void startMediaPlayer() {
        startActivity(new Intent(MainActivity.this,MediaPlayerActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_WAKE_LOCK_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                    ContentResolver contentResolver = getContentResolver();
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    if(cursor != null && cursor.moveToFirst()) {
                        do {
                            MediaFile mediaFile = new MediaFile();
                            mediaFile.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                            mediaFile.setMediaId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                            mediaFiles.add(mediaFile);
                        } while (cursor.moveToNext());
                    }
                    if(cursor != null)
                        cursor.close();
                    MediaFilesDatabaseHelper helper = new MediaFilesDatabaseHelper(MainActivity.this);
                    for (MediaFile mediaFile: mediaFiles) {
                        helper.addMediaFile(mediaFile);
                    }
                    helper.close();
                    SharedPreferences sharedPreferences1 = getSharedPreferences("mediaPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putLong("lastFetchTime",0);
                    editor.apply();
                    mediaLoadProgressBar.setVisibility(View.GONE);
                    startMediaPlayer();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preparingTheApp = (TextView) findViewById(R.id.preparing_the_app);
        mediaLoadProgressBar = (ProgressBar) findViewById(R.id.media_load_progress_bar);
        SharedPreferences sharedPreferences = getSharedPreferences("mediaPreferences",MODE_PRIVATE);
        long time = sharedPreferences.getLong("lastFetchTime",0);
        if(System.currentTimeMillis() > 7 * 24 * 60 * 60 * 1000 + time) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WAKE_LOCK,Manifest.permission.READ_EXTERNAL_STORAGE},MY_WAKE_LOCK_PERMISSION);
            } else {
                ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = contentResolver.query(uri,null,null,null,null);
                if(cursor != null && cursor.moveToFirst()) {
                    do {
                        MediaFile mediaFile = new MediaFile();
                        mediaFile.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                        mediaFile.setMediaId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        mediaFiles.add(mediaFile);
                    } while (cursor.moveToNext());
                }
                if(cursor != null)
                    cursor.close();
                MediaFilesDatabaseHelper helper = new MediaFilesDatabaseHelper(MainActivity.this);
                for (MediaFile mediaFile: mediaFiles) {
                    helper.addMediaFile(mediaFile);
                }
                helper.close();
                SharedPreferences sharedPreferences1 = getSharedPreferences("mediaPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putLong("lastFetchTime",0);
                editor.apply();
                mediaLoadProgressBar.setVisibility(View.GONE);
                startMediaPlayer();
            }
        } else {
            preparingTheApp.setText("Loading...");
            mediaLoadProgressBar.setVisibility(View.GONE);
            startMediaPlayer();
        }
    }
}
