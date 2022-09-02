package node.db;

import java.util.List;

import KryoMod.StringListReg;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class IPs {

	public static void storeIPAddressList(List<String> IPaddressList) {
		 int counter = 0;
	      	List<String> IPlist = getIPAddressList();
	       	if(IPlist != null) {
	       		for(String ip : IPaddressList) {
	       			IPlist.add(ip);
		      		counter++;
		      		 System.out.println("Potential peer ip " + counter + " stored....." + "\n");
		      	}
	       	}
		 try {
			 EasyRam accDB = new EasyRam(db.PEER_IPs);
			 accDB.createStore("PeerIps", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("PeerIps","ip_address", new StringListReg(IPlist));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
		 
	}
	
	public static void storeIPAddress(String IPaddress)  {
	 
      	List<String> IPlist = getIPAddressList();
      	IPlist.add(IPaddress);

        try {
			 EasyRam accDB = new EasyRam(db.PEER_IPs);
			 accDB.createStore("PeerIps", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("PeerIps","ip_address", new StringListReg(IPlist));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	        
	}
	
	public static List<String> getIPAddressList() {
		 
      	List<String> IPlist = null;
      	
      	EasyRam accDB = new EasyRam(db.PEER_IPs);
		try {
   			accDB.createStore("PeerIps", DataStore.Storage.PERSISTED,1);
   			IPlist = ((StringListReg) accDB.getObject("PeerIps", "ip_address")).getStringList();
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
	      	
	      	
		 return IPlist;
	}
	
}
