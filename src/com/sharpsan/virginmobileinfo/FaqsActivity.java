package com.sharpsan.virginmobileinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.sharpsan.virginmobileinfo.utils.HtmlUtils;

public class FaqsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_faqs);

		TextView txt_faq_body = (TextView) findViewById(R.id.txt_faq_body);
		txt_faq_body.setText(Html.fromHtml(HtmlUtils.getFileContentsFromAsset(this, "html/faqs.html")));
	}

}