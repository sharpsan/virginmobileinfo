package com.sharpsan.virginmobileinfo.profile;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.database.Cursor;

import com.sharpsan.virginmobileinfo.database.Contract.LoginDataEntry;
import com.sharpsan.virginmobileinfo.utils.DebugMessage;
import com.sharpsan.virginmobileinfo.utils.StringUtils;

public class UserData {

	/* DOWNLOAD latest profile source code */
	public Document downloadUserData(Cursor cursor) {
		Document vm_webpage = null;
		String username = cursor.getString(cursor.getColumnIndex(LoginDataEntry.LOGIN_PHONE_NUMBER));
		String password = cursor.getString(cursor.getColumnIndex(LoginDataEntry.LOGIN_PASSWORD));

		DebugMessage.say("Attempting to download profile...");
		try {
			//@formatter:off
			vm_webpage = Jsoup.connect("https://www2.virginmobileusa.com/login/login.do")
					.timeout(10 * 6000)
					.data("min", username)
					.data("vkey", password)
					.data("submit", "submit")
					.post();
			//@formatter:on
			DebugMessage.say("Profile downloaded!");
		} catch (IOException e) {
			DebugMessage.say("Failed to download profile.");
			e.printStackTrace();
			return null;
		}

		return vm_webpage;
	}

	/** FILTER for usable data from VM website code */
	public static String[] filterUserData(Document vm_webpage) {
		final String[] tags_to_load = { "first-name", "phone-number", "balance", "total", "start-date", "mins-used", "mins-left", "account-status" };
		String vm_webpage_to_string = vm_webpage.toString();
		String[] vm_profile;
		String tag_contents;

		DebugMessage.say("Filter 1 (account section)...");
		vm_webpage_to_string = StringUtils.substringBetween(vm_webpage_to_string, "<vmu-page", "</vmu-page>");
		DebugMessage.say("Filter 1 should have worked.");

		/* Turn bulk info into pieces */
		DebugMessage.say("Filter 2 (each pertinent piece set to array)...");
		vm_profile = new String[tags_to_load.length];
		for (int i = 0; i < tags_to_load.length; i++) {
			String tag_id = tags_to_load[i];
			tag_contents = StringUtils.substringBetween(vm_webpage_to_string, "<" + tags_to_load[i] + ">", "</" + tag_id + ">");
			/* Halt if can't get tag */
			if (tag_contents == null) {
				DebugMessage.say("Can't filter profile right now... tag(s) don't exist!");
				return null;
			}
			vm_profile[i] = tag_contents.trim();
			DebugMessage.say(tag_id + " : " + tag_contents);
		}

		return vm_profile;
	}

}