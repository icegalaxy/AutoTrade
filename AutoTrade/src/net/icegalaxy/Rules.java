package net.icegalaxy;

public abstract class Rules implements Runnable {

	protected boolean hasContract;
	protected double tempCutLoss;
	protected double tempStopEarn;
	protected float refPt;
	protected float buyingPoint;
	private boolean globalRunRule;
	protected String className;
	double stopEarnPt;
	double cutLossPt;

	private final float CUTLOSS_FACTOR = 5.0F;
	private final float STOPEARN_FACTOR = 5.0F;

	boolean usingMA20;
	boolean usingMA10;
	boolean usingMA5;
	boolean shutdown;

	private static float balance; // holding contracts �� balance

	//can use default trade time, just do not use the setTime method
	int morningOpen = 92000;
	int morningClose = 113000;
	int noonOpen = 130500;
	int noonClose = 160000;
	int nightOpen = 173000;
	int nightClose = 231500;

	public Rules(boolean globalRunRule) {

		this.globalRunRule = globalRunRule;
		this.className = this.getClass().getSimpleName();

	}

	@Override
	public void run() {

		if (!globalRunRule) {
			while (Global.isRunning()) {
				sleep(1000);
			}
		} else {

			Global.addLog(className + " Acivated");

			while (Global.isRunning()) {

				usingMA20 = true;
				usingMA10 = true;
				usingMA5 = true;

				if (hasContract) {
					closeContract();
				} else {
					openContract();
				}

				sleep(1000);
			}
		}
	}

	protected boolean reachGreatProfitPt() {

		if (getStopEarnPt() < stopEarnPt)
			stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - stopEarnPt > buyingPoint; // it's
		// moving
		else if (Global.getNoOfContracts() < 0)
			return Global.getCurrentPoint() + stopEarnPt < buyingPoint;
		else
			return false;
	}

