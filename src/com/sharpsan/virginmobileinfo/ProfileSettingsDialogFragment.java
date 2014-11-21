package com.sharpsan.virginmobileinfo;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sharpsan.virginmobileinfo.profile.LoginAdapter;

public class ProfileSettingsDialogFragment extends DialogFragment {

	private Context mContext;
	private View mView;
	private LoginAdapter mLoginAdapter;
	private Cursor mLoginCursor;

	public ProfileSettingsDialogFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mContext = activity;
		mLoginAdapter = new LoginAdapter(mContext);
		mLoginAdapter.open();
		mLoginCursor = mLoginAdapter.fetchLastEntry();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = getActivity().getLayoutInflater().inflate(R.layout.layout_profile_login, null); // Dialog view
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Account Login");
		builder.setView(mView);

		if (mLoginCursor.getCount() > 0) {
			mLoginAdapter.bind(mContext, mView, mLoginCursor);
		}

		// Add action buttons
		builder.setPositiveButton(R.string.pref_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				EditText edittxt_phone_number = (EditText) mView.findViewById(R.id.pref_edittxt_phone_number);
				EditText edittxt_password = (EditText) mView.findViewById(R.id.pref_edittxt_password);

				String edittxt_phone_number_value = edittxt_phone_number.getText().toString();
				String edittxt_password_value = edittxt_password.getText().toString();
				String new_date_saved = new Date().toString();

				String[] new_login_data = { edittxt_phone_number_value, edittxt_password_value, new_date_saved };
				mLoginAdapter.createEntry(new_login_data);

				// close dialog
				ProfileSettingsDialogFragment.this.getDialog().cancel();
			}
		});

		builder.setNegativeButton(R.string.pref_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				ProfileSettingsDialogFragment.this.getDialog().cancel();
			}
		});

		return builder.create();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mLoginAdapter.close();
	}
}