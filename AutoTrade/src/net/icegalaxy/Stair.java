package net.icegalaxy;

public class Stair
{
	public String lineType;
	public double value;
	public double cutLoss;
	public boolean shutdown; //It's manual, if shutdown, it will not be re-activated unless edit csv
	public int reActivateTime;
	public boolean buying = true;
	public boolean selling = true;
	public int timesOfShutdown =1;
	public double refHigh;
	public double refLow;
	public double tolerance;
}
