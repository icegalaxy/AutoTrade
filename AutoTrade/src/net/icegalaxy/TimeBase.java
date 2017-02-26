package net.icegalaxy;




import java.util.ArrayList;




public class TimeBase {
	
	public EMA[] EMAs = new EMA[6];
	
	ArrayList<Float> point = new ArrayList<Float>();
	ArrayList<Float> quantity = new ArrayList<Float>();
	ArrayList<Float> agalPt = new ArrayList<Float>(); 
	
	private TechnicalIndicators ti;
	private TechnicalIndicators agal;
	private RSI rsi14;
	public ArrayList<Candle> candle;
	public ArrayList<Rail> upRails;
	public ArrayList<Rail> downRails;

	private int baseMin;
	private int mainUpRailIndex;
	private int mainDownRailIndex;
	
	private double totalQuantity;
	
	private int maDirection = 1;
	private int maRetained;


	private float maSlope = 1;
	
	public TimeBase() {
		this.ti = new TechnicalIndicators(point); //pointer?
		this.agal = new TechnicalIndicators(agalPt);
		this.rsi14 = new RSI(point, 14);
		this.candle = new ArrayList<Candle>();

		this.upRails = new ArrayList<Rail>();
		this.downRails = new ArrayList<Rail>();

		this.upRails.add(new Rail(1.0D, true, 1));
		this.downRails.add(new Rail(1.0D, false, 1));
		
		
	}

	public void setBaseMin(int baseMin) {
		this.baseMin = baseMin;
	}

	public void addCandle(String time, double high, double low, double open,
			double close, double volume) {
		volume -= total(this.quantity); //�L�Ϊ��A����
		this.candle.add(new Candle(time, high, low, open, close, getQuantity(), rsi14
				.getRSI()));

		
		
		
		double upRail = -1.0D;
		mainUpRailIndex = 0;
		for (int i = upRails.size() - 1; i >= 0; i--) {
			upRails.get(i).rail = upRails.get(i).getTheRail();
			if (i > upRails.size() - 1) {
//				System.out.println("Up Rail: " + i + " Removed");
				continue;
			}
			// System.out.println("Up Rail " + i + ": " +
			// upRails.get(i).getRail()
			// + " Org: " + upRails.get(i).getOrgIndex() + " Support: "
			// + upRails.get(i).supportIndex + " Slope: "
			// + upRails.get(i).getSlope() + " Retain: " +
			// upRails.get(i).slopeRetained);

			try {
				if (upRails.get(i).getRail() >= upRail) {
					upRail = upRails.get(i).getRail();
					mainUpRailIndex = i;
				}
			} catch (IndexOutOfBoundsException e) {
//				System.out.println("Up Rail " + i + " Removed");
			}
		}
//		System.out.println("Main Up Rail(" + baseMin + "): "
//				+ upRails.get(mainUpRailIndex).getRail());

		double downRail = 99999.0D;
		mainDownRailIndex = 0;
		for (int i = downRails.size() - 1; i >= 0; i--) {
			downRails.get(i).rail = downRails.get(i).getTheRail();
			if (i > downRails.size() - 1) {
//				System.out.println("Down Rail: " + i + " Removed");
				continue;
			}

			// System.out.println("Down Rail " + i + ": "
			// + downRails.get(i).getRail() + " Org: "
			// + downRails.get(i).getOrgIndex() + " Support: "
			// + downRails.get(i).supportIndex + " Slope: "
			// + downRails.get(i).getSlope() + " Retain: " +
			// downRails.get(i).slopeRetained);

			try {
				if (downRails.get(i).getRail() <= downRail
						&& downRails.get(i).getRail() > 0.0D) {
					downRail = downRails.get(i).getRail();
					mainDownRailIndex = i;
				}
			} catch (IndexOutOfBoundsException e) {
//				System.out.println("Down Rail " + i + " Removed");
			}
		}
//		System.out.println("Main Down Rail(" + baseMin + "): "
//				+ downRails.get(mainDownRailIndex).getRail());

		if ((getMA(10) - getMA(20)) * maDirection > 0) {
			maRetained++;
		} else {
			maDirection = maDirection * -1;
			maRetained = 0;
		}

	}
	
