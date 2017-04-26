package net.icegalaxy;

public class IntraDayReader extends XMLReader
{
	
	public double rangeResist;
	public double rangeSupport;
	

	public IntraDayReader(String tradeDate, String filePath)
	{
		super(tradeDate, filePath);
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
			e.printStackTrace();
			Global.addLog("Cannot find rangeTag");
		}
	}

}
