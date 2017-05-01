package net.icegalaxy;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class GetData implements Runnable
{

	private static TimeBase shortTB;
	private static TimeBase m15TB;
	private static TimeBase longTB;
	// private static TimeBase sec10TB;
	private QuotePower qp;
	
	public static OHLC AOL;
	public static OHLC AOH;
	
	GetData.CandleData shortData;
	GetData.CandleData m15Data;
	GetData.CandleData longData;
	
	int[] EMAs = new int[]
			{ 5, 25, 50, 100, 250, 1200 };

	private boolean aohAdded;
	// GetData.CandleData sec10Data;
	
//	private static EMA ema5;
//	private static EMA ema25;
//	private static EMA ema50;
//	private static EMA ema100;
//	private static EMA ema250;
//	private static EMA ema1200;
	private int errTimes;

	// private float previousClose = 0;

	// private static TimeBase sec5TB;

	public GetData()
	{
//		Sikuli.makeRobot();
		shortTB = new TimeBase();
		shortTB.setBaseMin(Setting.getShortTB());
		m15TB = new TimeBase();
		m15TB.setBaseMin(15);
		longTB = new TimeBase();
		longTB.setBaseMin(Setting.getLongTB());
		
	
		AOH = new OHLC();
		AOH.name = "AOH";
		AOL = new OHLC();
		AOL.name = "AOL";
	
		// sec10TB = new TimeBase();

		qp = new QuotePower();
		// sec5TB = new TimeBase();

		shortData = new CandleData();
		m15Data = new CandleData();
		longData = new CandleData();
		// sec10Data = new CandleData();
	}

	private boolean getIndex()
	{

		try
		{

			// time of QuotePower.java is past from here everytime this method
			// is called
			qp.setTime(time);
			qp.getQuote();
			deal = new Float(qp.getDeal());
			bid = new Float(qp.getBid());
			ask = new Float(qp.getAsk());

			if (deal == 0)
			{

				Global.addLog("Deal = 0, try again");
				System.out.println("Time: " + time);
				sleep(1000);
				return false;
			}

			// change = new Float(qp.getChange());

			totalQuantity = qp.getQuantity();

		} catch (FailGettingDataException e)
		{
			Global.addLog("Can't get index, shutDown!!");
			Global.shutDown = true;
			Global.setRunning(false);
		//	Sikuli.liquidateOnly();
			return false;

		} catch (Exception e)
		{
			
			if (errTimes > 50)
			{
				Global.addLog("errTimes > 50");
				Global.addLog("Force Close");
				Global.shutDown = true;
				Global.setRunning(false);
				return false;
			}
			Global.addLog("Can't get index, try again");
			System.out.println("Time: " + time);
			e.printStackTrace();
			sleep(1000);
			getIndex();
			errTimes++;
		}
		return true;
	}

	

	@Override
	public void run()
	{

	
		getPreviousData();

		// Auto getOpen

		while (Global.isRunning())
		{

			time = getTime();
			// Sikuli.capScreen(); // check if there is any errors to the feeder
			// or
			// spTrader. Fix it by teamviewer

			// should be put inside isRunning
		

			if (Global.isTradeTime())
			{

				// this is for quote power
				if (!getIndex())
					continue;

				min = new Integer(time.substring(3, 5));

		
				point = deal;
					
				shortData.getHighLow();
				shortData.getOpen();

				m15Data.getHighLow();
				m15Data.getOpen();

				longData.getHighLow();
				longData.getOpen();
				
				try
				{
				if (getTimeInt() > 92000)
				{
					if (Global.getCurrentPoint() - getShortTB().getLatestCandle().getLow() > 
					(shortTB.getHL(15).getTempHigh() - shortTB.getHL(15).getTempLow()) * 0.5)
						Global.setRapidRise(true);
					else
						Global.setRapidRise(false);

					if (getShortTB().getLatestCandle().getHigh() - Global.getCurrentPoint() > 
					(shortTB.getHL(15).getTempHigh() - shortTB.getHL(15).getTempLow()) * 0.5)
						Global.setRapidDrop(true);
					else
						Global.setRapidDrop(false);
				}
				}catch (Exception e)
				{
					e.printStackTrace();
				}
				
				try
				{
				if (TimePeriodDecider.getTime() >= 91600)
				{
					if (shortData.periodHigh - getShortTB().getLatestCandle().getLow() > 50
//							|| longData.periodHigh - getLongTB().getLatestCandle().getLow() > 50 
							)
					{
						Global.setHugeRise(true);
					}
					else
						Global.setHugeRise(false);

					if (getShortTB().getLatestCandle().getHigh() - shortData.periodLow > 50
//							|| getLongTB().getLatestCandle().getHigh() - longData.periodLow  > 50 
							)
					{
						Global.setHugeDrop(true);
					}
					else
						Global.setHugeDrop(false);
				}
				}catch (Exception e)
				{
					e.printStackTrace();
				}

				// sec10Data.getHighLow();
				// sec10Data.getOpen();

				// min will be add at first 01 sec and first 59 sec, so there
				// will be one more record
				// everyMin should be initialed as -1

				// if (min == 59 || min == 00 || min == 01) {
				// if (counter >= 0) {
				// counter = -20;
				// shortMinutes++;
				// longMinutes++;
				// m15Minutes++;
				// }
				// }

				// that Math.abs is for when min = 59 and ref = -1
				// use 10 in case the feeder stopped for serval mins
				if (min > refMin && Math.abs(min - refMin) < 10)
				{

					shortMinutes++;
					longMinutes++;
					m15Minutes++;

					if (refMin == 58)
						refMin = -1;
					else
						refMin = min;
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

				if (!aohAdded)
				{

					if (getTimeInt() >= 93000)
					{
						Global.setAOL(Global.getDayLow());
						Global.setAOH(Global.getDayHigh());
						Global.addLog("AOL: " + Global.getAOL());
						Global.addLog("AOH: " + Global.getAOH());
						
						AOH.position = Global.getAOH();
						AOL.position = Global.getAOL();
						
						aohAdded = true;
					}
				}

				if (shortMinutes == Setting.getShortTB())
				{

					if (Global.getOpen() == 0)
					{
						Global.setOpen(SPApi.getAPIPrice().Open);
						Global.addLog("Set open after 91500 at: " + Global.getOpen());
					}


//					if (Global.getpHigh() == 0)
//					{
//						setOHLC();
//
//					}

					// get noonOpen, check every minutes
//					if (Global.isNoonOpened)
//						setNoonOpen();

					getShortTB().addData(point, new Float(totalQuantity));

					getShortTB().addCandle(getTime(), shortData.periodHigh, shortData.periodLow, shortData.openPt,
							point, totalQuantity);

					for (int x = 0; x < shortTB.EMAs.length; x++)
						shortTB.EMAs[x].setlatestEMA(point);

					System.out.println(getTime() + " " + point);
//					System.out.println("MA10: " + getShortTB().getMA(10));
//					System.out.println("MA20: " + getShortTB().getMA(20));

					shortMinutes = 0;
					shortData.reset();

//					if (Global.getAOH() == 0)
//						setAOHL();
					
					

				}

				if (m15Minutes == 15)
				{

					getM15TB().addData(point, new Float(totalQuantity));

					getM15TB().addCandle(getTime(), m15Data.periodHigh, m15Data.periodLow, m15Data.openPt, point,
							totalQuantity);
					m15Minutes = 0;
					m15Data.reset();

				}

				if (longMinutes == Setting.getLongTB())
				{

					for (int x = 0; x < longTB.EMAs.length; x++)
						longTB.EMAs[x].setlatestEMA(point);
					
					// addDat = addPoint + quantity
					getLongTB().addData(point, new Float(totalQuantity));

					getLongTB().addCandle(getTime(), longData.periodHigh, longData.periodLow, longData.openPt, point,
							totalQuantity);

					getLongTB().getMACD();
					// System.out.println("MACD Histo: "
					// + getLongTB().getMACDHistogram());
					longMinutes = 0;
					longData.reset();
				}

			
			}

			sleep(860);

//			if (!Global.isTradeTime())
//			{
				// counter = 1;
//			}
			// if (getTimeInt() > 161400 && !liquidated) {
			// Sikuli.liquidateOnly();
			// liquidated = true;
			// }

		}

		qp.close();

	}

//	private void checkStop()
//	{
//		XMLReader ohlc = new XMLReader(Global.getToday());
//
//		if (ohlc.isStop())
//			Global.setRunning(false);
//
//	}

	private void getPreviousData()
	{

		parseSPRecord csv = new parseSPRecord("C:\\Users\\joech\\Dropbox\\TradeData\\SPRecords\\" + Global.getToday() + "\\m1.txt");
		csv.parseOHLC();
		
		int m5Period = 0;

		for (int i = 0; i < csv.getLow().size(); i++)
		{

			Double close = csv.getClose().get(i);
			// addPoint is for technical indicators
			getShortTB().addData(close.floatValue(), csv.getVolume().get(i).floatValue());
			// addCandle History is made for previous data, volume is not
			// accumulated
			getShortTB().addCandleHistory(csv.getTime().get(i), csv.getHigh().get(i), csv.getLow().get(i),
					csv.getOpen().get(i), close, csv.getVolume().get(i));

			if (i == 0)
			{
				for (int x = 0; x < EMAs.length; x++)
				{
					shortTB.EMAs[x] = new EMA(close, EMAs[x]); // creating new
																// instance of
																// EMA and set
																// it to the EMA
																// is array,
																// changing it's
																// reference. It
																// is not the
																// field in
																// shortTB
																// anymore
					longTB.EMAs[x] = new EMA(close, EMAs[x]);

				}
			} else
			{
				for (int x = 0; x < EMAs.length; x++)
				{
					shortTB.EMAs[x].setlatestEMA(close);
					// System.out.println("settting latest EMA" + EMAs[x]);
				}
				
				m5Period++;

				if (m5Period == 5)
				{
					for (int x = 0; x < EMAs.length; x++)
						longTB.EMAs[x].setlatestEMA(close);

					m5Period = 0;
				}
			}
//			j++;
//			k++;
//
//			if (j == 5)
//			{
//				getLongTB().addData(close.floatValue(), csv.getVolume().get(i).floatValue());
//				getLongTB().addCandleHistory(csv.getTime().get(i), csv.getHigh().get(i), csv.getLow().get(i),
//						csv.getOpen().get(i), close, csv.getVolume().get(i));
//				j = 0;
//			}
//
//			if (k == 15)
//			{
//				getM15TB().addData(close.floatValue(), csv.getVolume().get(i).floatValue());
//				getM15TB().addCandleHistory(csv.getTime().get(i), csv.getHigh().get(i), csv.getLow().get(i),
//						csv.getOpen().get(i), close, csv.getVolume().get(i));
//				k = 0;
//			}

		}
		
		Global.addLog("Previous m1_EMA250: " + getEma250().getEMA());

	}

	public static EMA getEma5()
	{
		return shortTB.EMAs[0];
	}

	public static EMA getEma25()
	{
		return shortTB.EMAs[1];
	}

	public static EMA getEma50()
	{
		return shortTB.EMAs[2];
	}

	public static EMA getEma100()
	{
		return shortTB.EMAs[3];
	}

	public static EMA getEma250()
	{
		return shortTB.EMAs[4];
	}

	public static EMA getEma1200()
	{
		return shortTB.EMAs[5];
	}

	


//	private void setNoonOpen()
//	{
//
//		if (Global.getNoonOpen() == 0)
//		{
//			XMLReader noon = new XMLReader(Global.getToday());
//			if (noon.getnOpen() == 0)
//				return;
//			Global.setNoonOpen(noon.getnOpen());
//		}
//
//	}
//
//	private void setAOHL()
//	{
//		XMLReader aohl = new XMLReader(Global.getToday());
//		Global.setAOH(aohl.getAOH());
//		Global.setAOL(aohl.getAOL());
//	}

	

	public static synchronized TimeBase getShortTB()
	{
		return shortTB;
	}

	public static synchronized TimeBase getM15TB()
	{
		return m15TB;
	}

	public static synchronized TimeBase getLongTB()
	{
		return longTB;
	}

	// public static synchronized TimeBase getSec10TB() {
	// return sec10TB;
	// }

	// public static synchronized TimeBase getSec5TB() {
	// return sec5TB;
	// }
	
	public String getYearMonth() {

		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMM");
		String s = formatter.format(now.getTime());

		return s;
	}

	public static String getTime()
	{ // String is thread safe
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String time = new String(formatter.format(now.getTime()));

		return time;
	}

	public static int getTimeInt()
	{

		return new Integer(time.replaceAll(":", ""));

	}

	public static void sleep(int miniSecond)
	{

		try
		{
			Thread.sleep(miniSecond);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

	static String time = getTime();
	InputStream is;
	int min;
	int refMin = 15;

	int macdMin = 0;
	int timeInFormat;
	float bid;
	float ask;
	float deal;

	// to add the first data as a candle
	private int shortMinutes = 1;
	private int longMinutes = 5;
	private int m15Minutes = 15;

	private int counter = 1;
	// private int counter10Sec = 0;
	// private int counter5Sec = 0;

	float dealPt;
	float askPt;
	float bidPt;

	Float point = new Float(0);
	double totalQuantity = 0;

	class CandleData
	{
		private double periodHigh = 0.0D;
		private double periodLow = 99999.0D;
		private double openPt = 0.0D;
		private boolean openAdded = false;

		void reset()
		{

			periodHigh = 0.0D;
			periodLow = 99999.0D;
			openAdded = false;
		}

		void getHighLow()
		{
			if (point > periodHigh)
				periodHigh = point;
			if (point < periodLow)
				periodLow = point;
		}

		void getOpen()
		{
			if (!openAdded)
			{
				openPt = point;
				openAdded = true;
			}
		}

	}

}
