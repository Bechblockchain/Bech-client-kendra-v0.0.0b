package penalty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;

import SEWS_Protocol.StakeObj;
import SEWS_Protocol.db.retrieve;
import SEWS_Protocol.db.store;
import temp.Holder;
import temp.Static;
import vault.Vault_obj;
import vault.db.vault_retrie;
import vault.db.vault_store;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class Penalty {
	
	//Validator looses all their stake and reward
	public static void Korust(Report report,BigDecimal confiscatedRewards) {
		
			//felon punishment data
			thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(report.getSuspectAddress());
			BigDecimal allFines = punishmentData.getFines();
			long nonce = punishmentData.getPenaltyNonce();
			
			StakeObj felonStakeData = retrieve.retrieveSingleStakeData(report.getSuspectAddress());
		    String coinAddress = felonStakeData.getAddress();
			BigDecimal stakedCoins = felonStakeData.getStakeCoins();
			String lastValidationStatus = Static.LAST_VAL_STATUS_INCOMPLETE;
			long timestamp = felonStakeData.getTimestamp(); 
			long lastValidationTimestamp = felonStakeData.getLastValidationTimestamp();
			
			BigDecimal newFines = confiscatedRewards.add(allFines);
			BigDecimal vaultRatio = confiscatedRewards.multiply(new BigDecimal(0.50));
			
			BigDecimal newStakeCoins = stakedCoins.subtract(stakedCoins);
			long newNonce = nonce + 1L;	

			StakeObj newStakeObj = new StakeObj(coinAddress,newStakeCoins,timestamp,lastValidationTimestamp,lastValidationStatus);
			store.storeSingleStakeData(newStakeObj);
			
			thePunisher punishment = new thePunisher("the_punisher",report.getSuspectAddress(),newFines,report,newNonce);
			thePunisherAcc.storePunishmentData(punishment);
			
			
			Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
			BigDecimal vaultCoins = vault.getVaultCoins();
			PublicKey publicKey = vault.getPublicKey();
			long vaultNonce = vault.getNonce();
			
			BigDecimal newVaultCoins = vaultCoins.add(stakedCoins).add(vaultRatio);
			
			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,newVaultCoins,publicKey,vaultNonce));
			
			Holder.receivedReports.clear();
			
		
	}
	
	//Validator looses all the reward
	public static void Vanity(Report report,BigDecimal confiscatedRewards) {
		
			StakeObj felonStakeData = retrieve.retrieveSingleStakeData(report.getSuspectAddress());
		    String coinAddress = felonStakeData.getAddress();
			BigDecimal stakedCoins = felonStakeData.getStakeCoins();
			String lastValidationStatus = Static.LAST_VAL_STATUS_INCOMPLETE;
			long timestamp = felonStakeData.getTimestamp(); 
			long lastValidationTimestamp = felonStakeData.getLastValidationTimestamp();
			
			StakeObj newStakeObj = new StakeObj(coinAddress,stakedCoins,timestamp,lastValidationTimestamp,lastValidationStatus);
			store.storeSingleStakeData(newStakeObj);

			thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(report.getSuspectAddress());
			BigDecimal allFines = punishmentData.getFines();
			long nonce = punishmentData.getPenaltyNonce();
			
			BigDecimal newFines = confiscatedRewards.add(allFines);
			BigDecimal vaultRatio = confiscatedRewards.multiply(new BigDecimal(0.50));
			
			long newNonce = nonce + 1L;	
			thePunisher punishment = new thePunisher("the_punisher",report.getSuspectAddress(),newFines,report,newNonce);
			thePunisherAcc.storePunishmentData(punishment);
			
			
			Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
			BigDecimal vaultCoins = vault.getVaultCoins();
			PublicKey publicKey = vault.getPublicKey();
			long vaultNonce = vault.getNonce();
			
			BigDecimal newVaultCoins = vaultCoins.add(vaultRatio);
			
			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,newVaultCoins,publicKey,vaultNonce));
			
			Holder.receivedReports.clear();
		
	}
	
	//Validator looses all their coins in account and is banned for life
	public static void Gehenna(Report report,BigDecimal confiscatedRewards) {
			//felon punishment data
			thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(report.getSuspectAddress());
			BigDecimal allFines = punishmentData.getFines();
			long nonce = punishmentData.getPenaltyNonce();
			
			StakeObj felonStakeData = retrieve.retrieveSingleStakeData(report.getSuspectAddress());
			    String coinAddress = felonStakeData.getAddress();
				BigDecimal stakedCoins = felonStakeData.getStakeCoins();
				String lastValidationStatus = Static.LAST_VAL_STATUS_INCOMPLETE;
				long timestamp = felonStakeData.getTimestamp(); 
				long lastValidationTimestamp = felonStakeData.getLastValidationTimestamp();
				
			
			BigDecimal newFines = confiscatedRewards.add(allFines);
			BigDecimal vaultRatio = confiscatedRewards.multiply(new BigDecimal(0.50));
			
			BigDecimal newStakeCoins = stakedCoins.subtract(stakedCoins);
			long newNonce = nonce + 1L;	
			
			Acc_obj accFelonData = Retrie.retrieveAccData(coinAddress);
			
			BigDecimal accCoins = accFelonData.getCoinBalance();
			String mintAddress = accFelonData.getMintAddress();
			BigDecimal coinBalance = accFelonData.getCoinBalance();
			BigDecimal newCoinBalance = coinBalance.subtract(coinBalance);
			PublicKey pubkey = accFelonData.getPubkey();
			long ctxNonce = 0;
			long ptxNonce = 0;
			HashMap<String,BigInteger> tradesNbalances = accFelonData.getTradesNbalances();
			tradesNbalances.clear();
			Acc_obj newAccount = new Acc_obj(coinAddress,mintAddress,ctxNonce,ptxNonce,newCoinBalance,pubkey,tradesNbalances);
			Str.storeSingleAccData(newAccount);

			StakeObj newStakeObj = new StakeObj(coinAddress,newStakeCoins,timestamp,lastValidationTimestamp,lastValidationStatus);
			store.storeSingleStakeData(newStakeObj);
			thePunisher punishment = new thePunisher("the_punisher",report.getSuspectAddress(),newFines,report,newNonce);
			thePunisherAcc.storePunishmentData(punishment);
			
			BigDecimal seizedCoins = accCoins.add(stakedCoins).add(vaultRatio);
			
			Vault_obj vault = vault_retrie.retrieveMaintenanceVaultData();
			BigDecimal vaultCoins = vault.getVaultCoins();
			PublicKey publicKey = vault.getPublicKey();
			long vaultNonce = vault.getNonce();
			
			BigDecimal newVaultCoins = vaultCoins.add(seizedCoins);
			
			vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,newVaultCoins,publicKey,vaultNonce));
			
			
			Holder.gehennaList.add(report.getSuspectAddress());
			thePunisherAcc.storeGehennaData(report);
			Holder.receivedReports.clear();
		
	}
	
}
