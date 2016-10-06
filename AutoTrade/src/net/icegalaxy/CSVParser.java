package net.icegalaxy;

import java.io.File;

import java.io.FileNotFoundException;

import java.util.ArrayList;

import java.util.Scanner;

public class CSVParser {

	private String fileName;

	private ArrayList<String> time;

	private ArrayList<Double> open;

	private ArrayList<Double> high;

	private ArrayList<Double> low;

	private ArrayList<Double> close;

	private ArrayList<Double> volume;

	public CSVParser(String fileName) {

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

	
		for (int i = 1; i < al.size(); i++) {			//start from 1, ignore the title

			Scanner sc2 = new Scanner(al.get(i));
			sc2.useDelimiter(",");

			while (sc2.hasNext()) {

				time.add(sc2.next());

//				System.out.println("Time: " + time.get(time.size() - 1));

				open.add(sc2.nextDouble());

//				System.out.println("Open: " + open.get(open.size() - 1));

				high.add(sc2.nextDouble());

				low.add(sc2.nextDouble());

				close.add(sc2.nextDouble());

				volume.add(sc2.nextDouble());

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
