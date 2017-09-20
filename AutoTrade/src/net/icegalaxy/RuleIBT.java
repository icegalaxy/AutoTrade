package net.icegalaxy;

public class RuleIBT extends Rules
{

	// private double refEMA;
	private boolean traded;
	private double cutLoss;
	private Chasing chasing;

	public RuleIBT(boolean globalRunRule)
	{
		super(globalRunRule);
		// setOrderTime(91500, 110000, 133000, 160000);
		// wait for EMA6, that's why 0945
		setOrderTime(91600, 93000, 160000, 160000, 230000, 230000);
		chasing = new Chasing();
	}

	public void openContract()
	{

		// if (chasing.chaseUp() || chasing.chaseDown()){
		//
		// Global.setChasing(chasing);
		// chasing = new Chasing();
		// }

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown || TimePeriodDecider.getTime() > 91800
				|| Global.getOpen() == 0 || traded)
			return;

		if (GetData.getShortTB().getLatestCandle().getClose() > Global.getOpen() + 10 
				&& XMLWatcher.ibtRise
				&& Global.getOpen() > Global.getpClose() + 10 
				&& TimePeriodDecider.getTime() > 91600)
				
		{
			Global.addLog("IBT UP confirmed");
					
			while (Global.getCurrentPoint() > Global.getOpen() + 10 
					&& Global.getCurrentPoint() < Global.getOpen())
			{
				
				sleep(1000);
				
				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}
					
			}		

			Global.addLog("Reached Open");

			waitForANewCandle();
			
			updateHighLow();

			while (getTimeBase().getLatestCandle().getOpen() > getTimeBase().getLatestCandle().getClose() - 5) 
																											
			{
				

				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}

				if (Global.getCurrentPoint() < Global.getOpen() - 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
				
				updateHighLow();
			}

			if (Global.getCurrentPoint() > refLow + 15)
				Global.addLog("Rise to fast, waiting for a pull back");

			while (Global.getCurrentPoint() > refLow + 15 || Global.isRapidDrop())
			{
				
				updateHighLow();
				
				if (Global.getCurrentPoint() > refLow + 30)
				{
					Global.addLog("Too far away");
					return;
				}
				
				if (Global.getCurrentPoint() < Global.getOpen() - 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}
				
				sleep(1000);
			}

			longContract();
			traded = true;
			cutLoss = refLow;
			Global.addLog("refLow: " + cutLoss);

		}

		else if (GetData.getShortTB().getLatestCandle().getClose() < Global.getOpen() - 10
				&& Global.getOpen() - 10 < Global.getpClose() 
				&& XMLWatcher.ibtDrop
				&& TimePeriodDecider.getTime() > 91600)

		{

			
			Global.addLog("IBT Down confirmed");
			
			while (Global.getCurrentPoint() < Global.getOpen() - 10 
					&& Global.getCurrentPoint() > Global.getOpen())
			{
				sleep(1000);
				
				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}
					
			}	
			
			
			Global.addLog("Reached Open");

			waitForANewCandle();
			
			updateHighLow();

			while (getTimeBase().getLatestCandle().getOpen() < getTimeBase().getLatestCandle().getClose() + 5) 
																												
			{

				if (TimePeriodDecider.getTime() > 93000)
				{
					Global.addLog(">93000");
					return;
				}

				if (Global.getCurrentPoint() > Global.getOpen() + 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				sleep(1000);
				
				updateHighLow();
			}

			if (Global.getCurrentPoint() < refHigh - 15)
				Global.addLog("Drop to fast, waiting for a pull back");

			while (Global.getCurrentPoint() < refHigh - 15 || Global.isRapidRise())
			{
				
				updateHighLow();
				
				if (Global.getCurrentPoint() < refHigh - 30)
				{
					Global.addLog("Too far away");
					return;
				}
				
				if (Global.getCurrentPoint() > Global.getOpen() + 10)
				{
					Global.addLog("Current point out of range");
					shutdown = true;
					return;
				}

				
				sleep(1000);

			}

			shortContract();
			traded = true;
			cutLoss = refHigh;
			Global.addLog("refHigh: " + cutLoss);

		}

		sleep(1000);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0)
		{
			
			// update stair
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (stair == 0 && GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
			


		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (stair == 0 && GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();

		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		
		double stair = XMLWatcher.stair;
		
		if (Global.getNoOfContracts() > 0)
		{
			
			//Expected profit
			if (getHoldingTime() > 300 && getProfit() > getExpectedProfit() + 5 && getProfit() <  16)
				if (tempCutLoss < buyingPoint + getExpectedProfit())
					tempCutLoss = buyingPoint + getExpectedProfit();
			
					
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
			if (getHoldingTime() > 300 && getProfit() > getExpectedProfit() + 5 && getProfit() <  16)
				if (tempCutLoss > buyingPoint - getExpectedProfit())
					tempCutLoss = buyingPoint - getExpectedProfit();
			
			
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
		
		return Math.max(10, Math.abs(buyingPoint - cutLoss) + 5);
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
		if (XMLWatcher.stair == 0)
			return 50;
		else
			return Math.abs(XMLWatcher.stair - buyingPoint);
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}

}