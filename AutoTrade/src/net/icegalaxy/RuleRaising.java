package net.icegalaxy;

public class RuleRaising implements Runnable
{

	public double cutLoss;
	public boolean buying;
	public boolean selling;
	public int noOfContracts;

	
	public RuleRaising(Raising raise)
	{
		cutLoss = raise.cutLoss;
		buying = raise.buying;
		selling = raise.selling;
		noOfContracts = raise.noOfContracts;
	}
	
	
	@Override
	public void run()
	{
		if (Global.maxContracts - Global.noOfTrades >= noOfContracts)
		{
			if (buying)
			{
				trailingDown(2);
				longContract(noOfContracts);
			}else if (selling)
			{
				trailingUp(2);
				shortContract(noOfContracts);			
			}
			
			
		}else{
			Global.addLog("> Max conctracts");
			return;
		}
		
	}
	
	private void shortContract(int noOfContracts2)
	{
		
				
	}


	private void longContract(int noOfContracts2)
	{
		if (!isOrderTime())
		{
			Global.addLog("Rule chasing : not order time");
			return;
		}
		
		if (!Global.isConnectionOK())
		{
			Global.addLog("Rule chasing : Connection probelm");
			return;
		}

		if (Global.maxContracts - Global.noOfTrades >= noOfContracts)
		{
			Global.addLog("Rule chasing : > Max Contracts");
			return;
		}

		boolean b = Sikuli.longContract(noOfContracts);
		if (!b)
		{
			Global.addLog("Rule chasing :  Fail to long");
			return;
		}
		
		Global.addLog("Rule chasing :  Long @" + Global.getCurrentAsk() + " X " + noOfContracts);
	}


	void trailingDown(int points)
	{
		double refLow = 99999;

		while (Global.getCurrentPoint() < refLow + points)
		{
			if (Global.getCurrentPoint() < refLow){
				refLow = Global.getCurrentPoint();
				Global.addLog("RefLow updated: " + refLow);
			}		
			sleep(1000);
		}
	}
	
	void trailingUp(int points)
	{
		double refHigh = 0;
		
		while (Global.getCurrentPoint() > refHigh - points)
		{
			if (Global.getCurrentPoint() > refHigh){
				refHigh = Global.getCurrentPoint();
				Global.addLog("RefHigh updated: " + refHigh);
			}
			sleep(1000);
		}
	}
	
	public void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
