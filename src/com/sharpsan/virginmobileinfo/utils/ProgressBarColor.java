package com.sharpsan.virginmobileinfo.utils;

import android.content.Context;

public abstract class ProgressBarColor {

	Context context;
	int color;

	public Object determineColor(int current_progress, int max_progress) {
		if (current_progress < ((float) 15 / 100) * max_progress) {
			return onLow();
		} else if (current_progress < ((float) 35 / 100) * max_progress) {
			return onMed();
		} else {
			return onHigh();
		}
	}

	protected Object onLow() {
		return null;
	}

	protected Object onMed() {
		return null;
	}

	protected Object onHigh() {
		return null;
	}

}