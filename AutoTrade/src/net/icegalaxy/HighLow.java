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
	public ArrayList<Double> volumeOfRefLows = new ArrayList<Double>();
	public ArrayList<Double> volumeOfRefHighs = new ArrayList<Double>();
	public double volumeOfRefLow;
	public double volumeOfRefHigh;
	public double spread;
	public String objectName;
	
	
	public HighLow(String objectName, double spread)
	{
		this.objectName = objectName;
		this.spread = spread / 100;
	}
	
	public void findHigh()
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
			refLow = GetData.getShortTB().getLatestCandle().getLow();

		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
		{
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();
			volumeOfRefHigh = GetData.getShortTB().getLatestCandle().getVolume();
			refLow = 99999;
		}
		
		if (refLow < refHigh - (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refHighs.add(refHigh);
			volumeOfRefHighs.add(volumeOfRefHigh);
			findingLow = true;
			findingHigh = false;
			refHigh = 0;
			refLow = 99999;
		}
	}
	
	public void findHigh(String hlName)
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
			refLow = GetData.getShortTB().getLatestCandle().getLow();

		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
		{
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();
			volumeOfRefHigh = GetData.getShortTB().getLatestCandle().getVolume();
			refLow = 99999;
		}
		
		if (refLow < refHigh - (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refHighs.add(refHigh);
			volumeOfRefHighs.add(volumeOfRefHigh);
			Global.addLog(hlName + ": Recent High Update: " + refHigh);
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
			volumeOfRefLow = GetData.getShortTB().getLatestCandle().getVolume();
			refHigh = 0;
		}
		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();

		if (refHigh > refLow + (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refLows.add(refLow);
			volumeOfRefLows.add(volumeOfRefLow);
			findingLow = false;
			findingHigh = true;
			refHigh = 0;
			refLow = 99999;
		}
	}
	
	public void findLow(String hlName)
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
		{
			refLow = GetData.getShortTB().getLatestCandle().getLow();
			volumeOfRefLow = GetData.getShortTB().getLatestCandle().getVolume();
			refHigh = 0;
		}
		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();

		if (refHigh > refLow + (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			refLows.add(refLow);
			volumeOfRefLows.add(volumeOfRefLow);
			Global.addLog(hlName + ": Recent Low Update: " + refLow);
			findingLow = false;
			findingHigh = true;
			refHigh = 0;
			refLow = 99999;
		}
	}
	
	public double getLatestHigh()
	{
		if (refHighs.size() == 0)
			return 99999;
		return refHighs.get(refHighs.size() -1);
	}

	public double getLatestLow()
	{
		if (refLows.size() == 0)
			return 0;
		return refLows.get(refLows.size() -1);
	}
	
	public boolean isRising()
	{
		if (refLows.size() < 2)
			return false;
		
		if (refLows.get(refLows.size() - 1) > refLows.get(refLows.size() - 2)
				&& refLow >= refLows.get(refLows.size() - 1))
			return true;
		else
			return false;
	}
	
	public boolean isDropping()
	{
		if (refHighs.size() < 2)
			return false;
		
		if (refHighs.get(refHighs.size() - 1) < refHighs.get(refHighs.size() - 2)
				&& refHigh <= refHighs.get(refHighs.size() - 1))
			return true;
		else
			return false;
	}
	
	public double getVolumeOfRecentLow(){
		return volumeOfRefLows.get(volumeOfRefLows.size() -1);
	}
	
	public double getVolumeOfRecentHigh(){
		return volumeOfRefHighs.get(volumeOfRefHighs.size() -1);
	}
	
	
}
