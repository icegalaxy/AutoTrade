package net.icegalaxy;

public class RuleIBT extends Rules
{

	// private double refEMA;
	private boolean traded;
	private double cutLoss;
	private Chasing chasing;

	public RuleIBT(boolean globalRunRule)
	{
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91600, 93000, 160000, 160000, 230000, 230000);
		chasing = new Chasing();
	}

	public void openContract()
	{

		// if (chasing.chaseUp() || chasing.chaseDown()){
		//
		// Global.setChasing(chasing);
		// chasing = new Chasing();
		// }

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown || TimePeriodDecider.getTime() > 93000
				|| Global.getOpen() == 0 || traded)
			return;

		if (GetData.getShortTB().getLatestCandle().getClose() > Global.getOpen() + 10 
				&& XMLWatcher.ibtRise
				&& Global.getOpen() > Global.getpClose() + 10 
				&& TimePeriodDecider.getTime() > 91600
				&& Global.getCurrentPoint() < Global.getOpen() + 10 
				&& Global.getCurrentPoint() > Global.getOpen())
		{

			Global.addLog("Reached Open");

			waitForANewCandle();

			while (getTimeBase().getLatestCandle().getOpen() > getTimeBase().getLatestCandle().getClose() - 5) 
																											
			{

				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}

				if (Global.getCurrentPoint() < Global.getOpen() - 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
			}

			if (Global.getCurrentPoint() > Global.getOpen() + 20)
				Global.addLog("Rise to fast, waiting for a pull back");

			while (Global.getCurrentPoint() > Global.getOpen() + 10)
			{
				if (Global.getCurrentPoint() > Global.getOpen() + 50)
				{
					Global.addLog("Too far away");
					return;
				}
				sleep(1000);
			}

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}

		else if (GetData.getShortTB().getLatestCandle().getClose() < Global.getOpen() - 10
				&& Global.getOpen() - 10 < Global.getpClose() 
				&& XMLWatcher.ibtDrop
				&& TimePeriodDecider.getTime() > 91600

				&& Global.getCurrentPoint() > Global.getOpen() - 10 
				&& Global.getCurrentPoint() < Global.getOpen())
		{

			Global.addLog("Reached Open");

			waitForANewCandle();

			while (getTimeBase().getLatestCandle().getOpen() < getTimeBase().getLatestCandle().getClose() + 5) 
																												
			{

				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}

				if (Global.getCurrentPoint() > Global.getOpen() + 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
			}

			if (Global.getCurrentPoint() < Global.getOpen() - 20)
				Global.addLog("Drop to fast, waiting for a pull back");

			while (Global.getCurrentPoint() < Global.getOpen() - 10)
			{
				if (Global.getCurrentPoint() < Global.getOpen() - 50)
				{
					Global.addLog("Too far away");
					return;
				}
				sleep(1000);

			}

			shortContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}

		sleep(1000);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;
		//
		// if (getProfit() < 100)
		// {
		ema5 = GetData.getShortTB().getLatestCandle().getClose();
		ema6 = GetData.getLongTB().getEMA(5);
		// } else
		// {
		// ema5 = GetData.getLongTB().getEMA(5);
		// ema6 = GetData.getLongTB().getEMA(6);
		// }

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
			// tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6)
			{
				tempCutLoss = 99999;
				// if (getProfit() > 0)
				chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
			// tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				// if (getProfit() > 0)
				chasing.setChaseDown(true);
			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return cutLoss + 10;
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

	double getStopEarnPt()
	{
		// if (Global.getNoOfContracts() > 0)
		// {
		// if (GetData.getShortTB().getLatestCandle().getClose() >
		// getTimeBase().getEMA(5))
		// return -100;
		//
		//
		//
		//
		// } else if (Global.getNoOfContracts() < 0)
		// {
		// if (GetData.getShortTB().getLatestCandle().getClose() <
		// getTimeBase().getEMA(6))
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