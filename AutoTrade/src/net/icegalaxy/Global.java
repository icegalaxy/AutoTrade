package net.icegalaxy;



public class Global {

	public static synchronized boolean isTradeTime() {
		return isTradeTime;
	}

	public static synchronized void setTradeTime(boolean isTradeTime) {
		Global.isTradeTime = isTradeTime;
	}

	public static synchronized boolean hasLoggedIn() {
		return hasLoggedIn;
	}

	public static synchronized void setLoggedIn(boolean hasLoggedIn) {
		Global.hasLoggedIn = hasLoggedIn;
	}

	public static synchronized boolean isRunning() {
		return isRunning;
	}

	public static synchronized void setRunning(boolean isRunning) {
		Global.isRunning = isRunning;
	}

	public static synchronized float getCurrentPoint() {
		return currentPoint;
	}

	public static synchronized void setCurrentPoint(float currentPoint) {
		Global.currentPoint = currentPoint;
	}

	public static synchronized float getDayHigh() {
		return dayHigh;
	}

	public static synchronized void setDayHigh(float dayHigh) {
		Global.dayHigh = dayHigh;
	}

	public static synchronized float getDayLow() {
		return dayLow;
	}

	public static synchronized void setDayLow(float dayLow) {
		Global.dayLow = dayLow;
	}



	public static synchronized float getTempHigh() {
		return tempHigh;
	}

	public static synchronized void setTempHigh(float tempHigh) {
		Global.tempHigh = tempHigh;
	}

	public static synchronized float getTempLow() {
		return tempLow;
	}

	public static synchronized void setTempLow(float tempLow) {
		Global.tempLow = tempLow;
	}

	public static synchronized float getCutLost() {
		return cutLost;
	}

	public static synchronized void setCutLost(float cutLost) {
		Global.cutLost = cutLost;
	}


	public static synchronized boolean isOrderTime() {
		return isOrderTime;
	}

	public static synchronized void setOrderTime(boolean isOrderTime) {
		Global.isOrderTime = isOrderTime;
	}

	public static synchronized boolean isRuleOneTime() {
		return isRuleOneTime;
	}

	public static synchronized void setRuleOneTime(boolean isRuleOneTime) {
		Global.isRuleOneTime = isRuleOneTime;
	}


	public static synchronized void addLog(String msg) {
		msg = GetData.getTime() + "	" + msg + "\r\n";
		System.out.println(msg);
		Global.log.append(msg);
		DB.stringtoFile(Global.log.toString(), "Z:\\TradeData\\log " + getToday() + ".txt");
	}

	public static synchronized void clearLog() {

		Global.log = new StringBuffer("");
	}
	
	public static synchronized String getLog() {
		return Global.log.toString();
	}

	
	

	public static synchronized boolean isForceSellTime() {
		return isForceSellTime;
	}

	public static synchronized void setForceSellTime(boolean isForceSellTime) {
		Global.isForceSellTime = isForceSellTime;
	}


	public static synchronized boolean isQuotePowerTime() {
		return isQuotePowerTime;
	}

	public static synchronized void setQuotePowerTime(boolean isQuotePowerTime) {
		Global.isQuotePowerTime = isQuotePowerTime;
	}

	



	




	public static synchronized float getCurrentBid() {
		return currentBid;
	}

	public static synchronized void setCurrentBid(float currentBid) {
		Global.currentBid = currentBid;
	}

	public static synchronized float getCurrentAsk() {
		return currentAsk;
	}

	public static synchronized void setCurrentAsk(float currentAsk) {
		Global.currentAsk = currentAsk;
	}



	public static synchronized boolean backHandLong() {
		return backHandLong;
	}

	public static synchronized void setBackHandLong(boolean backHandLong) {
		Global.backHandLong = backHandLong;
	}

	public static synchronized boolean backHandShort() {
		return backHandShort;
	}

	public static synchronized void setBackHandShort(boolean backHandShort) {
		Global.backHandShort = backHandShort;
	}

	

//	public static synchronized boolean ma5Rising() {
//		return ma5Rising;
//	}
//
//	public static synchronized void setMa5Rising(boolean ma5Rising) {
//		Global.ma5Rising = ma5Rising;
//	}




	public static synchronized int getNoOfContracts() {
		return noOfContracts;
	}

	public static synchronized void setNoOfContracts(int noOfContracts) {
		Global.noOfContracts = noOfContracts;
	}
	

	public static synchronized float getCurrentDeal() {
		return currentDeal;
	}

	public static synchronized void setCurrentDeal(float currentDeal) {
		Global.currentDeal = currentDeal;
	}



	public static synchronized float getGreatProfit() {
		return greatProfit;
	}

	public static synchronized void setGreatProfit(float greatProfit) {
		Global.greatProfit = greatProfit;
	}



	
	



