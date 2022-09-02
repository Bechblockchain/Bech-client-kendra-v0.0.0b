package db;

import java.math.BigDecimal;

import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class db_store {

	public static void storeValidatorRewards(String validatorAddr, BigDecimal reward) {
		
		try {
			 EasyRam accDB = new EasyRam(db.VALIDATOR_REWARD);
			 accDB.createStore("ValReward", DataStore.Storage.PERSISTED,1);
			 accDB.putObject("ValReward",validatorAddr, reward);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

	
	public static void storeTop50Rewards(BigDecimal reward50) {
		
		try {
			 EasyRam accDB = new EasyRam(db.VALIDATOR_REWARD);
			 accDB.createStore("50Reward", DataStore.Storage.PERSISTED,1);
			 accDB.putObject("50Reward","50Reward", reward50);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public static void storeCoinTxIndex(String ctxSigHex, String Blocknum) {
		
		try {
			 EasyRam accDB = new EasyRam(db.CTX_INDEX);
			 accDB.createStore("CtxIndex", DataStore.Storage.PERSISTED,1);
			 accDB.putString("CtxIndex",ctxSigHex, Blocknum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
