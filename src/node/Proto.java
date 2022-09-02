package node;

import node.Mod.NodeData;
import node.db.Node_db;

public class Proto {

	public static NodeData returnNodeInfo(String  recievedTimestamp) {
		String [] dataArray = Node_db.getNodeData();
		 String  Version = dataArray[0];
		 String  nodeType = dataArray[1];
		 String  IP_Addr = dataArray[2];
		 String numOfCtxBlocks = dataArray[3];
		 String  epochHeight = dataArray[4];
		 String  numEpochWon = dataArray[5];
		 String  replyTimestamp = String.valueOf(System.currentTimeMillis());
				
		 NodeData nodeObject = new NodeData(Version, nodeType, IP_Addr, Long.valueOf(numOfCtxBlocks), Long.valueOf(epochHeight), Long.valueOf(numEpochWon), replyTimestamp,recievedTimestamp);
		return nodeObject;
		
	}
	
	
	public static NodeData returnClientNodeInfo() {
		String [] dataArray = Node_db.getNodeData();
		 String  Version = dataArray[0];
		 String nodeType = dataArray[1];
		 String  IP_Addr = dataArray[2];
		 String numOfCtxBlocks = dataArray[3];
		 String  epochHeight = dataArray[4];
		 String  numEpochWon = dataArray[5];
		 String  sentTimestamp = String.valueOf(System.currentTimeMillis());
		 String recievedTimestamp = dataArray[6];
		
				
		 NodeData nodeObject = new NodeData(Version, nodeType, IP_Addr, Long.valueOf(numOfCtxBlocks), Long.valueOf(epochHeight), Long.valueOf(numEpochWon), sentTimestamp,recievedTimestamp);
		return nodeObject;
		
	}
	
	
}
