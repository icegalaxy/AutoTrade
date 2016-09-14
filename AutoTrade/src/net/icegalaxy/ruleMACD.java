package net.icegalaxy;


public class ruleMACD extends Rules {

	private double fluctuation = 100;
	
	public ruleMACD(boolean globalRunRule) {
		super(globalRunRule);
	}
	public void openContract() {

		if (getTimeBase().getMACDHistogram() < -1) {

			while (getTimeBase().getMACDHistogram() < 1 && Global.isOrderTime()){
//				System.out.println("AGAL: " + getAGAL());
				sleep(1000);
			}

	//		sleep(1000);

			if (!Global.isOrderTime()
					|| getAGAL() < 6.5
					|| Global.getCurrentPoint() < GetData.getShortTB().getMA(20) 
	//				|| !getTimeBase().isMARising(20)
					|| GetData.getShortTB().getRSI() > 70				
					|| !getTimeBase().isQuantityRising()
	//				|| !shouldRise()
	//				|| getBalance() < 0
					|| GetData.getShortTB().getHL(60).getFluctuation() < fluctuation
					)
				return;

			
			longContract();

		} else if (getTimeBase().getMACDHistogram() > 1) {

			while (getTimeBase().getMACDHistogram() > -1 && Global.isOrderTime()){
//				System.out.println("AGAL: " + getAGAL());
				sleep(1000);
			}
	//		sleep(1000);

			if (!Global.isOrderTime()
					|| getAGAL() < 6.5
					|| Global.getCurrentPoint() > GetData.getShortTB().getMA(20)  
		//			|| !getTimeBase().isMADropping(20)
					|| GetData.getShortTB().getRSI() < 30					
					|| !getTimeBase().isQuantityRising()
		//			|| !shouldDrop()
		//			|| getBalance() < 0
					|| GetData.getShortTB().getHL(60).getFluctuation() < fluctuation
					)
				return;
		
			shortContract();

		}

	}

	double getCutLossPt() {
		return Global.getCutLost();
	}

	double getStopEarnPt() {
		return Global.getGreatProfit();
	}
	
	@Override
	void updateStopEarn() {

		if (Global.getNoOfContracts() > 0) {
			
			if (getTimeBase().getRSI() > 70){
				usingMA20 = false;
				usingMA10 = false;
//				usingMA5 = false;
			}
			

			if (getTimeBase().getMA(20) > tempCutLoss && usingMA20) {
				if (getTimeBase().getMA(10) > tempCutLoss)
					tempCutLoss = getTimeBase().getMA(20);

			} else if (getTimeBase().getMA(10) > tempCutLoss && usingMA10) {
				if (getTimeBase().getMA(10) > tempCutLoss) {
					tempCutLoss = getTimeBase().getMA(10);
					usingMA20 = false;
				}

			} else if (getTimeBase().getMA(5) > tempCutLoss && usingMA5) {
				if (getTimeBase().getMA(10) > tempCutLoss) {
					tempCutLoss = getTimeBase().getMA(5);
					usingMA20 = false;
					usingMA10 = false;
				}

			} else if (getTimeBase().getMA(1) > tempCutLoss) {
				tempCutLoss = getTimeBase().getMA(1);
				usingMA20 = false;
				usingMA10 = false;
				usingMA5 = false;
			}

		} else if (Global.getNoOfContracts() < 0) {
			
			if (getTimeBase().getRSI() < 30){
				usingMA20 = false;
				usingMA10 = false;
//				usingMA5 = false;
			}

			if (getTimeBase().getMA(20) < tempCutLoss && usingMA20) {
				if (getTimeBase().getMA(10) < tempCutLoss)
					tempCutLoss = getTimeBase().getMA(20);

			} else if (getTimeBase().getMA(10) < tempCutLoss && usingMA10) {
				if (getTimeBase().getMA(10) < tempCutLoss) {
					tempCutLoss = getTimeBase().getMA(10);
					usingMA20 = false;
				}
			} else if (getTimeBase().getMA(5) < tempCutLoss && usingMA5) {
				if (getTimeBase().getMA(10) < tempCutLoss) {
					tempCutLoss = getTimeBase().getMA(5);
					usingMA20 = false;
					usingMA10 = false;
				}

			} else if (getTimeBase().getMA(1) < tempCutLoss){
				tempCutLoss = getTimeBase().getMA(1);
					usingMA20 = false;
					usingMA10 = false;
					usingMA5 = false;
				}
		}

	}
	
	@Override
	boolean trendReversed() {
		return false;
	}



	@Override
	public TimeBase getTimeBase() {		
		return GetData.getLongTB();
	}
	
}