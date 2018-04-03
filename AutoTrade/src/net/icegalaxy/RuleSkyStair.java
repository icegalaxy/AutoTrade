package net.icegalaxy;

import java.util.ArrayList;
import java.util.List;

//Use the OPEN Line

public class RuleSkyStair extends Rules
{
	// Stair currentStair;
	int currentStairIndex;
//	Stair currentStair;
	static ArrayList<Integer> shutdownIndex;
	double cutLoss;
	double refHL;
	static int reActivatePeriod = 10000;
	int EMATimer;

	public RuleSkyStair(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 131500, 160000, 173000, 1003000); // need to
																		// observe
																		// the
																		// first
																		// 3min
		shutdownIndex = new ArrayList<Integer>();
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;

		// RE-activate after 1hr
		if (shutdownIndex.size() > 0)
		{
			for (int i = 0; i < shutdownIndex.size(); i++)
			{
				if (GetData.getTimeInt() > XMLWatcher.stairs.get(shutdownIndex.get(i)).reActivateTime)
				{
					XMLWatcher.stairs.get(shutdownIndex.get(i)).buying = true;
					XMLWatcher.stairs.get(shutdownIndex.get(i)).selling = true;
					Global.addLog("Re-activate: " + XMLWatcher.stairs.get(shutdownIndex.get(i)).lineType + " @ "
							+ XMLWatcher.stairs.get(shutdownIndex.get(i)).value);
					shutdownIndex.remove(i);
					i--;
					Global.updateCSV();
				}
			}
		}

		for (int i = 0; i < XMLWatcher.stairs.size(); i++)
		{

			currentStairIndex = i;

//			currentStair = XMLWatcher.stairs.get(currentStairIndex);

			if (Global.getNoOfContracts() != 0)
				return;

			if (XMLWatcher.stairs.get(currentStairIndex).value == 0)
				continue;

			// if (currentStair.shutdown)
			// continue;

			// Long
			if (GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value + 50
					&& Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value + 20
					&& Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value)
			{
				
				

				if (!XMLWatcher.stairs.get(currentStairIndex).buying || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Long)");
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				Global.addLog("Waiting for a refLow");
				while(Global.getCurrentPoint() > GetData.refHigh - Global.getCurrentPoint() * 0.05)
				{
					if (shutdownLong(currentStairIndex))
						return;
					sleep(waitingTime);
				}
				
//				Global.addLog("RefLow: " + GetData.refLows.get(GetData.refLows.size()));
				
//				if (isDownTrend())
//				{
//					Global.addLog("Down Trend");
//					XMLWatcher.stairs.get(currentStairIndex).buying = false;
//					shutdownStair(currentStairIndex);
//					// shutdown = true;
//					return;
//				}
				
				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYinCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				// waiting for a Yang candle
				while (Global.isRapidDrop()
						|| getTimeBase().getLatestCandle().getClose() - getTimeBase().getLatestCandle().getOpen() < 5
						|| Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value)
				{
					
					

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					//dont need this beause EMA >50
//					while (GetData.getShortTB().getRSI() > 40)
//					{
//						if (shutdownLong(currentStairIndex))
//							return;
//						sleep(waitingTime);
//					}
					
					if (shutdownLong(currentStairIndex))
						return;

					sleep(waitingTime);
				}

				
				//wait 30% rise
				while(Global.getCurrentPoint() < GetData.getLatestLow() + (GetData.getLatestHigh() - GetData.getLatestLow()) * 0.3)
				{
					if (shutdownLong(currentStairIndex))
						return;
					
					sleep(waitingTime);
				}

				// if (Global.getCurrentPoint() > currentStair.value + 20)
				// Global.addLog("Rise to fast, waiting for a pull back");
				
				if (refLow < XMLWatcher.stairs.get(currentStairIndex).refLow)
					XMLWatcher.stairs.get(currentStairIndex).refLow = refLow;

				cutLoss = Math.min(XMLWatcher.stairs.get(currentStairIndex).refLow - 20, XMLWatcher.stairs.get(currentStairIndex).value - 10);
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

					if (rr > 2 
							&& Global.getCurrentPoint() - cutLoss < 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.5)
					{
						Global.addLog("RR= " + rr);
						XMLWatcher.stairs.get(currentStairIndex).buying = false;
						shutdownStair(currentStairIndex);
						return;
					}

					sleep(waitingTime);

				}

				trailingDown(2);

				if (Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value - 10)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.stairs.get(currentStairIndex).buying = false;
					shutdownStair(currentStairIndex);
					// shutdown = true;
					return;
				}

