package net.icegalaxy;

public class RuleEMA56 extends Rules {

//	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleEMA56(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(92000, 113000, 130500, 160000, 233000, 233000);
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
		

		if (!isOrderTime() 
				|| Global.getNoOfContracts() != 0
				|| Global.getpHigh() == 0)
			return;

		//use 1min TB will have more profit sometime, but will lose so many times when ranging.
		
			if(getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2 + lossTimes){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				
				while(Global.getCurrentPoint() > getTimeBase().getEMA(6)){
					sleep(1000);
					
					if(getTimeBase().getEMA(5) < getTimeBase().getEMA(6)){
						Global.addLog(className + ": wrong trend");
						return;
					}
				}
							
				longContract();
			}
			else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 2 - lossTimes){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				
				while(Global.getCurrentPoint() < getTimeBase().getEMA(6)){
					sleep(1000);
					
					if(getTimeBase().getEMA(5) > getTimeBase().getEMA(6)){
						Global.addLog(className + ": wrong trend");
						return;
					}
					
				}
				
				
				
				shortContract();
			}
		
	}

	//use 1min instead of 5min
	void updateStopEarn() {

		//use 1min TB will have more profit sometime, but will lose so many times when ranging.
		
		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}
	
	//use 1min instead of 5min
	double getCutLossPt() {
		
		//One time lost 100 at first trade >_< 20160929
//		if (Global.getNoOfContracts() > 0){
//			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
//				return 1;
//			else
//				return 30;
//		}else{
//			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
//				return 1;
//			else
//				return 30;
//		}
		
		return 50;

	}
	
	@Override
	protected void cutLoss() {
		
		double ref = Global.getCurrentPoint();

		if (Global.getNoOfContracts() > 0 && ref < tempCutLoss) {
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
			
			//wait for it to clam down
			
			if (refPt < getTimeBase().getEMA(6)){
				Global.addLog(className + ": waiting for it to calm down");
			}
			
			while (refPt < getTimeBase().getEMA(6))
				sleep(1000);
			
		} else if (Global.getNoOfContracts() < 0 && ref  > tempCutLoss) {
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
			
			if (refPt > getTimeBase().getEMA(6)){
				Global.addLog(className + ": waiting for it to calm down");
			}
			
			while (refPt > getTimeBase().getEMA(6))
				sleep(1000);
		}
	}

	double getStopEarnPt() {
		return -100;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		
		// switching between 1 & 5 minutes
//		if (lossTimes % 2 ==1)
//			return GetData.getShortTB();
//		else
			return GetData.getLongTB();
	}

}