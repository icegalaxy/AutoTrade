package net.icegalaxy;

//Use the OPEN Line

public class RuleDanny2 extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private Chasing chasing;

	public RuleDanny2(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 115500, 130500, 160000, 230000, 230000);
		chasing = new Chasing();
		// wait for EMA6, that's why 0945
	}
	
	private double getRefPt(){
		return getTimeBase().getEMA(10);
	}
	
	double getMADiff(){
		return GetData.getEma5().getEMA() - GetData.getEma50().getEMA();
	}

	public void openContract()
	{
		


		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 10)
			return;

		if (isUpTrend2() 
//				&& GetData.getEma25().getEMA() > GetData.getEma50().getEMA()
///			&& GetData.getShortTB().getLatestCandle().getClose() < GetData.getEma25().getEMA() 
//				&& Global.getCurrentPoint() > GetData.getEma50().getEMA()
				&& GetData.getEma5().getEMA() > GetData.getEma50().getEMA()
				&& GetData.getEma50().getEMA() - GetData.getEma100().getEMA() > 20
				)
		{

			Global.addLog("Up Trend");
			
			while (Global.getCurrentPoint() > GetData.getEma50().getEMA() + 10)
			{
				sleep(1000);

				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				
				if (!isUpTrend2())
				{
					Global.addLog("Trend Change");
					return;
				}
			}
			int spreadingTimes = 0;
			double refDiff = 0;

			while (spreadingTimes < 3)
			{
				sleep(1000);

				if (!isUpTrend2() || GetData.getEma5().getEMA() < GetData.getEma50().getEMA())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (getMADiff() > refDiff){
					refDiff = getMADiff();
					spreadingTimes++;
//					Global.addLog("Spreading time: "  + spreadingTimes);
				}else if (getMADiff() < refDiff){
					refDiff = getMADiff();
					spreadingTimes--;
					if (spreadingTimes < 0)
						return;
				}
				
//				if (Math.abs(buyingPoint - GetData.getEma50().getEMA()) > 50){
//					Global.addLog("Risk too high");
//					return;
//				}

			}
			
//			if (GetData.getEma50().getEMA() - GetData.getEma100().getEMA() < 10)
//				return;
				
			longContract();
			cutLossPt = Math.abs(buyingPoint - GetData.getEma50().getEMA());
//			Global.addLog("EMA5: " + getTimeBase().getEMA(5));
//			Global.addLog("EMA10: " + getTimeBase().getEMA(10));
//			Global.addLog("EMA50: " + getTimeBase().getEMA(50));
//			Global.addLog("EMA240: " + getTimeBase().getEMA(240));
//			Global.addLog("CutLossPt: " + cutLossPt);
			
		} else if (isDownTrend2()
//				&& GetData.getEma25().getEMA() < GetData.getEma50().getEMA()
//				&& GetData.getShortTB().getLatestCandle().getClose() > GetData.getEma25().getEMA() 
//				&& Global.getCurrentPoint() < GetData.getEma50().getEMA()
				&& GetData.getEma5().getEMA() < GetData.getEma50().getEMA()
				&& GetData.getEma100().getEMA() - GetData.getEma50().getEMA() > 20
				)
		{

			Global.addLog("Down Trend");
			
			while (Global.getCurrentPoint() < GetData.getEma50().getEMA() - 10)
			{
				sleep(1000);

				if (!isDownTrend2())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
			}

			int spreadingTimes = 0;
			double refDiff = 0;

			while (spreadingTimes < 3)
			{
				sleep(1000);

				if (!isDownTrend2() || GetData.getEma5().getEMA() > GetData.getEma50().getEMA())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (getMADiff() < refDiff){
					refDiff = getMADiff();
					spreadingTimes++;
//					Global.addLog("Spreading time: "  + spreadingTimes);
				}else if (getMADiff() > refDiff){
					refDiff = getMADiff();
					spreadingTimes--;
					if (spreadingTimes < 0)
						return;
				}
				
//				if (Math.abs(buyingPoint - GetData.getEma50().getEMA()) > 50){
//					Global.addLog("Risk too high");
//					return;
//				}
			}
			
//			if (GetData.getEma100().getEMA() - GetData.getEma50().getEMA() < 10)
//				return;
		
			shortContract();
			cutLossPt = Math.abs(buyingPoint - GetData.getEma50().getEMA());
//			Global.addLog("EMA5: " + getTimeBase().getEMA(5));
//			Global.addLog("EMA10: " + getTimeBase().getEMA(10));
//			Global.addLog("EMA50: " + getTimeBase().getEMA(50));
//			Global.addLog("EMA240: " + getTimeBase().getEMA(240));
////			Global.addLog("Before High: " + refPt);
//			Global.addLog("CutLossPt: " + cutLossPt);

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

	@Override
	boolean trendReversed(){
		
		if (Global.getNoOfContracts() > 0)
			return GetData.getEma5().getEMA() < GetData.getEma50().getEMA();
		else
			return GetData.getEma5().getEMA() > GetData.getEma50().getEMA();
		
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

		
//		return Math.min(cutLossPt + 10, 40);
		return 40;

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