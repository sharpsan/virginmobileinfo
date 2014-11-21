package com.sharpsan.virginmobileinfo;

import java.text.ParseException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sharpsan.virginmobileinfo.profile.AccountAdapter;
import com.sharpsan.virginmobileinfo.profile.LoginAdapter;
import com.sharpsan.virginmobileinfo.utils.DebugMessage;
import com.sharpsan.virginmobileinfo.utils.Feedback;
import com.sharpsan.virginmobileinfo.utils.LoginDialog;
import com.sharpsan.virginmobileinfo.utils.SimplePackageInfo;

public class MainActivity extends Activity {

	private Context mContext;
	private ActionBar mActionBar;
	private Menu mMenu;
	private MenuItem menu_item_refresh = null;
	private boolean isReceiverRegistered;

	// service vars
	private WorkerService mService;
	private boolean mBound = false;

	// database vars
	private LoginAdapter mLoginAdapter;
	private AccountAdapter mAccountAdapter;
	private Cursor mLoginCursor;
	private Cursor mAccountCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBar = getActionBar();
		mContext = this;

		mLoginAdapter = new LoginAdapter(this);
		mLoginAdapter.open();
		mAccountAdapter = new AccountAdapter(this);
		mAccountAdapter.open();

		/** Set theme based on pref */
		//Themeing.loadPrefTheme(getApplicationContext());

		/** First run check */
		// TODO could make lighter...
		String pref_file = "other";
		String pref_name = "firstrun_shown_on_version";
		int app_version = SimplePackageInfo.get(getApplicationContext()).versionCode;
		int firstrun = getSharedPreferences(pref_file, MODE_PRIVATE).getInt(pref_name, 0);
		if (firstrun != app_version) {
			DialogFragment fragment = new FirstRunDialogFragment();
			fragment.show(getFragmentManager(), null);
			// Save the state
			getSharedPreferences(pref_file, MODE_PRIVATE).edit().putInt(pref_name, app_version).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_profile, menu);
		// getMenuInflater().inflate(R.menu.main, menu);
		menu_item_refresh = menu.findItem(R.id.action_refresh);
		mMenu = menu;
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Bind service to activity
		Intent intent = new Intent(this, WorkerService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		// Register reciever
		if (!isReceiverRegistered) {
			registerReceiver(mReceiver, new IntentFilter("com.sharpsan.virginmobileinfo.UPDATE_PROFILE_VIEWS"));
			isReceiverRegistered = true;
		}

		// inflate proper layout
		mAccountCursor = mAccountAdapter.fetchLastEntry();
		if (mAccountCursor.getCount() == 0) {
			setContentView(R.layout.layout_profile_empty);
		} else {
			// Update profile views
			sendBroadcast(new Intent().setAction("com.sharpsan.virginmobileinfo.UPDATE_PROFILE_VIEWS"));
			// NOTE: will setcontentview to proper layout in profile receiver
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
			case R.id.action_help:
				startActivity(new Intent(this, HelpActivity.class));
				break;
				/*case R.id.action_graphs:
				 *startActivity(new Intent(this, GraphsActivity.class));
				break;*/
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsPreferenceActivity.class));
				break;
			case R.id.action_about:
				new AboutDialogFragment().show(getFragmentManager(), null);
				break;
			case R.id.action_refresh:
				menu_item_refresh.setActionView(R.layout.menu_action_refresh); // show menu indicator
				mService.refreshThread();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				invalidateOptionsMenu();
				mMenu.performIdentifierAction(R.id.action_overflow, 0);
				break;
			case KeyEvent.KEYCODE_BACK:
				finish();
				break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
			// Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
		}

		// Unregister reciever
		if (isReceiverRegistered) {
			try {
				this.unregisterReceiver(mReceiver);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			isReceiverRegistered = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoginAdapter.close();
		mAccountAdapter.close();
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			WorkerService.LocalBinder binder = (WorkerService.LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			// Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			mBound = false;
		}
	};

	/** Profile/account receiver */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		// TODO does all the work on main thread... send to service?
		@Override
		public void onReceive(Context context, Intent intent) throws CursorIndexOutOfBoundsException {
			// Toast.makeText(context, "Profile receiver called", Toast.LENGTH_SHORT).show();

			String debugMessage = intent.getStringExtra("debugMessage");
			if (debugMessage != null) {
				DebugMessage.show(getApplicationContext(), debugMessage);
			}
			String action = intent.getStringExtra("action");
			if (action != null) {
				LoginDialog.show(mContext);
			}

			mLoginCursor = mLoginAdapter.fetchLastEntry();
			mAccountCursor = mAccountAdapter.fetchLastEntry();

			try {
				if (mAccountCursor.getCount() > 0) {
					setContentView(R.layout.layout_profile);
					mAccountAdapter.bind(getApplication(), findViewById(R.id.layout_profile), mAccountCursor, mActionBar);
				}
			} catch (ParseException | CursorIndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			if (menu_item_refresh != null) {
				// reset refresh item in menu
				menu_item_refresh.setActionView(null);
			}
		}
	};

	public void emailLink(View arg0) {
		Feedback.contactDev(this);
	}

}