package net.icegalaxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class parseSPRecord
{
	private String fileName;

	private ArrayList<String> time;

	private ArrayList<Double> open;

	private ArrayList<Double> high;

	private ArrayList<Double> low;

	private ArrayList<Double> close;

	private ArrayList<Double> volume;
	
	public parseSPRecord(String fileName)
	{
		this.fileName = fileName;

	}

	public void parseOHLC() {

		File file = new File(fileName);

		time = new ArrayList<String>();

		open = new ArrayList<Double>();

		high = new ArrayList<Double>();

		low = new ArrayList<Double>();

		close = new ArrayList<Double>();

		volume = new ArrayList<Double>();

		Scanner sc = null;

		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sc.useDelimiter("\r\n");

		ArrayList<String> al = new ArrayList<String>();

		//each line for one record
		while (sc.hasNext()) {
			al.add(sc.next());
//			System.out.println(al.get(al.size() -1));
		}
		
		sc.close();

	
		for (int i = 0; i < al.size(); i++) {

			Scanner sc2 = new Scanner(al.get(i));
			sc2.useDelimiter(";");

			while (sc2.hasNext()) {

				time.add(sc2.next());

//				System.out.println("Time: " + time.get(time.size() - 1));

				open.add(Double.parseDouble(sc2.next().trim()));

//				System.out.println("Open: " + open.get(open.size() - 1));

				high.add(Double.parseDouble(sc2.next().trim()));

				low.add(Double.parseDouble(sc2.next().trim()));

				close.add(Double.parseDouble(sc2.next().trim()));

				volume.add(Double.parseDouble(sc2.next().trim()));

			}
			
			sc2.close();

		}

	}

	public ArrayList<String> getTime() {

		return time;

	}

	public ArrayList<Double> getOpen() {

		return open;

	}

	public ArrayList<Double> getHigh() {

		return high;

	}

	public ArrayList<Double> getLow() {

		return low;

	}

	public ArrayList<Double> getClose() {

		return close;

	}

	public ArrayList<Double> getVolume() {
		return volume;
	}

}
	
	
	

