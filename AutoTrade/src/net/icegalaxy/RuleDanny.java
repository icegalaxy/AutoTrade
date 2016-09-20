package net.icegalaxy;

public class RuleDanny extends Rules {

	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;

	public RuleDanny(boolean globalRunRule) {
		super(globalRunRule);
//		setOrderTime(91500, 110000, 133000, 160000);
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

		if (!isOrderTime() || lossTimes >= 3 || Global.getNoOfContracts() != 0)
			return;

		openOHLC(GetData.getLongTB().getEMA(240));
	}

	void updateStopEarn() {

		if (getProfit() < 30 || getTimeBase().getEMA(5) == -1)
			super.updateStopEarn();
		else
			thirdStopEarn();

	}

	double getCutLossPt() {
		return 10;
	}

	double getStopEarnPt() {
		return 15;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getLongTB();
	}

}