package net.icegalaxy;

public class RuleNoonOpen extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleNoonOpen(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(120000, 120000, 131500, 160000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || lossTimes >= 2 || Global.getNoOfContracts() != 0 || Global.getNoonOpen() == 0)
			return;

//		if (isInsideDay())
//			reverseOHLC(Global.getNoonOpen());
//		else
			openOHLC(Global.getNoonOpen());
	}

	void updateStopEarn() {

//		if (getProfit() < 30)
//			super.updateStopEarn();
//		else
			thirdStopEarn();

	}

	double getCutLossPt() {
		return Math.abs(buyingPoint - Global.getNoonOpen());
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