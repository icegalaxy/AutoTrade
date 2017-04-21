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

		
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;

		if (shutdown || isOutOfRange() || trendReversed)
		{
			intraDay.findElementOfToday();
			intraDay.updateNode("rangeResist", "0");
			intraDay.updateNode("rangeSupport", "0");
			Global.addLog("Shut down ruleRange");
			trendReversed = false;
			shutdown = false;
		}

		while (true)
		{
			
			intraDay.findElementOfToday();
			intraDay.findOHLC();
			rangeResist = intraDay.rangeResist;
			rangeSupport = intraDay.rangeSupport;

			if (rangeResist != 0 || rangeSupport != 0)
			{
				Global.addLog("RangeResist: " + rangeResist);
				Global.addLog("RangeSupport: " + rangeSupport);
				break;
			}
			
			sleep(60000);
		}

		

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

	private boolean isOutOfRange()
	{
		if (rangeResist != 0 && Global.getCurrentPoint() > rangeResist + 20)
			return true;
		else if (rangeSupport != 0 && Global.getCurrentPoint() < rangeSupport - 20)
			return true;

		return false;
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
		} else
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
			return Global.getCurrentPoint() < rangeSupport - 5;
		else
			return Global.getCurrentPoint() > rangeResist + 5;
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