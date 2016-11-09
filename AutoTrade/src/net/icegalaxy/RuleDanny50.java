package net.icegalaxy;



public class RuleDanny50 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;
	private Chasing chasing;


	public RuleDanny50(boolean globalRunRule) {
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91600, 113000, 130500, 160000, 231500, 231500);
		chasing = new Chasing();
	}

	private double getRefPt(){
		return getTimeBase().getEMA(50);
	}

	public void openContract()
	{
		
//		if (chasing.chaseUp() || chasing.chaseDown()){
//			
//			Global.setChasing(chasing);
//			chasing = new Chasing();
//		}

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= getLossTimesAllowed())
			return;
//		
		//in case it is a small range
		while (Math.abs(Global.getCurrentPoint() - getRefPt()) < 50)
			sleep(1000);

		while (Math.abs(Global.getCurrentPoint() - getRefPt()) > 10)
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

			Global.addLog("EMA5: " + GetData.getShortTB().getEMA(5));
			Global.addLog("MA20: " + GetData.getShortTB().getMA(20));

			while (GetData.getShortTB().getEMA(5) < GetData.getShortTB().getMA(20))
			{
				sleep(1000);


				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();
				
				if (!isUpTrend())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if (Global.getCurrentPoint() < getRefPt() - 30)
				{
					Global.addLog("Penatraded");
					shutdown = true;
					return;
				}
				
				if (Global.getCurrentPoint() > refPt + 50)
				{
					Global.addLog("Risk too high");
					return;
				}


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

			Global.addLog("EMA5: " + GetData.getShortTB().getEMA(5));
			Global.addLog("EMA20: " + GetData.getShortTB().getMA(20));

			while (GetData.getShortTB().getEMA(5) > GetData.getShortTB().getMA(20))
			{

				sleep(1000);

			
				
				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

				if (!isDownTrend())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if (Global.getCurrentPoint() < refPt - 50)
				{
					Global.addLog("Risk too high");
					return;
				}

				
				if (Global.getCurrentPoint() > getRefPt() + 30)
				{
					Global.addLog("Penatraded");
					shutdown = true;
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
		

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = GetData.getShortTB().getLatestCandle().getClose();
		ema6 = getTimeBase().getEMA(5);
		// }else{
		// ema5 = GetData.getShortTB().getEMA(5);
		// ema6 = GetData.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

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

		if (cutLossPt < 15)
			return 15;
		else
			return cutLossPt;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && GetData.getShortTB().getLatestCandle().getClose() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && GetData.getShortTB().getLatestCandle().getClose() > tempCutLoss)
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