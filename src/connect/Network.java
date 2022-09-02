package connect;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import Blocks.mod.Block_obj;
import Blocks.mod.Epoch;
import Blocks.mod.PreviousBlockObj;
import Blocks.mod.StakeBlock;
import KryoMod.BlockListReg;
import KryoMod.CtxListReg;
import KryoMod.InetAddressListReg;
import KryoMod.PtxListReg;
import KryoMod.StakeObjListReg;
import KryoMod.StringListReg;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import connect.Inbound.KryonetClient;
import connect.Mod.Request;
import node.Mod.CheqIn;
import node.Mod.NodeData;
import penalty.Report;
import temp.Holder;
import transc.mod.Ctx;
import transc.mod.Ptx;
import vault.BackUpVault;
import vault.Vault_obj;
import wallets.mod.Acc_obj;

public class Network {
	static public final int port = 12035;
	static public final int port2 = 12036;
	static public final int port3 = 12037;
	String requestInitialblocks = "req-first-blocks";
	static String coinHash;
	//static HashMap<String,String> peerIBDstatus = new HashMap<String,String>();
	
	
	
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		ObjectSpace.registerClasses(kryo);
		kryo.register(org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey.class);
		kryo.register(org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey.class);
		kryo.register(java.math.BigDecimal.class);
		kryo.register(Block_obj.class, new JavaSerializer());
		kryo.register(PreviousBlockObj.class, new JavaSerializer());
		kryo.register(Request.class, new JavaSerializer());
		kryo.register(Report.class, new JavaSerializer());
		kryo.register(NodeData.class, new JavaSerializer());
		kryo.register(Integer.class);
		kryo.register(ArrayList.class);
		kryo.register(CopyOnWriteArrayList.class);
		kryo.register(BlockListReg.class, new JavaSerializer());
		kryo.register(StringListReg.class, new JavaSerializer());
		kryo.register(StakeObjListReg.class, new JavaSerializer());
		kryo.register(InetAddressListReg.class, new JavaSerializer());
		kryo.register(CtxListReg.class, new JavaSerializer());
		kryo.register(PtxListReg.class, new JavaSerializer());
		kryo.register(Acc_obj.class, new JavaSerializer());
		kryo.register(StakeBlock.class, new JavaSerializer());
		kryo.register(weightResults.class, new JavaSerializer());
		kryo.register(StakeObj.class, new JavaSerializer());
		kryo.register(Ctx.class, new JavaSerializer());
		kryo.register(Ptx.class, new JavaSerializer());
		kryo.register(byte[].class);
		kryo.register(Object.class);
		kryo.register(BackUpVault.class, new JavaSerializer());
		kryo.register(Vault_obj.class, new JavaSerializer());
		kryo.register(CheqIn.class, new JavaSerializer());
		kryo.register(Epoch.class, new JavaSerializer());
	}
	
	
	public static List<String> getOutBoundIPs() {
		List<String> IPs = new ArrayList<>();
		for(NodeData peer : Holder.outboundPeers) {
			IPs.add(peer.getIP_Addr());
		}
		return IPs;
	}
	/////
	public static List<String> getInBoundIPs() {
		List<String> IPs = new ArrayList<>();
		for(NodeData peer : Holder.inboundPeers) {
			IPs.add(peer.getIP_Addr());
		}
		return IPs;
	}
	/////
	public static boolean checkBestChain(String IP) {
		List<NodeData> allOutboundPeers = Holder.outboundPeers;
		allOutboundPeers.sort((o1, o2) -> BigInteger.valueOf(o2.getNumOfCtxBlocks()).compareTo(BigInteger.valueOf(o1.getNumOfCtxBlocks())));
		
		if(allOutboundPeers.get(0).getIP_Addr().equals(IP)) {
			return true;
		}
		return false;
	}
	
	public static void sendNetworkRequest(Request req) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		List<NodeData> allOutboundPeers = Holder.outboundPeers;
		allOutboundPeers.sort((o1, o2) -> BigInteger.valueOf(o2.getNumOfCtxBlocks()).compareTo(BigInteger.valueOf(o1.getNumOfCtxBlocks())));
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(allOutboundPeers.get(0).getIP_Addr())) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.sendRequest(req);
				        break;
				}
			}
        }
	}
	
	public static String returnBestHeight() {
		List<NodeData> allOutboundPeers = Holder.outboundPeers;
		allOutboundPeers.sort((o1, o2) -> BigInteger.valueOf(o2.getNumOfCtxBlocks()).compareTo(BigInteger.valueOf(o1.getNumOfCtxBlocks())));
		
		return String.valueOf(allOutboundPeers.get(0).getNumOfCtxBlocks());
	}
	
	public static void broadcastCheckIn() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		
		List<String>outboundIPs = Network.getOutBoundIPs();
		if(outboundIPs.size() != 0) {
			for (Thread t : threadSet) {
				for(int i = 0; i < outboundIPs.size(); i++) {
					if(t.getName().equals(outboundIPs.get(i))) {
						 KryonetClient clientThread = (KryonetClient)t;
					        clientThread.broadcastCheckIn("cheq_in");
					}
				}
	        }
		}
		
	}
	
	public static void broadcastStakeBlock(StakeBlock block) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastStakeBlock(block);
				}
			}
        }
	}
	
	public static void broadcastPackTx (Ptx ptx) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastPackTx(ptx);
				}
			}
        }
	}
	
    public static void broadcastCoinTx (Ctx ctx) {
    	Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastCoinTx(ctx);
				}
			}
        }
	}
    
    public static void broadcastNewAcount (Acc_obj account) {
    	Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastNewAcount(account);
				}
			}
        }
	}
    
    public static void broadcastNewStakeAcount (StakeObj stakeAccount) {
    	Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastNewStakeAcount(stakeAccount);
				}
			}
        }
	}
    
    public static void broadcastReport (Report report) {
    	Holder.myReports.add(report);
   	}

	public static void broadcastweightResults(List<weightResults> weights) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastweightResults(weights);
				}
			}
        }
		
	}

	public static void broadcastCoinBlock(Block_obj NewCoinBlock) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(outboundIPs.get(i))) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.broadcastCoinBlock(NewCoinBlock);
				}
			}
        }
		
	}
	
	
	public static void requestSingleAccount(String coinAddress) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		List<NodeData> allOutboundPeers = Holder.outboundPeers;
		allOutboundPeers.sort((o1, o2) -> BigInteger.valueOf(o2.getNumOfCtxBlocks()).compareTo(BigInteger.valueOf(o1.getNumOfCtxBlocks())));
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(allOutboundPeers.get(0).getIP_Addr())) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.requestSingleAccData(coinAddress);
				        break;
				}
			}
        }
		
	}
	
	public static void requestSingleStakeOBJ(String coinAddress) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String>outboundIPs = Network.getOutBoundIPs();
		List<NodeData> allOutboundPeers = Holder.outboundPeers;
		allOutboundPeers.sort((o1, o2) -> BigInteger.valueOf(o2.getNumOfCtxBlocks()).compareTo(BigInteger.valueOf(o1.getNumOfCtxBlocks())));
		
		for (Thread t : threadSet) {
			for(int i = 0; i < outboundIPs.size(); i++) {
				if(t.getName().equals(allOutboundPeers.get(0).getIP_Addr())) {
					 KryonetClient clientThread = (KryonetClient)t;
				        clientThread.requestSingleStakeOBJData(coinAddress);
				        break;
				}
			}
        }
		
	}
	

}
