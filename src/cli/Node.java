package cli;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.Security;

import connect.Inbound.KryonetClient;
import connect.Outbound.KryonetServer;
import node.Mod.NodeData;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Holder;
import temp.Static;

@Command( name = "node", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nnative node data")
public class Node implements Runnable{

	@Option(names = { "-n", "--node" }, required = true,description = "Hex of transaction signature")
	String command;
	
	public void getCommand() {
		if(command.equals("version")) {
			System.out.println("Version- " +Static.VERSION);
			
		}else if(command.equals("blockHeight")) {
			System.out.println("Node type- " +Static.NATIVE_BLOCK_HEIGHT);
			
		}else if(command.equals("type")) {
			System.out.println("Node type- " +Static.NODE_TYPE);
			
		}else if(command.equals("epochHeight")) {
			System.out.println("Epoch height- " +Static.EPOCH_HEIGHT);
		
		}else if(command.equals("epochWon")) {
			System.out.println("Number of epochs won- " +Static.NUM_EPOCH_WON);
		
		}else if(command.equals("outboundPeerNum")) {
			System.out.println("Number of connected outbound peers- " +KryonetClient.connectedServers);
		
		}else if(command.equals("inboundPeerNum")) {
			System.out.println("Number of connected inbound peers- " +KryonetServer.connectionsCounter);
		
		}else if(command.equals("peerTotal")) {
			int sum = KryonetClient.connectedServers + KryonetServer.connectionsCounter;
			System.out.println("Total peers- " +sum);
		
		}else if(command.equals("outboundPeerNumLimit")) {
			System.out.println("Outbound peer max limit- " + Static.MAX_OUTBOUND_PEERS);
		
		}else if(command.equals("inboundPeerNumLimit")) {
			System.out.println("Inbound peer max limit- " +Static.MAX_INBOUND_PEERS);
		
		}else if(command.equals("startTime")) {
			System.out.println("Node started at- " +Static.START_TIME);
		
		}else if(command.equals("totalUpTime")) {
			elapsedTime();
		
		}else if(command.equals("allPeerIps")) {
			int count=0;
			for(String ip : Holder.allPeersIP) {
				count++;
				System.out.println("Peer" + count + "- " +ip);
			}
			
		}else if(command.equals("totalPeers")) {
			System.out.println("Total Peers- " +  Holder.allPeers.size());
		
		}else if(command.equals("totalInboundPeers")) {
			System.out.println("Total inbound peers- " +  Holder.inboundPeers.size());
			
		}else if(command.equals("totalOutboundPeers")) {
			System.out.println("Total outbound peers- " +  Holder.outboundPeers.size());
		
		}else if(command.equals("allNodes")) {
			int count=0;
			System.out.println("All Peers\n");
			for(NodeData node : Holder.allPeers) {
				count++;
				System.out.println("Peer " + count);
				displayNodeData(node); 
			}
			System.out.println("Total Peers- " + count);
		
		}else if(command.equals("allInboundNodes")) {
			int count=0;
			System.out.println("All inbound peers\n");
			for(NodeData node : Holder.inboundPeers) {
				count++;
				System.out.println("Peer " + count);
				displayNodeData(node); 
			}
			System.out.println("Total inbound nodes- " + count);
		
		}else if(command.equals("allOutboundNodes")) {
			int count=0;
			System.out.println("All outbound peers\n");
			for(NodeData node : Holder.outboundPeers) {
				count++;
				System.out.println("Peer " + count);
				displayNodeData(node); 
			}
			System.out.println("Total outbound nodes- " + count);
		}
	
	}
	
	
	public static void main(String args[]) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Node()).execute(args);

	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		getCommand();
	}

	public void displayNodeData(NodeData node) {
		System.out.println(" Version- " + node.getVersion());
		System.out.println(" Node type- " + node.getNodeType());
		System.out.println(" IP address- " + node.getIP_Addr());
		System.out.println(" Chain height- " + node.getNumOfCtxBlocks());
		System.out.println(" Epoch height- " + node.getEpochHeight());
		System.out.println(" Num of epoch won- " + node.getVersion() + "\n");
		
		
	}
	
	public void elapsedTime() {

		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long  passedTimeInMs = rb.getUptime();
	        
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = passedTimeInMs / daysInMilli;
		passedTimeInMs = passedTimeInMs % daysInMilli;
		
		long elapsedHours = passedTimeInMs / hoursInMilli;
		passedTimeInMs = passedTimeInMs % hoursInMilli;
		
		long elapsedMinutes = passedTimeInMs / minutesInMilli;
		passedTimeInMs = passedTimeInMs % minutesInMilli;
		
		long elapsedSeconds = passedTimeInMs / secondsInMilli;
		
		Static.TOTAL_UP_TIME = elapsedDays + " days: " + elapsedHours + " Hrs: " + elapsedMinutes + " Mins: " + elapsedSeconds;
		System.out.print("\rTotal node up time- " +Static.TOTAL_UP_TIME);
	}
	
	
}
