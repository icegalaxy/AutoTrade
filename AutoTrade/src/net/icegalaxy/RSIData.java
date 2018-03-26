package net.icegalaxy;


public class RSIData
{
	public double refHigh = 0;
	public double refLow = 99999;
	public double rsi;
	int time;
	
	public void setRSI(double rsi, double refPoint, int time)
	{
		if (rsi > 50)
		{
			refHigh = refPoint;			
		}else
		{
			refLow = refPoint;
		}
		this.time = time;
	}
}
