package net.icegalaxy;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Setting extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static Integer password;
	private JTextField passwordField;
	private JTextField maxContracts;
	private JButton startBtn;
	private JButton stopBtn;
	private JTextArea result;
	private JScrollPane resultPane;
	private JCheckBox ruleMAcheckBox;
	private JCheckBox ruleMA2checkBox;
	private JCheckBox ruleMACDcheckBox;
	private JCheckBox ruleRSIcheckBox;
	private JCheckBox ruleSynccheckBox;

	private static int shortTB;
	private static int mediumTB;
	private static int longTB;

	public Setting() {
		super("Auto Trade System");

		setLayout(new FlowLayout());

		result = new JTextArea();
		passwordField = new JTextField("0", 40);
		maxContracts = new JTextField("3", 40);
		startBtn = new JButton("Start");
		stopBtn = new JButton("Stop");

		ruleMAcheckBox = new JCheckBox("Rule MA", false);
		ruleMA2checkBox = new JCheckBox("Rule MA2", false);
		ruleMACDcheckBox = new JCheckBox("Rule MACD", false);
		ruleRSIcheckBox = new JCheckBox("Rule RSI", true);
		ruleSynccheckBox = new JCheckBox("Rule Sync", false);


		add(new JLabel("Great Profit Point"));
		add(passwordField);

		add(new JLabel("Max Contracts"));
		add(maxContracts);

		add(ruleMAcheckBox);
		add(ruleMA2checkBox);
		add(ruleMACDcheckBox);
		add(ruleRSIcheckBox);
		add(ruleSynccheckBox);

		add(startBtn);
		add(stopBtn);

		result = new JTextArea();
		result.setRows(16);
		result.setColumns(50);
		resultPane = new JScrollPane(result);
		add(resultPane);

		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Global.setCutLost(20);
				
				password = new Integer(passwordField.getText());
				
//				Global.setGreatProfit(new Float(passwordField.getText()));
				Global.maxContracts = new Integer(maxContracts.getText());
				shortTB = new Integer(1);
				mediumTB = new Integer(3);
				longTB = new Integer(5);
				
					

				Global.runRuleMA = ruleMAcheckBox.isSelected();
				Global.runRuleMA2 = ruleMA2checkBox.isSelected();
				Global.runRuleMACD = ruleMACDcheckBox.isSelected();
				Global.runRSI = ruleRSIcheckBox.isSelected();
				Global.ruleSync = ruleSynccheckBox.isSelected();
				
				
				String myLibraryPath = System.getProperty("user.dir");//or another absolute or relative path
				System.setProperty("java.library.path", myLibraryPath);
				
				
				XMLWatcher.csvLog = new MyFile("C:\\Users\\joecheung0\\Dropbox\\TradeData\\csvLog " + getToday() + ".csv");
				XMLWatcher.csvLog.fileString.append("Time,Caller,Message,Remark\r\n");
				XMLWatcher.files.add(XMLWatcher.csvLog);
				
				XMLWatcher.csvLog.writeToFile();
				
				SPApi.init();
				
				
				
				
				while (getDayOfWeek() == 1 || getDayOfWeek() == 7){
					System.out.println("Sunday or Saturday " + GetData.getTimeInt() + " Sleep for 1 hr");
					try {
						Thread.sleep(3600000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					
				}

				while (getTimeInt() > 1010000 || getTimeInt() < 90001){
					System.out.println(getTimeInt() + ": Sleep for 5 min");
					try {
						Thread.sleep(300000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					
				}
			
				
				Global.setCurrentPoint(SPApi.getAPIPrice().Last[0]); //don't know if this is necessary, try to test if the API is functioning
				
				runThreads();
			}
		});

		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.setRunning(false);
				System.out.println("Program is stopping in 1 minutes.");
			}
		});

	}

	private synchronized void runThreads() {

		Global.setRunning(true);
		Global.setDayHigh(0);
		Global.setDayLow(99999);
		Global.setToday(getToday());
		Global.setChasing(new Chasing());

		
//		ruleMA_0.setBufferPt(0);
//		RuleOpen ruleMA_5 = new RuleOpen(Global.runRuleMA);
//		ruleMA_5.setBufferPt(5);
//		RuleOpen ruleMA_10 = new RuleOpen(Global.runRuleMA);
//		ruleMA_10.setBufferPt(10);
//		RuleMA2 ruleMA2_0 = new RuleMA2(Global.runRuleMA2);
//		ruleMA2_0.setBufferPt(0);
//		RuleMA2 ruleMA2_5 = new RuleMA2(Global.runRuleMA2);
//		ruleMA2_5.setBufferPt(5);
//		RuleMA2 ruleMA2_10 = new RuleMA2(Global.runRuleMA2);
//		ruleMA2_10.setBufferPt(10);
		
//		RuleSuddenBreakThrough sudden = new RuleSuddenBreakThrough(false);
//		RuleSuddenBreakThrough2 sudden2 = new RuleSuddenBreakThrough2(false);
//		RuleRebound rebound = new RuleRebound(false);
//		RulePLow pLow = new RulePLow(false);
//		RuleRSI2 rsi2 = new RuleRSI2(false);
		
//		RuleMABackup backup	= new RuleMABackup(false);
		
//		ruleMACD ruleMACD = new ruleMACD(Global.runRuleMACD);
//		RuleRSI rsi = new RuleRSI(false);
//		LoginThread login = new LoginThread();
		TimePeriodDecider tpd = new TimePeriodDecider();
		GetData gd = new GetData();
//		RuleAOH aoh = new RuleAOH(true);
//		RuleAOL aol = new RuleAOL(true);
//		RulePClose pClose = new RulePClose(false);
//		RuleDanny250Pena danny250 = new RuleDanny250Pena(false);
//		RuleDanny250Pena2 danny2502 = new RuleDanny250Pena2(false);
//		RuleDanny250Pena3 danny2503 = new RuleDanny250Pena3(false);
//		RuleRange range = new RuleRange(true);
//		RuleBreakThrough breakThrough = new RuleBreakThrough(false);
		
//		RuleDanny2 danny2 = new RuleDanny2(false);
//		RuleDanny240 danny240 = new RuleDanny240(true);
//		RuleDanny50 danny50 = new RuleDanny50(true);
//		RuleNoonOpen noonOpen = new RuleNoonOpen(true);
//		RuleSilvia silvia = new RuleSilvia(true);
//		RuleEMA56 ema56 = new RuleEMA56(false);
		
//		RuleChasing chasing = new RuleChasing(true);
		
		Runnable[] r = { gd, tpd};

		Thread[] t = new Thread[r.length];
		for (int i = 0; i < r.length; i++) {
			t[i] = new Thread(r[i]);
			t[i].start();
		}

	}

	public static int getShortTB() {
		return shortTB;
	}

	public static int getMediumTB() {
		return mediumTB;
	}

	public static int getLongTB() {
		return longTB;
	}
	
	public Integer getTimeInt() { 
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String time = new String(formatter.format(now.getTime()));
		return new Integer(time.replaceAll(":", ""));
	}
	
	public int getDayOfWeek() { 
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK); 
		return day;
	}
	
	public String getToday() {

		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		String s = formatter.format(now.getTime());

		return s;
	}

}