	//for added previous data with volume
	public void addCandleHistory(String time, double high, double low, double open,
			double close, double volume) {

		this.candle.add(new Candle(time, high, low, open, close, volume, rsi14
				.getRSI()));

		
		
		
		double upRail = -1.0D;
		mainUpRailIndex = 0;
		for (int i = upRails.size() - 1; i >= 0; i--) {
			upRails.get(i).rail = upRails.get(i).getTheRail();
			if (i > upRails.size() - 1) {
//				System.out.println("Up Rail: " + i + " Removed");
				continue;
			}
			// System.out.println("Up Rail " + i + ": " +
			// upRails.get(i).getRail()
			// + " Org: " + upRails.get(i).getOrgIndex() + " Support: "
			// + upRails.get(i).supportIndex + " Slope: "
			// + upRails.get(i).getSlope() + " Retain: " +
			// upRails.get(i).slopeRetained);

			try {
				if (upRails.get(i).getRail() >= upRail) {
					upRail = upRails.get(i).getRail();
					mainUpRailIndex = i;
				}
			} catch (IndexOutOfBoundsException e) {
//				System.out.println("Up Rail " + i + " Removed");
			}
		}
//		System.out.println("Main Up Rail(" + baseMin + "): "
//				+ upRails.get(mainUpRailIndex).getRail());

		double downRail = 99999.0D;
		mainDownRailIndex = 0;
		for (int i = downRails.size() - 1; i >= 0; i--) {
			downRails.get(i).rail = downRails.get(i).getTheRail();
			if (i > downRails.size() - 1) {
//				System.out.println("Down Rail: " + i + " Removed");
				continue;
			}

			// System.out.println("Down Rail " + i + ": "
			// + downRails.get(i).getRail() + " Org: "
			// + downRails.get(i).getOrgIndex() + " Support: "
			// + downRails.get(i).supportIndex + " Slope: "
			// + downRails.get(i).getSlope() + " Retain: " +
			// downRails.get(i).slopeRetained);

			try {
				if (downRails.get(i).getRail() <= downRail
						&& downRails.get(i).getRail() > 0.0D) {
					downRail = downRails.get(i).getRail();
					mainDownRailIndex = i;
				}
			} catch (IndexOutOfBoundsException e) {
//				System.out.println("Down Rail " + i + " Removed");
			}
		}
//		System.out.println("Main Down Rail(" + baseMin + "): "
//				+ downRails.get(mainDownRailIndex).getRail());

		if ((getMA(10) - getMA(20)) * maDirection > 0) {
			maRetained++;
		} else {
			maDirection = maDirection * -1;
			maRetained = 0;
		}

	}
	
	public boolean isOutsideDay(){
		
		if (candle.size() < 2)
			return false;
		
		Candle currentCandle = candle.get(candle.size()-1);
		Candle previosCandle = candle.get(candle.size()-2);
		
	
		return currentCandle.getHigh() > previosCandle.getHigh()
			&& currentCandle.getLow() < previosCandle.getLow();
	}

	public HighLow getHL(int period){
		HighLow hl = new HighLow(period);
		hl.calculateHighLow();
		return hl;
	}
	
	
	public float getAverageHL(int periods) throws NotEnoughPeriodException{
		
		if (periods > candle.size())
			throw new NotEnoughPeriodException();
		
		double total = 0;
		
		for (int i=candle.size()-1; i >= candle.size() - periods; i--){
			total = total + (candle.get(i).getHigh() - candle.get(i).getLow());
		}
		
		return (float) (total/periods);
		
		
	}

	public int getMARetain() {
		return maRetained;
	}

