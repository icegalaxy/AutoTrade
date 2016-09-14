package net.icegalaxy;

public class LoginThread extends AutoTradeDB implements Runnable {

	@Override
	public void run() {
		boolean didLogin = false;		
		
		while (Global.isRunning()) {
			if (Global.isTradeTime()) {
				if (!didLogin) {
					//Sikuli.login();
					didLogin = true;
				}
				
			}	
			sleep(60000);
		}
		
	}

}
