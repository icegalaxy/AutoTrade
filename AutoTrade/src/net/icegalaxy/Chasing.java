package net.icegalaxy;

public class Chasing
{
	private double refHigh = 0;
	private double refLow = 99999;
	private boolean chaseUp;
	private boolean chaseDown;
	
	public Chasing (){
		
	}
	
	

	public double getRefHigh()
	{
		return refHigh;
	}
	
	public double getRefLow()
	{
		return refLow;
	}



	public void setRefHigh(double refHigh)
	{
		this.refHigh = refHigh;
	}
	
	public void setRefLow(double refLow)
	{
		this.refLow = refLow;
	}



	public boolean chaseUp()
	{
		return chaseUp;
	}

	public void setChaseUp(boolean chaseUp)
	{
		this.chaseUp = chaseUp;
	}

	public boolean chaseDown()
	{
		return chaseDown;
	}

	public void setChaseDown(boolean chaseDown)
	{
		this.chaseDown = chaseDown;
	}
	
}
