package net.icegalaxy;

public class RuleSilvia extends Rules {

	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;

	public RuleSilvia(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(94500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
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

		if (!isOrderTime() || lossTimes >= 2 || Global.getNoOfContracts() != 0)
			return;

		if(isUpTrend()){
			
			if (Global.getCurrentPoint() <= GetData.getM15TB().getHL(1).getTempLow() + 5){
						
				Global.addLog("Entered waiting zone");
			
				while (Global.getCurrentPoint() < GetData.getM15TB().getHL(1).getTempLow() + 10)
					sleep(1000);
				
				if(!isUpTrend()){
					Global.addLog("Is not upTrend anymore");
					return;
				}
				
				longContract();				
			}	
		}else if (isDownTrend()){
			
			if (Global.getCurrentPoint() >= GetData.getM15TB().getHL(1).getTempHigh() - 5){
				
				Global.addLog("Entered waiting zone");
			
				while (Global.getCurrentPoint() > GetData.getM15TB().getHL(1).getTempHigh() - 10)
					sleep(1000);
				
				if(!isUpTrend()){
					Global.addLog("Is not downTrend anymore");
					return;
				}
				
				shortContract();		
			}
		}
	}

	void updateStopEarn() {

		if (getProfit() < 30 || getTimeBase().getEMA(5) == -1)
			super.updateStopEarn();
		else if (getProfit() <50)
			thirdStopEarn();secondStopEarn();

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