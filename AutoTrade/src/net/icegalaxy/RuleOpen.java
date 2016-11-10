package net.icegalaxy;

import java.util.ArrayList;

public class RuleOpen extends Rules {

	private int lossTimes = 0;
	private double cutLoss;
	// private double refEMA;

	public RuleOpen(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(91530, 115500, 160000, 160000, 231500, 231500);

	}

	public void openContract()
	{

//		if (shutdown)
//		{
//			lossTimes++;
//			shutdown = false;
//		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;

		if (isHigherThan4MA(5))
		{
			while(!isLowerThan4MA(1) ){
				sleep(1000);
			
			}
			
			shortContract();
		}else if (isLowerThan4MA(5))
		{
			while(!isHigherThan4MA(1) ){
				sleep(1000);
			
			}
			longContract();
		}
	}
	
	
	public boolean isHigherThan4MA(double pointsHigher){
		
		double highestMA = 0;
		
		for (int i=0; i <get4MAs().size(); i++){		
			highestMA = Math.max(highestMA, get4MAs().get(i));			
		}	
		return GetData.getShortTB().getLatestCandle().getClose() > highestMA + pointsHigher;
	}
	
public boolean isLowerThan4MA(double pointsLower){
		
		double lowestMA = 99999;
		
		for (int i=0; i <get4MAs().size(); i++){		
			lowestMA = Math.min(lowestMA, get4MAs().get(i));			
		}	
		return GetData.getShortTB().getLatestCandle().getClose() < lowestMA - pointsLower;
	}

	private ArrayList<Float> get4MAs()
	{
		ArrayList<Float> mas = new ArrayList<Float>();
		
		mas.add(GetData.getM15TB().getMA(20));
		mas.add(GetData.getM15TB().getEMA(50));
		mas.add(GetData.getLongTB().getEMA(50));
		mas.add(GetData.getLongTB().getEMA(240));
		
		return mas;
		
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;
//
//		if (getProfit() < 100)
//		{
			ema5 = GetData.getShortTB().getLatestCandle().getClose();
			ema6 = GetData.getLongTB().getEMA(5);
//		} else
//		{
//			ema5 = GetData.getLongTB().getEMA(5);
//			ema6 = GetData.getLongTB().getEMA(6);
//		}

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
//			 tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6){
				tempCutLoss = 99999;
//				if (getProfit() > 0)
//					chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
//			 tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6){
				tempCutLoss = 0;
//				if (getProfit() > 0)
//					chasing.setChaseDown(true);
			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return Math.max(15, cutLoss);
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
		
//		if (Global.getCurrentPoint() > chasing.getRefHigh())
//			chasing.setRefHigh(Global.getCurrentPoint());
//		if (Global.getCurrentPoint() < chasing.getRefLow())
//			chasing.setRefLow(Global.getCurrentPoint());
		
	}

	double getStopEarnPt()
	{
//		if (Global.getNoOfContracts() > 0)
//		{
//			if (GetData.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5))
//				return -100;
//			
//			
//			
//			
//		} else if (Global.getNoOfContracts() < 0)
//		{
//			if (GetData.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(6))
//				return -100;
//		}
		
		
		
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}