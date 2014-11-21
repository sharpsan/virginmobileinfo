package com.sharpsan.virginmobileinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sharpsan.virginmobileinfo.utils.Feedback;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_help);
	}

	public void submitBug(View arg0) {
		Feedback.submitBugReport(this);
	}

	public void contactDev(View arg0) {
		Feedback.contactDev(this);
	}

	public void showFaqs(View arg0) {
		startActivity(new Intent(this, FaqsActivity.class));
	}

}