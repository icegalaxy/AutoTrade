package net.icegalaxy;

import java.util.ArrayList;
import java.util.List;

//Use the OPEN Line

public class RulePriceAction extends Rules
{

	double cutLoss;
	double profitRange;
	double profitPt;

	public RulePriceAction(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(94500, 110000, 143000, 154500, 203000, 234500);
	}

	public void openContract()
	{
		
		//Re-activated
		if (!Global.isTradeTime() && shutdownRule)
		{
			Global.addLog("Re-activate PriceAction");
			shutdownRule = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdownRule)
			return;

		if (GetData.nanoHL.isRising() 
				&& !GetData.nanoHL.isDropping()
				&& GetData.tinyHL.isRising() 
				&& !GetData.tinyHL.isDropping()
				&& GetData.minuteLow < GetData.nanoHL.getLatestLow() + 10)
				
		{
			
			Global.addLog("Price Action: Long");
			
			Global.addLog("Waiting for a new candle");
			
			waitForANewCandle();
			
			Global.addLog("Waiting for a Yang Candle");

			while (true)
			{
				
				if (getTimeBase().getLatestCandle().getClose() <  GetData.nanoHL.getLatestLow() - 10)
				{
					Global.addLog("1min Close out of range");
					return;
				}
				
				if ( GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getLatestCandle().getOpen() + 5)
					break;
						
				sleep(waitingTime);
			}
			

			while (true)
			{	
				if (GetData.nanoHL.findingHigh)
					profitPt = Math.min(GetData.nanoHL.refHigh, getLongStopEarn(Global.getCurrentPoint()));
				else
					profitPt = Math.min(GetData.nanoHL.getLatestHigh(), getLongStopEarn(Global.getCurrentPoint()));
				
				double reward = profitPt - Global.getCurrentPoint();
						
				double risk = Global.getCurrentPoint() - GetData.nanoHL.getLatestLow() + 5;

				double rr = reward / risk;
				
				profitRange = reward;
				

				
				if (!GetData.nanoHL.isRising() || GetData.nanoHL.isDropping())
				{
					Global.addLog("Not Rising or is Dropping");
					return;
				}

				if (2 < rr && rr < 3 && risk < 100)
				{
					Global.addLog("RR= " + rr);
					break;
				}

				if (Global.getCurrentPoint() > GetData.nanoHL.getLatestLow() + 30)
				{
					Global.addLog("Left");
					return;
				}

				sleep(waitingTime);

			}

			trailingDown(2);

			longContract();

			cutLoss = GetData.nanoHL.getLatestLow() - 5;

			Global.addLog("Profit: " + profitPt);
			Global.addLog("Cut Loss: " + cutLoss);

			return;

		} else if (GetData.nanoHL.isDropping() 
				&& !GetData.nanoHL.isRising()
				&& GetData.tinyHL.isDropping() 
				&& !GetData.tinyHL.isRising()
				&& GetData.minuteHigh > GetData.nanoHL.getLatestHigh() - 10)
		{
			
			Global.addLog("Price Action: Short");
			
			
			Global.addLog("Waiting for a new candle");
			
			waitForANewCandle();
			
			Global.addLog("Waiting for a Ying Candle");

			while (true)
			{
				
				if (getTimeBase().getLatestCandle().getClose() >  GetData.nanoHL.getLatestLow() + 10)
				{
					Global.addLog("1min Close out of range");
					return;
				}
				
				if ( GetData.getShortTB().getLatestCandle().getClose() < GetData.getShortTB().getLatestCandle().getOpen() - 5)
					break;
						
				sleep(waitingTime);
			}
			
			

			while (true)
			{


			
				if (GetData.nanoHL.findingLow)
					profitPt = Math.max(GetData.nanoHL.refLow, getShortStopEarn(Global.getCurrentPoint()));
				else
					profitPt = Math.max(GetData.nanoHL.getLatestLow(), getShortStopEarn(Global.getCurrentPoint()));
				
				double reward = Global.getCurrentPoint() - profitPt;
				
				profitRange = reward;

				double risk = GetData.nanoHL.getLatestHigh() - Global.getCurrentPoint() + 5;

				double rr = reward / risk;
				

				if (!GetData.nanoHL.isDropping() || GetData.nanoHL.isRising())
				{
					Global.addLog("Not Dropping or is Rising");
					return;
				}
				
				
				if (2 < rr && rr < 3 && risk < 100)
				{
					Global.addLog("RR= " + rr);
					break;
				}

				if (Global.getCurrentPoint() < GetData.nanoHL.getLatestLow() - 30)
				{
					Global.addLog("Left");
					return;
				}

				sleep(waitingTime);

			}

			trailingUp(2);

			shortContract();

			cutLoss = GetData.nanoHL.getLatestHigh() + 5;

			Global.addLog("Profit: " + profitPt);
			Global.addLog("Cut Loss: " + cutLoss);

			return;

		}

	}


