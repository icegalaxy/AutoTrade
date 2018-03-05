package net.icegalaxy;

import java.util.ArrayList;

//Use the OPEN Line

public class RuleSkyStair extends Rules
{
	Stair currentOHLC;
	int currentStairIndex;
	ArrayList<Integer> shutdownIndex;
	double cutLoss;
	double refHL;
	int reActivatePeriod = 10000;
	int EMATimer;

	public RuleSkyStair(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 130100, 160000, 171800, 230000); // need to
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

		
		//RE-activate after 1hr
		if (shutdownIndex.size() > 0)
		{
			for (int i=0; i<shutdownIndex.size(); i++)
			{
				if (GetData.getTimeInt() > XMLWatcher.stairs.get(shutdownIndex.get(i)).reActivateTime)
				{
					XMLWatcher.stairs.get(shutdownIndex.get(i)).buying = true;	
					XMLWatcher.stairs.get(shutdownIndex.get(i)).selling = true;
					Global.addLog("Re-activate: " + XMLWatcher.stairs.get(shutdownIndex.get(i)).lineType + " @ " + XMLWatcher.stairs.get(shutdownIndex.get(i)).value);
					shutdownIndex.remove(i);
					Global.updateCSV();
				}
			}		
		}
		
		EMATimer++;
		
		if (EMATimer > 60) //don't want to check too frequently
		{
			checkEMA();
			updateEMAValue();
			EMATimer = 0;
		}
		
