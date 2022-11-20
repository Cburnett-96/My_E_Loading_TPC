package com.aqp.mye_loading.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aqp.mye_loading.model.MustPromoList;
import com.aqp.mye_loading.model.PromoList;

import java.util.ArrayList;
import java.util.Objects;

public class DBHandlerMustPromo extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "mustpromodb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "mustPromo";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our name column
    private static final String TELCO_COL = "telecom";
    private static final String PROMO_COL = "promocode";
    private static final String PRICE_COL = "price";
    private static final String DESC_COL = "descriptions";

    // creating a constructor for our database handler.
    public DBHandlerMustPromo(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryMustPromo = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TELCO_COL + " TEXT,"
                + PROMO_COL + " TEXT,"
                + DESC_COL + " TEXT,"
                + PRICE_COL + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(queryMustPromo);
    }

    public void addMustPromo(String telecom, String promoCode, String Descriptions, int Price) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(TELCO_COL, telecom);
        values.put(PROMO_COL, promoCode);
        values.put(DESC_COL, Descriptions);
        values.put(PRICE_COL, Price);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    public ArrayList<MustPromoList> readMustPromo() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorPromo;
        ArrayList<MustPromoList> courseModalArrayList = new ArrayList<>();

        cursorPromo = db.rawQuery("SELECT * FROM ( SELECT * ,COUNT(*) AS cnt FROM mustPromo GROUP BY promocode) t ORDER BY t.cnt DESC", null);

        if (cursorPromo.moveToFirst()) {
            do {
                courseModalArrayList.add(new MustPromoList(cursorPromo.getInt(0),
                        cursorPromo.getString(2),
                        cursorPromo.getInt(4),
                        cursorPromo.getString(3)));
            } while (cursorPromo.moveToNext());
        }
        cursorPromo.close();
        return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
