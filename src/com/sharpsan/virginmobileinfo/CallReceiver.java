package com.sharpsan.virginmobileinfo;

import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import com.sharpsan.virginmobileinfo.database.Contract.UserDataEntry;
import com.sharpsan.virginmobileinfo.profile.AccountAdapter;
import com.sharpsan.virginmobileinfo.utils.PhonecallReceiver;

public class CallReceiver extends PhonecallReceiver {

	private void notification(Context context, String state) {
		Toast notification;
		AccountAdapter mAccountAdapter;
		Cursor mAccountCursor;
		Resources resources;
		SharedPreferences default_shared_preferences;
		String pref_toast_incoming_call_name;
		boolean pref_toast_incoming_call;
		String pref_toast_outgoing_call_name;
		boolean pref_toast_outgoing_call;

		// pref check -> end if false
		resources = context.getResources();
		default_shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		switch (state) {
			case "incoming_call":
				pref_toast_incoming_call_name = resources.getString(R.string.pref_toast_incoming_call);
				pref_toast_incoming_call = default_shared_preferences.getBoolean(pref_toast_incoming_call_name, true);
				if (!pref_toast_incoming_call) {
					return;
				}
				break;
			case "outgoing_call":
				pref_toast_outgoing_call_name = resources.getString(R.string.pref_toast_outgoing_call);
				pref_toast_outgoing_call = default_shared_preferences.getBoolean(pref_toast_outgoing_call_name, true);
				if (!pref_toast_outgoing_call) {
					return;
				}
				break;
		}

		mAccountAdapter = new AccountAdapter(context);
		mAccountAdapter.open();

		mAccountCursor = mAccountAdapter.fetchLastEntry();
		try {
			int save_mins_left = mAccountCursor.getInt(mAccountCursor.getColumnIndex(UserDataEntry.USER_DATA_MINS_LEFT));
			notification = Toast.makeText(context, String.valueOf(context.getResources().getString(R.string.label_mins_left) + ": " + save_mins_left),
					Toast.LENGTH_LONG);
			notification.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			notification.show();
		} catch (CursorIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		mAccountAdapter.close();
	}

	public static void update(Context context) {
		Intent intent = new Intent(context, WorkerService.class);
		intent.putExtra("call_action", "refresh");
		context.startService(intent);

		// update widget
		Intent intent_widget = new Intent(context, BaseProvider.class);
		intent_widget.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
		// since it seems the onUpdate() is only fired on that:
		int[] ids = { R.xml.widget_1x1 };
		intent_widget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		context.sendBroadcast(intent_widget);
	}

	@Override
	protected void onIncomingCallStarted(Context context, Date start) {
		notification(context, "incoming_call");
	}

	@Override
	protected void onOutgoingCallStarted(Context context, Date start) {
		notification(context, "outgoing_call");
	}

	@Override
	protected void onIncomingCallEnded(Context context, Date start, Date end) {
		update(context);
	}

	@Override
	protected void onOutgoingCallEnded(Context context, Date start, Date end) {
		update(context);
	}
}
