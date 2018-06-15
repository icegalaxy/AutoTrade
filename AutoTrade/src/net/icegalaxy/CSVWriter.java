package net.icegalaxy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class CSVWriter
{
	
	List<Stair> stairs;
	String csvPath;
		
	public CSVWriter(List<Stair> stairs, String csvPath)
	{
		this.stairs = stairs;
		this.csvPath = csvPath;
	}
	
	public String getCSVString()
	{
		StringBuilder csvString = new StringBuilder();
		
		csvString.append("LineType,Value,CutLoss,Buying,RefLow,Selling,RefHigh,Tolerance,Re-Active Time,Shutdown\r\n");
		
		
		for (int i=0; i<stairs.size(); i++)
		{
			csvString.append(stairs.get(i).lineType + ",");
			csvString.append(stairs.get(i).value + ",");
			csvString.append(stairs.get(i).cutLoss + ",");
			csvString.append(stairs.get(i).buying + ",");
			csvString.append(stairs.get(i).refLow + ",");
			csvString.append(stairs.get(i).selling + ",");
			csvString.append(stairs.get(i).refHigh + ",");
			csvString.append(stairs.get(i).tolerance + ",");
			csvString.append(stairs.get(i).reActivateTime + ",");
			csvString.append(stairs.get(i).shutdown + "\r\n");	
			
		}
		
//		stringtoFile(csvString.toString(), csvPath);
		
		return csvString.toString();
	}
	
	public static void stringtoFile(String inputString,
			String outputFileNameWithExtension) {

		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFileNameWithExtension);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(inputString);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
}


