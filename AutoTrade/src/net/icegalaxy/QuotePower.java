package net.icegalaxy;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class QuotePower {

	Integer num = 1;

	// Location lc = new Location(986, 90); // ie

	private String fhi; //hhi;
	private String deal;
	private String change;
	private String quantityB4Treatment;
	private String bidQuantity;
	private String askQuantity;
	private String bid;
	private String ask;
	private Float quantity;
	private String time;
	private int errCount;
	
	public QuotePower() {
		DB.connect("AutoTrade");
//		hhi = createTable("");
		fhi = createTable("FHI");

	}

	public void getQuote() throws FailGettingDataException {

		
		try {
			Sikuli.quotePower();
			errCount = 0;
		} catch (Exception e) {
			Global.addLog("Can't get quote, try again");
			Sikuli.resetQuotePower();
			e.printStackTrace();
			sleep(100);
			getQuote();
			if (errCount > 50)
				throw new FailGettingDataException();
			
			errCount++;
			
		}

		sleep(100); // �i��]��copy���Yget��get����AScanner���n�hnull

		String s = DB.getClipboard();
		time = getTime();

//		for (int i = 0; i < 2; i++) {

		for (int i = 0; i < 1; i++) { //HSI only
			
			String index = "";
			String tableName = "";
			if (i == 0) {
//				index = "HSI ";
				index = "HSI Futures ";
				tableName = fhi;
			} else { // �ĤG�����|�YH�A�|COVER��HSI D data, getDeal�GD�|get��h��
//				index = "HHI ";
				index = "H�ѫ��ƴ��f ";
//				tableName = hhi;
			}

			deal = "";
			change = "";
			quantityB4Treatment = "";
			bidQuantity = "";
			askQuantity = "";
			bid = "";
			ask = "";
			quantity = new Float(0);

			try {
				Scanner sc = new Scanner(s);
				sc.useDelimiter(index + "..........."); //�h��HKD
				sc.next();
				Scanner sc2 = new Scanner(sc.next());
				deal = sc2.next();
				change = sc2.next();
				sc2.next();
				quantityB4Treatment = sc2.next();
				
				bidQuantity = sc2.next();
				bid = sc2.next();
				ask = sc2.next();
				askQuantity = sc2.next();

			} catch (Exception e) {
				e.printStackTrace();
				sleep(1000);

				if (new Integer(getTime()) < 91600){ //�ڦ��O�׫Y�\�Ĥ@��GET����clipboard�A�զh�X��
					Global.addLog("Can't get quote, try again");
					getQuote();
				}
				else {
					Global.shutDown = true;
					throw new FailGettingDataException();
				}
				// getQuote();
			}

			if (quantityB4Treatment.contains("K")) {
				quantity = new Float(quantityB4Treatment.replace("K", ""));
				quantity = quantity * 1000;
			} else {
				quantity = new Float(quantityB4Treatment);
			}

			String query = "INSERT INTO " + tableName + " VALUES("
					+ quote(num.toString()) + "," + quote(time) + ","
					+ quote(deal) + "," + quote(change) + ","
					+ quote(quantity.toString()) + "," + quote(bidQuantity)
					+ "," + quote(bid) + "," + quote(ask) + ","
					+ quote(askQuantity) + ");";

			try {
				DB.stmt.executeUpdate(query.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		num++;

	}

	public void close() {
		DB.close();
		sleep(5000);
		// Sikuli.getSleep();
	}

	private String createTable(String s) {
		String query = "create table " + Global.getToday() + s + "(MyIndex integer, "
				+ "TradeTime time, " + "Deal float, " + "Change float, "
				+ "TotalQuantity float, " + "BidQuantity integer, "
				+ "Bid float, Ask float, " + "AskQuantity integer)";

		System.out.println(query);

		try {
			DB.stmt.executeUpdate(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Global.getToday() + s;
	}

	public void sleep(int miniSecond) {

		try {
			Thread.sleep(miniSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static String quote(String point) {
		return ("'" + point + "'");
	}

	private String getTime() {
		return time;
	}

	public String getDeal() {
		return deal;
	}

	public String getBid() {
		return bid;
	}

	public String getAsk() {
		return ask;
	}
	
	public String getChange(){
		return change;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
