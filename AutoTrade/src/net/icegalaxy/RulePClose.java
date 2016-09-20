package net.icegalaxy;

public class RulePClose extends Rules {

	private int lossTimes;

	public RulePClose(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(92000, 113000, 130500, 160000);

	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || lossTimes >= 2 || Global.getNoOfContracts() != 0
				|| Global.getpClose() == 0)
			return;

//		if (isInsideDay())
//			reverseOHLC(Global.getpClose());
//		else
			openOHLC(Global.getpClose());
	}

	void updateStopEarn() {

		if (getProfit() < 30 || getTimeBase().getEMA(5) == -1)
			super.updateStopEarn();
		else
			thirdStopEarn();

//		if (getProfit() < 40 || getTimeBase().getEMA(6) == -1)
//			super.updateStopEarn();
//		else {
//			if (Global.getNoOfContracts() > 0) {
//				if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {
//					tempCutLoss = 99999;
//					Global.addLog(className + ": StopEarn by EMA");
//				}
//
//				if (Global.getOpen() < Global.getpHigh()
//						&& Global.getCurrentPoint() >= Global.getpHigh() - 2) {
//					tempCutLoss = 99999;
//					Global.addLog(className + ": StopEarn by p.High");
//				}
//
//				if (Global.getOpen() < Global.getpLow()
//						&& Global.getCurrentPoint() >= Global.getpLow() - 2) {
//					tempCutLoss = 99999;
//					Global.addLog(className + ": StopEarn by p.Low");
//				}
//
//				// refEMA = GetData.getLongTB().getEMA(5);
//
//			} else if (Global.getNoOfContracts() < 0
//					|| Global.getCurrentPoint() <= Global.getAOL() + 2) {
//				if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
//					tempCutLoss = 0;
//					Global.addLog(className + ": StopEarn by EMA");
//				}
//
//				if (Global.getOpen() > Global.getpLow()
//						&& Global.getCurrentPoint() <= Global.getpLow() + 2) {
//					tempCutLoss = 0;
//					Global.addLog(className + ": StopEarn by p.Low");
//				}
//
//				if (Global.getOpen() > Global.getpHigh()
//						&& Global.getCurrentPoint() <= Global.getpHigh() + 2) {
//					tempCutLoss = 0;
//					Global.addLog(className + ": StopEarn by p.High");
//				}
//
//				// refEMA = GetData.getLongTB().getEMA(5);
//			}
//		}

	}

	double getCutLossPt() {
		return Math.abs(buyingPoint - Global.getpClose());
	}

	double getStopEarnPt() {
		return Math.abs(buyingPoint - Global.getpClose()) * 1.5;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getShortTB();
	}

}
