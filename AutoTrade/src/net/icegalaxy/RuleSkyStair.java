package net.icegalaxy;

import java.util.ArrayList;

//Use the OPEN Line

public class RuleSkyStair extends Rules
{
	// Stair currentStair;
	int currentStairIndex;
//	Stair currentStair;
	ArrayList<Integer> shutdownIndex;
	double cutLoss;
	double refHL;
	int reActivatePeriod = 10000;
	int EMATimer;

	public RuleSkyStair(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 143000, 160000, 203000, 233000); // need to
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
			if (GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value + 3
					&& Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value + 10
					&& Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value)
			{

				if (!XMLWatcher.stairs.get(currentStairIndex).buying || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Long)");
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYinCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				// waiting for a Yang candle
				while (Global.isRapidDrop()
						|| getTimeBase().getLatestCandle().getClose() - getTimeBase().getLatestCandle().getOpen() < 5
						|| Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value + 10)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);

					updateHighLow();

					// if (isDownTrend())
					// {
					// Global.addLog("Down Trend");
					// shutdown = true;
					// return;
					// }

					// if (refLow < currentStair.value - 10)
					// {
					// Global.addLog("refLow out of range");
					// currentStair.buying = false;
					// currentStair.reActivateTime = GetData.getTimeInt() +
					// reActivatePeriod;
					// shutdownIndex.add(i);
					// Global.updateCSV();
					//// shutdown = true;
					// return;
					// }

					if (GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value)
					{
						Global.addLog("M5_EMA out of range");
						XMLWatcher.stairs.get(currentStairIndex).buying = false;
						shutdownStair(currentStairIndex);
						// shutdown = true;
						return;
					}

					if (Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value - 50)
					{
						Global.addLog("Current point out of range");
						XMLWatcher.stairs.get(currentStairIndex).buying = false;
						shutdownStair(currentStairIndex);
						// shutdown = true;
						return;
					}

