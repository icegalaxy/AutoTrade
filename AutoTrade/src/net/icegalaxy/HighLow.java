package net.icegalaxy;

import java.util.ArrayList;

public class HighLow
{
	public boolean findingLow = true;
	public boolean findingHigh = true;
	public double refLow = 99999;
	public double refHigh = 0;
//	public ArrayList<Double> refLows = new ArrayList<Double>();
//	public ArrayList<Double> refHighs = new ArrayList<Double>();
//	public ArrayList<Double> volumeOfRefLows = new ArrayList<Double>();
//	public ArrayList<Double> volumeOfRefHighs = new ArrayList<Double>();
//	public ArrayList<Long> epochTimeOfHighs = new ArrayList<Long>();
//	public ArrayList<Long> epochTimeOfLows = new ArrayList<Long>();
	
	public ArrayList<RefPoint> highs = new ArrayList<RefPoint>();
	public ArrayList<RefPoint> lows = new ArrayList<RefPoint>();
	
	
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
			
			RefPoint ref = new RefPoint(refHigh,volumeOfRefHigh,TimePeriodDecider.getEpochSec());
			highs.add(ref);
//			refHighs.add(refHigh);
//			volumeOfRefHighs.add(volumeOfRefHigh);
//			epochTimeOfHighs.add(TimePeriodDecider.getEpochSec());
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
			long time = TimePeriodDecider.getEpochSec();
			RefPoint ref = new RefPoint(refHigh,volumeOfRefHigh,time);
			highs.add(ref);
			
//			refHighs.add(refHigh);
//			volumeOfRefHighs.add(volumeOfRefHigh);
//			epochTimeOfHighs.add(TimePeriodDecider.getEpochSec());
			Global.addLog(hlName + ": Recent High Update: " + refHigh + "; volume: " + volumeOfRefHigh + "; time: " + time);
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
			
			RefPoint ref = new RefPoint(refLow,volumeOfRefLow,TimePeriodDecider.getEpochSec());
			lows.add(ref);
			
//			refLows.add(refLow);
//			volumeOfRefLows.add(volumeOfRefLow);
//			epochTimeOfLows.add(TimePeriodDecider.getEpochSec());
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
			
			long time = TimePeriodDecider.getEpochSec();
			
			RefPoint ref = new RefPoint(refLow,volumeOfRefLow,time);
			lows.add(ref);
			
//			refLows.add(refLow);
//			volumeOfRefLows.add(volumeOfRefLow);
//			epochTimeOfLows.add(TimePeriodDecider.getEpochSec());
			Global.addLog(hlName + ": Recent Low Update: " + refLow + "; volume: " + volumeOfRefLow + "; time: " + time);
			findingLow = false;
			findingHigh = true;
			refHigh = 0;
			refLow = 99999;
		}
	}
	
	public double getLatestHigh()
	{
		if (highs.size() == 0)
			return 0;
		return highs.get(highs.size() -1).refPoint;
	}

	public double getLatestLow()
	{
		if (lows.size() == 0)
			return 99999;
		return lows.get(lows.size() -1).refPoint;
	}
	
	public boolean isRising()
	{
		if (lows.size() < 2)
			return false;
		
		if (lows.get(lows.size() - 1).epochTime - lows.get(lows.size() - 2).epochTime < 300)
			return false;
		
		if (lows.get(lows.size() - 1).refPoint > lows.get(lows.size() - 2).refPoint
				&& refLow >= lows.get(lows.size() - 1).refPoint)
			return true;
		else
			return false;
	}
	
	public boolean isDropping()
	{
		if (highs.size() < 2)
			return false;
		
		if (highs.get(highs.size() - 1).epochTime - highs.get(highs.size() - 2).epochTime < 300)
			return false;
		
		if (highs.get(highs.size() - 1).refPoint < highs.get(highs.size() - 2).refPoint
				&& refHigh <= highs.get(highs.size() - 1).refPoint)
			return true;
		else
			return false;
	}
	
	public double getVolumeOfRecentLow(){
		return lows.get(lows.size() -1).volume;
	}
	
	public double getVolumeOfRecentHigh(){
		return highs.get(highs.size() -1).volume;
	}
	
	
}
