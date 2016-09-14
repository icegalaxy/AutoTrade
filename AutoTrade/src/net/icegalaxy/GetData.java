package net.icegalaxy;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetData implements Runnable {

	private static TimeBase shortTB;
	private static TimeBase m15TB;
	private static TimeBase longTB;
	// private static TimeBase sec10TB;
	private QuotePower qp;

	private boolean liquidated;

	GetData.CandleData shortData;
	GetData.CandleData m15Data;
	GetData.CandleData longData;

	private boolean aohAdded;
	// GetData.CandleData sec10Data;
	private float gap = 0;

	// private float previousClose = 0;

	// private static TimeBase sec5TB;

	public GetData() {
		Sikuli.makeRobot();
		shortTB = new TimeBase();
		shortTB.setBaseMin(Setting.getShortTB());
		m15TB = new TimeBase();
		m15TB.setBaseMin(15);
		longTB = new TimeBase();
		longTB.setBaseMin(Setting.getLongTB());

		// sec10TB = new TimeBase();

		qp = new QuotePower();
		// sec5TB = new TimeBase();

		shortData = new CandleData();
		m15Data = new CandleData();
		longData = new CandleData();
		// sec10Data = new CandleData();
	}

	private boolean getIndex() {

		try {
			
			//time of QuotePower.java is past from here everytime this method is called
			qp.setTime(time);
			qp.getQuote();
			deal = new Float(qp.getDeal());
			bid = new Float(qp.getBid());
			ask = new Float(qp.getAsk());

			// change = new Float(qp.getChange());

			totalQuantity = qp.getQuantity();

		} catch (FailGettingDataException e) {
			Global.addLog("Can't get index, shutDown!!");
			Global.shutDown = true;
			Global.setRunning(false);
			Sikuli.liquidateOnly();
			return false;

		} catch (Exception e) {
			Global.addLog("Can't get index, try again");
			System.out.println("Time: " + time);
			e.printStackTrace();
			sleep(1000);
			getIndex();
		}
		return true;
	}

	@Override
	public void run() {

		setOHLC();
		getPreviousData();

		while (Global.isRunning()) {

			time = getTime();
			Sikuli.capScreen(); // check if there is any errors to the feeder or
								// spTrader. Fix it by teamviewer

			if (Global.isTradeTime()) {

				if (!getIndex())
					return;

				// gap is 0 at the first time, so this must run at first time
				// When market opens, gap is 0 means the feeder may not be
				// functioning, so it will keep trying to get the open and gap
				if (gap == 0 && Global.getOpen() == 0) {
					gap = Float.valueOf(qp.getChange()); // getChange is moving,
															// when it comes
															// back to previous
															// close, the gap
															// becomes 0, so
															// Global.open is
															// added to the
															// clause

					Global.setGap(gap);
					// Global.setOpen(Double.parseDouble(qp.getDeal()));
					// Not setting open manually because this is faster, want to
					// catch the first wave
					// open needs to be set manually, difference can be large

					// Global.addLog("Open @ " + Global.getOpen());
					Global.addLog("Gap: " + gap);
					Global.addLog(" ");

				}

				// if (previousClose == 0){
				// previousClose = deal - gap;
				// Global.addLog("PreviousClose: " + previousClose);
				// Global.setPreviousClose(previousClose);
				// }

				sec = new Integer(time.substring(6, 8));

				// if (deal >= bid && deal <= ask)
				point = deal;
				// else if (deal < bid)
				// point = bid;
				// else if (deal > ask)
				// point = ask;

				shortData.getHighLow();
				shortData.getOpen();

				m15Data.getHighLow();
				m15Data.getOpen();

				longData.getHighLow();
				longData.getOpen();

				// sec10Data.getHighLow();
				// sec10Data.getOpen();

				if (sec == 59 || sec == 00 || sec == 01) {
					if (counter >= 0) {
						counter = -20;
						shortMinutes++;
						longMinutes++;
						m15Minutes++;
					}
				}

				// int remain = sec % 10;

				// if (remain == 9 || remain == 0 || remain == 1
				// && counter10Sec > 0) {
				// counter10Sec = -5;
				// getSec10TB().addData(point, totalQuantity);
				//
				// getSec10TB().addCandle(getTime(), sec10Data.periodHigh,
				// sec10Data.periodLow, sec10Data.openPt, point,
				// totalQuantity);
				//
				// sec10Data.reset();
				//
				// // System.out.println("Sec10 Up Slope: " +
				// // getSec10TB().getMainUpRail().getSlope());
				// // System.out.println("Sec10 Down Slope: " +
				// // getSec10TB().getMainDownRail().getSlope());
				// }

				if (shortMinutes == Setting.getShortTB()) {

					// getDayOpen, check every minutes
					setOpen();
					// get noonOpen, check every minutes
					if (Global.isNoonOpened)
						setNoonOpen();

					getShortTB().addData(point, totalQuantity);

					getShortTB().addCandle(getTime(), shortData.periodHigh,
							shortData.periodLow, shortData.openPt, point,
							totalQuantity);

					System.out.println(getTime() + " " + point);
					System.out.println("MA10: " + getShortTB().getMA(10));
					System.out.println("MA20: " + getShortTB().getMA(20));

					shortMinutes = 0;
					shortData.reset();

					if (Global.getAOH() == 0)
						setAOHL();

				}

				if (m15Minutes == 15) {

					getM15TB().addData(point, totalQuantity);

					getM15TB().addCandle(getTime(), m15Data.periodHigh,
							m15Data.periodLow, m15Data.openPt, point,
							totalQuantity);
					m15Minutes = 0;
					m15Data.reset();

					if (!aohAdded) {
						Global.setAOL(getM15TB().getHL(1).getTempLow());
						Global.setAOH(getM15TB().getHL(1).getTempHigh());
						Global.addLog("AOL: "
								+ getM15TB().getHL(1).getTempLow());
						Global.addLog("AOH: "
								+ getM15TB().getHL(1).getTempHigh());
						aohAdded = true;
					}
				}

				if (longMinutes == Setting.getLongTB()) {

					// addDat = addPoint + quantity
					getLongTB().addData(point, totalQuantity);

					getLongTB().addCandle(getTime(), longData.periodHigh,
							longData.periodLow, longData.openPt, point,
							totalQuantity);

					getLongTB().getMACD();
					// System.out.println("MACD Histo: "
					// + getLongTB().getMACDHistogram());
					longMinutes = 0;
				}

				setGlobal();
				counter++;
				// counter10Sec++;
			}

			sleep(860);

			if (!Global.isTradeTime())
				counter = 1;

//			if (getTimeInt() > 161400 && !liquidated) {
//				Sikuli.liquidateOnly();
//				liquidated = true;
//			}

		}

		qp.close();

	}

	private void getPreviousData() {

		CSVParser csv = new CSVParser("Z:\\TradeData\\5minOHLC.csv");
		csv.parseOHLC();
		int j = 0;

		for (int i = 0; i < csv.getLow().size(); i++) {

			// addPoint is for technical indicators
			getLongTB().addData(csv.getClose().get(i).floatValue(),
					csv.getVolume().get(i).floatValue());
			// addCandle History is made for previous data, volume is not
			// accumulated
			getLongTB().addCandleHistory(csv.getTime().get(i),
					csv.getHigh().get(i), csv.getLow().get(i),
					csv.getOpen().get(i), csv.getClose().get(i),
					csv.getVolume().get(i));

			j++;
			if (j == 3) {
				getM15TB().addData(csv.getClose().get(i).floatValue(),
						csv.getVolume().get(i).floatValue());
				getM15TB().addCandleHistory(csv.getTime().get(i),
						csv.getHigh().get(i), csv.getLow().get(i),
						csv.getOpen().get(i), csv.getClose().get(i),
						csv.getVolume().get(i));
				j = 0;
			}

		}

	}

	private void setOHLC() {

		XMLReader ohlc = new XMLReader(Global.getToday());
		Global.setpHigh(ohlc.getpHigh());
		Global.setpLow(ohlc.getpLow());
		Global.setpOpen(ohlc.getpOpen());
		Global.setpClose(ohlc.getpClose());
		Global.setpFluc(ohlc.getpFluc());

	}

	private void setOpen() {

		if (Global.getOpen() == 0) {
			XMLReader open = new XMLReader(Global.getToday());
			if (open.getOpen() == 0)
				return;
			Global.setOpen(open.getOpen());
		}

	}

	private void setNoonOpen() {

		if (Global.getNoonOpen() == 0) {
			XMLReader noon = new XMLReader(Global.getToday());
			if (noon.getnOpen() == 0)
				return;
			Global.setNoonOpen(noon.getnOpen());
		}

	}

	private void setAOHL() {
		XMLReader aohl = new XMLReader(Global.getToday());
		Global.setAOH(aohl.getAOH());
		Global.setAOL(aohl.getAOL());
	}

	private void setGlobal() {

		Global.setCurrentPoint(point);
		Global.setCurrentBid(bid);
		Global.setCurrentAsk(ask);
		Global.setCurrentDeal(deal);

		if (Global.getDayHigh() < point)
			Global.setDayHigh(point);

		if (Global.getDayLow() > point)
			Global.setDayLow(point);

		if (Global.getDayHigh() - Global.getDayLow() < Global.getCurrentPoint() / 100)
			Global.setLowFluctuation(true);
		else
			Global.setLowFluctuation(false);

	}

	public static synchronized TimeBase getShortTB() {
		return shortTB;
	}

	public static synchronized TimeBase getM15TB() {
		return m15TB;
	}

	public static synchronized TimeBase getLongTB() {
		return longTB;
	}

	// public static synchronized TimeBase getSec10TB() {
	// return sec10TB;
	// }

	// public static synchronized TimeBase getSec5TB() {
	// return sec5TB;
	// }

	public static String getTime() { // String is thread safe
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String time = new String(formatter.format(now.getTime()));

		return time;
	}

	public static int getTimeInt() {

		return new Integer(time.replaceAll(":", ""));

	}

	public static void sleep(int miniSecond) {

		try {
			Thread.sleep(miniSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	static String time = getTime();
	InputStream is;
	int sec = 1;
	int min = 0;
	int macdMin = 0;
	int timeInFormat;
	float bid;
	float ask;
	float deal;

	private int shortMinutes;
	private int longMinutes;
	private int m15Minutes;

	private int counter = 1;
	// private int counter10Sec = 0;
	// private int counter5Sec = 0;

	float dealPt;
	float askPt;
	float bidPt;

	Float point = new Float(0);
	float totalQuantity = 0;

	class CandleData {
		private double periodHigh = 0.0D;
		private double periodLow = 99999.0D;
		private double openPt = 0.0D;
		private boolean openAdded = false;

		void reset() {

			periodHigh = 0.0D;
			periodLow = 99999.0D;
			openAdded = false;
		}

		void getHighLow() {
			if (point > periodHigh)
				periodHigh = point;
			if (point < periodLow)
				periodLow = point;
		}

		void getOpen() {
			if (!openAdded) {
				openPt = point;
				openAdded = true;
			}
		}

	}

}
