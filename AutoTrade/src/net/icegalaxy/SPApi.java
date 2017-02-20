package net.icegalaxy;

import java.util.Arrays;
import java.util.List;

import javax.imageio.spi.RegisterableService;

import com.sun.jna.Callback;
import com.sun.jna.Library;

import com.sun.jna.Native;

import com.sun.jna.Structure;

import net.icegalaxy.SPApi.SPApiDll.SPApiOrder;
import net.icegalaxy.SPApi.SPApiDll.RegisterConn;
//import net.icegalaxy.SPApi.SPApiDll.RegisterError;
//import net.icegalaxy.SPApi.SPApiDll.RegisterLoginReply;
import net.icegalaxy.SPApi.SPApiDll.RegisterPriceUpdate;
import net.icegalaxy.SPApi.SPApiDll.SPApiPrice;




public class SPApi
{
	static int counter;
	static long status = 0;
	
	 private static final int port = 8080;
	 private static final String license = "76C2FB5B60006C7A";
	 private static final String app_id  = "BS";
	 private static final String userid = "T865829";
	 private static final String password = "ting1980";
	 private static final String server = "futures.bsgroup.com.hk";

	public static interface SPApiDll extends Library {
		public static SPApiDll INSTANCE = (SPApiDll) Native.loadLibrary("spapidllm64", SPApiDll.class);
		int SPAPI_Initialize();
		int SPAPI_Uninitialize();
		
		void SPAPI_SetLoginInfo(String server, int port, String license, String app_id, String userid, String password);
		int SPAPI_Login();
	
		int SPAPI_Logout(String user_id);
		
		int SPAPI_AddOrder(SPApiOrder order);
		
		int SPAPI_SubscribePrice(String user_id, String prod_code, int mode);
		
		void SPAPI_RegisterApiPriceUpdate(RegisterPriceUpdate priceUpdate);
		
		void SPAPI_RegisterConnectingReply(RegisterConn conn);
		
//		void SPAPI_RegisterTradeReport(RegisterTradeReport tradeReport);
	
		
		void SPAPI_RegisterLoginReply(RegisterLoginReply register);
		void SPAPI_RegisterLoginStatusUpdate(RegisterLoginStatusUpdate update);
		
		void SPAPI_RegisterConnectionErrorUpdate(RegisterError error);
		
		
		
		public interface RegisterPriceUpdate extends Callback
		{
			void invoke(SPApiPrice price);
		}
		
		
		public interface RegisterConn extends Callback
		{
			void invoke(long host_type, long con_status);
		}
		
//		public interface RegisterTradeReport extends Callback
//		{
//			void invoke(String acc_no);
//		}
		
		public interface RegisterError extends Callback
		{
			void invoke(short host_id, long link_err);
		}
		
		
	
		
		
		public interface RegisterLoginReply extends Callback{
			void printLoginStatus(long ret_code, String ret_msg);
		}
		
		public interface RegisterLoginStatusUpdate extends Callback{
			void printStatus(long login_status);
		}
		
		public class SPApiPrice extends Structure
		{
			
//			public static class ByReference extends SPApiPrice implements Structure.ByReference{}
			
			public double[] Bid = new double[20];
			public int[] BidQty = new int[20];
			public int[] BidTicket = new int [20];
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
			protected List getFieldOrder() {
				return Arrays.asList(new String[]{"Bid","BidQty","BidTicket","Ask","AskQty","AskTicket","Last","LastQty","LastTime","Equil","Open","High","Low","Close","CloseDate","TurnoverVol","TurnoverAmt","OpenInt","ProdCode","ProdName","DecInPrice","ExstateNo","TradeStateNo","Suspend","ExpiryYMD","ContractYMD","Timestamp"});
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
			public int ExtOrderNo;
			public int IntOrderNo;
			public int Qty;
			public int TradedQty;
			public int TotalQty;
			public int ValidTime;
			public int SchedTime;
			public int TimeStamp;
			public long OrderOptions;
			public char[] AccNo = new char[16];
			public char[] ProdCode = new char[16];
			public char[] Initiator = new char[16];
			public char[] Ref = new char[16];
			public char[] Ref2 = new char[16];
			public char[] GatewayCode = new char[16];
			public char[] ClOrderId = new char[40];
			public char BuySell;
			public char StopType;
			public char OpenClose;
			public int CondType;
			public int OrderType;
			public int ValidType;
			public int Status;
			public int DecInPrice;
			public int OrderAction;
			public int updateTime;
			public int updateSeqNo;
			
	
			@Override
			protected List getFieldOrder()
			{
				return Arrays.asList(new String[]{"Price",	"StopLevel",	"UpLevel",	"UpPrice",	"DownLevel",	"DownPrice",	"ExtOrderNo",	"IntOrderNo",	"Qty",	"TradedQty",	"TotalQty",	"ValidTime",	"SchedTime",	"TimeStamp",	"OrderOptions",	"AccNo",	"ProdCode",	"Initiator",	"Ref",	"Ref2",	"GatewayCode",	"ClOrderId",	"BuySell",	"StopType",	"OpenClose",	"CondType",	"OrderType",	"ValidType",	"Status",	"DecInPrice",	"OrderAction",	"updateTime",	"updateSeqNo"});
			}
			
			
			
		}
		
	}
	
