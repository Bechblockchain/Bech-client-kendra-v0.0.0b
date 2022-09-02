package genesis;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.weightResults;
import Transc_Util.Merkle_tree;
import crypto.BytesToFro;
import crypto.HashUtil;
import temp.Static;
import threads.CoinBlockThread;
import threads.StakeBlockThread;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import transc.mod.Ptx;
import vault.BackUpVault;
import vault.Vault_obj;
import vault.db.vault_store;
import wallets.db.Str;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;
import wallets.mod.Acc_obj;

public class Genesis implements Runnable{
	static List<weightResults> weights;
	@Override
	public void run() {
	
			createAccObjs();
			
			List<Ctx> tx = createGenesisCtx();
			createGenesisBlock(tx);
			
			try {
				Thread.sleep(5000);
				createStakeBlock(tx);
			} catch (InterruptedException | IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	
	private StakeBlock createStakeBlock(List<Ctx> ctxlist) throws IOException {

		StakeBlock retrievedStakeblock = null;
					try {
				EasyRam DB = new EasyRam(db.paths.db.genStakePath);
				DB.createStore("genStakeblock", DataStore.Storage.PERSISTED,1);
				retrievedStakeblock = (StakeBlock)DB.getObject("genStakeblock", "genStakeblock");
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}
			
			
			try {
				StakeBlockThread.newStakeBlock.put(retrievedStakeblock);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Block_db.storeLastestStakeHash(retrievedStakeblock.getHash());
			Block_db.storeStakeBlockData(retrievedStakeblock);
			
		
		return retrievedStakeblock;
	}

	public static List<Ctx> createGenesisCtx() {
		
		 List<Ctx> retrievedTx = new ArrayList<>();
		 Ctx ctx = null;
		 Ctx ctx2 = null;
		 Ctx ctx3 = null;
				  
			try {
				EasyRam DB = new EasyRam(db.paths.db.genCtxPath);
				DB.createStore("genCtx", DataStore.Storage.PERSISTED,1);
				ctx = (Ctx)DB.getObject("genCtx", "genCtx");
				
				EasyRam DB2 = new EasyRam(db.paths.db.genCtxPath);
				DB2.createStore("genCtx2", DataStore.Storage.PERSISTED,1);
				ctx2 = (Ctx)DB2.getObject("genCtx2", "genCtx2");
				
				EasyRam DB3 = new EasyRam(db.paths.db.genCtxPath);
				DB3.createStore("genCtx3", DataStore.Storage.PERSISTED,1);
				ctx3 = (Ctx)DB3.getObject("genCtx3", "genCtx3");
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}
		  
		  retrievedTx.add(ctx);
		  retrievedTx.add(ctx2);
		  retrievedTx.add(ctx3);
			
		return retrievedTx;
	}
	
	public static Block_obj createGenesisBlock(List<Ctx> ctxlist) {
	   
		Block_obj retrievedblock = null;
		
		try {
			EasyRam DB = new EasyRam(db.paths.db.genCoinPath);
			DB.createStore("genCoinblock", DataStore.Storage.PERSISTED,1);
   			retrievedblock = (Block_obj)DB.getObject("genCoinblock", "genCoinblock");
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
		
		try {
			CoinBlockThread.newCoinBlock.put(retrievedblock);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
			
		return retrievedblock;
	}
	
	public void createAccObjs() {
		
		BackUpVault backupVault = new BackUpVault(Static.BACKUP_VAULT,new BigDecimal(0),returnKey("backUpPubkey"),0);
		Vault_obj  maintenanceVault = new Vault_obj(Static.MAINTENANCE_VAULT,new BigDecimal(100000000000L),returnKey("maintenancePubkey"),0);
		
			HashMap<String,BigInteger> tradesNbalances = null;
			
			vault_store.storeMaintenanceVaultData(maintenanceVault);
			vault_store.storeBackUpVaultData(backupVault);
			
			Acc_obj acc = new Acc_obj("a4edab8e619ba7b7ee09623df3b1ef85df8bae655ff90ad93f","1K3vnyysXwmMGqu2Ai2MBswMLdDvF81CvU",0,0,new BigDecimal(0),returnKey("firstAccountPubkey"),tradesNbalances);
			
			//Store first Account
			Str.storeSingleAccData(acc);
			
			Acc_obj acc2 = new Acc_obj("5a0e622d89395d1067cb0caa469ace415af270a0b55c1d8fc0","1JWCjqDy2tMA7aMZmZkVN6TLiswi3APbho",0,0,new BigDecimal(0),returnKey("secondAccountPubkey"),tradesNbalances);
			
			Str.storeSingleAccData(acc2);
			
			Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = "5a0e622d89395d1067cb0caa469ace415af270a0b55c1d8fc0";
			
			Static.EPOCH_VALIDATOR_ADDRESS = "5a0e622d89395d1067cb0caa469ace415af270a0b55c1d8fc0";

	}
		
	public static PublicKey returnKey(String key) {
		
		FileInputStream keyfis;PublicKey pubkey = null;
		try {
			keyfis = new FileInputStream("src/vault/" + key);
			byte[] encKey = new byte[keyfis.available()];  
			keyfis.read(encKey);
			pubkey =  wallets.crypto.Hasher.getPublicKeyfromByteArray(encKey);
			keyfis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return pubkey;
	}
		
	public static PrivateKey returnPrivKey(String key) {
		
		FileInputStream keyfis;PrivateKey privkey = null;
		try {
			keyfis = new FileInputStream("src/vault/" + key);
			byte[] encKey = new byte[keyfis.available()];  
			keyfis.read(encKey);
			privkey =  wallets.crypto.Hasher.getPrivateKeyfromByteArray(encKey);
			keyfis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return privkey;
	}
	
	
}
