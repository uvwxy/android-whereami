package de.uvwxy.whereami.db_location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBLocation extends SQLiteOpenHelper {
	private static final String DB_NAME = "locations.db";
	private static final int DB_VERSION = 2;

	//	required double latitude = 1;
	//  required double longitude = 2;
	//  required double altitude = 3;
	//  required double bearing = 4;
	//  required double accuracy = 5;
	//  required string provider = 6;
	//  required double speed = 7;
	//  required int64 time = 8;
	//  required string name = 1000;

	public static String DB_TABLE_NAME = "location_table";
	public static String COL0_TIMESTAMP = "_id";
	public static String COL1_PROVIDER = "provider";
	public static String COL2_NAME = "name";
	public static String COL3_LAT = "latitude";
	public static String COL4_LON = "longitude";
	public static String COL5_ALT = "altitude";
	public static String COL6_BER = "bearing";
	public static String COL7_ACC = "accuracy";
	public static String COL8_SPD = "speed";
	public static String COL9_FAV = "favourite";

	private String CREATE_DB = "create table " + DB_TABLE_NAME + "(" //
			+ COL0_TIMESTAMP + " long primary key, " //
			+ COL1_PROVIDER + " text not null, " //
			+ COL2_NAME + " text not null, " //
			+ COL3_LAT + " double not null, "//
			+ COL4_LON + " double not null, "//
			+ COL5_ALT + " double not null, "//
			+ COL6_BER + " double not null, "//
			+ COL7_ACC + " double not null, "//
			+ COL8_SPD + " double not null, "//
			+ COL9_FAV + " integer not null);";

	public DBLocation(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DB);
	}

	/**
	 * Deletes the table on upgrade and crates a new empty table by calling
	 * onCreate(db).
	 * 
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
		onCreate(db);
	}

}
