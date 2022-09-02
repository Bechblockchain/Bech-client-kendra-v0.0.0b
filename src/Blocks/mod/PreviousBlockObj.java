package Blocks.mod;

import java.io.Serializable;
import java.math.BigInteger;

import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class PreviousBlockObj implements Serializable{

	String hash;
	String prevhash;
	BigInteger blocknum;
	long timestamp;
	
	public PreviousBlockObj(String hash, String prevhash, BigInteger blocknum, long timestamp) {
		this.hash = hash;
		this.prevhash = prevhash;
		this.blocknum = blocknum;
		this.timestamp = timestamp;
	}
	
	public String getHash() {
		return hash;
	}
	
	public String getPrevHash() {
		return hash;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public BigInteger getBlockNum() {
		return blocknum;
	}

	public static PreviousBlockObj getLatestBlockData() {
		 
     	PreviousBlockObj block = null;
     	
     	EasyRam accDB = new EasyRam(db.LATEST_BLOCK_DATA_PATH);
		try {
   			accDB.createStore("LatestBlockData", DataStore.Storage.PERSISTED,1);
   			block = (PreviousBlockObj) accDB.getObject("LatestBlockData", "latestCoinBlockData");
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return block;
	}
		

	public static void storeLatestBlockData(PreviousBlockObj obj) {
		 try {
			 EasyRam accDB = new EasyRam(db.LATEST_BLOCK_DATA_PATH);
			 accDB.createStore("LatestBlockData", DataStore.Storage.PERSISTED,1);
			 accDB.putObject("LatestBlockData","latestCoinBlockData", obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }

	
	
}
