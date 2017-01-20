package com.kalapuneet.mediaplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kalapuneet.mediaplayer.objects.MediaFile;

import java.util.ArrayList;

/**
 * Created by puneetkkala on 11/01/17.
 */

public class MediaFilesDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MediaFilesDb";
    private static final String TABLE_NAME = "mediaFiles";
    private static final String TAG_ID = "id";
    private static final String TAG_MEDIA_ID = "media_id";
    private static final String TAG_TITLE = "title";

    public MediaFilesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TAG_MEDIA_ID + " LONG," +
                TAG_TITLE + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);
    }

    public void addMediaFile(MediaFile mediaFile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_TITLE,mediaFile.getTitle());
        contentValues.put(TAG_MEDIA_ID,mediaFile.getMediaId());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null,contentValues);
    }

    public ArrayList<MediaFile> getAllMediaFiles() {
        String selectAll = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + TAG_TITLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAll,null);
        ArrayList<MediaFile> mediaFiles = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                MediaFile mediaFile = new MediaFile();
                mediaFile.setMediaId(cursor.getLong(cursor.getColumnIndex(TAG_MEDIA_ID)));
                mediaFile.setTitle(cursor.getString(cursor.getColumnIndex(TAG_TITLE)));
                mediaFiles.add(mediaFile);
            } while (cursor.moveToNext());
        }
        return mediaFiles;
    }
}
