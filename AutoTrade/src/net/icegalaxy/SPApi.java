package net.icegalaxy;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.sun.jna.Callback;
import com.sun.jna.Library;

import com.sun.jna.Native;

import com.sun.jna.Structure;

import net.icegalaxy.SPApi.SPApiDll.SPApiOrder;
import net.icegalaxy.SPApi.SPApiDll.RegisterConn;

import net.icegalaxy.SPApi.SPApiDll.RegisterPriceUpdate;
import net.icegalaxy.SPApi.SPApiDll.RegisterTradeReport;
import net.icegalaxy.SPApi.SPApiDll.SPApiPrice;
import net.icegalaxy.SPApi.SPApiDll.SPApiTrade;
import net.icegalaxy.SPApi.SPApiDll.AccLoginReply;

public class SPApi
{
	static int counter;
	static long status = 0;
	static String product = "HSIH7";
	
	static ArrayList<SPApiOrder> orders = new ArrayList<SPApiOrder>();

	/*
	static final int port = 8080;
	static final String license = "76C2FB5B60006C7A";
	static final String app_id = "BS";
	static final String userid = "T865829";
	static final String password = "ting1980";
	static final String server = "futures.bsgroup.com.hk";
*/
	
	 static int port = 8080;
	 static String license = "58A665DE84D02";
	 static String app_id = "SPDEMO";
	 static String userid = "DEMO201702141";
	 static String password = "00000000";
	 static String server = "demo.spsystem.info";

	public static interface SPApiDll extends Library
	{
		public static SPApiDll INSTANCE = (SPApiDll) Native.loadLibrary("spapidllm64", SPApiDll.class);

		int SPAPI_Initialize();

		int SPAPI_Uninitialize();

		void SPAPI_SetLoginInfo(String server, int port, String license, String app_id, String userid, String password);

		int SPAPI_Login();

		int SPAPI_Logout(String user_id);

		int SPAPI_AddOrder(SPApiOrder order);

		int SPAPI_SubscribePrice(String user_id, String prod_code, int mode);

		void SPAPI_RegisterApiPriceUpdate(RegisterPriceUpdate priceUpdate);
		
		void SPAPI_RegisterOrderBeforeSendReport(RegisterOrderB4 orderB4);
		
		void SPAPI_RegisterTradeReport(RegisterTradeReport tradeReport);

		void SPAPI_RegisterConnectingReply(RegisterConn conn);

		// void SPAPI_RegisterTradeReport(RegisterTradeReport tradeReport);
		void SPAPI_RegisterAccountLoginReply(AccLoginReply loginReply);

		void SPAPI_RegisterLoginReply(RegisterLoginReply register);

		void SPAPI_RegisterLoginStatusUpdate(RegisterLoginStatusUpdate update);

		void SPAPI_RegisterConnectionErrorUpdate(RegisterError error);

		int SPAPI_GetPriceByCode(String user_id, String prod_code, SPApiPrice price);
		
		public interface RegisterOrderB4 extends Callback
		{
			void invoke(SPApiOrder order);
		}

		public interface RegisterPriceUpdate extends Callback
		{
			void invoke(SPApiPrice price);
		}

		public interface AccLoginReply extends Callback
		{
			void invoke(String accNo, long ret_code, String ret_msg);
		}

		public interface RegisterConn extends Callback
		{
			void invoke(long host_type, long con_status);
		}

		// public interface RegisterTradeReport extends Callback
		// {
		// void invoke(String acc_no);
		// }

		public interface RegisterError extends Callback
		{
			void invoke(short host_id, long link_err);
		}

		public interface RegisterLoginReply extends Callback
		{
			void printLoginStatus(long ret_code, String ret_msg);
		}

		public interface RegisterLoginStatusUpdate extends Callback
		{
			void printStatus(long login_status);
		}
		
		public interface RegisterTradeReport extends Callback
		{
			void invoke(long rec_no, SPApiTrade trade);
		}

		public class SPApiTrade extends Structure
		{
			public double RecNo;
			public double Price;
			public double AvgPrice;
			public int TradeNo;
			public int ExtOrderNo;
			public int IntOrderNo;
			public int Qty;
			public int TradeDate;
			public int TradeTime;
			public char[] AccNo = new char[16];
			public char[] ProdCode = new char[16];
			public char[] Initiator = new char[16];
			public char[] Ref = new char[16];
			public char[] Ref2 = new char[16];
			public char[] GatewayCode = new char[16];
			public char[] ClOrderId = new char[40];
			public char BuySell;
			public char OpenClose;
			public int Status;
			public int DecInPrice;
			public double OrderPrice;
			public char[] TradeRef = new char[40];
			public int TotalQty;
			public int RemainingQty;
			public int TradedQty;
			public double AvgTradedPrice;

