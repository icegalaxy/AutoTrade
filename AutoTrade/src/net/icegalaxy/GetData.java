package net.icegalaxy;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GetData implements Runnable
{

	ArrayList<RSIData> rsiDatas;
	
	public static double minuteHigh = 0;
	public static double minuteLow = 99999;

	private static TimeBase shortTB;
	private static TimeBase m15TB;
	private static TimeBase longTB;
	
	private double previousPoint;
	private int samePointCount;
	
	// private static TimeBase sec10TB;
	private QuotePower qp;

	public static OHLC AOL;
	public static OHLC AOH;

	GetData.CandleData shortData;
	GetData.CandleData m15Data;
	GetData.CandleData longData;
	
	public static HighLow nanoHL;
	public static HighLow tinyHL;
	public static HighLow smallHL;
	public static HighLow largeHL;
	
	private ArrayList<HighLow> HLs;

	int[] EMAs = new int[]
	{ 5, 25, 50, 100, 250, 1200 };

	private boolean aohAdded;
	// GetData.CandleData sec10Data;

	// private static EMA ema5;
	// private static EMA ema25;
	// private static EMA ema50;
	// private static EMA ema100;
	// private static EMA ema250;
	// private static EMA ema1200;
	private int errTimes;

	private boolean createdLog;

	// private float previousClose = 0;

	// private static TimeBase sec5TB;

	public GetData()
	{
		nanoHL = new HighLow("NanoHL",0.15);
		tinyHL = new HighLow("TinyHL",0.3);
		smallHL = new HighLow("SmallHL",0.5);
		largeHL = new HighLow("LargeHL",1);
		
		HLs = new ArrayList<HighLow>();
		
		HLs.add(nanoHL);
		HLs.add(tinyHL);
		HLs.add(smallHL);
		HLs.add(largeHL);
		
		// Sikuli.makeRobot();
		rsiDatas = new ArrayList<RSIData>();
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

			totalQuantity = Global.getTurnOverVol();

		} catch (FailGettingDataException e)
		{
			Global.addLog("Can't get index, shutDown!!");
			Global.shutDown = true;
			Global.setRunning(false);
			// Sikuli.liquidateOnly();
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
		
//		checkEMA50(); cannot check here, stairs not ready
//		checkEMA250();

		XMLWatcher xmlWatcher = new XMLWatcher();
		Thread t = new Thread(xmlWatcher);
		t.start();

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

//				if(!createdLog)
//				{
//					XMLWatcher.csvLog = new MyFile("C:\\Users\\joech\\Dropbox\\TradeData\\csvLog " + Global.getToday() + ".csv");
//					XMLWatcher.csvLog.fileString.append("Time,Caller,Message,Remark\r\n");
//					XMLWatcher.files.add(XMLWatcher.csvLog);
//					
//					XMLWatcher.csvLog.writeToFile();	
//					createdLog = true;
//				}

				
				// this is for quote power
				if (!getIndex())
				{
					SPApi.unSubscribePrice();
					samePointCount = 0;
					sleep(10000);
					SPApi.subscribePrice();
					sleep(10000);
					
					continue;
				}
				
				if (Global.getCurrentPoint() != previousPoint)
				{
					previousPoint = Global.getCurrentPoint();
					samePointCount = 0;
				}
				else
					samePointCount++;

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
						if (Global.getCurrentPoint() - getShortTB().getLatestCandle()
								.getLow() > (shortTB.getHL(15).getTempHigh() - shortTB.getHL(15).getTempLow()) * 0.5)
							Global.setRapidRise(true);
						else
							Global.setRapidRise(false);

						if (getShortTB().getLatestCandle().getHigh() - Global
								.getCurrentPoint() > (shortTB.getHL(15).getTempHigh() - shortTB.getHL(15).getTempLow())
										* 0.5)
							Global.setRapidDrop(true);
						else
							Global.setRapidDrop(false);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					if (TimePeriodDecider.getTime() >= 91600)
					{
						if (shortData.periodHigh - getShortTB().getLatestCandle().getLow() > 50
						// || longData.periodHigh -
						// getLongTB().getLatestCandle().getLow() > 50
						)
						{
							Global.setHugeRise(true);
						} else
							Global.setHugeRise(false);

						if (getShortTB().getLatestCandle().getHigh() - shortData.periodLow > 50
						// || getLongTB().getLatestCandle().getHigh() -
						// longData.periodLow > 50
						)
						{
							Global.setHugeDrop(true);
						} else
							Global.setHugeDrop(false);
					}
				} catch (Exception e)
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

			

				// if (min > refMin && Math.abs(min - refMin) < 10)
				// {
				//
				// shortMinutes++;
				// longMinutes++;
				// m15Minutes++;
				//
				// if (refMin == 58)
				// refMin = -1;
				// else
				// refMin = min;
				// }

				if (min != refMin)
				{

					shortMinutes++;
					longMinutes++;
					m15Minutes++;

					System.out.println("Updating... min: " + min);
					System.out.println("shortMinutes: " + shortMinutes);
					System.out.println("longMinutes: " + longMinutes);
					System.out.println("m15Minutes: " + m15Minutes);
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

				// if (shortMinutes == Setting.getShortTB())
				if (shortMinutes >= 1)
				{

					if (Global.getOpen() == 0)
					{
						Global.setOpen(SPApi.getAPIPrice().Open);
						Global.addLog("Set open after 91500 at: " + Global.getOpen());
					}

					for (MyFile file: XMLWatcher.files)
					{
						if(file.isStringModified())
						{
							file.writeToFile();
							file.previousModifiedTime = file.lastModified();
						}
					}
					
					// if (Global.getpHigh() == 0)
					// {
					// setOHLC();
					//
					// }

					// get noonOpen, check every minutes
					// if (Global.isNoonOpened)
					// setNoonOpen();

					getShortTB().addData(point, new Float(totalQuantity));

//					getShortTB().addCandle(getTime(), shortData.periodHigh, shortData.periodLow, shortData.openPt,
//							point, totalQuantity);
					
					getShortTB().addCandle(getTime(), minuteHigh, minuteLow, shortData.openPt,
							point, totalQuantity);
					
					minuteHigh = 0;
					minuteLow = 99999;

					for (int x = 0; x < shortTB.EMAs.length; x++)
						shortTB.EMAs[x].setlatestEMA(point);

					System.out.println(getTime() + " " + point);
					// System.out.println("MA10: " + getShortTB().getMA(10));
					// System.out.println("MA20: " + getShortTB().getMA(20));

					shortMinutes = 0;
					shortData.reset();
					
					for (HighLow hl : HLs)
					{
						if (hl.findingLow)
							hl.findLow(hl.objectName);
						if (hl.findingHigh)
							hl.findHigh(hl.objectName);
						
						if (hl.findingLow) // do it one more time in case it makes a high and a low in one candle
							hl.findLow(hl.objectName);
					}

					//check connection
					if (!Global.isConnectionOK())
					{
						Global.addLog("Conncetion fail, try re-login");
						
						SPApi.unInit();
						
						sleep(10000);
						
						SPApi.init();
						
						sleep(20000);
					}
					
					

					// if (Global.getAOH() == 0)
					// setAOHL();

					// updateStair();
					// updateRSIData();

				}

				if (m15Minutes >= 15)
				{
					
					try {
						Global.addLog("Tiny Up Trend: " + tinyHL.getUpTrend() + "; Nano Up Trend: " + nanoHL.getUpTrend());
						Global.addLog("Tiny Down Trend: " + tinyHL.getDownTrend() + "; Nano Down Trend: " + nanoHL.getDownTrend());
					}catch (Exception e) {
						e.printStackTrace();
					}

					getM15TB().addData(point, new Float(totalQuantity));

					getM15TB().addCandle(getTime(), m15Data.periodHigh, m15Data.periodLow, m15Data.openPt, point,
							totalQuantity);
					m15Minutes = 0;
					m15Data.reset();

				}

				// if (longMinutes == Setting.getLongTB())
				if (longMinutes >= 5)
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

					XMLWatcher.stairs.get(0).value = GetData.getLongTB().getEma50().getEMA();
					XMLWatcher.stairs.get(1).value = GetData.getLongTB().getEma250().getEMA();
//					checkEMA50();
//					checkEMA250();
				}
			}

			sleep(860);

			// if (!Global.isTradeTime())
			// {
			// counter = 1;
			// }
			// if (getTimeInt() > 161400 && !liquidated) {
			// Sikuli.liquidateOnly();
			// liquidated = true;
			// }

		}

		qp.close();

	}

	

	// private void checkStop()
	// {
	// XMLReader ohlc = new XMLReader(Global.getToday());
	//
	// if (ohlc.isStop())
	// Global.setRunning(false);
	//
	// }

	private void updateRSIData()
	{

	}

	private void updateStair()
	{
		for (int i = 0; i < XMLWatcher.stairs.size(); i++)
		{
			double high = getShortTB().getLatestCandle().getHigh();
			double low = getShortTB().getLatestCandle().getLow();

			if (high > XMLWatcher.stairs.get(i).value && high < XMLWatcher.stairs.get(i).value + 50
					&& high > XMLWatcher.stairs.get(i).refHigh)
			{
				XMLWatcher.stairs.get(i).refHigh = high;

			} else if (high > XMLWatcher.stairs.get(i).value + 50 && high < XMLWatcher.stairs.get(i).value + 100
					&& XMLWatcher.stairs.get(i).selling)
			{
				XMLWatcher.stairs.get(i).selling = false;
				RuleSkyStair.shutdownStair(i);
				Global.addLog("ShutDown Selling: " + XMLWatcher.stairs.get(i).lineType + " @ "
						+ XMLWatcher.stairs.get(i).value);
			}

			if (low < XMLWatcher.stairs.get(i).value && low > XMLWatcher.stairs.get(i).value - 50
					&& low < XMLWatcher.stairs.get(i).refLow)
			{
				XMLWatcher.stairs.get(i).refLow = low;

			} else if (low < XMLWatcher.stairs.get(i).value - 50 && low > XMLWatcher.stairs.get(i).value - 100
					&& XMLWatcher.stairs.get(i).buying)
			{
				XMLWatcher.stairs.get(i).buying = false;
				RuleSkyStair.shutdownStair(i);
				Global.addLog("ShutDown Buying: " + XMLWatcher.stairs.get(i).lineType + " @ "
						+ XMLWatcher.stairs.get(i).value);
			}
		}

	}

	private void getPreviousData()
	{

		// parseSPRecord csv = new
		// parseSPRecord("C:\\Users\\joech\\Dropbox\\TradeData\\SPRecords\\" +
		// Global.getToday() + "\\m1.txt");
		parseSPRecord csv = new parseSPRecord("C:\\Users\\joecheung0\\Dropbox\\TradeData\\SPRecords\\m1.txt");
		csv.parseOHLC();

		int m5Period = -4;

		for (int i = 0; i < csv.getLow().size(); i++)
		{

			double close = csv.getClose().get(i);
			double open = csv.getOpen().get(i);
			double high = csv.getHigh().get(i);
			double low = csv.getLow().get(i);
			double volume = csv.getVolume().get(i);
			String time = csv.getTime().get(i);
			// addPoint is for technical indicators
			getShortTB().addData(csv.getClose().get(i).floatValue(), csv.getVolume().get(i).floatValue());
			// addCandle History is made for previous data, volume is not
			// accumulated
			getShortTB().addCandleHistory(time,high,low,open, close, volume);

//			if (smallHL.findingLow)
//				smallHL.findLow();
//			if (smallHL.findingHigh)
//				smallHL.findHigh();
//			
//			if (largeHL.findingLow)
//				largeHL.findLow();
//			if (largeHL.findingHigh)
//				largeHL.findHigh();
			
			for (HighLow hl : HLs)
			{
				if (hl.findingLow)
					hl.findLow();
				if (hl.findingHigh)
					hl.findHigh();
			}
			
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

					longTB.EMAs[x] = new EMA(csv.getClose().get(4), EMAs[x]);

					// longTB.EMAs[x] = new EMA(close, EMAs[x]);

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
			// j++;
			// k++;
			//
			// if (j == 5)
			// {
			// getLongTB().addData(close.floatValue(),
			// csv.getVolume().get(i).floatValue());
			// getLongTB().addCandleHistory(csv.getTime().get(i),
			// csv.getHigh().get(i), csv.getLow().get(i),
			// csv.getOpen().get(i), close, csv.getVolume().get(i));
			// j = 0;
			// }
			//
			// if (k == 15)
			// {
			// getM15TB().addData(close.floatValue(),
			// csv.getVolume().get(i).floatValue());
			// getM15TB().addCandleHistory(csv.getTime().get(i),
			// csv.getHigh().get(i), csv.getLow().get(i),
			// csv.getOpen().get(i), close, csv.getVolume().get(i));
			// k = 0;
			// }

		}

		Global.addLog("Previous m1_EMA250: " + getEma250().getEMA());

		try
		{
			Global.addLog("Previous m5_EMA250: " + GetData.getLongTB().getEma250().getEMA());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Global.addLog("Latest High (Large): " + largeHL.getLatestHigh());
		Global.addLog("Latest Low (Large): " + largeHL.getLatestLow());

	}

	private void checkEMA50()
	{

		if (getTimeInt() < XMLWatcher.stairs.get(0).reActivateTime)
			return;

		if (GetData.getLongTB().getEma5().getEMA() > GetData.getLongTB().getEma50().getEMA() + 50
				&& GetData.getLongTB().getEma50().getEMA() > GetData.getLongTB().getEma250().getEMA() + 100)
		{

			// if (Global.getCurrentPoint() >
			// GetData.getLongTB().getEma50().getEMA())
			// {
			XMLWatcher.stairs.get(0).buying = true;
			XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = true;
			// XMLWatcher.stairs.get(1).selling = false;
			// }else if (Global.getCurrentPoint() >
			// GetData.getLongTB().getEma250().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = true;
			// XMLWatcher.stairs.get(1).selling = false;
			// }
			// else
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = false;
			// }

		} else if (GetData.getLongTB().getEma5().getEMA() < GetData.getLongTB().getEma50().getEMA() - 50
				&& GetData.getLongTB().getEma50().getEMA() < GetData.getLongTB().getEma250().getEMA() - 100)
		{
			// if (Global.getCurrentPoint() <
			// GetData.getLongTB().getEma50().getEMA())
			// {
			XMLWatcher.stairs.get(0).buying = false;
			XMLWatcher.stairs.get(0).selling = true;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = true;
			// }else if (Global.getCurrentPoint() <
			// GetData.getLongTB().getEma250().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = true;
			// }else
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = false;
			// }

		} else
		{
			XMLWatcher.stairs.get(0).buying = false;
			XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = false;
		}

	}

	private void checkEMA250()
	{

		if (getTimeInt() < XMLWatcher.stairs.get(1).reActivateTime)
			return;

		if (GetData.getLongTB().getEma50().getEMA() > GetData.getLongTB().getEma250().getEMA() + 50)
		{

			// if (Global.getCurrentPoint() >
			// GetData.getLongTB().getEma50().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = true;
			// XMLWatcher.stairs.get(0).selling = false;
			XMLWatcher.stairs.get(1).buying = true;
			XMLWatcher.stairs.get(1).selling = false;
			// }else if (Global.getCurrentPoint() >
			// GetData.getLongTB().getEma250().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = true;
			// XMLWatcher.stairs.get(1).selling = false;
			// }
			// else
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = false;
			// }

		} else if (GetData.getLongTB().getEma50().getEMA() < GetData.getLongTB().getEma250().getEMA() - 50)
		{
			// if (Global.getCurrentPoint() <
			// GetData.getLongTB().getEma50().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = true;
			XMLWatcher.stairs.get(1).buying = false;
			XMLWatcher.stairs.get(1).selling = true;
			// }else if (Global.getCurrentPoint() <
			// GetData.getLongTB().getEma250().getEMA())
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = true;
			// }else
			// {
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			// XMLWatcher.stairs.get(1).buying = false;
			// XMLWatcher.stairs.get(1).selling = false;
			// }

		} else
		{
			// XMLWatcher.stairs.get(0).buying = false;
			// XMLWatcher.stairs.get(0).selling = false;
			XMLWatcher.stairs.get(1).buying = false;
			XMLWatcher.stairs.get(1).selling = false;
		}

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

	// private void setNoonOpen()
	// {
	//
	// if (Global.getNoonOpen() == 0)
	// {
	// XMLReader noon = new XMLReader(Global.getToday());
	// if (noon.getnOpen() == 0)
	// return;
	// Global.setNoonOpen(noon.getnOpen());
	// }
	//
	// }
	//
	// private void setAOHL()
	// {
	// XMLReader aohl = new XMLReader(Global.getToday());
	// Global.setAOH(aohl.getAOH());
	// Global.setAOL(aohl.getAOL());
	// }

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

	public String getYearMonth()
	{

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

		int t = new Integer(time.replaceAll(":", ""));

		// for 0-2 a.m.
		if (t < 20000)
			t = t + 1000000;

		return t;

	}

	public static long getTimeInSec()
	{
		Calendar now = Calendar.getInstance();
		long timeInMillis = now.getTimeInMillis();
		return timeInMillis / 1000;
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
	int refMin = 59;

	int macdMin = 0;
	int timeInFormat;
	float bid;
	float ask;
	float deal;

	// to add the first data as a candle
	private int shortMinutes = 0;
	private int longMinutes = 4;
	private int m15Minutes = 14;

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
