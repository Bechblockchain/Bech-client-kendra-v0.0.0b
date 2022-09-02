package connect.Inbound;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Blocks.mod.Block_obj;
import Blocks.mod.PreviousBlockObj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import connect.Network;
import connect.Mod.Request;
import node.PeerInfo;
import node.Proto;
import node.Mod.CheqIn;
import node.Mod.NodeData;
import node.db.IPs;
import penalty.Report;
import temp.Holder;
import temp.Static;
import threads.InitialBlockDownloadThread;
import transc.mod.Ctx;
import transc.mod.Ptx;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class KryonetClient extends Thread {
	public static Client client ;
	   String IP;
	   String requestCoinblocks = "getCoinBlocks";
	   String requestAccountData = "req-acc-data";
	   String reqSingleAccData = "req-single-accData";
	   String AccDone = "acc-transfer-end";
	   String reqIPs = "req-ips";
	   List<Acc_obj> accountData;
	   List<String> IPList;
	   List<Block_obj> blockList;
	   Block_obj lastCoinBlock;
	  
	   List<Block_obj> coinBlocksList = new CopyOnWriteArrayList<>();
	   public static int connectedServers;
	   
	public KryonetClient (String IP) {
		this.IP = IP;
	}
	
	@Override
	public void run () {
		try {
			  int writebuffer = 62500000;
		      int objectbuffer = 62500000;
			this.setName(IP);
			client = new Client(writebuffer,objectbuffer);
			client.start();
			Network.register(client);
			
			
			client.connect(5000,IP,Network.port);
			
			client.addListener(new Listener() {
				long clientSentTime;
				NodeData nodeData = null;
				public void connected (Connection connection) {
					connectedServers++;
					
			    	  nodeData = Proto.returnClientNodeInfo();
					
					  clientSentTime = System.currentTimeMillis();
					  client.sendTCP(nodeData);
					  client.sendTCP(reqIPs);
					 
				}
				
				int accListNumber;
				
				public void received (Connection connection, Object object) {
					
					if (object instanceof CheqIn) {
						long clientReceivedTime = System.currentTimeMillis();
						CheqIn cheqIn = (CheqIn)object;
						
						PeerInfo.forEachOutBoundPeerInfo(client,cheqIn.getNodeData(),Static.CLIENT_SENT_TIME,clientReceivedTime);
							Static.NETWORK_BLOCK_HEIGHT =  Network.returnBestHeight();
						
						
					}
					
					if (object instanceof NodeData) {
						long clientReceivedTime = System.currentTimeMillis();
						NodeData peerNode = (NodeData)object;
						
							PeerInfo.forEachOutBoundPeerInfo(client,peerNode,clientSentTime,clientReceivedTime);
							
							if((System.currentTimeMillis() - PreviousBlockObj.getLatestBlockData().getTimestamp()) >= Static.COLD_START_TIME_THRESHOLD ) {
								if(Network.checkBestChain(IP)) {
									client.sendTCP(requestAccountData);
								}
							}else {
								Request request = new Request(requestCoinblocks,PreviousBlockObj.getLatestBlockData().getBlockNum().toString());
								client.sendTCP(request);
							}
							
						
						return;
					}
				
					
					if (object instanceof List) {
						
						if(IPList instanceof List) {
							List<String> ips = (List<String>)IPList;
							
							IPs.storeIPAddressList(ips);
							
						}
						
						if(accountData instanceof List) {
							List<Acc_obj> accDataList = (List<Acc_obj>)accountData;
							if(accDataList != null) {
								
								Str.storeBulkAccData(accDataList);
								accListNumber++;
								String next = "nextAccBatch_" + String.valueOf(accListNumber);
								client.sendTCP(next);
								
							}else {
								accListNumber = 0;
								client.sendTCP(AccDone);
								
								Request request = new Request(requestCoinblocks,PreviousBlockObj.getLatestBlockData().getBlockNum().toString());
								client.sendTCP(request);
								
							}
						}
						
						if(blockList instanceof List) {
							coinBlocksList = (List<Block_obj>)blockList;
							
							if(coinBlocksList.size() <= Static.MAX_BLOCKS_REQUEST) {
								
								coinBlocksList.sort((o1, o2) -> new BigInteger(o1.getBlock_num()).compareTo(new BigInteger(o2.getBlock_num())));
								Request request = new Request(requestCoinblocks,coinBlocksList.get(coinBlocksList.size() - 1).getBlock_num());
								validateBlocks(coinBlocksList);
								client.sendTCP(request);
							
							}else if(coinBlocksList.size() == 0){
								
								coinBlocksList.sort((o1, o2) -> new BigInteger(o1.getBlock_num()).compareTo(new BigInteger(o2.getBlock_num())));
								validateBlocks(coinBlocksList);
								coinBlocksList.clear();
								Static.START_TYPE = Static.WARM_START;
							}
						}
						return;
					}
					
					
					if (object instanceof Request) {
						Request request = ((Request)object);
						
						txFinality(request);
						
						return;
					}
				}

				public void disconnected (Connection connection) {
					Thread.currentThread().interrupt();
				}
			});
			
			
			
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public void validateBlocks(List<Block_obj> blocklist) {
		 try {
			InitialBlockDownloadThread.downloadedBlocks.put(blocklist);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void reqInitialBlocks(String reqInitialBlocks) {
		client.sendTCP(reqInitialBlocks);
	}
	
	public void reqNextBlocks(long reqNextBlocks) {
		client.sendTCP(reqNextBlocks);
	}
	
	public void broadcastPackTx (Ptx ptx) {
		client.sendTCP(ptx);
	}
	
    public void broadcastStakeTx (Ctx stx) {
    	client.sendTCP(stx);
	}
    
    public void broadcastCoinTx (Ctx ctx) {
    	client.sendTCP(ctx);
	}
       
    public void broadcastNewAcount (Acc_obj account) {
    	client.sendTCP(account);
	}
    
    public void broadcastNewStakeAcount (StakeObj stakeAccount) {
    	client.sendTCP(stakeAccount);
	}
    
    public void broadcastReport (Report report) {
    	Holder.myReports.add(report);
    	
   	}

	public void broadcastweightResults(List<weightResults> weights) {
		client.sendTCP(weights);
		
	}

	public void broadcastCoinBlock(Block_obj NewCoinBlock) {
		client.sendTCP(NewCoinBlock);
		
	}
	
	public void sendRequest(Request request) {
		client.sendTCP(request);
	}

	public void broadcastStakeBlock(StakeBlock NewStakeBlock) {
		client.sendTCP(NewStakeBlock);
	}
	
	public void broadcastCheckIn(String checkIn) {
		client.sendTCP(checkIn);
	}
	
	public void requestSingleAccData (String coinAddress) {
		String requestedAccount = "req-single-accData|" + coinAddress;
		client.sendTCP(requestedAccount);
	}
	
	public void requestSingleStakeOBJData (String coinAddress) {
		String requestedData = "req-single-StakeOBJData|" + coinAddress;
		client.sendTCP(requestedData);
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		client.close();
		Thread.currentThread().interrupt();
	}
	
	public void txFinality(Request request) {
		String command = request.getCommand();
		if(command.equals("txFinalityCheq")) {
			String blocknum  = request.getItem();
			if(Long.parseLong(Static.PREV_BLOCK_NUM) > Long.parseLong(blocknum) + 5) {
				System.out.println("Transaction is in block " + blocknum + " and has reached finality");
			}else{
				System.out.println("Transaction is in block " + blocknum + " and waiting to reach finality");
			}
			
		}
	}
	
	
}
