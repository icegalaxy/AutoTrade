package net.icegalaxy;



//Use the OPEN Line

public class RuleDanny250Pena extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private Chasing chasing;

	public RuleDanny250Pena(boolean globalRunRule)
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
	
	double getClosePrice(){
		return GetData.getShortTB().getLatestCandle().getClose();
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

		if (getClosePrice() < GetData.getEma250().getEMA())
		{
			while (GetData.getEma5().getEMA() < GetData.getEma250().getEMA())
				sleep(1000);
		
			int spreadingTimes = 0;
			double refDiff = 0;	
			refPt = Global.getCurrentPoint();
			
			while (spreadingTimes < 3)
			{
				sleep(1000);	
				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (getClosePrice() < GetData.getEma250().getEMA())
					return;
										
				
				if (getMADiff() > refDiff){
					refDiff = getMADiff();
					spreadingTimes++;
				}else if (getMADiff() < refDiff){
					refDiff = getMADiff();
					spreadingTimes--;
				}
			}
			
			if (GetData.getEma5().getEMA() < GetData.getEma25().getEMA()
					|| Global.getCurrentPoint() < refPt)
				return;
			
			longContract();
			cutLossPt = buyingPoint - GetData.getEma250().getEMA();
			
			
		}else if (GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
		{
			while (GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
				sleep(1000);
		
			int spreadingTimes = 0;
			double refDiff = 0;	
			refPt = Global.getCurrentPoint();
			
			while (spreadingTimes < 3)
			{
				sleep(1000);	
				if ( !isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (getClosePrice() > GetData.getEma250().getEMA())
					return;
										
				
				if (getMADiff() < refDiff){
					refDiff = getMADiff();
					spreadingTimes++;
				}else if (getMADiff() > refDiff){
					refDiff = getMADiff();
					spreadingTimes--;
				}
			}
			if (GetData.getEma5().getEMA() > GetData.getEma25().getEMA()
					|| Global.getCurrentPoint() > refPt)
				return;
			shortContract();
			cutLossPt = GetData.getEma250().getEMA() - buyingPoint;
		}
		
	

	}

	@Override
	boolean trendReversed(){
		
		if (Global.getNoOfContracts() > 0)
			return GetData.getEma5().getEMA() < GetData.getEma250().getEMA();
		else
			return GetData.getEma5().getEMA() > GetData.getEma250().getEMA();
		
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = GetData.getEma5().getEMA();
		ema6 = GetData.getEma50().getEMA();


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