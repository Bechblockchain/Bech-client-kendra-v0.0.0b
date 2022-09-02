package cli;

import java.io.IOException;
import java.security.Security;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import Blocks.epoch.FormBlock;
import Blocks.mod.PreviousBlockObj;
import KryoMod.KryoSeDe;
import connect.IpsForNewClients;
import connect.Network;
import connect.Inbound.KryonetClient;
import connect.Inbound.ReqAllPeers;
import connect.Outbound.KryonetServer;
import connect.Outbound.sendAllPeers;
import connect.Util.NetworkTime;
import genesis.Genesis;
import node.db.IPs;
import node.db.Node_db;
import picocli.CommandLine.Command;
import temp.Holder;
import temp.Static;
import threads.CoinBlockThread;
import threads.InitialBlockDownloadThread;
import threads.StakeBlockThread;
import threads.ValidatorAccountThread;
import threads.Accounts.AccountMapMem;
import threads.Accounts.AccountsThread;
import threads.Tx.CoinTxThread;
import threads.Tx.TxIndexThread;
import wallets.db.Retrie;

@Command( name = "start", mixinStandardHelpOptions = true ,description = "starts native node machine")
public class Start implements Runnable{

	public void start() throws IOException, ClassNotFoundException {
		//Register classes
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		Static.NUM_OF_CORES = Runtime.getRuntime().availableProcessors();
		
		if(Static.NUM_OF_CORES < 8) {
			System.out.println("Minimum number of cpu cores required is 8 /n upgrade your machine...");
			System.exit(0);
		}
		
		startThreads();
		
		KryoSeDe.register();
		
		//start and elapsed time
		uptime();
		
		//Add ntp servers to machine 
		NetworkTime.addNtp();
		
		//Check System time with NTP servers
		checkSystemTime();
		
		if(PreviousBlockObj.getLatestBlockData() == null) {
			//creates genesis block if a cold start
			new Genesis().run();
		}

		//Get native staking account
		String nativeStakingAddress = Retrie.retrieveNativeValidatorAddress();
		if(nativeStakingAddress == null) {
			//create native staking account
			 new ValidatorAccountThread().run();
		}
		
		//Store Node data
		nodeCycle();
		
		//Connect to peers
		connectpeers();
		
		//Start kryonet outbound/server tcp  
		new KryonetServer ();
		
		//Start kryonet tcp server to serve ips to peers 
		new sendAllPeers();
		
		//Check In with peers periodically
		reqCheckIn();
	}
			
	public static void connectpeers() throws IOException, ClassNotFoundException {
		System.out.println("Trying to connect to peers....." + "\n");
		if(IPs.getIPAddressList() == null) {
			Static.START_TYPE = Static.COLD_START;
			do{
				System.out.println("Requesting for potential peer ips....." + "\n");
				new ReqAllPeers(IpsForNewClients.returnIP());
			}while(IPs.getIPAddressList() == null);
		}
		System.out.println("Potential peer ips received....." + "\n");
		System.out.println("Connecting to potential peers....." + "\n");
		int counter =0;
		do {
			List<String> randomIPs = pickNRandom(IPs.getIPAddressList(), Static.MAX_OUTBOUND_PEERS - KryonetClient.connectedServers);
			int max = Static.MAX_OUTBOUND_PEERS + 1;
			for(int j = 0; j < max; j++) {
				String ip  = randomIPs.get(j);
				if(!Holder.allPeersIP.contains(ip)) {
					counter++;
				//	new KryonetClient(ip);
					new Thread (new KryonetClient(ip)).start();
					System.out.println("Peer " + counter + " connected....." + "\n");
				}
			}
		}while(KryonetClient.connectedServers < Static.MAX_OUTBOUND_PEERS);
		
	
	}
	
	public static List<String> pickNRandom(List<String> lst, int n) {
	    List<String> copy = new ArrayList<String>(lst);
	    Collections.shuffle(copy);
	    return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
	}
	
	public void reqCheckIn() {
		java.util.Timer timer = new java.util.Timer();
		timer.schedule( new TimerTask() {
		    public void run() {
		    	Static.CLIENT_SENT_TIME = System.currentTimeMillis();
		    	Network.broadcastCheckIn();
		    }
		 }, 0, 60000);
		
	}
	
	public void checkSystemTime() {
		System.out.println("Checking sytem time....." + "\n");
		java.util.Timer timer = new java.util.Timer();
		timer.schedule( new TimerTask() {
		    public void run() {
					NetworkTime.checkSetTime();
		    }
		 }, 0, 300000);
		
	}
	
	public void nodeCycle() {
		java.util.Timer timer = new java.util.Timer();
		timer.schedule( new TimerTask() {
		    public void run() {
		    	Node_db.storeNodeData(Static.VERSION, Static.NODE_TYPE, Static.NATIVE_VALIDATOR_ADDRESS, String.valueOf(Static.NATIVE_BLOCK_HEIGHT), Static.EPOCH_HEIGHT, String.valueOf(Static.NUM_EPOCH_WON), String.valueOf(System.currentTimeMillis()));
		    }
		 }, 0, 15000);
	}
	
	public void uptime() {
	    long time  = System.currentTimeMillis();
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
	    String startTime = format.format(date);
	    Static.START_TIME = startTime;
	}
	
	public static void startThreads() {
		new Thread (new CoinBlockThread()).start(); //starts coin block validation thread
		new Thread (new StakeBlockThread()).start(); //starts stake block validation thread
		new Thread (new InitialBlockDownloadThread()).start(); //starts IBD thread
		new Thread (new AccountMapMem()).start(); //starts accountMemMap thread
		new Thread (new AccountsThread()).start(); //starts newAccounts validation thread
		new Thread (new CoinTxThread()).start(); //starts newCtxs thread
		new Thread (new TxIndexThread()).start(); //starts TxIndex thread
		new Thread (new FormBlock()).start(); //starts blocks formation thread

	}
	 
	
	public static void main(String args[]) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		Static.NUM_OF_CORES = Runtime.getRuntime().availableProcessors();
		
		startThreads();
		KryoSeDe.register();
		new Genesis().run();
	
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			KryoSeDe.register();
			start();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
