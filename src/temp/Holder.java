package temp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import node.Mod.NodeData;
import penalty.Report;
import transc.mod.Ctx;
import transc.mod.Ptx;

public class Holder {
		
		//public static LinkedBlockingQueue<Block_obj> newCoinBlock = new LinkedBlockingQueue<Block_obj>();
		////////////////////////////////////////////////////////////////
	
		public static List<weightResults> STAKE_RESULTS = new CopyOnWriteArrayList<>();
		////////////////////////////////////////////////////////////////

		public static List<weightResults> CURRENT_FINAL_CONFIRMED_PROCESSED_STAKE_RESULTS;
		////////////////////////////////////////////////////////////////
		
		public static HashMap<String,BigInteger> epochBlock = new HashMap<String,BigInteger>();
		////////////////////////////////////////////////////////////////
	
		public static List<Long> allTimestampOffset = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////
	
		public static List<NodeData> outboundPeers = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////
		
		public static List<NodeData> allPeers = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////
		
		public static List<String> allPeersIP = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////
	
		public static List<NodeData> inboundPeers = new ArrayList<>();
		//////////////////////////////////////////////////////////
		
	
		public static List<StakeObj> ObsoleteStakeAcc = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////

		public static List<String> gehennaList = new CopyOnWriteArrayList<>();
		//////////////////////////////////////////////////////////
		
		public static List<Report> receivedReports = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		
		public static List<Report> myReports = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		
		public static List<Ctx> epochStakeTxs = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		
		public static List<String> processedStakerAddr = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		
		public static List<String> packagedPtxAddr = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		
		public static List<Ctx> packagedStakeTx = new CopyOnWriteArrayList<>();
		///////////////////////////////////////////////////////////
		  
		public static List<Ctx> memTrancHolder =  new CopyOnWriteArrayList<>();
		 //////////////////////////////////////////////////////////
		 
		public static List<Ptx> memPackTrancHolder =  new CopyOnWriteArrayList<>();
		 //////////////////////////////////////////////////////////	
		 
		public static List<Ctx> memStakeTrancHolder =  new CopyOnWriteArrayList<>();
		 //////////////////////////////////////////////////////////	

		
		
	
		////Return coin txs from mem holder
		 public static List<Ctx> returnTxInMem() {
			
			 return memTrancHolder;
		 }
		 
		////Return stake txs from mem holder
		 public static List<Ctx> returnSTxInMem() {
			 return memStakeTrancHolder;
		 }
		 
		 ////Return pack from mem holder
		 public static List<Ptx> returnPackTxInMem() {
			 return memPackTrancHolder;
		 }
 
		 ////Add Pack txs to Memory Holder
         public static void addPackTranc2Holder(Ptx tranc) {
			 if(memPackTrancHolder != null && memPackTrancHolder.size() < Static.MAX_PTX_HOLD){
				 memPackTrancHolder.add(tranc);
			 }
		 }

		public static void addStakeTranc2Holder(Ctx ctx) {
			if(memStakeTrancHolder != null && memStakeTrancHolder.size() < Static.MAX_STX_HOLD){
				memStakeTrancHolder.add(ctx);
				
			}
			
		}

		public static void addCoinTranc2Holder(Ctx ctx) {
			if(memTrancHolder != null && memTrancHolder.size() < Static.MAX_CTX_HOLD ){
				memTrancHolder.add(ctx);
			}
			
		}
        
	
		  
}		    
		    
