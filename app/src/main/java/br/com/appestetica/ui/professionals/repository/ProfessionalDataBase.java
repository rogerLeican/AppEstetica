package br.com.appestetica.ui.professionals.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfessionalDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AppEstetica";
    private static final int VERSION = 1;

    public ProfessionalDataBase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS professionals (  " +
                "  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,  " +
                "  name TEXT NOT NULL , " +
                "  telephone TEXT NOT NULL ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
