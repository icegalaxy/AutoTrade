package net.icegalaxy;



//Use the OPEN Line

public class RuleDanny250Pena4 extends Rules
{


	private double cutLoss;
	private double OHLC;
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
		
		refHigh = 0;
		refLow = 99999;


		
		if (!isOrderTime() || Global.getNoOfContracts() != 0 || GetData.getLongTB().getEma5().getEMA() == 0 || shutdown
				|| Global.balance < -30)
			return;
		


		if (GetData.getLongTB().getEma5().getPreviousEMA(1) < GetData.getLongTB().getEma250().getPreviousEMA(1)
				&& GetData.getLongTB().getEma5().getEMA() > GetData.getLongTB().getEma250().getEMA())
		{
		
			
			longContract();
			refLow = buyingPoint;
			cutLoss = buyingPoint - refLow;
			
		}else if (GetData.getLongTB().getEma5().getPreviousEMA(1) > GetData.getLongTB().getEma250().getPreviousEMA(1)
				&& GetData.getLongTB().getEma5().getEMA() < GetData.getLongTB().getEma250().getEMA())
		{	
			
			
			shortContract();	
			refHigh = buyingPoint;
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
	double getCutLossPt()
	{
		return Math.max(50, cutLoss);
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
		double adjustPt = 0;
		
		if (Global.getNoOfContracts() > 0)
		{
			
			adjustPt = buyingPoint - refLow;
			
		} else if (Global.getNoOfContracts() < 0)
		{
			adjustPt = refHigh - buyingPoint;
		}
		double pt;
		
		pt = (160000 - TimePeriodDecider.getTime()) / 1000;
		
		if (trendReversed)
		{
			shutdown = true;
//			return 5;
			return Math.min(5, pt - adjustPt);		
		}
		else if (pt < 20)
			return 20 - adjustPt;
		else return pt - adjustPt;
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