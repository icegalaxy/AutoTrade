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
		if (Global.maxContracts - Math.abs(Global.getNoOfContracts()) >= noOfContracts)
		{
			if (buying)
			{
//				trailingDown(2);
				longContract();
			}else if (selling)
			{
//				trailingUp(2);
				shortContract();			
			}
				
		}else{
			Global.addLog("> Max conctracts");
			return;
		}
		
		while (Global.getNoOfContracts() != 0)
		{
			
			if (Global.getNoOfContracts() > 0)
			{
				if (Global.getCurrentPoint() < cutLoss)
				{
					shortContract();
					Global.shutDownRaising = true;
					Global.addLog("Shut down raising");
					return;	
				}
			}else
			{
				if (Global.getCurrentPoint() > cutLoss)
				{
					longContract();
					Global.shutDownRaising = true;
					Global.addLog("Shut down raising");
					return;
				}
				
			}
			
			sleep(1000);
		}
		
		Global.addLog("Rule raising: No. of contracts = 0");
		
	}
	
	private void shortContract()
	{
				
				if (!Global.isConnectionOK())
				{
					Global.addLog("Rule chasing : Connection probelm");
					return;
				}

				if (Global.maxContracts - Math.abs(Global.getNoOfContracts()) < noOfContracts)
				{
					Global.addLog("Rule chasing : > Max Contracts");
					return;
				}

				boolean b = Sikuli.shortContract(noOfContracts);
				if (!b)
				{
					Global.addLog("Rule chasing : Fail to short");
					return;
				}
				Global.addLog("Rule chasing : Short @ " + Global.getCurrentBid() + " X " + noOfContracts);
				
				
	}


	private void longContract()
	{
//		if (!isOrderTime())
//		{
//			Global.addLog("Rule chasing : not order time");
//			return;
//		}
		
		if (!Global.isConnectionOK())
		{
			Global.addLog("Rule chasing : Connection probelm");
			return;
		}

		if (Global.maxContracts - Math.abs(Global.getNoOfContracts()) < noOfContracts)
		{
			Global.addLog("Rule chasing : > Max Contracts");
			return;
		}

		boolean b = Sikuli.longContract(noOfContracts);
		if (!b)
		{
			Global.addLog("Rule chasing : Fail to long");
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
