package node.db;

import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class Node_db {

	public static void storeNodeData(String Version, String nodeType, String IP_Addr, String numOfCtxBlocks, String epochHeight, String numEpochWon, String timestamp) {
		
		try {
			 String [] nodeDataArray = {Version, nodeType, IP_Addr, numOfCtxBlocks, epochHeight, numEpochWon, timestamp}; 
			 String nodeData = String.join(" ", nodeDataArray);	   
			 EasyRam accDB = new EasyRam(db.NODE_DATA);
			 accDB.createStore("NodeData", DataStore.Storage.PERSISTED,1);
			 accDB.putString("NodeData","node info", nodeData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }

	public static String[] getNodeData() {
		
     	String[] nodeData = null;
   		EasyRam accDB = new EasyRam(db.NODE_DATA);
		try {
   			accDB.createStore("NodeData", DataStore.Storage.PERSISTED,1);
   			String nodeDataStr = (String)accDB.getString("NodeData", "node info");
   			nodeData = nodeDataStr.split(" ");
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
	     	
	    return nodeData;
	    
	}
	

	

}
