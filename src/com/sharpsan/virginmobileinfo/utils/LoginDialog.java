package com.sharpsan.virginmobileinfo.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;

import com.sharpsan.virginmobileinfo.ProfileSettingsDialogFragment;

public class LoginDialog {
	public static void show(Context context) {
		DialogFragment fragment = new ProfileSettingsDialogFragment();
		fragment.show(((Activity) context).getFragmentManager(), null);
	}
}
