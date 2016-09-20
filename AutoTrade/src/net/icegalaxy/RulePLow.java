package net.icegalaxy;


public class RulePLow extends Rules {
	
	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;
private double ohlc;

	public RulePLow(boolean globalRunRule) {
		super(globalRunRule);
//		setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		
	}

	public void openContract() {
		
		if (shutdown){
			lossTimes++;
			shutdown = false;
		}
		
		// Reset the lossCount at afternoon because P.High P.Low is so important
		if (isAfternoonTime() && !tradeTimesReseted) {
			lossTimes = 0;
			tradeTimesReseted = true;
		}

		if(!isOrderTime()
				|| lossTimes  >= 3
				|| Global.getNoOfContracts() != 0
				|| Global.getpLow() == 0)
			return;
		
		ohlc = Global.getpLow();
		
		//use the openOHLC but do not use danny trend	
		if (Global.getCurrentPoint() <= ohlc + 5
				&& Global.getCurrentPoint() >= ohlc - 5) {

			Global.addLog(className + ": Entered waiting zone");
			Global.addLog("MA20(M15): " + GetData.getM15TB().getMA(20)
					+ "; EMA50(M15): " + GetData.getM15TB().getEMA(50)
					+ "; EMA50(M5): " + GetData.getLongTB().getEMA(50)
					+ "; EMA240(M5): " + GetData.getLongTB().getEMA(240));

			while (Global.getCurrentPoint() <= ohlc + 10
					&& Global.getCurrentPoint() >= ohlc - 10)
				sleep(1000);

			if (Global.getCurrentPoint() > ohlc + 10) {
				longContract();
			} else if (Global.getCurrentPoint() < ohlc - 10) {		//cause if big drop trend
				shortContract();
			}
		}
		
//		openOHLC(Global.getpLow());
		

	}
	void updateStopEarn() {

		if (getProfit() < 30)
			super.updateStopEarn();
		else
			thirdStopEarn();

	}	
	double getCutLossPt(){
		return 10;
	}
	
	double getStopEarnPt(){
		return 15;
	}
	
	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getLongTB();
	}
}