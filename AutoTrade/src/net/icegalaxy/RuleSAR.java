package net.icegalaxy;

//Use the OPEN Line

public class RuleSAR extends Rules
{

	private double SAR = 0;
	private double cutLoss = 0;
	private double stopEarn = 0;
	private double reverse = 0;
	private boolean buying;
	private boolean selling;
	private boolean trendReversed;

	public RuleSAR(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 113000, 130100, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;
		
		if (shutdown || trendReversed)
		{
			XMLWatcher.updateIntraDayXML("buying", "false");
			XMLWatcher.updateIntraDayXML("selling", "false");
			Global.addLog("Shut down RuleSAR");
			buying = false;
			selling = false;
			trendReversed = false;
			shutdown = false;
			
			sleep(60000);
		}
		

		SAR = XMLWatcher.SAR;
		cutLoss = XMLWatcher.cutLoss;
		stopEarn = XMLWatcher.stopEarn;
		reverse = XMLWatcher.reverse;
		buying = XMLWatcher.buying;
		selling = XMLWatcher.selling;
		
		if (buying)
		{
			if (Global.getCurrentPoint() < SAR + 5 && Global.getCurrentPoint() > SAR && !Global.isRapidDrop())
			{
				longContract();
			}	
		}else if (selling)
		{
			if (Global.getCurrentPoint() > SAR - 5 && Global.getCurrentPoint() < SAR && !Global.isRapidRise())
			{
				shortContract();
			}
		}
	}


	

	// use 1min instead of 5min
	double getCutLossPt()
	{
		
		cutLoss = XMLWatcher.cutLoss;
		
		if (cutLoss == 0)
			return 15;
		else if (Global.getNoOfContracts() > 0)
			return buyingPoint - cutLoss + 3;
		else
			return cutLoss - buyingPoint + 3;
	}

	@Override
	protected void cutLoss()
	{
		
		if (Global.getNoOfContracts() > 0)
		{
			
			if (getProfit() > 20 && tempCutLoss < buyingPoint + 5)
				tempCutLoss = buyingPoint + 5;
			
			if (Global.getCurrentPoint() < tempCutLoss)
			{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (getProfit() > 20 && tempCutLoss > buyingPoint - 5)
				tempCutLoss = buyingPoint - 5;
			
			if (Global.getCurrentPoint() > tempCutLoss)
			{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
			}

		}

	}

	@Override
	boolean trendReversed()
	{
		if (reverse == 0)
			return false;
		else if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() < reverse;
		else
			return Global.getCurrentPoint() > reverse;
	}

	double getStopEarnPt()
	{
		
		stopEarn = XMLWatcher.stopEarn;
		
		
		
		if (trendReversed)
			return 10;
		else if(stopEarn == 0)
			return 30;
		else if (Global.getNoOfContracts() > 0)
			return stopEarn - buyingPoint - 5;
		else
			return buyingPoint - stopEarn -5;
	}

	@Override
	public void trendReversedAction()
	{

		trendReversed = true;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}