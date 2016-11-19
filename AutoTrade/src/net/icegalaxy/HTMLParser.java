package net.icegalaxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class HTMLParser {
	String url;

	public HTMLParser(String url) {
		this.url = url;
	}

	public double parseETNetOpen() throws Exception {
		double open = 0;
		URLConnection connection = null;
		// try {
		connection = new URL(url).openConnection();
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

		Scanner sc = new Scanner(bufReader);
		sc.useDelimiter("¶}¥«");
		sc.next();
		Scanner sc2 = new Scanner(sc.next());
		sc.close();
		sc2.useDelimiter(">¡J");
		sc2.next();
		Scanner sc3 = new Scanner(sc2.next());
		sc2.close();
		sc3.useDelimiter("</span>");
		open = sc3.nextDouble();
		sc3.close();
		return open;

	}

}
