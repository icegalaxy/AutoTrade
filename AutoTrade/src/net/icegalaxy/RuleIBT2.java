package net.icegalaxy;

//Use the OPEN Line

public class RuleIBT2 extends Rules
{
	private double cutLoss;
	private boolean traded;
	private double refHigh;
	private double refLow;

	public RuleIBT2(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(91500, 93000, 160000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}
	
	double getMADiff(){
		return GetData.getEma5().getEMA() - GetData.getEma50().getEMA();
	}

	public void openContract()
	{
		
//		if (chasing.chaseUp() || chasing.chaseDown()){
//			
////			Global.setChasing(chasing);
////			chasing = new Chasing();
//		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown
				|| Global.getOpen() == 0 || traded || Global.getpHigh() == 0 || Global.getpLow() == 0)
			return;

		// Global.addLog("Open: " + Global.getOpen());
		// Global.addLog("EMA50: " + getTimeBase().getEMA(50));
		// Global.addLog("EMA240: " + getTimeBase().getEMA(240));
		// Global.addLog("0");

		// if (Global.getCurrentPoint() > Global.getOpen() + 30 &&
		// Global.getOpen() > Global.getpClose() + 10 &&
		// Global.getCurrentPoint() > getTimeBase().getMA(240)){

		// longContract();
		// traded = true;
		// cutLoss = Math.abs(buyingPoint - Global.getOpen());
		// Global.addLog("cutLoss: " + cutLoss);

		// }else
		if (
//				Global.getCurrentPoint() > Global.getOpen() + 15 && 
				Global.getOpen() > Global.getpClose() + 10
//				&& GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1) 
				&& Global.getCurrentPoint() < Global.getOpen() - 10
				)
		{

			Global.addLog("Waiting to break through open");
			while (Global.getCurrentPoint() < Global.getOpen() + 5)
			{
				sleep(1000);
				
				if (!isOrderTime())
					return;
			}
			

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}

		else if (
//				Global.getCurrentPoint() < Global.getOpen() - 15 
//				&& 
				Global.getOpen() - 10 < Global.getpClose()
//				&& GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1) 
				&& Global.getCurrentPoint() > Global.getOpen() + 10
				)
		{
			Global.addLog("Waiting to break through open");
			while (Global.getCurrentPoint() > Global.getOpen() - 5)
			{
				sleep(1000);
				
				if (!isOrderTime())
					return;
			}

			shortContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
		}

		sleep(1000);

	}
	
	
//	@Override
//	boolean trendReversed(){
//		
//		if (Global.getNoOfContracts() > 0)
//			return GetData.getEma5().getEMA() < refLow;
//		else
//			return GetData.getEma5().getEMA() > refHigh;
//		
//	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
//	void updateStopEarn()
//	{
//		double ema5;
//		double ema6;
////
////		if (getProfit() < 100)
//		
//		
//		
////		if (Math.abs(GetData.getEma5().getEMA() - GetData.getEma25().getEMA()) > 30)
////			{
////			ema6 = GetData.getEma5().getPreviousEMA(1);
////			ema5 = GetData.getEma5().getEMA();
////			}
////		else
////		{
//			ema5 = GetData.getShortTB().getLatestCandle().getClose();
//			ema6 = GetData.getEma25().getEMA();
////		}
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
//			// tempCutLoss = buyingPoint + 5;
//
//			if (buyingPoint > tempCutLoss && getProfit() > 50)
//			{
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint + 5;
//			}
//			
//			if (ema5 < ema6){
//				tempCutLoss = 99999;
////				if (getProfit() > 0)
//					chasing.setChaseUp(true);
//			}
//
//		} else if (Global.getNoOfContracts() < 0)
//		{
//
//			// if (ema5 > ema6)
//			// tempCutLoss = buyingPoint - 5;
//
//			if (buyingPoint < tempCutLoss && getProfit() > 50)
//			{
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint - 5;
//			}
//			
//			if (ema5 > ema6){
//				tempCutLoss = 0;
////				if (getProfit() > 0)
//					chasing.setChaseDown(true);
//			}
//		}
//
//	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return 20;
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
		
		
		
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}

}