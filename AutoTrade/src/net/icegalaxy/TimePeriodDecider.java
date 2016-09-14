package net.icegalaxy;



public class TimePeriodDecider implements Runnable {
	
	public final int getReadyTime = 90000;
	public final int dayOpen = 91459; 
	public final int noonClose = 120001;
	public final int noonOpen = 125959;
	public final int dayClose = 163001;
	public final int nightOpen = 171500;
	public final int nightClose = 234500;
	

	public final int morningOrderStart = 100000;
	public final int morningOrderStop = 114500;
	public final int afternoonOrderStart = 134500;
	public final int afternoonOrderStop = 154500;
	public final int nightOrderStart = 173000;
	public final int nightOrderStop = 231500; 

	public final int forceSell = 162500;
	public final int forceSell2 = 234000;
	private boolean noonClosed;
	private boolean noonOpened;
	private boolean dayClosed;
	private boolean nightOpened;
	
	@Override
	public void run() {

		System.out.println("Program Started");
		

		
		while (getTime() < nightClose) {
			
//			if (Global.shutDown){
//				Sikuli.liquidateOnly();
//				break;
//			}
				

			int time = getTime();

			if (time >= dayOpen && time <= noonClose)
				Global.setTradeTime(true);
			else if (time > noonClose && time < noonOpen){
				if (!noonClosed){
					Global.addLog("Noon Close");
					noonClosed = true;
				}
				Global.setTradeTime(false);
			}
			else if (time >= noonOpen && time <= dayClose){
				if (!noonOpened){
					Global.addLog("Noon Opened");
					Global.setNoonOpened(true);
					noonOpened = true;
				}
				Global.setTradeTime(true);
				if (time >= forceSell)
					Global.setForceSellTime(true);
				
			}else if (time > dayClose && time < nightOpen){
				if (!dayClosed){
					Global.addLog("Day Close");
					dayClosed = true;
				}
				Global.setTradeTime(false);
				if (Global.isForceSellTime())
					Global.setForceSellTime(false);
			}else if (time >= nightOpen){
				if (!nightOpened){
					Global.addLog("Night Opened");
					nightOpened = true;
				}
				Global.setTradeTime(true);
			}
			
			if (time >= forceSell2)
				Global.setForceSellTime(true);
			
			//these should be no use, need to check
			if (time >= morningOrderStart && time <= morningOrderStop)
				Global.setOrderTime(true);
			else if (time > morningOrderStop && time < afternoonOrderStart)
				Global.setOrderTime(false);
			else if (time >= afternoonOrderStart && time < afternoonOrderStop)
				Global.setOrderTime(true);
			else if (time > afternoonOrderStop)
				Global.setOrderTime(false);


			if (!Global.isRunning()) {
				Global.setTradeTime(false);
				Global.setOrderTime(false);
			}

			sleep(1000); 

		}

		Global.setTradeTime(false); 
		Global.setQuotePowerTime(false);
		Global.setRunning(false);



		System.out.println("Program Ended");
//		Sikuli.closeWindow();
//		Global.clearLog();
//		Sikuli.closeEclipse();
	}



	public static int getTime() {

		return GetData.getTimeInt();
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
