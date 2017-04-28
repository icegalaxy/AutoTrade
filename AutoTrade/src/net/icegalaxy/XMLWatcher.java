package net.icegalaxy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//Use the OPEN Line

public class XMLWatcher implements Runnable
{

	public static IntraDayReader intraDay;
	XMLReader ohlc;
	static String intraDayXMLPath = "C:\\Users\\joech\\Dropbox\\TradeData\\Intraday.xml";

	public static double rangeResist = 0;
	public static double rangeSupport = 0;

	public static OHLC open;
	public static OHLC pHigh;
	public static OHLC pLow;
	public static OHLC pClose;
	public static OHLC mySupport;
	public static OHLC myResist;
	public static OHLC mySAR;

	public static double SAR = 0;
	public static double cutLoss = 0;
	public static double stopEarn = 0;
	public static double reverse = 0;
	public static boolean buying;
	public static boolean selling;

	private long fileModifiedTime;

	private int secCounter;

	public XMLWatcher()
	{

		fileModifiedTime = new File("FHIdata.xml").lastModified();

		intraDay = new IntraDayReader(Global.getToday(), intraDayXMLPath);

		open = new OHLC();
		open.name = "Open";
		pHigh = new OHLC();
		pHigh.name = "P.High";
		pLow = new OHLC();
		pLow.name = "P.Low";
		pClose = new OHLC();
		pClose.name = "P.Close";
		mySupport = new OHLC();
		mySupport.name = "MySupport";
		myResist = new OHLC();
		myResist.name = "MyResist";
		mySAR = new OHLC();
		mySAR.name = "SAR";

		ohlc = new XMLReader(Global.getToday(), "TradeData\\FHIdata.xml");
		ohlc.findOHLC();
	}

	public void run()
	{

		setOHLC();

		while (Global.isRunning())
		{

			if (GetData.getTimeInt() > 91420 && Global.getOpen() == 0)
			{
				setOpenPrice();
				Global.addLog("Open: " + Global.getOpen());
			}

			if (secCounter >= 10)
			{
				secCounter = 0;

				if (isFileModified())
				{
					intraDay.findElementOfToday();
					intraDay.findOHLC();

					rangeResist = intraDay.rangeResist;
					rangeSupport = intraDay.rangeSupport;
					SAR = Double.parseDouble(intraDay.getValueOfNode("SAR"));
					cutLoss = Double.parseDouble(intraDay.getValueOfNode("cutLoss"));
					stopEarn = Double.parseDouble(intraDay.getValueOfNode("stopEarn"));
					reverse = Double.parseDouble(intraDay.getValueOfNode("reverse"));
					buying = Boolean.parseBoolean(intraDay.getValueOfNode("buying"));
					selling = Boolean.parseBoolean(intraDay.getValueOfNode("selling"));

					mySAR.position = SAR;
					
					Global.addLog("--------------------");
					Global.addLog("RangeResist/Support: " + rangeResist + "/" + rangeSupport);				
					Global.addLog("SAR: " + SAR);
					Global.addLog("--------------------");
				}

			}

			secCounter++;
			sleep(1000);
		}
	}

	private boolean isFileModified()
	{

		if (fileModifiedTime == new File(intraDayXMLPath).lastModified())
			return false;
		else
		{
			fileModifiedTime = new File(intraDayXMLPath).lastModified();
			Global.addLog("XML file updated");
			return true;
		}

	}

	private void setOpenPrice()
	{

		double openPrice = 0;

		SPApi.setOpenPrice();

		openPrice = Global.getOpen();

		if (openPrice == 0)
		{
			Global.addLog("Open = 0");
			sleep(5000);

			if (GetData.getTimeInt() > 91500)
			{
				Global.addLog("Fail to set open b4 91500, try again later");
			}

			setOpenPrice();
		}

		ohlc.updateNode("open", String.valueOf(openPrice));

		// wait for open price to add them together
		open.position = Global.getOpen();
		pHigh.position = Global.getpHigh();
		pLow.position = Global.getpLow();
		pClose.position = Global.getpClose();

		mySupport.position = Global.getKkSupport();
		myResist.position = Global.getKkResist();

		// return openPrice;

	}

	public static void updateIntraDayXML(String node, String value)
	{
		intraDay.updateNode(node, value);
		Global.addLog("Updated Node: " + node + ", value: " + value);
	}

	private void sleep(int miniSecond)
	{
		try
		{
			Thread.sleep(miniSecond);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

	private void setOHLC()
	{

		// XMLReader ohlc = new XMLReader(Global.getToday());
		Global.setpHigh(ohlc.getpHigh());
		Global.setpLow(ohlc.getpLow());
		Global.setpOpen(ohlc.getpOpen());
		Global.setpClose(ohlc.getpClose());
		Global.setpFluc(ohlc.getpFluc());

		Global.setKkResist(ohlc.getKkResist());
		Global.setKkSupport(ohlc.getKkSupport());

		if (Global.getpHigh() != 0)
		{
			Global.addLog("-------------------------------------");
			Global.addLog("P.High: " + Global.getpHigh());
			Global.addLog("P.Low: " + Global.getpLow());
			Global.addLog("-------------------------------------");
		}

	}

}
