package Blocks.Validate.Util;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

import Blocks.Validate.ValCoinBlock;
import Blocks.Validate.Util.ReportVerification.VerifyPenalty;
import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.retrieve;
import Util.OrderDiff;
import connect.Network;
import crypto.BytesToFro;
import db.db_retrie;
import penalty.Report;
import penalty.thePunisher;
import penalty.thePunisherAcc;
import temp.Holder;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import vault.BackUpVault;
import vault.Vault_obj;
import vault.db.vault_retrie;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class Tx_Val {
	
	public static int invalidctx;
	public static int invalidstx;
	
	public static Boolean validateCtx (Block_obj block) throws IOException, ClassNotFoundException  {
	
		Ctx transaction;
        List<Ctx> tx;
        tx = block.getTransactionList();

        //Account Data
        String coinAddress = null;
    	long nonce = 0;
    	PublicKey pubkey = null;

    	//Transaction Data
    	String FromAddress;
  		String ToAddress;
      	BigDecimal value,coinRewardAfterPartition = null,stakeRewardAfterPartition = null,vaultRatio = null;
      	long timestamp;
      	byte[] txSig;
      	long TxNonce;
      	String range;
      	String validator = block.getValidator_Addr();
    	BigDecimal totalEpochReward = null;
       
     	if(tx.isEmpty()) {
     		return true;
     	}else {
     		int occurrences = 0;
     		 for(int i=1; i < tx.size(); i++) {
             	transaction = tx.get(i);
             	//Transaction data in primitive type
             	 FromAddress = BytesToFro.convertByteArrayToString(transaction.getFromAddress());
         		 ToAddress = BytesToFro.convertByteArrayToString(transaction.getToAddress());
         		 value = BytesToFro.convertBytesToBigDecimal(transaction.getValue());
         		 timestamp = BytesToFro.convertBytesToLong(transaction.getTimestamp());
         		 txSig = transaction.getTxSig();
         		 TxNonce = BytesToFro.convertBytesToLong(transaction.getNonce());
         		 range = BytesToFro.convertByteArrayToString(transaction.getRange());
             	
         		 if(!range.equals(Static.REWARD_RANGE) && !FromAddress.equals(Static.MAINTENANCE_VAULT) && !FromAddress.equals(Static.BACKUP_VAULT)) {
        			 Acc_obj accData = Retrie.retrieveAccData(FromAddress);
                  	if(accData.equals(null)) {
                      	continue;
                  	}else {
                  		coinAddress = accData.getCoinAddress();
                      	nonce = accData.getCtxNonce();
                      	pubkey = accData.getPubkey();
                  	}
     			 }else {
     				 if(FromAddress.equals(Static.MAINTENANCE_VAULT)){
     					Vault_obj maintenanceVault = vault_retrie.retrieveMaintenanceVaultData();
     					coinAddress = Static.MAINTENANCE_VAULT;
                      	nonce = maintenanceVault.getNonce();
                      	pubkey = maintenanceVault.getPublicKey();
                      	
     				 }else if(FromAddress.equals(Static.BACKUP_VAULT) ) {
     					BackUpVault backupVault = vault_retrie.retrieveBackUpVaultData();
     					coinAddress = Static.BACKUP_VAULT;
                      	nonce = backupVault.getNonce();
                      	pubkey = backupVault.getPublicKey();
     				 }
     			
     			 }
         		 
         		 
         		 if(range.equals(Static.TYPE_STAKE) || range.equals(Static.TYPE_UNSTAKE) ) {
         			//Ensures number of stake transactions is not more than 1 per block
         			occurrences++;
	     			if(occurrences > 1) {
	     				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	     				Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
	     				return false;
	             	}
         			 
         			if(timestamp >= ValCoinBlock.ValidatorconfirmationTime.longValue()) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
         				Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
         				return false;
         			}
         			
         			 StakeObj stakeObj = retrieve.retrieveSingleStakeData(FromAddress);
     				 if(stakeObj.equals(null)) {
     					Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
     					return false;
     				 }
         			 
         			//Checks for banned validators
            		 if(Holder.gehennaList.contains(FromAddress)){
            			 Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
            			 Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
             				return false;
            		 }
         			
         			 
         			if(!ToAddress.equals(Static.STAKE_OBJ)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
            			 return false;
         			}
         			if(Holder.processedStakerAddr.contains(Static.EPOCH_VALIDATOR_ADDRESS)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
         				Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
         				return false;
         			}
         			
         			
         		 }else if(range.equals(Static.OBSOLETE_STAKE_CLEARANCE)){
	     			StakeObj stakeobj = retrieve.retrieveSingleStakeData(ToAddress);
	     			
	     			long stakeTimestampDiff = timestamp - stakeobj.getTimestamp();
	     			if(!(stakeobj.getStakeCoins().compareTo(Static.MIN_STAKE_VALUE) < 0) || !(stakeTimestampDiff > Static.STAKE_OBJ_OBSOLETE_TIME_THRESHOLD) || !(Holder.gehennaList.contains(stakeobj.getAddress()))) {
	     				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.STAKE_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	     				Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
	     				return false;
	     			}
	     			
		     	 }else if(range.equals(Static.REWARD_RANGE)) {
 	     			 totalEpochReward = BytesToFro.convertBytesToBigDecimal(transaction.getValue());
 	     			 coinRewardAfterPartition = BytesToFro.convertBytesToBigDecimal(transaction.getCoinRewardAfterPartition());
 	        		 stakeRewardAfterPartition =  BytesToFro.convertBytesToBigDecimal(transaction.getStakeRewardAfterPartition());
 	        		 vaultRatio = BytesToFro.convertBytesToBigDecimal(transaction.getVaultRatio());
 	        		 
 	        		if(!block.getEpochStatus().equals(Static.EPOCH_COMPLETE)) {
         				Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
       				    return false;
         			}
         			
         			if( db_retrie.getSingleValidatorReward(ToAddress).compareTo(totalEpochReward) != 0) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
       				    return false;
         			}
         			
         			
        			 if(!ToAddress.equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS)) {
        				 Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
        				 return false;
        			 }
        			 if(!FromAddress.equals("reward_tx") || !FromAddress.equals("reward_50_tx")) {
        				 Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
         				 return false;
         			 }
        			
        			 if(TxNonce != 0) {      	
	            		 Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.CTX_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	            		 Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
		              		return false;
		             }
        			 
        			 if(!verifiyCTxSignature(coinAddress,ToAddress,pubkey,totalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,vaultRatio,TxNonce,timestamp,txSig,BytesToFro.convertBytesToLong(transaction.getEpochNumber()),BytesToFro.convertByteArrayToString(transaction.getEpochHash()),range)) {
          				Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
        				    return false;
      				 }
        			 
        			 	BigDecimal allReward = db.db_retrie.getTop50Reward();
		          		int counter = 0; int staker50counter = 0;
		          		for(int t=1; t < tx.size(); t++) {
		                 	Ctx Tranc = tx.get(t);
		                 	if(BytesToFro.convertByteArrayToString(Tranc.getFromAddress()).equals("reward_tx")) {
		                 		counter++;
		                 	}else if(BytesToFro.convertByteArrayToString(Tranc.getFromAddress()).equals("reward_50_tx")) {
		                 		staker50counter++;
		                 		if(Check50.checkEachstaker50Partition(allReward).compareTo(BytesToFro.convertBytesToBigDecimal(tx.get(t).getEachStaker50Partition())) != 0) {
			                 		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
			                 		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
			                 		return false;
			                 	}
		                 	}
		                 	
		                 	if(counter != 1 || staker50counter != Check50.checkstaker50Num()) {
		                 		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
		                 		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
		                 		return false;
		                 	}
		                 	
		                 	
		                 }
        			 
 	     		 }else if(range.equals(Static.PENALTY_RANGE)) {
 	     			Report report = BytesToFro.convertByteArrayToReport(transaction.getReport());
	
         			if(!verifiyCTxSignature(coinAddress,ToAddress,pubkey,value,TxNonce,timestamp,txSig,report,range)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.CTX_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
                  		return false;
                  	}
         			if(!VerifyPenalty.verifyPenaltyDecision(report)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.REWARD_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
       				    return false;
         			}
         			
         			if(!ToAddress.equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS) || !FromAddress.equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
        				 return false;
        			 }
         			
         			if(TxNonce < nonce) {      	
	     				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.CTX_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	     				Network.broadcastReport(Report.createCtxTxReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	              		return false;
	              	}
         			
         			int counter = 0;
		      		for(int t=1; t < tx.size(); t++) {
		             	Ctx Tranc = tx.get(t);
		             	if(BytesToFro.convertByteArrayToString(Tranc.getRange()).equals(Static.PENALTY_RANGE)) {
		             		counter++;
		             	}
		             	if(counter <= 0 || counter > 2) {
		             		Network.broadcastReport(Report.createCoinBlockReport(Static.GEHENNA_REQUEST,Static.VALIDATOR_DISHONESTY,Static.NATIVE_VALIDATOR_ADDRESS,validator,block));
			      			return false;
		             	}
		             }
		      		
         			
         			thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(ToAddress);
         			nonce = punishmentData.getPenaltyNonce();
         			
         			
         		 }else {
         			 
         			if(!verifiyCTxSignature(coinAddress,ToAddress,pubkey,value,TxNonce,timestamp,txSig,range)) {
         				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.CTX_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
                  		return false;
                  	}
         			
         			if(TxNonce < nonce) {      	
	     				Network.broadcastReport(Report.createCtxTxReport(Static.KORUST_REQUEST,Static.CTX_TAMPERING,Static.NATIVE_VALIDATOR_ADDRESS,validator,transaction));
	              		return false;
	              	}
         		 }
              
             }
     		
     	}
        return true;
    }
		

	public static boolean verifiyCTxSignature(String FromAddress, String ToAddress, PublicKey Pubkey, BigDecimal value, long TxNonce, long timestamp, byte[] txSig, String range) {
		if(Hasher.verifiyCoinSignature ( FromAddress, ToAddress, Pubkey, value, TxNonce, timestamp, txSig, range)) {
			return true;
		}
		return false;
	} 
	
	public static boolean verifiyCTxSignature(String FromAddress, String ToAddress, PublicKey Pubkey, BigDecimal value, long TxNonce, long timestamp, byte[] txSig, Report report, String range) {
		if(Hasher.verifiyCoinSignature ( FromAddress, ToAddress, Pubkey, value, TxNonce, timestamp, txSig, report, range)) {
			return true;
		}
		return false;
	}
	
	public static boolean verifiyCTxSignature(String FromAddress, String ToAddress, PublicKey Pubkey,BigDecimal totalEpochReward,BigDecimal coinRewardAfterPartition,BigDecimal stakeRewardAfterPartition, BigDecimal vaultRatio, long TxNonce, long timestamp, byte[] txSig, long epochNumber, String epochHash, String range) {
		if(Hasher.verifiyCoinSignature ( FromAddress, ToAddress, Pubkey, totalEpochReward, coinRewardAfterPartition, stakeRewardAfterPartition,vaultRatio, TxNonce, timestamp, txSig, epochNumber, epochHash, range)) {
			return true;
		}
		return false;
	}
	
	public static boolean checkEpochStakerPosition(Report report, long Position,String address,boolean isFreefall){
		if(report.equals(null)) {
			List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  		
	  		if(isFreefall) {
	  			
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(address)) {
						if(results.get(report.getStakerPos().intValue()).getStakeobj().getAddress() != address && Position != report.getStakerPos().intValue()) {
							return false;
							
						}
					}
	  			}
	  			
	  		}else {
	  			if(results.get(0).getStakeobj().getAddress() != address && Position != report.getStakerPos().intValue()) {
					return false;
				}
	  		}
		}else {
			List<weightResults> results = Block_db.getSingleStakeBlockData(report.getEpochHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  		
	  		if(isFreefall) {
	  			
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(address)) {
						if(results.get(report.getStakerPos().intValue()).getStakeobj().getAddress() != address && Position != report.getStakerPos().intValue()) {
							return false;
						}
					}
	  			}
	  			
	  		}else {
	  			if(results.get(0).getStakeobj().getAddress() != address && Position != report.getStakerPos().intValue()) {
					return false;
				}
	  		}
		}
		
			
		
  		return true;
	}
	
	public static boolean checkNonceSeq(int addrCounter) {
		int[] nonceArr = new int[addrCounter];

		for (int n = 0; n < nonceArr.length; n++) {  
			nonceArr[n]=n;
		}
		if(OrderDiff.orderedTx(nonceArr) != 1) {
		return false;	
		}
		return true;
	}
	
}