	public static synchronized boolean isSideWay() {
		return isSideWay;
	}

	public static synchronized void setSideWayTrue() {
		Global.isSideWay = true;
		Global.isUpTrend = false;
		Global.isDownTrend = false;
	}

	public static synchronized boolean isUpTrend() {
		return isUpTrend;
	}

	public static synchronized void setUpTrendTrue() {
		Global.isSideWay = false;
		Global.isUpTrend = true;
		Global.isDownTrend = false;
	}

	public static synchronized boolean isDownTrend() {
		return isDownTrend;
	}

	public static synchronized void setDownTrendTrue() {
		Global.isSideWay = false;
		Global.isUpTrend = false;
		Global.isDownTrend = true;
	}

	public static synchronized boolean isLowFluctuation() {
		return isLowFluctuation;
	}

	public static synchronized void setLowFluctuation(boolean isLowFluctuation) {
		Global.isLowFluctuation = isLowFluctuation;
	}

	public static double getGap() {
		return gap;
	}

	public static void setGap(double gap) {
		Global.gap = gap;
	}

	public static synchronized double getpOpen() {
		return pOpen;
	}

	public static synchronized void setpOpen(double pOpen) {
		Global.pOpen = pOpen;
	}

	public static synchronized double getpHigh() {
		return pHigh;
	}

	public static synchronized void setpHigh(double pHigh) {
		Global.pHigh = pHigh;
	}

	public static synchronized double getpLow() {
		return pLow;
	}

	public static synchronized void setpLow(double pLow) {
		Global.pLow = pLow;
	}

	public static synchronized double getpClose() {
		return pClose;
	}

	public static synchronized void setpClose(double pClose) {
		Global.pClose = pClose;
	}

	public static synchronized double getpFluc() {
		return pFluc;
	}

	public static synchronized void setpFluc(double pFluc) {
		Global.pFluc = pFluc;
	}

	public static synchronized double getAOH() {
		return AOH;
	}

	public static synchronized void setAOH(double aOH) {
		AOH = aOH;
	}

	public static synchronized double getAOL() {
		return AOL;
	}

	public static synchronized void setAOL(double aOL) {
		AOL = aOL;
	}

	public static synchronized double getOpen() {
		return open;
	}

	public static synchronized void setOpen(double open) {
		Global.open = open;
		addLog("Global Set Open: " + open);
	}




	public static synchronized String getToday() {
		return Today;
	}

	public static synchronized void setToday(String today) {
		Today = today;
	}


	


	public static boolean isNoonOpened() {
		return isNoonOpened;
	}

	public static void setNoonOpened(boolean isNoonOpened) {
		Global.isNoonOpened = isNoonOpened;
	}





	public static synchronized double getNoonOpen() {
		return noonOpen;
	}

	public static synchronized void setNoonOpen(double noonOpen) {
		Global.noonOpen = noonOpen;
		addLog("Set Noon Open: " + noonOpen);
	}



	public static synchronized Chasing getChasing()
	{
		return chasing;
	}

	public static synchronized void setChasing(Chasing chasing)
	{
		Global.chasing = chasing;
	}

	static Chasing chasing;


	private static boolean backHandLong = false;
	private static boolean backHandShort = false;

	private static boolean isOrderTime = false;  
	private static boolean isTradeTime = false; 
	private static boolean isRuleOneTime = false; 
	private static boolean isForceSellTime = false;
	private static boolean isQuotePowerTime = false;

	private static boolean isSideWay;
	private static boolean isUpTrend;
	private static boolean isDownTrend;
	private static boolean isLowFluctuation;
	
	
	private static boolean hasLoggedIn;
	private static boolean isRunning;
	
	

	private static float currentBid;
	private static float currentAsk;
	private static float currentDeal;
	
	
	private static int noOfContracts = 0;
	private static float cutLost;
	private static float dayHigh;
	private static float dayLow;
	private static float tempHigh;
	private static float tempLow;
	private static float currentPoint; // �O�ӫY����
	
	private static float greatProfit;
	

	public static StringBuffer log = new StringBuffer("");

	public static float totalBalance;
	public static boolean analysingAll;
	
	public static boolean runRuleMA;
	public static boolean runRuleMA2;
	public static boolean runRSI;
	public static boolean runRuleWaveTheory;
	
	
	public static float balance = 0;
	public static boolean runRSI5;
	public static boolean ruleSync;
	public static boolean runRuleMACD;
	
	public static boolean shutDown;
	public static int maxContracts = 1;
	
	public static int noOfTrades = 0;

	public static double gap;

	public static boolean isNoonOpened;
	
	static double pOpen;
	static double pHigh;
	static double pLow;
	static double pClose;
	static double pFluc;
	static double AOH;
	static double AOL;
	static double open;
	static String Today;
	static double noonOpen;
	
	
}

