package com.sharpsan.virginmobileinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sharpsan.virginmobileinfo.utils.IsNetworkAvailable;

public class NetworkChangedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		if (IsNetworkAvailable.check(context)) {
			String pref_file = "other";
			String pref_name = "download_queued";
			boolean download_queued = context.getSharedPreferences(pref_file, Context.MODE_PRIVATE).getBoolean(pref_name, false);
			if (download_queued) {
				CallReceiver.update(context);
			}
		}
	}
}
