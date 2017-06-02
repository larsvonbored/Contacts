package com.example.contactbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by me on 01.06.2017.
 */

public class ContactDataSource {

    private SQLiteDatabase db;
    private MySQLiteHelper helper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_LAST_NAME,
            MySQLiteHelper.COLUMN_NUMBER, MySQLiteHelper.COLUMN_EMAIL,MySQLiteHelper.COLUMN_PHOTO};

    public ContactDataSource(Context context, String user) {
        helper = new MySQLiteHelper(context, user);
    }

    public void open() throws SQLException {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public Contact createContact(String name, String lastName, String number, String email, String photo) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_LAST_NAME, lastName);
        values.put(MySQLiteHelper.COLUMN_NUMBER, number);
        values.put(MySQLiteHelper.COLUMN_EMAIL, email);
        values.put(MySQLiteHelper.COLUMN_PHOTO, photo);

        long insertId = db.insert(MySQLiteHelper.TABLE_CONTACTS, null,
                values);
        Cursor cursor = db.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        Contact newContact = null;
        if(cursor != null && cursor.moveToFirst()) {
            newContact = cursorToContact(cursor);
            cursor.close();
        }
        return newContact;
    }

    public Contact updateContact(String name, String lastName, String number, String email, String photo, long id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_LAST_NAME, lastName);
        values.put(MySQLiteHelper.COLUMN_NUMBER, number);
        values.put(MySQLiteHelper.COLUMN_EMAIL, email);
        values.put(MySQLiteHelper.COLUMN_PHOTO, photo);

        db.update(MySQLiteHelper.TABLE_CONTACTS, values,
                "_id=?", new String[]{String.valueOf(id)});

        Cursor cursor = db.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        Contact newContact = null;
        if(cursor != null && cursor.moveToFirst()) {
            newContact = cursorToContact(cursor);
            cursor.close();
        }
        return newContact;
    }

    public void deleteContact(long contactId) {
        db.delete(MySQLiteHelper.TABLE_CONTACTS, MySQLiteHelper.COLUMN_ID
                + " = " + contactId, null);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        Cursor cursor = db.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, null, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
        while (!cursor.isAfterLast()) {
            Contact contact = cursorToContact(cursor);
            contacts.add(contact);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        }
        return contacts;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setId(cursor.getLong(0));
        contact.setName(cursor.getString(1));
        contact.setLastName(cursor.getString(2));
        contact.setNumber(cursor.getString(3));
        contact.setEmail(cursor.getString(4));
        contact.setPhoto(cursor.getString(5));
        /*byte[] photoInBytes = cursor.getBlob(5);
        contact.setPhoto(DbBitmapUtility.getImage(photoInBytes));*/
        return contact;
    }
}
