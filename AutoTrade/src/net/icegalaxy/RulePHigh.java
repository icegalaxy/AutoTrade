package net.icegalaxy;

public class RulePHigh extends Rules {

//	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;
	double ohlc;

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
		
		
		// Reset the lossCount at afternoon because P.High P.Low is so important
		if (isAfternoonTime() && !tradeTimesReseted) {
			lossTimes = 0;
			tradeTimesReseted = true;
		}

		if (!isOrderTime() || lossTimes >= 3 || Global.getNoOfContracts() != 0
				|| Global.getpHigh() == 0)
			return;

		ohlc = Global.getpHigh();
		
		//use the openOHLC but do not use danny trend	
		if (Global.getCurrentPoint() <= ohlc + 5
				&& Global.getCurrentPoint() >= ohlc - 5) {

			Global.addLog(className + ": Entered waiting zone");

			waitForANewCandle();

			while (getTimeBase().getLatestCandle().getClose() <= ohlc + 10
					&& getTimeBase().getLatestCandle().getClose()  >= ohlc - 10)
				sleep(1000);

			if (getTimeBase().getLatestCandle().getClose()  > ohlc + 10) {
				longContract();
			} else if (getTimeBase().getLatestCandle().getClose() < ohlc - 10) {		//cause if big drop trend
				shortContract();
			}
		}
		
//		openOHLC(Global.getpHigh());
	}

	void updateStopEarn() {

//		if (getProfit() < 30)
//			super.updateStopEarn();
//		else	
			thirdStopEarn();

	}

	double getCutLossPt() {
		return Math.abs(buyingPoint - Global.getpHigh());
	}

	double getStopEarnPt() {
		return Math.abs(buyingPoint - Global.getpHigh()) * 1.5;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getLongTB();
	}

}