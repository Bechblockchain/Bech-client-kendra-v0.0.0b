package SEWS_Protocol.db;

import java.util.ArrayList;
import java.util.List;

import Blocks.mod.Epoch;
import KryoMod.StakeObjListReg;
import SEWS_Protocol.StakeObj;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class store {

///// Store single Stake Data	
	public static void storeSingleStakeData(StakeObj stakeobj) {
		 
		 List<StakeObj> stakeObjList = null ;
		 stakeObjList =	retrieve.retrieveBulkStakeData();
		 if(stakeObjList == null) {
			 stakeObjList = new ArrayList<>();
		 }else {
			 stakeObjList.removeIf(o -> o.getAddress().equals(stakeobj.getAddress()));
		 }
	       	 
		 try {
			 stakeObjList.add(stakeobj);
			 StakeObjListReg stake = new StakeObjListReg(stakeObjList);
			 
			 EasyRam accDB = new EasyRam(db.ALL_STAKE_OBJECT_PATH);
			 accDB.createStore("Stakeobjs", DataStore.Storage.PERSISTED,50);
			 accDB.putObject("Stakeobjs","stakeobjs", stake);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	
	///// Store Epoch Data	
	public static void storeEpochInfo(Epoch epoch) {
		 
		try {
			 EasyRam accDB = new EasyRam(db.EPOCH_DATA_PATH);
			 accDB.createStore("EpochData", DataStore.Storage.PERSISTED,5);
			 accDB.putObject("EpochData",String.valueOf(epoch.getHeight()), epoch);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	
	
	///// Store Validator Epoch Data	
	public static void storeSingleEpochData(String EpochValidator, long Timestamp){
				 
		try {
			 EasyRam accDB = new EasyRam(db.EACH_EPOCH_VALIDATOR_DATA_PATH);
			 accDB.createStore("EpochVal", DataStore.Storage.PERSISTED,1);
			 accDB.putLong("EpochVal",EpochValidator, Timestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	

	// Store All Stake Data	
	public static void storeAllStakeObjs(List<StakeObj> stakeObjs) {
				 
		try {
			 StakeObjListReg stake = new StakeObjListReg(stakeObjs);
			 EasyRam accDB = new EasyRam(db.ALL_STAKE_OBJECT_PATH);
			 accDB.createStore("Stakeobjs", DataStore.Storage.PERSISTED,50);
			 accDB.putObject("Stakeobjs","stakeobjs", stake);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	
	
}
