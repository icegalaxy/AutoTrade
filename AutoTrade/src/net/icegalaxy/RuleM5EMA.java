package net.icegalaxy;

//Use the OPEN Line

public class RuleM5EMA extends Rules
{

	private double cutLoss = 0;
	private double stopEarn = 0;
	private boolean buying;
	private boolean selling;
	private boolean trendReversed;

	public RuleM5EMA(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(100000, 113000, 130100, 160000, 171800, 230000); // need to observe the first 3min
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
		
		
		if (XMLWatcher.M5EMA50)
			cutLoss = GetData.getLongTB().getEma50().getEMA();
		else if (XMLWatcher.M5EMA250)
			cutLoss = GetData.getLongTB().getEma250().getEMA();
		
		stopEarn = XMLWatcher.EMAstopEarn;
		buying = XMLWatcher.EMAbuying;
		selling = XMLWatcher.EMAselling;
		
		if (GetData.getShortTB().getEma5().getEMA() > cutLoss
				&& buying
				&& Global.getCurrentPoint() < cutLoss + 10
				&& Global.getCurrentPoint() > cutLoss)
		{
			
			updateHighLow();
				
				while (Global.isRapidDrop())
				{
					
					if (XMLWatcher.M5EMA50)
						cutLoss = GetData.getLongTB().getEma50().getEMA();
					else if (XMLWatcher.M5EMA250)
						cutLoss = GetData.getLongTB().getEma250().getEMA();
					
					
					updateHighLow();
					
					if (isDownTrend())
					{
						Global.addLog("Down Trend");
						shutDownSAR();
						return;	
					}
					
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
				
				if  (Global.getCurrentPoint() > cutLoss + 10)
					Global.addLog("Rise to fast, waiting for a pull back");
				
				
				while (Global.getCurrentPoint() > cutLoss + 10 || Global.isRapidDrop())
				{
					
					updateHighLow();
					
					if (XMLWatcher.M5EMA50)
						cutLoss = GetData.getLongTB().getEma50().getEMA();
					else if (XMLWatcher.M5EMA250)
						cutLoss = GetData.getLongTB().getEma250().getEMA();
					
					if  (Global.getCurrentPoint() > cutLoss + 50)
					{
						Global.addLog("Too far away");
						return;
					}				
										
					sleep(1000);		
				}
				
				trailingDown(2);
				
				if (Global.getCurrentPoint() < cutLoss - 10)
				{
					Global.addLog("Current point out of range");
					shutDownSAR();
					return;
				}

				longContract();
				
				Global.addLog("Ref Low: " + refLow);
//				cutLoss = refLow;
				
//			}	
		}else if (GetData.getShortTB().getEma5().getEMA() < cutLoss
				&& selling
				&& Global.getCurrentPoint() > cutLoss - 10
				&& Global.getCurrentPoint() < cutLoss)
		{
			
			// below is not correct
//			if (Global.getCurrentPoint() > SAR - 5 && Global.getCurrentPoint() < SAR && !Global.isRapidRise())
//			{
			
//			waitForANewCandle();
			
			updateHighLow();
			
				while (Global.isRapidRise())
				{
					
					updateHighLow();
					
					if (XMLWatcher.M5EMA50)
						cutLoss = GetData.getLongTB().getEma50().getEMA();
					else if (XMLWatcher.M5EMA250)
						cutLoss = GetData.getLongTB().getEma250().getEMA();
					
					if (isUpTrend())
					{
						Global.addLog("Up Trend");
						shutDownSAR();
						return;	
					}
					
					
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
				
				
				if (Global.getCurrentPoint() < cutLoss - 10)
					Global.addLog("Drop to fast, waiting for a pull back");
				
				while (Global.getCurrentPoint() < cutLoss - 10 || Global.isRapidRise())
				{
					updateHighLow();
					
					if (XMLWatcher.M5EMA50)
						cutLoss = GetData.getLongTB().getEma50().getEMA();
					else if (XMLWatcher.M5EMA250)
						cutLoss = GetData.getLongTB().getEma250().getEMA();
					
					if (Global.getCurrentPoint() < cutLoss - 50)
					{
						Global.addLog("Too far away");
						return;
					}
					
					sleep(1000);
				}
				
				trailingUp(2);
				
				if (Global.getCurrentPoint() > cutLoss + 10)
				{
					Global.addLog("Current point out of range");
					shutDownSAR();
					return;
				}

				shortContract();
				
				Global.addLog("Ref High: " + refHigh);
//				cutLoss = refHigh;
//			}
		}
	}

	private void shutDownSAR()
	{
		XMLWatcher.updateEMAXML("buying", "false");
		XMLWatcher.updateEMAXML("selling", "false");
		Global.addLog("Shut down RuleEMA");
		buying = false;
		selling = false;
	}


	double getCutLossPt()
	{
		
		double stair = XMLWatcher.EMAstair;
		
		updateExpectedProfit(10);

		if (Global.getNoOfContracts() > 0){
			
			// first profit then loss
//			if (tempCutLoss < cutLoss - 10 && refHigh > cutLoss + 30)
//				tempCutLoss = cutLoss - 10; 
				
			
			if (tempCutLoss < cutLoss - 10)
				tempCutLoss = cutLoss - 10; 
			
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 10;
			}
			
			return Math.max(10, buyingPoint - cutLoss + 20);
		}
		else
		{
			
			
			
			if (tempCutLoss > cutLoss + 10)
				tempCutLoss = cutLoss + 10; 
	
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
				
			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 10;
			}
			return  Math.max(10, cutLoss - buyingPoint + 5);
		}
	}