		for (int i = 0; i < XMLWatcher.stairs.size(); i++)
		{
				
			currentOHLC = XMLWatcher.stairs.get(i);
			currentStairIndex = i;

			if (Global.getNoOfContracts() != 0)
				return;

			if (currentOHLC.value == 0)
				continue;

//			if (currentOHLC.shutdown)
//				continue;

			//Long
			if (GetData.getLongTB().getEma5().getEMA() > currentOHLC.value
					&& Global.getCurrentPoint() < currentOHLC.value + 10
					&& Global.getCurrentPoint() > currentOHLC.value)
			{
				
				if (!currentOHLC.buying || currentOHLC.shutdown)
					continue;

				Global.addLog("Reached " + currentOHLC.lineType + " @ " + currentOHLC.value + " (Long)");

				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYinCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				//waiting for a Yang candle
				while (Global.isRapidDrop()
						|| getTimeBase().getLatestCandle().getClose() - getTimeBase().getLatestCandle().getOpen() < 5)
				{

					updateHighLow();

//					if (isDownTrend())
//					{
//						Global.addLog("Down Trend");
//						shutdown = true;
//						return;
//					}


					if (refLow < currentOHLC.value - 10)
					{
						Global.addLog("refLow out of range");
						XMLWatcher.stairs.get(i).buying = false;
						XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;
						shutdownIndex.add(i);
						Global.updateCSV();
//						shutdown = true;
						return;
					}


					sleep(waitingTime);
				}
				
				//check energy
				if (getTimeBase().getLatestCandle().getClose() < refHL
						&& getTimeBase().getLatestCandle().getClose() < getTimeBase().getLatestCandle().getOpen() + 5)
				{
					Global.addLog("Not enough energy, wait for next time");

					for (int x = 0; x < 5; x++)
					{
						waitForANewCandle();
						if (refLow < currentOHLC.value - 10)
						{
							Global.addLog("refLow out of range");
							XMLWatcher.stairs.get(i).buying = false;
							XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
							shutdownIndex.add(i);
							Global.updateCSV();
//							shutdown = true;
							return;
						}

					}
					return;
					// Not shutting down
				}

				
				if (Global.getCurrentPoint() > currentOHLC.value + 20)
					Global.addLog("Rise to fast, waiting for a pull back");

				
				while (Global.getCurrentPoint() > currentOHLC.value + 20 || Global.isRapidDrop())
				{

					updateHighLow();

					if (Global.getCurrentPoint() > refLow + (getLongStopEarn(currentOHLC.value) - refLow) * 0.7)
					{
						Global.addLog("Too far away");
//						shutdown = true;
						return;
					}

					sleep(waitingTime);
				}

				trailingDown(2);

				if (refLow < currentOHLC.value - 10)
				{
					Global.addLog("refLow out of range");
					XMLWatcher.stairs.get(i).buying = false;
					XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
					shutdownIndex.add(i);
					Global.updateCSV();
//					shutdown = true;
					return;
				}

				longContract();
				Global.updateCSV();
				Global.addLog("Ref Low: " + refLow);

				cutLoss = Math.min(refLow - 20, currentOHLC.value - 10);

				Global.addLog("OHLC: " + currentOHLC.lineType);
				return;

			} else if (GetData.getLongTB().getEma5().getEMA() < currentOHLC.value
					&& Global.getCurrentPoint() > currentOHLC.value - 10
					&& Global.getCurrentPoint() < currentOHLC.value)
			{
				
				if (!currentOHLC.selling || currentOHLC.shutdown)
					continue;

				Global.addLog("Reached " + currentOHLC.lineType + " @ " + currentOHLC.value + " (Short)");

				refHL = getTimeBase().getLatestCandle().getOpen();

				waitForANewCandle();

				if (getTimeBase().getLatestCandle().isYangCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

				// updateHighLow();

				while (Global.isRapidRise()
						|| getTimeBase().getLatestCandle().getOpen() - getTimeBase().getLatestCandle().getClose() < 5)
				{

					updateHighLow();


//					if (isUpTrend())
//					{
//						Global.addLog("Up Trend");
//						shutdown = true;
//						return;
//					}

					

					if (refHigh > currentOHLC.value + 10)
					{
						Global.addLog("RefHigh out of range");
						XMLWatcher.stairs.get(i).selling = false;
						XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
						shutdownIndex.add(i);
						Global.updateCSV();
//						shutdown = true;
						return;
					}


					sleep(waitingTime);
				}

				if (getTimeBase().getLatestCandle().getClose() > refHL
						&& getTimeBase().getLatestCandle().getClose() > getTimeBase().getLatestCandle().getOpen() - 5)
				{
					Global.addLog("Not enough energy, wait for next time");

					for (int x = 0; x < 5; x++)
					{
						waitForANewCandle();
						if (refHigh > currentOHLC.value + 10)
						{
							Global.addLog("RefHigh out of range");
							XMLWatcher.stairs.get(i).selling = false;
							XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
							shutdownIndex.add(i);
							Global.updateCSV();
//							shutdown = true;
							return;
						}
					}

					return;
					// Not shutting down
				}

				if (Global.getCurrentPoint() < currentOHLC.value - 20)
					Global.addLog("Drop to fast, waiting for a pull back");

				while (Global.getCurrentPoint() < currentOHLC.value - 20 || Global.isRapidRise())
				{

					updateHighLow();

					if (Global.getCurrentPoint() < refHigh - (refHigh - getShortStopEarn(currentOHLC.value) ) * 0.7)
					{
						Global.addLog("Too far away");
//						shutdown = true;
						return;
					}

					sleep(waitingTime);
				}

				trailingUp(2);

				if (refHigh > currentOHLC.value + 10)
				{
					Global.addLog("RefHigh out of range");
					XMLWatcher.stairs.get(i).selling = false;
					XMLWatcher.stairs.get(i).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
					shutdownIndex.add(i);
					Global.updateCSV();
//					shutdown = true;
					return;
				}

				shortContract();
				Global.updateCSV();
				Global.addLog("Ref High: " + refHigh);

				cutLoss = Math.max(refHigh + 20, currentOHLC.value + 10);

				Global.addLog("OHLC: " + currentOHLC.lineType);
				return;

			}

		}
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

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

			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

			// return Math.max(20, buyingPoint - currentOHLC.value + 30);

			// just in case, should be stopped by tempCutLoss first
			return Math.max(10, buyingPoint - cutLoss);
		} else
		{
			// first profit then loss
			// if (tempCutLoss > currentOHLC.value + 10 && refLow <
			// currentOHLC.value - 30)
			// tempCutLoss = currentOHLC.value + 10;

			if (tempCutLoss > cutLoss)
				tempCutLoss = cutLoss;

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
			// return Math.max(20, currentOHLC.value - buyingPoint + 30);

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

			if (GetData.getLongTB().getLatestCandle().getLow() > tempCutLoss && tempCutLoss < getLongStopEarn(currentOHLC.value))
				tempCutLoss = Math.min(getLongStopEarn(currentOHLC.value), GetData.getLongTB().getLatestCandle().getLow());

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

			if (GetData.getLongTB().getLatestCandle().getHigh() < tempCutLoss && tempCutLoss > getShortStopEarn(currentOHLC.value))
				tempCutLoss = Math.max(getShortStopEarn(currentOHLC.value), GetData.getLongTB().getLatestCandle().getHigh());

			// if (GetData.getLongTB().getEMA(5) >
			// GetData.getLongTB().getEMA(6))
			// tempCutLoss = 0;
		}

	}
	
	double getLongStopEarn(double value){
		
		double stopEarn = 99999;
		
		
		
		for (int i=0; i<XMLWatcher.stairs.size(); i++)
		{
			if (XMLWatcher.stairs.get(i).value - value < stopEarn
					&& XMLWatcher.stairs.get(i).value - value > 0)
				
				stopEarn = XMLWatcher.stairs.get(i).value;
			
		}
		
		if (TimePeriodDecider.nightOpened)
			return value + 50;
		
		if (stopEarn == 99999) 	//for the Max or Min of stair
			return value+100;
		
		return Math.max(stopEarn, value + 50);
	}
	
	double getShortStopEarn(double value){
		
		double stopEarn = 99999;
		
		for (int i=0; i<XMLWatcher.stairs.size(); i++)
		{
			if (value - XMLWatcher.stairs.get(i).value < stopEarn
					&& value - XMLWatcher.stairs.get(i).value > 0)
				
				stopEarn = XMLWatcher.stairs.get(i).value;
			
		}
		
		if (TimePeriodDecider.nightOpened)
			return value - 50;
		
		if (stopEarn == 99999) 	//for the Max or Min of stair
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
//				shutdown = true;
			} else if (Global.getCurrentPoint() < tempCutLoss)
				closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() > buyingPoint - 5)
			{
				closeContract(className + ": Break even, long @ " + Global.getCurrentAsk());
//				shutdown = true;
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

			if (refLow < currentOHLC.value - 20)
			{
				shutdown = true;
				return Math.min(20, refHigh - buyingPoint - 5);
			}

			if (refLow < currentOHLC.value - 10)
			{
				// Global.addLog("Line unclear, trying to take little profit");
				shutdown = true;
				return 30;
			}

			// Try to take profit if blocked by EMA
			if (GetData.getLongTB().getEma50().getEMA() - buyingPoint > 50)
			{
				return GetData.getLongTB().getEma50().getEMA() - buyingPoint;
			} else if (GetData.getLongTB().getEma250().getEMA() - buyingPoint > 50)
			{
				return GetData.getLongTB().getEma250().getEMA() - buyingPoint;
			}

			return Math.max(10, getLongStopEarn(currentOHLC.value) - buyingPoint - 10);
		} else
		{

			if (refHigh > currentOHLC.value + 20)
			{
				shutdown = true;
				return Math.min(20, buyingPoint - refLow - 5);
			}

			if (refHigh > currentOHLC.value + 10)
			{
				// Global.addLog("Line unclear, trying to take little profit");
				shutdown = true;
				return 30;
			}

			// Try to take profit if blocked by EMA
			if (buyingPoint - GetData.getLongTB().getEma50().getEMA() > 50)
			{
				return buyingPoint - GetData.getLongTB().getEma50().getEMA();
			} else if (buyingPoint - GetData.getLongTB().getEma250().getEMA() > 50)
			{
				return buyingPoint - GetData.getLongTB().getEma250().getEMA();
			}

			return Math.max(10, buyingPoint - getShortStopEarn(currentOHLC.value) - 10);
		}

	}

	public void waitForANewCandle(TimeBase tb, int currentSize, boolean buying)
	{

		currentSize = tb.getCandles().size();

		while (currentSize == tb.getCandles().size())
		{

			updateHighLow();
			sleep(waitingTime);

			if (buying)
			{
				if (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getPreviousCandle(1)
						.getOpen())
				{
					Global.addLog("Break previous open");
					break;
				}
			}

		}

	}
	
	@Override
	protected void cutLoss()
	{

		double refPt = 0;

		refPt = GetData.getShortTB().getLatestCandle().getClose();
		

		if (Global.getNoOfContracts() > 0 && refPt < tempCutLoss)
		{
			
			if (getProfit() > 5)
			{
				stopEarn();
				return;
			}
			
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			XMLWatcher.stairs.get(currentStairIndex).buying = false;
			XMLWatcher.stairs.get(currentStairIndex).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
			shutdownIndex.add(currentStairIndex);
			Global.updateCSV();
//			shutdown = true;
			
		} else if (Global.getNoOfContracts() < 0 && refPt > tempCutLoss)
		{
			
			if (getProfit() > 5)
			{
				stopEarn();
				return;
			}
			
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			XMLWatcher.stairs.get(currentStairIndex).selling = false;
			XMLWatcher.stairs.get(currentStairIndex).reActivateTime = GetData.getTimeInt() + reActivatePeriod;;
			shutdownIndex.add(currentStairIndex);
			Global.updateCSV();
//			shutdown = true;
		}
	}

	// @Override
	// public void trendReversedAction()
	// {
	//
	// trendReversed = true;
	// }
	
	private void checkEMA(){
		
		if (GetData.getLongTB().getEma5().getEMA() > GetData.getLongTB().getEma50().getEMA() + 10
				&& GetData.getLongTB().getEma50().getEMA() > GetData.getLongTB().getEma250().getEMA() + 10)
		{
			
			if (Global.getCurrentPoint() > GetData.getLongTB().getEma50().getEMA())
			{
				XMLWatcher.stairs.get(0).buying = true;
				XMLWatcher.stairs.get(0).selling = false;
				XMLWatcher.stairs.get(1).buying = true;
				XMLWatcher.stairs.get(1).selling = false;
			}else if (Global.getCurrentPoint() > GetData.getLongTB().getEma250().getEMA())
			{
				XMLWatcher.stairs.get(0).buying = false;
				XMLWatcher.stairs.get(0).selling = false;
				XMLWatcher.stairs.get(1).buying = true;
				XMLWatcher.stairs.get(1).selling = false;
			}else
			{
				XMLWatcher.stairs.get(0).buying = false;
				XMLWatcher.stairs.get(0).selling = false;
				XMLWatcher.stairs.get(1).buying = false;
				XMLWatcher.stairs.get(1).selling = false;
			}

		}else if (GetData.getLongTB().getEma5().getEMA() < GetData.getLongTB().getEma50().getEMA() - 10
				&& GetData.getLongTB().getEma50().getEMA() < GetData.getLongTB().getEma250().getEMA() - 10)
		{
			if (Global.getCurrentPoint() < GetData.getLongTB().getEma50().getEMA())
			{
				XMLWatcher.stairs.get(0).buying = false;
				XMLWatcher.stairs.get(0).selling = true;
				XMLWatcher.stairs.get(1).buying = false;
				XMLWatcher.stairs.get(1).selling = true;
			}else if (Global.getCurrentPoint() < GetData.getLongTB().getEma250().getEMA())
			{
				XMLWatcher.stairs.get(0).buying = false;
				XMLWatcher.stairs.get(0).selling = false;
				XMLWatcher.stairs.get(1).buying = false;
				XMLWatcher.stairs.get(1).selling = true;
			}else
			{
				XMLWatcher.stairs.get(0).buying = false;
				XMLWatcher.stairs.get(0).selling = false;
				XMLWatcher.stairs.get(1).buying = false;
				XMLWatcher.stairs.get(1).selling = false;
			}
			
		}else{
			XMLWatcher.stairs.get(0).buying = false;
			XMLWatcher.stairs.get(0).selling = false;
			XMLWatcher.stairs.get(1).buying = false;
			XMLWatcher.stairs.get(1).selling = false;
		}
	
	}
	
	private void updateEMAValue(){
		
		if (XMLWatcher.stairs.get(0).value != GetData.getLongTB().getEma50().getEMA())
			XMLWatcher.stairs.get(0).value = GetData.getLongTB().getEma50().getEMA();
		if (XMLWatcher.stairs.get(1).value != GetData.getLongTB().getEma250().getEMA())
			XMLWatcher.stairs.get(1).value = GetData.getLongTB().getEma250().getEMA();	
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}