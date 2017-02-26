package net.icegalaxy;



//Use the OPEN Line

public class RuleDanny250Pena4 extends Rules
{


	private double cutLoss;
	private double refHigh;
	private double refLow;
	private boolean trendReversed;

	public RuleDanny250Pena4(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 103000, 150000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

//		if (shutdown)
//		{
//			lossTimes++;
//			shutdown = false;
//		}
		
	
		
//		if (chasing.chaseUp() || chasing.chaseDown()){
//			
//			Global.setChasing(chasing);
//			chasing = new Chasing();
//		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0 || GetData.getEma250().getEMA() == 0 || shutdown
				|| Math.abs(GetData.getEma250().getEMA() - GetData.getEma1200().getEMA()) > 100)
			return;
		
//		while (Math.abs(Global.getCurrentPoint() - GetData.getEma250().getEMA()) < 20)
//			{
//				wanPrevious.middleWaiter(wanNext);
//				if (!isOrderTime())
//					return;
//			}

		if (GetData.getLongTB().getEma5().getPreviousEMA(1) < GetData.getLongTB().getEma250().getPreviousEMA(1)
				&& GetData.getLongTB().getEma5().getEMA() > GetData.getLongTB().getEma250().getEMA())
		{
		
			
			longContract();
			cutLoss = buyingPoint - refLow;
			
		}else if (GetData.getLongTB().getEma5().getPreviousEMA(1) > GetData.getLongTB().getEma250().getPreviousEMA(1)
				&& GetData.getLongTB().getEma5().getEMA() < GetData.getLongTB().getEma250().getEMA())
		{	
			
			
			shortContract();		
			cutLoss = refHigh - buyingPoint;
		}
		
		sleep(1000);
	}
	
	public double getCurrentClose(){
		return GetData.getShortTB().getLatestCandle().getClose();
	}
	
	void updateStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
			{
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
			}
			
			if (getProfit() >= 5 && trendReversed)
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
			{
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
			}
			
			if (getProfit() >= 5 && trendReversed)
				tempCutLoss = 0;
		}

	}

	// use 1min instead of 5min
//	void updateStopEarn()
//	{
//		double ema5;
//		double ema6;
////
////		if (getProfit() < 100)
////		{
//			ema5 = GetData.getShortTB().getLatestCandle().getClose();
//			ema6 = GetData.getEma25().getEMA();
////		} else
////		{
////			ema5 = StockDataController.getLongTB().getEMA(5);
////			ema6 = StockDataController.getLongTB().getEMA(6);
////		}
//
//		if (Global.getNoOfContracts() > 0)
//		{
//
//			// if (ema5 < ema6)
////			 tempCutLoss = buyingPoint + 5;
//
//			if (ema5 < ema6){
//				tempCutLoss = 99999;
////				if (getProfit() > 0)
////					chasing.setChaseUp(true);
//			}
//
//		} else if (Global.getNoOfContracts() < 0)
//		{
//
//			// if (ema5 > ema6)
////			 tempCutLoss = buyingPoint - 5;
//
//			if (ema5 > ema6){
//				tempCutLoss = 0;
////				if (getProfit() > 0)
////					chasing.setChaseDown(true);
//			}
//		}
//
//	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return Math.max(100, cutLoss);
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
	boolean trendReversed(){
		
		if (Global.getNoOfContracts() > 0)
			return GetData.getLongTB().getEma5().getEMA() < GetData.getLongTB().getEma250().getEMA();
		else
			return GetData.getLongTB().getEma5().getEMA() > GetData.getLongTB().getEma250().getEMA();		
	}

	double getStopEarnPt()
	{
//		if (Global.getNoOfContracts() > 0)
//		{
//			if (StockDataController.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5))
//				return -100;
//			
//			
//			
//			
//		} else if (Global.getNoOfContracts() < 0)
//		{
//			if (StockDataController.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(6))
//				return -100;
//		}
		int pt;
		
		pt = (160000 - TimePeriodDecider.getTime()) / 1000;
		
		if (trendReversed)
		{
			shutdown = true;
			return 5;
//			return Math.min(5, (160000 - TimePeriodDecider.getTime()) / 2000);		
		}
		else if (pt < 20)
			return 20;
		else return pt;
	}

	@Override
	public void trendReversedAction() {
		
		trendReversed = true;
	}
	
	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}