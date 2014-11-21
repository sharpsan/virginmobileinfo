package com.sharpsan.virginmobileinfo;

import org.jsoup.nodes.Document;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.sharpsan.virginmobileinfo.database.Contract.UserDataEntry;
import com.sharpsan.virginmobileinfo.profile.AccountAdapter;
import com.sharpsan.virginmobileinfo.profile.LoginAdapter;
import com.sharpsan.virginmobileinfo.profile.UserData;
import com.sharpsan.virginmobileinfo.utils.ProgressBarColor;
import com.sharpsan.virginmobileinfo.utils.Stopwatch;

public class WorkerService extends Service {

	private Context mContext;

	private final IBinder mBinder = new LocalBinder();

	private LoginAdapter mLoginAdapter;
	private AccountAdapter mAccountAdapter;
	private Cursor mLoginCursor;
	private Cursor mAccountCursor;

	private AppWidgetManager appWidgetManager;
	private RemoteViews remoteViews;
	private int[] allWidgetIds;

	private boolean isDownloadInProgress = false;

	private long time_to_download;

	private String call_action;

	@Override
	public void onCreate() {
		super.onCreate();

		mContext = this;

		registerReceiver(mReceiver, new IntentFilter("com.sharpsan.virginmobileinfo.UPDATE_WIDGET_VIEWS"));
		// registerReceiver(mReceiver, new IntentFilter("com.sharpsan.virginmobileinfo.REFRESH_THREAD"));

		mAccountAdapter = new AccountAdapter(this);
		mLoginAdapter = new LoginAdapter(this);
		mAccountAdapter.open();
		mLoginAdapter.open();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Widget add: only call once for widgets, on device boot, and when updating *all* widgets. Will still need to refresh each widget's views...
		// DebugMessage.show(this, "BackgroundService: onStartCommand called");

		appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		remoteViews = new RemoteViews(getPackageName(), R.layout.widget_1x1);
		allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		// onClick listener for opening app on widget click
		Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainActivity, 0);
		remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

		// Handle widget calls
		if (allWidgetIds != null) {
			for (int widgetId : allWidgetIds) {
				sendBroadcast(new Intent().setAction("com.sharpsan.virginmobileinfo.UPDATE_WIDGET_VIEWS"));
			}
		}

		// Handle CallReceiver
		call_action = intent.getStringExtra("call_action");
		try {
			if (call_action.equals("refresh")) {
				refreshThread();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mAccountAdapter.close();
		mLoginAdapter.close();

		unregisterReceiver(mReceiver);
	}

	public class LocalBinder extends Binder {
		WorkerService getService() {
			// Return this instance of _____Service so clients can call public methods
			return WorkerService.this;
		}
	}

	public void refreshThread() {
		// DebugMessage.show(this, "refreshThread() called");
		if (isDownloadInProgress) {
			return;
		}
		isDownloadInProgress = true;
		final Stopwatch stopwatch = new Stopwatch();
		final String pref_download_queue_file = "other";
		final String pref_download_queue_name = "download_queued";

		Thread newThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String debugMessage = null;
				String action = null;
				try {
					mLoginCursor = mLoginAdapter.fetchLastEntry();
					stopwatch.start(); // Start download timer
					Document webpage = new UserData().downloadUserData(mLoginCursor);
					time_to_download = stopwatch.getElapsedTimeMili(); // get download duration
					if (webpage != null) {
						String[] filtered = UserData.filterUserData(webpage);
						if (filtered != null) {
							String[] extra = { String.valueOf(time_to_download) };
							mAccountAdapter.createEntry(filtered, extra);

							// clear queue
							getSharedPreferences(pref_download_queue_file, Context.MODE_PRIVATE).edit().putBoolean(pref_download_queue_name, false).commit();

							debugMessage = "Updated";
						} else {
							debugMessage = "Login failed";
						}
					} else {
						// queue download for when there is a network connection
						getSharedPreferences(pref_download_queue_file, Context.MODE_PRIVATE).edit().putBoolean(pref_download_queue_name, true).commit();

						debugMessage = "No network connection";
					}
				} catch (CursorIndexOutOfBoundsException e) {
					e.printStackTrace();
					action = "show_login_dialog";
				}

				// Tell Receivers that we're done
				sendBroadcast(new Intent().setAction("com.sharpsan.virginmobileinfo.UPDATE_PROFILE_VIEWS").putExtra("debugMessage", debugMessage)
						.putExtra("action", action));
				sendBroadcast(new Intent().setAction("com.sharpsan.virginmobileinfo.UPDATE_WIDGET_VIEWS"));

				isDownloadInProgress = false;
				stopSelf();
			}
		});

		newThread.start();
	}

	/** Widget receiver */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Toast.makeText(context, "Widget receiver called - intent: " + intent.getAction(), Toast.LENGTH_SHORT).show();

			switch (intent.getAction()) {
				case "com.sharpsan.virginmobileinfo.UPDATE_WIDGET_VIEWS":
					mAccountCursor = mAccountAdapter.fetchLastEntry();
					if (allWidgetIds != null) {
						if (mAccountCursor.getCount() > 0) {
							final int save_mins_used = mAccountCursor.getInt(mAccountCursor.getColumnIndex(UserDataEntry.USER_DATA_MINS_USED));
							final int save_mins_left = mAccountCursor.getInt(mAccountCursor.getColumnIndex(UserDataEntry.USER_DATA_MINS_LEFT));
							final int mins_total = save_mins_used + save_mins_left;
							int color = Color.WHITE;

							// Set symbol color TODO: seems way too complicated, using extend, but did learn something in the process
							class SetColor extends ProgressBarColor {

								@Override
								protected Object onLow() {
									return Color.parseColor(getResources().getString(R.color.vm_red));
								}

								@Override
								protected Object onMed() {
									return Color.parseColor(getResources().getString(R.color.compliment_color_med));
								}

								@Override
								protected Object onHigh() {
									return Color.parseColor(getResources().getString(R.color.color_high_widget));
								}
							}
							color = (int) new SetColor().determineColor(save_mins_left, mins_total);

							// bind widget views here
							remoteViews.setTextViewText(R.id.widget_symbol, "+");
							remoteViews.setTextColor(R.id.widget_symbol, color);
							remoteViews.setTextViewText(R.id.widget_mins_left, String.valueOf(save_mins_left));

							for (int widgetId : allWidgetIds) {
								appWidgetManager.updateAppWidget(widgetId, remoteViews);
							}
						}
					}
					break;
				// TODO: maybe implement later...
				case "com.sharpsan.virginmobileinfo.REFRESH_THREAD":
					refreshThread();
					break;
			}
		}
	};

}