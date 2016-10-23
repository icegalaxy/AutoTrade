package net.icegalaxy;



public class RuleIBT extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;
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
				|| TimePeriodDecider.getTime() > 92000 || Global.getOpen() == 0)
			return;

//		Global.addLog("Open: " + Global.getOpen());
//		Global.addLog("EMA50: " + getTimeBase().getEMA(50));
//		Global.addLog("EMA240: " + getTimeBase().getEMA(240));
//		Global.addLog("0");
		
		if (Global.getCurrentPoint() > Global.getOpen() + 15 && Global.getOpen() > Global.getpClose() + 10 && Global.getCurrentPoint() > getTimeBase().getMA(240))
		{
//			while (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getPreviousCandle(1).getClose())
//				wanPrevious.middleWaiter(wanNext);
			
//			Global.addLog("1");

//			while (TimePeriodDecider.getTime() < 91800)
//			{
//
//				if (Global.getCurrentPoint() < Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing up");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}

//			Global.addLog(className + ": waiting for a pull back");
//
//			while (GetData.getShortTB().)
//			{
//
//				if (Global.getCurrentPoint() < Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing up");
//					return;
//				}
//
//				if (TimePeriodDecider.getTime() > 91800)
//				{
//					shutdown = true;
//					Global.addLog(className + ": waited for too long");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}
//
//			Global.addLog(className + ": waiting for a second corner");
//
//			while (GetData.getShortTB().getLatestCandle().getClose() < GetData.getShortTB()
//					.getPreviousCandle(1).getClose())
//			{
//
//				if (Global.getCurrentPoint() < Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing up");
//					return;
//				}
//				
//				if (TimePeriodDecider.getTime() > 91800)
//				{
//					shutdown = true;
//					Global.addLog(className + ": waited for too long");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}

			longContract();
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			
//			Global.addLog("BuyingPt: " + buyingPoint);
//			Global.addLog("Open: " + Global.getOpen());
//			Global.addLog("cutLoss: " + Global.getOpen());
			

		} else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() -10 < Global.getpClose()  && Global.getCurrentPoint() < getTimeBase().getMA(240))
		{
			
//			while (GetData.getShortTB().getLatestCandle().getClose() < GetData.getShortTB().getPreviousCandle(1).getClose())
//				wanPrevious.middleWaiter(wanNext);
			
//			Global.addLog("2");
			
//			while (TimePeriodDecider.getTime() < 91800)
//			{
//
//				if (Global.getCurrentPoint() > Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing down");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}

//			Global.addLog(className + ": waiting for a pull back");
//
//			while (Global.getCurrentPoint() < Global.getpOpen() - 15)
//			{
//
//				if (Global.getCurrentPoint() > Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing down");
//					return;
//				}
//
//				if (TimePeriodDecider.getTime() > 91800)
//				{
//					shutdown = true;
//					Global.addLog(className + ": waited for too long");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}
//
//			Global.addLog(className + ": waiting for a second corner");
//
//			while (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB()
//					.getPreviousCandle(1).getClose())
//			{
//
//				if (Global.getCurrentPoint() > Global.getOpen())
//				{
//					shutdown = true;
//					Global.addLog(className + ": not standing down");
//					return;
//				}
//				
//				if (TimePeriodDecider.getTime() > 91800)
//				{
//					shutdown = true;
//					Global.addLog(className + ": waited for too long");
//					return;
//				}
//
//				wanPrevious.middleWaiter(wanNext);
//			}

			shortContract();
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
		}

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
			if (ema5 < ema6)
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (ema5 > ema6)
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

		// 有可能行夠50點都未 5 > 6，咁會即刻食左
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
	


}