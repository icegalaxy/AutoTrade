package net.icegalaxy;


public class RuleIBT extends Rules {

	// private double refEMA;
	private boolean traded;
	private double cutLoss;

	public RuleIBT(boolean globalRunRule) {
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91800, 92000, 160000, 160000, 230000, 230000);
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown
				|| TimePeriodDecider.getTime() > 92000 || Global.getOpen() == 0 || traded)
			return;

		if (Global.getCurrentPoint() > Global.getOpen() + 15 && Global.getOpen() > Global.getpClose() + 10 && Global.getCurrentPoint() > getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			
			Global.addLog("cutLoss: " + cutLoss);
			

		}else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() -10 < Global.getpClose()  && Global.getCurrentPoint() < getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{
			


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
		float ema5;
		float ema6;
		
		if (getProfit() > 50 && getProfit() < 100){
			ema5 = GetData.getShortTB().getEMA(5);
			ema6 = GetData.getShortTB().getEMA(6);
		}else
		{
			ema5 = getTimeBase().getEMA(5);
			ema6 = getTimeBase().getEMA(6);
		}

		if (Global.getNoOfContracts() > 0)
		{
			if (ema5 < ema6 && getProfit() > 30)
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (ema5 > ema6 && getProfit() > 30)
				tempCutLoss = 0;

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
	}

	double getStopEarnPt()
	{
		if (Global.getNoOfContracts() > 0 && getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
			return -100;
		else if (Global.getNoOfContracts() < 0 && getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
			return -100;

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
	


}