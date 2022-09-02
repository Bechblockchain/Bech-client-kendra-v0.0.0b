package Blocks.Validate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Blocks.db.Block_db;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.weightResults;
import Transc_Util.Merkle_tree;
import connect.Network;
import crypto.BytesToFro;
import crypto.HashUtil;
import penalty.Report;
import temp.Holder;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class ValStakeBlock {

	public static BigInteger ValidatorcofirmationTime;
	public static String epochBlockHash;
	static String EndTime;
	
	
	public static boolean validateNewStakeBlock(StakeBlock block) throws IOException {
		epochBlockHash = Static.EPOCH_BLOCK_HASH;
		
		 String merkleRootProcessedStakes = block.getMerkleRootProcessedStakes();
		 String merkleRootStakeTx = block.getMerkleRootStakeTx();
		 List<Ctx> tx = block.getEpochStakeTx();
		 List<weightResults> weights = block.getProcessedStakes();
		 
		 Random rand = new Random(); //instance of random class
	      int upperbound = weights.size();
	      int randomStake = rand.nextInt(upperbound); 
	      
		 long time =  System.currentTimeMillis();
		 if(!block.getBlock_num().equals(String.valueOf(0))) {
		     if(time < Static.EPOCH_CONFIRMATION_TIME + 1000) {
		    	 try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		     }
		 }
	     
		 List<weightResults> nextValidatorsWeights = StakeProcessor.weighStakes(weights.get(randomStake).getTimestamp());
		 String epochCreated = block.getEpochCreated();
		 String epochCreatedfor = block.getEpochCreatedFor();
		 Acc_obj accData = Retrie.retrieveAccData(Static.EPOCH_VALIDATOR_ADDRESS);
		
		 PublicKey validatorPublicKey = accData.getPubkey();
			
		 String data = block.getHash() + block.getPrev_hash() + String.valueOf(block.getBlock_num()) + String.valueOf(block.getTimestamp()) + block.getMerkleRootProcessedStakes() + block.getMerkleRootStakeTx();
		
		 //Verify validator signature
		 if(!Hasher.verifiyValidatorSignature(validatorPublicKey,data,BytesToFro.hexStringToBytes(block.getValidatorSig()))) {
			 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
			 return false;
		 }
		 
		 //Validate block hash
		 if(!block.getHash().equals(BytesToFro.bytesToHex(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(epochCreated + epochCreatedfor + block.getPrev_hash() + String.valueOf(block.getBlock_num()) + String.valueOf(block.getTimestamp()) + block.getMerkleRootProcessedStakes() + block.getMerkleRootStakeTx()))))))) {
			Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
			 return false;
		 }
		 
		 //Checks for genesis stakeblock
		 if(!block.getBlock_num().equals(String.valueOf(0))) {
			 //Validate block number and parent hash
			 if(!block.getPrev_hash().equals(Block_db.getLatestStakeBlockHash()) || block.getBlock_num() != Block_db.getSingleStakeBlockData(block.getPrev_hash()).getBlock_num() + 1) {
				
				 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
				 return false;
			 }
		 }
		 //Validate merkleroot of stake Txs
		 if(!merkleRootStakeTx.equals(Merkle_tree.getCtxMerkleRoot(tx))) {
			 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
			 return false;
		 }
		 
		 //Validate merkleroot of weight results of next epoch
		 if(!merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(weights)) || !merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(nextValidatorsWeights))) {
			 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
			 return false;
		 }
		
		 //Validate merkleroot of weight results of next epoch
		 if(!merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(weights)) || !merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(nextValidatorsWeights))) {
			 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
			 return false;
		 }
		
		 if(!checkStakeTxs(Static.EPOCH_VALIDATOR_ADDRESS)) {
			 List<String> poolListSnapshot = new ArrayList<>();
  			 List<String> processedListSnapShot = new ArrayList<>();
  			 for(Ctx stx : Holder.memStakeTrancHolder) {
  				poolListSnapshot.add(BytesToFro.convertByteArrayToString(stx.getTxSig()));
  			 }
  			 for(Ctx stx : Holder.epochStakeTxs) {
  				processedListSnapShot.add(BytesToFro.convertByteArrayToString(stx.getTxSig()));
  			 }
			 Network.broadcastReport(Report.createStakeTxReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,poolListSnapshot,processedListSnapShot));
			 return false;
		 }
		 
		//Checks for genesis stakeblock
		 if(!block.getBlock_num().equals(String.valueOf(0))) {
			 
			 
			 List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
			 results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
		  		
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
						 
						long ValidatorconfirmationTime = weightresults.getValidatorConfirmationEpoch().longValue() + block.getTimestamp();
						
				  		 if(block.getTimestamp() < ValidatorconfirmationTime || block.getTimestamp() > ValidatorconfirmationTime + weightresults.getEpochAllowance().longValue()) {
				  			Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
							return false;
				 		 }
						 break;
					}
	  			}
			 
			 
		     //Check block timestamp validity
	  		 if((block.getTimestamp() < Long.valueOf(Static.CURRENT_EPOCH_START_TIME + 780000))) {
	  			 Network.broadcastReport(Report.createStakeBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,Static.EPOCH_VALIDATOR_ADDRESS,nextValidatorsWeights,epochBlockHash,block));
	 			 return false;
	 		 }
  		 }
	    
  		Static.NEXT_EPOCH_START_TIME = Static.CURRENT_EPOCH_END_TIME;
  		Static.NEXT_EPOCH_END_TIME = String.valueOf(new BigInteger(Static.CURRENT_EPOCH_END_TIME).add(weights.get(0).getEpoch()));
  		Static.NEXT_EPOCH_VALIDATOR_ADDRESS = weights.get(0).getStakeobj().getAddress();
  		Static.NEXT_VALIDATOR_FALL_POSITION = BigInteger.valueOf(0);
  		
  		Static.RECEIVED_STAKE_BLOCK = block;
  		Static.PREV_STAKE_BLOCK_NUM = block.getBlock_num();
  		
			return true;
		
	} 
	
	
	public static boolean checkStakeTxs(String validatorAddr) {
		if(Static.NATIVE_VALIDATOR_ADDRESS != validatorAddr) {
			int stakeCount = 0;
			for(Ctx ctx : Holder.epochStakeTxs) {
				if(Holder.memStakeTrancHolder.contains(ctx)) {
					stakeCount++;
				}
			}
			BigDecimal allProcessedStakes = new BigDecimal(Holder.epochStakeTxs.size());
			BigDecimal percentage = new BigDecimal(stakeCount).divide(allProcessedStakes,2,RoundingMode.HALF_EVEN).multiply(new BigDecimal(100));
			if(percentage.compareTo(Static.MIN_STAKE_TX_POOL_RATIO) >= 0) {
				return true;
			}else {
				return false;
			}
			
		}else {
			return true;
		}
		
	}
	
}
