package wallets.db;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import KryoMod.StringListReg;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;
import wallets.mod.Acc_obj;

public class Retrie {
	
	//Retrieve Native Validator Address
	public static String retrieveNativeValidatorAddress()  {
		String Address =null;
			
       		EasyRam accDB = new EasyRam(db.NATIVE_VAL_ADDRESS);
			try {
	   			accDB.createStore("ValAddress", DataStore.Storage.PERSISTED,1);
	   			Address = accDB.getString("ValAddress", "ValidatorAddress");
	   		} catch (Exception e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
       		
           return Address;
		     
			 
		}
	
	///////////////////////////////////////////////////////////////////
	
	// Retrieve key
	public static PrivateKey retrievePrivateKeyWithPublicKey(PublicKey publicKey) {
		
	   		EasyRam accDB = new EasyRam(db.PUB_PRIV_PATH);
	   		PrivateKey key = null;
			try {
	   			accDB.createStore("keyGen", DataStore.Storage.PERSISTED,1);
	   			key = ((PrivateKey) accDB.getObject("keyGen", wallets.crypto.Hasher.returnPublicKeyString(publicKey)));
	   		} catch (Exception e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
	   		
	   		 return key;
	           
	}
	
	// Retrieve single coin Account
	public static Acc_obj retrieveAccData(String CoinAddress){
		Acc_obj acountData = null;
		EasyRam accDB = new EasyRam(db.COIN_ACCOUNTS);
		 try {
			accDB.createStore("AccountsDB", DataStore.Storage.PERSISTED,4);
			acountData = ((Acc_obj) accDB.getObject("AccountsDB", CoinAddress));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return acountData;
	}

	// Retrieve Bulk AccData
	public static List<Acc_obj> retrieveBulkAccData(List<String> CoinAddresses) {
		   int size;
			 if(CoinAddresses.size() > 10000) {
				size = 10000; 
			 }else {
				 size = CoinAddresses.size();
			 } 
			 List<Acc_obj> accounts = new CopyOnWriteArrayList<>();
			 
		EasyRam accDB = new EasyRam(db.COIN_ACCOUNTS);
		 try {
			accDB.createStore("AccountsDB", DataStore.Storage.PERSISTED,size);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Acc_obj acc = null;
		 for(String address: CoinAddresses) {
			acc = ((Acc_obj) accDB.getObject("AccountsDB", address));
			accounts.add(acc);
		 }
		 return accounts;
	}
	
	
	public static List<String> returnCoinAddresses() {
		 List<String> addresses = null;
		 try {
			 EasyRam accDB = new EasyRam(db.COIN_ADDRESSES_PATH);
			 accDB.createStore("Addresses", DataStore.Storage.PERSISTED,500);
			 
			 addresses = ((StringListReg) accDB.getObject("Addresses", "addresses")).getStringList();
	    	  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	        return addresses;
	  
	}
	
}
