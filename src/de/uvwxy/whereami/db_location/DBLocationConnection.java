package de.uvwxy.whereami.db_location;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
			DBLocation.COL8_SPD, //
			DBLocation.COL9_FAV };

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

	public long addEntry(de.uvwxy.whereami.db_location.Location entry) {
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
		values.put(DBLocation.COL9_FAV, 0);

		long ret = db.insert(DBLocation.DB_TABLE_NAME, null, values);
		Log.d("DB", "Insertion returned " + ret);
		return ret;
	}

	public void deleteEntry(de.uvwxy.whereami.db_location.Location entry) {
		long id = entry.getTime();
		db.delete(DBLocation.DB_TABLE_NAME, DBLocation.COL0_TIMESTAMP + " = " + id, null);
		Log.d("DB", "Deleted " + id);
	}

	/**
	 * Obtain all entries from the DB. The input list can not be null.
	 * 
	 * @param list
	 * @param all
	 * @param fav
	 */
	public void getAllEntries(ArrayList<de.uvwxy.whereami.db_location.Location> list, boolean all, boolean fav) {
		list.clear();

		Cursor cursor = null;
		if (all) {
			cursor = db.query(DBLocation.DB_TABLE_NAME, dbColumns, null, null, null, null, null);
		} else {
			cursor = db.query(DBLocation.DB_TABLE_NAME, dbColumns, DBLocation.COL9_FAV + "=" + (fav ? "1" : "0"), null,
					null, null, null);
		}

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			de.uvwxy.whereami.db_location.Location temp = cursorToDBEntry(cursor);
			list.add(temp);
			cursor.moveToNext();
		}
		cursor.close();
	}

	public void toggleFavorite(Location loc) {
		Cursor cursor = db.query(DBLocation.DB_TABLE_NAME, //
				dbColumns, DBLocation.COL0_TIMESTAMP + "=" + loc.getTime(), null, null, null, null);
		cursor.moveToFirst();
		int isFav = cursor.getInt(cursor.getColumnIndex(DBLocation.COL9_FAV));
		cursor.close();
		ContentValues args = new ContentValues();
		args.put(DBLocation.COL9_FAV, isFav == 0 ? 1 : 0);
		db.update(DBLocation.DB_TABLE_NAME, args, DBLocation.COL0_TIMESTAMP + "=" + loc.getTime(), null);
	}

	public void rename(Location loc, String newName) {
		ContentValues args = new ContentValues();
		args.put(DBLocation.COL2_NAME, newName);
		db.update(DBLocation.DB_TABLE_NAME, args, DBLocation.COL0_TIMESTAMP + "=" + loc.getTime(), null);
	}

	private de.uvwxy.whereami.db_location.Location cursorToDBEntry(Cursor c) {
		de.uvwxy.whereami.db_location.Location temp = new de.uvwxy.whereami.db_location.Location();
		temp.setTime(c.getLong(0));
		temp.setProvider(c.getString(1));
		temp.setName(c.getString(2));
		temp.setLatitude(c.getDouble(3));
		temp.setLongitude(c.getDouble(4));
		temp.setAltitude(c.getDouble(5));
		temp.setBearing(c.getDouble(6));
		temp.setAccuracy(c.getDouble(7));
		temp.setSpeed(c.getDouble(8));
		return temp;
	}

}