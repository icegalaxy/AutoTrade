package net.icegalaxy;



public class RuleSync extends Rules {

	public RuleSync(boolean globalRunRule) {
		super(globalRunRule);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openContract() {
		
		if (Global.getNoOfContracts() == 3 || Global.getNoOfContracts() == -3){  //防止其他rule買左三張後跌番落黎,ruleSync照買
			while(Global.getNoOfContracts() > 1 || Global.getNoOfContracts() < -1){
				if (!Global.isOrderTime())
					return;
				sleep(1000);
			}
		}
		

		if (Global.getNoOfContracts() == 2){
		
			System.out.println("Holding Balance: " + getBalance());
			
			sleep(1000);
			
			while (getBalance() <= 0){
				if(Global.getNoOfContracts() != 2)
					return;
				sleep(1000);
			}
			
			if (!Global.isOrderTime())
				return;
			longContract();
			System.out.println("Holding balance: " + getBalance());

		} if (Global.getNoOfContracts() == -2){
			
			System.out.println("Holding Balance: " + getBalance());
			
			sleep(1000);

			while (getBalance() <= 0){
				if(Global.getNoOfContracts() != 2)
					return;
				sleep(1000);
			}
			if (!Global.isOrderTime())
				return;
			shortContract();	
			System.out.println("Holding balance: " + getBalance());

		}

	}

	@Override
	public void closeContract() {
		
		while (Global.getNoOfContracts() < -1 || Global.getNoOfContracts() > 1){
			sleep(1000);
		}
		
		if (Global.getNoOfContracts() == 0){
			Global.addLog("RuleSync: Contract Closed by other Threads");
			hasContract = false;
			return;
		}
		
		closeContract("RuleSync: Close Contract");
	}
	


	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}


}