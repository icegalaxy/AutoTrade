package net.icegalaxy;

import java.util.ArrayList;
import java.util.List;


public class RuleSkyStairBreakOut extends Rules
{
	// Stair currentStair;
	int currentStairIndex;
//	Stair currentStair;
	
	double cutLoss;
	double refHL;
	
	int EMATimer;
	double profitRange;

	public RuleSkyStairBreakOut(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(93000, 115800, 143000, 160000, 1003000, 1003000); 
		shutdownIndex = new ArrayList<Integer>();
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{
		
//		boolean volumeRising = false;

		if (!Global.isTradeTime() || Global.getNoOfContracts() != 0 || !isOrderTime())
			return;

		for (int i = 2; i < XMLWatcher.stairs.size(); i++)
		{

			currentStairIndex = i;


			if (Global.getNoOfContracts() != 0)
				return;

			if (XMLWatcher.stairs.get(currentStairIndex).value == 0)
				continue;

			// Long
			if (
					GetData.getLongTB().getPreviousCandle(1).getClose() < XMLWatcher.stairs.get(currentStairIndex).value && 
					GetData.getLongTB().getLatestCandle().getClose() > XMLWatcher.stairs.get(currentStairIndex).value
					)
			{
				
				if (localShutdownLongIndex == currentStairIndex)
					continue;
				else
					localShutdownLongIndex = -1;

				if (!XMLWatcher.stairs.get(currentStairIndex).buying || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Break out " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Long)");
				Global.addLog("ST1 Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				if (shutdownLong(currentStairIndex))
					return;
				
//				Global.addLog("Waiting for a new candle");
//				
//				waitForANewCandle();
				
				
//				Global.addLog("Waiting for a refLow");	
//				
//				while(!GetData.tinyHL.findingLow || !isOrderTime())
//				{
//					if (shutdownLong(currentStairIndex))
//						return;
//					
//					if (Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value + 50)
//					{
//						Global.addLog("Left");
//						return;
//					}
//
//					sleep(waitingTime);
//				}
//				
//				if (GetData.tinyHL.refLow < refLow)
//					refLow = GetData.tinyHL.refLow;
//				
//				Global.addLog("Waiting for a tiny rise");
//				
//				
//				while (true)
//				{
//					
//					if (shutdownLong(currentStairIndex))
//						return;
//					
//					if (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getLatestCandle().getOpen() + 5
//						&& GetData.getShortTB().getLatestCandle().getClose() > XMLWatcher.stairs.get(currentStairIndex).value)
//						break;
//							
//					sleep(waitingTime);
//				}
				
				
				
				

				while (true)
				{
//					updateHighLow();
//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					if (shutdownLong(currentStairIndex))
						return;
					
					cutLoss = Math.min(Math.min(refLow, XMLWatcher.stairs.get(currentStairIndex).refLow), XMLWatcher.stairs.get(currentStairIndex).value - 10);

					double reward = getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - Global.getCurrentPoint();
					double risk = Global.getCurrentPoint() - cutLoss;

					double rr = reward / risk;

					profitRange = reward;
					
					if (2 < rr && rr < 3 && risk <100 && stealing && reward > 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}
					
					if (1.5 < rr && rr < 2 && risk < 100 && !stealing && reward > 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					
					

					sleep(waitingTime);

				}
				
				
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				trailingDown(2);
				
				if (Global.getCurrentPoint() < cutLoss + 10)
				{
					Global.addLog("Too close to cutLoss");
					return;
				}

				longContract();
				
				if (refLow < XMLWatcher.stairs.get(currentStairIndex).refLow)
					XMLWatcher.stairs.get(currentStairIndex).refLow = refLow;
				
				Global.updateCSV();
				Global.addLog("Ref Low: " + refLow);
				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			} else if (
					GetData.getLongTB().getPreviousCandle(1).getClose() > XMLWatcher.stairs.get(currentStairIndex).value && 
					GetData.getLongTB().getLatestCandle().getClose() < XMLWatcher.stairs.get(currentStairIndex).value)
			{
				
				if (localShutdownLongIndex == currentStairIndex)
					continue;
				else
					localShutdownLongIndex = -1;

				if (!XMLWatcher.stairs.get(currentStairIndex).selling || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Break out " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Short)");
				Global.addLog("ST1 Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				
				if (shutdownShort(currentStairIndex))
					return;
				
//				Global.addLog("Waiting for a refHigh");
//				
//				while(!GetData.tinyHL.findingHigh || !isOrderTime())
//				{
//					if (shutdownShort(currentStairIndex))
//						return;
//					
//					if (Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value - 50)
//					{
//						Global.addLog("Left");
//						return;
//					}
//					
//					sleep(waitingTime);
//				}
//				
//				if (GetData.tinyHL.refHigh > refHigh)
//					refHigh = GetData.tinyHL.refHigh;
//
//				Global.addLog("Waiting for a tiny drop");
//
//				while(true)
//				{
//				
//					if(shutdownShort(currentStairIndex))
//						return;
//					
//					if (GetData.getShortTB().getLatestCandle().getClose() < GetData.getShortTB().getLatestCandle().getOpen() - 5
//							&& GetData.getShortTB().getLatestCandle().getClose() < XMLWatcher.stairs.get(currentStairIndex).value)
//						break;
//		
//					sleep(waitingTime);
//				}
				


				

				while (true)
				{

					if(shutdownShort(currentStairIndex))
						return;
					
					cutLoss = Math.max(Math.max(refHigh, XMLWatcher.stairs.get(currentStairIndex).refHigh), XMLWatcher.stairs.get(currentStairIndex).value + 10);
//					currentStair = XMLWatcher.stairs.get(currentStairIndex);


					double reward = Global.getCurrentPoint() - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value);
					double risk = cutLoss - Global.getCurrentPoint();

					double rr = reward / risk;

					profitRange = reward;
					
					if (2 < rr && rr < 3 && risk <100 && stealing && reward > 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}
					
					if (1.5 < rr && rr < 2 && risk < 100 && !stealing && reward > 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}


					sleep(waitingTime);

				}

				
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));
				
				trailingUp(2);
				
				if (Global.getCurrentPoint() > cutLoss - 10)
				{
					Global.addLog("Too close to cutLoss");
					return;
				}

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

		double stair = XMLWatcher.stair;

//		updateExpectedProfit(10);

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

			if (stair != 0 && tempCutLoss > stair && GetData.getShortTB().getLatestCandle().getClose() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

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
		double stair = XMLWatcher.stair;

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

			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

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

			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

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
	
	@Override
	void updateHighLow()
	{
		double refPoint = GetData.getLongTB().getLatestCandle().getClose();
		
		
		
		if (refPoint > refHigh)
			refHigh = refPoint;
		else if (refPoint < refLow)
			refLow = refPoint;
		
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}