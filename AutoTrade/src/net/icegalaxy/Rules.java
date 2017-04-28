package net.icegalaxy;

import java.util.ArrayList;



public abstract class Rules implements Runnable
{

	protected boolean hasContract;
	protected double tempCutLoss;
	protected double tempStopEarn;
	protected double refPt;
	protected double buyingPoint;
	private boolean globalRunRule;
	protected String className;
	double stopEarnPt;
	double cutLossPt;
	
	double refHigh;
	double refLow;

	public int lossTimes;

	private final float CUTLOSS_FACTOR = 5.0F;
	private final float STOPEARN_FACTOR = 5.0F;

	boolean usingMA20;
	boolean usingMA10;
	boolean usingMA5;
	boolean shutdown;

	private static float balance; // holding contracts �� balance

	// can use default trade time, just do not use the setTime method
	int morningOpen = 92000;
	int morningClose = 113000;
	int noonOpen = 130500;
	int noonClose = 160000;
	int nightOpen = 173000;
	int nightClose = 231500;

	public Rules(boolean globalRunRule)
	{

		this.globalRunRule = globalRunRule;
		this.className = this.getClass().getSimpleName();

	}

	@Override
	public void run()
	{

		if (!globalRunRule)
		{
			while (Global.isRunning())
			{
				sleep(1000);
			}
		} else
		{

			Global.addLog(className + " Activated");

			while (Global.isRunning())
			{

				usingMA20 = true;
				usingMA10 = true;
				usingMA5 = true;

				if (hasContract)
				{
					closeContract();
				} else
				{
					openContract();
				}

				sleep(1000);
			}
		}
	}

	protected boolean reachGreatProfitPt()
	{

		if (getStopEarnPt() < stopEarnPt)
			stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - stopEarnPt > buyingPoint; // it's
		// moving
		else if (Global.getNoOfContracts() < 0)
			return Global.getCurrentPoint() + stopEarnPt < buyingPoint;
		else
			return false;
	}

	// can choose not to set the night time
	public void setOrderTime(int[] orderTime)
	{
		this.morningOpen = orderTime[0];
		this.morningClose = orderTime[1];
		this.noonOpen = orderTime[2];
		this.noonClose = orderTime[3];
	}

