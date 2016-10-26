package net.icegalaxy;

public class RuleRSI extends Rules {
	
	private int lossTimes;
	// private double refEMA;
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
			
			if (tempCutLoss < Global.getCurrentPoint() - 30){
				tempCutLoss = Global.getCurrentPoint() - 30;
			}

			if (buyingPoint > tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) && getProfit() > 50)
				tempCutLoss = 99999;
			
		} else if (Global.getNoOfContracts() < 0) {
			
			if (tempCutLoss > Global.getCurrentPoint() + 30){
				tempCutLoss = Global.getCurrentPoint() + 30;
			}

			if (buyingPoint < tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) && getProfit() > 50)
				tempCutLoss = 0;

		}

	}

	// use 1min instead of 5min
	double getCutLossPt() {

		if (cutLoss < 10)
			return 10;
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


	double getStopEarnPt() {
		
		if (Global.getNoOfContracts() > 0 && getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
			return -100;
		else 	if (Global.getNoOfContracts() < 0 && getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
			return -100;
		
		//有可能行夠50點都未 5 > 6，咁會即刻食左
		return  30;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getLongTB();
	}

}
