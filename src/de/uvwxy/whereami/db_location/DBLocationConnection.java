package de.uvwxy.whereami.db_location;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.uvwxy.whereami.proto.Messages;

public class DBLocationConnection {
	private SQLiteDatabase db;
	private DBLocation dbOpenWrapper;
	private String[] dbColumns = { //
	DBLocation.COL0_TIMESTAMP, //
			DBLocation.COL1_PROVIDER, //
			DBLocation.COL2_NAME, //
			DBLocation.COL3_LAT, //
			DBLocation.COL4_LON, //
			DBLocation.COL5_ALT, //
			DBLocation.COL6_BER, //
			DBLocation.COL7_ACC, //
			DBLocation.COL8_SPD,
			};

	public DBLocationConnection(Context context) {
		dbOpenWrapper = new DBLocation(context);
	}

	public void openWrite() {
		db = dbOpenWrapper.getWritableDatabase();
	}

	public void openReadOnly() {
		db = dbOpenWrapper.getReadableDatabase();
	}

	public void close() {
		db.close();
	}

	public long addEntry(Messages.Location entry) {
		ContentValues values = new ContentValues();

		values.put(DBLocation.COL0_TIMESTAMP, entry.getTime());
		values.put(DBLocation.COL1_PROVIDER, entry.getProvider());
		values.put(DBLocation.COL2_NAME, entry.getName());
		values.put(DBLocation.COL3_LAT, entry.getLatitude());
		values.put(DBLocation.COL4_LON, entry.getLongitude());
		values.put(DBLocation.COL5_ALT, entry.getAltitude());
		values.put(DBLocation.COL6_BER, entry.getBearing());
		values.put(DBLocation.COL7_ACC, entry.getAccuracy());
		values.put(DBLocation.COL8_SPD, entry.getSpeed());
		values.put(DBLocation.COL9_FAV, false);
		
		long ret = db.insert(DBLocation.DB_TABLE_NAME, null, values);
		Log.d("DB", "Insertion returned " + ret);
		return ret;
	}

	public void deleteEntry(Messages.Location entry) {
		long id = entry.getTime();
		db.delete(DBLocation.DB_TABLE_NAME, DBLocation.COL0_TIMESTAMP + " = " + id, null);
		Log.d("DB", "Deleted " + id);
	}

	public void getAllEntries(ArrayList<Messages.Location> list) {
		list.clear();

		Cursor cursor = db.query(DBLocation.DB_TABLE_NAME, dbColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Messages.Location temp = cursorToDBEntry(cursor);
			list.add(temp);
			cursor.moveToNext();
		}
		cursor.close();
	}

	private Messages.Location cursorToDBEntry(Cursor c) {
		Messages.Location.Builder temp = Messages.Location.newBuilder();
		temp.setTime(c.getLong(0));
		temp.setProvider(c.getString(1));
		temp.setName(c.getString(2));
		temp.setLatitude(c.getDouble(3));
		temp.setLongitude(c.getDouble(4));
		temp.setAltitude(c.getDouble(5));
		temp.setBearing(c.getDouble(6));
		temp.setAccuracy(c.getDouble(7));
		temp.setSpeed(c.getDouble(8));
		return temp.build();
	}

}