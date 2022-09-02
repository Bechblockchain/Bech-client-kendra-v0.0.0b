package connect.Outbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections4.ListUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import Blocks.Validate.Util.ReportVerification.VerifyPenalty;
import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.retrieve;
import connect.Network;
import connect.Inbound.KryonetClient;
import connect.Mod.Request;
import crypto.BytesToFro;
import db.db_retrie;
import node.PeerInfo;
import node.Proto;
import node.Mod.CheqIn;
import node.Mod.NodeData;
import node.db.IPs;
import penalty.Report;
import temp.Holder;
import temp.Static;
import threads.CoinBlockThread;
import threads.StakeBlockThread;
import threads.Accounts.AccountsThread;
import threads.Tx.CoinTxThread;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import transc.mod.Ptx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class KryonetServer {

	public static int connectionsCounter;
	  
	static Client client = KryonetClient.client;
	
	Server server;
	   String requestPeers = "hot-node-info";
	   String coldeHeaders = "cold-headers";
	   String myBestChain = "my-best-chain";
	   String startBlockUpload = "start-block-download";
	   String IBDdownload = "IBD-block-download";
	   String AccountDataList = "acc-data";
	   String singAccData = "single-accData";
	   String requestTimestamp = "timestampRequest";
	   List<weightResults> stakingResults;
	  
    public KryonetServer () throws IOException{
       int writebuffer = 62500000;
       int objectbuffer = 62500000;
 	   server = new Server(writebuffer,objectbuffer); 
		  Network.register(server);
		   
		  server.addListener(new Listener() {
			  List<Acc_obj> list = new CopyOnWriteArrayList<>();
			  List<Acc_obj> accList = new CopyOnWriteArrayList<>();
			  int targetSize = 0;
			  List<List<Acc_obj>> listOfLists = new CopyOnWriteArrayList<>();
		      
			  public void connected (Connection c) {
					if(connectionsCounter < Static.MAX_INBOUND_PEERS) {
						connectionsCounter++;
						
						list = Retrie.retrieveBulkAccData(Retrie.returnCoinAddresses());
						targetSize = (int)( (5f/100f)*list.size());
					    listOfLists = ListUtils.partition(list, targetSize);
						
					}else {
						c.close();
					}
						
			    }
				public void received (Connection c, Object object) {
					
					if (object instanceof NodeData) {
						long receivedTime = System.currentTimeMillis();
						NodeData peerNode = (NodeData)object;
						 NodeData nodeData = null;
							PeerInfo.forEachInBoundPeerInfo(peerNode);
							nodeData = Proto.returnNodeInfo(String.valueOf(receivedTime));
							c.sendTCP(nodeData);
						
						return;
					}
				
					//Send single account data
					if (object instanceof Acc_obj) {
						String accAddress = ((Acc_obj)object).getCoinAddress();
						
						c.sendTCP(Retrie.retrieveAccData(accAddress));
						
						return;
					}
					
					if (object instanceof Request) {
						Request request = ((Request)object);
						String command = request.getCommand();
						if(command.equals("getCoinBlocks")) {
							
							c.sendTCP(Block_db.getBlockDataList(request.getItem()));
							
						}else if(command.equals("req-urgent-single-accData")) {
						  
							Acc_obj account = Retrie.retrieveAccData(request.getItem());
							Request resume = new Request("requested-urgent-single-acc",null,account,null,true);
							c.sendTCP(resume);
							
						}
						
						//Ctx finality request
						sendTxFinality(request,c);
						
						return;
					}
					
					if (object instanceof String) {
						String command = ((String)object);
						
						//Send all account data
						sendAccData(listOfLists,accList,list,targetSize,command,c);
						
						//Send single account data
						sendSingleAccData(command,c);
						
						if(command.equals("cheq_in")) {
							cheqIn(c);
						}
												
						if(command.equals("req-ips")) {
						 	
							c.sendTCP(IPs.getIPAddressList());
							
						}
						
						
						return;
					}
					//Recieve reports
					if (object instanceof Report) {
						Report report = ((Report)object);
						
							try {
								if(VerifyPenalty.verifyPenaltyDecision(report)) {
									Holder.receivedReports.add(report);
								}
							} catch ( ClassNotFoundException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						return;
					}
					
					if(object instanceof Block_obj) {
							
						Block_obj coinblock =  (Block_obj)object;
						try {
							addToNewCoinBlockQueue(coinblock);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						return;
					}
					
					
					if(object instanceof StakeBlock) {
							
						StakeBlock stakeblock =  (StakeBlock)object;
						try {
							addToNewStakeBlockQueue(stakeblock);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					
					if(object instanceof Ctx) {
						Ctx ctx = (Ctx)object;
						sendAddToCtxQueue(ctx);
							
						return;
					}
					
					if(object instanceof Ptx) {
						Ptx ptx = (Ptx)object;
						Holder.addPackTranc2Holder(ptx);
						Network.broadcastPackTx(ptx);
						
						return;
					}

					if (object instanceof List) {
						
						if(stakingResults instanceof List) {
							if(Static.START_TYPE.equals(Static.WARM_START)) {
								List<weightResults> results = (List<weightResults>)stakingResults;
								try {
									valWeightResults(results);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							return;
						}
						
						return;
					}
					
					
					if(object instanceof Acc_obj) {
						Acc_obj account = (Acc_obj)object;
						sendAddNewAccToQueue(account);
						return;
					}
					
				}
				
				public void disconnected (Connection c) {
				//	ChainConnection connection = (ChainConnection)c;
					System.out.println("inbound server clossing.........");
					Thread.currentThread().interrupt();
				}
				
    });
		  server.bind(Network.port);
		  server.start();
    }
   
    static class ChainConnection extends Connection {
    	ChainConnection conn = new ChainConnection();
		public int peerNum = conn.getID();
	}
    
    public static void sendAccData(List<List<Acc_obj>> listOfLists, List<Acc_obj> accList,List<Acc_obj> list,int targetSize,String req,Connection c) {
 	   if(req.contains("_")) {
 		   String [] string = req.split("_");
 		   String command = string[0];
 		   String batchNum = string[1];
 		   
 		   if(command.equals("nextAccBatch")) {
 			   if(Integer.parseInt(batchNum) <= targetSize) {
 				  accList = listOfLists.get(Integer.parseInt(batchNum));
 	 			  c.sendTCP(accList);
 			   }else {
 				  c.sendTCP(null);
 			   }
 			 
 		   }
 		   
 	   }else if(req.equals("req-acc-data")) {
	 		   accList = listOfLists.get(0);
	 		   c.sendTCP(accList);
	   }else if(req.equals("acc-transfer-end")) {
	 		   list.clear();
	 		  listOfLists.clear();
	   }
 	   
		 
	 }
    
    public static void sendSingleAccData(String req,Connection c) {
	   if(req.contains("|")) {
		   String [] string = req.split("[|]");
		   String first = string[0];
		   String second = string[1];
		   
		   if(first.equals("req-single-accData")) {
			  
				c.sendTCP(Retrie.retrieveAccData(second));
			
		   }else if(first.equals("req-single-StakeOBJData")) {
				  
				c.sendTCP(retrieve.retrieveSingleStakeData(second));
					
		   }
		    
	 }
   
    }
    
    public static void sendAddNewAccToQueue(Acc_obj account) {
	   	try {
	   		AccountsThread.newAccount.put(account);
	   	} catch (InterruptedException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
    
    public static void sendAddToCtxQueue(Ctx ctx) {
    	try {
    		 CoinTxThread.newCtx.put(ctx);
    	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
    }
    
    public static void addToNewStakeBlockQueue(StakeBlock newStakeBlock) throws InterruptedException {
    	StakeBlockThread.newStakeBlock.put(newStakeBlock);
	}
    
    public static void addToNewCoinBlockQueue(Block_obj newCoinBlock) throws InterruptedException {
    	CoinBlockThread.newCoinBlock.put(newCoinBlock);
	}
    
    public static void valWeightResults(List<weightResults> results) throws InterruptedException {
		List<weightResults> validResults = new ArrayList<>();
		for(weightResults weight : results) {
			
			String data = weight.toString() + weight.getEpoch().toString() + weight.getProcessingEpoch().toString() + weight.getValidatorConfirmationEpoch().toString() + weight.getEpochAllowance().toString() + weight.getFallPosition().toString();	
			if(Hasher.verifiyValidatorSignature(null, data, BytesToFro.hexStringToBytes(weight.getSignature()) )){
				validResults.add(weight);
			}else {
				return;
			}
			
		}
		Network.broadcastweightResults(validResults);
		
	}
    
    public void cheqIn(Connection c) {
    	long receivedTime = System.currentTimeMillis();
		
		NodeData nodeData = null;
		
  		nodeData = Proto.returnNodeInfo(String.valueOf(receivedTime));
			
		c.sendTCP(new CheqIn(nodeData));
    }
      
    public void sendTxFinality(Request request, Connection c) {
    	String command = request.getCommand();
    	Request req = null; 
		if(command.equals("getTxFinality")) {
	    
			String txHexItem;
			try {
				txHexItem = db_retrie.getCoinTxIndex(request.getItem());
				req = new Request("txFinalityCheq",txHexItem, String.valueOf(Static.PREV_BLOCK_NUM)); 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	c.sendTCP(req);
    }
   
}