	public Rail getMainUpRail() {
		return (upRails.get(this.mainUpRailIndex));
	}

	public Rail getMainDownRail() {
		return (downRails.get(this.mainDownRailIndex));
	}

	public void addPoint(Float f) {
		this.point.add(f);
		rsi14.getRSI();
		this.agalPt.add(getAG() + getAL());
	}

	private void addQuantity(Float totalQuantity) {
		if (this.quantity.size() == 0){
			this.quantity.add(totalQuantity);
			this.totalQuantity = totalQuantity;
		}
		else{
			this.quantity.add(Float.valueOf(totalQuantity.floatValue()
					- total(this.quantity)));
			this.totalQuantity = totalQuantity;
		}
	}
	
	public double getTotalQuantity(){
		return totalQuantity;
	}

	public void addData(Float point, Float totalQuantity) {
		addPoint(point);
		addQuantity(totalQuantity);
	}

	private float total(ArrayList<Float> al) {
		float total = 0f;
		for (int i = 0; i < al.size(); i++) {
			total += al.get(i);
		}
		return total;
	}
	
	
	private float total(ArrayList<Float> al, int noOfPeriods) throws NotEnoughPeriodException {
		
		if (al.size() < noOfPeriods)
			throw new NotEnoughPeriodException();
		
		float total = 0f;
		for (int i = al.size()-1; i >= al.size() - noOfPeriods; i--) {
			total += al.get(i);
		}
		return total;
	}
	
	public float getStandD(){
		return ti.getStandardDeviation(20);
	}

	public double getQuatityByPeriods(int periods){
		
		float total = 0f;
		for (int i = 0; i < periods; i++) {
			total += candle.get(i).getVolume();
		}
		return total;
		
	}
	
	// current status
	public boolean isQuantityRising() {
		float ma5;

		if (quantity.size() == 0)
			return true; // for����quantity����lr
		else if (quantity.size() < 5)
			return false;
		ma5 = TI.getEMA(quantity, 5);
		return ma5 > getAverageQuantity();
	}

	public float getMA5Quantity() {
		float ma5;

		if (quantity.size() == 0)
			return 0; // for����quantity����lr
		else if (quantity.size() < 5)
			return 0;
		ma5 = TI.getEMA(quantity, 5);
		return ma5;
	}

	public float getAverageQuantity() {
		
		return total(quantity) / quantity.size();
		
//		try {
//			return total(quantity, 60) / 60;
//		} catch (NotEnoughPeriodException e) {
//			return 99999;			
//		}
	}

	// status over no of periods
	public boolean isQuantityRising(int periods) {

		if (quantity.size() == 0)
			return true; // for����quantity����lr
		else if (quantity.size() < periods)
			return false;

		for (int i = 0; i < periods - 1; i++) {
			boolean b = quantity.get(quantity.size() - 1 - i) > quantity
					.get(quantity.size() - 2 - i);
			if (!b)
				return false;
		}

		return true;
	}

	public boolean isQuantityDropping() {
		float ma5;
		if (quantity.size() < 5)
			return false;
		ma5 = TI.getEMA(quantity, 5);
		return ma5 < total(quantity) / quantity.size();
	}

	public boolean isQuantityDropping(int periods) {

		if (quantity.size() == 0)
			return true; // for����quantity����lr
		else if (quantity.size() < periods)
			return false;

		for (int i = 0; i < periods - 1; i++) {
			boolean b = quantity.get(quantity.size() - 1 - i) < quantity
					.get(quantity.size() - 2 - i);
			if (!b)
				return false;
			if (!b)
				return false;
		}

		return true;
	}

	public float getEMA(int period) {
		return ti.getEMA(period);
	}

	public float getMA(int period) {
		return ti.getMovingAverage(period);
	}
	
	public float getPreviousMA(int period) {
		return ti.getMovingAverage(period, 1);
	}
	
	public float getAGAL(int period) {
		return agal.getMovingAverage(period);
	}



