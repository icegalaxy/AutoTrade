package net.icegalaxy;



public class RuleDanny50 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleDanny50(boolean globalRunRule) {
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91600, 113000, 130500, 160000, 172000, 231500);
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= getLossTimesAllowed())
			return;

		while (Math.abs(Global.getCurrentPoint() - getTimeBase().getEMA(50)) > 10)
			sleep(1000);

		if (isUpTrend())
		{

			Global.addLog("Up Trend");

//			while (Global.getCurrentPoint() > getTimeBase().getEMA(50) + 5)
//			{
//				sleep(1000);
//
//				if (!isUpTrend())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//			}
			refPt = Global.getCurrentPoint();

			Global.addLog("CurrentPt: " + Global.getCurrentPoint());
			Global.addLog("EMA240: " + getTimeBase().getEMA(240));

			while (GetData.getShortTB().getEMA(5) < GetData.getShortTB().getMA(20)
					&& Math.abs(Global.getCurrentPoint() - refPt) < 30)
			{
				sleep(1000);

//				if (Math.abs(Global.getCurrentPoint() - refPt) > 30)
//				{
//					Global.addLog("Risk too high");
//					return;
//				}
				
				
				if (!isUpTrend())
				{
					Global.addLog("Trend Change");
					return;
				}

				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();

			}
			longContract();
			cutLossPt = Math.abs(buyingPoint - refPt);
			Global.addLog("Before Low: " + refPt);
			Global.addLog("CutLossPt: " + cutLossPt);

		} else if (isDownTrend())
		{

			Global.addLog("Down Trend");

//			while (Global.getCurrentPoint() < getTimeBase().getEMA(50) - 5)
//			{
//				sleep(1000);
//
//				if (!isDownTrend())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//			}
			refPt = Global.getCurrentPoint();

			Global.addLog("CurrentPt: " + Global.getCurrentPoint());
			Global.addLog("EMA240: " + getTimeBase().getEMA(240));

			while (GetData.getShortTB().getEMA(5) > GetData.getShortTB().getMA(20)
					&& Math.abs(Global.getCurrentPoint() - refPt) < 30)
			{

				sleep(1000);

//				if (Math.abs(Global.getCurrentPoint() - refPt) > 30)
//				{
//					Global.addLog("Risk too high");
//					return;
//				}
				
				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

				if (!isDownTrend())
				{
					Global.addLog("Trend Change");
					return;
				}
			}

			shortContract();
			cutLossPt = Math.abs(buyingPoint - refPt);
			Global.addLog("Before High: " + refPt);
			Global.addLog("CutLossPt: " + cutLossPt);

		}

	}

	private int getLossTimesAllowed()
	{

		double balance = Global.balance + Global.getCurrentPoint() * Global.getNoOfContracts();

		if (balance > 60)
			return 3;
		else if (balance > 30)
			return 2;
		else
			return 1;
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		int difference;

		if (getProfit() > 100)
			difference = 0;
		else
			difference = 2;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = getTimeBase().getEMA(5);
		ema6 = getTimeBase().getEMA(6);
		// }else{
		// ema5 = GetData.getShortTB().getEMA(5);
		// ema6 = GetData.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0)
		{

			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

			if (ema5 < ema6 && getProfit() > 50)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
			}

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 x MA20");

			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		if (cutLossPt < 15)
			return 15;
		else
			return cutLossPt;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{
			//
			// while (Global.getCurrentPoint() <
			// GetData.getShortTB().getEMA(5)){
			// sleep(1000);
			// if (getProfit() < -30)
			// break;
			// }
			//

			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;

			// wait for it to clam down

			// if (Global.getCurrentPoint() < getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }

			// while (Global.getCurrentPoint() < getTimeBase().getEMA(6))
			// sleep(1000);

		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{
			//
			//
			// while (Global.getCurrentPoint() >
			// GetData.getShortTB().getEMA(5)){
			// sleep(1000);
			// if (getProfit() < -30)
			// break;
			// }

			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

			// if (Global.getCurrentPoint() > getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }
			//
			// while (Global.getCurrentPoint() > getTimeBase().getEMA(6))
			// sleep(1000);
		}
	}

	double getStopEarnPt()
	{

		// if (Global.getNoOfContracts() > 0){
		// if (!isUpTrend())
		// return -100;
		// }else if (Global.getNoOfContracts() < 0){
		// if (!isDownTrend())
		// return -100;
		// }

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}

}