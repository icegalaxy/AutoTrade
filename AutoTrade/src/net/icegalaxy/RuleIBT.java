package net.icegalaxy;

public class RuleIBT extends Rules
{

	// private double refEMA;
	private boolean traded;

	public RuleIBT(boolean globalRunRule)
	{
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91600, 93000, 160000, 160000, 230000, 230000);
	}

	public void openContract()
	{

		// if (chasing.chaseUp() || chasing.chaseDown()){
		//
		// Global.setChasing(chasing);
		// chasing = new Chasing();
		// }

		if (!isOrderTime() 
				|| Global.getNoOfContracts() != 0 
				|| shutdown 
				|| TimePeriodDecider.getTime() > 91800
				|| Global.getOpen() == 0 
				|| traded
				)
			return;

		if (GetData.getShortTB().getLatestCandle().getClose() > Global.getOpen() 
				&& XMLWatcher.ibtRise
//				&& Global.getOpen() > Global.getpClose() + 10 
				&& TimePeriodDecider.getTime() > 91600)
				
		{
			Global.addLog("IBT UP confirmed");
					
			while (Global.getCurrentPoint() > Global.getOpen() + 10)
			{
				
				updateHighLow();
				
				sleep(1000);
				
				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}
					
			}		

			Global.addLog("Reached Open");

//			waitForANewCandle();
			
			updateHighLow();

			trailingDown(2);
			
			if (Global.getCurrentPoint() < Global.getOpen() - 10)
			{
				Global.addLog("Current point out of range");
				shutdown = true;
				return;
			}
			
			longContract();
			traded = true;
//			cutLoss = refLow;
			Global.addLog("refLow: " + refLow);

		}

		else if (GetData.getShortTB().getLatestCandle().getClose() < Global.getOpen()
//				&& Global.getOpen() - 10 < Global.getpClose() 
				&& XMLWatcher.ibtDrop
				&& TimePeriodDecider.getTime() > 91600)

		{

			
			Global.addLog("IBT Down confirmed");
			
			while (Global.getCurrentPoint() < Global.getOpen() - 10)
			{
				
				updateHighLow();
				
				sleep(1000);
				
				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}
					
			}	
			
			
			Global.addLog("Reached Open");

//			waitForANewCandle();
			
			updateHighLow();
			
			trailingUp(2);
			
			if (Global.getCurrentPoint() > Global.getOpen() + 10)
			{
				Global.addLog("Current point out of range");
				shutdown = true;
				return;
			}

			shortContract();
			traded = true;
//			cutLoss = refHigh;
			Global.addLog("refHigh: " + refHigh);

		}

		sleep(1000);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double stair = XMLWatcher.SAR;

		if (Global.getNoOfContracts() > 0)
		{
			
			// update stair
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss
					&& tempCutLoss < buyingPoint + getStopEarnPt())
					tempCutLoss = Math.min(buyingPoint + getStopEarnPt(), GetData.getShortTB().getLatestCandle().getLow());	

			if (stair == 0 
					&& GetData.getLongTB().getEma5().getEMA() > tempCutLoss
					&& GetData.getShortTB().getLatestCandle().getClose() > GetData.getLongTB().getEma5().getEMA())
				tempCutLoss = GetData.getLongTB().getEma5().getEMA();
			


		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > buyingPoint - getStopEarnPt())
				tempCutLoss = Math.max(buyingPoint - getStopEarnPt(), GetData.getShortTB().getLatestCandle().getHigh());

			if (stair == 0 
					&& GetData.getLongTB().getEma5().getEMA() < tempCutLoss
					&& GetData.getShortTB().getLatestCandle().getClose() < GetData.getLongTB().getEma5().getEMA())
				tempCutLoss = GetData.getLongTB().getEma5().getEMA();

		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		
		double stair = XMLWatcher.SAR;
		
		long max = 0;
		if (getExpectedProfit() > 10)
			max = 10;
		else
			max = getExpectedProfit();
		
		if (Global.getNoOfContracts() > 0)
		{
			
			//Expected profit
			if (getHoldingTime() > 300 
					&& getProfit() > tempCutLoss - buyingPoint + 5
				//	&& getProfit() <= 16
					&& getProfit() > max + 5
					&& tempCutLoss < buyingPoint + max)
			{
					tempCutLoss = buyingPoint + max;
					Global.addLog("Expected profit updated: " + (buyingPoint + max));
			}
			
					
			if (stair != 0 && tempCutLoss < stair && GetData.getShortTB().getLatestCandle().getClose() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}
		}else
		{
			
			//Expected profit
			if (getHoldingTime() > 300 
					&& getProfit() > buyingPoint - tempCutLoss + 5 
			//		&& getProfit() <=  16
					&& getProfit() > max + 5
					&& tempCutLoss > buyingPoint - max)
				{
					tempCutLoss = buyingPoint - max;
					Global.addLog("Expected profit updated: " + (buyingPoint - max));
				}
			
			
			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}
			
			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
			}
		}
		
		return Math.max(10, Math.abs(buyingPoint - Global.getOpen()) + 10);
	}

//	@Override
//	protected void cutLoss()
//	{
//
//		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
//		{
//			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
//			shutdown = true;
//		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
//		{
//			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
//			shutdown = true;
//
//		}
//
//		if (Global.getCurrentPoint() > chasing.getRefHigh())
//			chasing.setRefHigh(Global.getCurrentPoint());
//		if (Global.getCurrentPoint() < chasing.getRefLow())
//			chasing.setRefLow(Global.getCurrentPoint());
//
//	}

	double getStopEarnPt()
	{
		if (XMLWatcher.SAR == 0)
			return 50;
		else
			return Math.abs(XMLWatcher.SAR - buyingPoint);
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}

}