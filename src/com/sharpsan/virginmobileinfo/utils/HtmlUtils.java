package com.sharpsan.virginmobileinfo.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class HtmlUtils {
	public static String getFileContentsFromAsset(Context context, String filepath) {
		InputStream is;
		String filecontents = null;
		try {
			is = context.getAssets().open(filepath);
			// We guarantee that the available method returns the total
			// size of the asset...  of course, this does mean that a single
			// asset can't be more than 2 gigs.
			int size = is.available();
			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string.
			filecontents = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filecontents;
	}
}
