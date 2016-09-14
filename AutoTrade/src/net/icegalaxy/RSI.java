package net.icegalaxy;

import java.util.ArrayList;

public class RSI {

	public RSI(ArrayList<Float> close, int noOfPeriods) {

		this.noOfPeriods = noOfPeriods;
		this.close = close; // passing an
		// object，咁Global.close變的話呢度變唔變？最好變，因為唔變的話getRSI()會唔郁
		// 如果真係唔郁的話，可以係TIThread做野，將create Object果句放番入個loop

	}

	private void calculateFirstAG() {

		if (close.size() < noOfPeriods + 2) {
			firstAverageGain = 1;
			firstAverageLoss = 1;
			return;
		}
		float totalGain = 0;
		float totalLoss = 0;

		for (int i = noOfPeriods; i > 0; i--) { // 最後會loop到去
			// i=1，點解唔要i=0係因為果個數只係用黎減
			// i=1個數，
			// 係個if果度最後會用到，如果唔係會變左get(-1)
			// First AG係要十五個data先計到的

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				totalGain += different; // 之前居然計錯數，直按加個close.get(i)落去totalGain度，但都有錢賺，shit!
			else if (different < 0)
				totalLoss += Math.abs(different); // 要正數

			// 相減等於零的話就唔洗理，唔洗加，又為無AG or AL
		}

		firstAverageGain = totalGain / noOfPeriods;
		firstAverageLoss = totalLoss / noOfPeriods;
	}

	private void calculateAG() {
		
		if (close.size() < noOfPeriods + 2) {
			averageGain = 1;
			averageLoss = 1;
			return;
		}

		float temAG = firstAverageGain;
		float temAL = firstAverageLoss;

		for (int i = noOfPeriods + 1; i < close.size(); i++) {

			float gain = 0; // 係度initial下面可以做少好多野
			float loss = 0;

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				gain = different;
			else if (different < 0)
				loss = Math.abs(different);
			// 相減等於零唔洗理，gain and loss會維持0

			temAG = (temAG * (noOfPeriods - 1) + gain) / noOfPeriods;
			temAL = (temAL * (noOfPeriods - 1) + loss) / noOfPeriods;
		}

		averageGain = temAG;
		averageLoss = temAL;

	}

	// previous RSI, 唔郁的
	public float getRSI() {

		calculateFirstAG();
		calculateAG();

		if (averageLoss == 0) // 避免除零
			return 100;

		float f = 100 - 100 / (1 + averageGain / averageLoss);
		// System.out.println("CurrentRSI: " + f);
		return f;
	}

	// current RSI,而家郁緊，所以要take currentPoint as para
	public float getRSI(float currentPoint) {

		if (close.size() <= noOfPeriods) // check下夠唔夠料計，如果唔夠會出-1
			return -1;

		float currentGain = 0;
		float currentLoss = 0;

		if ((currentPoint - close.get(close.size() - 1)) >= 0) // 同最後個record比較
			currentGain = currentPoint - close.get(noOfPeriods);
		else
			currentLoss = currentPoint - close.get(noOfPeriods);

		float currentAverageGain = ((averageGain * (noOfPeriods - 1)) + currentGain)
				/ noOfPeriods;
		float currentAverageLoss = ((averageLoss * (noOfPeriods - 1)) + currentLoss)
				/ noOfPeriods;

		float f = 100 - 100 / (1 + currentAverageGain / currentAverageLoss);
		return f;

	}

	public float getAverageGain() {
		return averageGain;
	}

	public float getAverageLoss() {
		return averageLoss;
	}
	
	public float getALAGsoFar() {
		
		if (close.size() < 2) {
			return 0;
		}
				

		float temAG = 0;
		float temAL = 0;

		for (int i = 1; i < close.size(); i++) {

			float gain = 0; // 係度initial下面可以做少好多野
			float loss = 0;

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				gain = different;
			else if (different < 0)
				loss = Math.abs(different);
			// 相減等於零唔洗理，gain and loss會維持0

			temAG = (temAG * (close.size() - 1) + gain) / close.size();
			temAL = (temAL * (close.size() - 1) + loss) / close.size();
		}

		return temAG + temAL;

	}

	int noOfPeriods;
	ArrayList<Float> close = new ArrayList<Float>();
	float firstAverageGain;
	float firstAverageLoss;
	float averageGain;
	float averageLoss;
	

}