	// use 1min instead of 5min
	double getStopEarnPt()
	{

	
			if (Global.getNoOfContracts() > 0)
			{
				
				if (refLow < cutLoss - 20){
					shutdown = true;
					return Math.min(20, refHigh - buyingPoint - 5);
				}
				
				if (refLow < cutLoss - 10)
				{
//					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 30;
				} 
					
				return Math.max(10, stopEarn - buyingPoint - 10);
			}
			else
			{
				
				if (refHigh > cutLoss + 20){
					shutdown = true;
					return Math.min(20, buyingPoint - refLow - 5);
				}
				
				if (refHigh > cutLoss + 10)
				{
//					Global.addLog("Line unclear, trying to take little profit");
					shutdown = true;
					return 30;
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
	
	@Override
	void updateStopEarn()
	{

		double stair = XMLWatcher.EMAstair;
		
		if (Global.getNoOfContracts() > 0)
		{
			
			if (stair != 0 && tempCutLoss < stair && GetData.getShortTB().getLatestCandle().getClose() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getLongTB().getLatestCandle().getLow() > tempCutLoss
					&& tempCutLoss < stopEarn)	
			{
					double stopEarnPt = Math.min(stopEarn, GetData.getLongTB().getLatestCandle().getLow());
					Global.addLog("StopEarn update: " + stopEarnPt);
					tempCutLoss = stopEarnPt;
			}
//			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6))
//				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (GetData.getLongTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > stopEarn)
			{
				double stopEarnPt = Math.max(stopEarn, GetData.getLongTB().getLatestCandle().getHigh());
				Global.addLog("StopEarn update: " + stopEarnPt);
				tempCutLoss = stopEarnPt;
			}
//			if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6))
//				tempCutLoss = 0;
		}

	}
	
	@Override
	void updateExpectedProfit(long buffer){
		
//		long max = 0;
//		if (getExpectedProfit() > 100)
//			max = 100;
//		else
//			max = getExpectedProfit();
		
		if (Global.shutDownRaising)
		{
			shutDownRaising = true;
			Global.shutDownRaising = false;
		}
			
		double range = refHigh - refLow;

		if (Global.getNoOfContracts() > 0)
		{
			double profitLine;
			double ema = 0;
			
			if (refHigh > GetData.getLongTB().getEma50().getEMA() + 30)
				ema = GetData.getLongTB().getEma50().getEMA();
			else if (refHigh > GetData.getLongTB().getEma250().getEMA() + 30)
				ema = GetData.getLongTB().getEma250().getEMA();
			
			if (range > 60 && range < 100)
			{
				
				if (Math.abs(Global.getNoOfContracts()) < 2
						&& !shutDownRaising
						&& Global.getCurrentPoint() > ema
						&& Global.getCurrentPoint() < ema + 5
						&& ema != 0)
				{
					Raising raise = new Raising();
					raise.buying = true;
					raise.cutLoss = ema - 5;
					raise.noOfContracts = 1;	
					
					RuleRaising raising = new RuleRaising(raise);
					Thread r = new Thread (raising);
					r.start();
				}
				
				if (ema == 0)
					return; // just not updating the profit line
				
				profitLine = ema - 10;
				
				if (tempCutLoss < profitLine)
				{
					tempCutLoss = profitLine;
					Global.addLog("Expected profit updated: " + profitLine);
				}
				
			}else if (range > 100)
			{
				
				if (Math.abs(Global.getNoOfContracts()) < 3
						&& !shutDownRaising
						&& Global.getCurrentPoint() > ema
						&& Global.getCurrentPoint() < ema + 5
						&& ema != 0)
				{
					Raising raise = new Raising();
					raise.buying = true;
					raise.cutLoss = ema - 5;
					raise.noOfContracts = 1;	
					
					RuleRaising raising = new RuleRaising(raise);
					Thread r = new Thread (raising);
					r.start();
				}
				
				if (ema == 0)
					return; // just not updating the profit line
				
				profitLine = ema - 10;
				
				if (tempCutLoss < profitLine)
				{
					tempCutLoss = profitLine;
					Global.addLog("Expected profit updated: " + profitLine);
				}
				
				
			}
			
			
		}else
		{
			
			double profitLine;
			
			double ema = 0;
			
			if (refLow < GetData.getLongTB().getEma50().getEMA() - 30)
				ema = GetData.getLongTB().getEma50().getEMA();
			else if (refLow < GetData.getLongTB().getEma250().getEMA() - 30)
				ema = GetData.getLongTB().getEma250().getEMA();
			
			if (getProfit() > 50 && getProfit() < 100)
			{
				
				if (Math.abs(Global.getNoOfContracts()) < 2
						&& !shutDownRaising
						&& Global.getCurrentPoint() < ema
						&& Global.getCurrentPoint() > ema - 5
						&& ema != 0)
				{
					Raising raise = new Raising();
					raise.selling = true;
					raise.cutLoss = ema + 5;
					raise.noOfContracts = 1;	
					
					RuleRaising raising = new RuleRaising(raise);
					Thread r = new Thread (raising);
					r.start();
				}
				
				if (ema == 0)
					return;
				
				profitLine = ema + 10;
				
				if (tempCutLoss > profitLine)
				{
					tempCutLoss = profitLine;
					Global.addLog("Expected profit updated: " + profitLine);
				}
				
			}else if (getProfit() > 100)
			{
				
				if (Math.abs(Global.getNoOfContracts()) < 3
						&& !shutDownRaising
						&& Global.getCurrentPoint() < ema
						&& Global.getCurrentPoint() > ema - 5
						&& ema != 0)
				{
					Raising raise = new Raising();
					raise.selling = true;
					raise.cutLoss = ema + 5;
					raise.noOfContracts = 2;	
					
					RuleRaising raising = new RuleRaising(raise);
					Thread r = new Thread (raising);
					r.start();
				}
				
				if (ema == 0)
					return;
				
				profitLine = ema + 10;
				
				if (tempCutLoss > profitLine)
				{
					tempCutLoss = profitLine;
					Global.addLog("Expected profit updated: " + profitLine);
				}
				
				
			}
			
			
			
			
		}
		
		
//		if (Global.getNoOfContracts() > 0)
//		{
//			if (getHoldingTime() > 300)
//			{
//
//				if (getProfit() > max + buffer && tempCutLoss < buyingPoint + max)
//				{
//					tempCutLoss = buyingPoint + max;
//					Global.addLog("Expected profit updated: " + (buyingPoint + max));
//				}
//
//				else if (getProfit() < max + buffer && getProfit() >= buffer+2 && tempCutLoss < buyingPoint + getProfit() - buffer)
//				{
//					tempCutLoss = buyingPoint + getProfit() - buffer;
//					Global.addLog("Expected profit updated: " + (buyingPoint + getProfit() - buffer));
//				}
//
//			}
//		}else
//		{			
//			if (getHoldingTime() > 300)
//			{
//
//				if (getProfit() > max + buffer && tempCutLoss > buyingPoint - max)
//				{
//					tempCutLoss = buyingPoint - max;
//					Global.addLog("Expected profit updated: " + (buyingPoint - max));
//				}
//
//				else if (getProfit() < max + buffer && getProfit() >= buffer+2 && tempCutLoss > buyingPoint - getProfit() + buffer)
//				{
//					tempCutLoss = buyingPoint - getProfit() + buffer;
//					Global.addLog("Expected profit updated: " + (buyingPoint - getProfit() + buffer));
//				}
//
//			}
//				
//		}
		
		
		
	}

	

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}