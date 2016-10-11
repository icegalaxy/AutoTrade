package net.icegalaxy;

public class RulePHigh extends Rules {

//	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;
	double ohlc;
	private double cutLoss;

	public RulePHigh(boolean globalRunRule) {
		super(globalRunRule);
//		setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				)
			return;

		if (isUpTrend()
				&& Global.getCurrentPoint() > Global.getpHigh() + 5
				&& getTimeBase().getRSI() > 30){
			
			
			// pull back
			while (Global.getCurrentPoint() > Global.getpHigh() + 5){
				sleep(1000);
			}
			
			//second corner
			refPt = Global.getCurrentPoint();
			while (Global.getCurrentPoint() < Global.getpHigh() + 5
					|| Global.getCurrentPoint() < refPt + 10){
				sleep(1000);
				
				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();
			}
			
			longContract();
			cutLoss = Math.abs(buyingPoint - refPt);
			
		}else if (isDownTrend()
				&& Global.getCurrentPoint() < Global.getpHigh() - 5
				&& getTimeBase().getRSI() < 70){
			
			while (Global.getCurrentPoint() < Global.getpHigh() - 5)
				sleep(1000);
			
			//second corner
			refPt = Global.getCurrentPoint();
			while (Global.getCurrentPoint() > Global.getpHigh() - 5
					|| Global.getCurrentPoint() > refPt - 10){
				sleep(1000);

				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();
			}
			
			shortContract();
			cutLoss = Math.abs(buyingPoint - refPt);
		}
		
		
		
		
	}

	

	// use 1min instead of 5min
	void updateStopEarn() {

		double ema5;
		double ema6;
		int difference;

		if (getProfit() > 100)
			difference = 0;
		else
			difference = 2;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = getTimeBase().getEMA(5);
		ema6 = getTimeBase().getEMA(6);
		// }else{
		// ema5 = GetData.getShortTB().getEMA(5);
		// ema6 = GetData.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0) {
			
			if (buyingPoint > tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			
			if (ema5 < ema6) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			
			if (buyingPoint < tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			
			if (ema5 > ema6) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt() {

		// One time lost 100 at first trade >_< 20160929
		// if (Global.getNoOfContracts() > 0){
		// if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }else{
		// if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }

		return cutLoss;

	}

	@Override
	protected void cutLoss() {

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss) {
			//
			// while (Global.getCurrentPoint() <
			// GetData.getShortTB().getEMA(5)){
			// sleep(1000);
			// if (getProfit() < -30)
			// break;
			// }
			//

			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;

			// wait for it to clam down

			// if (Global.getCurrentPoint() < getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }

			// while (Global.getCurrentPoint() < getTimeBase().getEMA(6))
			// sleep(1000);

		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss) {
			//
			//
			// while (Global.getCurrentPoint() >
			// GetData.getShortTB().getEMA(5)){
			// sleep(1000);
			// if (getProfit() < -30)
			// break;
			// }

			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

			// if (Global.getCurrentPoint() > getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }
			//
			// while (Global.getCurrentPoint() > getTimeBase().getEMA(6))
			// sleep(1000);
		}
	}

	double getStopEarnPt() {
		if (Global.getNoOfContracts() > 0){
			if (getTimeBase().getEMA(5) >  getTimeBase().getEMA(6))
				return -100;
		}else if (Global.getNoOfContracts() < 0){
			if (getTimeBase().getEMA(5) <  getTimeBase().getEMA(6))
				return -100;
		}
		
		return 100;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getLongTB();
	}

}