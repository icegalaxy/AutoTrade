package net.icegalaxy;





//Use the OPEN Line

public class RuleDanny250Pena2 extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private Chasing chasing;
	double buffer = 5;

	public RuleDanny250Pena2(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 103000, 150000, 160000, 230000, 230000);
		chasing = new Chasing();
		// wait for EMA6, that's why 0945
	}

	private double getRefPt()
	{
		return getTimeBase().getEMA(10);
	}

	double getMADiff()
	{
		return GetData.getEma5().getEMA() - GetData.getEma50().getEMA();
	}

	double getClosePrice()
	{
		return GetData.getShortTB().getLatestCandle().getClose();
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
			Global.addLog("LossTimes: " + lossTimes);
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 10
				|| Math.abs(GetData.getEma250().getEMA() - GetData.getEma1200().getEMA()) > 100
				|| Global.balance < -30)
			return;

		// Global.addLog("P5: " + GetData.getEma5().getPreviousEMA(1));
		// Global.addLog("now5: " + GetData.getEma5().getEMA());

		if (GetData.getEma5().getPreviousEMA(1) < GetData.getEma250().getPreviousEMA(1)
				&& GetData.getEma5().getEMA() > GetData.getEma250().getEMA()
				&& GetData.getEma250().getEMA() == getHighestMA())
		{

			// Global.addLog("---------------------");
			// Global.addLog("P5: " + GetData.getEma5().getPreviousEMA(1));
			// Global.addLog("P250: " + GetData.getEma250().getPreviousEMA(1));
			// Global.addLog("5: " + GetData.getEma5().getEMA());
			// Global.addLog("250: " + GetData.getEma250().getEMA());
			// Global.addLog("---------------------");

			int spreadingTimes = 0;
			double refDiff = 0;

			while (spreadingTimes <= 3)
			{
				sleep(1000);

				if (GetData.getEma5().getEMA() < GetData.getEma250().getEMA())
				{
					Global.addLog("Trend Change");
					return;
				}

				if (!isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}

				if (getMADiff() > refDiff)
				{
					refDiff = getMADiff();
					spreadingTimes++;
					// Global.addLog("Spreading time: " + spreadingTimes);
				} else if (getMADiff() < refDiff)
				{
					refDiff = getMADiff();
					spreadingTimes--;
					if (spreadingTimes < 0)
						return;
				}
			}
			longContract();
			cutLossPt = buyingPoint - GetData.getEma250().getEMA();

		} else if (GetData.getEma5().getPreviousEMA(1) > GetData.getEma250().getPreviousEMA(1)
				&& GetData.getEma5().getEMA() < GetData.getEma250().getEMA()
				&& GetData.getEma250().getEMA() == getLowestMA())
		{

			// Global.addLog("---------------------");
			// Global.addLog("P5: " + GetData.getEma5().getPreviousEMA(1));
			// Global.addLog("P250: " + GetData.getEma250().getPreviousEMA(1));
			// Global.addLog("5: " + GetData.getEma5().getEMA());
			// Global.addLog("250: " + GetData.getEma250().getEMA());
			// Global.addLog("---------------------");

			int spreadingTimes = 0;
			double refDiff = 0;

			while (spreadingTimes <= 3)
			{
				sleep(1000);

				if (GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
				{
					Global.addLog("Trend Change");
					return;
				}

				if (!isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}

				if (getMADiff() < refDiff)
				{
					refDiff = getMADiff();
					spreadingTimes++;
					// Global.addLog("Spreading time: " + spreadingTimes);
				} else if (getMADiff() > refDiff)
				{
					refDiff = getMADiff();
					spreadingTimes--;
					if (spreadingTimes < 0)
						return;
				}

				// if (Math.abs(buyingPoint - GetData.getEma50().getEMA()) >
				// 50){
				// Global.addLog("Risk too high");
				// return;
				// }
			}

			shortContract();
			cutLossPt = GetData.getEma250().getEMA() - buyingPoint;
		}

		sleep(1000);

	}

	@Override
	boolean trendReversed()
	{

		if (Global.getNoOfContracts() > 0)
		{

//			if (getProfit() > 30)
				return GetData.getEma5().getEMA() < GetData.getEma250().getEMA();
//			else
//				return GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1);

		} else
		{
//			if (getProfit() > 30)
				return GetData.getEma5().getEMA() > GetData.getEma250().getEMA();
//			else
//				return GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1);
		}
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = GetData.getShortTB().getLatestCandle().getClose();
		ema6 = GetData.getEma25().getEMA();

		if (Global.getNoOfContracts() > 0)
		{

			if (isUpTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}

			if (buyingPoint > tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

			if (ema5 < ema6)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
				chasing.setChaseUp(true);
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (isDownTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}

			if (buyingPoint < tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;

			}

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
				chasing.setChaseDown(true);
			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		// return Math.min(cutLossPt + 10, 40);
		return 100;

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

		if (Global.getCurrentPoint() > chasing.getRefHigh())
			chasing.setRefHigh(Global.getCurrentPoint());
		if (Global.getCurrentPoint() < chasing.getRefLow())
			chasing.setRefLow(Global.getCurrentPoint());
	}

	private int getLossTimesAllowed()
	{

		double balance = Global.balance + Global.getCurrentPoint() * Global.getNoOfContracts();

		if (balance > 30)
			return 3;
		else if (balance > 0)
			return 2;
		else
			return 1;
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