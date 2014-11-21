package com.sharpsan.virginmobileinfo.utils;

import android.content.Context;
import android.content.Intent;

import com.sharpsan.virginmobileinfo.R;

public class Feedback {
	public static void submitBugReport(Context context) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { context.getResources().getString(R.string.developer_email) });
		intent.putExtra(Intent.EXTRA_SUBJECT, SimplePackageInfo.get(context).packageName + " Bug-report (v" + SimplePackageInfo.get(context).versionCode + ")");
		intent.putExtra(Intent.EXTRA_TEXT, "Explain your issue here.");
		try {
			context.startActivity(Intent.createChooser(intent, "Submit bug..."));
		} catch (android.content.ActivityNotFoundException ex) {
			DebugMessage.show(context, "There are no email clients installed.");
		}
	}

	public static void contactDev(Context context) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { context.getResources().getString(R.string.developer_email) });
		intent.putExtra(Intent.EXTRA_SUBJECT, SimplePackageInfo.get(context).packageName + " Contact (v" + SimplePackageInfo.get(context).versionCode + ")");
		intent.putExtra(Intent.EXTRA_TEXT, "Message body here.");
		try {
			context.startActivity(Intent.createChooser(intent, "Contact developer..."));
		} catch (android.content.ActivityNotFoundException ex) {
			DebugMessage.show(context, "There are no email clients installed.");
		}
	}
}
