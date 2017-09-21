package net.icegalaxy;

import java.io.File;


//Use the OPEN Line

public class XMLWatcher implements Runnable
{

	public static IntraDayReader intraDay;
	XMLReader ohlc;
	static String intraDayXMLPath = "C:\\Users\\joech\\Dropbox\\TradeData\\Intraday.xml";
	static String OHLCPath = "C:\\Users\\joech\\Dropbox\\TradeData\\FHIdata.xml";

	public static double rangeResist = 0;
	public static double rangeSupport = 0;
	
	public static boolean ibtRise;
	public static boolean ibtDrop;

	public static OHLC open;
	public static OHLC pHigh;
	public static OHLC pLow;
	public static OHLC pClose;
	public static OHLC mySupport;
	public static OHLC myResist;
	public static OHLC mySAR;

	public static OHLC[] ohlcs;

	public static double SAR = 0;
	public static double cutLoss = 0;
	public static double stopEarn = 0;
	public static double reverse = 0;
	public static boolean buying;
	public static boolean selling;
	public static double stair = 0;

	private long intraDayModifiedTime;
	private long FHIDataModifiedTime;

	private int secCounter;

	public XMLWatcher()
	{

		intraDayModifiedTime = new File(intraDayXMLPath).lastModified();
		FHIDataModifiedTime = new File(OHLCPath).lastModified();
		
		intraDay = new IntraDayReader(Global.getToday(), intraDayXMLPath);

		open = new OHLC();
		open.name = "Open";
		pHigh = new OHLC();
		pHigh.name = "pHigh";
		pLow = new OHLC();
		pLow.name = "pLow";
		pClose = new OHLC();
		pClose.name = "pClose";
		mySupport = new OHLC();
		mySupport.name = "mySupport";
		myResist = new OHLC();
		myResist.name = "myResist";
		mySAR = new OHLC();
		mySAR.name = "SAR";

		//ohlc = new XMLReader(Global.getToday(), OHLCPath);
		//using today
//		ohlc = new XMLReader("Today", OHLCPath);
//		ohlcs = new OHLC[5];
//
//		ohlcs[0] = pHigh;
//		ohlcs[1] = pLow;
//		ohlcs[2] = pClose;
//		ohlcs[3] = mySupport;
//		ohlcs[4] = myResist;

//		ohlc.findOHLC();
	}

	public void run()
	{

		//reset XMLWatcher
		XMLWatcher.updateIntraDayXML("stair", "0");
		XMLWatcher.updateIntraDayXML("cutLoss", "0");
		XMLWatcher.updateIntraDayXML("stopEarn", "0");
		XMLWatcher.updateIntraDayXML("rangeResist", "0");
		XMLWatcher.updateIntraDayXML("rangeSupport", "0");
		XMLWatcher.updateIntraDayXML("SAR", "0");
		XMLWatcher.updateIntraDayXML("buying", "false");
		XMLWatcher.updateIntraDayXML("selling", "false");
		
		
		setOHLC();
		
		RuleSAR sar = new RuleSAR(true);
		RuleRR rr = new RuleRR(true);
		RuleIBT ibt = new RuleIBT(true);
		RuleRange range = new RuleRange(true);
		Thread s = new Thread(sar);
		s.start();
		Thread r = new Thread(rr);
		r.start();
		Thread i = new Thread(ibt);
		i.start();
		Thread ran = new Thread(range);
		ran.start();

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

				if (isIntraDayModified(intraDayXMLPath))
				{
					intraDay.findElementOfToday();
					intraDay.findOHLC();

					stair = Double.parseDouble(intraDay.getValueOfNode("stair"));
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
					Global.addLog("CutLoss/StopEarn: " + cutLoss + "/" + stopEarn);
					Global.addLog("--------------------");
				}
				
				if (isFHIModified(OHLCPath))
					setOHLC();

			}

			secCounter++;
			sleep(1000);
		}
	}

	private boolean isFHIModified(String filePath)
	{

		if (FHIDataModifiedTime == new File(filePath).lastModified())
			return false;
		else
		{
			FHIDataModifiedTime = new File(filePath).lastModified();
			Global.addLog("XML file updated");
			return true;
		}

	}
	
	private boolean isIntraDayModified(String filePath)
	{

		if (intraDayModifiedTime == new File(filePath).lastModified())
			return false;
		else
		{
			intraDayModifiedTime = new File(filePath).lastModified();
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
//		pHigh.position = Global.getpHigh();
//		pLow.position = Global.getpLow();
//		pClose.position = Global.getpClose();
//
//		mySupport.position = Global.getKkSupport();
//		myResist.position = Global.getKkResist();

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
		
		ohlc = new XMLReader("Today", OHLCPath);
		
		try{
			ibtRise = Boolean.parseBoolean(ohlc.getValueOfNode("ibtRise"));
			ibtDrop = Boolean.parseBoolean(ohlc.getValueOfNode("ibtDrop"));
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		ohlcs = new OHLC[5];

		ohlcs[0] = pHigh;
		ohlcs[1] = pLow;
		ohlcs[2] = pClose;
		ohlcs[3] = mySupport;
		ohlcs[4] = myResist;
		

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				switch (i)
				{
				case 0:
					ohlcs[j].position = Double.parseDouble(ohlc.getValueOfChildNode(ohlcs[j].name, i));
					break;
				case 1:
					ohlcs[j].stopEarn = Double.parseDouble(ohlc.getValueOfChildNode(ohlcs[j].name, i));
					break;
				case 2:
					ohlcs[j].cutLoss = Double.parseDouble(ohlc.getValueOfChildNode(ohlcs[j].name, i));
					break;
				}

			}

		}

		// XMLReader ohlc = new XMLReader(Global.getToday());
		Global.setpHigh(ohlcs[0].position);
		Global.setpLow(ohlcs[1].position);
		Global.setpClose(ohlcs[2].position);
//		Global.setpClose(ohlc.getpClose());
//		Global.setpFluc(ohlc.getpFluc());
//
//		Global.setKkResist(ohlc.getKkResist());
//		Global.setKkSupport(ohlc.getKkSupport());

//		if (pHigh.position != 0)
//		{
			Global.addLog("-------------------------------------");
			Global.addLog("P.High: " + Global.getpHigh());
			Global.addLog("P.Low: " + Global.getpLow());
			Global.addLog("P.Close: " + Global.getpClose());
			Global.addLog("IBT Rise: " + ibtRise);
			Global.addLog("IBT Drop: " + ibtDrop);
			Global.addLog("-------------------------------------");
//		}

	}

}
