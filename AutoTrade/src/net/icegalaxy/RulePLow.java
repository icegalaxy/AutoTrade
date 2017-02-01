package net.icegalaxy;

public class RulePLow extends Rules {

	private double cutLoss;
	private double refHigh;
	private double refLow;

	public RulePLow(boolean globalRunRule) {
		super(globalRunRule);
//		setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(93000, 103000, 150000, 160000, 230000, 230000);
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
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getpLow() == 0 || shutdown
				|| Math.abs(GetData.getEma250().getEMA() - GetData.getEma1200().getEMA()) > 100
				|| Global.balance < -30)
			return;
		
//		while (Math.abs(Global.getCurrentPoint() - Global.getpLow()) < 20)
//			{
//				sleep(1000);
//				if (!isOrderTime())
//					return;
//			}

		if (GetData.getEma5().getPreviousEMA(1) < Global.getpLow()
				&& GetData.getEma5().getEMA() > Global.getpLow())
		{
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1))
			{
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (GetData.getEma5().getEMA() < Global.getpLow())
					return;
				
				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
//				else if (GetData.getEma5().getEMA() < refLow)
//					refLow = GetData.getEma5().getEMA();
				
				sleep(1000);
			}
			
			while (GetData.getEma5().getEMA() < refHigh)
			{
				sleep(1000);
				
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
				if (GetData.getEma5().getEMA() < Global.getpLow())
					return;

			 if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
			}
			
			if (GetData.getEma5().getEMA() < GetData.getEma250().getEMA())
				return;
			
			longContract();
			cutLoss = buyingPoint - refLow;
			
		}else if (GetData.getEma5().getPreviousEMA(1) > Global.getpLow()
				&& GetData.getEma5().getEMA() < Global.getpLow())
		{	
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1))
			{
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (GetData.getEma5().getEMA()  > Global.getpLow())
					return;
				
//				if (GetData.getEma5().getEMA() > refHigh)
//					refHigh = GetData.getEma5().getEMA();
				else if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
				sleep(1000);
			}
			
			while (GetData.getEma5().getEMA() > refLow)
			{
				sleep(1000);
				
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
				if (GetData.getEma5().getEMA() > Global.getpLow())
					return;

				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
			
				
			}
			
			if (GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
				return;
			
			shortContract();		
			cutLoss = refHigh - buyingPoint;
		}
	}
	
	public double getCurrentClose(){
		return GetData.getShortTB().getLatestCandle().getClose();
	}
	
	

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;
//
//		if (getProfit() < 100)
//		{
			ema5 = GetData.getShortTB().getLatestCandle().getClose();
			ema6 = GetData.getEma25().getEMA();
//		} else
//		{
//			ema5 = StockDataController.getLongTB().getEMA(5);
//			ema6 = StockDataController.getLongTB().getEMA(6);
//		}

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
//			 tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6){
				tempCutLoss = 99999;
//				if (getProfit() > 0)
//					chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
//			 tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6){
				tempCutLoss = 0;
//				if (getProfit() > 0)
//					chasing.setChaseDown(true);
			}
		}

	}

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
			return GetData.getEma5().getEMA() < refLow;
		else
			return GetData.getEma5().getEMA() > refHigh;
		
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
		
		
		
		return 20;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}