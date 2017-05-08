package net.icegalaxy;

public class OHLC
{
	
	int morningOpen = 93000;
	int morningClose = 103000;
	int noonOpen = 150000;
	int noonClose = 160000;
	int nightOpen = 173000;
	int nightClose = 231500;
	
	
	
	public String name;
	public double position = 0;
	public double stopEarn = 0;
	public double cutLoss = 0;
	
	public boolean testedBreakThrough;
	public boolean testedRebound;
	public boolean breakThroughValid;
	public boolean reboundValid;
	
	public boolean shutdown;
	
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen, int noonClose)
	{
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
	}
	
	public int[] getOrderTime()
	{
		int[] orderTimes = new int[4];
		
		orderTimes[0] = morningOpen;
		orderTimes[1] = morningClose;
		orderTimes[2] = noonOpen;
		orderTimes[3] = noonClose;
		
		return orderTimes;
		
	}
	
}
