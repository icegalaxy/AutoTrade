package net.icegalaxy;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.sikuli.script.*;

//Global.setHoldingStock(true)及Global.setHoldingStock(false)都係度，因為度度用到
//買之前setTrue，賣之後先至setFalse，以防買多左
public class Sikuli {

	static Region centre = new Region(640, 0, 640, 1080);
	static Region topLeft = new Region(0, 0, 960, 540);
	static Region topRight = new Region(960, 0, 960, 540);
	static Region bottomLeft = new Region(0, 540, 960, 540);
	static Region bottomRight = new Region(960, 540, 960, 540);

	static Location spTitle = new Location(471, 308); //驚spTrader D window pop up 會block左，所以點一下個title先買

	static Location buyOne = new Location(594, 480);
	static Location sellOne = new Location(732, 480);

	static Location buyTwo = new Location(594, 480);
	static Location sellTwo = new Location(732, 480);

	static Location liquidation = new Location(880, 330);
	
	static Location resetFutureOption = new Location(42, 77);
	static Location resetProfolio = new Location(42, 97);

	//static Location quotePower = new Location(975, 114); //chrome
	//static Location sleep = new Location(160, 585);

	static Screen sc = new Screen();
	static Robot robot;
	
	public static void makeRobot(){
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void capScreen(){
		robot.keyPress(KeyEvent.VK_PRINTSCREEN);
	}

	public static void login() {
		Global.addLog("Login");
		try {
			centre.wait("images\\login\\1.png", 20);
			centre.click("images\\login\\2.png", 0);
			centre.wait("images\\login\\1.png", 20);
			sc.type(null, "ting1980\n", 0);
			topLeft.wait("images\\login\\3.png", 20);

		} catch (FindFailed e) {
			e.printStackTrace();
			Sikuli.resetWindow();
			Sikuli.login();
		}
	}

	public static void resetWindow() {
		try {
			centre.click("images\\login\\cross.png", 0);
		} catch (FindFailed e1) {
			e1.printStackTrace();
		}
		closeWindow();
		try {
			bottomLeft.click("images\\login\\toolBarIcon.png", 0);
		} catch (FindFailed e1) {
			Global.addLog("can't Reset Window, Program Ended!!");
			Global.setRunning(false);
			Global.setTradeTime(false);
			Global.setOrderTime(false);
		}
	}
	
	public static void resetQuotePower(){
		Global.addLog("Resetting QuotePower... " );
		try {
			sc.click(resetFutureOption,0);
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(5000);
		
		try {
			sc.click(resetProfolio, 0);
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(5000);
	}

	public static synchronized boolean longContract() {

		if (Global.getNoOfContracts() >= Global.maxContracts) {
			return false;
		}

//		Global.addLog("Long at " + Global.getCurrentAsk());
		Global.balance -= Global.getCurrentAsk();
		Global.noOfTrades += 1;
		Global.setNoOfContracts(Global.getNoOfContracts() + 1);

		try {
			sc.click(spTitle, 0);
			sleep(300);
			sc.click(buyOne, 0);
		} catch (Exception e) {
			e.printStackTrace();
			Global.addLog("Fail to Long, liquidate");
			liquidate();
		}

		if (Global.getNoOfContracts() == 0) {
//			Global.addLog("Current Balance: " + Global.balance + " points, No of Trades: " + Global.noOfTrades);
			Rules.setBalance(0);
		}
		sleep(2000);

		robot.keyPress(KeyEvent.VK_PRINTSCREEN);
		return true;
	}

	public static synchronized boolean shortContract() {

		if (Global.getNoOfContracts() <= Global.maxContracts * -1) {
			return false;
		}

//		Global.addLog("Short at " + Global.getCurrentBid());
		Global.balance += Global.getCurrentBid();
		Global.noOfTrades += 1;
		Global.setNoOfContracts(Global.getNoOfContracts() - 1);

		try {
			sc.click(spTitle, 0);
			sleep(300);
			sc.click(sellOne, 0);
		} catch (Exception e) {
			e.printStackTrace();
			Global.addLog("Fail to Long, liquidate");
			liquidate();
		}

		if (Global.getNoOfContracts() == 0) {
//			Global.addLog("Current Balance: " + Global.balance +  " points, No of Trades: " + Global.noOfTrades);
			Rules.setBalance(0);
		}

		sleep(2000);
		robot.keyPress(KeyEvent.VK_PRINTSCREEN);
		return true;
	}

	public static synchronized void liquidate() {
		Global.addLog("Force liquidate");
		Global.setRunning(false);
		Global.setNoOfContracts(0);

		try {
			sc.click(spTitle, 0);
			sleep(300);
			sc.click(liquidation, 0);
			sleep(100);
			sc.type(null, "\n", 0);
			
			sleep(2000);
			robot.keyPress(KeyEvent.VK_PRINTSCREEN);
		} catch (Exception e) {
			Global
					.addLog("Liquidation Fail, Program Stopped, Please liquidate manually (TeamViewer)");
			e.printStackTrace();
		}
	}
	
	public static synchronized void liquidateOnly() {
		Global.addLog("Liquidate: Precaution");
		
		try {
			sc.click(spTitle, 0);
			sleep(300);
			sc.click(liquidation, 0);
			sleep(100);
			sc.type(null, "\n", 0);
		} catch (Exception e) {
			Global
					.addLog("Liquidation Fail, Program Stopped, Please liquidate manually (TeamViewer)");
			e.printStackTrace();
		}
	}

	public static synchronized boolean closeContract() {

		if (Global.getNoOfContracts() > 0) {

			boolean b = Sikuli.shortContract();
			if (!b) {
				Global.addLog("Fail to close, reset Window");
				return false;
			}

		} else if (Global.getNoOfContracts() < 0) {

			boolean b = Sikuli.longContract();
			if (!b) {
				Global.addLog("Fail to close, reset Window");
				return false;
			}
		} else {
			Global.addLog("Error: No Contract to close");
			return false;
		}
		return true;
	}

	public static void closeWindow() {
		Global.addLog("Close SP Trader Window");
		try {
			topRight.click("images\\login\\cross.png", 0);
			centre.wait("images\\login\\sure.png", 20);
			centre.type(null, "\n", 0);
		} catch (FindFailed e) {
			Global.addLog("Can't close SP Trader Window");
			e.printStackTrace();
		}
	}

//	public static void closeEclipse() {
//		Global.addLog("Close Eclipse");
//		try {
//			topRight.click(cross, 0);
//			// centre.wait("images\\login\\sure.png", 20);
//			// centre.type(null, "\n", 0);
//		} catch (FindFailed e) {
//			Global.addLog("Can't close Eclipse");
//			e.printStackTrace();
//		}
//	}

	private static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void quotePower() { // 要synchronized因為驚佢同long//用番robot plz
													// short一齊按
		try {
//			robot.mouseMove(975, 114); // for chrome (因為chrome個新板唔support quotepower (JAVA)，所要用番IE)
//			robot.mouseMove(987, 102); // for IE or firefox
			robot.mouseMove(988, 77); // for Java web app
			robot.mousePress(InputEvent.BUTTON1_MASK );
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static synchronized void getSleep() {
//		try {
//			sc.click(sleep, 0);
//		} catch (FindFailed e) {
//			e.printStackTrace();
//		}
//	}

}
