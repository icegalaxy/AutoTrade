package net.icegalaxy;

public class RuleSilvia extends Rules {

	private int lossTimes;
//	private double refEMA;
	private boolean tradeTimesReseted;

	public RuleSilvia(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(94500, 113000, 130500, 160000);
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

		//used 1hr instead of 15min
		
		if(isUpTrend()){
			
			if (Global.getCurrentPoint() <= GetData.getM15TB().getHL(4).getTempLow() + 5){
						
				Global.addLog(className + ": Entered waiting zone");
			
				while (Global.getCurrentPoint() < GetData.getM15TB().getHL(4).getTempLow() + 10)
					sleep(1000);
				
				if(!isUpTrend()){
					Global.addLog(className + ": Is not upTrend anymore");
					return;
				}
				
				//don't want to trade at Day H/L
				if(GetData.getM15TB().getHL(4).getTempLow() == Global.getDayLow()){
					Global.addLog(className + ": Is DayLow");
					return;
				}
				
				longContract();				
			}	
		}else if (isDownTrend()){
			
			if (Global.getCurrentPoint() >= GetData.getM15TB().getHL(4).getTempHigh() - 5){
				
				Global.addLog(className + ": Entered waiting zone");
			
				while (Global.getCurrentPoint() > GetData.getM15TB().getHL(4).getTempHigh() - 10)
					sleep(1000);
				
				if(!isDownTrend()){
					Global.addLog(className + ": Is not downTrend anymore");
					return;
				}
				
				//don't want to trade at Day H/L
				if(GetData.getM15TB().getHL(4).getTempHigh() == Global.getDayHigh()){
					Global.addLog(className + ": Is DayHigh");
					return;
				}
				
				shortContract();		
			}
		}
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