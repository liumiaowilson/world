package org.wilson.world.util;

public class NetUtils {
	public static String toHTTPSCompatibleURL(String url) {
		return "https://www.google.com/search?q=%" + url + "&btnI=Im+Feeling+Lucky";
	}
}
