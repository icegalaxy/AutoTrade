package net.icegalaxy;

import java.sql.SQLException;
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
		try {
			DB.connect("AutoTrade");
		} catch (SQLException e) {

			e.printStackTrace();
		}
//		hhi = createTable("");
		fhi = createTable("HSF");

	}
	
	public QuotePower(String typeSthOnly) {
	

	}
	
	public double getDealOnly(){
		
		try {
			Sikuli.quotePower();
			errCount = 0;
		} catch (Exception e) {
			Global.addLog("Can't get quote");
			Sikuli.resetQuotePower();
			e.printStackTrace();
			sleep(100);
			errCount++;
			
		}

		sleep(100); // give time the the computer, dont knwo whether is necessary
			
			String tableName = "";
			tableName = fhi;


			deal = "";
			change = "";
			quantityB4Treatment = "";
			bidQuantity = "";
			askQuantity = "";
			bid = "";
			ask = "";
			quantity = new Float(0);

			if (TimePeriodDecider.getTime() <164500)
				getDayMarket();
			else
				getNighMarket();

			if (quantityB4Treatment.contains("K")) {
				quantity = new Float(quantityB4Treatment.replace("K", ""));
				quantity = quantity * 1000;
			} else {
				quantity = new Float(quantityB4Treatment);
			}
			
			return Double.parseDouble(deal);
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

		sleep(100); // give time the the computer, dont knwo whether is necessary
			
			String tableName = "";
			tableName = fhi;


			deal = "";
			change = "";
			quantityB4Treatment = "";
			bidQuantity = "";
			askQuantity = "";
			bid = "";
			ask = "";
			quantity = new Float(0);

			if (TimePeriodDecider.getTime() <164500)
				getDayMarket();
			else
				getNighMarket();

			if (quantityB4Treatment.contains("K")) {
				quantity = new Float(quantityB4Treatment.replace("K", ""));
				quantity = quantity * 1000;
			} else {
				quantity = new Float(quantityB4Treatment);
			}
			
			if (Integer.parseInt(deal) == 0) {

//				Global.addLog("Deal = 0, try again");
//				System.out.println("Time: " + time);
				return;
			}

			String query = "INSERT INTO " + tableName + " VALUES("
					+ quote(num.toString()) + ",\"" + quote(time) + "\","
					+ quote(deal) + "," + quote(change) + ","
					+ quote(quantity.toString()) + "," + quote(bidQuantity)
					+ "," + quote(bid) + "," + quote(ask) + ","
					+ quote(askQuantity) + ");";

			try {
				DB.stmt.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		

		num++;

	}
	
	private void getDayMarket(){
		try {
			Scanner sc = new Scanner(DB.getClipboard());
			sc.useDelimiter("HKD");
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

			sc.close();
			sc2.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			sleep(1000);

			//dont know what this is actually
//			if (new Integer(time) < 91600){ //�ڦ��O�׫Y�\�Ĥ@��GET����clipboard�A�զh�X��
//				Global.addLog("Can't get quote, try again");
//				getQuote();
//			}
//			else {
//				Global.shutDown = true;
//				throw new FailGettingDataException();
//			}
			// getQuote();
		}
	}
	
	private void getNighMarket(){
		try {
			Scanner sc = new Scanner(DB.getClipboard());
			sc.useDelimiter("HKD");
			sc.next();
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

			sc.close();
			sc2.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			sleep(1000);

			//dont know what this is actually
//			if (new Integer(time) < 91600){ //�ڦ��O�׫Y�\�Ĥ@��GET����clipboard�A�զh�X��
//				Global.addLog("Can't get quote, try again");
//				getQuote();
//			}
//			else {
//				Global.shutDown = true;
//				throw new FailGettingDataException();
//			}
			// getQuote();
		}
	}

	public void close() {
		DB.close();
		sleep(5000);
		// Sikuli.getSleep();
	}

	//cannot create table that starts with number
	//so string is added to the beginning of today
	private String createTable(String s) {
		String query = "CREATE TABLE " + s + Global.getToday() + "(MyIndex integer, "
				+ "TradeTime time, " + "Deal float, " + "Change float, "
				+ "TotalQuantity float, " + "BidQuantity integer, "
				+ "Bid float, Ask float, " + "AskQuantity integer)";

		System.out.println(query);

		try {
			DB.stmt.executeUpdate(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return s + Global.getToday();
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

//	private String getTime() {
//		return time;
//	}

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
