package net.icegalaxy;




// ¨ä¹ê«YTest 3

public class RuleRSI2 extends Rules {
	
	private double fluctuation = 100;
	private double smallFluctuation = 50;
	private double minFluctuation = 20;
	private double buffer = 10;
	protected double referencePoint;
	
	private double lowestRSI = 100;
	private double highestRSI = 0;
	
	private double stopEarnPt = 200;

	double refHLPoint;
	boolean lowFluc = true;
	boolean shutDownUp;
	boolean shutDownDown;
	double highestPt = 0;
	double lowestPt = 99999;
	boolean shortContract;
	boolean longContract;
	boolean shutdownthis;

	boolean reachLittleProfit;

	private int shutDownCount = 0;

	public RuleRSI2(boolean globalRunRule) {
		super(globalRunRule);
	
	}

	@Override
	public void openContract() {
		
//		checkLowFluc();

		if (!Global.isOrderTime()
		// || shutdownthis
//				|| isClose()
//				|| shutdown
//				|| !lowFluc
//				|| Global.isSidewayMrt
				)
			return;
		

		if(getTimeBase().getRSI() > 70){
//			lowestRSI = 100;
//			highestRSI = 0;
			shortContract();			
		}
//		else if(getTimeBase().getRSI() < 30){
//			lowestRSI = 100;
//			highestRSI = 0;
//			shortContract();
//		}
		sleep(1000);
	}
	


	private boolean isClose() {
		return Global.getCurrentAsk() - Global.getCurrentBid() <= 1;
	}

	boolean isLargeAverageHL() {
		try {
			return getTimeBase().getAverageHL(15) > 10;
		} catch (NotEnoughPeriodException e) {
			return false;
		}
	}

	protected boolean isSideWay() {
		return
		// !isTrending()
		// &&
		GetData.getShortTB().getHL(60).getFluctuation() < 80;
		// && GetData.getShortTB().getHL(3).getFluctuation() > 35;
	}

//	boolean isTrending() {
//		return !lowFluc;
		// ||getTimeBase().isMARising(10, 2)
		// || getTimeBase().isMADropping(10, 2)
		

//	}

	boolean isUpperEnd() {
		double difference = getTimeBase().getHL(60).getTempHigh()
				- getTimeBase().getHL(60).getTempLow();

		return Global.getCurrentPoint() > Global.getDayLow() + difference * 0.8;
	}

	boolean isLowerEnd() {
		double difference = getTimeBase().getHL(60).getTempHigh()
				- getTimeBase().getHL(60).getTempLow();

		return Global.getCurrentPoint() < Global.getDayLow() + difference * 0.2;
	}

	@Override
	public TimeBase getTimeBase() {

		return GetData.getShortTB();
	}

	double getCutLossPt() {

		return getTimeBase().getStandD() * 3;
	}

	double getStopEarnPt() {
		return getTimeBase().getStandD() * 2;
	}

	// double getStopEarnPt() {
	//
	// if (Global.getNoOfContracts() > 0){
	// if (Global.balance + Global.getCurrentPoint() <= 50){
	//
	// return 20;
	//
	// }else return 50;
	// }
	// else if (Global.getNoOfContracts() < 0){
	// if (Global.balance - Global.getCurrentPoint() <= 50){
	// return 20;
	//
	// }else return 50;
	// }
	//
	// return 50;
	// }

	// protected boolean reachGreatProfitPt(){
	//
	// if (Global.getNoOfContracts() > 0){
	// while(getTimeBase().getRSI() > 70)
	// sleep(1000);
	//
	// }else if (Global.getNoOfContracts() < 0){
	// while(getTimeBase().getRSI() < 30)
	// sleep(1000);
	// }
	// return super.reachGreatProfitPt();
	// }

	// protected void cutLoss(){
	// if (Global.getNoOfContracts() > 0){
	//
	//
	// while(getTimeBase().getRSI() > 70){
	// if (Global.isForceSellTime())
	// break;
	// sleep(1000);
	// }
	//
	// }else if (Global.getNoOfContracts() < 0){
	// while(getTimeBase().getRSI() < 30){
	// if (Global.isForceSellTime())
	// break;
	// sleep(1000);
	// }
	// }
	// super.cutLoss();
	// }

	// @Override
	// protected void cutLoss() {
	//
	// if (Global.getNoOfContracts() > 0){
	// if (Global.balance + Global.getCurrentPoint() <= -20
	//
	// ){
	//
	// // Global.addLog("Balance: " + (Global.balance +
	// Global.getCurrentPoint()));
	// closeContract("CutLoss " + className);
	// shutdownthis = true;
	// }
	// }else if (Global.getNoOfContracts() < 0){
	// if (Global.balance - Global.getCurrentPoint() <= -20
	//
	// ){
	// // Global.addLog("Balance: " + (Global.balance -
	// Global.getCurrentPoint()));
	// closeContract("CutLoss " + className);
	// shutdownthis = true;
	// }
	// }
	//
	//
	// }

//	protected boolean reachGreatProfitPt() {
//
//		if (getStopEarnPt() < stopEarnPt)
//			stopEarnPt = getStopEarnPt();
//
//
//		if (Global.getNoOfContracts() > 0)
//			return Global.getCurrentPoint() - stopEarnPt > buyingPoint
//					|| getTimeBase().getRSI() > 85;
//		else if (Global.getNoOfContracts() < 0)
//			return Global.getCurrentPoint() + stopEarnPt < buyingPoint
//					|| getTimeBase().getRSI() < 15;
//		else
//			return false;
//	}

	// void stopEarn() {
	// super.stopEarn();
	// if (Global.getNoOfContracts() > 0
	// && getTimeBase().getRSI() > 85)
	// closeContract("StopEarn " + className);
	// else if (Global.getNoOfContracts() < 0
	// && getTimeBase().getRSI() < 15)
	// closeContract("StopEarn " + className);
	// }

	// private double getProfit() {
	// if (Global.getNoOfContracts() > 0)
	// return Global.getCurrentPoint() - buyingPoint;
	// else if (Global.getNoOfContracts() < 0)
	// return buyingPoint - Global.getCurrentPoint();
	// return 0;
	// }

	@Override
	boolean trendReversed() {
		
//		if (getTimeBase().getRSI() > highestRSI)
//			highestRSI = getTimeBase().getRSI();
//		if (getTimeBase().getRSI() < lowestRSI)
//			lowestRSI = getTimeBase().getRSI();
//
//		if(Global.getNoOfContracts() > 0)
//			return getTimeBase().getRSI() < highestRSI - 10;
//		else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().getRSI() > lowestRSI + 10;
			
		return false;
		
		
//		checkLowFluc();
//		if(!lowFluc)
//			return true;
//		else
//			return false;
	}

	// protected void cutLoss() {
	// if (Global.getNoOfContracts() > 0
	// && Global.getCurrentPoint() < tempCutLoss) {
	// closeContract("CutLoss " + className);
	// shutdown = true;
	// // Global.setBackHandShort(true);
	// } else if (Global.getNoOfContracts() < 0
	// && Global.getCurrentPoint() > tempCutLoss) {
	// closeContract("CutLoss " + className);
	// shutdown = true;
	// // Global.setBackHandLong(true);
	// }
	// }

}