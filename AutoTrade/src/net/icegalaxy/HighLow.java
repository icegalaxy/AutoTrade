package net.icegalaxy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	
	long epochSecForHigh;
	long epochSecForLow;
	
	public double volumeOfRefLow;
	public double volumeOfRefHigh;
	public double spread;
	public String objectName;
	
	
	public HighLow(String objectName, double spread)
	{
		this.objectName = objectName;
		this.spread = spread / 100;
	}
	
	//for previous data
	public void findHigh()
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
			refLow = GetData.getShortTB().getLatestCandle().getLow();

		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
		{ 
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();
			volumeOfRefHigh = GetData.getShortTB().getLatestCandle().getVolume();
			epochSecForHigh = strToEpoch(GetData.getShortTB().getLatestCandle().getTime());
			refLow = 99999;
		}
		
		if (refLow < refHigh - (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			
			RefPoint ref = new RefPoint(refHigh,volumeOfRefHigh,epochSecForHigh);
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
			epochSecForHigh = TimePeriodDecider.getEpochSec();
			refLow = 99999;
		}
		
		if (refLow < refHigh - (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			RefPoint ref = new RefPoint(refHigh,volumeOfRefHigh,epochSecForHigh);
			highs.add(ref);
			
//			refHighs.add(refHigh);
//			volumeOfRefHighs.add(volumeOfRefHigh);
//			epochTimeOfHighs.add(TimePeriodDecider.getEpochSec());
			Global.addLog(hlName + ": Recent High Update: " + refHigh + "; volume: " + volumeOfRefHigh + "; time: " + epochSecForHigh);
			findingLow = true;
			findingHigh = false;
			refHigh = 0;
			refLow = 99999;
		}
	}

	//for previous data
	public void findLow()
	{
		if (GetData.getShortTB().getLatestCandle().getLow() < refLow)
		{
			refLow = GetData.getShortTB().getLatestCandle().getLow();
			volumeOfRefLow = GetData.getShortTB().getLatestCandle().getVolume();
			epochSecForLow = strToEpoch(GetData.getShortTB().getLatestCandle().getTime());
			refHigh = 0;
		}
		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();

		if (refHigh > refLow + (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			
			RefPoint ref = new RefPoint(refLow,volumeOfRefLow,epochSecForLow);
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
			epochSecForLow = TimePeriodDecider.getEpochSec();
			refHigh = 0;
		}
		if (GetData.getShortTB().getLatestCandle().getHigh() > refHigh)
			refHigh = GetData.getShortTB().getLatestCandle().getHigh();

		if (refHigh > refLow + (GetData.getShortTB().getLatestCandle().getHigh() * spread))
		{
			
			
			RefPoint ref = new RefPoint(refLow,volumeOfRefLow,epochSecForLow);
			lows.add(ref);
			
//			refLows.add(refLow);
//			volumeOfRefLows.add(volumeOfRefLow);
//			epochTimeOfLows.add(TimePeriodDecider.getEpochSec());
			Global.addLog(hlName + ": Recent Low Update: " + refLow + "; volume: " + volumeOfRefLow + "; time: " + epochSecForLow);
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
	
	
	private double getSlopeOfUpTrend() {
		
		
		if (lows.size() < 2)
			return 0;
		
		
		double pointsDiff = lows.get(lows.size()-1).refPoint - lows.get(lows.size()-2).refPoint;
		double secDiff = lows.get(lows.size()-1).epochTime - lows.get(lows.size()-2).epochTime;
		
		if (secDiff == 0)
		{
			Global.addLog("Sec Diff = 0");
			return 0;
		}
		
		return pointsDiff/secDiff;
		
	}
	
	//get the current Up Trend point based on current epoch sec
	public double getUpTrend()
	{
		if (getSlopeOfUpTrend() < 0.02 || !isRising())
			return 0;
		
		return getSlopeOfUpTrend() * (TimePeriodDecider.getEpochSec() - lows.get(lows.size()-1).epochTime) + lows.get(lows.size()-1).refPoint;
		
		
	}
	
	private double getSlopeOfDownTrend() {
		
		if (highs.size() < 2)
			return 0;
		
		double pointsDiff = highs.get(highs.size()-1).refPoint - highs.get(highs.size()-2).refPoint;
		double secDiff = highs.get(highs.size()-1).epochTime - highs.get(highs.size()-2).epochTime;
		
		if (secDiff == 0)
		{
			Global.addLog("Sec Diff = 0");
			return 0;
		}
		
		return pointsDiff/secDiff;
		
	}
	
	//get the current Down Trend point based on current epoch sec
		public double getDownTrend()
		{
			if (getSlopeOfDownTrend() > -0.02 || !isDropping())
				return 0;
			
			return getSlopeOfDownTrend() * (TimePeriodDecider.getEpochSec() - lows.get(lows.size()-1).epochTime) + lows.get(lows.size()-1).refPoint;		
			
		}
		
		private long strToEpoch(String s)
		{
			
//			String dateTime = "2018/06/05/15/23/01";
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
//			SimpleDateFormat df = new SimpleDateFormat(s);
			Date date = null;
			try {
				date = df.parse(s);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long epoch = date.getTime();
			return epoch;
			
			
			
		}
	
}
