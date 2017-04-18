package net.icegalaxy;

public class IntraDayReader extends XMLReader
{
	
	public double rangeResist;
	public double rangeSupport;
	

	public IntraDayReader(String tradeDate, String filePath)
	{
		super(tradeDate, filePath);
		findElementOfToday();
	}
	
	public void findOHLC()
	{

		try
		{
		rangeResist = Double.parseDouble(eElement.getElementsByTagName("rangeResist").item(0).getTextContent());
		rangeSupport = Double.parseDouble(eElement.getElementsByTagName("rangeSupport").item(0).getTextContent());
		}
		catch (Exception e)
		{
			System.out.print(".");
		}
	}

}
