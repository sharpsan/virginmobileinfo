package com.sharpsan.virginmobileinfo.utils;

// package com.gabesechan.android.reusable.receivers;
// http://gabesechansoftware.com/is-the-phone-ringing/#more-8
// http://stackoverflow.com/questions/15563921/detecting-an-incoming-call-coming-to-an-android-device

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public abstract class PhonecallReceiver extends BroadcastReceiver {

	// The receiver will be recreated whenever android feels like it. We need a static variable to remember data between instantiations

	private static int lastState = TelephonyManager.CALL_STATE_IDLE;
	private static Date callStartTime;
	private static boolean isIncoming;

	@Override
	public void onReceive(Context context, Intent intent) {

		// We listen to two intents. The new outgoing call only tells us of an outgoing call. We use it to get the number.
		if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

		} else {
			String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
			int state = 0;
			if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				state = TelephonyManager.CALL_STATE_IDLE;
			} else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				state = TelephonyManager.CALL_STATE_OFFHOOK;
			} else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				state = TelephonyManager.CALL_STATE_RINGING;
			}

			onCallStateChanged(context, state);
		}
	}

	// Derived classes should override these to respond to specific events of interest
	protected void onIncomingCallStarted(Context ctx, Date start) {
	}

	protected void onOutgoingCallStarted(Context ctx, Date start) {
	}

	protected void onIncomingCallEnded(Context ctx, Date start, Date end) {
	}

	protected void onOutgoingCallEnded(Context ctx, Date start, Date end) {
	}

	protected void onMissedCall(Context ctx, Date start) {
	}

	// Deals with actual events

	// Incoming call- goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
	// Outgoing call- goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
	public void onCallStateChanged(Context context, int state) {
		if (lastState == state) {
			// No change, debounce extras
			return;
		}
		switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				isIncoming = true;
				callStartTime = new Date();
				onIncomingCallStarted(context, callStartTime);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// Transition of ringing->offhook are pickups of incoming calls. Nothing done on them
				if (lastState != TelephonyManager.CALL_STATE_RINGING) {
					isIncoming = false;
					callStartTime = new Date();
					onOutgoingCallStarted(context, callStartTime);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// Went to idle- this is the end of a call. What type depends on previous state(s)
				if (lastState == TelephonyManager.CALL_STATE_RINGING) {
					// Ring but no pickup- a miss
					onMissedCall(context, callStartTime);
				} else if (isIncoming) {
					onIncomingCallEnded(context, callStartTime, new Date());
				} else {
					onOutgoingCallEnded(context, callStartTime, new Date());
				}
				break;
		}
		lastState = state;
	}
}
