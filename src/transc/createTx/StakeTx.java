package transc.createTx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import crypto.BytesToFro;
import temp.Static;
import transc.BlancChk;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class StakeTx {

	
	public static Ctx createStakeTx(String FromCoinAddress, BigDecimal value,String Range) throws IOException  {
		String ToCoinAddress = Static.STAKE_OBJ;
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		
		
		Acc_obj accData = null;
		if(FromCoinAddress.equals("native")) {
			FromCoinAddress = Retrie.retrieveNativeValidatorAddress();
		}
		
		 accData = Retrie.retrieveAccData(FromCoinAddress);
		
		PublicKey senderPublicKey = accData.getPubkey();
	    String coinAddress = accData.getCoinAddress();
	    long accNonce = accData.getCtxNonce();
     	long ptxNonce = accData.getPtxNonce();
     	BigDecimal coinBalance = accData.getCoinBalance();
     	String mintAddress = accData.getMintAddress();
     	HashMap<String,BigInteger> tradesNbalances = accData.getTradesNbalances();
     	
     	byte[] ValueBytes = BytesToFro.convertBigDecimalToByteArray(value);
     	byte[] FromCoinAddressBytes = BytesToFro.convertStringToByteArray(FromCoinAddress);
    	byte[] ToCoinAddressBytes = BytesToFro.convertStringToByteArray(ToCoinAddress);
     	
     	PrivateKey senderPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(senderPublicKey);
     	
		String balanceCheck = BlancChk.chkblance(ValueBytes, BytesToFro.convertStringToByteArray(Range), FromCoinAddressBytes, ToCoinAddressBytes);
		
		
		if(balanceCheck.equals("A")) {
			
			long newNonce  = accNonce + 1; 
			coinAddress = accData.getCoinAddress();
     
			Acc_obj Account = new Acc_obj(coinAddress,mintAddress,newNonce,ptxNonce,coinBalance,senderPublicKey,tradesNbalances);
					Str.storeSingleAccData(Account);
			
			byte[] TxSig = Hasher.generateSenderCoinSignature(senderPrivateKey, FromCoinAddress, ToCoinAddress, senderPublicKey, value, newNonce, timestamp, Range);
			
			byte[] senderAddress = BytesToFro.convertStringToByteArray(FromCoinAddress);
			byte[] receiverAddress = BytesToFro.convertStringToByteArray(ToCoinAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] txNonce = BytesToFro.convertLongToBytes(newNonce);
			
			byte[] range = BytesToFro.convertStringToByteArray(Range);
			
			
			transaction = new Ctx(senderAddress, receiverAddress, ValueBytes, txtimestamp, txNonce, TxSig, range);
			
		}else if(balanceCheck.equals("U")) {
			System.out.println("\nNot Enough Balance to Execute this Transaction"); 
			System.out.println("Account Blanace- " + coinBalance + "\nSending Amount- " + value + "\nFee- " + Static.FEE);
		}else if(balanceCheck.equals("N")) {
			System.out.println("\nAccount does not exist"); 
		}
		return transaction;
		
	}	

	
}
