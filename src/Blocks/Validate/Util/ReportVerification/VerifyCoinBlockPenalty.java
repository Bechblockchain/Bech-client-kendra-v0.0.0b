package Blocks.Validate.Util.ReportVerification;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import Blocks.Validate.Util.Calc_Time_Diff;
import Blocks.Validate.Util.Check50;
import Blocks.Validate.Util.Tx_Val;
import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Blocks.mod.PreviousBlockObj;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.retrieve;
import Transc_Util.Merkle_tree;
import crypto.BytesToFro;
import crypto.HashUtil;
import penalty.Report;
import temp.Holder;
import temp.Static;
import transc.mod.Ctx;
import transc.mod.Ptx;

public class VerifyCoinBlockPenalty {

	public static boolean verifyCoinBlockPenalty(Report report,Block_obj block) throws IOException  {
		
		 String merkelRoot = block.getMerkle_root_hash();
		 List<Ctx> tx = block.getTransactionList();
		 List<Ptx> ptx = block.getPackTransactionList();
		
		//Ensures number of stake transactions is not more than 1 per block
		int occurrences = 0;
		
		List<Ctx>ctxlist = Holder.myReports.get(1).getCoinBlock().getTransactionList();
		
		for(Ctx ctx : ctxlist) {
			if(BytesToFro.convertByteArrayToString(ctx.getRange()).equals(Static.TYPE_STAKE) || BytesToFro.convertByteArrayToString(ctx.getRange()).equals(Static.TYPE_UNSTAKE) ) {
				occurrences++;
			}
			if(occurrences > 1) {
				return false;
			}
		}
		
		 if(!block.getHash().equals( BytesToFro.convertByteArrayToString(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(String.valueOf(block.getBlock_num()) + block.getPrev_hash() + String.valueOf(block.getReward()) + String.valueOf(block.getStakesReward()) + block.getValidator_Addr() + block.getTimestamp() + block.getMerkle_root_hash() + tx + ptx + block.getEpochStatus() + block.getIsFreeFall() + block.getValPosition()))))))) {
	      	 return false;
	     }
		 
		 if(!block.getPrev_hash().equals(Block_db.getSingleBlockData(block.getPrev_hash()).getHash()) || block.getBlock_num() != Block_db.getSingleBlockData(block.getPrev_hash()).getBlock_num() + 1) {
			 return false;
		 }
		 
			if(block.getEpochStatus().equals(Static.EPOCH) ) {
				if(!block.getIsFreeFall()) {
					
					if(block.getValPosition() == 0 && Tx_Val.checkEpochStakerPosition(report,block.getValPosition(),block.getValidator_Addr(),false)) {
		      			
			      		BigDecimal previousCompletedEpochTimestamp = new BigDecimal(block.getTimestamp());
			      		if((new BigDecimal(block.getTimestamp()).subtract(previousCompletedEpochTimestamp).compareTo(Static.ALLOWANCE_TIME_CONSTANT) > 0)){
			      			return false;
			      		}
			      		
			      		if(Tx_Val.checkEpochStakerPosition(report,block.getValPosition(),block.getValidator_Addr(),false)) {
			      			return false;
			      		}
			      		BigDecimal allReward = db.db_retrie.getTop50Reward();
			      		int counter = 0;int staker50counter = 0;
			      		for(int t=1; t < tx.size(); t++) {
			             	Ctx Tranc = tx.get(t);
			             	if(BytesToFro.convertByteArrayToString(Tranc.getRange()).equals(Static.REWARD_RANGE)) {
			             		counter++;
			             	}
			             	
			             	if(BytesToFro.convertByteArrayToString(Tranc.getFromAddress()).equals("reward_50_tx")) {
		                 		staker50counter++;
		                 		if(Check50.checkEachstaker50Partition(allReward).compareTo(BytesToFro.convertBytesToBigDecimal(tx.get(t).getEachStaker50Partition())) != 0) {
			                 		return false;
			                 	}
		                 	}
		                 	
		                 	if(counter != 1) {
		                 		return false;
		                 	}
		                 	
		                 	if(staker50counter != Check50.checkstaker50Num()) {
		                 		return false;
		                 	}
		                 	
			             	
			             }
			      		
		      		}
					
				}else {
					if(Tx_Val.checkEpochStakerPosition(report,block.getValPosition(),block.getValidator_Addr(),false)) {
						PreviousBlockObj parentBlock = PreviousBlockObj.getLatestBlockData();
		    			
			      		BigDecimal allowedTimeAfterReport = new BigDecimal(12000);
			      		if((new BigDecimal(block.getTimestamp()).subtract(new BigDecimal(parentBlock.getTimestamp())).compareTo(allowedTimeAfterReport)) > 0){
			      			return false;
			      		}
			      		
			      		int counter = 0;
			      		for(int t=1; t < tx.size(); t++) {
			             	Ctx Tranc = tx.get(t);
			             	if(BytesToFro.convertByteArrayToString(Tranc.getRange()).equals(Static.PENALTY_RANGE)) {
			             		counter++;
			             	}
			             	if(counter <= 0 || counter > 2) {
			             		return false;
			             	}
			             }
			      		
			      		
		      		}else {
		      			return false;
		      		}
				}
			}else if(block.getEpochStatus().equals(Static.EPOCH_COMPLETE)) {
				   long expectedEpochTime = 0;
				   long EPOCH_TIMESTAMP = 0;
				   List<weightResults> results = Block_db.getSingleStakeBlockData(report.getEpochHash()).getProcessedStakes();
			  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
			  		
			  			for(weightResults weightresults: results) {
							if(weightresults.getStakeobj().getAddress().equals(block.getValidator_Addr()) && (weightresults.getFallPosition().compareTo(report.getStakerPos()) == 0)) {
								 expectedEpochTime = weightresults.getEpoch().longValue();
								 EPOCH_TIMESTAMP = retrieve.retrieveSingleEpochData(block.getValidator_Addr());
								 break;
							}
			  			}
				   
			  			long completedEpochTime = block.getTimestamp() - EPOCH_TIMESTAMP; 
					
						if(!(completedEpochTime > expectedEpochTime || completedEpochTime < expectedEpochTime - Static.ALLOWANCE_TIME_CONSTANT.longValue())) {
						return false;	
						}
						
				}
	      	
	      	//Check block timestamp validity
	      	if(!block.getIsFreeFall() || block.getEpochStatus().equals(Static.EPOCH_ULTIMO_GRADU)) {
	      		if(!Calc_Time_Diff.ValidateTimestamp(block)) {
		      		return false;
		      	}
	      	}
	      	
			
	      	//check ctx merkle root
	      	if(Merkle_tree.getCtxMerkleRoot(tx) != merkelRoot) {
				return false;
	      	}

		return true;
	}
	
}
