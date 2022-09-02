package connect.Inbound;

import java.io.IOException;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import connect.Network;
import node.db.IPs;

public class ReqAllPeers {
  Client client;
  List<String> IPlist;
	
	public ReqAllPeers (String host) {
		client = new Client();
		client.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(client);

		client.addListener(new Listener() {
			public void connected (Connection connection) {
				System.out.println("ReqAllPeers connection established....." + "\n");
				client.sendTCP("req_all_peers");
			}
			
			public void received (Connection connection, Object object) {
				
				if (object instanceof List) {
					if(IPlist instanceof List) {
						
						System.out.println("Potential peer ips received....." + "\n");
						IPs.storeIPAddressList((List<String>)IPlist);
						client.stop();
						
					}
					return;
				}
			}

			public void disconnected (Connection connection) {
				
			}
		});

		new Thread("getPeers") {
			public void run () {
				try {
					client.connect(20000, host, Network.port2);
					// Server communication after connection can go here, or in Listener#connected().
				} catch (IOException ex) {
					ex.printStackTrace();
					
				//	System.exit(1);
				}
			}
		}.start();
	}
	
}
