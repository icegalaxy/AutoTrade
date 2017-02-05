package net.icegalaxy;


public class RuleRSI extends Rules {
	
	private int lossTimes;
	// private double refEMA;
	private double cutLoss;
	double lowerRSI = 30;
	double upperRSI = 70;
	
	public RuleRSI(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(93000, 103000, 150000, 160000, 230000, 230000);
	}
	


	
	 @Override
	    public void openContract() {
	    	
//			if (shutdown) {
//				lossTimes++;
//				shutdown = false;
	//
//			}

	  
	        if (!Global.isOrderTime()
	        		|| shutdown
//	        		|| lossTimes >= 2
//	                || noOfCutLoss >= 3
	        // || Global.getDayHigh() - Global.getDayLow() > 100
	                )
	            return;

	        if (shutdown){

//	            while (getTimeBase().getRSI() < lowerRSI || getTimeBase().getRSI() > upperRSI)
//	                sleep(1000);
	//
//	            shutdown = false;

	        }

	        if (getTimeBase().getRSI() < lowerRSI) {
	        	
	        	refPt = 99999;
	        	
	        	while(GetData.getEma5().getEMA() < GetData.getEma25().getEMA())
	        	{
	        		 sleep(1000);
	        		 
	        		 if (!isOrderTime())
	        			 return;
	        		 
	        		 if (GetData.getEma5().getEMA() < refPt)
	        		 {
	        			 refPt = GetData.getEma5().getEMA();
	        		 }
	        		 
	        	}

	            longContract();
	        	
	        


	        } else
	        if (getTimeBase().getRSI() > upperRSI) {

	        	refPt = 0;
	        	
	        	while(GetData.getEma5().getEMA() > GetData.getEma25().getEMA())
	        	{
	        		 sleep(1000);
	        		 
	        		 if (!isOrderTime())
	        			 return;
	        		 
	        		 if (GetData.getEma5().getEMA() > refPt)
	        		 {
	        			 refPt = GetData.getEma5().getEMA();
	        		 }
	        	}

	            shortContract();

	        }

	        sleep(1000);
//	         wait to escape 70 30 zone
//	        while (getTimeBase().getRSI() < lowerRSI || getTimeBase().getRSI() > upperRSI)
//	            sleep(1000);
	    }



	    private boolean isSmallFluctutaion() {
	        return getTimeBase().getHL(60).getFluctuation() < 100;
	    }

	    boolean isDropping() {

	        double slope = 0;
	        double longSlope = 0;

	        if (GetData.getShortTB().getMainDownRail().getSlope() != 100)
	            slope = GetData.getShortTB().getMainDownRail()
	                    .getSlope();
	        if (getTimeBase().getMainUpRail().getSlope() != 100)
	            longSlope = getTimeBase().getMainUpRail().getSlope();

	        return slope > longSlope * 2
	                && GetData.getShortTB().getMainDownRail().slopeRetained > 2;
	    }

	    boolean isRising() {

	        double slope = 0;
	        double longSlope = 0;

	        if (GetData.getShortTB().getMainUpRail().getSlope() != 100)
	            slope = GetData.getShortTB().getMainUpRail().getSlope();

	        if (getTimeBase().getMainDownRail().getSlope() != 100)
	            longSlope = getTimeBase().getMainDownRail().getSlope();

	        return slope > longSlope * 2
	                && GetData.getShortTB().getMainUpRail().slopeRetained > 2;

	    }

	    double getCutLossPt() {
	        return 15;
	    }

	    double getStopEarnPt() {
//	        return Global.getDayHigh() - Global.getDayLow(); //應該只會計買果一刻的diff (i.e. escaped 30 70後)  因為進入Close Contract個Loop之後，StopEarn只會減少，不會增多
//	        return getTimeBase().getHL(120).getFluctuation() / 2;
	    	return 30;
	    }

	    @Override
		boolean trendReversed()
		{

			if (Global.getNoOfContracts() > 0)
			{

//				if (getProfit() > 30)
					return GetData.getEma5().getEMA() < refPt;
//				else
//					return GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1);

			} else
			{
//				if (getProfit() > 30)
					return GetData.getEma5().getEMA() > refPt;
//				else
//					return GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1);
			}
		}


	    @Override
	    public TimeBase getTimeBase() {
	        return GetData.getShortTB();
	    }

	    

	    void updateStopEarn() {

	    	double ema5;
			double ema6;

			// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
			// 10){
			ema5 = GetData.getShortTB().getLatestCandle().getClose();
			ema6 = GetData.getEma25().getEMA();

			if (Global.getNoOfContracts() > 0)
			{


				if (buyingPoint > tempCutLoss && getProfit() > 50)
				{
					Global.addLog("Free trade");
					tempCutLoss = buyingPoint + 30;
				}

				if (ema5 < ema6)
				{
					tempCutLoss = 99999;
					Global.addLog(className + " StopEarn: EMA5 x MA20");
					
				}
			} else if (Global.getNoOfContracts() < 0)
			{

		

				if (buyingPoint < tempCutLoss && getProfit() > 50)
				{
					Global.addLog("Free trade");
					tempCutLoss = buyingPoint - 30;

				}

				if (ema5 > ema6)
				{
					tempCutLoss = 0;
					Global.addLog(className + " StopEarn: EMA5 x MA20");
				
				}
			}

		}

	    
	}
