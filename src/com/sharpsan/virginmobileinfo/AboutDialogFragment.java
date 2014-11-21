package com.sharpsan.virginmobileinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.sharpsan.virginmobileinfo.utils.HtmlUtils;

public class AboutDialogFragment extends DialogFragment {

	private Context mContext;
	private View mView;

	public AboutDialogFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = getActivity().getLayoutInflater().inflate(R.layout.layout_about, null); // Dialog view
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("About");
		builder.setCancelable(false);
		builder.setView(mView);
		// Add action buttons
		builder.setPositiveButton(R.string.dialog_about_okay_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				AboutDialogFragment.this.getDialog().cancel();
			}
		});

		TextView txt_about_body = (TextView) mView.findViewById(R.id.txt_about_body);
		txt_about_body.setText(Html.fromHtml(HtmlUtils.getFileContentsFromAsset(mContext, "html/about.html")));

		return builder.create();
	}
}