	public ArrayList<Float> getMAList(int period, int noOfPeriodsWanted) {
		ArrayList<Float> mas = new ArrayList<Float>();

		for (int i = noOfPeriodsWanted - 1; i >= 0; i--) {
			mas.add(ti.getMovingAverage(period, i));
		}
		return mas;
	}

	public float getRSI() {
		return rsi14.getRSI();
	}

	public float getAG() {
		return rsi14.getAverageGain();
	}

	public float getAL() {
		return rsi14.getAverageLoss();
	}

	public boolean isMARising(int period, float pointsToRise) {

		if (point.size() < period + 2)
			return false;
		else
			return ti.getMovingAverage(period, 0)
					- ti.getMovingAverage(period, 1) > pointsToRise // 0�Y�̷s��
					&& ti.getMovingAverage(period, 1)
							- ti.getMovingAverage(period, 2) > pointsToRise;
	}
	
	public boolean isEMARising(int period, float pointsToRise) {

		if (point.size() < period + 2)
			return false;
		else
			return ti.getEMA(period, 0)
					- ti.getEMA(period, 1) > pointsToRise // 0�Y�̷s��
					&& ti.getEMA(period, 1)
							- ti.getEMA(period, 2) > pointsToRise;
	}

//	public boolean isMARising(int period, int periodsToCompare) {
//
//		boolean rising = true;
//		boolean acceleating = true;
//
//		if (periodsToCompare < 3)
//			return false;
//
//		if (point.size() < period + periodsToCompare)
//			return false;
//
//		for (int i = 0; i < periodsToCompare - 1; i++) {
//			if (ti.getMovingAverage(period, i) <= ti.getMovingAverage(period,
//					i + 1))
//				rising = false;
//		}
//
//		for (int i = 0; i < periodsToCompare - 2; i++) {
//			if (ti.getMovingAverage(period, i)
//					- ti.getMovingAverage(period, i + 1) <= ti
//					.getMovingAverage(period, i + 1)
//					- ti.getMovingAverage(period, i + 2))
//				acceleating = false;
//		}
//
//		return rising && acceleating;
//	}

	public boolean isMADropping(int period, float pointsToDrop) {

		if (point.size() < period + 2)
			return false;
		else
			return ti.getMovingAverage(period, 2)
					- ti.getMovingAverage(period, 1) > pointsToDrop
					&& ti.getMovingAverage(period, 1)
							- ti.getMovingAverage(period, 0) > pointsToDrop;

	}
	
	public boolean isEMADropping(int period, float pointsToDrop) {

		if (point.size() < period + 2)
			return false;
		else
			return ti.getEMA(period, 2)
					- ti.getEMA(period, 1) > pointsToDrop
					&& ti.getEMA(period, 1)
							- ti.getEMA(period, 0) > pointsToDrop;

	}

//	public boolean isMADropping(int period, int periodsToCompare) {
//
//		boolean rising = true;
//		boolean acceleating = true;
//
//		if (periodsToCompare < 3)
//			return false;
//
//		if (point.size() < period + periodsToCompare)
//			return false;
//
//		for (int i = 0; i < periodsToCompare - 1; i++) {
//			if (ti.getMovingAverage(period, i) >= ti.getMovingAverage(period,
//					i + 1))
//				rising = false;
//		}
//
//		for (int i = 0; i < periodsToCompare - 2; i++) {
//			if (ti.getMovingAverage(period, i)
//					- ti.getMovingAverage(period, i + 1) >= ti
//					.getMovingAverage(period, i + 1)
//					- ti.getMovingAverage(period, i + 2))
//				acceleating = false;
//		}
//
//		return rising && acceleating;
//	}

	public float getMACD() {
		return ti.getMACD();
	}

	public float getMACDHistogram() {
		return ti.getMACDHistogram();
	}

