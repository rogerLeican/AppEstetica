package br.com.appestetica.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppEstetica";
    private static final int VERSION = 2;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String clientsTable = "CREATE TABLE IF NOT EXISTS clients (  " +
                "  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,  " +
                "  name TEXT NOT NULL , " +
                "  telephone TEXT NOT NULL , " +
                "  email TEXT ) ";

        String professionalTable = "CREATE TABLE IF NOT EXISTS professionals (  " +
                "  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,  " +
                "  name TEXT NOT NULL , " +
                "  telephone TEXT NOT NULL ) ";

        sqLiteDatabase.execSQL(professionalTable);
        sqLiteDatabase.execSQL(clientsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS clients");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS professionals");
        onCreate(sqLiteDatabase);
    }
}
