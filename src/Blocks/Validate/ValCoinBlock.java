package Blocks.Validate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import Blocks.Validate.Util.Calc_Time_Diff;
import Blocks.Validate.Util.Tx_Val;
import Blocks.db.Block_db;
import Blocks.epoch.FormBlock;
import Blocks.mod.Block_obj;
import Blocks.mod.Epoch;
import Blocks.mod.PreviousBlockObj;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.store;
import Transc_Util.Merkle_tree;
import connect.Network;
import connect.Mod.Request;
import crypto.BytesToFro;
import crypto.HashUtil;
import db.db_store;
import penalty.Report;
import temp.Holder;
import temp.Static;
import threads.Accounts.AccountMapMem;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import transc.mod.Ptx;
import vault.Vault_obj;
import vault.db.vault_retrie;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class ValCoinBlock extends RecursiveTask<Boolean> {

	private static final long serialVersionUID = -4442668265632992298L;

	public static BigInteger ValidatorconfirmationTime,epoch,fallPosition,nextEpochProcessing;
	public static String epochBlockHah;
	static String EndTime;
	Block_obj block;
	
	public ValCoinBlock(Block_obj block) {
		this.block = block;
	}
	
	protected boolean validateNewCoinBlock() throws IOException, ClassNotFoundException {
		long start = System.currentTimeMillis();
	   
		 String merkelRootSum = block.getMerkle_root_hash();
		 String prevHash = block.getPrev_hash();
		 String blockHash = block.getHash();
		 long timestamp = block.getTimestamp();
		 String validatorAddr = block.getValidator_Addr();
		 String blocknum = block.getBlock_num();
		 String coinReward = block.getReward();
		 String stakeReward = block.getStakesReward();
		 boolean isFreeFall = block.getIsFreeFall();
		 String epochStatus = block.getEpochStatus();
		 List<Ctx> tx = block.getTransactionList();
		 List<Ptx> ptx = block.getPackTransactionList();
		 long valPosition = block.getValPosition();
		
			if(!blocknum.equals("0")) {
				PreviousBlockObj parentBlock = PreviousBlockObj.getLatestBlockData();
				
				
				//Check block timestamp validity
		      	if(!blocknum.equals("1") && !isFreeFall && epochStatus.equals(Static.EPOCH)) {
			      	if(!Calc_Time_Diff.ValidateTimestamp(block)) {
			      		Network.broadcastReport(Report.createCoinBlockReport(Static.VANITY_REQUEST,Static.EPOCH_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
					}
		      	}
				 
		      	 if(!blockHash.equals( BytesToFro.bytesToHex(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(blocknum + prevHash + coinReward + stakeReward + validatorAddr + String.valueOf(timestamp) + merkelRootSum + epochStatus + String.valueOf(isFreeFall) + String.valueOf(valPosition)))))))) {
			      		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
			      		return false;
			     }
		      	
				if(!prevHash.equals(parentBlock.getHash()) || blocknum != (parentBlock.getBlockNum().add(BigInteger.valueOf(1))).toString()) {
					Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.COIN_BLOCK_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
					 return false;
				}
				
				if(epochStatus.equals(Static.EPOCH)){
					
					if(!isFreeFall) {
			      		if(valPosition == 0 && Static.EPOCH_VALIDATOR_ADDRESS.equals(validatorAddr) && Tx_Val.checkEpochStakerPosition(null,valPosition,validatorAddr,isFreeFall)) {
			      			if(!blocknum.equals("1")) {
				      			Static.EPOCH_TIMESTAMP = block.getTimestamp();
				          		BigDecimal previousCompletedEpochTimestamp = new BigDecimal(Static.COMPLETED_EPOCH_ANCESTOR_BLOCK_TIMESTAMP);
				          		
			          		
			          			if((new BigDecimal(Static.EPOCH_TIMESTAMP).subtract(previousCompletedEpochTimestamp).compareTo(Static.ALLOWANCE_TIME_CONSTANT) > 0)){
				          			Network.broadcastReport(Report.createCoinBlockReport(Static.VANITY_REQUEST,Static.EPOCH_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
				          			
				          		}
			          		}
			          		
			          		if(Holder.CURRENT_FINAL_CONFIRMED_PROCESSED_STAKE_RESULTS.get(0).getFallPosition().compareTo(BigInteger.valueOf(0)) != 0) {
			          			Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
			          			return false;
			          		}
			          		
			          		
			          		StakeProcessor.updateValStartStakeTime(validatorAddr,timestamp);
			          		
			          		List<weightResults> prevEpochResults = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
			        		prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
			        			for(weightResults prevWeight : prevEpochResults) {
			        				if(prevWeight.getStakeobj().getAddress().equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
			        					ValidatorconfirmationTime = prevWeight.getValidatorConfirmationEpoch().add(BigInteger.valueOf(block.getTimestamp()));
			        					epoch = prevWeight.getEpoch();
			        					fallPosition = prevWeight.getFallPosition();
			        					nextEpochProcessing = prevWeight.getProcessingEpoch();
			        					break;
			        				}
			        			}
			        			
			        			Holder.epochBlock.put(block.getHash(),ValidatorconfirmationTime);
			        			Static.CUURENT_EPOCH_STAKE_BLOCK = Static.RECEIVED_STAKE_BLOCK;
			        			Static.RECEIVED_STAKE_BLOCK = null;
			        			
			        			Static.EPOCH_BLOCK_HASH = block.getHash();
			        			
			        			Static.CURRENT_EPOCH_START_TIME = String.valueOf(block.getTimestamp()); // Time epoch started
			        			Static.CURRENT_EPOCH_END_TIME = String.valueOf(epoch.add(BigInteger.valueOf(block.getTimestamp()))); // Time epoch ends
			        			Static.CURRENT_NEXT_STAKE_PROCESSING =  BigInteger.valueOf(timestamp).add(nextEpochProcessing);//next stake weighing
			        			Static.CURENT_EPOCH_HEIGHT = String.valueOf(Static.EPOCH_HEIGHT + 1); // current epoch height
			        			Static.CURRENT_VALIDATOR_FALL_POSITION = fallPosition;
			        			//Store epoch info
			        			Epoch epoc = new Epoch(Static.EPOCH_VALIDATOR_ADDRESS,Long.parseLong(Static.CURENT_EPOCH_HEIGHT),Static.CURRENT_VALIDATOR_FALL_POSITION.longValue(),Long.parseLong(Static.CURRENT_EPOCH_START_TIME),Long.parseLong(Static.CURRENT_EPOCH_END_TIME),Static.CURRENT_NEXT_STAKE_PROCESSING.longValue());
			        			store.storeEpochInfo(epoc);
			      		}else {
			      			Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
			      			return false;
			    		}
			      		
					}else {
			      		if(Static.EPOCH_VALIDATOR_ADDRESS.equals(validatorAddr) && Tx_Val.checkEpochStakerPosition(null,block.getValPosition(),validatorAddr,isFreeFall)) {
			      			if(!blocknum.equals("1")) {
				    			Static.PARENT_TIMESTAMP = parentBlock.getTimestamp();
				      			Static.EPOCH_TIMESTAMP = block.getTimestamp();
				      			
					      		BigDecimal allowedTimeAfterReport = new BigDecimal(12000);
					      		if((new BigDecimal(Static.EPOCH_TIMESTAMP).subtract(new BigDecimal(Static.PARENT_TIMESTAMP)).compareTo(allowedTimeAfterReport) > 0)){
					      			Network.broadcastReport(Report.createCoinBlockReport(Static.VANITY_REQUEST,Static.EPOCH_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
					      			
					      		}
			      			}
				      		
				      		StakeProcessor.updateValStartStakeTime(validatorAddr,timestamp);
				      		
				      		List<weightResults> prevEpochResults = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
			        		prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
			        			for(weightResults prevWeight : prevEpochResults) {
			        				if(prevWeight.getStakeobj().getAddress().equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
			        					ValidatorconfirmationTime = prevWeight.getValidatorConfirmationEpoch().add(BigInteger.valueOf(timestamp));
			        					epoch = prevWeight.getEpoch();
			        					fallPosition = prevWeight.getFallPosition();
			        					nextEpochProcessing = prevWeight.getProcessingEpoch();
			        					break;
			        				}
			        				
			        			}
			        			Holder.epochBlock.put(block.getHash(),ValidatorconfirmationTime);
			        		
			        			Static.EPOCH_BLOCK_HASH = block.getHash();
			        	
			        			Static.CURRENT_EPOCH_START_TIME = String.valueOf(block.getTimestamp()); // Time epoch started
			        			Static.CURRENT_EPOCH_END_TIME = String.valueOf(epoch.add(BigInteger.valueOf(block.getTimestamp()))); // Time epoch ends
			        			Static.CURRENT_NEXT_STAKE_PROCESSING =  BigInteger.valueOf(timestamp).add(nextEpochProcessing);//next stake weighing
			        			Static.CURENT_EPOCH_HEIGHT = String.valueOf(Static.EPOCH_HEIGHT + 1); // current epoch height
			        			Static.CURRENT_VALIDATOR_FALL_POSITION = fallPosition;
			        			//Store epoch info
			        			Epoch epoc = new Epoch(Static.EPOCH_VALIDATOR_ADDRESS,Long.parseLong(Static.CURENT_EPOCH_HEIGHT),Static.CURRENT_VALIDATOR_FALL_POSITION.longValue(),Long.parseLong(Static.CURRENT_EPOCH_START_TIME),Long.parseLong(Static.CURRENT_EPOCH_END_TIME),Static.CURRENT_NEXT_STAKE_PROCESSING.longValue());
			        			store.storeEpochInfo(epoc);
			        			
	        			}else {
			      			Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
			      			return false;
			      		}
					}
					
				}
								
			      	//check merkle root
			      	if(BytesToFro.convertByteArrayToString(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(Merkle_tree.getCtxMerkleRoot(tx) + Merkle_tree.getPtxMerkleRoot(ptx)))))) != merkelRootSum) {
			      		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.COIN_BLOCK_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
						return false;
			      	}

			      	if(validatorAddr.equals(Static.NATIVE_VALIDATOR_ADDRESS)) {
						Static.BLOCKS_VALIDATED_BY_NATIVE = Static.BLOCKS_VALIDATED_BY_NATIVE + 1;
					}
			      	
			      	//check ctx validity
			      	if(!Tx_Val.validateCtx(block)) {
						return false;
					}
			      	
			      	
					//Coin Reward and Stake Reward
			      	if(epochStatus.equals(Static.EPOCH)) {
			      		Static.CTX_REWARD = new BigDecimal(coinReward).subtract(new BigDecimal(Tx_Val.invalidctx)); 
						Static.STAKE_REWARD = new BigDecimal(stakeReward).subtract(new BigDecimal(Tx_Val.invalidstx)); 
			      	}else if(epochStatus.equals(Static.EPOCH_COMPLETE)){
						EpochEnd(validatorAddr,timestamp,block,tx,ptx);
			      	}else {
						Static.CTX_REWARD = Static.CTX_REWARD.add(new BigDecimal(coinReward)).subtract(new BigDecimal(Tx_Val.invalidctx));  
						Static.STAKE_REWARD = Static.STAKE_REWARD.add(new BigDecimal(stakeReward)).subtract(new BigDecimal(Tx_Val.invalidstx)); 
					}
			      	
			}else {
				if(!verifyGenTx(tx)) {
					System.out.println("Genesis Transaction verification Failed");
					return false;
				}else {
					
        			Static.CURRENT_EPOCH_START_TIME = String.valueOf(block.getTimestamp()); // Time epoch started
        			Static.CURRENT_EPOCH_END_TIME = String.valueOf(BigInteger.valueOf(15000).add(BigInteger.valueOf(block.getTimestamp()))); // Time epoch ends
        			Static.CURRENT_NEXT_STAKE_PROCESSING =  new BigInteger(Static.CURRENT_EPOCH_END_TIME).subtract((Static.PROCESSING_TIME_CONSTANT).toBigInteger());//next stake weighing
        			Static.CURENT_EPOCH_HEIGHT = "0"; // current epoch height
        			Static.CURRENT_VALIDATOR_FALL_POSITION = BigInteger.valueOf(0);
        			
        			Epoch epoc = new Epoch(Static.EPOCH_VALIDATOR_ADDRESS,Long.parseLong(Static.CURENT_EPOCH_HEIGHT),Static.CURRENT_VALIDATOR_FALL_POSITION.longValue(),Long.parseLong(Static.CURRENT_EPOCH_START_TIME),Long.parseLong(Static.CURRENT_EPOCH_END_TIME),Static.CURRENT_NEXT_STAKE_PROCESSING.longValue());
        			store.storeEpochInfo(epoc);
				}
			}
		
			PreviousBlockObj.storeLatestBlockData(new PreviousBlockObj(blockHash,prevHash,new BigInteger(blocknum),timestamp));
			
			 try {
				 AccountMapMem.newTxblocks.put(block);
			 }catch (InterruptedException e) {
					e.printStackTrace();
			 }
	      	
	      	Static.PREV_BLOCK_NUM = blocknum;
	      
	 		long end = System.currentTimeMillis();
	 		long finall = end - start;
	 		System.out.println("Validation Total time: " + finall + " milliseconds" + "\n");
			return true;
		
	} 
	

	//Epoch End
	public static void EpochEnd(String validatorAddr, long timestamp, Block_obj block,List<Ctx>tx,List<Ptx>ptx) throws IOException {
		Static.CTX_REWARD = Static.CTX_REWARD.add(new BigDecimal(block.getReward())).subtract(new BigDecimal(Tx_Val.invalidctx));  
		Static.STAKE_REWARD = Static.STAKE_REWARD.add(new BigDecimal(block.getStakesReward())).subtract(new BigDecimal(Tx_Val.invalidstx)); 
	
		Static.COMPLETED_EPOCH_ANCESTOR_BLOCK_TIMESTAMP = timestamp;
		long completedEpochTime = Static.COMPLETED_EPOCH_ANCESTOR_BLOCK_TIMESTAMP - Static.EPOCH_TIMESTAMP; 
		long expectedEpochTime = 0;
		if(validatorAddr.equals(Static.PREVIOUS_FINAL_CONFIRMED_PROCESSED_STAKE_RESULTS.get(0).getStakeobj().getAddress())) {
			expectedEpochTime = Static.PREVIOUS_FINAL_CONFIRMED_PROCESSED_STAKE_RESULTS.get(0).getEpoch().longValue();
		}else {
			List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(validatorAddr)) {
						expectedEpochTime = weightresults.getEpoch().longValue();
						break;
					}
	  			}
		}
		
		if((completedEpochTime > expectedEpochTime || completedEpochTime < expectedEpochTime - Static.ALLOWANCE_TIME_CONSTANT.longValue())) {
			Network.broadcastReport(Report.createCoinBlockReport(Static.VANITY_REQUEST,Static.EPOCH_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validatorAddr,block));
		}
	
		Static.TEMP_STAKE_BLOCK = Static.RECEIVED_STAKE_BLOCK;
		
		Block_db.storeLastestStakeHash(Static.TEMP_STAKE_BLOCK.getHash());
		Block_db.storeStakeBlockData(Static.TEMP_STAKE_BLOCK);
		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = validatorAddr;
		Static.EPOCH_HEIGHT = Static.EPOCH_HEIGHT + 1;
		
		BigDecimal coinRewardAfterPartition = Static.CTX_REWARD.multiply(new BigDecimal(0.5)).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal validatorTotalEpochReward = coinRewardAfterPartition.add(Static.STAKE_REWARD).setScale(2, RoundingMode.HALF_EVEN);
		
		
		db_store.storeValidatorRewards(validatorAddr,validatorTotalEpochReward);
		db_store.storeTop50Rewards( Static.CTX_REWARD.multiply(new BigDecimal(0.5)).setScale(2, RoundingMode.HALF_EVEN));
		//Store epoch info
		Epoch epoc = new Epoch(Static.EPOCH_VALIDATOR_ADDRESS,Long.parseLong(Static.EPOCH_HEIGHT),Static.CURRENT_VALIDATOR_FALL_POSITION.longValue(),Long.parseLong(Static.CURRENT_EPOCH_START_TIME),timestamp,Static.CURRENT_NEXT_STAKE_PROCESSING.longValue());
		store.storeEpochInfo(epoc);
		
		clearStakeTx();
		Holder.epochStakeTxs.clear();
		Holder.processedStakerAddr.clear();
		List<weightResults> results =Static.TEMP_STAKE_BLOCK.getProcessedStakes();
  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
  		Static.EPOCH_VALIDATOR_ADDRESS= results.get(0).getStakeobj().getAddress();
  		Static.EPOCH_CONFIRMATION_TIME = results.get(0).getValidatorConfirmationEpoch().longValue() + Static.EPOCH_TIMESTAMP;
  		
  		Static.PREVIOUS_VALIDATOR_CONFIRMATION_TIME = BigInteger.valueOf(Static.EPOCH_CONFIRMATION_TIME);
  		
  		if(Static.EPOCH_VALIDATOR_ADDRESS.equals(Static.NATIVE_VALIDATOR_ADDRESS)) {
				
  			try {
				 Request req = new Request(results.get(0),true);
				 FormBlock.startRequest.put(req);
		   	 } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
			 }
				Static.NUM_EPOCH_WON = Static.NUM_EPOCH_WON + 1;
		}
  		Holder.epochBlock.clear();
		
  		Static.NEXT_EPOCH_START_TIME = null;
  		Static.NEXT_EPOCH_END_TIME = null;
  		Static.NEXT_EPOCH_VALIDATOR_ADDRESS = null;
  		Static.NEXT_VALIDATOR_FALL_POSITION = null;
  		
		
	}

	
	private static boolean verifyGenTx(List<Ctx> tx) {
			// TODO Auto-generated method stub
	 	for(Ctx transaction : tx) {
	 		try {
	     		String FromAddress = BytesToFro.convertByteArrayToString(transaction.getFromAddress());
	          	String ToAddress = BytesToFro.convertByteArrayToString(transaction.getToAddress());
				BigDecimal value = BytesToFro.convertBytesToBigDecimal(transaction.getValue());
				long timestamp = BytesToFro.convertBytesToLong(transaction.getTimestamp());
		 		byte[] txSig = transaction.getTxSig();
		 		long TxNonce = BytesToFro.convertBytesToLong(transaction.getNonce());
		 		String range = BytesToFro.convertByteArrayToString(transaction.getRange());
		 		PublicKey publicKey;
		 		if(FromAddress.equals(Static.MAINTENANCE_VAULT)) {
		 			Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
	    			publicKey = vault.getPublicKey();
		 		}else {
		 			Acc_obj acc = Retrie.retrieveAccData(FromAddress);
	    			publicKey = acc.getPubkey();
		 		}
		 		
		 		if(!Hasher.verifiyCoinSignature(FromAddress,ToAddress,publicKey,value,TxNonce,timestamp,txSig,range)) {
		 			System.out.println("Verification Failed");
		 			return false;
	          	}
		 		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	 	}
	      return true;
	}

	public static void clearStakeTx() {
		Thread run = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Ctx tx :  Holder.epochStakeTxs) {
				   if( Holder.memStakeTrancHolder.contains(tx)) {
					   Holder.memStakeTrancHolder.remove(tx);  
				   }
				}
			}
		};run.start();
	}
	
    @Override
	protected Boolean compute() {
    	boolean result = false;  
		
			try {
				result = validateNewCoinBlock();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
    	return result;
	}

}
