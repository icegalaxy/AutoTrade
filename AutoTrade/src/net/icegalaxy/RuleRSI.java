package net.icegalaxy;


public class RuleRSI extends Rules {
	
	protected double referencePoint;

	double lowerRSI = 30;
	double upperRSI = 70;
	double refRSI;
	
	public RuleRSI(boolean globalRunRule) {
		super(globalRunRule);
		
	}
	
	@Override
	boolean trendReversed(){
		return false;
	}

	
	@Override
	public void openContract() {
		
		refRSI = 50;

		if (!Global.isOrderTime()
		// || Global.getDayHigh() - Global.getDayLow() > 100
		)
			return;
		
		
		if (shutdown){
			

			while (getTimeBase().getRSI() < 30 || getTimeBase().getRSI() > 70)
				sleep(1000);
			
			shutdown = false;

		}
		

		if (getTimeBase().getRSI() < lowerRSI) {
						
			while (getTimeBase().getRSI() <= refRSI){
				refRSI = getTimeBase().getRSI();
				sleep(1000);
			}

			longContract();
			referencePoint = buyingPoint;
			


			// } else if (isDropping()) {
		} else if (getTimeBase().getRSI() > upperRSI) {
			
			while (getTimeBase().getRSI() >= refRSI){
				refRSI = getTimeBase().getRSI();
				sleep(1000);
			}

			shortContract();
			referencePoint = buyingPoint;
		}

		// wait to escape 70 30 zone
		while (getTimeBase().getRSI() < 30 || getTimeBase().getRSI() > 70)
			sleep(1000);
	}

	private boolean isSmallFluctutaion() {
		return getTimeBase().getHL(60).getFluctuation() < 100;
	}



	double getCutLossPt() {
		return 20;
	}

	double getStopEarnPt() {
//		return Global.getDayHigh() - Global.getDayLow(); //應該只會計買果一刻的diff (i.e. escaped 30 70後)  因為進入Close Contract個Loop之後，StopEarn只會減少，不會增多
//		return getTimeBase().getHL(120).getFluctuation() / 2;
		
		if (Global.getNoOfContracts() > 0){
			if (getTimeBase().getRSI() > 70)
				return 10;
			else
				return 50;
		}
		else{
			if (getTimeBase().getRSI() < 30)
				return 10;
			else
				return 50;
		}
		
	}

//	protected void cutLoss() {
//		if (Global.getNoOfContracts() > 0
//				&& Global.getCurrentPoint() < tempCutLoss){
//			closeContract("CutLoss " + className);
//			shutdown = true;
//			lowerRSI = lowerRSI - 5;
//		}
//		else if (Global.getNoOfContracts() < 0
//				&& Global.getCurrentPoint() > tempCutLoss){
//			closeContract("CutLoss " + className);
//			shutdown = true;
//			upperRSI = upperRSI + 5; 
//		}
//	}

	

	// @Override
	// double getCutLossPt() {
	// return 20;
	// }
	//
	// @Override
	// double getStopEarnPt() {
	// return 20;
	// }

	@Override
	public TimeBase getTimeBase() {
		return GetData.getShortTB();
	}
	
	@Override
	protected void cutLoss() {
		if (Global.getNoOfContracts() > 0
				&& Global.getCurrentPoint() < tempCutLoss){
			closeContract("CutLoss " + className);
			lowerRSI = lowerRSI - 5;
			shutdown = true;
		}
		else if (Global.getNoOfContracts() < 0
				&& Global.getCurrentPoint() > tempCutLoss){
			closeContract("CutLoss " + className);
			upperRSI = upperRSI + 5;
			shutdown = true;
		}
	}

	// @Override
	// boolean trendReversed() {
	//
	// if (Global.getNoOfContracts() > 0)
	// return getTimeBase().getRSI() < 70;
	// if (Global.getNoOfContracts() < 0)
	// return getTimeBase().getRSI() > 30;
	// return false;
	// }

	// @Override
	// boolean trendReversed2() {
	// double slope = 0;
	// double longSlope = 0;
	//
	// if (Global.getNoOfContracts() > 0) {
	// if (GetData.getSec10TB().getMainDownRail().getSlope() != 100)
	// slope = GetData.getSec10TB().getMainDownRail()
	// .getSlope();
	//
	// if (getTimeBase().getMainUpRail().getSlope() != 100)
	// longSlope = getTimeBase().getMainUpRail().getSlope();
	//
	// }
	// if (Global.getNoOfContracts() < 0) {
	//
	// if (GetData.getSec10TB().getMainUpRail().getSlope() != 100)
	// slope = GetData.getSec10TB().getMainUpRail()
	// .getSlope();
	//
	// if (getTimeBase().getMainDownRail().getSlope() != 100)
	// longSlope = getTimeBase().getMainDownRail().getSlope();
	// }
	// return slope > 5 && slope > longSlope * 2;
	// }

	// @Override
	// void updateStopEarn() {
	//
	// float ma10 = getTimeBase().getMA(10);
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (ma10 > tempCutLoss && Global.getCurrentPoint() > ma10)
	// tempCutLoss = ma10;
	// if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
	// tempCutLoss = getTimeBase().getMainUpRail().getRail();
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (ma10 < tempCutLoss && Global.getCurrentPoint() < ma10)
	// tempCutLoss = ma10;
	// if (getTimeBase().getMainDownRail().getRail() != 0
	// && getTimeBase().getMainDownRail().getRail() < tempCutLoss)
	// tempCutLoss = getTimeBase().getMainDownRail().getRail();
	// }
	// }

	// @Override
	// protected boolean reachGreatProfitPt() {
	//
	// // if (getStopEarnPt() < stopEarnPt)
	// // stopEarnPt = getStopEarnPt();
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (Global.getCurrentPoint() < referencePoint)
	// referencePoint = Global.getCurrentPoint();
	//
	// return Global.getCurrentPoint() - getStopEarnPt() > referencePoint;
	// }
	//
	// else if (Global.getNoOfContracts() < 0) {
	//
	// if (Global.getCurrentPoint() > referencePoint)
	// referencePoint = Global.getCurrentPoint();
	//
	// return Global.getCurrentPoint() + getStopEarnPt() < referencePoint;
	//
	// } else
	// return false;
	// }
	// @Override
	// protected void cutLoss() {
	//
	// if (Global.getNoOfContracts() > 0) {
	// if (Global.balance + Global.getCurrentPoint() <= -1 * getCutLossPt()) {
	//
	// closeContract("CutLoss " + className);
	// shutdown = true;
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	// if (Global.balance - Global.getCurrentPoint() <= -1 * getCutLossPt()) {
	// closeContract("CutLoss " + className);
	// shutdown = true;
	//
	// }
	// }
	// }

}
