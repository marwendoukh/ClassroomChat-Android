package com.classroomchat.marwen.classroomchat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.classroomchat.marwen.classroomchat.entity.Friend;

import java.util.ArrayList;
import java.util.List;

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
    private static final String CONNECTION_COUNT = "connectionCount";
    private static final String MESSAGES_SENT_COUNT = "messagesSentCount";
    private static final String MESSAGES_RECEIVED_COUNT = "messagesReceivedCount";

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
                FRIEND_NAME + " VARCHAR(50)," +
                BLUETOOTH_UUID + " VARCHAR(50)," +
                CONNECTION_COUNT + " INTEGER," +
                MESSAGES_SENT_COUNT + " INTEGER," +
                MESSAGES_RECEIVED_COUNT + " INTEGER" +
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
    public Boolean saveOrUpdateFriend(Friend friend) {

        Boolean success;


        if (!friendAlreadyExist(friend.getUuid())) {

            // Create and/or open the database for writing
            SQLiteDatabase db = getWritableDatabase();
            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put(FRIEND_NAME, friend.getFriendName());
                values.put(BLUETOOTH_UUID, friend.getUuid());
                values.put(CONNECTION_COUNT, 1);
                values.put(MESSAGES_SENT_COUNT, 0);
                values.put(MESSAGES_RECEIVED_COUNT, 0);

                db.insertOrThrow(FRIENDS, null, values);
                db.setTransactionSuccessful();
                success = true;
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add to database");
                success = false;
            } finally {
                db.endTransaction();
            }
        } else {
            // friend already exists


            // Create and/or open the database for writing
            SQLiteDatabase db = getWritableDatabase();
            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {


                String FIND_FRIEND_BY_UUID_QUERY = new String("UPDATE " + FRIENDS + " SET " + FRIEND_NAME + " = \"" + friend.getFriendName() + "\" , " + CONNECTION_COUNT + " = " + CONNECTION_COUNT + "+1" + " WHERE " + BLUETOOTH_UUID + " = \"" + friend.getUuid() + "\" ;");

                db.execSQL(FIND_FRIEND_BY_UUID_QUERY);
                db.setTransactionSuccessful();

            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add to database");
                success = false;
            } finally {
                db.endTransaction();
            }
            success = false;
        }
        return success;
    }


    // find saved player names
    public Friend findFriendByUUID(String uuid) {

        Friend friend = new Friend();
        String FIND_FRIEND_BY_UUID_QUERY = new String("SELECT * FROM " + FRIENDS + " WHERE " + BLUETOOTH_UUID + "=\"" + uuid + "\" ");


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FIND_FRIEND_BY_UUID_QUERY, null);


        try {
            if (cursor.moveToFirst()) {

                    friend.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
                    friend.setUuid(cursor.getString(cursor.getColumnIndex(BLUETOOTH_UUID)));
                friend.setConnectionCount(cursor.getInt(cursor.getColumnIndex(CONNECTION_COUNT)));
                friend.setMessagesSentCount(cursor.getInt(cursor.getColumnIndex(MESSAGES_SENT_COUNT)));
                friend.setMessagesReceivedCount(cursor.getInt(cursor.getColumnIndex(MESSAGES_RECEIVED_COUNT)));


            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to communicate with database");
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


    /// increase messages sent Count
    public void increaseMessagesSentCount(Friend friend) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {


            String FIND_FRIEND_BY_UUID_QUERY = new String("UPDATE " + FRIENDS + " SET " + MESSAGES_SENT_COUNT + " = " + MESSAGES_SENT_COUNT + "+1" + " WHERE " + BLUETOOTH_UUID + " = \"" + friend.getUuid() + "\" ;");

            db.execSQL(FIND_FRIEND_BY_UUID_QUERY);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add to database");
        } finally {
            db.endTransaction();
        }

    }


    /// increase messages received Count
    public void increaseMessagesReceivedCount(Friend friend) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            String FIND_FRIEND_BY_UUID_QUERY = new String("UPDATE " + FRIENDS + " SET " + MESSAGES_RECEIVED_COUNT + " = " + MESSAGES_RECEIVED_COUNT + "+1" + " WHERE " + BLUETOOTH_UUID + " = \"" + friend.getUuid() + "\" ;");

            db.execSQL(FIND_FRIEND_BY_UUID_QUERY);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add to database");
        } finally {
            db.endTransaction();
        }

    }


    // total messages sent
    public Integer totalMessagesSent() {

        Integer totalMessagesSent = 0;

        Friend friend = new Friend();
        String FIND_FRIEND_BY_UUID_QUERY = new String("SELECT SUM(" + MESSAGES_SENT_COUNT + ") FROM " + FRIENDS);


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FIND_FRIEND_BY_UUID_QUERY, null);


        try {
            cursor.moveToFirst();

            totalMessagesSent = cursor.getInt(0);


        } catch (Exception e) {
            Log.d(TAG, "Error while trying to communicate with database" + e.toString());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return totalMessagesSent;
    }


    // total messages received
    public Integer totalMessagesReceived() {

        Integer totalMessagesReceived = 0;

        Friend friend = new Friend();
        String FIND_FRIEND_BY_UUID_QUERY = new String("SELECT SUM(" + MESSAGES_RECEIVED_COUNT + ") FROM " + FRIENDS);


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FIND_FRIEND_BY_UUID_QUERY, null);


        try {
            cursor.moveToFirst();

            totalMessagesReceived = cursor.getInt(0);


        } catch (Exception e) {
            Log.d(TAG, "Error while trying to communicate with database" + e.toString());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return totalMessagesReceived;
    }


    // find all friends
    public List<Friend> findAllFriends() {

        List<Friend> friends = new ArrayList<>();
        String FIND_FRIEND_BY_UUID_QUERY = new String("SELECT * FROM " + FRIENDS);


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FIND_FRIEND_BY_UUID_QUERY, null);


        try {
            if (cursor.moveToFirst()) {

                Friend friend = new Friend();
                friend.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
                friend.setUuid(cursor.getString(cursor.getColumnIndex(BLUETOOTH_UUID)));
                friend.setConnectionCount(cursor.getInt(cursor.getColumnIndex(CONNECTION_COUNT)));
                friend.setMessagesSentCount(cursor.getInt(cursor.getColumnIndex(MESSAGES_SENT_COUNT)));
                friend.setMessagesReceivedCount(cursor.getInt(cursor.getColumnIndex(MESSAGES_RECEIVED_COUNT)));
                friends.add(friend);

            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to communicate with database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return friends;
    }
}
