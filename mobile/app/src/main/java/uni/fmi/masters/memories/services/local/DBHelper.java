package uni.fmi.masters.memories.services.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.entities.User;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "memories.db";
    public static final int DB_VERSION = 1;

    public static final String MY_ERROR = "MyError";

    // User - for future version use separate class
    public static final String TABLE_USER = "user";

    public static final String TABLE_USER_ID = "id";
    public static final String TABLE_USER_USERNAME = "username";
    public static final String TABLE_USER_PASSWORD = "password";

    public static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
            + " ('" + TABLE_USER_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT," +
            "'" + TABLE_USER_USERNAME + "' varchar(50) NOT NULL UNIQUE," +
            "'" + TABLE_USER_PASSWORD + "' varchar(50) NOT NULL)";

    // Category
    public static final String TABLE_CATEGORY = "category";

    public static final String TABLE_CATEGORY_ID = "id";
    public static final String TABLE_CATEGORY_NAME = "name";

    public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY
            + " ('" + TABLE_CATEGORY_ID + "' INTEGER PRIMARY KEY," +
            "'" + TABLE_CATEGORY_NAME + "' varchar(50) NOT NULL)";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    // User methods
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

    // Category methods
    public boolean addCategory(Category category) {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_CATEGORY_ID, category.getId());
            contentValues.put(TABLE_CATEGORY_NAME, category.getName());
            if (db.insert(TABLE_CATEGORY, null, contentValues) != -1) {
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

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<Category>();

        SQLiteDatabase db = null;
        Cursor c = null;

        try {
            String getAllCategoriesSQL = "SELECT * FROM category ORDER BY id DESC";

            db = this.getWritableDatabase();
            c = db.rawQuery(getAllCategoriesSQL, null);

            if (c.moveToFirst()) {
                do {
                    int id = Integer.parseInt(c.getString(c.getColumnIndex(TABLE_CATEGORY_ID)));
                    String name = c.getString(c.getColumnIndex(TABLE_CATEGORY_NAME));

                    Category category = new Category(id, name);

                    categories.add(category);
                } while (c.moveToNext());
            }
        } catch (SQLException e) {
            Log.wtf(MY_ERROR, e.getMessage());
        } finally {
            if (c != null)
                c.close();

            if (db != null)
                db.close();
        }

        return categories;
    }

    public Category getById(int categoryId) {
        Category category = null;

        SQLiteDatabase db = null;
        Cursor c = null;

        try {
            String getByIdCategorySQL = "SELECT * FROM category where id = " + categoryId;

            db = this.getWritableDatabase();
            c = db.rawQuery(getByIdCategorySQL, null);

            if (c.moveToFirst()) {
                int id = Integer.parseInt(c.getString(c.getColumnIndex(TABLE_CATEGORY_ID)));
                String name = c.getString(c.getColumnIndex(TABLE_CATEGORY_NAME));

                category = new Category(id, name);
            }
        } catch (SQLException e) {
            Log.wtf(MY_ERROR, e.getMessage());
        } finally {
            if (c != null)
                c.close();

            if (db != null)
                db.close();
        }

        return category;
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_CATEGORY_ID, category.getId());
            contentValues.put(TABLE_CATEGORY_NAME, category.getName());

            String where = "id = ?";
            String[] whereArgs = { String.valueOf(category.getId()) };

            if (db.update(TABLE_CATEGORY, contentValues, where, whereArgs) > 0) {
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

    public void removeCategory(int id) {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            String where = TABLE_CATEGORY_ID + "=?";
            String[] whereArgs = { String.valueOf(id) };

            db.delete(TABLE_USER, where, whereArgs);
        } catch (SQLException e) {
            Log.wtf(MY_ERROR, e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
    }

    // Category methods
    public boolean addOrUpdateCategory(Category category) {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_CATEGORY_ID, category.getId());
            contentValues.put(TABLE_CATEGORY_NAME, category.getName());

            if (db.insertWithOnConflict(TABLE_CATEGORY, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE) != -1) {
                return true;
            } else {
                String where = "id = ?";
                String[] whereArgs = { String.valueOf(category.getId()) };

                if (db.update(TABLE_CATEGORY, contentValues, where, whereArgs) > 0) {
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            Log.wtf(MY_ERROR, sqlException.getMessage());
        } finally {
            if (db != null)
                db.close();
        }

        return false;
    }
}