				longContract();
				
				if (refLow < XMLWatcher.stairs.get(currentStairIndex).refLow)
					XMLWatcher.stairs.get(currentStairIndex).refLow = refLow;
				
				Global.updateCSV();
				Global.addLog("Ref Low: " + refLow);

				cutLoss = Math.min(XMLWatcher.stairs.get(currentStairIndex).refLow - 20, XMLWatcher.stairs.get(currentStairIndex).value - 10);

				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			} else if (GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value - 50
					&& Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value - 20
					&& Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value)
			{

				if (!XMLWatcher.stairs.get(currentStairIndex).selling || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Short)");
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				Global.addLog("Waiting for a refHigh");
				while(Global.getCurrentPoint() < GetData.refLow + Global.getCurrentPoint() * 0.05)
				{
					if (shutdownShort(currentStairIndex))
						return;
					sleep(waitingTime);
				}
				
//				Global.addLog("RefHigh: " + GetData.refLows.get(GetData.refLows.size()));
				
//				if (isUpTrend())
//				{
//					Global.addLog("Up Trend");
//					XMLWatcher.stairs.get(currentStairIndex).selling = false;
//					shutdownStair(currentStairIndex);
//					// shutdown = true;
//					return;
//				}
				
				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYangCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				// updateHighLow();

				while (Global.isRapidRise()
						|| getTimeBase().getLatestCandle().getOpen() - getTimeBase().getLatestCandle().getClose() < 5
						|| Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value)
				{


					if(shutdownShort(currentStairIndex))
						return;

					sleep(waitingTime);
				}

				
				//wait 30% drop
				while(Global.getCurrentPoint()  > GetData.getLatestHigh() - (GetData.getLatestHigh() - GetData.getLatestLow()) * 0.3)
				{
					if(shutdownShort(currentStairIndex))
						return;
					sleep(waitingTime);
				}

				// if (Global.getCurrentPoint() < currentStair.value - 20)
				// Global.addLog("Drop to fast, waiting for a pull back");

				if(refHigh > XMLWatcher.stairs.get(currentStairIndex).refHigh)
					XMLWatcher.stairs.get(currentStairIndex).refHigh = refHigh;
				
				cutLoss = Math.max(XMLWatcher.stairs.get(currentStairIndex).refHigh + 20, XMLWatcher.stairs.get(currentStairIndex).value + 10);
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

					if (rr > 2
							&& cutLoss - Global.getCurrentPoint() < 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.5)
					{
						Global.addLog("RR= " + rr);
						XMLWatcher.stairs.get(currentStairIndex).selling = false;
						shutdownStair(currentStairIndex);
						return;
					}

					sleep(waitingTime);

				}

				trailingUp(2);

				if (refHigh > XMLWatcher.stairs.get(currentStairIndex).value + 50)
				{
					Global.addLog("RefHigh out of range");
					XMLWatcher.stairs.get(currentStairIndex).selling = false;
					shutdownStair(currentStairIndex);
					return;
				}

			
				
				shortContract();
				Global.updateCSV();
				Global.addLog("Ref High: " + refHigh);
				
				if(refHigh > XMLWatcher.stairs.get(currentStairIndex).refHigh)
					XMLWatcher.stairs.get(currentStairIndex).refHigh= refHigh;

				cutLoss = Math.max(XMLWatcher.stairs.get(currentStairIndex).refHigh + 20, XMLWatcher.stairs.get(currentStairIndex).value + 10);

				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			}

		}
	}

	private boolean shutdownShort(int currentStairIndex)
	{
		
		boolean shutdown = false;
		
		updateHighLow();


		if (GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value - 50)
		{
			Global.addLog("M5_EMA out of range");
			XMLWatcher.stairs.get(currentStairIndex).selling = false;
			shutdownStair(currentStairIndex);
			shutdown = true;
		}
		
		if (GetData.getShortTB().getRSI() < 40)
		{
			Global.addLog("RSI out of range");
			XMLWatcher.stairs.get(currentStairIndex).selling = false;
			shutdownStair(currentStairIndex);
			shutdown = true;
		}

		if (refHigh > XMLWatcher.stairs.get(currentStairIndex).value + 50)
		{
			Global.addLog("RefHigh out of range");
			XMLWatcher.stairs.get(currentStairIndex).selling = false;
			shutdownStair(currentStairIndex);
			shutdown = true;
		}
		
		return shutdown;
	}

	private boolean shutdownLong(int currentStairIndex)
	{
		boolean shutdown = false;
		
		updateHighLow();
		

		if (GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value + 50)
		{
			Global.addLog("M5_EMA out of range");
			XMLWatcher.stairs.get(currentStairIndex).buying = false;
			shutdownStair(currentStairIndex);
			shutdown = true;
		}
		
		if (GetData.getShortTB().getRSI() > 60)
		{
			Global.addLog("RSI out of range");
			XMLWatcher.stairs.get(currentStairIndex).buying = false;
			shutdownStair(currentStairIndex);
			shutdown = true;		
		}

		if (refLow < XMLWatcher.stairs.get(currentStairIndex).value - 50)
		{
			Global.addLog("RefLow out of range");
			XMLWatcher.stairs.get(currentStairIndex).buying = false;
			shutdownStair(currentStairIndex);
			shutdown = true;
		}
		
		return shutdown;
	}

	public static void shutdownStair(int i)
	{
		XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod * XMLWatcher.stairs.get(i).timesOfShutdown;
		XMLWatcher.stairs.get(i).timesOfShutdown++;
		shutdownIndex.add(i);
		Global.updateCSV();
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
		}else if (Global.getNoOfContracts() < 0)
		{
			
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
			
			if (GetData.getShortTB().getLatestCandle().getLow() < GetData.getLongTB().getEma5().getEMA()
					&& GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
			
			
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

			// update stair
			if (stair != 0 && tempCutLoss < stair && Global.getCurrentPoint() > stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss
					&& tempCutLoss < getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
				tempCutLoss = Math.min(getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getShortTB().getLatestCandle().getLow());

			// if (GetData.getLongTB().getEMA(5) <
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{
			
			if (GetData.getShortTB().getLatestCandle().getHigh() > GetData.getLongTB().getEma5().getEMA()
					&& GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
			
			
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

			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
				tempCutLoss = Math.max(getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getShortTB().getLatestCandle().getHigh());

			// if (GetData.getLongTB().getEMA(5) >
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 0;
		}

	}

	double getLongStopEarn(double value)
	{

		double stopEarn = 99999;
		List<Stair> stairs = XMLWatcher.stairs;

		for (int j = 0; j < stairs.size(); j++)
		{
			Stair stair = stairs.get(j);
//			if (!stair.buying || stair.shutdown)
//				continue;
			
			if (stair.value < stopEarn && stair.value - value > 0)

				stopEarn = stair.value;

		}

//		if (TimePeriodDecider.nightOpened)
//			return value + 50;

		if (stopEarn == 99999) // for the Max or Min of stair
			return value + 100;

		return Math.max(stopEarn, value + 50);
	}

	double getShortStopEarn(double value)
	{

		double stopEarn = 0;
		List<Stair> stairs = XMLWatcher.stairs;

		for (int j = 0; j < stairs.size(); j++)
		{
			Stair stair = stairs.get(j);
			
//			if (!stair.selling || stair.shutdown)
//				continue;
			
			if (stair.value > stopEarn && value - stair.value > 0)

				stopEarn = stair.value;

		}

//		if (TimePeriodDecider.nightOpened)
//			return value - 50;

		if (stopEarn == 0) // for the Max or Min of stair
			return value - 100;

		return Math.min(stopEarn, value - 50);
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

			return Math.max(10, getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - buyingPoint - 10);
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

			return Math.max(10, buyingPoint - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - 10);
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
		
		if(shutdown)
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
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}