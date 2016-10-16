package net.icegalaxy;



public class RuleRSI extends Rules {
	
	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = false;
	private double cutLoss;
	
	public RuleRSI(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(93000, 113000, 130500, 160000, 230000, 230000);
	}
	
	@Override
	boolean trendReversed(){
		return false;
	}

	
	public void openContract() {

		if (shutdown) {
			lossTimes++;
			firstCorner = true;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				|| lossTimes >=2
		)
			return;
		
		if (isUpTrend() && GetData.getShortTB().getRSI() < 30 - lossTimes * 10)
		{
			
			refPt = Global.getCurrentPoint();
			while (GetData.getShortTB().getLatestCandle().getClose() < GetData.getShortTB().getPreviousCandle(1).getClose())
				{
					sleep(1000);
					if (Global.getCurrentPoint() < refPt)
						refPt = Global.getCurrentPoint();
				}
			
			
			longContract();
			
			cutLoss = buyingPoint - refPt;
		}
		else if  (isDownTrend() && GetData.getShortTB().getRSI() > 70 + lossTimes * 10)
		{
			
			while (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getPreviousCandle(1).getClose())
				{
					sleep(1000);
					
					if (Global.getCurrentPoint() > refPt)
						refPt = Global.getCurrentPoint();
				}
			
			shortContract();
			cutLoss = refPt - buyingPoint;
		}
	
	}
		
//		openOHLC(Global.getpHigh());


	// use 1min instead of 5min
	void updateStopEarn() {



		if (Global.getNoOfContracts() > 0) {

			if (buyingPoint > tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				tempCutLoss = 99999;
			
		} else if (Global.getNoOfContracts() < 0) {

			if (buyingPoint < tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				tempCutLoss = 0;

		}

	}

	// use 1min instead of 5min
	double getCutLossPt() {

		if (cutLoss < 5)
			return 5;
		else
			return cutLoss;

	}

	@Override
	protected void cutLoss() {

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss) {
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss) {
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

		}
	}

	private void firstCorner() {

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
			// wait for a better position
			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				sleep(1000);

			firstCorner = false;

		} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {

			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				sleep(1000);

			firstCorner = false;
		}
	}

	double getStopEarnPt() {
		
		if (Global.getNoOfContracts() > 0 && getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
			return -100;
		else 	if (Global.getNoOfContracts() < 0 && getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
			return -100;
		
		//有可能行夠50點都未 5 > 6，咁會即刻食左
		return  50;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getLongTB();
	}

}
