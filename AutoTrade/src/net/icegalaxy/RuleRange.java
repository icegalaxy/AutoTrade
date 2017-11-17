package net.icegalaxy;


public class RuleRange extends Rules
{


	private double rangeResist = 0;
	private double rangeSupport = 0;

	public RuleRange(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(103000, 114500, 130100, 150000, 171800, 231500);
	}

	public void openContract()
	{

		
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;
		
		if (shutdown)
		{
			XMLWatcher.updateIntraDayXML("rangeResist", "0");
			XMLWatcher.updateIntraDayXML("rangeSupport", "0");
			Global.addLog("Shut down ruleRange");
			rangeResist = 0;
			rangeSupport = 0;
			shutdown = false;
			
			sleep(60000);
		}
		
		rangeResist = XMLWatcher.rangeResist;
		rangeSupport = XMLWatcher.rangeSupport;

		
	

		if (rangeSupport != 0)
		{
			if (Global.getCurrentPoint() < rangeSupport + 5 && Global.getCurrentPoint() > rangeSupport)
			{	
				Global.addLog("Reached rangeSupport");
			
			waitForANewCandle();
			
			while (Global.isRapidDrop()
					|| getTimeBase().getLatestCandle().getOpen() > getTimeBase().getLatestCandle().getClose() - 2) // need five pt to confirm
			{
	

				if (Global.getCurrentPoint() < rangeSupport - 5)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
			}
			
			if  (Global.getCurrentPoint() > rangeSupport + 5)
				Global.addLog("Rise to fast, waiting for a pull back");
			
			
			while (Global.getCurrentPoint() > rangeSupport + 5 || Global.isRapidDrop())
			{
				if  (Global.getCurrentPoint() > rangeSupport + 20)
				{
					Global.addLog("Too far away");
					return;
				}		
				
				if (Global.getCurrentPoint() < rangeSupport - 5)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}
				
				sleep(1000);		
			}
							
				longContract();
			}
		
		}
		
		if (rangeResist != 0)
		{
			if (Global.getCurrentPoint() > rangeResist - 5 && Global.getCurrentPoint() < rangeResist)
			{	
				Global.addLog("Reached rangeResist");

			waitForANewCandle();
			
			while (Global.isRapidRise()
					|| getTimeBase().getLatestCandle().getOpen() < getTimeBase().getLatestCandle().getClose() + 2)
			{
				

				if (Global.getCurrentPoint() > rangeResist + 5)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
			}
			
			if (Global.getCurrentPoint() < rangeResist - 5)
				Global.addLog("Rise to fast, waiting for a pull back");
			
			while (Global.getCurrentPoint() < rangeResist - 5 || Global.isRapidRise())
			{
				if (Global.getCurrentPoint() < rangeResist - 20)
				{
					Global.addLog("Too far away");
					return;
				}
				
				if (Global.getCurrentPoint() > rangeResist + 5)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}
				
				sleep(1000);
			}
				
				shortContract();
		
			}
		}

	}

	

	// use 1min instead of 5min
	void updateStopEarn()
	{
		if (Global.getNoOfContracts() > 0)
		{

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss 
					&& tempCutLoss < XMLWatcher.SAR)
				tempCutLoss = GetData.getLongTB().getLatestCandle().getLow();
			
			if (XMLWatcher.rangeResist !=0 && GetData.getShortTB().getLatestCandle().getLow() > XMLWatcher.rangeResist - 10)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
				
		} else if (Global.getNoOfContracts() < 0)
		{

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > XMLWatcher.SAR)
				tempCutLoss = GetData.getLongTB().getLatestCandle().getHigh();
			
			if (XMLWatcher.rangeSupport !=0 && GetData.getShortTB().getLatestCandle().getHigh() < XMLWatcher.rangeSupport + 10)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
		}
	}

	double getCutLossPt()
	{
		
		updateExpectedProfit(5);
				
		return 10;
		
//		return Math.abs(buyingPoint - Global.getOpen()) + 5;
	}

//	@Override
//	protected void cutLoss()
//	{
//
//		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
//		{
//			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
//			shutdown = true;
//		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
//		{
//			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
//			shutdown = true;
//
//		}
//
//	}


	double getStopEarnPt()
	{
		
		if (XMLWatcher.SAR == 0)
			return 20;
		else
			return Math.abs(XMLWatcher.SAR - buyingPoint);

	}
	

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}