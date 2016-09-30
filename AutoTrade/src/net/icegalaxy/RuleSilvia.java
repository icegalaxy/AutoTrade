package net.icegalaxy;

public class RuleSilvia extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;

	public RuleSilvia(boolean globalRunRule) {
		super(globalRunRule);
		setOrderTime(94500, 113000, 130500, 160000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

		// Reset the lossCount at afternoon because P.High P.Low is so important
		// if (isAfternoonTime() && !tradeTimesReseted) {
		// lossTimes = 0;
		// tradeTimesReseted = true;
		// }

		if (!isOrderTime() || lossTimes >= 2 || Global.getNoOfContracts() != 0)
			return;

		// used 1hr instead of 15min

		if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 2) {

			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {

				Global.addLog(className + ": waiting for a better position");

				while (Global.getCurrentPoint() > getTimeBase().getEMA(6)) {
					sleep(1000);

					if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {
						Global.addLog(className + ": wrong trend");
						return;
					}
				}
				longContract();

			} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {

				Global.addLog(className + ": waiting for a better position");

				while (Global.getCurrentPoint() < getTimeBase().getEMA(6)) {
					sleep(1000);

					if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
						Global.addLog(className + ": wrong trend");
						return;
					}
				}
				shortContract();

			}
		}

	}
	
	void stopEarn() {
		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss){
			
			closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			if (lossTimes > 0)
				lossTimes--;
				
		}
		else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint()> tempCutLoss){
			
			closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			if (lossTimes > 0)
				lossTimes--;
		}
	}

	void updateStopEarn() {

		if(getProfit() > getStopEarnPt())	{
			
			if(Global.getNoOfContracts() > 0)
				tempCutLoss = 99999;
			else if(Global.getNoOfContracts() < 0)
				tempCutLoss = 0;		
		}
		
			

	}

	double getCutLossPt() {
		return 15;
	}

	double getStopEarnPt() {
		return 30;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return GetData.getLongTB();
	}

}