	private double getVolumeOfHigh()
	{
		if (GetData.nanoHL.refHigh > GetData.nanoHL.getLatestHigh() )
			return GetData.nanoHL.volumeOfRefHigh;
		else
			return GetData.nanoHL.getVolumeOfRecentHigh();
	}
	
	private double getVolumeOfLow()
	{
		if (GetData.nanoHL.refLow < GetData.nanoHL.getLatestLow())
			return GetData.nanoHL.volumeOfRefLow;
		else
			return GetData.nanoHL.getVolumeOfRecentLow();
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

//		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0)
		{

			// set 10 pts below cutLoss

			if (tempCutLoss < cutLoss)
				tempCutLoss = cutLoss;

//			if (stair != 0 && tempCutLoss < stair && GetData.getShortTB().getLatestCandle().getClose() > stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

			return Math.max(10, buyingPoint - cutLoss);
			
		} else
		{
			

			if (tempCutLoss > cutLoss)
				tempCutLoss = cutLoss;

//			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}


			// just in case, should be stopped by tempCutLoss first
			return Math.max(10, cutLoss - buyingPoint);
		}
	}

	@Override
	protected void updateCutLoss()
	{
		super.updateCutLoss();

		if (Global.getNoOfContracts() > 0)
		{

			//Calculate how for to reach stop earn and set it equal to tempCutLoss
			if (Global.getCurrentPoint() > buyingPoint + profitRange / 2 && Global.getCurrentPoint() < profitPt - 20)
			{
				double expectedEarn =  profitPt - Global.getCurrentPoint();
				if (tempCutLoss < Global.getCurrentPoint() - expectedEarn)
				{
					tempCutLoss = Global.getCurrentPoint() - expectedEarn;
					Global.addLog("Profit update: " + tempCutLoss);
				}
				
			}
			
			
			if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss < buyingPoint + 80)
			{
				tempCutLoss = buyingPoint + 80;
				Global.addLog("Get 100pt profit");
			}

			if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss < buyingPoint + 5)
			{
				tempCutLoss = buyingPoint + 5;
				Global.addLog("Free trade");
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (Global.getCurrentPoint() < buyingPoint - profitRange / 2 && Global.getCurrentPoint() > profitPt + 20)
			{
				double expectedEarn = Global.getCurrentPoint() - profitPt;
				if (tempCutLoss > Global.getCurrentPoint() + expectedEarn)
				{
					tempCutLoss = Global.getCurrentPoint() + expectedEarn;
					Global.addLog("Profit update: " + tempCutLoss);
				}
				
			}
			

			if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss > buyingPoint - 80)
			{
				tempCutLoss = buyingPoint - 80;
				Global.addLog("Get 100pt profit");
			}

