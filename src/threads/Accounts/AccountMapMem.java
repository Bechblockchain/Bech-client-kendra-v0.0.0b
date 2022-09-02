package threads.Accounts;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.db.retrieve;
import SEWS_Protocol.db.store;
import connect.Mod.Request;
import crypto.BytesToFro;
import penalty.Penalty;
import temp.Holder;
import temp.Static;
import threads.Tx.TxIndexThread;
import transc.mod.Ctx;
import vault.BackUpVault;
import vault.Vault_obj;
import vault.db.vault_retrie;
import vault.db.vault_store;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class AccountMapMem implements Runnable{

	public static LinkedBlockingQueue<Block_obj> newTxblocks = new LinkedBlockingQueue<Block_obj>();
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			
			try {
				Block_obj block = newTxblocks.take();
				String blocknum =block.getBlock_num();
				
				Block_db.storeBlockData(block);
				
					Ctx transaction;
			        List<Ctx> tx;
			        String ToAddress, FromAddress, range;
			        long TxNonce,timestamp;
			        BigDecimal value,coinRewardAfterPartition = null,stakeRewardAfterPartition = null,maintenanceVaultRatio = null,eachStaker50Partition = null;
			        
			        tx = block.getTransactionList();
			        for(int i = 0; i < tx.size(); i++) {
			        	
			        	 transaction = tx.get(i);
			        	 
			        	 range = BytesToFro.convertByteArrayToString(transaction.getRange());
			         	 FromAddress = BytesToFro.convertByteArrayToString(transaction.getFromAddress());
			     		 ToAddress = BytesToFro.convertByteArrayToString(transaction.getToAddress());
			     		 value = BytesToFro.convertBytesToBigDecimal(transaction.getValue());
			     		 TxNonce = BytesToFro.convertBytesToLong(transaction.getNonce());
			        	 timestamp =BytesToFro.convertBytesToLong(transaction.getTimestamp());
			        	
			        
			          if(checkbalance(transaction.getValue(),transaction.getRange(),transaction.getFromAddress(),transaction.getToAddress()).equals("A")) {
			        	 try {
			        		 TxIndexThread.newTxReq.put(new Request("indexCtx",BytesToFro.bytesToHex(transaction.getTxSig()),blocknum));
						 }catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						 }
						
			        	 
			     		 if(range.equals(Static.REWARD_RANGE)) {
			     			 if(FromAddress.equals("reward_50_tx")) {
			     				 eachStaker50Partition =  BytesToFro.convertBytesToBigDecimal(transaction.getEachStaker50Partition());
			     			 }
			     			 coinRewardAfterPartition = BytesToFro.convertBytesToBigDecimal(transaction.getCoinRewardAfterPartition());
			        		 stakeRewardAfterPartition =  BytesToFro.convertBytesToBigDecimal(transaction.getStakeRewardAfterPartition());
			        		 maintenanceVaultRatio = BytesToFro.convertBytesToBigDecimal(transaction.getVaultRatio()); 
			        		
			     		 }
		         		
			        	 //Account variables
			         	String coinAddress = null,rCoinAddress;
			         	String mintAddress = null,rMintAddress;
			         	long rNonce;
			         	long ptxNonce = 0,rPtxNonce;
			         	BigDecimal coinBalance = null,rCoinBalance;
			         	PublicKey pubkey = null,rPubkey;
			         	HashMap<String,BigInteger> tradesNbalances = null;
			         	HashMap<String,BigInteger> rTradesNbalances;
			         	BigDecimal totalSendingValue = null;
			         	
			         	if(ToAddress.equals(Static.MAINTENANCE_VAULT) && FromAddress.equals(Static.BACKUP_VAULT)) {
			         		
			         			BackUpVault backUpVault = vault_retrie.retrieveBackUpVaultData();
				    			BigDecimal backUpVaultCoins = backUpVault.getVaultCoins();
				    			PublicKey backUpPublicKey = backUpVault.getPublicKey();
				    			long backUpnonce = backUpVault.getNonce();
				    			
				    			//Store new E-Rebase BackUp vault balance
				    			BigDecimal backUpVaultNewBalance = backUpVaultCoins.setScale(2, RoundingMode.HALF_EVEN).subtract(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeBackUpVaultData(new BackUpVault(Static.BACKUP_VAULT ,backUpVaultNewBalance,backUpPublicKey,backUpnonce));
					    		
					    		
					    		Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
				    			BigDecimal vaultCoins = vault.getVaultCoins();
				    			PublicKey publicKey = vault.getPublicKey();
				    			long nonce = vault.getNonce();
				    			
				    			//Store new maintenance vault balance
				    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,vaultNewBalance,publicKey,nonce));	
					    		
			         		
			         	}else if(ToAddress.equals(Static.BACKUP_VAULT) && FromAddress.equals(Static.MAINTENANCE_VAULT)) {
		
			         			Vault_obj maintenanceVault = vault_retrie.retrieveMaintenanceVaultData();
				    			BigDecimal maintenanceVaultCoins = maintenanceVault.getVaultCoins();
				    			PublicKey maintenanceVaultPublicKey = maintenanceVault.getPublicKey();
				    			long maintenanceNonce = maintenanceVault.getNonce();
				    			
				    			//Store new maintenance vault balance
				    			BigDecimal maintenanceVaultCoinsNewBalance = maintenanceVaultCoins.setScale(2, RoundingMode.HALF_EVEN).subtract(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,maintenanceVaultCoinsNewBalance,maintenanceVaultPublicKey,maintenanceNonce));	
					    		 
					    		
					    		BackUpVault vault = vault_retrie.retrieveBackUpVaultData();
				    			BigDecimal vaultCoins = vault.getVaultCoins();
				    			PublicKey publicKey = vault.getPublicKey();
				    			long nonce = vault.getNonce();
				    			
				    			//Store new E-Rebase BackUp vault balance
				    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeBackUpVaultData(new BackUpVault(Static.BACKUP_VAULT ,vaultNewBalance,publicKey,nonce));	
					    		 
					         	
			         	}else if(ToAddress.equals(Static.BACKUP_VAULT) && !FromAddress.equals(Static.MAINTENANCE_VAULT)) {
		
				         		Acc_obj senderAccData = Retrie.retrieveAccData(FromAddress);
					         	coinAddress = senderAccData.getCoinAddress();
					         	mintAddress = senderAccData.getMintAddress();
					         	ptxNonce = senderAccData.getPtxNonce();
					         	coinBalance = senderAccData.getCoinBalance();
					         	pubkey = senderAccData.getPubkey();
					         	tradesNbalances = senderAccData.getTradesNbalances();
					         	
					    		totalSendingValue = value.setScale(2, RoundingMode.HALF_EVEN).add(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
					    		BigDecimal senderNewBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(totalSendingValue.setScale(2, RoundingMode.HALF_EVEN));
					    		//store sender new balance
					         	Acc_obj newSenderAccData = new Acc_obj(coinAddress,mintAddress,TxNonce,ptxNonce,senderNewBalance,pubkey,tradesNbalances);
					         	Str.storeSingleAccData(newSenderAccData);
					         	
				         		
				         		BackUpVault vault = vault_retrie.retrieveBackUpVaultData();
				    			BigDecimal vaultCoins = vault.getVaultCoins();
				    			PublicKey publicKey = vault.getPublicKey();
				    			long nonce = vault.getNonce();
				    			
				    			//Store new E-Rebase BackUp vault balance
				    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeBackUpVaultData(new BackUpVault(Static.BACKUP_VAULT ,vaultNewBalance,publicKey,nonce));	
					    		 
					         	
			         	}else if(ToAddress.equals(Static.MAINTENANCE_VAULT) && !FromAddress.equals(Static.BACKUP_VAULT)) {
			         			Acc_obj senderAccData = Retrie.retrieveAccData(FromAddress);
					         	coinAddress = senderAccData.getCoinAddress();
					         	mintAddress = senderAccData.getMintAddress();
					         	ptxNonce = senderAccData.getPtxNonce();
					         	coinBalance = senderAccData.getCoinBalance();
					         	pubkey = senderAccData.getPubkey();
					         	tradesNbalances = senderAccData.getTradesNbalances();
					         	
					    		totalSendingValue = value.setScale(2, RoundingMode.HALF_EVEN).add(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
					    		BigDecimal senderNewBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(totalSendingValue.setScale(2, RoundingMode.HALF_EVEN));
					    		//store sender new balance
					         	Acc_obj newSenderAccData = new Acc_obj(coinAddress,mintAddress,TxNonce,ptxNonce,senderNewBalance,pubkey,tradesNbalances);
					         	Str.storeSingleAccData(newSenderAccData);
					         	
				         		Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
				    			BigDecimal vaultCoins = vault.getVaultCoins();
				    			PublicKey publicKey = vault.getPublicKey();
				    			long nonce = vault.getNonce();
				    			//Store new maintenance vault balance
				    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				    			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,vaultNewBalance,publicKey,nonce));	
					    		
			         	}else if(FromAddress.equals(Static.MAINTENANCE_VAULT) && !ToAddress.equals(Static.BACKUP_VAULT)) {
			         		Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
			    			BigDecimal vaultCoins = vault.getVaultCoins();
			    			PublicKey publicKey = vault.getPublicKey();
			    			long nonce = vault.getNonce();
			    			//Store new maintenance vault balance
			    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).subtract(value.setScale(2, RoundingMode.HALF_EVEN));
			    			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,vaultNewBalance,publicKey,nonce));	
				    		 
				         	Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
				         	rCoinAddress = ReceiverAccData.getCoinAddress();
				         	rMintAddress = ReceiverAccData.getMintAddress();
				         	rNonce = ReceiverAccData.getCtxNonce();
				         	rPtxNonce = ReceiverAccData.getPtxNonce();
				         	rCoinBalance = ReceiverAccData.getCoinBalance();
				         	rPubkey = ReceiverAccData.getPubkey();
				         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
				         	//store receiver new balance
				         	BigDecimal receiverNewBalance = value.setScale(2, RoundingMode.HALF_EVEN).add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
				    		Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,receiverNewBalance,rPubkey,rTradesNbalances);
				         	Str.storeSingleAccData(newReceiverAccData);
				         	
			         	}else if(FromAddress.equals(Static.BACKUP_VAULT) && !ToAddress.equals(Static.MAINTENANCE_VAULT)) {
			         		
			         		BackUpVault vault = vault_retrie.retrieveBackUpVaultData();
			    			BigDecimal vaultCoins = vault.getVaultCoins();
			    			PublicKey publicKey = vault.getPublicKey();
			    			long nonce = vault.getNonce();
			    			
			    			//Store new E-Rebase BackUp vault balance
			    			BigDecimal vaultNewBalance = vaultCoins.setScale(2, RoundingMode.HALF_EVEN).subtract(value.setScale(2, RoundingMode.HALF_EVEN));
			    			vault_store.storeBackUpVaultData(new BackUpVault(Static.BACKUP_VAULT ,vaultNewBalance,publicKey,nonce));	
				    		 
				         	Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
				         	rCoinAddress = ReceiverAccData.getCoinAddress();
				         	rMintAddress = ReceiverAccData.getMintAddress();
				         	rNonce = ReceiverAccData.getCtxNonce();
				         	rPtxNonce = ReceiverAccData.getPtxNonce();
				         	rCoinBalance = ReceiverAccData.getCoinBalance();
				         	rPubkey = ReceiverAccData.getPubkey();
				         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
				         	//store receiver new balance
				         	BigDecimal receiverNewBalance = value.setScale(2, RoundingMode.HALF_EVEN).add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
				    		Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,receiverNewBalance,rPubkey,rTradesNbalances);
				         	Str.storeSingleAccData(newReceiverAccData);
				         	
						     
			         	}else if(range.equals(Static.OBSOLETE_STAKE_CLEARANCE)){
		         			StakeObj stakeObj = retrieve.retrieveSingleStakeData(ToAddress);
		         			BigDecimal stakedCoinBalance = stakeObj.getStakeCoins();
			    
				         	Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
				         	rCoinAddress = ReceiverAccData.getCoinAddress();
				         	rMintAddress = ReceiverAccData.getMintAddress();
				         	rNonce = ReceiverAccData.getCtxNonce();
				         	rPtxNonce = ReceiverAccData.getPtxNonce();
				         	rCoinBalance = ReceiverAccData.getCoinBalance();
				         	rPubkey = ReceiverAccData.getPubkey();
				         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
				         	
				         	BigDecimal stakerNewAccBalance = stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).add(rCoinBalance).subtract(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
				    		
				         	Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,stakerNewAccBalance,rPubkey,rTradesNbalances);
				         	retrieve.deleteSingleStakeData(ToAddress);
				         	Str.storeSingleAccData(newReceiverAccData);
				         	
		         		}else if(range.equals(Static.TYPE_UNSTAKE)) {
			    			StakeObj stakeObj = retrieve.retrieveSingleStakeData(FromAddress);
			    			BigDecimal stakedCoinBalance = stakeObj.getStakeCoins();
			    			coinAddress = stakeObj.getAddress();
			    			long oldLastValTimestamp = stakeObj.getLastValidationTimestamp();
			    			long oldtimestamp = stakeObj.getTimestamp();
			    			String oldLastValStatus = stakeObj.getLastValidationStatus();
			    			
			    			Acc_obj senderAccData = Retrie.retrieveAccData(FromAddress);
				         	coinAddress = senderAccData.getCoinAddress();
				         	mintAddress = senderAccData.getMintAddress();
				         	ptxNonce = senderAccData.getPtxNonce();
				         	coinBalance = senderAccData.getCoinBalance();
				         	pubkey = senderAccData.getPubkey();
				         	tradesNbalances = senderAccData.getTradesNbalances();
				         	
				    		totalSendingValue = value.setScale(2, RoundingMode.HALF_EVEN).add(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
				    		
			    			
			    			BigDecimal stakerNewBalance = stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(totalSendingValue.setScale(2, RoundingMode.HALF_EVEN));
				    		
			    			StakeObj stakeAcc = new StakeObj(coinAddress,stakerNewBalance,oldtimestamp,oldLastValTimestamp,oldLastValStatus);
			    			store.storeSingleStakeData(stakeAcc);
			    			
			    			BigDecimal stakerNewAccBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				         	Acc_obj stakerAccData = new Acc_obj(coinAddress,mintAddress,TxNonce,ptxNonce,stakerNewAccBalance,pubkey,tradesNbalances);
				         	Str.storeSingleAccData(stakerAccData);
				        	Holder.processedStakerAddr.add(FromAddress);
				         	Holder.epochStakeTxs.add(transaction);
			    			
			    		}else if(range.equals(Static.TYPE_STAKE)) {
			    			
			    			Acc_obj senderAccData = Retrie.retrieveAccData(FromAddress);
				         	coinAddress = senderAccData.getCoinAddress();
				         	mintAddress = senderAccData.getMintAddress();
				         	ptxNonce = senderAccData.getPtxNonce();
				         	coinBalance = senderAccData.getCoinBalance();
				         	pubkey = senderAccData.getPubkey();
				         	tradesNbalances = senderAccData.getTradesNbalances();
				         	
				    		totalSendingValue = value.setScale(2, RoundingMode.HALF_EVEN).add(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
				    		
			    			
			    			BigDecimal stakerNewAccBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(totalSendingValue.setScale(2, RoundingMode.HALF_EVEN));
				         	Acc_obj stakerAccData = new Acc_obj(coinAddress,mintAddress,TxNonce,ptxNonce,stakerNewAccBalance,pubkey,tradesNbalances);
				         	Str.storeSingleAccData(stakerAccData);
				         	StakeObj stakerStakeAcc = null;
				         	StakeObj stakeObj = retrieve.retrieveSingleStakeData(FromAddress);
				         	if(stakeObj == null) {
				    			long LastValTimestamp = 0;
				    			String LastValStatus = Static.NONE;
				    			BigDecimal stakerBalance = value;
				    			stakerStakeAcc = new StakeObj(coinAddress,stakerBalance,timestamp,LastValTimestamp,LastValStatus);
				         	}else {
				         		coinBalance =  stakeObj.getStakeCoins();
				    			coinAddress = stakeObj.getAddress();
				    			long oldLastValTimestamp = stakeObj.getLastValidationTimestamp();
				    			String oldLastValStatus = stakeObj.getLastValidationStatus();
				    			BigDecimal stakerNewBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).add(value.setScale(2, RoundingMode.HALF_EVEN));
				    			stakerStakeAcc = new StakeObj(coinAddress,stakerNewBalance,timestamp,oldLastValTimestamp,oldLastValStatus);
				    	
				         	}
				         
				         	store.storeSingleStakeData(stakerStakeAcc);
				         	Holder.processedStakerAddr.add(FromAddress);
			    			Holder.epochStakeTxs.add(transaction);
			    			
			    		}else if(range.equals(Static.REWARD_RANGE)) {
			    			
			    			if(FromAddress.equals(Static.VAL_REWARD_TX)) {
			    				
			    				Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
					         	rCoinAddress = ReceiverAccData.getCoinAddress();
					         	rMintAddress = ReceiverAccData.getMintAddress();
					         	rNonce = ReceiverAccData.getCtxNonce();
					         	rPtxNonce = ReceiverAccData.getPtxNonce();
					         	rCoinBalance = ReceiverAccData.getCoinBalance();
					         	rPubkey = ReceiverAccData.getPubkey();
					         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
					         	
					         	BigDecimal receiverNewBalance = coinRewardAfterPartition.setScale(2, RoundingMode.HALF_EVEN).add(stakeRewardAfterPartition.setScale(2, RoundingMode.HALF_EVEN)).add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
					    		
					         	Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,receiverNewBalance,rPubkey,rTradesNbalances);
					         	Str.storeSingleAccData(newReceiverAccData);
					         	
					         	Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
								BigDecimal vaultCoins = vault.getVaultCoins();
								PublicKey publicKey = vault.getPublicKey();
								long nonce =  vault.getNonce(); 
								
								BigDecimal newVaultCoins = vaultCoins.add(maintenanceVaultRatio);
								vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,newVaultCoins,publicKey,nonce));
								
					         	StakeProcessor.updateValStartStakeStatus(block.getValidator_Addr(),Static.LAST_VAL_STATUS_COMPLETE);
					         	
			    				
			    			}else if(FromAddress.equals(Static.TOP_50_REWARD)) {
			    				
			    				Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
					         	rCoinAddress = ReceiverAccData.getCoinAddress();
					         	rMintAddress = ReceiverAccData.getMintAddress();
					         	rNonce = ReceiverAccData.getCtxNonce();
					         	rPtxNonce = ReceiverAccData.getPtxNonce();
					         	rCoinBalance = ReceiverAccData.getCoinBalance();
					         	rPubkey = ReceiverAccData.getPubkey();
					         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
					         	
					         	
					         	BigDecimal receiverNewBalance = eachStaker50Partition.add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
					    		
					         	Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,receiverNewBalance,rPubkey,rTradesNbalances);
					         	Str.storeSingleAccData(newReceiverAccData);
					         
					         	StakeProcessor.updateValStartStakeStatus(block.getValidator_Addr(),Static.LAST_VAL_STATUS_COMPLETE);
					         	
			    			}
			    			
				         
			    		}else if(range.equals(Static.PENALTY_RANGE)) {
			    			
			    			BigDecimal reporterRatio = value.multiply(new BigDecimal(0.50));
			    			Acc_obj reporterAccData = Retrie.retrieveAccData(Static.EPOCH_VALIDATOR_ADDRESS);
				         	rCoinAddress = reporterAccData.getCoinAddress();
				         	rMintAddress = reporterAccData.getMintAddress();
				         	rNonce = reporterAccData.getCtxNonce();
				         	rPtxNonce = reporterAccData.getPtxNonce();
				         	rCoinBalance = reporterAccData.getCoinBalance();
				         	rPubkey = reporterAccData.getPubkey();
				         	rTradesNbalances = reporterAccData.getTradesNbalances();
				         	
				         	BigDecimal reporterNewBalance = reporterRatio.setScale(2, RoundingMode.HALF_EVEN).add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
				    		
				    		
				         	Acc_obj newReporterAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,reporterNewBalance,rPubkey,rTradesNbalances);
				         	Str.storeSingleAccData(newReporterAccData);
				         	//Crime
			    			String crime = BytesToFro.convertByteArrayToString(transaction.getReport());
			    			if(crime.equals(Static.GEHENNA_REQUEST)) {
			    				Penalty.Gehenna(BytesToFro.convertByteArrayToReport(transaction.getReport()),value);
			    			}else if(crime.equals(Static.KORUST_REQUEST)) {
			    				Penalty.Korust(BytesToFro.convertByteArrayToReport(transaction.getReport()),value);
			    			}else if(crime.equals(Static.VANITY_REQUEST)) {
			    				Penalty.Vanity(BytesToFro.convertByteArrayToReport(transaction.getReport()),value);
			    			}
			    			
			    		}else {
			    			
				         	Acc_obj senderAccData = Retrie.retrieveAccData(FromAddress);
				         	coinAddress = senderAccData.getCoinAddress();
				         	mintAddress = senderAccData.getMintAddress();
				         	ptxNonce = senderAccData.getPtxNonce();
				         	coinBalance = senderAccData.getCoinBalance();
				         	pubkey = senderAccData.getPubkey();
				         	tradesNbalances = senderAccData.getTradesNbalances();
				         	
				    		totalSendingValue = value.setScale(2, RoundingMode.HALF_EVEN).add(Static.FEE.setScale(2, RoundingMode.HALF_EVEN));
				    		
			    			
			    			BigDecimal senderNewBalance = coinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(totalSendingValue.setScale(2, RoundingMode.HALF_EVEN));
				    		
				         	Acc_obj newSenderAccData = new Acc_obj(coinAddress,mintAddress,TxNonce,ptxNonce,senderNewBalance,pubkey,tradesNbalances);
				         	Str.storeSingleAccData(newSenderAccData);
				         	
				         	Acc_obj ReceiverAccData = Retrie.retrieveAccData(ToAddress);
				         	rCoinAddress = ReceiverAccData.getCoinAddress();
				         	rMintAddress = ReceiverAccData.getMintAddress();
				         	rNonce = ReceiverAccData.getCtxNonce();
				         	rPtxNonce = ReceiverAccData.getPtxNonce();
				         	rCoinBalance = ReceiverAccData.getCoinBalance();
				         	rPubkey = ReceiverAccData.getPubkey();
				         	rTradesNbalances = ReceiverAccData.getTradesNbalances();
				         	
				         	BigDecimal receiverNewBalance = value.setScale(2, RoundingMode.HALF_EVEN).add(rCoinBalance.setScale(2, RoundingMode.HALF_EVEN));
				    		
				    		
				         	Acc_obj newReceiverAccData = new Acc_obj(rCoinAddress,rMintAddress,rNonce,rPtxNonce,receiverNewBalance,rPubkey,rTradesNbalances);
				         	Str.storeSingleAccData(newReceiverAccData);
			    		}
			    	
						//clearPacksTx(ptx);
						clearCoinTx(tx);
			        	}else {
			        		continue;
			        	}
			        }	
				
				
			}catch ( ClassNotFoundException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

	
	public static String checkbalance(byte[] value,byte[] range, byte[] fromaddress,byte [] toaddress) throws IOException {
		BigDecimal fee = Static.FEE;
		String blanceAvailabilty = null;
		if(BytesToFro.convertByteArrayToString(fromaddress).equals(Static.MAINTENANCE_VAULT) || BytesToFro.convertByteArrayToString(fromaddress).equals(Static.BACKUP_VAULT)) {
			blanceAvailabilty = "A";
	   	}else {
			if(BytesToFro.convertByteArrayToString(range).equals(Static.TYPE_UNSTAKE)) {
				StakeObj stakeObj = retrieve.retrieveSingleStakeData(BytesToFro.convertByteArrayToString(fromaddress));
				if(stakeObj != null) {
					BigDecimal stakedCoinBalance = stakeObj.getStakeCoins().add(fee);
					
					if(stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(BytesToFro.convertBytesToBigDecimal(value).setScale(2, RoundingMode.HALF_EVEN)) >= 0 ) {
						blanceAvailabilty = "A";
					}else if(stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(BytesToFro.convertBytesToBigDecimal(value).setScale(2, RoundingMode.HALF_EVEN)) == -1) {
						blanceAvailabilty = "U";
					}
				}else {
					blanceAvailabilty = "U";
				}
				
			}else {
				BigDecimal totalAmount= BytesToFro.convertBytesToBigDecimal(value).add(fee);
				BigDecimal Value = BytesToFro.convertBytesToBigDecimal(value);
				String from = BytesToFro.convertByteArrayToString(fromaddress);
				String To =  BytesToFro.convertByteArrayToString(toaddress);
			
				if(from != Static.VAL_REWARD_TX && from != Static.TOP_50_REWARD ) {
					Acc_obj fromAcc =  Retrie.retrieveAccData(BytesToFro.convertByteArrayToString(fromaddress));
					
					if(fromAcc != null) {
						if(To.equals(Static.STAKE_OBJ)) {
							BigDecimal accCoinBalance = fromAcc.getCoinBalance();
							if(accCoinBalance != null) {
								if( accCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(totalAmount.setScale(2, RoundingMode.HALF_EVEN)) >= 0 ) {
									return blanceAvailabilty = "A";
								}else if( accCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(totalAmount.setScale(2, RoundingMode.HALF_EVEN)) == -1) {
									return blanceAvailabilty = "U";
								}else if(totalAmount.setScale(2, RoundingMode.HALF_EVEN).compareTo(Value.setScale(2, RoundingMode.HALF_EVEN)) == -1) {
									return blanceAvailabilty = "U";
								}
							}
							
						}else {
							if(Retrie.retrieveAccData(BytesToFro.convertByteArrayToString(toaddress)) == null){
								return blanceAvailabilty = "U";
							}else {
								BigDecimal accCoinBalance = fromAcc.getCoinBalance();
								if(accCoinBalance != null) {
									if( accCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(totalAmount.setScale(2, RoundingMode.HALF_EVEN)) >= 0 ) {
										return blanceAvailabilty = "A";
									}else if( accCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(totalAmount.setScale(2, RoundingMode.HALF_EVEN)) == -1) {
										return blanceAvailabilty = "U";
									}else if(totalAmount.setScale(2, RoundingMode.HALF_EVEN).compareTo(Value.setScale(2, RoundingMode.HALF_EVEN)) == -1) {
										return blanceAvailabilty = "U";
									}
								}
							}
						}
					}else {
						return blanceAvailabilty = "U";
					}
					
				}else {
					return blanceAvailabilty = "A";
				}
			}
	   	}
		return blanceAvailabilty;
	}
	

	public static void clearCoinTx(List<Ctx> TxList ) {
		Runnable run = new Runnable() {
	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Ctx tx :  Holder.memTrancHolder) {
				   if( Holder.memTrancHolder.contains(tx)) {
					   Holder.memTrancHolder.remove(tx);  
				   }
				   
				}
			}
			
		};run.run();
			
	}



}