	public float getQuantity() {
		
		//for csvParser
		if(quantity.size() == 0)
			return 0;
		
		return quantity.get(quantity.size() - 1);
	}
	
	
	public EMA getEma5()
	{
		return EMAs[0];
	}

	public EMA getEma25()
	{
		return EMAs[1];
	}

	public EMA getEma50()
	{
		return EMAs[2];
	}

	public EMA getEma100()
	{
		return EMAs[3];
	}

	public EMA getEma250()
	{
		return EMAs[4];
	}

	public EMA getEma1200()
	{
		return EMAs[5];
	}


	public ArrayList<Candle> getCandles() {
		return this.candle;
	}

	public Candle getLatestCandle() {
		try {return candle.get(candle.size() - 1);
		}
		catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	public Candle getPreviousCandle(int noOfPeriodBefore) {
		
		if (candle.size() < 1 + noOfPeriodBefore)
			throw new ArrayIndexOutOfBoundsException();
		
		try {
			return candle.get(candle.size() - 1 - noOfPeriodBefore);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	class Rail {
		public ArrayList<Rail> rails;
		private boolean isUpRail;
		private double direction;
		private int railIndex;
		private final double averageOneMinPt;
		private final int timeInterval = 2;
		// private int orgMaxRetain = 1000;
		private int shiftedPeriod;
		private int originIndex;
		private int supportIndex;
		private double rail;
		public int slopeRetained;
		public int orgRetained;
		double slope = 100.0D;
		private double bufferSlope;

		public Rail(double averageOneMinPt, boolean isUpRail, int shiftedPeriod) {
			this.isUpRail = isUpRail;
			this.averageOneMinPt = averageOneMinPt;
			this.shiftedPeriod = shiftedPeriod;

			if (isUpRail) {
				this.direction = 1;
				this.railIndex = upRails.size();
				this.rails = upRails;
			} else {
				this.direction = -1;
				this.railIndex = downRails.size();
				this.rails = downRails;
			}
		}

		public void addNewRail(double averageOneMinPt, boolean isUpRail,
				int shiftedPeriod) {

			if (shiftedPeriod == candle.size() - 1)
				return;

			Rail r = new Rail(averageOneMinPt, isUpRail, shiftedPeriod);

			if (r.isUpRail)
				upRails.add(r);
			else
				downRails.add(r);
		}

		private int getOriginIndex() {
			double origin = 99999 * direction;

//			System.out.println("ShiftedPeriod: " + shiftedPeriod);
//			System.out.println("Candle Size: " + candle.size());
			
			for (int i = shiftedPeriod; i < candle.size(); ++i){
//				System.out.println("GetPoint I: " + getPoint(i));
				if ((getPoint(i) - origin) * direction < 0.0D) {
					origin = getPoint(i);
				} else {
					originIndex = (i - 1);
					return originIndex;
				}

			}
			originIndex = (candle.size() - 1);
//			System.out.println("Loop Out, orginIndex:" + originIndex);
			return originIndex;
		}

		private double getTheSlope() {
			int origin = getOriginIndex();

//			System.out.println("Candle size: " + candle.size());
//			System.out.println("Origin: " + origin);
			
			if (candle.size() - origin < timeInterval) {
				return 0.0D;
			}

			if (supportIndex < origin)
				supportIndex = origin;

			for (int i = origin + 1; i < candle.size(); i++) {
				if (slope(origin, i) - averageOneMinPt < 0.0D) {
					supportIndex = i;
					break;
				}

				if (slope(origin, i) < slope) {
					supportIndex = i;
					slope = slope(origin, i);
					slopeRetained = 0;
				}

			}

			if (slope(origin, supportIndex) - averageOneMinPt < 0.0D) {
				if (railIndex == 0) {
					slopeRetained = 0;
					orgRetained = 0;
					slope = 100.0D;
					shiftedPeriod = (origin + 1);
					getTheSlope();
				} else if (isLatestRail()) {
					rails.remove(railIndex);
				} else { // �S���Y�Ĥ@���S���Y�̫�G��
					slopeRetained = 0;
					slope = 100.0D;
				}
			} else {
				if (supportIndex - origin < timeInterval) {
					slopeRetained = 0;
					if (isLatestRail())
						addNewRail(this.averageOneMinPt, this.isUpRail,
								supportIndex);
					return 0.0D;
				}
				if (slopeRetained < getRetainRef()) {
					slopeRetained++;
					orgRetained++;
					if (isLatestRail())
						addNewRail(this.averageOneMinPt, this.isUpRail,
								supportIndex);
					return 0.0D;
				}

				slopeRetained++;
				orgRetained++;
				slope = slope(origin, supportIndex);
				if (isLatestRail())
					addNewRail(this.averageOneMinPt, isUpRail, supportIndex);
				return slope;
			}
			return 0.0D;
		}

		private double getRetainRef() {
			double ref;

			ref = (1 / slope) * 20;

			if (ref < 2)
				return 2;
			else
				return ref;
		}

		private double slope(int orginIndex, int candleIndex) {
			double dt = candleIndex - orginIndex;
			double dx = getPoint(candleIndex) - getPoint(orginIndex);

			if (dt == 0.0D) {
				return 0.0D;
			}

			return (dx * direction) / dt;
		}

		private double getTheRail() {
			double slope = getTheSlope();

			double org = getPoint(this.originIndex);
			if (slope == 0.0D)
				return 0.0D;

			this.slope = slope;

			double dt2 = candle.size() - 1 - this.originIndex;
			double dx2 = slope * dt2;

			return (org + dx2 * this.direction);
		}

		public double getRail() {
			return this.rail;
		}

		public double getSlope() {
			if (this.slope == 100)
				return 0;
			else
				return this.slope;

		}

		public int getOrgIndex() {
			return this.originIndex;
		}

		private double getPoint(int index) {
			if (isUpRail)
				return ((Candle) candle.get(index)).getLow();

			return ((Candle) candle.get(index)).getHigh();
		}

		private boolean isLatestRail() {
			if (isUpRail)
				return railIndex == upRails.size() - 1;

			return railIndex == downRails.size() - 1;
		}
	}

	class HighLow {

		private int periods;

		private double tempHigh;
		private double tempLow;
		private double tempRsiHigh;
		private double tempRsiLow;
		
		private double fluctuation;
		private double rsiFluctuation;

		public HighLow(int periods) {
			this.periods = periods;
		}

		void calculateHighLow() {
			double periodHigh = 0;
			double periodLow = 99999;
			double periodRsiHigh = 0;
			double periodRsiLow = 100;

			for (int i = candle.size() - 1; i >= candle.size() - periods; i--) {
				if (i < 0)
					break;

				if (i == 166) // ����
					break;

				if (candle.get(i).getRsi() > periodRsiHigh)
					periodRsiHigh = candle.get(i).getRsi();
				if (candle.get(i).getRsi() < periodRsiLow)
					periodRsiLow = candle.get(i).getRsi();

				if (candle.get(i).getHigh() > periodHigh)
					periodHigh = candle.get(i).getHigh();
				if (candle.get(i).getLow() < periodLow)
					periodLow = candle.get(i).getLow();
			}

			tempHigh = periodHigh;
			tempLow = periodLow;
			fluctuation = periodHigh - periodLow;

			tempRsiHigh = periodRsiHigh;
			tempRsiLow = periodRsiLow;
			rsiFluctuation = periodRsiHigh - periodRsiLow;

		}

		public double getTempHigh() {
			return tempHigh;
		}

		public double getTempLow() {
			return tempLow;
		}

		public double getTempRsiHigh() {
			return tempRsiHigh;
		}

		public double getTempRsiLow() {
			return tempRsiLow;
		}

		public double getFluctuation() {
			return fluctuation;
		}

		public double getRsiFluctuation() {
			return rsiFluctuation;
		}
		
		
	}

	
}