			if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss > buyingPoint - 5)
			{
				tempCutLoss = buyingPoint - 5;
				Global.addLog("Free trade");
			}
		}

	}

	@Override
	void updateStopEarn()
	{
//		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0)
		{

//			if (GetData.getShortTB().getLatestCandle().getLow() < GetData.getLongTB().getEma5().getEMA()
//					&& GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
//				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();

			// if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss <
			// buyingPoint + 100)
			// {
			// tempCutLoss = buyingPoint + 100;
			// Global.addLog("Get 100pt profit");
			// }
			//
			// if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss <
			// buyingPoint + 5)
			// {
			// tempCutLoss = buyingPoint + 5;
			// Global.addLog("Free trade");
			// }
			
			if (GetData.nanoHL.getLatestLow() > tempCutLoss)
			{
				tempCutLoss = GetData.nanoHL.getLatestLow();
				Global.addLog("Profit pt update by nanoHL: " + tempCutLoss);
			}

			// update stair
//			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

			if (tempCutLoss < profitPt)
			{
				
				if (tempCutLoss < GetData.getShortTB().getLatestCandle().getLow())
					Global.addLog("Profit pt update by m1: " + GetData.getShortTB().getLatestCandle().getLow());
				
				tempCutLoss = Math.min(profitPt,GetData.getShortTB().getLatestCandle().getLow());
				
				
				
			}

			// if (GetData.getLongTB().getEMA(5) <
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

//			if (GetData.getShortTB().getLatestCandle().getHigh() > GetData.getLongTB().getEma5().getEMA()
//					&& GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
//				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();

			// if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss >
			// buyingPoint - 100)
			// {
			// tempCutLoss = buyingPoint - 100;
			// Global.addLog("Get 100pt profit");
			// }
			//
			// if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss >
			// buyingPoint - 5)
			// {
			// tempCutLoss = buyingPoint - 5;
			// Global.addLog("Free trade");
			// }
			if (GetData.nanoHL.getLatestHigh() < tempCutLoss)
			{			
				tempCutLoss = GetData.nanoHL.getLatestHigh();
				Global.addLog("Profit pt update by nanoHL: " + tempCutLoss);
			}
			

//			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

			if (tempCutLoss > profitPt)
			{
				
				if (tempCutLoss > GetData.getShortTB().getLatestCandle().getHigh())
					Global.addLog("Profit pt update by m1: " + GetData.getShortTB().getLatestCandle().getHigh());
				
				tempCutLoss = Math.max(profitPt,GetData.getShortTB().getLatestCandle().getHigh());
				
			}

			// if (GetData.getLongTB().getEMA(5) >
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 0;
		}

	}

	@Override
	void stopEarn()
	{
		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() < buyingPoint + 5)
			{
				closeContract(className + ": Break even, short @ " + Global.getCurrentBid());
				// shutdown = true;
			} else if (Global.getCurrentPoint() < tempCutLoss)
				closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() > buyingPoint - 5)
			{
				closeContract(className + ": Break even, long @ " + Global.getCurrentAsk());
				// shutdown = true;
			} else if (Global.getCurrentPoint() > tempCutLoss)
				closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());

		}
	}

	// @Override
	// protected void cutLoss()
	// {
	//
	// if (Global.getNoOfContracts() > 0)
	// {
	//
	// //breakEven
	// if (getProfit() > 20 && tempCutLoss < buyingPoint + 5)
	// tempCutLoss = buyingPoint + 5;
	//
	// if (Global.getCurrentPoint() < tempCutLoss)
	// {
	// closeContract(className + ": CutLoss, short @ " +
	// Global.getCurrentBid());
	// shutdown = true;
	// }
	// } else if (Global.getNoOfContracts() < 0)
	// {
	//
	// //breakEven
	// if (getProfit() > 20 && tempCutLoss > buyingPoint - 5)
	// tempCutLoss = buyingPoint - 5;
	//
	// if (Global.getCurrentPoint() > tempCutLoss)
	// {
	// closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
	// shutdown = true;
	// }
	//
	// }
	//
	// }

	// @Override
	// boolean trendReversed()
	// {
	// if (reverse == 0)
	// return false;
	// else if (Global.getNoOfContracts() > 0)
	// return Global.getCurrentPoint() < reverse;
	// else
	// return Global.getCurrentPoint() > reverse;
	// }

	double getStopEarnPt()
	{

		if (Global.getNoOfContracts() > 0)
		{
//			double profit = Math.min(profitPt - buyingPoint, getLongStopEarn(buyingPoint));

			return Math.max(10, profitPt - buyingPoint);
		} else
		{
			
//			double profit = Math.min(buyingPoint - profitPt, getShortStopEarn(buyingPoint));

			return Math.max(10, buyingPoint - profitPt);
		}

	}

	

//	@Override
//	protected void cutLoss()
//	{
//
//		super.cutLoss();
//
//		if (shutdown)
//		{
//			XMLWatcher.stairs.get(currentStairIndex).shutdown = true;
//			Global.updateCSV();
//		}
//
//	}

	// @Override
	// public void trendReversedAction()
	// {
	//
	// trendReversed = true;
	// }

	// private void updateEMAValue(){
	//
	// EMATimer++;
	//
	// if (EMATimer > 60) //don't want to check too frequently
	// {
	// if (XMLWatcher.stairs.get(0).value !=
	// GetData.getLongTB().getEma50().getEMA())
	// XMLWatcher.stairs.get(0).value = GetData.getLongTB().getEma50().getEMA();
	// if (XMLWatcher.stairs.get(1).value !=
	// GetData.getLongTB().getEma250().getEMA())
	// XMLWatcher.stairs.get(1).value =
	// GetData.getLongTB().getEma250().getEMA();
	//
	// EMATimer = 0;
	// }
	//
	//
	// }
	
	
	
	
	

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}