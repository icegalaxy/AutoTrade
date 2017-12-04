package net.icegalaxy;

//Use the OPEN Line

public class RuleRR extends Rules
{

	OHLC currentOHLC;
	double cutLoss;
	
	double refHL;


	public RuleRR(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(91800, 115800, 130100, 160000, 171800, 230000); // need to
																		// observe
																		// the
																		// first
																		// 3min
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0
		// || Global.balance < -30
		)
			return;

		// if cutLoss, shutdown the ohlc
		if (shutdown)
		{
			for (int i = 0; i < XMLWatcher.ohlcs.length; i++)
			{
				if (currentOHLC.name.equals(XMLWatcher.ohlcs[i].name))
					XMLWatcher.ohlcs[i].shutdown = true;
			}
			shutdown = false;
		}

		// stair should not be reseted in this area or it wont function
		// if (XMLWatcher.stair != 0) XMLWatcher.updateIntraDayXML("stair",
		// "0");

		// for (OHLC item : XMLWatcher.ohlcs)

		for (int i = 0; i < XMLWatcher.ohlcs.length; i++)
		{
			currentOHLC = XMLWatcher.ohlcs[i];
			// setOrderTime(item.getOrderTime());

			if (Global.getNoOfContracts() != 0)
				return;

			if (currentOHLC.cutLoss == 0)
				continue;

			if (currentOHLC.shutdown)
				continue;

			if (getTimeBase().getEma5().getEMA() > currentOHLC.cutLoss
					&& currentOHLC.stopEarn > currentOHLC.cutLoss && Global.getCurrentPoint() < currentOHLC.cutLoss + 10
					&& Global.getCurrentPoint() > currentOHLC.cutLoss)
			{

				Global.addLog("Reached " + currentOHLC.name);
				
				refHL = getTimeBase().getLatestCandle().getOpen();
				
						
						
				
//				int currentShortCandleSize = GetData.getShortTB().getCandles().size();
//				int currentLongCandleSize = GetData.getLongTB().getCandles().size();

//				waitForANewCandle(GetData.getShortTB(), currentShortCandleSize, true);
				
				waitForANewCandle();
				
				if (getTimeBase().getLatestCandle().isYinCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();
				
				//else 跟上面
				

//				updateHighLow();

				while (Global.isRapidDrop() || getTimeBase().getLatestCandle().getClose() - getTimeBase().getLatestCandle().getOpen() < 5)
				{

					updateHighLow();
					
//					if (Global.getCurrentPoint() < currentOHLC.cutLoss)
//					{
//						Global.addLog("Touched " + currentOHLC.name);
//						break;
//					}

					if (isDownTrend())
					{
						Global.addLog("Down Trend");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

//					if (GetData.getShortTB().getEma5().getEMA() < currentOHLC.cutLoss)
//					{
//						Global.addLog("EMA5 out of range");
//						XMLWatcher.ohlcs[i].shutdown = true;
//						return;
//					}

					if (refLow < currentOHLC.cutLoss - 10)
					{
						Global.addLog("refLow out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}
					
//					if (Global.getCurrentPoint() > refLow + 20
//							&& refLow < currentOHLC.cutLoss)
//						{
//							Global.addLog("Rebounded 20 points");
//							break;
//						}
					
//					if (GetData.getShortTB().getPreviousCandle(1).isYinCandle()
//							&& GetData.getShortTB().getLatestCandle().getClose() > 
//								GetData.getShortTB().getPreviousCandle(1).getOpen())
//					{
//						Global.addLog("1min break previous open");
//						break;			
//					}
					

					sleep(waitingTime);
				}
				
				if (getTimeBase().getLatestCandle().getClose() < refHL)
				{
					Global.addLog("Not enough energy, wait for next time");
					waitForANewCandle();
					return;
					//Not shutting down
				}

				if (Global.getCurrentPoint() > currentOHLC.cutLoss + 20)
					Global.addLog("Rise to fast, waiting for a pull back");

				while (Global.getCurrentPoint() > currentOHLC.cutLoss + 20 || Global.isRapidDrop())
				{

					updateHighLow();

					if (Global.getCurrentPoint() > refLow + (currentOHLC.stopEarn - refLow) * 0.7)
					{
						Global.addLog("Too far away");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					sleep(waitingTime);
				}

				trailingDown(2);

				if (refLow < currentOHLC.cutLoss - 10)
				{
					Global.addLog("refLow out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				longContract();
				Global.addLog("Ref Low: " + refLow);
				
				cutLoss = Math.min(refLow -20, currentOHLC.cutLoss - 10);
				
				Global.addLog("OHLC: " + currentOHLC.name);
				return;

			} else if (getTimeBase().getEma5().getEMA() < currentOHLC.cutLoss
					&& currentOHLC.stopEarn < currentOHLC.cutLoss && Global.getCurrentPoint() > currentOHLC.cutLoss - 10
					&& Global.getCurrentPoint() < currentOHLC.cutLoss)
			{

				Global.addLog("Reached " + currentOHLC.name);
				
				refHL = getTimeBase().getLatestCandle().getOpen();
				
//				int currentShortCandleSize = GetData.getShortTB().getCandles().size();
//				int currentLongCandleSize = GetData.getLongTB().getCandles().size();

//				waitForANewCandle(GetData.getShortTB(), currentShortCandleSize, true);
				
				waitForANewCandle();
				
				if (getTimeBase().getLatestCandle().isYangCandle())
					refHL = getTimeBase().getLatestCandle().getOpen();

//				updateHighLow();

				while (Global.isRapidRise() || getTimeBase().getLatestCandle().getOpen() - getTimeBase().getLatestCandle().getClose() < 5)
				{

					updateHighLow();
					
//					if (Global.getCurrentPoint() > currentOHLC.cutLoss)
//					{
//						Global.addLog("Touched " + currentOHLC.name);
//						break;
//					}

					if (isUpTrend())
					{
						Global.addLog("Up Trend");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

//					if (GetData.getShortTB().getEma5().getEMA() > currentOHLC.cutLoss)
//					{
//						Global.addLog("EMA5 out of range");
//						XMLWatcher.ohlcs[i].shutdown = true;
//						return;
//					}

					if (refHigh > currentOHLC.cutLoss + 10)
					{
						Global.addLog("RefHigh out of range");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}
					
//					if (Global.getCurrentPoint() < refHigh - 20
//							&& refHigh > currentOHLC.cutLoss)
//						{
//							Global.addLog("Rebounded 20 points");
//							break;
//						}
					
//					if (GetData.getShortTB().getPreviousCandle(1).isYangCandle()
//							&& GetData.getShortTB().getLatestCandle().getClose() < 
//								GetData.getShortTB().getPreviousCandle(1).getOpen())
//					{
//						Global.addLog("1min break previous open");
//						break;			
//					}

					sleep(waitingTime);
				}
				
				if (getTimeBase().getLatestCandle().getClose() > refHL)
				{
					Global.addLog("Not enough energy, wait for next time");
					waitForANewCandle();
					return;
					//Not shutting down
				}

				if (Global.getCurrentPoint() < currentOHLC.cutLoss - 20)
					Global.addLog("Drop to fast, waiting for a pull back");

				while (Global.getCurrentPoint() < currentOHLC.cutLoss - 20 || Global.isRapidRise())
				{

					updateHighLow();

					if (Global.getCurrentPoint() < refHigh - (refHigh - currentOHLC.stopEarn) * 0.7)
					{
						Global.addLog("Too far away");
						XMLWatcher.ohlcs[i].shutdown = true;
						return;
					}

					sleep(waitingTime);
				}

				trailingUp(2);

				if (refHigh > currentOHLC.cutLoss + 10)
				{
					Global.addLog("RefHigh out of range");
					XMLWatcher.ohlcs[i].shutdown = true;
					return;
				}

				shortContract();
				Global.addLog("Ref High: " + refHigh);
				
				cutLoss = Math.max(refHigh + 20, currentOHLC.cutLoss + 10);
				
				Global.addLog("OHLC: " + currentOHLC.name);
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

			// return Math.max(20, buyingPoint - currentOHLC.cutLoss + 30);

			// just in case, should be stopped by tempCutLoss first
			return Math.max(10, buyingPoint - cutLoss);
		} else
		{
			// first profit then loss
			// if (tempCutLoss > currentOHLC.cutLoss + 10 && refLow <
			// currentOHLC.cutLoss - 30)
			// tempCutLoss = currentOHLC.cutLoss + 10;


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
			// return Math.max(20, currentOHLC.cutLoss - buyingPoint + 30);

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

			if (GetData.getLongTB().getLatestCandle().getLow() > tempCutLoss && tempCutLoss < currentOHLC.stopEarn)
				tempCutLoss = Math.min(currentOHLC.stopEarn, GetData.getLongTB().getLatestCandle().getLow());

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

			if (GetData.getLongTB().getLatestCandle().getHigh() < tempCutLoss && tempCutLoss > currentOHLC.stopEarn)
				tempCutLoss = Math.max(currentOHLC.stopEarn, GetData.getLongTB().getLatestCandle().getHigh());

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
				shutdown = true;
			}
			else if (Global.getCurrentPoint() < tempCutLoss)
				closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() > buyingPoint - 5)
			{
				closeContract(className + ": Break even, long @ " + Global.getCurrentAsk());
				shutdown = true;
			}
			else if (Global.getCurrentPoint() > tempCutLoss)
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

			if (refLow < currentOHLC.cutLoss - 20)
			{
				shutdown = true;
				return Math.min(20, refHigh - buyingPoint - 5);
			}

			if (refLow < currentOHLC.cutLoss - 10)
			{
				// Global.addLog("Line unclear, trying to take little profit");
				shutdown = true;
				return 30;
			}
			
			//Try to take profit if blocked by EMA
			if (GetData.getLongTB().getEma50().getEMA() - buyingPoint > 50)
			{
				return GetData.getLongTB().getEma50().getEMA() - buyingPoint;
			}else if (GetData.getLongTB().getEma250().getEMA() - buyingPoint > 50)
			{
				return GetData.getLongTB().getEma250().getEMA() - buyingPoint;
			}
			
			return Math.max(10, currentOHLC.stopEarn - buyingPoint - 10);
		} else
		{

			if (refHigh > currentOHLC.cutLoss + 20)
			{
				shutdown = true;
				return Math.min(20, buyingPoint - refLow - 5);
			}

			if (refHigh > currentOHLC.cutLoss + 10)
			{
				// Global.addLog("Line unclear, trying to take little profit");
				shutdown = true;
				return 30;
			}
			
			//Try to take profit if blocked by EMA
			if (buyingPoint - GetData.getLongTB().getEma50().getEMA() > 50)
			{
				return buyingPoint - GetData.getLongTB().getEma50().getEMA();
			}else if (buyingPoint - GetData.getLongTB().getEma250().getEMA() > 50)
			{
				return buyingPoint - GetData.getLongTB().getEma250().getEMA();
			}
			
			return Math.max(10, buyingPoint - currentOHLC.stopEarn - 10);
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
				if (GetData.getShortTB().getLatestCandle().getClose() > GetData.getShortTB().getPreviousCandle(1).getOpen())
				{
					Global.addLog("Break previous open");
					break;	
				}
			}
			
		}

	}

	// @Override
	// public void trendReversedAction()
	// {
	//
	// trendReversed = true;
	// }

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getShortTB();
	}
}