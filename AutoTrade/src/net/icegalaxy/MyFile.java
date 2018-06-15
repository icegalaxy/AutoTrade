package net.icegalaxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MyFile extends File
{

	public long previousModifiedTime;
	public StringBuffer fileString;
	public StringBuffer previousString;
	String pathName;
	
	public MyFile(String pathname)
	{
		super(pathname);
		this.pathName = pathname;
		fileString = new StringBuffer("");
		previousString = new StringBuffer("");
		previousModifiedTime = this.lastModified();
		// TODO Auto-generated constructor stub
	}
	
	public void writeToFile()
	{
		try {
			// Create file
			FileWriter fstream = new FileWriter(pathName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(fileString.toString());
			// Close the output stream
			out.close();
			Thread.sleep(100);
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	public boolean isModified()
	{
		return previousModifiedTime != this.lastModified();
	}

}
