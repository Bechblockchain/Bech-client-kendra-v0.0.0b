
package db;

import java.io.IOException;
import java.math.BigDecimal;

import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class db_retrie {

	public static BigDecimal getSingleValidatorReward(String toAddress) {
		
		BigDecimal reward = null;  	
   		EasyRam accDB = new EasyRam(db.VALIDATOR_REWARD);
		try {
   			accDB.createStore("ValReward", DataStore.Storage.PERSISTED,1);
   			reward = (BigDecimal) accDB.getObject("ValReward", toAddress);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return reward;
	}
	
	
	public static BigDecimal getTop50Reward() {
		
		BigDecimal reward = null;  	
   		EasyRam accDB = new EasyRam(db.VALIDATOR_REWARD);
		try {
   			accDB.createStore("50Reward", DataStore.Storage.PERSISTED,1);
   			reward = (BigDecimal) accDB.getObject("50Reward", "50Reward");
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return reward;
	}



	public static String getCoinTxIndex(String ctxSigHex) throws IOException {
      	String blockNum = null;  	
   		EasyRam accDB = new EasyRam(db.CTX_INDEX);
		try {
   			accDB.createStore("CtxIndex", DataStore.Storage.PERSISTED,1);
   			blockNum = accDB.getString("CtxIndex", ctxSigHex);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return blockNum;
	}
	
}