					sleep(waitingTime);
				}

				// check energy
				if (getTimeBase().getLatestCandle().getClose() < refHL
						|| getTimeBase().getLatestCandle().getClose() < getTimeBase().getLatestCandle().getOpen() + 5)
				{
					Global.addLog("Not enough energy, wait for next time");

					for (int x = 0; x < 5; x++)
					{

//						currentStair = XMLWatcher.stairs.get(currentStairIndex);

						waitForANewCandle();
						if (GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value)
						{
							Global.addLog("M5_EMA out of range");
							XMLWatcher.stairs.get(currentStairIndex).buying = false;
							shutdownStair(currentStairIndex);
							return;
							// shutting down
						}

						if (Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value - 50)
						{
							Global.addLog("Current point out of range");
							XMLWatcher.stairs.get(currentStairIndex).buying = false;
							shutdownStair(currentStairIndex);
							return;
						}

					}
					return;
					// Not shutting down
				}

				// if (Global.getCurrentPoint() > currentStair.value + 20)
				// Global.addLog("Rise to fast, waiting for a pull back");

				cutLoss = Math.min(refLow - 20, XMLWatcher.stairs.get(currentStairIndex).value - 10);
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				while (true)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					updateHighLow();

					double reward = getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value) - Global.getCurrentPoint();
					double risk = Global.getCurrentPoint() - cutLoss;

					double rr = reward / risk;

					if (rr > 2 
							&& Global.getCurrentPoint() - cutLoss < 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.7)
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
				Global.updateCSV();
				Global.addLog("Ref Low: " + refLow);

				cutLoss = Math.min(refLow - 20, XMLWatcher.stairs.get(currentStairIndex).value - 10);

				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			} else if (GetData.getLongTB().getEma5().getEMA() < XMLWatcher.stairs.get(currentStairIndex).value - 3
					&& Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value - 10
					&& Global.getCurrentPoint() < XMLWatcher.stairs.get(currentStairIndex).value)
			{

				if (!XMLWatcher.stairs.get(currentStairIndex).selling || XMLWatcher.stairs.get(currentStairIndex).shutdown)
					continue;

				Global.addLog("Reached " + XMLWatcher.stairs.get(currentStairIndex).lineType + " @ " + XMLWatcher.stairs.get(currentStairIndex).value + " (Short)");
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYangCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				// updateHighLow();

				while (Global.isRapidRise()
						|| getTimeBase().getLatestCandle().getOpen() - getTimeBase().getLatestCandle().getClose() < 5
						|| Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value - 10)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);

					updateHighLow();

					// if (isUpTrend())
					// {
					// Global.addLog("Up Trend");
					// shutdown = true;
					// return;
					// }

					//
					// if (refHigh > currentStair.value + 10)
					// {
					// Global.addLog("RefHigh out of range");
					// currentStair.selling = false;
					// currentStair.reActivateTime = GetData.getTimeInt() +
					// reActivatePeriod;;
					// shutdownIndex.add(i);
					// Global.updateCSV();
					//// shutdown = true;
					// return;
					// }

					if (GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value)
					{
						Global.addLog("M5_EMA out of range");
						XMLWatcher.stairs.get(currentStairIndex).selling = false;
						shutdownStair(currentStairIndex);
						return;
					}

					if (Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value + 50)
					{
						Global.addLog("Current point out of range");
						XMLWatcher.stairs.get(currentStairIndex).selling = false;
						shutdownStair(currentStairIndex);
						return;
					}

					sleep(waitingTime);
				}

				if (getTimeBase().getLatestCandle().getClose() > refHL
						|| getTimeBase().getLatestCandle().getClose() > getTimeBase().getLatestCandle().getOpen() - 5)
				{
					Global.addLog("Not enough energy, wait for next time");

					for (int x = 0; x < 5; x++)
					{

//						currentStair = XMLWatcher.stairs.get(currentStairIndex);

						waitForANewCandle();
						if (GetData.getLongTB().getEma5().getEMA() > XMLWatcher.stairs.get(currentStairIndex).value)
						{
							Global.addLog("M5_EMA out of range");
							XMLWatcher.stairs.get(currentStairIndex).selling = false;
							shutdownStair(currentStairIndex);
							return;
						}

						if (Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value + 50)
						{
							Global.addLog("Current point out of range");
							XMLWatcher.stairs.get(currentStairIndex).selling = false;
							shutdownStair(currentStairIndex);
							return;
						}
					}

					return;
					// Not shutting down
				}

				// if (Global.getCurrentPoint() < currentStair.value - 20)
				// Global.addLog("Drop to fast, waiting for a pull back");

				cutLoss = Math.max(refHigh + 20, XMLWatcher.stairs.get(currentStairIndex).value + 10);
				Global.addLog("Cut loss: " + cutLoss);
				Global.addLog("Stop Earn: " + getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value));

				while (true)
				{

//					currentStair = XMLWatcher.stairs.get(currentStairIndex);
					updateHighLow();

					double reward = Global.getCurrentPoint() - getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value);
					double risk = cutLoss - Global.getCurrentPoint();

					double rr = reward / risk;

					if (rr > 2
							&& cutLoss - Global.getCurrentPoint() < 50)
					{
						Global.addLog("RR= " + rr);
						break;
					}

					if (rr < 0.7)
					{
						Global.addLog("RR= " + rr);
						XMLWatcher.stairs.get(currentStairIndex).selling = false;
						shutdownStair(currentStairIndex);
						return;
					}

					sleep(waitingTime);

				}

				trailingUp(2);

				if (Global.getCurrentPoint() > XMLWatcher.stairs.get(currentStairIndex).value + 10)
				{
					Global.addLog("Current point out of range");
					XMLWatcher.stairs.get(currentStairIndex).selling = false;
					shutdownStair(currentStairIndex);
					return;
				}

				shortContract();
				Global.updateCSV();
				Global.addLog("Ref High: " + refHigh);

				cutLoss = Math.max(refHigh + 20, XMLWatcher.stairs.get(currentStairIndex).value + 10);

				Global.addLog("OHLC: " + XMLWatcher.stairs.get(currentStairIndex).lineType);
				return;

			}

		}
	}

	private void shutdownStair(int i)
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

		updateExpectedProfit(10);

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

			if (GetData.getLongTB().getLatestCandle().getLow() > tempCutLoss
					&& tempCutLoss < getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
				tempCutLoss = Math.min(getLongStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getLongTB().getLatestCandle().getLow());

			// if (GetData.getLongTB().getEMA(5) <
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (stair != 0 && tempCutLoss > stair && Global.getCurrentPoint() < stair)
			{
				Global.addLog("Stair updated: " + stair);
				tempCutLoss = stair;
			}

			if (GetData.getLongTB().getLatestCandle().getHigh() < tempCutLoss
					&& tempCutLoss > getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value))
				tempCutLoss = Math.max(getShortStopEarn(XMLWatcher.stairs.get(currentStairIndex).value),
						GetData.getLongTB().getLatestCandle().getHigh());

			// if (GetData.getLongTB().getEMA(5) >
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 0;
		}

	}

	double getLongStopEarn(double value)
	{

		double stopEarn = 99999;

		for (int j = 0; j < XMLWatcher.stairs.size(); j++)
		{
			if (XMLWatcher.stairs.get(j).value - value + value < stopEarn && XMLWatcher.stairs.get(j).value - value > 0)

				stopEarn = XMLWatcher.stairs.get(j).value;

		}

//		if (TimePeriodDecider.nightOpened)
//			return value + 50;

		if (stopEarn == 99999) // for the Max or Min of stair
			return value + 100;

		return Math.max(stopEarn, value + 50);
	}

	double getShortStopEarn(double value)
	{

		double stopEarn = 99999;

		for (int j = 0; j < XMLWatcher.stairs.size(); j++)
		{
			if (value - XMLWatcher.stairs.get(j).value + value < stopEarn && value - XMLWatcher.stairs.get(j).value > 0)

				stopEarn = XMLWatcher.stairs.get(j).value;

		}

//		if (TimePeriodDecider.nightOpened)
//			return value - 50;

		if (stopEarn == 99999) // for the Max or Min of stair
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

		double refPt = 0;

		// refPt = GetData.getShortTB().getLatestCandle().getClose();

		refPt = Global.getCurrentPoint();

		if (Global.getNoOfContracts() > 0 && refPt < tempCutLoss)
		{

			if (getProfit() > 5)
			{
				stopEarn();
				return;
			}

			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			XMLWatcher.stairs.get(currentStairIndex).buying = false;
			XMLWatcher.stairs.get(currentStairIndex).reActivateTime = GetData.getTimeInt() + reActivatePeriod;
			shutdownIndex.add(currentStairIndex);
			Global.updateCSV();
			// shutdown = true;

		} else if (Global.getNoOfContracts() < 0 && refPt > tempCutLoss)
		{

			if (getProfit() > 5)
			{
				stopEarn();
				return;
			}

			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			XMLWatcher.stairs.get(currentStairIndex).selling = false;
			XMLWatcher.stairs.get(currentStairIndex).reActivateTime = GetData.getTimeInt() + reActivatePeriod;
			shutdownIndex.add(currentStairIndex);
			Global.updateCSV();
			// shutdown = true;
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