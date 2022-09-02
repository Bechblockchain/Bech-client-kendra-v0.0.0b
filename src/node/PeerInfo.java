package node;

import java.io.IOException;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import connect.Util.NetworkTime;
import node.Mod.NodeData;
import node.db.IPs;
import temp.Holder;

public class PeerInfo {

	public static void forEachOutBoundPeerInfo(Client client,NodeData peerNode,long clientSentTimestamp,long clientReceivedTimestamp) {
		// TODO Auto-generated method stub
		long peerReceivedTimestamp = Long.valueOf(peerNode.getReceivedTimestamp());
		long peerReplyTimestamp = Long.valueOf(peerNode.getReplyTimestamp());
		long offset = ((peerReceivedTimestamp - clientSentTimestamp)+(peerReplyTimestamp - clientReceivedTimestamp))/2;
		if(NetworkTime.checkTimeOffset(offset)) {
			Holder.allTimestampOffset.add(offset);
			Holder.outboundPeers.add(peerNode);
			Holder.allPeers.add(peerNode);
			Holder.allPeersIP.add(peerNode.getIP_Addr());
			IPs.storeIPAddressList(Holder.allPeersIP);
		}else {
			client.close();
		}
	}

	public static void forEachInBoundPeerInfo(NodeData peerNode) {
		// TODO Auto-generated method stub
		Holder.inboundPeers.add(peerNode);
		Holder.allPeers.add(peerNode);
		Holder.allPeersIP.add(peerNode.getIP_Addr());
		IPs.storeIPAddressList(Holder.allPeersIP);
	}
	
	public static void addIPs(List<String> IPlist) throws IOException {
		// TODO Auto-generated method stub
		for(String ip : IPlist) {
			Holder.allPeersIP.add(ip);
		}
	}
	
}
