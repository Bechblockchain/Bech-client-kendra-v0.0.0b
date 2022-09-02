package transc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;

import SEWS_Protocol.StakeObj;
import SEWS_Protocol.db.retrieve;
import crypto.BytesToFro;
import temp.Static;
import vault.db.vault_retrie;
import wallets.addresses.PackTradeAddress;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class BlancChk {
	
	public static String chkblance(byte[] value, byte[] range, byte[] fromaddress, byte[] toaddress) throws IOException  {
		BigDecimal fee = Static.FEE;
		
		String blanceAvailabilty = null;
		if(BytesToFro.convertByteArrayToString(range).equals(Static.TYPE_UNSTAKE)) {
			StakeObj stakeObj = retrieve.retrieveSingleStakeData(BytesToFro.convertByteArrayToString(fromaddress));
			if(stakeObj != null) {
				BigDecimal stakedCoinBalance = stakeObj.getStakeCoins().add(fee);
				
				if(stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(BytesToFro.convertBytesToBigDecimal(value).setScale(2, RoundingMode.HALF_EVEN)) >= 0 ) {
					blanceAvailabilty = "A";
				}else if(stakedCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(BytesToFro.convertBytesToBigDecimal(value).setScale(2, RoundingMode.HALF_EVEN)) == -1) {
					blanceAvailabilty = "U";
				}
			}
			
		}else {
			BigDecimal totalAmount= BytesToFro.convertBytesToBigDecimal(value).add( fee);
			BigDecimal Value = BytesToFro.convertBytesToBigDecimal(value);
			
			BigDecimal accCoinBalance = null;
			if(BytesToFro.convertByteArrayToString(fromaddress).equals(Static.MAINTENANCE_VAULT)) {
				accCoinBalance = vault_retrie.retrieveMaintenanceVaultData().getVaultCoins();
		   	 }else if(BytesToFro.convertByteArrayToString(fromaddress).equals(Static.BACKUP_VAULT)) {
		   		accCoinBalance = vault_retrie.retrieveBackUpVaultData().getVaultCoins();
		   	 }else {
		   		 
		   		String from = BytesToFro.convertByteArrayToString(fromaddress);
				String To =  BytesToFro.convertByteArrayToString(toaddress);
				
					if(from != Static.VAL_REWARD_TX && from != Static.TOP_50_REWARD ) {
						Acc_obj fromAcc =  Retrie.retrieveAccData(BytesToFro.convertByteArrayToString(fromaddress));
						
						if(fromAcc != null) {
							if(To.equals(Static.STAKE_OBJ)) {
								 accCoinBalance = fromAcc.getCoinBalance();
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
								accCoinBalance = fromAcc.getCoinBalance();
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
						}else {
							return blanceAvailabilty = "N";
						}
						
					}
		   	 }
		}
		return blanceAvailabilty;
	}
	
	
	public static String chkSellerPacksbalance(String sellerCoinAddress, String minterAddress, BigInteger packs, HashMap<String,BigInteger> freePackBalances) {
		String blanceAvailabilty = null;
		
		String senderTradeAddress = PackTradeAddress.createTradeAddress(minterAddress,sellerCoinAddress);
     	BigInteger accSenderPacks = freePackBalances.get(senderTradeAddress);
		BigInteger totalSellerpacksLeft = accSenderPacks.subtract(packs);
	
     	if(totalSellerpacksLeft.compareTo(packs) >= 0 ) {
			blanceAvailabilty = "PA";
		}else if(totalSellerpacksLeft.compareTo(packs) == -1 ) {
			blanceAvailabilty = "PU";
		}
		return blanceAvailabilty;
	}
	
	
	public static String chkBuyerCoinbalance(String buyerCoinAddress, String minterAddress, BigDecimal receiverCoinBalance, BigDecimal value, HashMap<String,BigInteger> rFreePackBalances) {
		String blanceAvailabilty = null;
		
     	BigDecimal receiverNewCoinBalance = receiverCoinBalance.setScale(2, RoundingMode.HALF_EVEN).subtract(value.setScale(2, RoundingMode.HALF_EVEN));
		
     	 if(receiverNewCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(value.setScale(2, RoundingMode.HALF_EVEN)) >= 0) {
			blanceAvailabilty = "CA";
		}else if(receiverNewCoinBalance.setScale(2, RoundingMode.HALF_EVEN).compareTo(value.setScale(2, RoundingMode.HALF_EVEN)) == -1) {
			blanceAvailabilty = "CU";
		}
		return blanceAvailabilty;
	}
	
	
}