			@Override
			protected List getFieldOrder()
			{
				
				return Arrays.asList(new String[]
				{ "RecNo", "Price", "AvgPrice", "TradeNo", "ExtOrderNo", "IntOrderNo", "Qty", "TradeDate", "TradeTime",
						"AccNo", "", "ProdCode", "Initiator", "Ref", "Ref2", "GatewayCode", "ClOrderId", "BuySell",
						"OpenClose", "Status", "DecInPrice", "OrderPrice", "TradeRef", "TotalQty", "RemainingQty",
						"TradedQty", "AvgTradedPrice" });
			}
		}
		
		public class SPApiPrice extends Structure
		{

			// public static class ByReference extends SPApiPrice implements
			// Structure.ByReference{}

			public double[] Bid = new double[20];
			public int[] BidQty = new int[20];
			public int[] BidTicket = new int[20];
			public double[] Ask = new double[20];
			public int[] AskQty = new int[20];
			public int[] AskTicket = new int[20];
			public double[] Last = new double[20];
			public int[] LastQty = new int[20];
			public int[] LastTime = new int[20];
			public double Equil;
			public double Open;
			public double High;
			public double Low;
			public double Close;
			public int CloseDate;
			public double TurnoverVol;
			public double TurnoverAmt;
			public int OpenInt;
			public char[] ProdCode = new char[16];
			public char[] ProdName = new char[40];
			public String DecInPrice;
			public int ExstateNo;
			public int TradeStateNo;
			public boolean Suspend;
			public int ExpiryYMD;
			public int ContractYMD;
			public int Timestamp;

			@Override
			protected List getFieldOrder()
			{
				return Arrays.asList(new String[]
				{ "Bid", "BidQty", "BidTicket", "Ask", "AskQty", "AskTicket", "Last", "LastQty", "LastTime", "Equil",
						"Open", "High", "Low", "Close", "CloseDate", "TurnoverVol", "TurnoverAmt", "OpenInt",
						"ProdCode", "ProdName", "DecInPrice", "ExstateNo", "TradeStateNo", "Suspend", "ExpiryYMD",
						"ContractYMD", "Timestamp" });
			}

		}

		public class SPApiOrder extends Structure
		{

			public double Price;
			public double StopLevel;
			public double UpLevel;
			public double UpPrice;
			public double DownLevel;
			public double DownPrice;
			public long ExtOrderNo;
			public int IntOrderNo;
			public int Qty;
			public int TradedQty;
			public int TotalQty;
			public int ValidTime;
			public int SchedTime;
			public int TimeStamp;
			public int OrderOptions;
			public byte[] AccNo = new byte[16];
			public byte[] ProdCode = new byte[16];
			public byte[] Initiator = new byte[16];
			public byte[] Ref = new byte[16];
			public byte[] Ref2 = new byte[16];
			public byte[] GatewayCode = new byte[16];
			public byte[] ClOrderId = new byte[40];
			public byte BuySell;
			public byte StopType;
			public byte OpenClose;
			public byte CondType;
			public byte OrderType;
			public byte ValidType;
			public byte Status;
			public byte DecInPrice;
			public int OrderAction;
			public int updateTime;
			public int updateSeqNo;

			@Override
			protected List getFieldOrder()
			{
				return Arrays.asList(new String[]
				{ "Price", "StopLevel", "UpLevel", "UpPrice", "DownLevel", "DownPrice", "ExtOrderNo", "IntOrderNo",
						"Qty", "TradedQty", "TotalQty", "ValidTime", "SchedTime", "TimeStamp", "OrderOptions", "AccNo",
						"ProdCode", "Initiator", "Ref", "Ref2", "GatewayCode", "ClOrderId", "BuySell", "StopType",
						"OpenClose", "CondType", "OrderType", "ValidType", "Status", "DecInPrice", "OrderAction",
						"updateTime", "updateSeqNo" });
			}

		}

	}

