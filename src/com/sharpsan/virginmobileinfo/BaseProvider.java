package com.sharpsan.virginmobileinfo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BaseProvider extends AppWidgetProvider {

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		// Intent intent = new Intent(context.getApplicationContext(), WorkerService.class);
		// context.startService(intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Get all ids
		ComponentName widget = new ComponentName(context, BaseProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(widget);

		// When we click the widget, we want to open our main activity.
		// TODO

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), WorkerService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
	}

}