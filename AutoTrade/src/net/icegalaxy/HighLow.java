package net.icegalaxy;

import java.util.ArrayList;

public class HighLow
{
	public boolean findingLow = true;
	public boolean findingHigh = true;
	public double refLow = 99999;
	public double refHigh = 0;
	public ArrayList<Double> refLows = new ArrayList<Double>();
	public ArrayList<Double> refHighs = new ArrayList<Double>();
	public double spread;
	
	public HighLow(double spread)
	{
		this.spread = spread / 100;
	}
	
	public void findHigh()
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
			refLow = GetData.getShortTB().getLatestCandle().getLow();

		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
		{
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();
			refLow = 99999;
		}
		
		if (refLow < refHigh - (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refHighs.add(refHigh);
			Global.addLog("Recent High Update: " + refHigh);
			findingLow = true;
			findingHigh = false;
			refHigh = 0;
			refLow = 99999;
		}
	}

	public void findLow()
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
		{
			refLow = GetData.getShortTB().getLatestCandle().getLow();
			refHigh = 0;
		}
		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();

		if (refHigh > refLow + (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refLows.add(refLow);
			Global.addLog("Recent Low Update: " + refLow);
			findingLow = false;
			findingHigh = true;
			refHigh = 0;
			refLow = 99999;
		}
	}
	
	public double getLatestHigh()
	{
		if (refHighs.size() == 0)
			return 0;
		return refHighs.get(refHighs.size() -1);
	}

	public double getLatestLow()
	{
		if (refLows.size() == 0)
			return 99999;
		return refLows.get(refLows.size() -1);
	}
}