	// can choose to set the night time
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen, int noonClose, int nightOpen,
			int nightClose)
	{
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
		this.nightOpen = nightOpen;
		this.nightClose = nightClose;
	}

	public boolean isOrderTime()
	{

		int time = TimePeriodDecider.getTime();

		// System.out.println("Check: " + time + morningOpen + morningClose +
		// noonOpen + noonClose);

		if (time > morningOpen && time < morningClose)
			return true;
		else if (time > noonOpen && time < noonClose)
			return true;
		else if (time > nightOpen && time < nightClose)
			return true;
		else
			return false;
	}

	protected void updateCutLoss()
	{

		if (getCutLossPt() < cutLossPt)
			cutLossPt = getCutLossPt();

		// if (getStopEarnPt() < stopEarnPt)
		// stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0)
		{
			// if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() - cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint - cutLossPt > tempCutLoss)
				tempCutLoss = buyingPoint - cutLossPt;

			// if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }

		} else if (Global.getNoOfContracts() < 0)
		{
			// if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() + cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint + cutLossPt < tempCutLoss)
				tempCutLoss = buyingPoint + cutLossPt;

			// if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }
		}
	}

	// protected void cutLoss() {
	// if (Global.getNoOfContracts() > 0
	// && Global.getCurrentPoint() < tempCutLoss) {
	// closeContract(className + ": CutLoss, short @ "
	// + Global.getCurrentBid());
	// shutdown = true;
	// } else if (Global.getNoOfContracts() < 0
	// && Global.getCurrentPoint() > tempCutLoss) {
	// closeContract(className + ": CutLoss, long @ "
	// + Global.getCurrentAsk());
	// shutdown = true;
	// }
	// }

	// Use the latest 1min close instead of current point
	protected void cutLoss()
	{

		double refPt = 0;

		if (isInsideDay())
			refPt = GetData.getLongTB().getLatestCandle().getClose();
		else
			refPt = GetData.getShortTB().getLatestCandle().getClose();

		if (Global.getNoOfContracts() > 0 && refPt < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && refPt > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
		}
	}

	void stopEarn()
	{
		if (Global.getNoOfContracts() > 0 && GetData.getShortTB().getLatestCandle().getClose() < tempCutLoss)
		{

			if (Global.getCurrentPoint() < buyingPoint)
			{
				cutLoss();
				return;
			}

			closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			if (lossTimes > 0)
				lossTimes--;

		} else if (Global.getNoOfContracts() < 0 && GetData.getShortTB().getLatestCandle().getClose() > tempCutLoss)
		{

			if (Global.getCurrentPoint() > buyingPoint)
			{
				cutLoss();
				return;
			}

			closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			if (lossTimes > 0)
				lossTimes--;
		}
	}

	public void closeContract(String msg)
	{
		boolean b = Sikuli.closeContract();
		Global.addLog(msg);
		Global.addLog("");
		Global.addLog("Current Balance: " + Global.balance + " points");
		Global.addLog("____________________");
		Global.addLog("");
		if (b)
			hasContract = false;
	}

	public void closeContract()
	{

		if (Global.getNoOfContracts() > 0)
		{
			tempCutLoss = buyingPoint - getCutLossPt();
			tempStopEarn = buyingPoint + getStopEarnPt();
		} else if (Global.getNoOfContracts() < 0)
		{
			tempCutLoss = buyingPoint + getCutLossPt();
			tempStopEarn = buyingPoint - getStopEarnPt();
		}

		stopEarnPt = getStopEarnPt();
		cutLossPt = 100; // �O�׫Y�O�I�A��ĤG��set���H�Pcut loss�Ӥj,
		// �O�ӫYMaximum

		Global.addLog("Enter loop of closeContract");
		
		while (!reachGreatProfitPt())
		{

			updateHighLow();
			updateCutLoss();
			cutLoss();

			// checkRSI();
			// checkDayHighLow();
			if (trendReversed())
			{
				trendReversedAction();
			}

			if (trendUnstable())
			{
				Global.addLog(className + ": Trend unstable");
				closeContract(className + ": Trend Unstable");
				return;
			}
			// if (maReversed())
			// return;

			if (Global.isForceSellTime())
			{
				Global.addLog(className + ": Force sell");
				closeContract("Force Sell");
				return;
			}

			if (Global.getNoOfContracts() == 0)
			{
				Global.addLog(className + ": Suddenly Global contract = 0");
				hasContract = false;
				break;
			}

			if (!hasContract){
				Global.addLog(className + ": Suddenly !hasContract");
				break;
			}
			sleep(1000);
		}
		
		Global.addLog(className + ": broke out the first loop");

		if (Global.getNoOfContracts() == 0)
		{
			hasContract = false;
			return;
		}

		if (!hasContract)
			return;

		// updateCutLoss();
		refPt = Global.getCurrentPoint();

		Global.addLog(className + ": Secure profit @ " + Global.getCurrentPoint());

		while (hasContract)
		{

			if (Global.getNoOfContracts() == 0)
			{
				hasContract = false;
				break;
			}

			if (trendReversed2())
				closeContract(className + ": TrendReversed2");

			if (Global.isForceSellTime())
			{
				closeContract("Force Sell");
				return;
			}

			updateCutLoss();
			cutLoss();

			updateStopEarn();
			stopEarn();

			// System.out.println("Temp Stop Earn" + tempCutLoss);

			sleep(1000);
		}
	}

	public void trendReversedAction()
	{
		Global.addLog(className + ": Trend reversed");
		
		if (getProfit() < 0)
		{
			shutdown = true;
		}
		closeContract(className + ": Trend Reversed");
	}
	
	private void updateHighLow()
	{
		if (Global.getCurrentPoint() > refHigh)
			refHigh = Global.getCurrentPoint();
		else if (Global.getCurrentPoint() < refLow)
			refLow = Global.getCurrentPoint();
		
	}

	boolean trendReversed2()
	{
		return false;
	}

	boolean trendUnstable()
	{
		return false;
	}

	protected float getAGAL()
	{

		GetData.getShortTB().getRSI(); // ���[�O�y����AGAL�Y���|����
		return (GetData.getShortTB().getAG() + GetData.getShortTB().getAL()); // �Y���O�׫Y���Y�n�εfShort
		// Period
		// ALAG��Ĺ��
	}

	public void shortContract()
	{

		// can't do it in sikuli, it stopped the forcesell
		if (!isOrderTime())
		{
			Global.addLog(className + ": not order time");
			return;
		}
		
		if (!Global.isConnectionOK())
		{
			Global.addLog(className + ": Connection probelm");
			return;
		}

		if (Global.getNoOfContracts() != 0)
		{
			Global.addLog(className + ": Has order already!");
			return;
		}

		boolean b = Sikuli.shortContract();
		if (!b)
		{
			Global.addLog(className + ": Fail to short");
			return;
		}
		hasContract = true;
		Global.addLog(className + ": Short @ " + Global.getCurrentBid());
		buyingPoint = Global.getCurrentBid();
		balance += buyingPoint;
	}

	public void longContract()
	{

		// can't do it in sikuli, it stopped the forcesell
		if (!isOrderTime())
		{
			Global.addLog(className + ": not order time");
			return;
		}
		
		if (!Global.isConnectionOK())
		{
			Global.addLog(className + ": Connection probelm");
			return;
		}

		if (Global.getNoOfContracts() != 0)
		{
			Global.addLog(className + ": Has order already!");
			return;
		}

		boolean b = Sikuli.longContract();
		if (!b)
		{
			Global.addLog(className + ": Fail to long");
			return;
		}
		hasContract = true;
		Global.addLog(className + ": Long @" + Global.getCurrentAsk());
		buyingPoint = Global.getCurrentAsk();
		balance -= buyingPoint;
	}

	public abstract void openContract();

	void updateStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss)
			{
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
				// usingMA20 = false;
				// usingMA10 = false;
				// usingMA5 = false;
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
			{
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
				// usingMA20 = false;
				// usingMA10 = false;
				// usingMA5 = false;
			}
		}

	}

	public boolean isInsideDay()
	{

		return Global.getCurrentPoint() > Global.getpLow() + 5 && Global.getCurrentPoint() < Global.getpHigh() - 5;

	}

	void secondStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{
			if (Global.getCurrentPoint() < GetData.getLongTB().getEMA(5))
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: Current Pt < EMA5");
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			if (Global.getCurrentPoint() > GetData.getLongTB().getEMA(5))
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: Current Pt > EMA5");

			}
		}

	}

	void thirdStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{
			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6))
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6))
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	double getCutLossPt()
	{
		return getAGAL() * CUTLOSS_FACTOR;
		// return GetData.getShortTB().getHL15().getFluctuation() /
		// CUTLOSS_FACTOR;
	}

	double getStopEarnPt()
	{
		return getAGAL() * STOPEARN_FACTOR;
		// return GetData.getShortTB().getHL15().getFluctuation() /
		// STOPEARN_FACTOR;
	}

	public void setName(String s)
	{
		className = s;
	}

	public static synchronized double getBalance()
	{
		if (Global.getNoOfContracts() > 0)
			return balance + Global.getCurrentPoint() * Global.getNoOfContracts();
		else if (Global.getNoOfContracts() < 0)
			return balance + Global.getCurrentPoint() * Global.getNoOfContracts();
		else
		{
			balance = 0;
			return balance;
		}
	}

	public static synchronized void setBalance(float balance)
	{
		Rules.balance = balance;
	}

	public abstract TimeBase getTimeBase();

	boolean maRising(int period)
	{
		return getTimeBase().isMARising(period, 1);
	}

	boolean maDropping(int period)
	{
		return getTimeBase().isMADropping(period, 1);
	}

	boolean emaRising(int period)
	{
		return getTimeBase().isEMARising(period, 1);
	}

	boolean emaDropping(int period)
	{
		return getTimeBase().isEMADropping(period, 1);
	}

	boolean trendReversed()
	{

		// double slope = 0;
		// double longSlope = 0;
		//
		// if (Global.getNoOfContracts() > 0){
		// if (GetData.getSec10TB().getMainDownRail()
		// .getSlope() != 100)
		// slope = GetData.getSec10TB().getMainDownRail()
		// .getSlope();
		//
		// if (getTimeBase().getMainUpRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainUpRail().getSlope();
		//
		// }
		// if (Global.getNoOfContracts() < 0){
		//
		// if (GetData.getSec10TB().getMainUpRail().getSlope() != 100)
		// slope = GetData.getSec10TB().getMainUpRail().getSlope();
		//
		// if (getTimeBase().getMainDownRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainDownRail().getSlope();
		// }
		// return slope > 5 && slope > longSlope*2;

		return false;

	}

	protected void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public double getProfit()
	{
		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		else
			return buyingPoint - Global.getCurrentPoint();
	}

	public boolean isUpTrend2()
	{
		return GetData.getEma100().getEMA() > GetData.getEma250().getEMA()
				&& GetData.getEma250().getEMA() > GetData.getEma1200().getEMA()
				&& Global.getCurrentPoint() > GetData.getEma250().getEMA();
//				&& GetData.getEma25().getEMA() > GetData.getEma50().getEMA()
//				&& GetData.getEma50().getEMA() > GetData.getEma100().getEMA();
				
	}

	public boolean isDownTrend2()
	{
		return GetData.getEma100().getEMA() < GetData.getEma250().getEMA()
				&& GetData.getEma250().getEMA() < GetData.getEma1200().getEMA()
				&& Global.getCurrentPoint() < GetData.getEma250().getEMA();
//				&& GetData.getEma25().getEMA() < GetData.getEma50().getEMA() 
//				&& GetData.getEma50().getEMA() < GetData.getEma100().getEMA() ;
		
	}
	
	public double getHighestMA()
	{

		double highestMA = 0;

		for (int i = 0; i < get4MAs().size(); i++)
		{
			highestMA = Math.max(highestMA, get4MAs().get(i));
		}
		return highestMA;
	}

	public double getLowestMA()
	{

		double lowestMA = 99999;

		for (int i = 0; i < get4MAs().size(); i++)
		{
			lowestMA = Math.min(lowestMA, get4MAs().get(i));
		}
		return lowestMA;
	}

	private ArrayList<Double> get4MAs()
	{
		ArrayList<Double> mas = new ArrayList<Double>();

		mas.add(GetData.getEma25().getEMA());
		mas.add(GetData.getEma50().getEMA());
		mas.add(GetData.getEma100().getEMA());
		mas.add(GetData.getEma250().getEMA());

		return mas;

	}
	
	// Danny �l�ȥ�e�w��V
	public boolean isUpTrend()
	{
		return GetData.getM15TB().getMA(20) > GetData.getM15TB().getEMA(50)
				&& GetData.getLongTB().getEMA(50) > GetData.getLongTB().getEMA(240);
//				&& GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6)
				// && GetData.getM15TB().isMARising(20, 1)
				// && GetData.getM15TB().isEMARising(50, 1)
				// && GetData.getLongTB().isEMARising(240, 1)
//				&& GetData.getLongTB().isEMARising(50, 1);
	}

	public boolean isDownTrend()
	{
		return GetData.getM15TB().getMA(20) < GetData.getM15TB().getEMA(50)
				&& GetData.getLongTB().getEMA(50) < GetData.getLongTB().getEMA(240);
//				&& GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6)
				// && GetData.getM15TB().isMADropping(20, 1)
				// && GetData.getM15TB().isEMADropping(50, 1)
				// && GetData.getLongTB().isEMADropping(240, 1)
//				&& GetData.getLongTB().isEMADropping(50, 1);

	}

	public boolean isSideWay()
	{

		return !GetData.getLongTB().isEMARising(50, 1) && !GetData.getLongTB().isEMADropping(50, 1);
	}

	void reverseOHLC(double ohlc)
	{
		if (Global.getCurrentPoint() <= ohlc + 5 && Global.getCurrentPoint() >= ohlc - 5)
		{

			Global.addLog(className + ": Entered waiting zone");
			Global.addLog("MA20(M15): " + GetData.getM15TB().getMA(20));
			Global.addLog("EMA50(M15): " + GetData.getM15TB().getEMA(50));
			Global.addLog("EMA50(M5): " + GetData.getLongTB().getEMA(50));
			Global.addLog("EMA240(M5): " + GetData.getLongTB().getEMA(240));
			Global.addLog("");

			while (Global.getCurrentPoint() <= ohlc + 20 && Global.getCurrentPoint() >= ohlc - 20)
				sleep(1000);

			if (Global.getCurrentPoint() > ohlc + 20 && isSideWay())
			{
				shortContract();
			} else if (Global.getCurrentPoint() < ohlc - 20 && isSideWay())
			{
				longContract();
			}
		}
	}

	void openOHLC(double ohlc)
	{
		if (Math.abs(Global.getCurrentPoint() - ohlc) <= 5)
		{

			Global.addLog(className + ": Entered waiting zone");

			waitForANewCandle();

			// wait until it standing firmly
			while (Math.abs(GetData.getLongTB().getLatestCandle().getClose() - ohlc) <= 5)
				sleep(1000);

			Global.addLog(className + ": Waiting for a pull back");
			// in case it get too fast, wait until it come back, just like
			// second corner but a little bit earlier
			while (Math.abs(Global.getCurrentPoint() - ohlc) > 5)
			{
				if (Math.abs(Global.getCurrentPoint() - ohlc) > 50)
				{
					Global.addLog(className + ": Risk is too big");
					return;
				}

				sleep(1000);
			}

			// for outside
			// if (Global.getCurrentPoint() > Global.getDayHigh()) {
			// if (GetData.getLongTB().getLatestCandle().getClose() > ohlc + 5)
			// longContract();
			// } else if (Global.getCurrentPoint() < Global.getDayLow()) {
			// if (GetData.getLongTB().getLatestCandle().getClose() < ohlc - 5)
			// shortContract();
			//
			// // for inside
			// } else {
			if (GetData.getLongTB().getLatestCandle().getClose() > ohlc)
			{
				if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6) + 2)
				{
					Global.addLog("Not Trending Up: EMA5 < EMA6");
					return;
				}
				longContract();
			} else if (GetData.getLongTB().getLatestCandle().getClose() < ohlc)
			{
				if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6) - 2)
				{
					Global.addLog("Not Trending Down: EMA5 > EMA6");
					return;
				}
				shortContract();
			}
			// }
		}
	}

	public void waitForANewCandle()
	{

		int currentSize = GetData.getLongTB().getCandles().size();

		while (currentSize == GetData.getLongTB().getCandles().size())
			sleep(1000);

	}

	public boolean isAfternoonTime()
	{

		int time = TimePeriodDecider.getTime();

		if (time > noonOpen && time < noonClose)
			return true;
		else
			return false;
	}
}
