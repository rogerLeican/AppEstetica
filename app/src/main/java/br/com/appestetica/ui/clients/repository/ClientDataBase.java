package br.com.appestetica.ui.clients.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClientDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppPersist";
    private static final int VERSION = 1;

    public ClientDataBase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS clients (  " +
                "  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,  " +
                "  name TEXT NOT NULL , " +
                "  telephone TEXT NOT NULL , " +
                "  email TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
