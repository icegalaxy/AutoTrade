package net.icegalaxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

//Use the OPEN Line

public class XMLWatcher implements Runnable
{

	public static List<Stair> stairs = new CopyOnWriteArrayList<Stair>();
	static MyFile Stair = new MyFile("C:\\Users\\joecheung0\\Dropbox\\TradeData\\stair.csv");
	static MyFile csvLog;
	public static ArrayList<MyFile> files = new ArrayList<MyFile>();
	public static double stair = 0;
	private int secCounter;

	public XMLWatcher()
	{
		files.add(Stair);
	}

	public void run()
	{

		try
		{
			resetStairs();
		} catch (Exception e1)
		{
			e1.printStackTrace();
			sleep(30000);
		}


		RuleSkyStair ss = new RuleSkyStair(true);
		RuleSkyStairNano na = new RuleSkyStairNano(true);
		RuleSkyStair1Min st1 = new RuleSkyStair1Min(true);
		RuleSkyStair5Min st5 = new RuleSkyStair5Min(true);
		//RulePriceAction pa = new RulePriceAction(true);
		
		Thread ts = new Thread(ss);
		ts.start();
		Thread nano = new Thread(na);
		nano.start();
		Thread tSt1 = new Thread(st1);
		tSt1.start();
		Thread tSt5 = new Thread(st5);
		tSt5.start();
		//Thread sPa = new Thread(pa);
		//sPa.start();

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



				if (Stair.isFileModified())
				{
					try{
						readStairs();
					}catch (Exception z)
					{
						z.printStackTrace();
						sleep(30000);
					}
				}

			}

			secCounter++;
			sleep(1000);
		}
	}

	public static void resetStairs() throws InterruptedException
	{

		try{
			readStairs();
		}catch (Exception z)
		{
			z.printStackTrace();
			Thread.sleep(30000);
		}

		if (stairs.size() <= 2)
		{
			Global.addLog("No stairs!!");
			return;
		}

		for (int s = 0; s < stairs.size(); s++)
		{

				stairs.get(s).buying = true;
				stairs.get(s).selling = true;
				stairs.get(s).refHigh = 0;
				stairs.get(s).refLow = 99999;
				stairs.get(s).reActivateTime = 0;
				stairs.get(s).shutdown = false;
		}
		Global.updateCSV();
	}

	private static void readStairs()
	{
		Scanner sc = null;
		try
		{
			sc = new Scanner(new File(Stair.pathName));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		sc.useDelimiter("\r\n");

		ArrayList<String> lines = new ArrayList<String>();

		sc.next();

		while (sc.hasNext())
			lines.add(sc.next());

		sc.close();

		List<Stair> tempStairs = new CopyOnWriteArrayList<Stair>(); // avoid
																	// modifying
																	// the
																	// original
																	// list at
																	// this
																	// stage.

		for (int i = 0; i < lines.size(); i++)
		{
			Scanner sc2 = new Scanner(lines.get(i));
			sc2.useDelimiter(",");

			Stair st = new Stair();

			st.lineType = sc2.next();
			st.value = sc2.nextDouble();
			st.cutLoss = sc2.nextDouble();
			st.buying = sc2.nextBoolean();
			st.refLow = sc2.nextDouble();
			st.selling = sc2.nextBoolean();
			st.refHigh = sc2.nextDouble();
			st.tolerance = sc2.nextDouble();
			st.reActivateTime = sc2.nextInt();
			st.shutdown = sc2.nextBoolean();

			sc2.close();

			// Global.addLog("Stair: " + st.lineType + ", value: " + st.value);

			tempStairs.add(st);

		}

		stairs = tempStairs;

	}



	private void setOpenPrice()
	{

		double openPrice = 0;

		SPApi.setOpenPrice();

		openPrice = Global.getOpen();

		if (openPrice == 0)
		{
			Global.addLog("Open = 0");
			
			SPApi.unSubscribePrice();
			
			sleep(10000);
			
			SPApi.subscribePrice();
			
			sleep(10000);

			if (TimePeriodDecider.nightOpened || TimePeriodDecider.dayClosed)
			{
				return;
			}
			if (GetData.getTimeInt() > 91500)
			{
				Global.addLog("Fail to set open b4 91500, try again later");
			}


		}


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



}
