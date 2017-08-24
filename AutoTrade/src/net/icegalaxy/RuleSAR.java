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
			shutDownSAR();
			trendReversed = false;
			shutdown = false;
			
			sleep(60000);
		}
		
	
		// stair should not be reseted in this area or it wont function
		//if (XMLWatcher.stair != 0) XMLWatcher.updateIntraDayXML("stair", "0");
		

		SAR = XMLWatcher.SAR;
		cutLoss = XMLWatcher.cutLoss;
		stopEarn = XMLWatcher.stopEarn;
		reverse = XMLWatcher.reverse;
		buying = XMLWatcher.buying;
		selling = XMLWatcher.selling;
		
		if (GetData.getShortTB().getEma5().getEMA() > cutLoss
				&& buying
				&& Global.getCurrentPoint() < cutLoss + 15
				&& Global.getCurrentPoint() > cutLoss)
		{
			
			// below is not correct
//			if (Global.getCurrentPoint() < SAR + 5 && Global.getCurrentPoint() > SAR && !Global.isRapidDrop())
//			{
				
				while (Global.isRapidDrop()
						|| getTimeBase().getLatestCandle().getOpen() > getTimeBase().getLatestCandle().getClose())
				{
					
					if (GetData.getShortTB().getEma5().getEMA() < cutLoss)
					{
						Global.addLog("EMA5 out of range");
						shutDownSAR();
						return;
					}

					if (Global.getCurrentPoint() < cutLoss - 10)
					{
						Global.addLog("Current point out of range");
						shutDownSAR();
						return;
					}

					sleep(1000);
				}

				longContract();
//			}	
		}else if (GetData.getShortTB().getEma5().getEMA() < cutLoss
				&& selling
				&& Global.getCurrentPoint() > cutLoss - 15
				&& Global.getCurrentPoint() < cutLoss)
		{
			
			// below is not correct
//			if (Global.getCurrentPoint() > SAR - 5 && Global.getCurrentPoint() < SAR && !Global.isRapidRise())
//			{
				while (Global.isRapidRise()
						|| getTimeBase().getLatestCandle().getOpen() < getTimeBase().getLatestCandle().getClose())
				{
					
					if (GetData.getShortTB().getEma5().getEMA() > cutLoss)
					{
						Global.addLog("EMA5 out of range");
						shutDownSAR();
						return;
					}

					if (Global.getCurrentPoint() > cutLoss + 10)
					{
						Global.addLog("Current point out of range");
						shutDownSAR();
						return;
					}

					sleep(1000);
				}

				shortContract();
//			}
		}
	}

	private void shutDownSAR()
	{
		XMLWatcher.updateIntraDayXML("buying", "false");
		XMLWatcher.updateIntraDayXML("selling", "false");
		Global.addLog("Shut down RuleSAR");
		buying = false;
		selling = false;
	}


	double getCutLossPt()
	{
		
		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0){
			
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint > tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 10;
			}
			return Math.max(20, buyingPoint - cutLoss + 30);
		}
		else
		{
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
				
			if (buyingPoint < tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 10;
			}
			return Math.max(20, cutLoss - buyingPoint + 30);
		}
	}

	// use 1min instead of 5min
	double getStopEarnPt()
	{

	
			if (Global.getNoOfContracts() > 0)
			{
				if (shutdown == false && refLow < cutLoss - 15)
				{
					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 15;
				}else 
					return Math.max(10, stopEarn - buyingPoint - 10);
			}
			else
			{
				if (shutdown == false && refHigh > cutLoss + 15)
				{
					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 15;
				}
				return Math.max(10, buyingPoint - stopEarn - 10);
			}
		
	}
	
	@Override
	void stopEarn()
	{
		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() < buyingPoint + 5)
				closeContract(className + ": Break even, short @ " + Global.getCurrentBid());
			else if (GetData.getShortTB().getLatestCandle().getClose() < tempCutLoss)
				closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			

		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (Global.getCurrentPoint() > buyingPoint - 5)
				closeContract(className + ": Break even, long @ " + Global.getCurrentAsk());
			else if (GetData.getShortTB().getLatestCandle().getClose() > tempCutLoss)
				closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			
		}
	}
	
	@Override
	void updateStopEarn()
	{

		double stair = XMLWatcher.stair;
		
		if (Global.getNoOfContracts() > 0)
		{
			
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
			{
				
				if (GetData.getShortTB().getLatestCandle().getLow() < stopEarn)
					tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
				else
					tempCutLoss = stopEarn;
			}
			
			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6))
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
			{
				
				if (GetData.getShortTB().getLatestCandle().getHigh() > stopEarn)
					tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
				else
					tempCutLoss = stopEarn;
			}
			
			if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6))
				tempCutLoss = 0;
		}

	}

	

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}