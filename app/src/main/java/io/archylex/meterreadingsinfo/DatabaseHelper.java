package io.archylex.meterreadingsinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String db_name = "services.db";
    private static final int db_version = 1;

    public DatabaseHelper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MeterService.create_table);
        db.execSQL(InfoService.create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MeterService.table_name);
        db.execSQL("DROP TABLE IF EXISTS " + InfoService.table_name);
        onCreate(db);
    }

    public long insertService(MeterService ms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MeterService.column_organization, ms.getOrganization());
        values.put(MeterService.column_login, ms.getLogin());
        values.put(MeterService.column_password, ms.getPassword());
        values.put(MeterService.column_image_id, ms.getImageId());
        values.put(MeterService.column_enabled, ms.getEnabled());

        long id = db.insert(MeterService.table_name, null, values);

        db.close();

        return id;
    }

    public MeterService getMeterService(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MeterService.table_name,
                new String[]{ MeterService.column_id, MeterService.column_organization, MeterService.column_login,
                        MeterService.column_password, MeterService.column_image_id, MeterService.column_enabled },
                MeterService.column_id + " = ?",
                new String[]{ String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        MeterService ms = new MeterService(
                cursor.getInt(cursor.getColumnIndex(MeterService.column_id)),
                cursor.getString(cursor.getColumnIndex(MeterService.column_organization)),
                cursor.getString(cursor.getColumnIndex(MeterService.column_login)),
                cursor.getString(cursor.getColumnIndex(MeterService.column_password)),
                cursor.getInt(cursor.getColumnIndex(MeterService.column_image_id)),
                cursor.getInt(cursor.getColumnIndex(MeterService.column_enabled)));

        cursor.close();

        return ms;
    }

    public List<MeterService> getMeterServices() {
        List<MeterService> services = new ArrayList<>();

        String query = "SELECT * FROM " + MeterService.table_name;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MeterService ms = new MeterService();
                ms.setId(cursor.getInt(cursor.getColumnIndex(MeterService.column_id)));
                ms.setOrganization(cursor.getString(cursor.getColumnIndex(MeterService.column_organization)));
                ms.setLogin(cursor.getString(cursor.getColumnIndex(MeterService.column_login)));
                ms.setPassword(cursor.getString(cursor.getColumnIndex(MeterService.column_password)));
                ms.setImageId(cursor.getInt(cursor.getColumnIndex(MeterService.column_image_id)));
                ms.setEnabled(cursor.getInt(cursor.getColumnIndex(MeterService.column_enabled)));

                services.add(ms);
            } while (cursor.moveToNext());
        }

        db.close();

        return services;
    }

    public int getServicesCount() {
        String query = "SELECT * FROM " + MeterService.table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    public int updateService(MeterService ms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MeterService.column_organization, ms.getOrganization());
        values.put(MeterService.column_login, ms.getLogin());
        values.put(MeterService.column_password, ms.getPassword());
        values.put(MeterService.column_image_id, ms.getImageId());
        values.put(MeterService.column_enabled, ms.getEnabled());

        return db.update(MeterService.table_name, values, MeterService.column_id + " = ?",
                new String[]{ String.valueOf(ms.getId()) });
    }

    public void deleteService(MeterService ms) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MeterService.table_name, MeterService.column_id + " = ?",
                new String[]{ String.valueOf(ms.getId()) });

        db.close();
    }

    public void deleteInfo(MeterService ms) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(InfoService.table_name, InfoService.column_service_id + " = ?",
                new String[]{ String.valueOf(ms.getId()) });

        db.close();
    }


    public long insertInfo(InfoService is) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(InfoService.column_organization, is.getOrganization());
        values.put(InfoService.column_address, is.getSubscriberAddress());
        values.put(InfoService.column_subscriber_number, is.getSubscriberNumber());
        values.put(InfoService.column_subscriber_name, is.getSubscriberName());
        values.put(InfoService.column_date, is.getDate());
        values.put(InfoService.column_last_reading, is.getLastReading());
        values.put(InfoService.column_saldo, is.getSaldo());
        values.put(InfoService.column_image_id, is.getImageId());
        values.put(InfoService.column_service_id, is.getServiceId());
        values.put(InfoService.column_online, is.getOnline() ? 1 : 0);

        long id = db.insert(InfoService.table_name, null, values);

        db.close();

        return id;
    }

    public List<InfoService> getInfoServices() {
        List<InfoService> info = new ArrayList<>();

        String query = "SELECT * FROM " + InfoService.table_name;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                InfoService is = new InfoService();
                is.setId(cursor.getInt(cursor.getColumnIndex(InfoService.column_id)));
                is.setOrganization(cursor.getString(cursor.getColumnIndex(InfoService.column_organization)));
                is.setSubscriberName(cursor.getString(cursor.getColumnIndex(InfoService.column_subscriber_name)));
                is.setSubscriberNumber(cursor.getString(cursor.getColumnIndex(InfoService.column_subscriber_number)));
                is.setSubscriberAddress(cursor.getString(cursor.getColumnIndex(InfoService.column_address)));
                is.setSaldo(cursor.getString(cursor.getColumnIndex(InfoService.column_saldo)));
                is.setDate(cursor.getString(cursor.getColumnIndex(InfoService.column_date)));
                is.setLastReading(cursor.getString(cursor.getColumnIndex(InfoService.column_last_reading)));
                is.setImageId(cursor.getInt(cursor.getColumnIndex(InfoService.column_image_id)));
                is.setServiceId(cursor.getInt(cursor.getColumnIndex(InfoService.column_service_id)));
                int online = cursor.getInt(cursor.getColumnIndex(InfoService.column_online));
                is.setOnline(cursor.getInt(cursor.getColumnIndex(InfoService.column_online)) == 1 ? true : false);

                info.add(is);
            } while (cursor.moveToNext());
        }

        db.close();

        return info;
    }

    public boolean isInfoExists(long service_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + InfoService.table_name + " WHERE " + InfoService.column_service_id + "=" + String.valueOf(service_id);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getInfoCount() {
        String query = "SELECT * FROM " + InfoService.table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    public int updateInfo(InfoService is) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(InfoService.column_organization, is.getOrganization());
        values.put(InfoService.column_address, is.getSubscriberAddress());
        values.put(InfoService.column_subscriber_number, is.getSubscriberNumber());
        values.put(InfoService.column_subscriber_name, is.getSubscriberName());
        values.put(InfoService.column_date, is.getDate());
        values.put(InfoService.column_last_reading, is.getLastReading());
        values.put(InfoService.column_saldo, is.getSaldo());
        values.put(InfoService.column_image_id, is.getImageId());
        values.put(InfoService.column_service_id, is.getServiceId());
        values.put(InfoService.column_online, is.getOnline() ? 1 : 0);

        return db.update(InfoService.table_name, values, InfoService.column_service_id + " = ?",
                new String[]{ String.valueOf(is.getServiceId()) });
    }

    public void setOfflineInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + InfoService.table_name + " SET " + InfoService.column_online + " =  0");
    }
}