package com.sharpsan.virginmobileinfo.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import com.sharpsan.virginmobileinfo.R;
import com.sharpsan.virginmobileinfo.database.Contract.LoginDataEntry;
import com.sharpsan.virginmobileinfo.database.DbHelper;
import com.sharpsan.virginmobileinfo.utils.DateUtils;

public class LoginAdapter {

	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Context mContext;

	public LoginAdapter(Context context) {
		this.mContext = context;
	}

	public LoginAdapter open() throws SQLException {
		mDbHelper = new DbHelper(mContext, DbHelper.DATABASE_LOGIN_NAME, DbHelper.DATABASE_LOGIN_VERSION);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public long createEntry(String[] data) {
		String remove_anything_that_is_not_a_digit = "\\D";
		String new_phone_number = data[0].replaceAll(remove_anything_that_is_not_a_digit, "");
		String new_password = data[1].replaceAll(remove_anything_that_is_not_a_digit, "");
		String new_date_saved = data[2];

		if (!new_phone_number.equals("") && !new_password.equals("")) {
			new_date_saved = DateUtils.format(new_date_saved, 1);
			ContentValues values = new ContentValues();

			values.put(LoginDataEntry.LOGIN_PHONE_NUMBER, new_phone_number);
			values.put(LoginDataEntry.LOGIN_PASSWORD, new_password);
			values.put(LoginDataEntry.LOGIN_DATE_SAVED, new_date_saved);

			mDb.delete(LoginDataEntry.TABLE_LOGIN, null, null);
			return mDb.insert(LoginDataEntry.TABLE_LOGIN, DbHelper._NULLABLE, values);
		} else {
			return 0;
		}
	}

	public void emptyDatabase() {
		mDb.delete(LoginDataEntry.TABLE_LOGIN, null, null);
	}

	public Cursor fetchLastEntry() throws SQLException, CursorIndexOutOfBoundsException {
		//@formatter:off
		String[] projection =
			{
				DbHelper._ID,
				LoginDataEntry.LOGIN_PHONE_NUMBER,
				LoginDataEntry.LOGIN_PASSWORD,
				LoginDataEntry.LOGIN_DATE_SAVED
			};
		//@formatter:on

		// String sortOrder = DbHelper._ID + " DESC";
		Cursor cursor = mDb.query(LoginDataEntry.TABLE_LOGIN, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null, // The sort order
				null // Limit to 1
				);

		if (cursor != null) {
			cursor.moveToLast();
		}

		return cursor;
	}

	public void bind(Context context, View view, Cursor cursor) throws CursorIndexOutOfBoundsException {
		String save_phone_number = cursor.getString(cursor.getColumnIndex(LoginDataEntry.LOGIN_PHONE_NUMBER));
		TextView txt_phone_number = (TextView) view.findViewById(R.id.pref_edittxt_phone_number);
		txt_phone_number.setText(save_phone_number);

		String save_password = cursor.getString(cursor.getColumnIndex(LoginDataEntry.LOGIN_PASSWORD));
		TextView txt_password = (TextView) view.findViewById(R.id.pref_edittxt_password);
		txt_password.setText(save_password);
	}

}
