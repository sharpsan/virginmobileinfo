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
import com.sharpsan.virginmobileinfo.utils.SimplePackageInfo;

public class FirstRunDialogFragment extends DialogFragment {

	private Context mContext;
	private View mView;

	public FirstRunDialogFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = getActivity().getLayoutInflater().inflate(R.layout.layout_first_run, null); // Dialog view
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("First Run");
		builder.setCancelable(false);
		builder.setView(mView);
		// Add action buttons
		builder.setPositiveButton(R.string.dialog_first_run_next_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// TODO once read, show login dialog
				// FragmentDialogFirstRun.this.getDialog().cancel();
				DialogFragment fragment = new ProfileSettingsDialogFragment();
				fragment.show(getFragmentManager(), null);
				FirstRunDialogFragment.this.getDialog().cancel();
			}
		});

		// Set version
		TextView txt_version = (TextView) mView.findViewById(R.id.txt_first_run_version);
		String version = SimplePackageInfo.get(mContext).versionName;
		txt_version.setText("Version: " + version + "\n");

		// Set changelog
		TextView txt_first_run_changelog = (TextView) mView.findViewById(R.id.txt_first_run_changelog);
		String first_run_changelog_html = HtmlUtils.getFileContentsFromAsset(mContext, "html/changelog.html");
		txt_first_run_changelog.setText(Html.fromHtml(first_run_changelog_html));

		// Set body
		TextView txt_first_run_body = (TextView) mView.findViewById(R.id.txt_first_run_body);
		String first_run_body_html = HtmlUtils.getFileContentsFromAsset(mContext, "html/first_run.html");
		txt_first_run_body.setText("\n" + Html.fromHtml(first_run_body_html));

		return builder.create();
	}
}