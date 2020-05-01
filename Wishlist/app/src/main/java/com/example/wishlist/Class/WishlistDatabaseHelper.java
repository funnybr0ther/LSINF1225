package com.example.wishlist.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WishlistDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="wishlist.db";

    //PREMIER TABLE
    private static final String USER_TABLE_NAME_A="detailWishlist";
    public static final String USER_COL0_A="wishlistID";
    public static final String USER_COL1_A="productReference";
    public static final String USER_COL2_A="quantity";

    //DEUXIEME TABLE
    private static final String USER_TABLE_NAME_B="wishlist";
    public static final String USER_COL0_B="userID";
    public static final String USER_COL1_B="wishlistID";
    public static final String USER_COL2_B="name";

    public WishlistDatabaseHelper(@Nullable Context context) { super(context, DATABASE_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand_A="CREATE TABLE "+
                USER_TABLE_NAME_A + " ("+
                USER_COL0_A + " INTEGER REFERENCES wishlist (\"wishlistID\") NOT NULL, "+   // PAIRE UNIQUE
                USER_COL1_A + " INTEGER REFERENCES product (\"productReference\") NOT NULL, "+
                USER_COL2_A + " INTEGER DEFAULT (1))";
        db.execSQL(sqlCommand_A);

        String sqlCommand_B="CREATE TABLE "+
                USER_TABLE_NAME_B + " ("+
                USER_COL0_B + " INTEGER NOT NULL REFERENCES user (\"userID\"), "+
                USER_COL1_B + " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, "+
                USER_COL2_B + " TEXT NOT NULL)";
        db.execSQL(sqlCommand_B);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlCommand="DROP IF TABLE EXISTS " + DATABASE_NAME;
        onCreate(db);
    }

    //WISHLIST DETAIL
    public int[] getProducts(int wishlistID){
        SQLiteDatabase db=getReadableDatabase();
        String strSql = "SELECT " + USER_COL1_A + " FROM " + USER_TABLE_NAME_A + " WHERE " + USER_COL0_A + " = '" + wishlistID + "'";
        Cursor cursor = db.rawQuery(strSql, null);

        int[] productArray = new int[cursor.getCount()];

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int prod = cursor.getInt(cursor.getColumnIndex(USER_COL1_A));

            productArray[i] = prod;
            cursor.moveToNext();
            i++;
        }
        return productArray;
    }

    //WISHLIST DETAIL
    public int[] getQuantity(int wishlistID){
        SQLiteDatabase db=getReadableDatabase();
        String strSql = "SELECT " + USER_COL2_A + " FROM " + USER_TABLE_NAME_A + " WHERE " + USER_COL0_A + " = '" + wishlistID + "'";
        Cursor cursor = db.rawQuery(strSql, null);

        int[] quantityArray = new int[cursor.getCount()];

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int quantity = cursor.getInt(cursor.getColumnIndex(USER_COL1_A));

            quantityArray[i] = quantity;
            cursor.moveToNext();
            i++;
        }
        return quantityArray;
    }

    //WISHLIST
    public boolean addWishlist(String name, int userID){

        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL0_B,userID);
        contentValues.put(USER_COL2_B,name);
        long err=db.insert(USER_TABLE_NAME_B,null,contentValues);
        return err!=-1;
    }

    //WISHLIST
    public ArrayList<Wishlist> getUserWishlist(int userID){
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<Wishlist> wishlists = new ArrayList<Wishlist>();


        String strSql = "SELECT " + USER_COL1_B +", " + USER_COL2_B + " FROM " + USER_TABLE_NAME_B + " WHERE " + USER_COL0_B + " = '" + userID + "'";
        Cursor cursor = db.rawQuery(strSql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int wishlistID = cursor.getInt(cursor.getColumnIndex(USER_COL1_B));
            String name = cursor.getString(cursor.getColumnIndex(USER_COL2_B));
            int[] productArray = getProducts(wishlistID);
            int[] quantityArray = getProducts(wishlistID);

            Wishlist wishlist = new Wishlist(name,productArray.length, userID, productArray, quantityArray, wishlistID);

            wishlists.add(wishlist);
            cursor.moveToNext();
        }
        return wishlists;
    }
}