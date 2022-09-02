package node.Mod;


import java.io.Serializable;

import temp.Static;

public class NodeData implements Serializable{

	 String nodeType;
	 String  version;
	 String  ip_addr;
	 long  numOfCtxBlocks;
	 long  numEpochWon;
	 long  epochHeight;
	 String  replyTimestamp;
	 String receivedTimestamp;
		// Constructor: 
		public NodeData(String Version, String nodeType, String IP_Addr, long numOfCtxBlocks, long epochHeight, long numEpochWon, String replyTimestamp,String receivedTimestamp) {
			
			this.version = Version;
			this.nodeType = nodeType;
			this.ip_addr = IP_Addr;
			this.numOfCtxBlocks = numOfCtxBlocks;
			this.epochHeight = epochHeight;
			this.numEpochWon = numEpochWon;
			this.replyTimestamp = replyTimestamp;
			this.receivedTimestamp = receivedTimestamp;
		}
	
		public String getVersion() {
		    return version;
		}
		
		public String getNodeType() {
		    return nodeType;
		}
		
		public String getIP_Addr() {
		    return ip_addr;
		}
		public long getNumOfCtxBlocks() {
		    return numOfCtxBlocks;
		}
		
		public long getEpochHeight() {
			return epochHeight;
		}
		
		public long getNumEpochWon() {
			return numEpochWon;
		}

		public String getReplyTimestamp() {
			return replyTimestamp;
		}
		public String getReceivedTimestamp() {
			return receivedTimestamp;
		}
		
		public static String checkNumCtxBlocks() {
			 String numCtxBlocks = String.valueOf(Static.NUM_CTX_BLOCKS);
			return numCtxBlocks;
		} 
	
			
}
