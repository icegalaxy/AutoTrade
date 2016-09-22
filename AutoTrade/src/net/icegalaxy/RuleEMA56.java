package net.icegalaxy;

public class RuleEMA56 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleEMA56(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(92000, 113000, 130500, 160000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || lossTimes >= 3 || Global.getNoOfContracts() != 0)
			return;

		if(!isInsideDay()){
			if(getTimeBase().getEMA(5) >= getTimeBase().getEMA(6))
				longContract();
			else
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