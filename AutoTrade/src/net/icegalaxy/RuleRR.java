package net.icegalaxy;

//Use the OPEN Line

public class RuleRR extends Rules
{

	OHLC currentOHLC;

	public RuleRR(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91600, 113000, 150000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.balance < -30)
			return;

		for (OHLC item : XMLWatcher.ohlcs)
		{
			currentOHLC = item;
//			setOrderTime(item.getOrderTime());

			if (Global.getNoOfContracts() != 0)
				return;

			if (currentOHLC.cutLoss == 0)
				continue;

			if (currentOHLC.shutdown)
				continue;

			if (GetData.getLongTB().getEma5().getEMA() > currentOHLC.cutLoss && Global.getCurrentPoint() < currentOHLC.cutLoss + 15
					&& Global.getCurrentPoint() > currentOHLC.cutLoss)
			{

				Global.addLog("Reached " + currentOHLC.name);
				
				while (Global.isRapidDrop()
						|| getTimeBase().getLatestCandle().getOpen() > getTimeBase().getLatestCandle().getClose())
				{

					if (GetData.getLongTB().getEma5().getEMA() < currentOHLC.cutLoss)
					{
						Global.addLog("EMA5 out of range");
						return;
					}

					if (Global.getCurrentPoint() < currentOHLC.cutLoss - 10)
					{
						Global.addLog("Current point out of range");
						return;
					}

					sleep(1000);
				}

				longContract();
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			} else if (GetData.getLongTB().getEma5().getEMA() < currentOHLC.cutLoss
					&& Global.getCurrentPoint() > currentOHLC.cutLoss - 15
					&& Global.getCurrentPoint() < currentOHLC.cutLoss)
			{
				
				Global.addLog("Reached " + currentOHLC.name);

				while (Global.isRapidRise()
						|| getTimeBase().getLatestCandle().getOpen() < getTimeBase().getLatestCandle().getClose())
				{

					if (GetData.getLongTB().getEma5().getEMA() > currentOHLC.cutLoss)
					{
						Global.addLog("EMA5 out of range");
						return;
					}

					if (Global.getCurrentPoint() > currentOHLC.cutLoss + 10)
					{
						Global.addLog("Current point out of range");
						return;
					}

					sleep(1000);
				}

				shortContract();
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			}

		}
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		if (Global.getNoOfContracts() > 0)
			return Math.max(20, buyingPoint - currentOHLC.cutLoss + 10);
		else
			return Math.max(20, currentOHLC.cutLoss - buyingPoint + 10);
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

		double intraDayStopEarn = XMLWatcher.stopEarn;

		if (intraDayStopEarn == 0)
		{
			if (Global.getNoOfContracts() > 0)
				return Math.max(10, currentOHLC.stopEarn - buyingPoint - 10);
			else
				return Math.max(10, buyingPoint - currentOHLC.stopEarn - 10);
		} else
		{
			if (Global.getNoOfContracts() > 0)
				return Math.max(10, intraDayStopEarn - buyingPoint - 10);
			else
				return Math.max(10, buyingPoint - intraDayStopEarn - 10);

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