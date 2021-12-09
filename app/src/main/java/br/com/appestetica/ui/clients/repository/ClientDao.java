package br.com.appestetica.ui.clients.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.ui.clients.model.Client;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientDao {

    private static final String CLIENT_TABLE = "clients";

    public static void insert(Context context, Client client) {

        ContentValues values = new ContentValues();
        values.put("name", client.getName());
        values.put("telephone", client.getTelephone());
        values.put("email", client.getEmail());
        ClientDataBase connection = new ClientDataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.insert(CLIENT_TABLE, null, values);
    }

    public static void update(Context context, Client client) {

        ContentValues values = new ContentValues();
        values.put("name", client.getName());
        values.put("telephone", client.getTelephone());
        values.put("email", client.getEmail());
        ClientDataBase connection = new ClientDataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.update(CLIENT_TABLE, values, "id = " + client.getId(), null);

    }

    public static void delete(Context context, int idClient) {

        ClientDataBase connection = new ClientDataBase(context);
        SQLiteDatabase database = connection.getWritableDatabase();

        database.delete(CLIENT_TABLE, "id = " + idClient, null);
    }

    public static List<Client> getClients(Context context) {
        List<Client> list = new ArrayList<>();

        ClientDataBase connection = new ClientDataBase(context);
        SQLiteDatabase database = connection.getReadableDatabase();

       try( Cursor cursor = database.rawQuery("SELECT * FROM clients ORDER BY id", null)){
           if (cursor.getCount() > 0) {
               cursor.moveToFirst();
               do {
                   Client client = new Client();
                   client.setId(cursor.getInt(0));
                   client.setName(cursor.getString(1));
                   client.setTelephone(cursor.getString(2));
                   client.setEmail(cursor.getString(3));
                   list.add(client);
               } while (cursor.moveToNext());
           }
           return list;
       }
    }

    public static Client getClientById(Context context, int clientId) {

        ClientDataBase connection = new ClientDataBase(context);
        SQLiteDatabase database = connection.getReadableDatabase();

        try(Cursor cursor = database.rawQuery("SELECT * FROM clients WHERE id = " + clientId, null)){
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Client client = new Client();
                client.setId(cursor.getInt(0));
                client.setName(cursor.getString(1));
                client.setTelephone(cursor.getString(2));
                client.setEmail(cursor.getString(3));
                return client;
            }
            return null;
        }
    }
}
