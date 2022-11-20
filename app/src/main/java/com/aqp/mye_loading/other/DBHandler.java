package com.aqp.mye_loading.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aqp.mye_loading.model.PromoList;

import java.util.ArrayList;
import java.util.Objects;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "promodb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "mypromo";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our name column
    private static final String TELCO_COL = "telecom";
    private static final String PROMO_COL = "promocode";
    private static final String SMS_COL = "sms";
    private static final String CALL_COL = "call";
    private static final String DATA_COL = "data";
    private static final String VALIDITY_COL = "validity";
    private static final String PRICE_COL = "price";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TELCO_COL + " TEXT,"
                + PROMO_COL + " TEXT,"
                + SMS_COL + " TEXT,"
                + CALL_COL + " TEXT,"
                + DATA_COL + " TEXT,"
                + VALIDITY_COL + " TEXT,"
                + PRICE_COL + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewPromo(String telecom, String promoCode, String SMS, String Call, String Data, String Validity, int Price) {

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
        values.put(SMS_COL, SMS);
        values.put(CALL_COL, Call);
        values.put(DATA_COL, Data);
        values.put(VALIDITY_COL, Validity);
        values.put(PRICE_COL, Price);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    public ArrayList<PromoList> readPromo(String telco) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorPromo;
        ArrayList<PromoList> courseModalArrayList = new ArrayList<>();

        if (Objects.equals(telco, "TM")){
            cursorPromo = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE telecom = 'TM'", null);
        } else if (Objects.equals(telco, "Globe")){
            cursorPromo = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE telecom = 'Globe'", null);
        } else if (Objects.equals(telco, "TNT")){
            cursorPromo = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE telecom = 'TNT'", null);
        } else if (Objects.equals(telco, "Smart")){
            cursorPromo = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE telecom = 'Smart'", null);
        } else {
            cursorPromo = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        }

        if (cursorPromo.moveToFirst()) {
            do {
                courseModalArrayList.add(new PromoList(cursorPromo.getInt(0),
                        cursorPromo.getString(2),
                        cursorPromo.getInt(7),
                        cursorPromo.getString(3),
                        cursorPromo.getString(4),
                        cursorPromo.getString(5),
                        cursorPromo.getString(6)));
            } while (cursorPromo.moveToNext());
        }
        System.out.println("SIZE COUNT: "+cursorPromo.getCount());
        cursorPromo.close();
        return courseModalArrayList;
    }

    public void deleteHandler(int ID) {

        String query = "Select*FROM " + TABLE_NAME + " WHERE " + ID_COL + " = '" + ID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PromoList promo = new PromoList();
        if (cursor.moveToFirst()) {
            promo.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, ID_COL + "=?",
                    new String[] {
                            String.valueOf(promo.getId())
                    });
            System.out.println("DELETED!!");
            cursor.close();
        }
        System.out.println("TEST!!"+query);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