	//can choose not to set the night time
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen,
			int noonClose) {
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
	}
	
	//can choose to set the night time
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen,
			int noonClose, int nightOpen, int nightClose) {
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
		this.nightOpen = nightOpen;
		this.nightClose = nightClose;
	}

	public boolean isOrderTime() {

		int time = TimePeriodDecider.getTime();

		// System.out.println("Check: " + time + morningOpen + morningClose +
		// noonOpen + noonClose);

		if (time > morningOpen && time < morningClose)
			return true;
		else if (time > noonOpen && time < noonClose)
			return true;
		else if (time > nightOpen && time < nightClose)
			return true;
		else
			return false;
	}

	protected void updateCutLoss() {

		if (getCutLossPt() < cutLossPt)
			cutLossPt = getCutLossPt();

		// if (getStopEarnPt() < stopEarnPt)
		// stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0) {
			// if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() - cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint - cutLossPt > tempCutLoss)
				tempCutLoss = buyingPoint - cutLossPt;

			// if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }

		} else if (Global.getNoOfContracts() < 0) {
			// if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() + cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint + cutLossPt < tempCutLoss)
				tempCutLoss = buyingPoint + cutLossPt;

			// if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }
		}
	}

	protected void cutLoss() {
		if (Global.getNoOfContracts() > 0
				&& Global.getCurrentPoint() < tempCutLoss) {
			closeContract(className + ": CutLoss, short @ "
					+ Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0
				&& Global.getCurrentPoint() > tempCutLoss) {
			closeContract(className + ": CutLoss, long @ "
					+ Global.getCurrentAsk());
			shutdown = true;
		}
	}

	void stopEarn() {
		if (Global.getNoOfContracts() > 0
				&& Global.getCurrentPoint() < tempCutLoss)
			closeContract(className + ": StopEarn, short @ "
					+ Global.getCurrentBid());
		else if (Global.getNoOfContracts() < 0
				&& Global.getCurrentPoint() > tempCutLoss)
			closeContract(className + ": StopEarn, long @ "
					+ Global.getCurrentAsk());
	}

	public void closeContract(String msg) {

		boolean b = Sikuli.closeContract();
		Global.addLog(msg);
		Global.addLog("");
		Global.addLog("Current Balance: " + Global.balance + " points");
		Global.addLog("____________________");
		Global.addLog("");
		if (b)
			hasContract = false;
	}

	public void closeContract() {

		if (Global.getNoOfContracts() > 0) {
			tempCutLoss = buyingPoint - getCutLossPt();
			tempStopEarn = buyingPoint + getStopEarnPt();
		} else if (Global.getNoOfContracts() < 0) {
			tempCutLoss = buyingPoint + getCutLossPt();
			tempStopEarn = buyingPoint - getStopEarnPt();
		}

		stopEarnPt = getStopEarnPt();
		cutLossPt = 100; // �O�׫Y�O�I�A��ĤG��set���H�Pcut loss�Ӥj,
		// �O�ӫYMaximum

		while (!reachGreatProfitPt()) {

			updateCutLoss();
			cutLoss();

			// checkRSI();
			// checkDayHighLow();
			if (trendReversed()) {
				closeContract(className + ": Trend Reversed");
				return;
			}

			if (trendUnstable()) {
				closeContract(className + ": Trend Unstable");
				return;
			}
			// if (maReversed())
			// return;

			if (Global.isForceSellTime()) {
				closeContract("Force Sell");
				return;
			}

			if (Global.getNoOfContracts() == 0) { // �i��ڨ�Lrule
				// close���A��Trend
				// truned�A�̧Y�Y�४�աA��
				hasContract = false;
				break;
			}

			if (!hasContract)
				break;

			sleep(1000);
		}

		if (Global.getNoOfContracts() == 0) {
			hasContract = false;
			return;
		}

		if (!hasContract)
			return;

		// updateCutLoss();
		refPt = Global.getCurrentPoint();

		Global.addLog(className + ": Secure profit @ "
				+ Global.getCurrentPoint());

		while (hasContract) {

			if (Global.getNoOfContracts() == 0) {
				hasContract = false;
				break;
			}

			if (trendReversed2())
				closeContract(className + ": TrendReversed2");

			if (Global.isForceSellTime()) {
				closeContract("Force Sell");
				return;
			}

			updateStopEarn();

			stopEarn();

			// System.out.println("Temp Stop Earn" + tempCutLoss);

			sleep(1000);
		}
	}

	boolean trendReversed2() {
		return false;
	}

	boolean trendUnstable() {
		return false;
	}

	protected float getAGAL() {

		GetData.getShortTB().getRSI(); // ���[�O�y����AGAL�Y���|����
		return (GetData.getShortTB().getAG() + GetData.getShortTB().getAL()); // �Y���O�׫Y���Y�n�εfShort
		// Period
		// ALAG��Ĺ��
	}

	public void shortContract() {
		boolean b = Sikuli.shortContract();
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short @ " + Global.getCurrentBid());
		buyingPoint = Global.getCurrentBid();
		balance += buyingPoint;
	}

	public void longContract() {
		boolean b = Sikuli.longContract();
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long @" + Global.getCurrentAsk());
		buyingPoint = Global.getCurrentAsk();
		balance -= buyingPoint;
	}

	public abstract void openContract();

	void updateStopEarn() {

		if (Global.getNoOfContracts() > 0) {

			if (GetData.getShortTB().getLatestCandle().getLow() > tempCutLoss) {
				tempCutLoss = GetData.getShortTB().getLatestCandle().getLow();
//				usingMA20 = false;
//				usingMA10 = false;
//				usingMA5 = false;
			}

		} else if (Global.getNoOfContracts() < 0) {

			if (GetData.getShortTB().getLatestCandle().getHigh() < tempCutLoss) {
				tempCutLoss = GetData.getShortTB().getLatestCandle().getHigh();
//				usingMA20 = false;
//				usingMA10 = false;
//				usingMA5 = false;
			}
		}

	}

	void secondStopEarn() {

		if (Global.getNoOfContracts() > 0) {
			if (Global.getCurrentPoint() < GetData.getLongTB().getEMA(5)) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: Current Pt < EMA5");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (Global.getCurrentPoint() > GetData.getLongTB().getEMA(5)) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: Current Pt > EMA5");

			}
		}

	}

	void thirdStopEarn() {

		if (Global.getNoOfContracts() > 0) {
			if (GetData.getLongTB().getEMA(5) < GetData.getLongTB().getEMA(6)) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (GetData.getLongTB().getEMA(5) > GetData.getLongTB().getEMA(6)) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	double getCutLossPt() {
		return getAGAL() * CUTLOSS_FACTOR;
		// return GetData.getShortTB().getHL15().getFluctuation() /
		// CUTLOSS_FACTOR;
	}

	double getStopEarnPt() {
		return getAGAL() * STOPEARN_FACTOR;
		// return GetData.getShortTB().getHL15().getFluctuation() /
		// STOPEARN_FACTOR;
	}

	public void setName(String s) {
		className = s;
	}

	public static synchronized float getBalance() {
		if (Global.getNoOfContracts() > 0)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else if (Global.getNoOfContracts() < 0)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else {
			balance = 0;
			return balance;
		}
	}

	public static synchronized void setBalance(float balance) {
		Rules.balance = balance;
	}

	public abstract TimeBase getTimeBase();

	boolean maRising(int period) {
		return getTimeBase().isMARising(period, 1);
	}

	boolean maDropping(int period) {
		return getTimeBase().isMADropping(period, 1);
	}

	boolean emaRising(int period) {
		return getTimeBase().isEMARising(period, 1);
	}

	boolean emaDropping(int period) {
		return getTimeBase().isEMADropping(period, 1);
	}

	boolean trendReversed() {

		// double slope = 0;
		// double longSlope = 0;
		//		
		// if (Global.getNoOfContracts() > 0){
		// if (GetData.getSec10TB().getMainDownRail()
		// .getSlope() != 100)
		// slope = GetData.getSec10TB().getMainDownRail()
		// .getSlope();
		//			
		// if (getTimeBase().getMainUpRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainUpRail().getSlope();
		//			
		// }
		// if (Global.getNoOfContracts() < 0){
		//			
		// if (GetData.getSec10TB().getMainUpRail().getSlope() != 100)
		// slope = GetData.getSec10TB().getMainUpRail().getSlope();
		//			
		// if (getTimeBase().getMainDownRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainDownRail().getSlope();
		// }
		// return slope > 5 && slope > longSlope*2;

		return false;

	}

	protected void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public double getProfit() {
		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		else
			return buyingPoint - Global.getCurrentPoint();
	}

	// Danny �l�ȥ�e�w��V
	public boolean isUpTrend() {
		return GetData.getM15TB().getMA(20) > GetData.getM15TB().getEMA(50)
				&& GetData.getLongTB().getEMA(50) > GetData.getLongTB().getEMA(
						240);
	}

	public boolean isDownTrend() {
		return GetData.getM15TB().getMA(20) < GetData.getM15TB().getEMA(50)
				&& GetData.getLongTB().getEMA(50) < GetData.getLongTB().getEMA(
						240);
	}

	void openOHLC(double ohlc) {
		if (Global.getCurrentPoint() <= ohlc + 5
				&& Global.getCurrentPoint() >= ohlc - 5) {

			Global.addLog(className + ": Entered waiting zone");
			Global.addLog("MA20(M15): " + GetData.getM15TB().getMA(20)
					+ "; EMA50(M15): " + GetData.getM15TB().getEMA(50)
					+ "; EMA50(M5): " + GetData.getLongTB().getEMA(50)
					+ "; EMA240(M5): " + GetData.getLongTB().getEMA(240));

			while (Global.getCurrentPoint() <= ohlc + 10
					&& Global.getCurrentPoint() >= ohlc - 10)
				sleep(1000);

			if (Global.getCurrentPoint() > ohlc + 10 && isUpTrend()) {
				longContract();
			} else if (Global.getCurrentPoint() < ohlc - 10 && isDownTrend()) {
				shortContract();
			}
		}
	}

	public boolean isAfternoonTime() {

		int time = TimePeriodDecider.getTime();

		if (time > noonOpen && time < noonClose)
			return true;
		else
			return false;
	}
}
