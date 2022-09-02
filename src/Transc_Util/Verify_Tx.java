package Transc_Util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Client;

import Blocks.Validate.Util.Tx_Val;
import Blocks.Validate.Util.ReportVerification.VerifyPenalty;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.db.retrieve;
import crypto.BytesToFro;
import penalty.Report;
import penalty.thePunisher;
import penalty.thePunisherAcc;
import temp.Holder;
import temp.Static;
import transc.BlancChk;
import transc.mod.Ctx;
import transc.mod.Ptx;
import vault.BackUpVault;
import vault.Vault_obj;
import vault.db.vault_retrie;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class Verify_Tx {
	static Client client;
	
	public static Boolean verifyCtx(Ctx ctx, long epochConfirmationTime) throws IOException, ClassNotFoundException {
		
        //Account Data
	    String coinAddress = null; 
    	long fromnonce = 0;
    	PublicKey frompubkey = null; 
    	
    	//Transaction Variable
    	String FromAddress;
  		String ToAddress;
      	BigDecimal value,coinRewardAfterPartition,stakeRewardAfterPartition,vaultRatio;
      	long timestamp;
      	byte[] txSig;
      	long TxNonce;
      	String range;
      
         	//Transaction data in primitive type
         	 FromAddress = BytesToFro.convertByteArrayToString(ctx.getFromAddress());
     		 ToAddress = BytesToFro.convertByteArrayToString(ctx.getToAddress());
     		 value = BytesToFro.convertBytesToBigDecimal(ctx.getValue());
     		 coinRewardAfterPartition = BytesToFro.convertBytesToBigDecimal(ctx.getCoinRewardAfterPartition());
   		     stakeRewardAfterPartition =  BytesToFro.convertBytesToBigDecimal(ctx.getStakeRewardAfterPartition());
   		     vaultRatio = BytesToFro.convertBytesToBigDecimal(ctx.getVaultRatio());    
     		 timestamp = BytesToFro.convertBytesToLong(ctx.getTimestamp());
        	 txSig = ctx.getTxSig();
        	 TxNonce = BytesToFro.convertBytesToLong(ctx.getNonce());
        	 range = BytesToFro.convertByteArrayToString(ctx.getRange());
         	 
         	
         	 //Account data
        	
 				 if(FromAddress.equals(Static.MAINTENANCE_VAULT)){
 					Vault_obj maintenanceVault = vault_retrie.retrieveMaintenanceVaultData();
 					coinAddress = Static.MAINTENANCE_VAULT;
 					fromnonce = maintenanceVault.getNonce();
                  	frompubkey = maintenanceVault.getPublicKey();
                  	
 				 }else if(FromAddress.equals(Static.BACKUP_VAULT) ) {
 					BackUpVault backupVault = vault_retrie.retrieveBackUpVaultData();
 					coinAddress = Static.BACKUP_VAULT;
                  	fromnonce = backupVault.getNonce();
                  	frompubkey = backupVault.getPublicKey();
 				 }else {
 					 Acc_obj accData = Retrie.retrieveAccData(FromAddress);
 	              	if(accData == null) {
 	              		return false;
 	              	}else {
 	              		coinAddress = accData.getCoinAddress();
 	              		fromnonce = accData.getCtxNonce();
 	              		frompubkey = accData.getPubkey();
 	              	} 
 				 }
 				 
 				 //Recipient
 				 if(ToAddress.equals(Static.MAINTENANCE_VAULT)){
  					ToAddress = Static.MAINTENANCE_VAULT;
                   	
  				 }else if(ToAddress.equals(Static.BACKUP_VAULT) ) {
  					ToAddress = Static.BACKUP_VAULT;
  				 }else {
  					Acc_obj ToAccData = Retrie.retrieveAccData(ToAddress);
  					if(ToAccData == null) {
  						return false;
  					}else {
  						ToAddress = ToAccData.getCoinAddress();
  					}
  				 }
 				 
 			if(range.equals(Static.TYPE_STAKE) || range.equals(Static.TYPE_UNSTAKE) ) {
         		if(timestamp >= epochConfirmationTime) {
         			System.out.println("Stake transaction failed..epoch confirmation time is not obeyed!");
         			return false;
         		}
         		 StakeObj stakeObj = retrieve.retrieveSingleStakeData(FromAddress);
 				 
         		 if(range.equals(Static.TYPE_UNSTAKE)) {
         			if(stakeObj == null) {
         				System.out.println("Unstake transaction failed...stake obj does not exist in SEWS pool!");
     					return false;
     				 }
         		 }
         		 
         		//Checks for banned validators
         		 if(Holder.gehennaList.contains(FromAddress)){
         			System.out.println("Stake transaction failed...account has been banned!");
         				return false;
         		 }
      			if(!ToAddress.equals(Static.STAKE_OBJ)) {
      				System.out.println("Stake transaction failed...recipient address not stated properly 'Stake_obj'");
      				 return false;
      			}
      			if(Holder.processedStakerAddr.contains(Static.EPOCH_VALIDATOR_ADDRESS)) {
      				System.out.println("Stake transaction failed...this account " + FromAddress + " has already staked coins...wait for the next epoch!");
      				return false;
      			}
      			
      		}else if(range.equals(Static.OBSOLETE_STAKE_CLEARANCE)){
         			StakeObj stakeobj = retrieve.retrieveSingleStakeData(ToAddress);
         			
         			long stakeTimestampDiff = timestamp - stakeobj.getTimestamp();
         			if(!(stakeobj.getStakeCoins().compareTo(Static.MIN_STAKE_VALUE) < 0) || !(stakeTimestampDiff > Static.STAKE_OBJ_OBSOLETE_TIME_THRESHOLD) || !(Holder.gehennaList.contains(stakeobj.getAddress()))) {
         				System.out.println("Obsolete stake obj clearance failed");
         				return false;
         			}
         			
     		}else if(range.equals(Static.REWARD_RANGE)) {
  			 
	     		//Verify reward transaction signature
	  			if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,frompubkey,value,coinRewardAfterPartition,stakeRewardAfterPartition,vaultRatio,TxNonce,timestamp,txSig,BytesToFro.convertBytesToLong(ctx.getEpochNumber()),BytesToFro.convertByteArrayToString(ctx.getEpochHash()),range)) {
	 				System.out.println("Reward transaction signature verification failed!");
	 				return false;
	 			}
	  			 
	     		//Compare nonce of account and Transaction
	     		if(TxNonce != 0) {      	
	       		 System.out.println("Reward transaction failed...Tx nonce is invalid");
	              		return false;
	            }
	     		//Compare reward amount
	 			if( Static.CTX_REWARD.compareTo(value) != 0) {
	 				System.out.println("Reward transaction failed...problem with reward amount!");
	 				 return false;
	 			}
	 			//Compare validator to award
				 if(!ToAddress.equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS)) {
					 System.out.println("Reward transaction failed " + ToAddress + " is not the previous epoch validator");
					 return false;
				 }
				//Compare current epoch validator
				 if(!FromAddress.equals(Static.VAL_REWARD_TX) ||!FromAddress.equals(Static.TOP_50_REWARD)) {
					 System.out.println("Reward transaction failed...problem with address " + FromAddress +" ...change sender address!");
	 				 return false;
	 			 }
    			
    		 }else if(range.equals(Static.PENALTY_RANGE)) {
     			 
     			 Report report = BytesToFro.convertByteArrayToReport(ctx.getReport());
     			 //verify penalty transaction signature
      			if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,frompubkey,value,TxNonce,timestamp,txSig,report,range)) {
      				System.out.println("Penalty transaction signature verification failed!");
               		return false;
               	}
      			//coinAddress penalty decision
      			if(!VerifyPenalty.verifyPenaltyDecision(report)) {
      				System.out.println("Report transaction decision verification failed!");
      				return false;
      			}
      			
     			//Compare nonce of account and Transaction
     			System.out.println("Penalty transaction failed...Tx nonce is invalid");
     			if(TxNonce < fromnonce) {      	
              		return false;
              	}
     			 
     			if(!ToAddress.equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS) || !FromAddress.equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
     				System.out.println("Penalty transaction failed...problem with one or both addresses"); 
     				return false;
    			 }
     			thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(ToAddress);
     			fromnonce = punishmentData.getPenaltyNonce();
       		 }else {
       			 //Verify coin transaction signature
           		if(!Tx_Val.verifiyCTxSignature(coinAddress,ToAddress,frompubkey,value,TxNonce,timestamp,txSig,range)) {
      				System.out.println("Transaction signature verification failed!");
               		return false;
               	}
           		
           		//check accounts balance
           		if(BlancChk.chkblance(ctx.getValue(),BytesToFro.convertStringToByteArray(range),BytesToFro.convertStringToByteArray(FromAddress),BytesToFro.convertStringToByteArray(ToAddress)).equals("U")) {
           			System.out.println("Transaction failed due to low balance in this account- " + coinAddress);
           			return false;	
               	}
           		//Check transaction nonce
           		if(TxNonce < fromnonce) {
         			 System.out.println("Transaction failed...Tx nonce is invalid");
             		return false;
             	}
       		 }
           
        return true;
    }
	
	public static boolean verifyPtx(Ptx ptx) throws IOException {

        //Account variables
        String sellerCoinAddress = null,buyerCoinAddress = null;
    	long sellerPackNonce = 0,buyerCoinNonce = 0;
    	BigDecimal buyerCoinBalance = null;
    	PublicKey buyerPubkey = null;
    	HashMap<String,BigInteger> sellerFreePackBalances = null;
    	HashMap<String,BigInteger> buyerFreePackBalances = null;
    
    	//Transaction variables
    	String FromCoinAddress, minterAddress,type;
  		String ToCoinAddress;
  		BigInteger packs;
        BigDecimal value;
      	long timestamp;
      	byte[] signature;
      	long txNonce;
      	
            	 FromCoinAddress = BytesToFro.convertByteArrayToString(ptx.getFromCoinAddress());
          		 ToCoinAddress = BytesToFro.convertByteArrayToString(ptx.getToCoinAddress());
          		 minterAddress = BytesToFro.convertByteArrayToString(ptx.getMinter());
          		 value = BytesToFro.convertBytesToBigDecimal(ptx.getValue());
          		 packs = new BigInteger(1,ptx.getPacks());
          		 txNonce = BytesToFro.convertBytesToLong(ptx.getPkNonce());
          		 signature = ptx.getSignature();
          		 timestamp = BytesToFro.convertBytesToLong(ptx.getTimestamp());
          		 type = BytesToFro.convertByteArrayToString(ptx.getType());
          		
          		
            		 //Account data
            		 Acc_obj accSellerData = Retrie.retrieveAccData(FromCoinAddress);
            		 Acc_obj accBuyerData = Retrie.retrieveAccData(ToCoinAddress);
                 		sellerCoinAddress = accSellerData.getCoinAddress();
                     	sellerPackNonce = accSellerData.getPtxNonce();
                     	
                     	buyerCoinAddress = accBuyerData.getCoinAddress();
                        buyerCoinBalance = accBuyerData.getCoinBalance();
                       	buyerCoinNonce = accBuyerData.getCtxNonce();
                       	buyerCoinBalance = accBuyerData.getCoinBalance();
                       	buyerPubkey = accBuyerData.getPubkey();
                 	
       	      
            /*	
             	if (type.equals("buy")) {
             		//Verify signature of pack buyer
                 	if(!Tx_Val.verifiyPTxSignature(sellerCoinAddress, buyerCoinAddress, packs, buyerPubkey, value, txNonce, timestamp, signature,type)) {
                 		return false;
                 	}
                 	
             	}else if (type.equals("lock")) {
             		//Verify signature of free pack owner
                 	if(!Tx_Val.verifiyPTxSignature(sellerCoinAddress, buyerCoinAddress, packs, buyerPubkey, value, txNonce, timestamp, signature,type)){
                 		return false;
                 	}
                 	
             	}else if (type.equals("free")) {
             		//Verify signature of locked pack owner
                 	if(!Tx_Val.verifiyPTxSignature(sellerCoinAddress, buyerCoinAddress, packs, buyerPubkey, value, txNonce, timestamp, signature,type)) {
                 		return false;
                 	}
                 	
             	}
             	*/
             	
             	//Check Seller Balance
             	if(BlancChk.chkSellerPacksbalance(FromCoinAddress,minterAddress,packs,sellerFreePackBalances).equals("PU")) {
             		return false;	
             	}
             	
             	//Check Buyer Balance
             	if(BlancChk.chkBuyerCoinbalance(ToCoinAddress,minterAddress,buyerCoinBalance,value,buyerFreePackBalances).equals("CU")) {
             		return false;	
             	}
             	
             	if(type.equals("buy")) {
             		//Compare nonce of buyer account and Transaction
                 	if(txNonce < buyerCoinNonce) {    
                 		return false;
                 	}
             	}else if(type.equals("lock")) {
             		//Compare nonce of buyer account and Transaction
                 	if(txNonce < sellerPackNonce) { 
                 		return false;
                 	}
             	}else if(type.equals("free")) {
             		//Compare nonce of buyer account and Transaction
                 	if(txNonce < sellerPackNonce) {     
                 		return false;
                 	}
             	}
            	

    
        return true;
	}
	
}
