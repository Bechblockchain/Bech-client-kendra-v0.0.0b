package node;

import java.util.List;
import java.util.Set;


import connect.Network;
import connect.Inbound.KryonetClient;
import node.db.IPs;
import temp.Holder;

public class DisconectAllOutBoundPeers {

	public static void clearAllOutboundPeers() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.disconnect();
				        Holder.allPeers.remove(i);
				        Holder.allPeersIP.remove(i);
				       
				}
			}
        }
			List<String> ips = IPs.getIPAddressList();
			
			 for(int j = 0; j < outboundIPs.size(); j++) {
				 
				 for(int i = 0; i < ips.size(); i++) {
					 
		    		 if(ips.get(i).equals(outboundIPs.get(j))) {
		    			 ips.remove(i);
		    		 }
				 }
			 }
			 IPs.storeIPAddressList(ips);
		
	    	 
			
		 Holder.outboundPeers.clear();
	}
	
}
