package com.sharpsan.virginmobileinfo.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DebugMessage {
	private static final String LOG = "com.sharpsan.virginmobileinfo";

	public static void say(String message) {
		Log.i(LOG, message);
	}

	public static void show(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
