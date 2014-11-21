package com.sharpsan.virginmobileinfo.utils;

public class Themeing {
	public static Resources loadPrefTheme(Context context) {
		SharedPreferences default_shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Resources resources = context.getResources();
		// return resource based on set pref
		// http://stackoverflow.com/questions/11562051/change-activitys-theme-programmatically
		return resources;

	}
}