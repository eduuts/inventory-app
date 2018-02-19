package edu.simpleinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edu on 7/19/2017.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " (" +
                    ItemContract.ItemEntry._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ItemContract.ItemEntry.COLUMN_ITEM_NAME + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    ItemContract.ItemEntry.COLUMN_ITEM_COUNT + INT_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    ItemContract.ItemEntry.COLUMN_ITEM_PRICE + REAL_TYPE + " NOT NULL DEFAULT 0.00" + COMMA_SEP +
                    ItemContract.ItemEntry.COLUMN_ITEM_IMAGE + BLOB_TYPE +
                    ")";

    private static final String SQL_DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL(SQL_DELETE_ITEMS_TABLE);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
