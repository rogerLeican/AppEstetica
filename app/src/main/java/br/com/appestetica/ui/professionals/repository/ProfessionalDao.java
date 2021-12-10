package br.com.appestetica.ui.professionals.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.ui.DataBase;
import br.com.appestetica.ui.professionals.model.Professional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfessionalDao {
    private static final String PROFESSIONAL_TABLE = "professionals";

    public static void insert(Context context, Professional professional) {
        ContentValues values = new ContentValues();
        values.put("name", professional.getName());
        values.put("telephone", professional.getTelephone());
//      TODO COLOCAR LISTA DE DATA NO BANCO COM FIREBASE
        DataBase connection = new DataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.insert(PROFESSIONAL_TABLE, null, values);
    }

    public static void update(Context context, Professional professional) {

        ContentValues values = new ContentValues();
        values.put("name", professional.getName());
        values.put("telephone", professional.getTelephone());
        //      TODO COLOCAR LISTA DE DATA NO BANCO COM FIREBASE
        DataBase connection = new DataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.update(PROFESSIONAL_TABLE, values, "id = " + professional.getId(), null);

    }

    public static void delete(Context context, int idProfessional) {

        DataBase connection = new DataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.delete(PROFESSIONAL_TABLE, "id = " + idProfessional, null);
    }

    public static List<Professional> getProfessionals(Context context) {
        List<Professional> list = new ArrayList<>();

        DataBase connection = new DataBase(context);
        SQLiteDatabase database = connection.getReadableDatabase();

        try (Cursor cursor = database.rawQuery("SELECT * FROM professionals ORDER BY id", null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Professional professional = new Professional();
                    professional.setId(cursor.getInt(0));
                    professional.setName(cursor.getString(1));
                    professional.setTelephone(cursor.getString(2));
                    //      TODO COLOCAR LISTA DE DATA NO BANCO COM FIREBASE
                    list.add(professional);
                } while (cursor.moveToNext());
            }
            return list;
        }
    }

    public static Professional getProfessionalById(Context context, int professionalId) {

        DataBase connection = new DataBase(context);
        SQLiteDatabase database = connection.getReadableDatabase();

        try (Cursor cursor = database.rawQuery("SELECT * FROM professionals WHERE id = " + professionalId, null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Professional professional = new Professional();
                professional.setId(cursor.getInt(0));
                professional.setName(cursor.getString(1));
                professional.setTelephone(cursor.getString(2));
                //      TODO COLOCAR LISTA DE DATA NO BANCO COM FIREBASE
                return professional;
            }
            return null;
        }
    }
}
