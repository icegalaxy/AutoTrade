package net.icegalaxy;

import java.util.ArrayList;
import java.util.List;

//Use the OPEN Line

public class RulePriceAction extends Rules
{
	// Stair currentStair;
//	int currentStairIndex;
	// Stair currentStair;
//	static ArrayList<Integer> shutdownIndex;
	double cutLoss;
//	double refHL;
//	static int reActivatePeriod = 10000;
//	int EMATimer;
	double profitRange;
	double profitPt;

	public RulePriceAction(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 115800, 130300, 161500, 170300, 1003000);
	}

	public void openContract()
	{
		
		//Re-activated
		if (!Global.isTradeTime() && shutdown)
		{
			Global.addLog("Re-activate PriceAction");
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown)
			return;

		if (GetData.tinyHL.isRising() 
				&& Global.getCurrentPoint() < GetData.tinyHL.getLatestLow() + 30)
				
		{
			
			Global.addLog("Price Action: Long");
			
			//check fewer times		
			if(GetData.tinyHL.getVolumeOfRecentLow() < GetData.getShortTB().getAverageQuantity() * 2
					|| GetData.tinyHL.getVolumeOfRecentLow() < getVolumeOfHigh())
			{
				Global.addLog("Volume Not Enough" + "\r\n" +
						"RecentLow: " + GetData.tinyHL.getVolumeOfRecentLow() + "\r\n" +
						"Average: " + GetData.getShortTB().getAverageQuantity() + "\r\n" +
						"High: " + getVolumeOfHigh());

				
				while(GetData.tinyHL.isRising() && Global.getCurrentPoint() < GetData.tinyHL.getLatestLow() + 50)
					sleep(waitingTime);
			}	

			while (true)
			{	
				if (GetData.tinyHL.findingHigh)
					profitPt = GetData.tinyHL.refHigh;
				else
					profitPt = GetData.tinyHL.getLatestHigh();
				
				double reward = profitPt - Global.getCurrentPoint();
						
				double risk = Global.getCurrentPoint() - GetData.tinyHL.getLatestLow() + 10;

				double rr = reward / risk;
				
				profitRange = reward;
				
				if (!GetData.tinyHL.isRising())
				{
					Global.addLog("Not Rising");
					return;
				}

				if (rr > 3 && risk < 30)
				{
					Global.addLog("RR= " + rr);
					break;
				}

				if (rr < 0.3)
				{
					Global.addLog("RR= " + rr);
					return;
				}

				sleep(waitingTime);

			}

			trailingDown(2);

			longContract();

			cutLoss = GetData.tinyHL.getLatestLow() - 10;

			Global.addLog("Profit: " + profitPt);
			Global.addLog("Cut Loss: " + cutLoss);

			return;

		} else if (GetData.tinyHL.isDropping() && Global.getCurrentPoint() > GetData.tinyHL.getLatestLow() - 30)
		{
			
			Global.addLog("Price Action: Short");
			
			if(GetData.tinyHL.getVolumeOfRecentHigh()  < GetData.getShortTB().getAverageQuantity() * 2
					|| GetData.tinyHL.getVolumeOfRecentHigh() < getVolumeOfLow())
			{
				Global.addLog("Volume Not Enough" + "\r\n" +
						"RecentHigh: " + GetData.tinyHL.getVolumeOfRecentHigh() + "\r\n" +
						"Average: " + GetData.getShortTB().getAverageQuantity() + "\r\n" +
						"Low: " + getVolumeOfLow());
				
				while(GetData.tinyHL.isDropping() && Global.getCurrentPoint() > GetData.tinyHL.getLatestLow() - 50)
					sleep(waitingTime);
			}
			

			while (true)
			{

//				if (shutdownShort(currentStairIndex))
//					return;
				
				

				if (GetData.tinyHL.findingLow)
					profitPt = GetData.tinyHL.refLow;
				else
					profitPt = GetData.tinyHL.getLatestLow();
				
				double reward = Global.getCurrentPoint() - profitPt;
				
				profitRange = reward;

				double risk = GetData.tinyHL.getLatestHigh() - Global.getCurrentPoint() + 10;

				double rr = reward / risk;

				if (!GetData.tinyHL.isDropping())
				{
					Global.addLog("Not Dropping");
					return;
				}
				
				
				if (rr > 3 && risk < 30)
				{
					Global.addLog("RR= " + rr);
					break;
				}

				if (rr < 0.3)
				{
					Global.addLog("RR= " + rr);
					return;
				}

				sleep(waitingTime);

			}

			trailingUp(2);

			shortContract();

			cutLoss = GetData.tinyHL.getLatestHigh() + 10;

			Global.addLog("Profit: " + profitPt);
			Global.addLog("Cut Loss: " + cutLoss);

			return;

		}

	}


	private double getVolumeOfHigh()
	{
		if (GetData.tinyHL.findingHigh)
			return GetData.tinyHL.volumeOfRefHigh;
		else
			return GetData.tinyHL.getVolumeOfRecentHigh();
	}
	
	private double getVolumeOfLow()
	{
		if (GetData.tinyHL.findingLow)
			return GetData.tinyHL.volumeOfRefLow;
		else
			return GetData.tinyHL.getVolumeOfRecentLow();
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		double stair = XMLWatcher.stair;

		if (Global.getNoOfContracts() > 0)
		{

			// set 10 pts below cutLoss

			if (tempCutLoss < cutLoss)
				tempCutLoss = cutLoss;

			if (stair != 0 && tempCutLoss < stair && GetData.getShortTB().getLatestCandle().getClose() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			return Math.max(10, buyingPoint - cutLoss);
			
		} else
		{
			

			if (tempCutLoss > cutLoss)
				tempCutLoss = cutLoss;

			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}


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
		double stair = XMLWatcher.stair;

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
			
			if (GetData.tinyHL.getLatestLow() > tempCutLoss)
			{
				tempCutLoss = GetData.tinyHL.getLatestLow();
				Global.addLog("Profit pt update by tinyHL: " + tempCutLoss);
			}

			// update stair
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

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
			if (GetData.tinyHL.getLatestHigh() < tempCutLoss)
			{			
				tempCutLoss = GetData.tinyHL.getLatestHigh();
				Global.addLog("Profit pt update by tinyHL: " + tempCutLoss);
			}
			

			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

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

			return Math.max(10, profitPt - buyingPoint - 10);
		} else
		{

			return Math.max(10, buyingPoint - profitPt - 10);
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