package net.icegalaxy;

import java.util.ArrayList;

//Use the OPEN Line
//Better not playing than lose

//This is for N shape

public class RuleSkyStair extends Rules
{
	// Stair currentStair;
	int currentStairIndex;
//	Stair currentStair;
	double cutLoss;
	double refHL;
	int EMATimer;
	double profitRange;

	public RuleSkyStair(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 115800, 130300, 160000, 1003000, 1003000); 
		shutdownIndex = new ArrayList<Integer>();
	}

	public void openContract()
	{


		if (!Global.isTradeTime() || Global.getNoOfContracts() != 0)
			return;

		

		for (int i = 0; i < XMLWatcher.stairs.size(); i++)
		{

			currentStairIndex = i;
			


			if (Global.getNoOfContracts() != 0)
				return;

			if (XMLWatcher.stairs.get(currentStairIndex).value == 0
					|| Math.abs(localShutdownPt - Global.getCurrentPoint()) < 50)
				continue;
			else
				localShutdownPt = 0;
			

			if (
					GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value && 
					GetData.minuteLow < XMLWatcher.stairs.get(currentStairIndex).value + XMLWatcher.stairs.get(currentStairIndex).tolerance / 2
					&& GetData.minuteLow > XMLWatcher.stairs.get(currentStairIndex).value)
			{
				
				
				if (localShutdownLongIndex == currentStairIndex && TimePeriodDecider.getEpochSec() - localShutdownLongSec < 1800)
					continue;
				else
				{
					localShutdownLongIndex = -1;
					localShutdownLongSec = -1;
				}

				if (!XMLWatcher.stairs.get(currentStairIndex).buying || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Long)");
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				Global.addLog("Waiting for a refLow");

				
				while(!GetData.tinyHL.findingLow || !isOrderTime())
				{
					if (shutdownLong(currentStairIndex))
						return;
					
//					if (Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value + 50)
//					{
//						Global.addLog("Left");
//						return;
//					}

					sleep(waitingTime);
				}
				
				
//				Global.addLog("Waiting for a tiny rise");
//
//				
//				while(Global.getCurrentPoint() < GetData.tinyHL.refLow + (GetData.tinyHL.getLatestHigh() - GetData.tinyHL.refLow)*0.24 //23.6% fibonacci
//
//						)
//				{
//
//					if (shutdownLong(currentStairIndex))
//						return;
//					
//					sleep(waitingTime);
//				}
				

				
				if (shutdownLong(currentStairIndex))
					return;
				
				if (refLow < XMLWatcher.stairs.get(currentStairIndex).refLow)
					XMLWatcher.stairs.get(currentStairIndex).refLow = refLow;
				else
					refLow = XMLWatcher.stairs.get(currentStairIndex).refLow;

				cutLoss = Math.min(XMLWatcher.stairs.get(currentStairIndex).refLow - XMLWatcher.stairs.get(currentStairIndex).tolerance / 2, XMLWatcher.stairs.get(currentStairIndex).value - 10);
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				while (true)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					if (shutdownLong(currentStairIndex))
						return;

					double reward = getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - Global.getCurrentPoint();
					double risk = Global.getCurrentPoint() - cutLoss;

					double rr = reward / risk;

					profitRange = reward;
					
					if (2 < rr && rr < 3 && risk < 100)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.3)
					{
						Global.addLog("RR= " + rr);
//						XMLWatcher.stairs.get(currentStairIndex).buying = false;
//						shutdownStair(currentStairIndex);
						return;
					}
					
					if (Global.getCurrentPoint() < cutLoss)
					{
						Global.addLog("Lower than cutloss");
						return;
					}

					sleep(waitingTime);

				}

				trailingDown(2);


				longContract();
				
				if (refLow < XMLWatcher.stairs.get(currentStairIndex).refLow)
					XMLWatcher.stairs.get(currentStairIndex).refLow = refLow;
				
				Global.updateCSV();
				Global.addLog("Ref Low: " + refLow);

//				cutLoss = Math.min(XMLWatcher.stairs.get(currentStairIndex).refLow - XMLWatcher.stairs.get(currentStairIndex).tolerance / 2, XMLWatcher.stairs.get(currentStairIndex).value - 10);
				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			} else if (
					GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value && 
					GetData.minuteHigh > XMLWatcher.stairs.get(currentStairIndex).value - XMLWatcher.stairs.get(currentStairIndex).tolerance / 2
					&& GetData.minuteHigh < XMLWatcher.stairs.get(currentStairIndex).value)
			{
				
				//must be put inside long or short to avoid reset of index and sec every time
				
				if (localShutdownShortIndex == currentStairIndex && TimePeriodDecider.getEpochSec() - localShutdownShortSec < 1800)
					continue;
				else
				{
					localShutdownShortIndex = -1;
					localShutdownShortSec = -1;
				}

				if (!XMLWatcher.stairs.get(currentStairIndex).selling || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Short)");
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				Global.addLog("Waiting for a refHigh");

				
				while(!GetData.tinyHL.findingHigh || !isOrderTime())
				{
					if (shutdownShort(currentStairIndex))
						return;
					
//					if (Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value - 50)
//					{
//						Global.addLog("Left");
//						return;
//					}
					
					sleep(waitingTime);
				}
				

//				Global.addLog("Waiting for a tiny drop");
//
//				
//				
//				while(Global.getCurrentPoint() > GetData.tinyHL.refHigh - (GetData.tinyHL.refHigh - GetData.tinyHL.getLatestLow())*0.24 //23.6% fibonacci
////						|| GetData.nanoHL.isRising()
//						)
//				{
//					
////					if (!volumeRising)
////						volumeRising = GetData.getShortTB().isQuantityRising();
//					
//					if(shutdownShort(currentStairIndex))
//						return;
//					sleep(waitingTime);
//				}
				
//				if (GetData.tinyHL.volumeOfRefHigh < GetData.getShortTB().getAverageQuantity() * 2)
//				{
//					Global.addLog("Ref Vol not enough");
//					Global.addLog("Ref High volume: " + GetData.tinyHL.volumeOfRefHigh);
//					Global.addLog("Average Quantitiy " + GetData.getShortTB().getAverageQuantity());
//					return;				
//				}
//				
//				Global.addLog("RecentLow: " + GetData.tinyHL.getVolumeOfRecentLow() + "\r\n" +
//						"Average: " + GetData.getShortTB().getAverageQuantity() + "\r\n" +
//						"High: " + GetData.tinyHL.volumeOfRefHigh);
//				
//				if (GetData.tinyHL.getVolumeOfRecentLow() < 0 || GetData.tinyHL.volumeOfRefHigh < 0)
//				{
//					Global.addLog("Quantity Erro");
//					XMLWatcher.stairs.get(currentStairIndex).selling = false;
//					shutdownStair(currentStairIndex);
//					
//					return;	
//				}
//				
//				
//				if (GetData.tinyHL.volumeOfRefHigh < GetData.tinyHL.getVolumeOfRecentLow())
//				{
//					
//					XMLWatcher.stairs.get(currentStairIndex).selling = false;
//					shutdownStair(currentStairIndex);
//					
//					return;				
//				}
				
				
				
//				if (!volumeRising)
//				{
//					Global.addLog("Volume not Rising");
//					return;
//				}
				
//				Global.addLog("Ref High: " + GetData.tinyHL.refHigh);
//				Global.addLog("Latest Low: " + GetData.tinyHL.getLatestLow());

				// if (Global.getCurrentPoint() < currentStair.value - 20)
				// Global.addLog("Drop to fast, waiting for a pull back");
				
				if(shutdownShort(currentStairIndex))
					return;

				if(refHigh > XMLWatcher.stairs.get(currentStairIndex).refHigh)
					XMLWatcher.stairs.get(currentStairIndex).refHigh = refHigh;
				else
					refHigh = XMLWatcher.stairs.get(currentStairIndex).refHigh;
				
				cutLoss = Math.max(XMLWatcher.stairs.get(currentStairIndex).refHigh + XMLWatcher.stairs.get(currentStairIndex).tolerance / 2, XMLWatcher.stairs.get(currentStairIndex).value + 10);
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				while (true)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					if(shutdownShort(currentStairIndex))
						return;

					double reward = Global.getCurrentPoint() - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value);
					double risk = cutLoss - Global.getCurrentPoint();

					double rr = reward / risk;

					profitRange = reward;
					
					if (2 < rr && rr < 3 && risk < 100)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.3)
					{
						Global.addLog("RR= " + rr);
//						XMLWatcher.stairs.get(currentStairIndex).selling = false;
//						shutdownStair(currentStairIndex);
						return;
					}
					
					if (Global.getCurrentPoint() > cutLoss)
					{
						Global.addLog("Higher than cutLoss");
						return;
					}

					sleep(waitingTime);

				}

				trailingUp(2);

