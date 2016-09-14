package net.icegalaxy;

public class RuleOpen extends Rules {

	private int lossTimes = 0;
//	private double refEMA;

	public RuleOpen(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(91500, 110000, 140000, 153000);
		
	}

	public void openContract() {
		
//		if (Global.getOpen() == 0){									//Not setting open manually because this is faster, want to catch the first wave
//			Global.setOpen(Global.getpClose() + Global.getGap()); //If gap == 0 then it will fail
//			Global.addLog("Open @ " + Global.getOpen());
//		}

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() 
				|| lossTimes >= 2 
				|| Global.getNoOfContracts() != 0
				|| Global.getOpen() == 0
//				|| Global.getCurrentPoint() > Global.getPreviousHigh() - 30
//				|| Global.getCurrentPoint() < Global.getPreviousLow() + 30
				)
			return;

		openOHLC(Global.getOpen());
	}

	double getCutLossPt() {
		return 10;
	}

	double getStopEarnPt() {
		return 15;
	}

	void updateStopEarn() {

		if (getProfit() < 30 || getTimeBase().getEMA(5) == -1)
			super.updateStopEarn();
		else
			thirdStopEarn();
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getLongTB();
	}
}