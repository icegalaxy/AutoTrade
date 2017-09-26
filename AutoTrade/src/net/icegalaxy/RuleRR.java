package net.icegalaxy;

//Use the OPEN Line

public class RuleRR extends Rules
{

	OHLC currentOHLC;

	public RuleRR(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 130100, 160000, 230000, 230000); // need to observe the first 3min
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
						

			if (GetData.getShortTB().getEma5().getEMA() > currentOHLC.cutLoss
					&& currentOHLC.stopEarn > currentOHLC.cutLoss
					&& Global.getCurrentPoint() < currentOHLC.cutLoss + 10
					&& Global.getCurrentPoint() > currentOHLC.cutLoss)
			{

				Global.addLog("Reached " + currentOHLC.name);
				
					
				
//				waitForANewCandle();
				
				updateHighLow();	
				
				while (Global.isRapidDrop()
						|| Global.getCurrentPoint() <= refLow + 5)
				{
					
					updateHighLow();

					if (isDownTrend())
					{
						Global.addLog("Down Trend");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;	
					}
					
					
					if (GetData.getShortTB().getEma5().getEMA() < currentOHLC.cutLoss)
					{
						Global.addLog("EMA5 out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					if (Global.getCurrentPoint() < currentOHLC.cutLoss - 10)
					{
						Global.addLog("Current point out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					sleep(1000);
				}
				
				if  (Global.getCurrentPoint() > currentOHLC.cutLoss + 10)
					Global.addLog("Rise to fast, waiting for a pull back");
				
				
				while (Global.getCurrentPoint() > currentOHLC.cutLoss + 10 || Global.isRapidDrop())
				{
					
					updateHighLow();
					
					if  (Global.getCurrentPoint() > currentOHLC.cutLoss + 50)
					{
						Global.addLog("Too far away");
						return;
					}		
	
					
					sleep(1000);		
				}
				
				trailingDown(2);
				
				if (Global.getCurrentPoint() < currentOHLC.cutLoss - 10)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				longContract();
				Global.addLog("Ref Low: " + refLow);
//				currentOHLC.cutLoss = refLow;
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			} else if (GetData.getShortTB().getEma5().getEMA() < currentOHLC.cutLoss
					&& currentOHLC.stopEarn < currentOHLC.cutLoss
					&& Global.getCurrentPoint() > currentOHLC.cutLoss - 10
					&& Global.getCurrentPoint() < currentOHLC.cutLoss)
			{
				
				Global.addLog("Reached " + currentOHLC.name);
				
//				waitForANewCandle();
				
				updateHighLow();
				
				while (Global.isRapidRise()
						|| Global.getCurrentPoint() >= refHigh - 5)
				{
					
					updateHighLow();
					
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

					if (Global.getCurrentPoint() > currentOHLC.cutLoss + 10)
					{
						Global.addLog("Current point out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					sleep(1000);
				}
				
				if (Global.getCurrentPoint() < currentOHLC.cutLoss - 10)
					Global.addLog("Drop to fast, waiting for a pull back");
				
				while (Global.getCurrentPoint() < currentOHLC.cutLoss - 10 || Global.isRapidRise())
				{
					
					updateHighLow();					
				
					
					if (Global.getCurrentPoint() < currentOHLC.cutLoss - 50)
					{
						Global.addLog("Too far away");
						return;
					}
					
				
					
					sleep(1000);
				}
				
				trailingUp(2);
				
				if (Global.getCurrentPoint() > currentOHLC.cutLoss + 10)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				shortContract();
				Global.addLog("Ref High: " + refHigh);
//				currentOHLC.cutLoss = refHigh;
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			}

		}
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		double stair = XMLWatcher.stair;
		
		long max = 0;
		if (getExpectedProfit() > 10)
			max = 10;
		else
			max = getExpectedProfit();
		
		if (Global.getNoOfContracts() > 0){
			
			//Expected profit
			if (getHoldingTime() > 300 
					&& getProfit() > tempCutLoss - buyingPoint + 10
				//	&& getProfit() <= 16
					&& getProfit() > max + 10
					&& tempCutLoss < buyingPoint + max)
			{
					tempCutLoss = buyingPoint + max;
					Global.addLog("Expected profit updated: " + (buyingPoint + max));
			}
				

			// first profit then loss
//			if (tempCutLoss < currentOHLC.cutLoss - 10 && refHigh > currentOHLC.cutLoss + 30)
//				tempCutLoss = currentOHLC.cutLoss - 10; 
			
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
			return Math.max(10, buyingPoint - currentOHLC.cutLoss + 5);
		}
		else
		{
			// first profit then loss
//			if (tempCutLoss > currentOHLC.cutLoss + 10 && refLow < currentOHLC.cutLoss - 30)
//				tempCutLoss = currentOHLC.cutLoss + 10; 
			
			//Expected profit
			if (getHoldingTime() > 300 
					&& getProfit() > buyingPoint - tempCutLoss + 10 
			//		&& getProfit() <=  16
					&& getProfit() > max + 10
					&& tempCutLoss > buyingPoint - max)
				{
					tempCutLoss = buyingPoint - max;
					Global.addLog("Expected profit updated: " + (buyingPoint - max));
				}
			
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
			return Math.max(10, currentOHLC.cutLoss - buyingPoint + 5);
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

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
			{
				
				if (GetData.getShortTB().getLatestCandle().getLow() < currentOHLC.stopEarn)
					tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
				else
					tempCutLoss = currentOHLC.stopEarn;
				
			}
//			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6))
//				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
			{
				
				if (GetData.getShortTB().getLatestCandle().getHigh() > currentOHLC.stopEarn)
					tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
				else
					tempCutLoss = currentOHLC.stopEarn;
			}
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

			if (Global.getNoOfContracts() > 0)
			{	
				
				if (refLow < currentOHLC.cutLoss - 20){
					shutdown = true;
					return Math.min(20, refHigh - buyingPoint - 5);
				}
				
				if (refLow < currentOHLC.cutLoss - 10)
				{
//					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 30;
				}
				return Math.max(10, currentOHLC.stopEarn - buyingPoint - 10);
			}
			else
			{
				
				if (refHigh > currentOHLC.cutLoss + 20){
					shutdown = true;
					return Math.min(20, buyingPoint - refLow - 5);
				}
				
				if (refHigh > currentOHLC.cutLoss + 10)
				{
//					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 30;
				}
				return Math.max(10, buyingPoint - currentOHLC.stopEarn - 10);
			}
		
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