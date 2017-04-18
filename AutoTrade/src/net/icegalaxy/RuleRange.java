package net.icegalaxy;

import java.util.ArrayList;

public class RuleRange extends Rules
{

	private boolean trendReversed;

	private double rangeResist = 0;
	private double rangeSupport = 0;

	IntraDayReader intraDay;

	public RuleRange(boolean globalRunRule)
	{
		super(globalRunRule);
		setOrderTime(103000, 114500, 130100, 150000, 231500, 231500);
		intraDay = new IntraDayReader(Global.getToday(), "C:\\Users\\joech\\Dropbox\\TradeData\\Intraday.xml");

	}

	public void openContract()
	{

		trendReversed = false;

		if (shutdown)
		{
			intraDay.updateNode("rangeResist", "0");
			intraDay.updateNode("rangeSupport", "0");
			Global.addLog("Shut down ruleRange");
			shutdown = false;
		}

		while (rangeResist == 0 && rangeSupport == 0)
		{
			sleep(60000);
			intraDay.findOHLC();
			rangeResist = intraDay.rangeResist;
			rangeSupport = intraDay.rangeSupport;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;

		if (rangeSupport != 0)
		{
			if (Global.getCurrentPoint() < rangeSupport + 5 && Global.getCurrentPoint() > rangeSupport)
				longContract();
		} else if (rangeResist != 0)
		{
			if (Global.getCurrentPoint() > rangeResist - 5 && Global.getCurrentPoint() < rangeResist)
				shortContract();
		}

	}

	public double getCurrentClose()
	{
		return GetData.getShortTB().getLatestCandle().getClose();
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{
		if (Global.getNoOfContracts() > 0)
		{
			if (rangeResist == 0 || trendReversed)
				tempCutLoss = 99999;
			else
				super.updateStopEarn();
		}
		else
		{
			if (rangeSupport == 0 || trendReversed)
				tempCutLoss = 0;
			else
				super.updateStopEarn();
		}
			
	}

	 

	double getCutLossPt()
	{
		return 25;
	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

		}

	}

	@Override
	boolean trendReversed()
	{

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() < rangeSupport;
		else
			return Global.getCurrentPoint() > rangeResist;
	}

	double getStopEarnPt()
	{

		if (trendReversed)
			return 5;

		if (Global.getNoOfContracts() > 0)
		{

			if (rangeResist != 0)
				return Math.max(10, rangeResist - buyingPoint - 3);
			else
				return 10;
		} else
		{

			if (rangeSupport != 0)
				return Math.max(10, buyingPoint - rangeSupport - 3);
			else
				return 10;
		}

	}

	@Override
	public void trendReversedAction()
	{

		trendReversed = true;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}