package net.icegalaxy;

import java.util.Date;

public class Candle {
	private String time;
	private double low;
	private double high;
	private double open;
	private double close;
	private double volume;
	
	private double rsi;

	public Candle(String time, double high, double low, double open,
			double close, double volume, double rsi) {
		this.time = time;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.volume = volume;
		this.rsi = rsi;
	}

	public double getLow() {
		return this.low;
	}

	public double getHigh() {
		return this.high;
	}

	public double getOpen() {
		return this.open;
	}

	public double getClose() {
		return this.close;
	}

	public double getVolume() {
		return this.volume;
	}

	public String getTime() {
		return this.time;
	}
	
	public double getRsi(){
		return this.rsi;
	}
}