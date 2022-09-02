package Blocks.Validate.Util.ReportVerification;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;

import Blocks.db.Block_db;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.retrieve;
import Transc_Util.Merkle_tree;
import crypto.BytesToFro;
import crypto.HashUtil;
import penalty.Report;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class VerifyStakeBlockPenalty {

	public static boolean verifyStakeBlockPenalty(Report report,StakeBlock block) throws IOException  {
		 List<Ctx> tx;
		 List<weightResults> weights;
		 String merkleRootProcessedStakes = block.getMerkleRootProcessedStakes();
		 String merkleRootStakeTx = block.getMerkleRootStakeTx();
		 tx = block.getEpochStakeTx();
		 weights = block.getProcessedStakes();
		 Random rand = new Random(); //instance of random class
	     int upperbound = weights.size();
	     int randomStake = rand.nextInt(upperbound); 
	     List<String> poolsnap = report.getStakeTxPoolSnap();
	     List<String> processedsnap = report.getProcessedStakeTxSnap();
	     String data = block.getHash() + block.getPrev_hash() + String.valueOf(block.getBlock_num()) + String.valueOf(block.getTimestamp()) + block.getMerkleRootProcessedStakes() + block.getMerkleRootStakeTx();
			
		 List<weightResults> nextValidatorsWeights = StakeProcessor.weighStakes(weights.get(randomStake).getTimestamp());
		 
		 Acc_obj accData = Retrie.retrieveAccData(report.getSuspectAddress());
			
		 PublicKey validatorPublicKey = accData.getPubkey();
		 
		 //Verify validator signature
		 if(!Hasher.verifiyValidatorSignature(validatorPublicKey,data,BytesToFro.hexStringToBytes(block.getValidatorSig()))) {
			 return false;
		 }
		 
		 //Validate block hash
		 if(!block.getHash().equals(BytesToFro.convertByteArrayToString(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(block.getPrev_hash() + String.valueOf(block.getBlock_num()) + String.valueOf(block.getTimestamp()) + block.getMerkleRootProcessedStakes() + block.getMerkleRootStakeTx() + String.valueOf(block.getEpochStakeTx()) + String.valueOf(block.getProcessedStakes())))))))) {
			 return false;
		 }
		 //Validate block number and parent hash
		 if(!block.getPrev_hash().equals(Block_db.getLatestStakeBlockHash()) || block.getBlock_num() != Block_db.getSingleStakeBlockData(block.getPrev_hash()).getBlock_num() + 1) {
			 return false;
		 }
		 //Validate merkleroot of stake Txs
		 if(!merkleRootStakeTx.equals(Merkle_tree.getCtxMerkleRoot(tx))) {
			 return false;
		 }
		 //Validate merkleroot of weight results of next epoch
		 if(!merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(weights)) || !merkleRootProcessedStakes.equals(Merkle_tree.getStxResultsMerkleRoot(nextValidatorsWeights))) {
			 return false;
		 }
		
		 //Check for availability of valid weighed stakes
		 nextValidatorsWeights.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
		 weights.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
		 if(!nextValidatorsWeights.equals(weights) || !report.getReporterResults().equals(nextValidatorsWeights)) {
			 return false;
		 }
		 
		 if(!checkStakeTxs(Static.EPOCH_VALIDATOR_ADDRESS,poolsnap,processedsnap)) {
			return false;
		 }
		 
		 
		 //Check if validator obeyed epoch block confirmation time
		  List<weightResults> results = Block_db.getSingleStakeBlockData(report.getEpochHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  		
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(report.getSuspectAddress()) && (weightresults.getFallPosition().compareTo(report.getStakerPos()) == 0)) {
						 
						long ValidatorconfirmationTime = weightresults.getValidatorConfirmationEpoch().longValue() +  Block_db.getSingleStakeBlockData(report.getEpochHash()).getTimestamp();;
						
				  		 if(block.getTimestamp() < ValidatorconfirmationTime || block.getTimestamp() > ValidatorconfirmationTime + weightresults.getEpochAllowance().longValue()) {
				  		 	 return false;
				 		 }
						 break;
					}
	  			}
		 if(block.getTimestamp() < retrieve.retrieveEpochData(Long.valueOf(Static.EPOCH_HEIGHT)).getEpochStart() + 780000) {
  			 return false;
 		 }
	      
			return true;
		
	} 

	public static boolean checkStakeTxs(String validatorAddr, List<String> poolsnap, List<String> processedsnap) {
		if(Static.NATIVE_VALIDATOR_ADDRESS != validatorAddr) {
			int stakeCount = 0;
			for(String stx : processedsnap) {
				if(poolsnap.contains(stx)) {
					stakeCount++;
				}
			}
			BigDecimal allProcessedStakes = new BigDecimal(processedsnap.size());
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
