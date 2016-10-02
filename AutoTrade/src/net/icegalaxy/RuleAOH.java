package net.icegalaxy;

public class RuleAOH extends Rules {

	private int lossTimes;
//	private double refEMA;
//	private boolean tradeTimesReseted;

	public RuleAOH(boolean globalRunRule) {
		super(globalRunRule);
//		setOrderTime(91500, 110000, 140000, 160000, 230000, 230000);

	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		
		// Reset the lossCount at afternoon because P.High P.Low is so important
//		if (isAfternoonTime() && !tradeTimesReseted) {
//			lossTimes = 0;
//			tradeTimesReseted = true;
//		}

		if (!isOrderTime() || lossTimes >= 2 || Global.getNoOfContracts() != 0
				|| Global.getAOH() == 0)
			return;

//		if (isInsideDay())
//			reverseOHLC(Global.getAOH());
//		else
			openOHLC(Global.getAOH());
	}

	void updateStopEarn() {

//		if (getProfit() < 30)
//			super.updateStopEarn();
//		else
			thirdStopEarn();
		

	}

	double getCutLossPt() {
		return Math.abs(buyingPoint - Global.getAOH());
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