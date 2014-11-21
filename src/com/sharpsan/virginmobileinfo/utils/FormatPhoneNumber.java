package com.sharpsan.virginmobileinfo.utils;

public class FormatPhoneNumber {
	public static String format(String number) {
		number = number.substring(0, number.length() - 4) + "-" + number.substring(number.length() - 4, number.length());
		number = number.substring(0, number.length() - 8) + ") " + number.substring(number.length() - 8, number.length());
		number = number.substring(0, number.length() - 13) + "(" + number.substring(number.length() - 13, number.length());
		return number;
	}
}