	public static int addOrder(byte buy_sell)
	{
		int rc;
		
		SPApiOrder order = new SPApiOrder();
		orders.add(order);

		setBytes(order.AccNo, userid);
		setBytes(order.Initiator, userid);
		order.BuySell = buy_sell;

		order.Qty = 2;

		setBytes(order.ProdCode, "MHIH7");

		setBytes(order.Ref, "@JAVA#TRADERAPI");
		setBytes(order.Ref2, "0");
		setBytes(order.GatewayCode, "");

		order.CondType = 0; // normal type
		setBytes(order.ClOrderId, "0");
		order.ValidType = 0;
		order.DecInPrice = 0;

		order.OrderType = 6; // market order
		order.Price = 0; // market price

		rc = SPApiDll.INSTANCE.SPAPI_AddOrder(order);

		return rc;
		// if (rc == 0) { if (DllShowTextData != null) DllShowTextData("Add
		// Order Success!"); }
		// else { if (DllShowTextData != null) DllShowTextData("Add Order
		// Failure! " + rc.ToString()); }

	}

	public static void accLoginReply()
	{
		AccLoginReply accReply = new AccLoginReply()
		{

			@Override
			public void invoke(String accNo, long ret_code, String ret_msg)
			{
				System.out.println("AccNo xxxxxx: " + accNo);
			}
		};

		SPApiDll.INSTANCE.SPAPI_RegisterAccountLoginReply(accReply);
		
	}

	public static double setGlobalPrice()
	{
		SPApiPrice price = new SPApiPrice();

		int i = SPApiDll.INSTANCE.SPAPI_GetPriceByCode(userid, product, price);

		if (i == 0)
		{
			Global.setCurrentPoint(price.Last[0]);
			System.out.println("Added Global price: " + price.Last[0]);

		} else
		{
			System.out.println("Failed to getPriceByCode!");
		}
		return i;
	}

	public static void registerPriceUpdate()
	{
		RegisterPriceUpdate priceUpdate = new RegisterPriceUpdate()
		{

			@Override
			public void invoke(SPApiPrice price)
			{
				Global.setCurrentPoint(price.Last[0]);
				Global.setCurrentAsk(price.Ask[0]);
				Global.setCurrentBid(price.Bid[0]);
				Global.setAskQty(price.AskQty[0]);
				Global.setBidQty(price.BidQty[0]);
				Global.setTurnOverVol(price.TurnoverVol);
				Global.setDayHigh(price.High);
				Global.setDayLow(price.Low);

			}

		};

		SPApiDll.INSTANCE.SPAPI_RegisterApiPriceUpdate(priceUpdate);

		System.out.println("Registered price update CALLBACK");
	}

	public static int subscribePrice()
	{

		int status = SPApiDll.INSTANCE.SPAPI_SubscribePrice(userid, product, 1);

		if (status == 0)
			System.out.println("Subscribed price: " + product + ", Succeed[" + status + "]");
		else
			System.out.println("Subscribed price: " + product + ", Failed[" + status + "]");

		return status;
	}
	
	public static void registerTradeReport(){
		RegisterTradeReport tradeReport = (rec_no, trade) -> Global.addLog("Rec_no: " + rec_no + ", Price: " + trade.Price);
		
	}

	public static void registerConnReply()
	{
		RegisterConn conn = new RegisterConn()
		{

			@Override
			public void invoke(long host_type, long con_status)
			{
				Global.addLog("connection reply - host type: " + host_type + ", con state: " + con_status);
				if (host_type == 80 || host_type == 81)
				{
					if (con_status == 2)
						Global.setTradeLink(true);
					else
						Global.setTradeLink(false);
				} else if (host_type == 83)
				{
					if (con_status == 2)
						Global.setPriceLink(true);
					else
						Global.setPriceLink(false);
				} else if (host_type == 88)
				{
					if (con_status == 2)
						Global.setGeneralLink(true);
					else
						Global.setGeneralLink(false);
				}
			}
		};

		SPApiDll.INSTANCE.SPAPI_RegisterConnectingReply(conn);

		System.out.println("Resistered connection reply CALLBACK");
	}

	public static int init()
	{
		int status = 0;

		status += SPApiDll.INSTANCE.SPAPI_Initialize();
		SPApiDll.INSTANCE.SPAPI_SetLoginInfo(server, port, license, app_id, userid, password);
		registerConnReply();
		registerPriceUpdate();
		registerTradeReport();
		status += SPApiDll.INSTANCE.SPAPI_Login();

		return status;
	}

	public static int unInit()
	{
		int status = 0;

		status += SPApiDll.INSTANCE.SPAPI_SubscribePrice(userid, "HSIH7", 0);
		status += SPApiDll.INSTANCE.SPAPI_Logout(userid);
		status += SPApiDll.INSTANCE.SPAPI_Uninitialize();

		return status;

	}
	
	public static void setBytes(byte[] bytes, String s)
	{
		 for (int i=0; i<s.length(); i++)
		 {
		        bytes[i] = (byte) s.charAt(i);
		   }

		
	}
	
	
}
