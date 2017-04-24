package net.icegalaxy;

import java.util.ArrayList;






public class RuleBreakThrough extends Rules {

	
	private double cutLoss;
	OHLC[] ohlcs;
	double ohlc = 0;
	private boolean trendReversed;

	public RuleBreakThrough(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(93000, 113000, 130000, 160000, 231500, 231500);

	}



	public void openContract()
	{

		refHigh = 0;
		refLow = 99999;
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;
		
		ohlcs = new OHLC[]
				{XMLWatcher.open, XMLWatcher.pHigh, XMLWatcher.pLow, XMLWatcher.pClose};

		

		for (OHLC item : ohlcs)
		{
			ohlc = item.position;

			if (Global.getNoOfContracts() !=0)
				return;
			
			if (ohlc == 0)
				continue;
			
//			if (!item.breakThroughValid)
//				continue;
			

//			if (Math.abs(Global.getCurrentPoint() - ohlc) > 30)
//				continue;

			if (Global.isHugeRise() && GetData.getShortTB().getLatestCandle().getOpen() < ohlc - 10 && Global.getCurrentPoint() > ohlc + 10)
			{
				
				
				//recordRecentLow
				
				//if < ohlc then return
				
				//waitForAPullBackToRecentLow + 5
				
//				while (Global.getCurrentPoint() > ohlc + 10)
//				{
//					
//					if (Global.getCurrentPoint() > ohlc + 50)
//						return;
//					
//					wanPrevious.middleWaiter(wanNext);
//				}
				
				
				
				
				
				longContract();		
				Global.addLog("OHLC: " + item.name);
				
				return;
				
			}else if (Global.isHugeDrop() && GetData.getShortTB().getLatestCandle().getOpen() > ohlc + 10 && Global.getCurrentPoint() < ohlc - 10)
			{
				
				
				//recordRecentLow
				
				//if < ohlc then return
				
				//waitForAPullBackToRecentLow + 5
				
//				while (Global.getCurrentPoint() < ohlc - 10)
//				{
//					if (Global.getCurrentPoint() < ohlc - 50)
//						return;
//					
//					wanPrevious.middleWaiter(wanNext);
//				}
				
				
				
				
				shortContract();
				Global.addLog("OHLC: " + item.name);
				
				return;
			}
			
			
			
		}
	}

	public double getCurrentClose()
	{
		return GetData.getShortTB().getLatestCandle().getClose();
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{
		if (getProfit() > 5)
			profitedStopEarn();
		else
			{
			if (Global.getNoOfContracts() > 0)
				tempCutLoss = 99999;
			else
				tempCutLoss = 0;
			}

	}

	void profitedStopEarn()
	{
		double ema5;
		double ema6;
		//
		// if (getProfit() < 100)
		// {
		ema5 = Global.getCurrentPoint();
		ema6 = GetData.getEma5().getEMA();
		// } else
		// {
		// ema5 = StockDataController.getLongTB().getEMA(5);
		// ema6 = StockDataController.getLongTB().getEMA(6);
		// }

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
			if (tempCutLoss < buyingPoint + 5)
				tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6 || Global.isRapidDrop())
			{
				tempCutLoss = 99999;
				// if (getProfit() > 0)
				// chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
			if (tempCutLoss > buyingPoint - 5)
			 tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6 || Global.isRapidRise())
			{
				tempCutLoss = 0;
				// if (getProfit() > 0)
				// chasing.setChaseDown(true);
			}
		}
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
//		if (Global.getNoOfContracts() > 0 && GetData.getEma5().getEMA() < ohlc)
//			return 10;
//		
//		else if (Global.getNoOfContracts() < 0 && GetData.getEma5().getEMA() > ohlc)
//			return 10;
//		
//		else
		return Math.abs(buyingPoint - ohlc) + 3;
	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

		}

	}

	@Override
	boolean trendReversed()
	{

		if (Global.getNoOfContracts() > 0)
			return GetData.getEma5().getEMA() < ohlc;
		else
			return GetData.getEma5().getEMA() > ohlc;
	}

	double getStopEarnPt()
	{
		
//		if (Global.getNoOfContracts() > 0 && GetData.getEma5().getEMA() < ohlc)
//			return 15;
//		
//		else if (Global.getNoOfContracts() < 0 && GetData.getEma5().getEMA() > ohlc)
//			return 15;
//		
//		else
		
		return (Math.abs(buyingPoint - ohlc)) * 2;

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