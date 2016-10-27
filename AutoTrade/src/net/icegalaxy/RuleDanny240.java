package net.icegalaxy;

public class RuleDanny240 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleDanny240(boolean globalRunRule) {
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(93000, 113000, 130500, 160000, 172000, 231500);
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				|| lossTimes >= getLossTimesAllowed())
			return;

		if (isUpTrend()
				&& Global.getCurrentPoint() > getTimeBase().getEMA(240) + 5 + lossTimes * 5){
			
			while (Global.getCurrentPoint() > getTimeBase().getEMA(240) + 5 + lossTimes)
				sleep(1000);
			
			while (Global.getCurrentPoint() < getTimeBase().getEMA(240) + 5 + lossTimes)
				sleep(1000);
			
			longContract();
			
		}else if (isDownTrend()
				&& Global.getCurrentPoint() < getTimeBase().getEMA(240) - 5 - lossTimes * 5){
			
			while (Global.getCurrentPoint() < getTimeBase().getEMA(240) - 5 - lossTimes)
				sleep(1000);
			
			while (Global.getCurrentPoint() > getTimeBase().getEMA(240) - 5 - lossTimes)
				sleep(1000);
			
			shortContract();
			
		}
		
		
		
		
	}

	private int getLossTimesAllowed(){
		
		double balance = Global.balance + Global.getCurrentPoint() * Global.getNoOfContracts();
		
		
		
		if  (balance > 15)
			return 2;
		else
			return 1;
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
				tempCutLoss = buyingPoint + 5;
			}
			
			
			if (ema5 < ema6) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			
			if (buyingPoint < tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
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

		return 15;

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
			if (getTimeBase().getEMA(5) >  getTimeBase().getEMA(6)
					&& getProfit() > 30)
				return -100;
		}else if (Global.getNoOfContracts() < 0){
			if (getTimeBase().getEMA(5) <  getTimeBase().getEMA(6)
					&& getProfit() > 30)
				return -100;
		}
		
		return 30;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getLongTB();
	}

}