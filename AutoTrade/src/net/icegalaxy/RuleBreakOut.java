package net.icegalaxy;

//Use the OPEN Line

public class RuleBreakOut extends Rules
{

	OHLC currentOHLC;

	public RuleBreakOut(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 130100, 160000, 171800, 230000); // need to observe the first 3min
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 
				//|| Global.balance < -30
				)
			return;
		
		
		// if cutLoss, shutdown the ohlc
		if (shutdown)
		{
			for (int i=0; i<XMLWatcher.ohlcs.length; i++)	
			{
				if (currentOHLC.name.equals(XMLWatcher.ohlcs[i].name))
					XMLWatcher.ohlcs[i].shutdown = true;				
			}
			shutdown = false;
		}
			
		// stair should not be reseted in this area or it wont function
		//if (XMLWatcher.stair != 0) XMLWatcher.updateIntraDayXML("stair", "0");
		

//		for (OHLC item : XMLWatcher.ohlcs)
			
		for (int i=0; i<XMLWatcher.ohlcs.length; i++)	
		{
			currentOHLC = XMLWatcher.ohlcs[i];
//			setOrderTime(item.getOrderTime());

			if (Global.getNoOfContracts() != 0)
				return;

			if (currentOHLC.cutLoss == 0)
				continue;

			if (currentOHLC.shutdown)
				continue;
						

			if (GetData.getShortTB().getEma5().getEMA() < currentOHLC.cutLoss
					&& currentOHLC.stopEarn > currentOHLC.cutLoss
					&& Global.getCurrentPoint() > currentOHLC.cutLoss - 10
					&& Global.getCurrentPoint() < currentOHLC.cutLoss)
			{

				Global.addLog("Reached " + currentOHLC.name);	
				
				waitForANewCandle();
				
//				updateHighLow();	
				
				while (Global.getCurrentPoint() < currentOHLC.cutLoss + 10)
				{
					
//					updateHighLow();

					if (isDownTrend())
					{
						Global.addLog("Down Trend");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;	
					}
					
					
					if (GetData.getShortTB().getEma5().getEMA() > currentOHLC.cutLoss)
					{
						Global.addLog("EMA5 out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}
					
					if (GetData.getShortTB().getLatestCandle().getClose() <  GetData.getShortTB().getLatestCandle().getOpen())
					{
						Global.addLog("Candle drop");
						return;
					}


					sleep(1000);
				}
				
					Global.addLog("Waiting for a pull back");
				
				
				while (Global.getCurrentPoint() >= currentOHLC.cutLoss + 10)
				{
					
//					updateHighLow();
					
					if  (Global.getCurrentPoint() > currentOHLC.cutLoss + 50)
					{
						Global.addLog("Too far away");
						return;
					}		
	
					
					sleep(1000);		
				}
				
				trailingDown(2);
				
				if (Global.getCurrentPoint() < currentOHLC.cutLoss)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				longContract();
//				Global.addLog("Ref Low: " + refLow);
//				currentOHLC.cutLoss = refLow;
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			} else if (GetData.getShortTB().getEma5().getEMA() > currentOHLC.cutLoss
					&& currentOHLC.stopEarn < currentOHLC.cutLoss
					&& Global.getCurrentPoint() < currentOHLC.cutLoss + 10
					&& Global.getCurrentPoint() > currentOHLC.cutLoss)
			{
				
				Global.addLog("Reached " + currentOHLC.name);
				
				waitForANewCandle();
				
//				updateHighLow();
				
				while (Global.getCurrentPoint() > currentOHLC.cutLoss - 10)
				{
					
//					updateHighLow();
					
					if (isUpTrend())
					{
						Global.addLog("Up Trend");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;	
					}
					
					if (GetData.getShortTB().getEma5().getEMA() > currentOHLC.cutLoss)
					{
						Global.addLog("EMA5 out of range");
						 XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					if (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getLatestCandle().getOpen())
					{
						Global.addLog("Candle rise");
						return;
					}

					sleep(1000);
				}
				
				
					Global.addLog("Waiting for a pull back");
				
				while (Global.getCurrentPoint() <= currentOHLC.cutLoss - 10)
				{
					
//					updateHighLow();					
				
					
					if (Global.getCurrentPoint() < currentOHLC.cutLoss - 50)
					{
						Global.addLog("Too far away");
						return;
					}
					
				
					
					sleep(1000);
				}
				
				trailingUp(2);
				
				if (Global.getCurrentPoint() > currentOHLC.cutLoss)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				shortContract();
//				Global.addLog("Ref High: " + refHigh);
//				currentOHLC.cutLoss = refHigh;
//				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			}

		}
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		double stair = XMLWatcher.stair;
		
		updateExpectedProfit(10);
		
		if (Global.getNoOfContracts() > 0){
			
			
			// set 10 pts below cutLoss
			if (tempCutLoss < currentOHLC.cutLoss - 10)
				tempCutLoss = currentOHLC.cutLoss - 10; 
			
			if (stair != 0 && tempCutLoss < stair && GetData.getShortTB().getLatestCandle().getClose() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

//			return Math.max(20, buyingPoint - currentOHLC.cutLoss + 30);
			
			//just in case, should be stopped by tempCutLoss first
			return Math.max(10, buyingPoint - currentOHLC.cutLoss + 10);
		}
		else
		{
			
			
			if (tempCutLoss > currentOHLC.cutLoss + 10)
				tempCutLoss = currentOHLC.cutLoss + 10; 
			
			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
			}
//			return Math.max(20, currentOHLC.cutLoss - buyingPoint + 30);
			
			//just in case, should be stopped by tempCutLoss first
			return Math.max(10, currentOHLC.cutLoss - buyingPoint + 10);
		}
	}
	
	@Override
	void updateStopEarn()
	{
		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0)
		{
			
			// update stair
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss
					&& tempCutLoss < currentOHLC.stopEarn)		
					tempCutLoss = Math.min(currentOHLC.stopEarn, GetData.getShortTB().getLatestCandle().getLow());
				
//			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6))
//				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > currentOHLC.stopEarn)
				tempCutLoss = Math.max(currentOHLC.stopEarn, GetData.getShortTB().getLatestCandle().getHigh());

//			if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6))
//				tempCutLoss = 0;
		}

	}

	@Override
	void stopEarn()
	{
		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() < buyingPoint + 5)
				closeContract(className + ": Break even, short @ " + Global.getCurrentBid());
			else if (Global.getCurrentPoint() < tempCutLoss)
				closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			

		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (Global.getCurrentPoint() > buyingPoint - 5)
				closeContract(className + ": Break even, long @ " + Global.getCurrentAsk());
			else if (Global.getCurrentPoint() > tempCutLoss)
				closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			
		}
	}
	
	// @Override
	// protected void cutLoss()
	// {
	//
	// if (Global.getNoOfContracts() > 0)
	// {
	//
	// //breakEven
	// if (getProfit() > 20 && tempCutLoss < buyingPoint + 5)
	// tempCutLoss = buyingPoint + 5;
	//
	// if (Global.getCurrentPoint() < tempCutLoss)
	// {
	// closeContract(className + ": CutLoss, short @ " +
	// Global.getCurrentBid());
	// shutdown = true;
	// }
	// } else if (Global.getNoOfContracts() < 0)
	// {
	//
	// //breakEven
	// if (getProfit() > 20 && tempCutLoss > buyingPoint - 5)
	// tempCutLoss = buyingPoint - 5;
	//
	// if (Global.getCurrentPoint() > tempCutLoss)
	// {
	// closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
	// shutdown = true;
	// }
	//
	// }
	//
	// }

	// @Override
	// boolean trendReversed()
	// {
	// if (reverse == 0)
	// return false;
	// else if (Global.getNoOfContracts() > 0)
	// return Global.getCurrentPoint() < reverse;
	// else
	// return Global.getCurrentPoint() > reverse;
	// }

	double getStopEarnPt()
	{
//
//			if (Global.getNoOfContracts() > 0)
//			{	
//				
//				return Math.max(10, currentOHLC.stopEarn - buyingPoint - 10);
//			}
//			else
//			{
//				
//				
//				return Math.max(10, buyingPoint - currentOHLC.stopEarn - 10);
//			}
			
			return Math.max(10, Math.abs(buyingPoint - currentOHLC.stopEarn) - 10);
		
	}

	// @Override
	// public void trendReversedAction()
	// {
	//
	// trendReversed = true;
	// }

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}