//				if (refHigh > XMLWatcher.stairs.get(currentStairIndex).value + 50)
//				{
//					Global.addLog("RefHigh out of range");
//					XMLWatcher.stairs.get(currentStairIndex).selling = false;
//					shutdownStair(currentStairIndex);
//					return;
//				}

			
				
				shortContract();
				Global.updateCSV();
				Global.addLog("Ref High: " + refHigh);
				
				if(refHigh > XMLWatcher.stairs.get(currentStairIndex).refHigh)
					XMLWatcher.stairs.get(currentStairIndex).refHigh= refHigh;

//				cutLoss = Math.max(XMLWatcher.stairs.get(currentStairIndex).refHigh + 20, XMLWatcher.stairs.get(currentStairIndex).value + 10);

				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;
				

			}

		}
	}

	@Override
	boolean shutdownShort(int currentStairIndex)
	{
		
		if(super.shutdownShort(currentStairIndex))
			return true;
		
		if (!isDropping())
		{
			Global.addLog("ST: no Dropping");
			localShutdownShortIndex = currentStairIndex;
			localShutdownShortSec = TimePeriodDecider.getEpochSec();
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		if (GetData.tinyHL.isRising())
		{
			Global.addLog("TinyHL Is Rising");
			localShutdownShortIndex = currentStairIndex;
			localShutdownShortSec = TimePeriodDecider.getEpochSec();
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		if (GetData.nanoHL.isRising())
		{
			Global.addLog("NanoHL Is Rising");
			localShutdownShortIndex = currentStairIndex;
			localShutdownShortSec = TimePeriodDecider.getEpochSec();	
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		return false;
	}

	@Override
	boolean shutdownLong(int currentStairIndex)
	{
		if(super.shutdownLong(currentStairIndex))
			return true;
		
		if (!isRising())
		{
			Global.addLog("ST: no Rising");
			localShutdownLongIndex = currentStairIndex;
			localShutdownLongSec = TimePeriodDecider.getEpochSec();			
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		if (GetData.tinyHL.isDropping())
		{
			Global.addLog("Tiny Is Dropping");
			localShutdownLongIndex = currentStairIndex;
			localShutdownLongSec = TimePeriodDecider.getEpochSec();
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		if (GetData.nanoHL.isDropping())
		{
			Global.addLog("Nano Is Dropping");
			localShutdownLongIndex = currentStairIndex;
			localShutdownLongSec = TimePeriodDecider.getEpochSec();
			localShutdownPt = Global.getCurrentPoint();
//			waitForAPeriod(1800);
			return true;
		}
		
		return false;
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

//		while (true)
//		{
//			try
//			{
//				currentStair = XMLWatcher.stairs.get(currentStairIndex);
//				break;
//			} catch (Exception e)
//			{
//				Global.addLog("Cannot get current stair");
//				sleep(1000);
//			}
//		}

//		double stair = XMLWatcher.stair;

//		updateExpectedProfit(10);

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

//			if (buyingPoint > tempCutLoss && getProfit() > 30)
//			{
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint + 5;
//			}

			// return Math.max(20, buyingPoint - currentStair.value + 30);

			// just in case, should be stopped by tempCutLoss first
			return Math.max(10, buyingPoint - cutLoss);
		} else
		{
			// first profit then loss
			// if (tempCutLoss > currentStair.value + 10 && refLow <
			// currentStair.value - 30)
			// tempCutLoss = currentStair.value + 10;

			if (tempCutLoss > cutLoss)
				tempCutLoss = cutLoss;

//			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

//			if (buyingPoint < tempCutLoss && getProfit() > 30)
//			{
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint - 5;
//			}
			// return Math.max(20, currentStair.value - buyingPoint + 30);

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
			if (Global.getCurrentPoint() > buyingPoint + profitRange / 2 && Global.getCurrentPoint() < getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - 20)
			{
				double expectedEarn =  getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - Global.getCurrentPoint();
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
			
			if (getHoldingTime() > 1800 && getProfit() > 5 && tempCutLoss < buyingPoint + 5)
			{
				tempCutLoss = buyingPoint + 5;
				Global.addLog("Free trade");
			}
		}else if (Global.getNoOfContracts() < 0)
		{
			
			//Calculate how for to reach stop earn and set it equal to tempCutLoss
			if (Global.getCurrentPoint() < buyingPoint - profitRange / 2 && Global.getCurrentPoint() > getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) + 20)
			{
				double expectedEarn = Global.getCurrentPoint() - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value);
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
			
			if (getHoldingTime() > 1800 && getProfit() > 5 && tempCutLoss > buyingPoint - 5)
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
			
			
//			if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss < buyingPoint + 100)
//			{
//				tempCutLoss = buyingPoint + 100;
//				Global.addLog("Get 100pt profit");
//			}
//			
//			if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss < buyingPoint + 5)
//			{
//				tempCutLoss = buyingPoint + 5;
//				Global.addLog("Free trade");
//			}
			
			if (GetData.tinyHL.getLatestLow() > tempCutLoss)
			{
				tempCutLoss = GetData.tinyHL.getLatestLow();
				Global.addLog("Profit pt update by tinyHL: " + tempCutLoss);
			}

			// update stair
//			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

			if (tempCutLoss < getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
			{
				
				if (tempCutLoss < GetData.getShortTB().getLatestCandle().getLow())
					Global.addLog("Profit pt update by m1: " + GetData.getShortTB().getLatestCandle().getLow());
				
				tempCutLoss = Math.min(getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getShortTB().getLatestCandle().getLow());
				
				
				
			}

			// if (GetData.getLongTB().getEMA(5) <
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{
			
//			if (GetData.getShortTB().getLatestCandle().getHigh() > GetData.getLongTB().getEma5().getEMA()
//					&& GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
//				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
			
			
//			if (getHoldingTime() > 3600 && getProfit() > 100 && tempCutLoss > buyingPoint - 100)
//			{
//				tempCutLoss = buyingPoint - 100;
//				Global.addLog("Get 100pt profit");
//			}
//			
//			if (getHoldingTime() > 3600 && getProfit() > 5 && tempCutLoss > buyingPoint - 5)
//			{
//				tempCutLoss = buyingPoint - 5;
//				Global.addLog("Free trade");
//			}
			
			if (GetData.tinyHL.getLatestHigh() < tempCutLoss)
			{			
				tempCutLoss = GetData.tinyHL.getLatestHigh();
				Global.addLog("Profit pt update by tinyHL: " + tempCutLoss);
			}

//			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
//			{
//				Global.addLog("Stair updated: " + stair);
//				tempCutLoss = stair;
//			}

			if (tempCutLoss > getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
			{
				
				if (tempCutLoss > GetData.getShortTB().getLatestCandle().getHigh())
					Global.addLog("Profit pt update by m1: " + GetData.getShortTB().getLatestCandle().getHigh());
				
				tempCutLoss = Math.max(getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getShortTB().getLatestCandle().getHigh());
				
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

			// if (refLow < currentStair.value - 20)
			// {
			// shutdown = true;
			// return Math.min(20, refHigh - buyingPoint - 5);
			// }
			//
			// if (refLow < currentStair.value - 10)
			// {
			// // Global.addLog("Line unclear, trying to take little profit");
			// shutdown = true;
			// return 30;
			// }
			//
			// // Try to take profit if blocked by EMA
			// if (GetData.getLongTB().getEma50().getEMA() - buyingPoint > 50)
			// {
			// return GetData.getLongTB().getEma50().getEMA() - buyingPoint;
			// } else if (GetData.getLongTB().getEma250().getEMA() - buyingPoint
			// > 50)
			// {
			// return GetData.getLongTB().getEma250().getEMA() - buyingPoint;
			// }

			return Math.max(10, getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - buyingPoint);
		} else
		{

			// if (refHigh > currentStair.value + 20)
			// {
			// shutdown = true;
			// return Math.min(20, buyingPoint - refLow - 5);
			// }
			//
			// if (refHigh > currentStair.value + 10)
			// {
			// // Global.addLog("Line unclear, trying to take little profit");
			// shutdown = true;
			// return 30;
			// }
			//
			// // Try to take profit if blocked by EMA
			// if (buyingPoint - GetData.getLongTB().getEma50().getEMA() > 50)
			// {
			// return buyingPoint - GetData.getLongTB().getEma50().getEMA();
			// } else if (buyingPoint - GetData.getLongTB().getEma250().getEMA()
			// > 50)
			// {
			// return buyingPoint - GetData.getLongTB().getEma250().getEMA();
			// }

			return Math.max(10, buyingPoint - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));
		}

	}

	public void waitForANewCandle(TimeBase tb, int currentSize, boolean buying)
	{

		currentSize = tb.getCandles().size();

		while (currentSize == tb.getCandles().size())
		{

			updateHighLow();
			sleep(waitingTime);

			// ??? if (buying)
			// {
			// if (GetData.getShortTB().getLatestCandle().getClose() >
			// GetData.getShortTB().getPreviousCandle(1)
			// .getOpen())
			// {
			// Global.addLog("Break previous open");
			// break;
			// }
			// }

		}

	}

	@Override
	protected void cutLoss()
	{

		super.cutLoss();
		
		if(shutdownRule)
		{
			XMLWatcher.stairs.get(currentStairIndex).shutdown = true;
			Global.updateCSV();
		}
		
	}

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
	
	boolean isRising()
	{
		return GetData.tinyHL.isRising() && !GetData.tinyHL.isDropping();
	}
	
	boolean isDropping(){
		return GetData.tinyHL.isDropping() && !GetData.tinyHL.isRising();
	}
	
	

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}