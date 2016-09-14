package net.icegalaxy;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoTradeDB extends DB {

	public static void createTable(String tableName) {

		String query = "create table "
				+ tableName
				+ "(MyIndex integer, TradeTime time, Deal float, Change float, TotalQuantity float, BidQuantity integer, Bid float, Ask float, AskQuantity integer)";

		System.out.println(query);

		AutoTradeDB.tableName = tableName;

		try {
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			Global.addLog("Table already exists");
			e.printStackTrace();
		}

	}

	public static void insert(String num, String time, String deal, String change, String quantity, String bidQuantity, String bid, String ask, String askQuantity) {

		String query = "INSERT INTO " + tableName + " VALUES(" 
		+ quote(num) + "," 
		+ quote(time) + "," 
		+ quote(deal) + "," 
		+ quote(change) + "," 
		+ quote(quantity) + ","
		+ quote(bidQuantity) + ","
		+ quote(bid) + ","
		+ quote(ask) + ","
		+ quote(askQuantity) 
		+ ");";

		try {
			stmt.executeUpdate(query.toString());
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static String getToday() {

		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		String s = formatter.format(now.getTime());

		return s;
	}

	public static String getTime() {

		return currentTime.toString();
	}
	
	public static void setTime(String time){		
		currentTime = new StringBuffer(time);
	}

	public static void sleep(int miniSecond) {

		try {
			Thread.sleep(miniSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	static StringBuffer currentTime = new StringBuffer("00000000000000000");
	static String tableName = "";
}
