package net.icegalaxy;

import java.time.Instant;

public class TimePeriodDecider implements Runnable
{

	public final int getReadyTime = 90000;
	public final int dayOpen = 91459;
	public final int noonClose = 120001;
	public final int noonOpen = 125959;
	public final int dayClose = 163001;
	public final int nightOpen = 171500;
//	public final int nightClose = 235959;
	public final int nightClose = 1005959;

	public final int morningOrderStart = 91500;
	public final int morningOrderStop = 103000;
	public final int afternoonOrderStart = 150000;
	public final int afternoonOrderStop = 160000;
	public final int nightOrderStart = 171800;
//	public final int nightOrderStop = 231500;
	public final int nightOrderStop = 1003000;

	public final int forceSell = 162500;
//	public final int forceSell2 = 235500;
	public final int forceSell2 = 1005500;
	private boolean noonClosed;
	private boolean noonOpened;
	public static boolean dayClosed;
	public static boolean nightOpened;
	
	static Instant instant;
	

	@Override
	public void run()
	{

		System.out.println("Program Started");
		

		while (getTime() < nightClose)
//		while (getTime() < dayClose)
		{

			// if (Global.shutDown){
			// Sikuli.liquidateOnly();
			// break;
			// }

			int time = getTime();

			if (time >= dayOpen && time <= noonClose)
				Global.setTradeTime(true);
			else if (time > noonClose && time < noonOpen)
			{
				if (!noonClosed)
				{
					Global.addLog("Noon Close");
					Global.setTradeTime(false);
					try
					{
						XMLWatcher.resetStairs();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					noonClosed = true;
				}
			
			} else if (time >= noonOpen && time <= dayClose)
			{
				if (!noonOpened)
				{
					Global.addLog("Noon Opened");
					Global.setTradeTime(true); // did not set here before
					Global.setNoonOpened(true);
					noonOpened = true;
				}
//				Global.setTradeTime(true);
//				if (time >= forceSell)
//					Global.setForceSellTime(true);

			} else if (time > dayClose && time < nightOpen)
			{
				if (!dayClosed)
				{
					Global.addLog("Day Close");
					Global.setTradeTime(false);
					try
					{
						XMLWatcher.resetStairs();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dayClosed = true;
				}
				
				if (Global.isForceSellTime())
					Global.setForceSellTime(false);
			} else if (time >= nightOpen)
			{
				if (!nightOpened)
				{
					Global.addLog("Night Opened");
					Global.setTradeTime(true);
					nightOpened = true;
				}			
			}

			if (time >= forceSell2)
				Global.setForceSellTime(true);

			if (getTime() >= morningOrderStart && getTime() <= morningOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > morningOrderStop && getTime() < afternoonOrderStart)
				Global.setOrderTime(false);
			else if (getTime() >= afternoonOrderStart && getTime() < afternoonOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > afternoonOrderStop && getTime() < nightOrderStart)
				Global.setOrderTime(false);
			else if (getTime() >= nightOrderStart && getTime() < nightOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > nightOrderStop)
				Global.setOrderTime(false);

			if (!Global.isRunning())
			{
				Global.setTradeTime(false);
				Global.setOrderTime(false);
				Global.setForceSellTime(true);
				break;
			}

			sleep(1000);

		}

		Global.addLog("Day Close");
		Global.setTradeTime(false);
		Global.setQuotePowerTime(false);
		Global.setRunning(false);
		
		SPApi.unInit();

		System.out.println("Program Ended");
		// Sikuli.closeWindow();
		// Global.clearLog();
		// Sikuli.closeEclipse();
	}

	public static int getTime()
	{

		return GetData.getTimeInt();
	}

	private void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public static long getEpochSec(){
		
		instant = Instant.now();	
		return instant.getEpochSecond();
	    
	}
}
