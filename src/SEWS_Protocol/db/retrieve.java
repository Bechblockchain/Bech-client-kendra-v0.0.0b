package SEWS_Protocol.db;

import java.util.ArrayList;
import java.util.List;

import Blocks.mod.Epoch;
import KryoMod.StakeObjListReg;
import SEWS_Protocol.StakeObj;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class retrieve {

///// Retrieve single stake Data
	public static StakeObj retrieveSingleStakeData(String CoinAddress){
		List<StakeObj> stakeObjList = null;
		StakeObj stakeobj = null;
		
   		EasyRam accDB = new EasyRam(db.ALL_STAKE_OBJECT_PATH);
		try {
   			accDB.createStore("Stakeobjs", DataStore.Storage.PERSISTED,50);
   			StakeObjListReg obj = (StakeObjListReg) accDB.getObject("Stakeobjs", "stakeobjs");
   			if(obj != null) {
	      		stakeObjList = obj.getStakeList();
	      	}
	      	
	        if(stakeObjList != null) {
	        	for(int i = 0; i < stakeObjList.size(); i++) {
		      		if(stakeObjList.get(i).getAddress().equals(CoinAddress) ) {
		      			stakeobj = stakeObjList.get(i);
		      			break;
		      		}
		      	}
	        }
	        
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
        
        return stakeobj;
	}
	
	// Delete single stake Data
	public static void deleteSingleStakeData(String CoinAddress) {
		 
		List<StakeObj> stakeObjList = new ArrayList<>();
   		EasyRam accDB = new EasyRam(db.ALL_STAKE_OBJECT_PATH);
		try {
   			accDB.createStore("Stakeobjs", DataStore.Storage.PERSISTED,50);
   			stakeObjList = ((StakeObjListReg) accDB.getObject("Stakeobjs", "stakeobjs")).getStakeList();
   			stakeObjList.removeIf(o -> o.getAddress().equals(CoinAddress)); 
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
    	
    	store.storeAllStakeObjs(stakeObjList);
  	
	}
	
	
	// Retrieve All Stake data
	public static List<StakeObj> retrieveBulkStakeData() {
		 
      	List<StakeObj> list = new ArrayList<>();
   		EasyRam accDB = new EasyRam(db.ALL_STAKE_OBJECT_PATH);
		try {
   			accDB.createStore("Stakeobjs", DataStore.Storage.PERSISTED,50);
   			StakeObjListReg obj = (StakeObjListReg) accDB.getObject("Stakeobjs", "stakeobjs");
	      	if(obj != null) {
	      		list = obj.getStakeList();
	      	} 
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return list;
	   
	}
	
	// Retrieve single validator epoch Data
	public static long retrieveSingleEpochData(String validatorAddress) {
		 
      	long timestamp = 0;  	
   		EasyRam accDB = new EasyRam(db.EACH_EPOCH_VALIDATOR_DATA_PATH);
		try {
   			accDB.createStore("EpochVal", DataStore.Storage.PERSISTED,1);
   			timestamp = accDB.getLong("EpochVal", validatorAddress);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return timestamp;
   
	}
	
	//Retrieve epoch data
	public static Epoch retrieveEpochData(long height) {
	
      	Epoch epoch =  null;  	
   		EasyRam accDB = new EasyRam(db.EPOCH_DATA_PATH);
		try {
   			accDB.createStore("EpochData", DataStore.Storage.PERSISTED,5);
   			epoch = (Epoch) accDB.getObject("EpochData", String.valueOf(height));
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
		
        return epoch;
	   
	}
	
}
