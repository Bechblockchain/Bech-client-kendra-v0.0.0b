package connect.Outbound;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import connect.Network;
import node.db.IPs;
import temp.Static;


public class sendAllPeers {
	int connectionsCounter = 0;
	Server server;

	public sendAllPeers () throws IOException {
		 int writebuffer = 50000000;
	       int objectbuffer = 50000000;
	 	   server = new Server(writebuffer,objectbuffer); 

		Network.register(server);

		server.addListener(new Listener() {
			
			 public void connected (Connection c) {
					
					if(connectionsCounter  < Static.MAX_IP_SERVER_OUTBOUND) {
						connectionsCounter++;
					}else {
						c.close();
					}
						
			    }
			
			
			public void received (Connection c, Object object) {
				
				if (object instanceof String) {
					String command = (String)object;
					if(command.equals("req_all_peers")) {
						
						c.sendTCP(IPs.getIPAddressList());
						
					}
					return;
				}

				
			}

			public void disconnected (Connection c) {
				Thread.currentThread().interrupt();
			}
		});
		server.bind(Network.port2);
		server.start();
	}

	
}
