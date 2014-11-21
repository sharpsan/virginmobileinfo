package com.sharpsan.virginmobileinfo.profile;

import java.text.ParseException;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sharpsan.virginmobileinfo.R;
import com.sharpsan.virginmobileinfo.database.Contract.UserDataEntry;
import com.sharpsan.virginmobileinfo.database.DbHelper;
import com.sharpsan.virginmobileinfo.utils.DateUtils;
import com.sharpsan.virginmobileinfo.utils.FormatPhoneNumber;
import com.sharpsan.virginmobileinfo.utils.ProgressBarColor;

public class AccountAdapter {

	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Context mContext;

	public AccountAdapter(Context context) {
		this.mContext = context;
	}

	public AccountAdapter open() throws SQLException {
		mDbHelper = new DbHelper(mContext, DbHelper.DATABASE_USER_DATA_NAME, DbHelper.DATABASE_USER_DATA_VERSION);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public void createEntry(String[] data, String[] extra) {

		/* Perform variable changes */
		data[4] = DateUtils.format(data[4], 1); // Convert start_date
		String date_saved = DateUtils.format(DateUtils.getCurrentDate(), 1);

		ContentValues values = new ContentValues();
		values.put(UserDataEntry.USER_DATA_FIRST_NAME, data[0]);
		values.put(UserDataEntry.USER_DATA_PHONE_NUMBER, data[1]);
		values.put(UserDataEntry.USER_DATA_BALANCE, data[2]);
		values.put(UserDataEntry.USER_DATA_TOTAL, data[3]);
		values.put(UserDataEntry.USER_DATA_START_DATE, data[4]);
		values.put(UserDataEntry.USER_DATA_MINS_USED, data[5]);
		values.put(UserDataEntry.USER_DATA_MINS_LEFT, data[6]);
		values.put(UserDataEntry.USER_DATA_ACCOUNT_STATUS, data[7]);
		// mDb.rawQuery(DbHelper.SQL_DELETE_TABLE_PROFILE, null);
		//mDb.delete(UserDataEntry.TABLE_PROFILE, null, null); //this is commented to continually add data
		long profile_id = mDb.insert(UserDataEntry.TABLE_PROFILE, DbHelper._NULLABLE, values);

		ContentValues values_extra = new ContentValues();
		values_extra.put(UserDataEntry.USER_DATA_PROFILE_ID, profile_id);
		values_extra.put(UserDataEntry.USER_DATA_DATE_SAVED, date_saved);
		values_extra.put(UserDataEntry.USER_DATA_DOWNLOAD_RATE, extra[0]);

		mDb.delete(UserDataEntry.TABLE_EXTRA_DATA, null, null); // TODO in later versions, we'll actually use this instead of emptying it...
		mDb.insert(UserDataEntry.TABLE_EXTRA_DATA, DbHelper._NULLABLE, values_extra);
	}

	public Cursor fetchLastEntry() throws SQLException, CursorIndexOutOfBoundsException {
		Cursor cursor = null;

		String query = "SELECT * FROM " + UserDataEntry.TABLE_PROFILE + ", " + UserDataEntry.TABLE_EXTRA_DATA + " WHERE " + UserDataEntry.TABLE_PROFILE + "."
				+ DbHelper._ID + " = " + UserDataEntry.TABLE_EXTRA_DATA + "." + UserDataEntry.USER_DATA_PROFILE_ID + " ORDER BY " + DbHelper._ID
				+ " DESC LIMIT 1";

		try {
			cursor = mDb.rawQuery(query, null);

			if (cursor != null) {
				cursor.moveToLast();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return cursor;
	}

	public void bind(Context context, View view_parent, Cursor cursor, ActionBar action_bar) throws ParseException, CursorIndexOutOfBoundsException {
		/* Save values (some won't be shown) */
		// String save_first_name = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_FIRST_NAME));
		String save_phone_number = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_PHONE_NUMBER));
		String save_balance = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_BALANCE));
		String save_total = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_TOTAL));
		String save_start_date = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_START_DATE));
		String save_date_saved = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_DATE_SAVED));
		int save_mins_used = cursor.getInt(cursor.getColumnIndex(UserDataEntry.USER_DATA_MINS_USED));
		int save_mins_left = cursor.getInt(cursor.getColumnIndex(UserDataEntry.USER_DATA_MINS_LEFT));
		String save_account_status = cursor.getString(cursor.getColumnIndex(UserDataEntry.USER_DATA_ACCOUNT_STATUS));

		/* TextViews to load label data into */
		TextView label_mins_usedleft = (TextView) view_parent.findViewById(R.id.label_mins_usedleft);

		/* TextView to load last saved data into */
		TextView txt_last_updated = (TextView) view_parent.findViewById(R.id.txt_last_updated);

		/* TextViews to load save data into */
		// TextView txt_first_name = (TextView) view_parent.findViewById(R.id.txt_first_name);
		// TextView txt_phone_number = (TextView) view_parent.findViewById(R.id.txt_phone_number);
		TextView txt_account_status = (TextView) view_parent.findViewById(R.id.txt_account_status);
		TextView txt_total = (TextView) view_parent.findViewById(R.id.txt_total);
		TextView txt_balance = (TextView) view_parent.findViewById(R.id.txt_balance);
		TextView txt_mins_usedleft = (TextView) view_parent.findViewById(R.id.txt_mins_usedleft);
		TextView txt_mins_total = (TextView) view_parent.findViewById(R.id.txt_mins_total);
		TextView txt_start_date = (TextView) view_parent.findViewById(R.id.txt_start_date);
		ProgressBar progress_bar_mins_usedleft = (ProgressBar) view_parent.findViewById(R.id.progressBar1);
		ProgressBar progress_bar_next_payment = (ProgressBar) view_parent.findViewById(R.id.progressBar2);

		// Perform any variable changes (formatting, etc)
		String formatted_phone_number = FormatPhoneNumber.format(save_phone_number);
		int mins_total = save_mins_used + save_mins_left;
		String str_mins_total = String.valueOf(mins_total);
		String formatted_save_start_date = DateUtils.format(save_start_date, 2);
		String label_mins_usedleft_value = null;
		int mins_usedleft_value = 0;
		int progress_mins_progress;
		int progress_mins_max;
		int progress_next_payment_progress = DateUtils.daysBetween(save_date_saved, save_start_date);
		int progress_next_payment_max = DateUtils.daysSinceSameDayLastMonth(save_start_date);
		Drawable progress_next_payment_drawable = (Drawable) new SetColor().determineColor(progress_next_payment_progress, progress_next_payment_max);
		Drawable progress_mins_drawable = (Drawable) new SetColor().determineColor(save_mins_left, mins_total);
		Resources resources = context.getResources();
		SharedPreferences default_shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String pref_mins_display_type_name = resources.getString(R.string.pref_mins_display_type);
		String pref_mins_display_type = default_shared_preferences.getString(pref_mins_display_type_name, "mins-left");

		switch (pref_mins_display_type) {
			case "mins-used":
				label_mins_usedleft_value = resources.getString(R.string.label_mins_used);
				mins_usedleft_value = save_mins_used;
				break;
			case "mins-left":
				label_mins_usedleft_value = resources.getString(R.string.label_mins_left);
				mins_usedleft_value = save_mins_left;
				break;
		}

		/* mins progress bar */
		switch (str_mins_total) {
			case "0": // support for unlimited minutes
				progress_mins_max = 100;
				progress_mins_progress = 100;
				break;
			default:
				progress_mins_max = save_mins_used + save_mins_left;
				progress_mins_progress = mins_usedleft_value;
				break;
		}

		/* Set views with label values */
		label_mins_usedleft.setText(label_mins_usedleft_value);

		/* Set views with save values */
		// txt_first_name.setText(save_first_name);
		// txt_phone_number.setText(formatted_phone_number);
		txt_account_status.setText(save_account_status);
		txt_total.setText(save_total);
		txt_balance.setText(save_balance);
		txt_mins_usedleft.setText(String.valueOf(mins_usedleft_value));
		txt_mins_total.setText(str_mins_total);
		txt_start_date.setText(formatted_save_start_date);
		progress_bar_mins_usedleft.setMax(progress_mins_max);
		progress_bar_mins_usedleft.setProgress(progress_mins_progress);
		progress_bar_mins_usedleft.setProgressDrawable(progress_mins_drawable);
		progress_bar_next_payment.setMax(progress_next_payment_max);
		progress_bar_next_payment.setProgress(progress_next_payment_progress);
		progress_bar_next_payment.setProgressDrawable(progress_next_payment_drawable);

		/* Set last updated */
		txt_last_updated.setText(resources.getString(R.string.label_last_updated) + " " + DateUtils.format(save_date_saved, 4));

		/* Set title */
		// ((Activity) context).setTitle(save_first_name);
		action_bar.setTitle(formatted_phone_number);
	}

	class SetColor extends ProgressBarColor {

		@Override
		protected Object onLow() {
			return mContext.getResources().getDrawable(R.drawable.progress_low);
		}

		@Override
		protected Object onMed() {
			return mContext.getResources().getDrawable(R.drawable.progress_med);
		}

		@Override
		protected Object onHigh() {
			return mContext.getResources().getDrawable(R.drawable.progress_high);
		}
	}

}
