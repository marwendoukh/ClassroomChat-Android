package com.classroomchat.marwen.classroomchat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.classroomchat.marwen.classroomchat.entity.Friend;

import static android.content.ContentValues.TAG;

/**
 * Created by marwen on 28/08/17.
 */

public class LocalStorage extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "ClassroomChat";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String FRIENDS = "friends";

    // friends Table Columns
    private static final String ID = "id";
    private static final String FRIEND_NAME = "friendName";
    private static final String BLUETOOTH_UUID = "uuid";
    private static final String PROFILE_PICTURE = "profilePicture";


    private static LocalStorage sInstance;


    private LocalStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton
    public static synchronized LocalStorage getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new LocalStorage(context.getApplicationContext());
        }
        return sInstance;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }


    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + FRIENDS +
                "(" +
                ID + " INTEGER PRIMARY KEY," + // Define a primary key
                FRIEND_NAME + " VARCHAR(50)" +
                BLUETOOTH_UUID + " VARCHAR(50)" +
                PROFILE_PICTURE + " VARCHAR(5000)" +
                ")";

        sqLiteDatabase.execSQL(CREATE_FRIENDS_TABLE);

    }


    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + FRIENDS);
            onCreate(db);
        }
    }


    // add new friend
    public Boolean saveFriend(Friend friend) {

        Boolean success;

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();


        if (!friendAlreadyExist(friend.getUuid())) {
            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put(BLUETOOTH_UUID, friend.getUuid());

                db.insertOrThrow(FRIENDS, null, values);
                db.setTransactionSuccessful();
                success = true;
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add to database");
                success = false;
            } finally {
                db.endTransaction();
            }
        } else
            // friend already exists
            success = false;

        return success;
    }


    // find saved player names
    public Friend findFriendByUUID(String uuid) {

        Friend friend = new Friend();
        String FIND_PLAYER_NAME_QUERY = new String("SELECT * from " + FRIENDS + " WHERE " + BLUETOOTH_UUID + "=\"" + uuid + "%\" ");


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FIND_PLAYER_NAME_QUERY, null);


        try {
            if (cursor.moveToFirst()) {
                do {
                    friend.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
                    friend.setProfilePicture(cursor.getString(cursor.getColumnIndex(PROFILE_PICTURE)));
                    friend.setUuid(cursor.getString(cursor.getColumnIndex(BLUETOOTH_UUID)));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return friend;
    }


    public Boolean friendAlreadyExist(String uuid) {

        if (findFriendByUUID(uuid).getUuid().equals(uuid))
            return true;
        else
            return false;
    }
}
