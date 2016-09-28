package net.icegalaxy;

public class RuleEMA56 extends Rules {

//	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleEMA56(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(92000, 113000, 130500, 160000, 233000, 233000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		// Reset the lossCount at afternoon because P.High P.Low is so important
				if (isAfternoonTime() && !tradeTimesReseted) {
					lossTimes = 0;
					tradeTimesReseted = true;
				}
		

		if (!isOrderTime() 
				|| Global.getNoOfContracts() != 0
				|| Global.getpHigh() == 0)
			return;

		if(!isInsideDay()){
			if(getTimeBase().getEMA(5) > getTimeBase().getEMA(6)
					&& GetData.getShortTB().getEMA(5) > GetData.getShortTB().getEMA(6))
				longContract();
			else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)
					&& GetData.getShortTB().getEMA(5) < GetData.getShortTB().getEMA(6))
				shortContract();
		}

		
	}

	void updateStopEarn() {

		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	double getCutLossPt() {
		return 100;
	}

	double getStopEarnPt() {
		return -100;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		
		// switching between 1 & 5 minutes
//		if (lossTimes % 2 ==1)
//			return GetData.getShortTB();
//		else
			return GetData.getLongTB();
	}

}