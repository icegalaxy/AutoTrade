package net.icegalaxy;

import java.util.ArrayList;




public class RuleBreakThrough extends Rules {

	
	private double cutLoss;
	double[] ohlcs;
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
		
		ohlcs = new double[]
				{ Global.getOpen(), Global.getpHigh(), Global.getpLow(), Global.getpClose(), Global.getAOH(), Global.getAOL(), GetData.getShortTB().getEma250().getEMA() };

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown || Global.balance < -30)
			return;

		for (double item : ohlcs)
		{
			ohlc = item;

			if (Global.getNoOfContracts() !=0)
				return;
			
			if (ohlc == 0)
				continue;
				
			if (GetData.getEma5().getPreviousEMA(1) < ohlc && GetData.getEma5().getEMA() > ohlc)
			{

				while (!Global.isRapidRise())
				{

					if (Global.getCurrentPoint() > refHigh)
						refHigh = Global.getCurrentPoint();
					else if (Global.getCurrentPoint() < refLow)
						refLow = Global.getCurrentPoint();

					if (GetData.getEma5().getEMA() < ohlc)
					{
						Global.addLog("EMA5 < Open, EMA5: " + GetData.getEma5().getEMA() + ", Open: " + ohlc);
						return;
					}

//					if (GetData.getShortTB().getRSI() > 70 || Global.isRapidDrop())
//					{
//						Global.addLog("RSI > 70");
//						return;
//
//					}

					sleep(1000);
				}

				

				longContract();

			} else if (GetData.getEma5().getPreviousEMA(1) > ohlc && GetData.getEma5().getEMA() < ohlc)
			{

				
				while (!Global.isRapidDrop())
				{

					if (Global.getCurrentPoint() > refHigh)
						refHigh = Global.getCurrentPoint();
					else if (Global.getCurrentPoint() < refLow)
						refLow = Global.getCurrentPoint();

					if (GetData.getEma5().getEMA() > ohlc)
					{
						Global.addLog("EMA5: " + GetData.getEma5().getEMA() + ", Open: " + ohlc);
						return;
					}

//					if (GetData.getShortTB().getRSI() < 30 || Global.isRapidRise())
//					{
//						Global.addLog("RSI < 30");
//						return;
//
//					}

					sleep(1000);
				}

			

				shortContract();

			}
			sleep(1000);
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
			super.updateStopEarn();

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
		return Math.max(50, cutLoss);
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
		double adjustPt = 0;

		if (Global.getNoOfContracts() > 0)
		{

			adjustPt = buyingPoint - refLow;

//			if (Global.isRapidDrop())
//				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{
			adjustPt = refHigh - buyingPoint;

//			if (Global.isRapidRise())
//				tempCutLoss = 0;
		}
		double pt;
		double stopEarn;

		pt = (160000 - TimePeriodDecider.getTime()) / 1000;

		if (trendReversed)
		{
//			shutdown = true;
			if (refHigh > Global.getDayHigh() - 5 || refLow < Global.getDayLow() + 5)
				return 5 - adjustPt;

			// return 5;
			return Math.min(5, pt / 2 - adjustPt);
		} else if (refHigh > Global.getDayHigh() - 5 || refLow < Global.getDayLow() + 5)
			return 5;
		
		
		
		else if (pt < 20)
			stopEarn = 20 - adjustPt;
		else
			stopEarn = pt - adjustPt;
		
			return Math.max(5, stopEarn);

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