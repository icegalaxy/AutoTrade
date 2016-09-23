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
			if(getTimeBase().getEMA(5) >= getTimeBase().getEMA(6) + lossTimes)
				longContract();
			else if (getTimeBase().getEMA(5) <= getTimeBase().getEMA(6) - lossTimes)
				shortContract();
		}

		
	}

	void updateStopEarn() {

			thirdStopEarn();

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
		return GetData.getLongTB();
	}

}