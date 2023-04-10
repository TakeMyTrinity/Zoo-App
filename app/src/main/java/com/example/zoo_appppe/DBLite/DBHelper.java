package com.example.zoo_appppe.DBLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "login.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "login";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_MATRICULE = "matricule";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_ADRESSE = "adresse";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_IDENTIFIANT = "identifiant";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_ISADMIN = "isAdmin";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER + " TEXT,"
                    + COLUMN_MATRICULE + " TEXT,"
                    + COLUMN_NOM + " TEXT,"
                    + COLUMN_PRENOM + " TEXT,"
                    + COLUMN_ADRESSE + " TEXT,"
                    + COLUMN_TELEPHONE + " TEXT,"
                    + COLUMN_IDENTIFIANT + " TEXT,"
                    + COLUMN_PASSWORD + " TEXT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_ISADMIN + " INTEGER"
                    + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
