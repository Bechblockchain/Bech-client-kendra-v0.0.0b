package Blocks.Validate.Util.ReportVerification;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

import Blocks.Validate.Util.Check50;
import Blocks.Validate.Util.Tx_Val;
import Blocks.db.Block_db;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import SEWS_Protocol.db.retrieve;
import connect.Network;
import crypto.BytesToFro;
import db.db_retrie;
import penalty.Report;
import temp.Holder;
import temp.Static;
import transc.mod.Ctx;
import vault.BackUpVault;
import vault.Vault_obj;
import vault.db.vault_retrie;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class VerifyCtxPenalty {
	public static boolean verifyCtxTxPenalty(Ctx transaction,Report report) throws IOException, ClassNotFoundException {
		
		//Account Data
        String coinAddress = null;
    	long nonce = 0;
    	
    	PublicKey pubkey = null;

    	//Transaction Data
    	String FromAddress;
  		String ToAddress;
      	BigDecimal totalEpochReward = null,coinRewardAfterPartition = null,stakeRewardAfterPartition = null,vaultRatio = null;
      	long timestamp;
      	byte[] txSig;
      	long TxNonce;
      	String range; BigDecimal value;
		
    	//Transaction data in primitive type
    	 FromAddress = BytesToFro.convertByteArrayToString(transaction.getFromAddress());
		 ToAddress = BytesToFro.convertByteArrayToString(transaction.getToAddress());
		 value = BytesToFro.convertBytesToBigDecimal(transaction.getToAddress());
 		 timestamp = BytesToFro.convertBytesToLong(transaction.getTimestamp());
		 txSig = transaction.getTxSig();
		 TxNonce = BytesToFro.convertBytesToLong(transaction.getNonce());
		 range = BytesToFro.convertByteArrayToString(transaction.getRange());
		 
		 //Account data
		 if(!range.equals(Static.REWARD_RANGE) && !FromAddress.equals(Static.MAINTENANCE_VAULT) && !FromAddress.equals(Static.BACKUP_VAULT)) {
			 Acc_obj accData = Retrie.retrieveAccData(FromAddress);
          	if(accData.equals(null)) {
          		Network.requestSingleAccount(FromAddress);
          		do {
          			
          		}while(Retrie.retrieveAccData(FromAddress) == null);    
          		
              	return false;
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
		 
		 //Validates stake Tx
		 if(range.equals(Static.TYPE_STAKE) || range.equals(Static.TYPE_UNSTAKE) ) {
			
			
  			if(timestamp >= Static.PREVIOUS_VALIDATOR_CONFIRMATION_TIME.longValue()) {
  				return false;
  			}
  			
  			StakeObj stakeObj = retrieve.retrieveSingleStakeData(FromAddress);
			if(stakeObj.equals(null)) {
				return false;
			}
  			 
  			//Checks for banned validators
     		if(Holder.gehennaList.contains(FromAddress)){
     			return false;
     		}
  			
  			 
  			if(!ToAddress.equals(Static.STAKE_OBJ)) {
  				return false;
  			}
  			if( Holder.processedStakerAddr.contains(Static.EPOCH_VALIDATOR_ADDRESS)) {
  				return false;
  			}
  			
			
		 }else if(range.equals(Static.OBSOLETE_STAKE_CLEARANCE)){
			 
  			StakeObj stakeobj = retrieve.retrieveSingleStakeData(ToAddress);
 			
  			long stakeTimestampDiff = timestamp - stakeobj.getTimestamp();
  			if(!(stakeobj.getStakeCoins().compareTo(Static.MIN_STAKE_VALUE) < 0) || !(stakeTimestampDiff > Static.STAKE_OBJ_OBSOLETE_TIME_THRESHOLD) || !(Holder.gehennaList.contains(stakeobj.getAddress()))) {
  				return false;
  			}
  			
		 }else if(range.equals(Static.REWARD_RANGE)) {
 			
 			 totalEpochReward = BytesToFro.convertBytesToBigDecimal(transaction.getValue());
 			 coinRewardAfterPartition = BytesToFro.convertBytesToBigDecimal(transaction.getCoinRewardAfterPartition());
    		 stakeRewardAfterPartition =  BytesToFro.convertBytesToBigDecimal(transaction.getStakeRewardAfterPartition());
    		 vaultRatio = BytesToFro.convertBytesToBigDecimal(transaction.getVaultRatio());

    		 if(! Holder.myReports.get(0).getCoinBlock().getEpochStatus().equals(Static.EPOCH_COMPLETE)) {
			    return false;
  			}
    		 
			 if( db_retrie.getSingleValidatorReward(ToAddress).compareTo(totalEpochReward) != 0) {
			    return false;
			 }
			 
			 String previousValidatorAddress = null;
			 if(report.getStakerPos().intValue() == 0) {
				 
				 StakeBlock stakeBlock =  Block_db.getSingleStakeBlockData(report.getEpochHash());
				 
				 List<weightResults> results = Block_db.getSingleStakeBlockData(stakeBlock.getPrev_hash()).getProcessedStakes();
			  	 results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
			  		
		  			for(weightResults weightresults: results) {
						if(weightresults.getStakeobj().getAddress().equals(report.getPrevValidatorAddress())) {
									previousValidatorAddress = report.getPrevValidatorAddress();
									break;
						}
		  			}
				 
			 }else {
				 List<weightResults> results = Block_db.getSingleStakeBlockData(report.getEpochHash()).getProcessedStakes();
			  	 results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
			  		
		  			for(weightResults weightresults: results) {
						if(weightresults.getStakeobj().getAddress().equals(report.getSuspectAddress())) {
							int previousValidatorPosition = report.getStakerPos().intValue() - 1;
							String Address = results.get(previousValidatorPosition).getStakeobj().getAddress();
							if(Address.equals(report.getPrevValidatorAddress())) {
								previousValidatorAddress = Address;
								break;
							}
						}
		  			}
			 }
			 
			 if(!ToAddress.equals(previousValidatorAddress)) {
				 return false;
			 }
			 
			 if(!FromAddress.equals("reward_tx") || !FromAddress.equals("reward_50_tx")) {
				 return false;
			 }
			 
			 if(TxNonce != 0) {      	
	            return false;
	         }
			 
			 if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,pubkey,totalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,vaultRatio,TxNonce,timestamp,txSig,BytesToFro.convertBytesToLong(transaction.getEpochNumber()),BytesToFro.convertByteArrayToString(transaction.getEpochHash()),range)) {
					return false;
	    	 }
			 
			BigDecimal allReward = db.db_retrie.getTop50Reward();
       		int counter = 0; int staker50counter = 0;
       		List<Ctx>ctxlist = Holder.myReports.get(1).getCoinBlock().getTransactionList();
       		for(int t=1; t < ctxlist.size(); t++) {
              	Ctx Tranc = ctxlist.get(t);
              	if(BytesToFro.convertByteArrayToString(Tranc.getFromAddress()).equals("reward_tx")) {
              		counter++;
              	}else if(BytesToFro.convertByteArrayToString(Tranc.getFromAddress()).equals("reward_50_tx")) {
              		staker50counter++;
              		if(Check50.checkEachstaker50Partition(allReward).compareTo(BytesToFro.convertBytesToBigDecimal(ctxlist.get(t).getEachStaker50Partition())) != 0) {
	                 		return false;
	                 	}
              	}
              	
              	if(counter != 1 || staker50counter != Check50.checkstaker50Num()) {
              		return false;
              	}
              	
              }
			 
		 }else if(range.equals(Static.PENALTY_RANGE)) {
				
  			if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,pubkey,value,TxNonce,timestamp,txSig,report,range)) {
  				return false;
           	}
  			if(!VerifyPenalty.verifyPenaltyDecision(report)) {
			    return false;
  			}
  			
  			if(!ToAddress.equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS) || !FromAddress.equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
  				return false;
 			}
  			
  			if(TxNonce < nonce) {      	
  				return false;
           	}
  			 List<Ctx>ctxlist = Holder.myReports.get(0).getCoinBlock().getTransactionList();
  			int counter = 0;
	      		for(int t=1; t < ctxlist.size(); t++) {
	             	Ctx Tranc = ctxlist.get(t);
	             	if(BytesToFro.convertByteArrayToString(Tranc.getRange()).equals(Static.PENALTY_RANGE)) {
	             		counter++;
	             	}
	             	if(counter <= 0 || counter > 2) {
	             		return false;
	             	}
	             }
	      		
  		 }else {
  			 
  			if(TxNonce < nonce) {      	
         		return false;
         	}
  			
  			if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,pubkey,totalEpochReward,TxNonce,timestamp,txSig,range)) {
         		return false;
         	}
  		 }
		 
        return true;
	}
	
}
