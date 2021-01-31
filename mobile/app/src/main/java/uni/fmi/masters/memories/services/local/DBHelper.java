package uni.fmi.masters.memories.services.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import uni.fmi.masters.memories.entities.User;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "hello.db";
    public static final int DB_VERSION = 1;

    // Колони на таблица user
    public static final String TABLE_USER = "user";

    public static final String TABLE_USER_ID = "id";
    public static final String TABLE_USER_USERNAME = "username";
    public static final String TABLE_USER_PASSWORD = "password";

    // Заявка създаване на таблица user
    public static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
            + " ('" + TABLE_USER_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT," +
            "'" + TABLE_USER_USERNAME + "' varchar(50) NOT NULL UNIQUE," +
            "'" + TABLE_USER_PASSWORD + "' varchar(50) NOT NULL)";
    public static final String MY_ERROR = "MyError";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public boolean registerUser(User user) {

        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_USER_USERNAME, user.getUsername());
            contentValues.put(TABLE_USER_PASSWORD, user.getPassword());
            if (db.insert(TABLE_USER, null, contentValues) != -1) {
                return true;
            }
        } catch (SQLException sqlException) {
            Log.wtf(MY_ERROR, sqlException.getMessage());
        } finally {
            if (db != null)
                db.close();
        }

        return false;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = null;
        Cursor c = null;

        try {
            db = getReadableDatabase();

            String sql = "SELECT * FROM " + TABLE_USER
                    + " WHERE " + TABLE_USER_USERNAME
                    + " = '" + username + "'"
                    + " AND " + TABLE_USER_PASSWORD + " = '" + password + "'";

            c = db.rawQuery(sql, null);

            return c.moveToFirst();
        } catch (Exception e) {
            Log.wtf(MY_ERROR, e.getMessage());
        } finally {
            if (c != null)
                c.close();

            if (db != null)
                db.close();
        }

        return false;
    }

    public void removeAccount(String username) {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            String where = TABLE_USER_USERNAME + "=?";
            String[] whereArgs = { username };

            db.delete(TABLE_USER, where, whereArgs);
        } catch (SQLException e) {
            Log.wtf(MY_ERROR, e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
    }
}
