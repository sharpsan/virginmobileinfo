package com.sharpsan.virginmobileinfo.database;

public final class Contract {

	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public Contract() {
	}

	public static abstract class UserDataEntry {
		public static final String TABLE_PROFILE = "vm_profile";
		public static final String USER_DATA_FIRST_NAME = "first_name";
		public static final String USER_DATA_PHONE_NUMBER = "phone_number";
		public static final String USER_DATA_BALANCE = "balance";
		public static final String USER_DATA_TOTAL = "total";
		public static final String USER_DATA_START_DATE = "start_date";
		public static final String USER_DATA_MINS_USED = "mins_used";
		public static final String USER_DATA_MINS_LEFT = "mins_left";
		public static final String USER_DATA_ACCOUNT_STATUS = "account_status";

		public static final String TABLE_EXTRA_DATA = "extra_data";
		public static final String USER_DATA_PROFILE_ID = "profile_id";
		public static final String USER_DATA_DATE_SAVED = "date_saved";
		public static final String USER_DATA_DOWNLOAD_RATE = "download_rate";
	}

	public static abstract class LoginDataEntry {
		public static final String TABLE_LOGIN = "vm_profile_login";
		public static final String LOGIN_PHONE_NUMBER = "phone_number";
		public static final String LOGIN_PASSWORD = "password";
		public static final String LOGIN_DATE_SAVED = "date_saved";
	}

}
