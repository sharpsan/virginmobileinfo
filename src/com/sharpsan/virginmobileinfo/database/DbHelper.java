package com.sharpsan.virginmobileinfo.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharpsan.virginmobileinfo.database.Contract.LoginDataEntry;
import com.sharpsan.virginmobileinfo.database.Contract.UserDataEntry;

public class DbHelper extends SQLiteOpenHelper {

	private ArrayList<String> create_tables = new ArrayList<String>();
	private ArrayList<String> delete_tables = new ArrayList<String>();

	// Global
	public static final String _ID = "_id";
	public static final String _NULLABLE = "error";
	public static final String TEXT_TYPE = " TEXT";
	public static final String COMMA_SEP = ", ";

	/** UserData.db vars */
	public static final int DATABASE_USER_DATA_VERSION = 1;
	public static final String DATABASE_USER_DATA_NAME = "UserData.db";
	//@formatter:off
	public static final String SQL_CREATE_TABLE_PROFILE =
			"CREATE TABLE IF NOT EXISTS " + UserDataEntry.TABLE_PROFILE + "( " +
					_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					UserDataEntry.USER_DATA_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_BALANCE + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_TOTAL + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_START_DATE + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_MINS_USED + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_MINS_LEFT + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_ACCOUNT_STATUS + TEXT_TYPE + " )";
	public static final String SQL_CREATE_TABLE_EXTRA_DATA =
			"CREATE TABLE IF NOT EXISTS " + UserDataEntry.TABLE_EXTRA_DATA + "( " +
					_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					UserDataEntry.USER_DATA_PROFILE_ID + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_DATE_SAVED + TEXT_TYPE + COMMA_SEP +
					UserDataEntry.USER_DATA_DOWNLOAD_RATE + TEXT_TYPE + " )";
	public static final String SQL_DELETE_TABLE_PROFILE = "DROP TABLE IF EXISTS " + UserDataEntry.TABLE_PROFILE;
	public static final String SQL_DELETE_TABLE_EXTRA_DATA = "DROP TABLE IF EXISTS " + UserDataEntry.TABLE_EXTRA_DATA;
	public static final String SQL_DELETE_SQLITE_SEQUENCE_PROFILE = "DELETE FROM sqlite_sequence WHERE name=" + UserDataEntry.TABLE_PROFILE;
	public static final String SQL_DELETE_SQLITE_SEQUENCE_EXTRA_DATA = "DELETE FROM sqlite_sequence WHERE name=" + UserDataEntry.TABLE_EXTRA_DATA;
	//@formatter:on

	/** LoginData.db vars */
	public static final int DATABASE_LOGIN_VERSION = 1;
	public static final String DATABASE_LOGIN_NAME = "LoginData.db";
	//@formatter:off
	public static final String SQL_CREATE_TABLE_LOGIN=
			"CREATE TABLE IF NOT EXISTS " + LoginDataEntry.TABLE_LOGIN + "( " +
					_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					LoginDataEntry.LOGIN_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
					LoginDataEntry.LOGIN_PASSWORD + TEXT_TYPE + COMMA_SEP +
					LoginDataEntry.LOGIN_DATE_SAVED + " )";
	public static final String SQL_DELETE_TABLE_LOGIN = "DROP TABLE IF EXISTS " + LoginDataEntry.TABLE_LOGIN;
	public static final String SQL_DELETE_SQLITE_SEQUENCE_LOGIN = "DELETE FROM sqlite_sequence WHERE name=" + LoginDataEntry.TABLE_LOGIN;
	//@formatter:on

	public DbHelper(Context context, String db, int version) {
		super(context, db, null, version);

		switch (db) {
			case DATABASE_USER_DATA_NAME:
				create_tables.add(SQL_CREATE_TABLE_PROFILE);
				create_tables.add(SQL_CREATE_TABLE_EXTRA_DATA);
				delete_tables.add(SQL_DELETE_TABLE_PROFILE);
				delete_tables.add(SQL_DELETE_TABLE_EXTRA_DATA);
				delete_tables.add(SQL_DELETE_SQLITE_SEQUENCE_PROFILE);
				delete_tables.add(SQL_DELETE_SQLITE_SEQUENCE_EXTRA_DATA);
				break;
			case DATABASE_LOGIN_NAME:
				create_tables.add(SQL_CREATE_TABLE_LOGIN);
				delete_tables.add(SQL_DELETE_TABLE_LOGIN);
				delete_tables.add(SQL_DELETE_SQLITE_SEQUENCE_LOGIN);
			default:
				return;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String create_table : create_tables) {
			db.execSQL(create_table);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String delete_table : delete_tables) {
			db.execSQL(delete_table);
			onCreate(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
