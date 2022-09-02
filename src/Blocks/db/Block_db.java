package Blocks.db;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import Blocks.mod.Block_obj;
import Blocks.mod.StakeBlock;
import db.paths.db;
import temp.Static;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class Block_db {

	
	public static void storeBlockData(Block_obj block) {
		long blockNum = Long.parseLong(block.getBlock_num());
		String folderNumber = null;
	
		 if(blockNum % 2 == 0){ 
			 folderNumber = String.valueOf(blockNum/Static.MAX_BLOCKS_FILES);
		 }else {
			 BigDecimal bigDecimal = new BigDecimal(String.valueOf(blockNum/Static.MAX_BLOCKS_FILES));
			 folderNumber= String.valueOf(bigDecimal.intValue());
		 }
			 
		 try {
             //Store block data
	         EasyRam accDB = new EasyRam(db.BLOCK_DATA_PATH + folderNumber);
			 accDB.createStore("BlocksDB", DataStore.Storage.PERSISTED,5);
			 accDB.putObject("BlocksDB", block.getHash(), block);
			 
			 //Store block metadata
			 String blockNumm = block.getBlock_num();
	    	 String blockhash = block.getHash();
			 
			 EasyRam DB = new EasyRam(db.BLOCK_HASH_NUM_PATH);
			 DB.createStore("BlocksMeta", DataStore.Storage.PERSISTED,1);
			 DB.putString("BlocksMeta", blockNumm, blockhash);
			 DB.putString("BlocksMeta", blockhash, blockNumm);
         
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
		
	 }
	
	public static Block_obj getSingleBlockData(String hash) {
		String folderNumber = null;
		Block_obj block = null;
         //Get blocks
		try {
			
			//get block num from db
			EasyRam blockDB = new EasyRam(db.BLOCK_HASH_NUM_PATH);
			blockDB.createStore("BlocksMeta", DataStore.Storage.PERSISTED,1);
			long BlockNum = Long.parseLong(blockDB.getString("BlocksMeta", hash));
			
			//get folder number of block
			 if(BlockNum % 2 == 0){ 
				 folderNumber = String.valueOf(BlockNum/Static.MAX_BLOCKS_FILES);
			 }else {
				 BigDecimal bigDecimal = new BigDecimal(String.valueOf(BlockNum/Static.MAX_BLOCKS_FILES));
				 folderNumber= String.valueOf(bigDecimal.intValue());
			 }
			
			 //get block
			EasyRam DB = new EasyRam(db.BLOCK_DATA_PATH + folderNumber);
			DB.createStore("BlocksDB", DataStore.Storage.PERSISTED,5);
			block = (Block_obj) DB.getObject("BlocksDB", hash);
			
          
        } catch (Exception e) {
			e.printStackTrace();
	    }
		
		return block;
	}
	
	
	public static Block_obj getSingleNumBlockData(String num) {
		
		String folderNumber = null;
		Block_obj block = null;
		long BlockNum = Long.parseLong(num);
		 if(BlockNum % 2 == 0){ 
			 folderNumber = String.valueOf(BlockNum/Static.MAX_BLOCKS_FILES);
		 }else {
			 BigDecimal bigDecimal = new BigDecimal(String.valueOf(BlockNum/Static.MAX_BLOCKS_FILES));
			 folderNumber= String.valueOf(bigDecimal.intValue());
		 }
		
		try {
			EasyRam blockDB = new EasyRam(db.BLOCK_HASH_NUM_PATH);
			blockDB.createStore("BlocksMeta", DataStore.Storage.PERSISTED,1);
			String hash = blockDB.getString("BlocksMeta", num);
	     	
			EasyRam DB = new EasyRam(db.BLOCK_DATA_PATH + folderNumber);
			DB.createStore("BlocksDB", DataStore.Storage.PERSISTED,5);
			block = (Block_obj) DB.getObject("BlocksDB", hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return block;
	}

	public static void storeStakeBlockData(StakeBlock block)  {
		 
		 try {
	         //Store block data
	         EasyRam accDB = new EasyRam(db.STAKE_BLOCK_DATA_PATH );
			 accDB.createStore("StakeBlocksDB", DataStore.Storage.PERSISTED,10);
			 accDB.putObject("StakeBlocksDB", block.getHash(), block);
			 
			 //Store block metadata
			 String blockNumm = block.getBlock_num();
	    	 String blockhash = block.getHash();
			 
			 EasyRam DB = new EasyRam(db.STAKE_BLOCK_HASH_NUM_PATH);
			 DB.createStore("StakeBlocksMeta", DataStore.Storage.PERSISTED,1);
			 DB.putString("StakeBlocksMeta", blockNumm, blockhash);
			 DB.putString("StakeBlocksMeta", blockhash, blockNumm);
			 
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	 }
		
	public static StakeBlock getSingleStakeBlockData(String Hash) {
		StakeBlock block= null;
		try {
			EasyRam DB = new EasyRam(db.STAKE_BLOCK_DATA_PATH);
			DB.createStore("StakeBlocksDB", DataStore.Storage.PERSISTED,10);
			block = (StakeBlock) DB.getObject("StakeBlocksDB", Hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return block;
	}
	
	public static StakeBlock getSingleStakeNumBlockData(String num)  {
		 
		StakeBlock block= null;
			try {
				EasyRam blockDB = new EasyRam(db.STAKE_BLOCK_HASH_NUM_PATH);
				blockDB.createStore("StakeBlocksMeta", DataStore.Storage.PERSISTED,1);
				String hash = blockDB.getString("StakeBlocksMeta", num);
		     	
				EasyRam DB = new EasyRam(db.STAKE_BLOCK_DATA_PATH);
				DB.createStore("StakeBlocksDB", DataStore.Storage.PERSISTED,10);
				block = (StakeBlock) DB.getObject("StakeBlocksDB", hash);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return block;
	}
		
	public static void storeLastestStakeHash(String Hash) throws IOException {
		 
		 try {
             EasyRam DB = new EasyRam(db.LATEST_STAKE_HASH_DATA_PATH);
			 DB.createStore("LatestStakeBlockHash", DataStore.Storage.PERSISTED,1);
			 DB.putString("LatestStakeBlockHash", "Stake_Block_hash", Hash);
             
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	 }
	
	public static String getLatestStakeBlockHash()  {
		String hash = null;
     	try {
	     	EasyRam DB = new EasyRam(db.LATEST_STAKE_HASH_DATA_PATH);
			DB.createStore("LatestStakeBlockHash", DataStore.Storage.PERSISTED,1);
			hash = DB.getString("LatestStakeBlockHash", "Stake_Block_hash");
		} catch (Exception e) {
			e.printStackTrace();
		}
         return hash;
	}
	
	public static List<Block_obj> getBlockDataList(String recievedBlockNum) {
		long nextBlockNum = Long.parseLong(recievedBlockNum);
		List<Block_obj> blocksList = new ArrayList<>();
		
        	do {
        		nextBlockNum++;
        		String folderNumber = null;
        		
        		 if(nextBlockNum % 2 == 0){ 
        			 folderNumber = String.valueOf(nextBlockNum/Static.MAX_BLOCKS_FILES);
        		 }else {
        			 BigDecimal bigDecimal = new BigDecimal(String.valueOf(nextBlockNum/Static.MAX_BLOCKS_FILES));
        			 folderNumber= String.valueOf(bigDecimal.intValue());
        		 }
        		
    		 	try {
    		 		//Get metadata
    		     	EasyRam DB = new EasyRam(db.BLOCK_HASH_NUM_PATH);
    				DB.createStore("BlocksMeta", DataStore.Storage.PERSISTED,50);
    				String nexthash = DB.getString("BlocksMeta", String.valueOf(nextBlockNum));
    				
    				//Get block
    				EasyRam blockDB = new EasyRam(db.BLOCK_DATA_PATH + folderNumber);
    				blockDB.createStore("BlocksDB", DataStore.Storage.PERSISTED,50);
    				Block_obj block = (Block_obj) blockDB.getObject("BlocksDB", nexthash);
    				 
    				 if(!block.equals(null)) {
                		 blocksList.add(block);
                	 }else{
                		 break;
                	 }
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
        		 
        	}while(blocksList.size() != Static.MAX_BLOCKS_REQUEST);
        	
		return blocksList;
	 }
	
	
	
}
