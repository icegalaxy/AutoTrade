package net.icegalaxy;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TechnicalIndicators {

	public TechnicalIndicators(String table) {

		ResultSet rs;

		try {
			rs = DB.stmt.executeQuery("Select Point FROM " + table);

			while (rs.next()) {
				this.close.add(rs.getFloat("Point"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public TechnicalIndicators(ArrayList<Float> close) {
		this.close = close;
	}

	// current RSI,�Ӯa����A�ҥH�ntake currentPoint as para
	public float getRSI(float currentPoint, int noOfPeriods) {
		RSI rsi = new RSI(close, noOfPeriods);
		return rsi.getRSI(currentPoint);
	}

	// previous RSI, ������
	public float getRSI(int noOfPeriods) {
		RSI rsi = new RSI(close, noOfPeriods);
		return rsi.getRSI();
	}


	//Only Get Current MA
	public float getMovingAverage(int noOfPeriods) {

		if (close.size() < noOfPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (close.size() - 1); i >= (close.size() - noOfPeriods); i--) {
			total += close.get(i);
		}
		float f = total / noOfPeriods;
		// System.out.println("Current MA20: " + f);
		return f;
	}
	
	//Can get Previos MA
	public float getMovingAverage(int noOfPeriods, int previosPeriods) {

		
		if (close.size() < noOfPeriods + previosPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (close.size() - 1-previosPeriods); i >= (close.size() - noOfPeriods-previosPeriods); i--) {
			total += close.get(i);
		}
		float f = total / noOfPeriods;
		// System.out.println("Current MA20: " + f);
		return f;
	}

	public float getEMA(int noOfPeriods, int previosPeriods) {

		float ema = 0;

		if (close.size() < noOfPeriods)
			return -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

		if (noOfPeriods == close.size()) {
			return getfirstMA(noOfPeriods);
		} else {

			ema = getfirstMA(noOfPeriods);

			for (int i = noOfPeriods; i < close.size() - previosPeriods; i++) {

				ema = (close.get(i) - ema) * smoothingConstant + ema;

			}
			return ema;
		}
	}
	
	public float getEMA(int noOfPeriods) {

		float ema = 0;

		if (close.size() < noOfPeriods)
			return -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

		if (noOfPeriods == close.size()) {
			return getfirstMA(noOfPeriods);
		} else {

			ema = getfirstMA(noOfPeriods);

			for (int i = noOfPeriods; i < close.size(); i++) {

				ema = (close.get(i) - ema) * smoothingConstant + ema;

			}
			return ema;
		}
	}

	private float getfirstMA(int noOfPeriods) {

		float sum = 0;

		if (close.size() < noOfPeriods)
			return -1;
		else {
			for (int i = 0; i < noOfPeriods; i++) {
				sum = sum + close.get(i);
			}
			return sum / noOfPeriods;
		}

	}

	// period�Y�Y��ma�X�h�����u
	public float getStandardDeviation(int period) {

		if (close.size() < period)
			return -1;
		float theSumPart = 0;
		float average = getMovingAverage(period);
		for (int i = 1; i < period; i++) {
			theSumPart = theSumPart
					+ (float) Math
							.pow(close.get(close.size() - i) - average, 2);
		}

		// System.out.println("sqrt: " + (period) );

		return (float) Math.sqrt((1.0 / period) * theSumPart); // �]��int ��
		// int�|�Xint,�O�׷|�X�s,�ҥH��1.0�����o
	}

	public float getMACD() {

		float macd;
		int currentCloseSize = close.size();

		if (getEMA(12) == -1.0 || getEMA(26) == -1.0)
			return 0;
		else {
			macd = getEMA(12) - getEMA(26);
			if (currentCloseSize != this.closeSize) {
				macdArray.add(macd);
				this.closeSize = currentCloseSize;
			}
			return macd;
		}
	}

	public float getMACDSignalLine() {

		if (macdArray.size() < 9)
			return 0;
		else {
			TechnicalIndicators tiMACD = new TechnicalIndicators(macdArray);
			return tiMACD.getEMA(9);
		}
	}

	public float getMACDHistogram() {

		if (macdArray.size() < 9)
			return 0;
		else
			return getMACD() - getMACDSignalLine();
	}

	int closeSize = 0;

	ArrayList<Float> close = new ArrayList<Float>();
	public ArrayList<Float> macdArray = new ArrayList<Float>();
}
