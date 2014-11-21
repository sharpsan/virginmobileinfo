package com.sharpsan.virginmobileinfo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
	static String output_date_formatted;
	static final String joda_default_date_format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	static final String default_date_format = "EEE MMM dd HH:mm:ss zzz yyyy";

	public static String getCurrentDate() {
		return new Date().toString();
	}

	public static String format(String input_date, String input_format, String output_format) {

		SimpleDateFormat simple_date_input_format;
		SimpleDateFormat simple_date_output_format;
		simple_date_input_format = new SimpleDateFormat(input_format, Locale.US);
		simple_date_output_format = new SimpleDateFormat(output_format, Locale.US);

		try {
			Date input_date_formatted = simple_date_input_format.parse(input_date);
			output_date_formatted = simple_date_output_format.format(input_date_formatted);
		} catch (ParseException e) {
			output_date_formatted = null;
			e.printStackTrace();
		}
		return output_date_formatted;
	}

	public static String format(String input_date, int cached_format_id) {
		String input_format;
		String output_format;

		switch (cached_format_id) {
			case 1:
				input_format = default_date_format;
				output_format = joda_default_date_format;
				break;

			case 2:
				input_format = joda_default_date_format;
				output_format = "EEE M/d";
				break;

			case 3:
				input_format = joda_default_date_format;
				output_format = "M/d 'at' HH:mm";
				break;

			case 4:
				input_format = joda_default_date_format;
				output_format = "EEE, h:mm a";
				break;

			default:
				return null;
		}

		return format(input_date, input_format, output_format);
	}

	public static int daysSinceSameDayLastMonth(String input_date) throws ParseException {
		// TODO clean up all of this... way too many type conversions...
		SimpleDateFormat dateFormatter = new SimpleDateFormat(joda_default_date_format, Locale.US);
		// dateFormatter.setLenient(false);
		Calendar calendar = Calendar.getInstance();
		Date end_date = dateFormatter.parse(input_date);
		calendar.setTime(end_date);
		calendar.add(Calendar.MONTH, -1);
		String start_date = DateUtils.format(calendar.getTime().toString(), 1);
		return daysBetween(start_date, input_date);
	}

	public static int daysBetween(String input_start_date, String input_end_date) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(joda_default_date_format, Locale.US);
		// dateFormatter.setLenient(false);
		Calendar calendar = Calendar.getInstance();
		Date start_date = dateFormatter.parse(input_start_date);
		calendar.setTime(start_date);
		int start_date_month = calendar.get(Calendar.MONTH);
		int start_date_day = calendar.get(Calendar.DAY_OF_MONTH);
		int start_date_year = calendar.get(Calendar.YEAR);
		Calendar start_date_calendar = new GregorianCalendar(start_date_year, start_date_month, start_date_day);
		int start_date_days_in_month = start_date_calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		Date end_date = dateFormatter.parse(input_end_date);
		calendar.setTime(end_date);
		int end_date_month = calendar.get(Calendar.MONTH);
		int end_date_day = calendar.get(Calendar.DAY_OF_MONTH);
		// int end_date_year = calendar.get(Calendar.YEAR);

		if (start_date_month == end_date_month) {
			return end_date_day - start_date_day;
		} else if (start_date_month == end_date_month - 1) {
			return start_date_days_in_month - start_date_day + end_date_day;
		}
		return 0;
	}
}