	   public static int addOrder(char buy_sell)
       {
           int rc;
           SPApiOrder order = new SPApiOrder();
         

           order.AccNo = userid.toCharArray();
           order.Initiator = userid.toCharArray();
           order.BuySell = buy_sell;
           
           order.Qty = 2;
           
           order.ProdCode = "MHIG7".toCharArray();

           order.Ref = "@JAVA#TRADERAPI".toCharArray();      
           order.Ref2 = "0".toCharArray();
           order.GatewayCode = "".toCharArray();
          
           order.CondType = 0; //normal type
           order.ClOrderId = "0".toCharArray();
           order.ValidType = 0;
           order.DecInPrice = 0;

           
               order.OrderType = 6; //market order
               order.Price = 0; // market price
           

           rc = SPApiDll.INSTANCE.SPAPI_AddOrder(order); 

           return rc;
//           if (rc == 0) { if (DllShowTextData != null) DllShowTextData("Add Order Success!"); }
//           else { if (DllShowTextData != null) DllShowTextData("Add Order Failure! " + rc.ToString()); }

       }
	   
	   public static void registerPriceUpdate()
	   {
		   RegisterPriceUpdate priceUpdate = new RegisterPriceUpdate() {
				
				@Override
				public void invoke(SPApiPrice price) {
					Global.setCurrentPoint(new Float(price.Last[0]));
					Global.setCurrentAsk(new Float(price.Ask[0]));
					Global.setCurrentBid(new Float(price.Bid[0]));
					Global.setAskQty(price.AskQty[0]);
					Global.setBidQty(price.BidQty[0]);
					Global.setTurnOverVol(price.TurnoverVol);
					
				}

			};
			
			SPApiDll.INSTANCE.SPAPI_RegisterApiPriceUpdate(priceUpdate);
	   }
	   
	   public static int subScribePrice()
	   {
		   int status = 0;
		   status += SPApiDll.INSTANCE.SPAPI_SubscribePrice(userid, "HSIG7", 1);
		   
		   return status;
	   }
	   
	   public static void registerConnReply()
	   {
		   RegisterConn conn = new RegisterConn() {
				
				@Override
				public void invoke(long host_type, long con_status) {
					Global.addLog("connection reply - host type: " + host_type + ", con state: " + con_status); 
					if (host_type == 80 || host_type == 81)
					{
						if (con_status == 2)
							Global.setTradeLink(true);
						else
							Global.setTradeLink(false);					
					}else if (host_type == 83)
					{
						if (con_status == 2)
							Global.setPriceLink(true);
						else
							Global.setPriceLink(false);
					}else if (host_type == 88)
					{
						if (con_status == 2)
							Global.setGeneralLink(true);
						else
							Global.setGeneralLink(false);
					}
				}
			};
			
			SPApiDll.INSTANCE.SPAPI_RegisterConnectingReply(conn);
	   }
	   
	   public static int init()
	   {
		   int status = 0;
		   
		   status += SPApiDll.INSTANCE.SPAPI_Initialize();
		   			 SPApiDll.INSTANCE.SPAPI_SetLoginInfo(server, port, license, app_id, userid, password);
		   			 registerConnReply();
		   			 registerPriceUpdate();
		   status += SPApiDll.INSTANCE.SPAPI_Login();
		   
		   return status;
	   }
	   
	   public static int unInit()
	   {
		   int status = 0;
		   
		   status += SPApiDll.INSTANCE.SPAPI_SubscribePrice(userid, "HSIG7", 0);
		   status += SPApiDll.INSTANCE.SPAPI_Logout(userid);
		   status += SPApiDll.INSTANCE.SPAPI_Uninitialize();
		   
		   return status;
		   
	   }
}

