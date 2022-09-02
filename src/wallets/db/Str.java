package wallets.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import KryoMod.StringListReg;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;
import wallets.mod.Acc_obj;

public class Str {
	
	///// Store Native Validator Address CoinAddress	
	public static void storeNativeValidatorAddress(String coinAddress) {
		
		try {
			 EasyRam accDB = new EasyRam(db.NATIVE_VAL_ADDRESS);
			 accDB.createStore("ValAddress", DataStore.Storage.PERSISTED,1);
			 accDB.putString("ValAddress","ValidatorAddress", coinAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	///////////////////////////////////////////////////////////////////////
	
	///Store key
	public static void storePubPrivKey(PublicKey publicKey, PrivateKey privateKey) {
        
		try {
			 EasyRam accDB = new EasyRam(db.PUB_PRIV_PATH);
			 accDB.createStore("keyGen", DataStore.Storage.PERSISTED,1);
			 accDB.putObject("keyGen",wallets.crypto.Hasher.returnPublicKeyString(publicKey), privateKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }
 	
	
	///// Store Single Account Data
	public static void storeSingleAccData(Acc_obj Account) {
		
		try {
			 EasyRam accDB = new EasyRam(db.COIN_ACCOUNTS);
			 accDB.createStore("AccountsDB", DataStore.Storage.PERSISTED,4);
			 accDB.putObject("AccountsDB",Account.getCoinAddress(), Account);
			 storeCoinAddress(Account.getCoinAddress());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	public static void storeCoinAddress(String coinAddress) {
		
		 List<String> list = new ArrayList<>();
		       try {
		            EasyRam accDB = new EasyRam(db.COIN_ADDRESSES_PATH);
					 accDB.createStore("Addresses", DataStore.Storage.PERSISTED,200);
					 
					 StringListReg  stringListObj = (StringListReg) accDB.getObject("Addresses", "addresses");
			    	  
			    	  if(stringListObj != null) {
			    		  list = stringListObj.getStringList();
			    		  if(!stringListObj.getStringList().contains(coinAddress)) {
			    			  list.add(coinAddress);
			    		  }
			    		 
			    	  }else {
			    		  list.add(coinAddress);
			    	  }
			    	  stringListObj = new StringListReg(list);
					 
					 accDB.putObject("Addresses","addresses", stringListObj);
		            
		            
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	}
	
	///// Store Bulk Coin Account Data	
	public static void storeBulkAccData(List<Acc_obj> accounts) {
	     int size;
		 if(accounts.size() > 10000) {
			size = 10000; 
		 }else {
			 size = accounts.size();
		 }
		 
		 try {
			 
			 EasyRam accDB = new EasyRam(db.COIN_ACCOUNTS);
			 accDB.createStore("AccountsDB", DataStore.Storage.PERSISTED,size);
			
			 
			 String coinAddress = null;Acc_obj newAccount = null;
			 for(Acc_obj account: accounts) {
				    coinAddress = account.getCoinAddress();
					String mintAddress = account.getMintAddress();
					long ctxNonce = 0;
					long ptxNonce = 0;
					BigDecimal coinBalance = new BigDecimal(0);
					PublicKey pubkey = account.getPubkey();
					HashMap<String,BigInteger> tradesNbalances = account.getTradesNbalances();
					tradesNbalances.clear();
					newAccount = new Acc_obj(coinAddress,mintAddress,ctxNonce,ptxNonce,coinBalance,pubkey,tradesNbalances);
					accDB.putObject("AccountsDB",coinAddress, newAccount);
					Str.storeCoinAddress(coinAddress);
			 }
		   } catch (Exception e) {
		       e.printStackTrace();
		   }
	}
	
	
}
