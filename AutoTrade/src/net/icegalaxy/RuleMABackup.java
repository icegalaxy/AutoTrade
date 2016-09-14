package net.icegalaxy;

public class RuleMABackup extends Rules {

	private double refPt;
	private float bufferPt = 5;
	private int maRetain = 15;
	private double fluctuation = 100;
	private double rsiFluctuation = 40;
	private double profit = 15;

	public RuleMABackup(boolean globalRunRule) {
		super(globalRunRule);
		// TODO Auto-generated constructor stub
	}


	
	public void setBufferPt(float f) {
		bufferPt = f;
	}

	public void openContract() {

		if (getTimeBase().getMA(20) == -1)
			return;

		if (getTimeBase().getMA(10) > getTimeBase().getMA(20)) {

			while (Global.getCurrentPoint() > getTimeBase().getHL(15)
					.getTempLow()) {

				// if (!getTimeBase().isMARising(20))
				// return;

				sleep(1000);

			}
			
			while (Global.getCurrentPoint() < getTimeBase().getHL(15)
					.getTempHigh()) {

				sleep(1000);

			}

			if (!Global.isOrderTime() || !getTimeBase().isMARising(20,1))
				return;
			longContract();

		} else {

			while (Global.getCurrentPoint() < getTimeBase().getHL(15)
					.getTempHigh()) {

				sleep(1000);

			}
			
			while (Global.getCurrentPoint() > getTimeBase().getHL(15)
					.getTempHigh()) {

				sleep(1000);

			}

			if (!Global.isOrderTime() || !getTimeBase().isMADropping(20,1))
				return;
			shortContract();

		}

	}

	@Override
	void updateStopEarn() {

		float ma10 = getTimeBase().getMA(10);

		if (Global.getNoOfContracts() > 0) {

			if (ma10 > tempCutLoss && Global.getCurrentPoint() > ma10)
				tempCutLoss = ma10;
			if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
				tempCutLoss = getTimeBase().getMainUpRail().getRail();

		} else if (Global.getNoOfContracts() < 0) {

			if (ma10 < tempCutLoss && Global.getCurrentPoint() < ma10)
				tempCutLoss = ma10;
			if (getTimeBase().getMainDownRail().getRail() != 0
					&& getTimeBase().getMainDownRail().getRail() < tempCutLoss)
				tempCutLoss = getTimeBase().getMainDownRail().getRail();
		}
	}

	@Override
	boolean trendReversed() {
		if (Global.getNoOfContracts() > 0){
			return getTimeBase().isMADropping(20,1);
		}else if (Global.getNoOfContracts() < 0)
			return getTimeBase().isMARising(20,1);
		return false;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getShortTB();
	}

//	@Override
//	public void closeContract() {
//
//		if (Global.getNoOfContracts() > 0)
//			tempCutLoss = buyingPoint - getCutLossPt();
//		else if (Global.getNoOfContracts() < 0)
//			tempCutLoss = buyingPoint + getCutLossPt();
//		
//		if (Global.getNoOfContracts() > 0) {
//						
//			while (hasContract) {
//				
//				float ma20 = getTimeBase().getMA(20);
//				
//				if (getTimeBase().getMainUpRail().getRail() > tempCutLoss) {
//					tempCutLoss = getTimeBase().getMainUpRail()
//							.getRail();
//				}
//
//				if (ma20 > tempCutLoss && Global.getCurrentPoint() > ma20)
//					tempCutLoss = getTimeBase().getMA(20);
//
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				sleep(1000);
//			}
//		} else if (Global.getNoOfContracts() < 0) {
//			while (hasContract) {
//				
//				float ma20 = getTimeBase().getMA(20);
//				
//				if (getTimeBase().getMainDownRail().getRail() != 0
//						&& getTimeBase().getMainDownRail().getRail() < tempCutLoss) {
//					tempCutLoss = (float) getTimeBase().getMainDownRail()
//							.getRail();
//					System.out.println("TempCutLoss: " + tempCutLoss);
//				}
//
//				if (getTimeBase().getMA(20) < tempCutLoss && Global.getCurrentPoint() < ma20)
//					tempCutLoss = getTimeBase().getMA(20);
//
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				sleep(1000);
//			}
//		}
//
//	}

}
