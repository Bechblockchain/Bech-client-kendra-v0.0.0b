package transc.createTx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import crypto.BytesToFro;
import transc.BlancChk;
import transc.crypto.Hasher;
import transc.mod.Ptx;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class PackTx {

	
	public static Ptx createBuyPackAtSetPriceTx(String BuyerCoinAddress,String minterAddress, BigInteger packs, BigDecimal value, String Type) throws IOException {
		
		
		
		Ptx transaction = null;
		long timestamp = System.currentTimeMillis() / 1000L;
     	Acc_obj accBuyerData = Retrie.retrieveAccData(BuyerCoinAddress);
		
		PublicKey buyerPublicKey = accBuyerData.getPubkey();
	    String buyerCoinAddress = accBuyerData.getCoinAddress();
	    long buyerAccNonce = accBuyerData.getCtxNonce();
     	long buyerPtxNonce = accBuyerData.getPtxNonce();
     	BigDecimal buyerCoinBalance = accBuyerData.getCoinBalance();
     	String buyerMintAddress = accBuyerData.getMintAddress();
     	HashMap<String,BigInteger> rTradesNbalances = accBuyerData.getTradesNbalances();
     	
     	PrivateKey buyerPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(buyerPublicKey);
     		
		String CoinsAvailability = BlancChk.chkBuyerCoinbalance(BuyerCoinAddress,minterAddress,buyerCoinBalance,value,rTradesNbalances);
		
		//Check Seller Pack Balance and buyer coin balance
     	if(CoinsAvailability.equals("CA")) {
     		
			long newPtxNonce  = buyerAccNonce + 1; 
			buyerCoinAddress = accBuyerData.getCoinAddress();
		     
			Acc_obj buyerAccount = new Acc_obj(buyerCoinAddress,buyerMintAddress,buyerAccNonce,newPtxNonce,buyerCoinBalance,buyerPublicKey,rTradesNbalances);
					Str.storeSingleAccData( buyerAccount);
					
			byte[] TxSig = Hasher.generateOrderBuyerPackSignature(buyerPrivateKey, BuyerCoinAddress, packs, buyerPublicKey, value, newPtxNonce, timestamp,"stat","data",null);
			
			byte[] buyerAddress = BytesToFro.convertStringToByteArray(BuyerCoinAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] ptxNonceBytes = BytesToFro.convertLongToBytes(newPtxNonce);
			byte[] minterAddr =  BytesToFro.convertStringToByteArray(minterAddress);
			byte[] ValueBytes = BytesToFro.convertBigDecimalToByteArray(value);
			byte[] Packs = packs.toByteArray();
			byte[] type = BytesToFro.convertStringToByteArray(Type);
			byte[] stat = BytesToFro.convertStringToByteArray("stat");
			byte[] data = BytesToFro.convertStringToByteArray("data");
			
						
			transaction = new Ptx(buyerAddress, minterAddr, Packs, ValueBytes, TxSig, ptxNonceBytes, txtimestamp, stat, data, type);
     	}else if(CoinsAvailability.equals("CU")) {
     		System.out.println("Buyer has low coin balance"); 
     	}
		return transaction;
	}
	
	public static Ptx createSellPackAtSetPriceTx(String sellerCoinAddress,String minterAddress, BigDecimal value, BigInteger packs, String Type) throws IOException  {
	
		
		
		Ptx transaction = null;
		long timestamp = System.currentTimeMillis() / 1000L;
    	
		Acc_obj sellerAccData = Retrie.retrieveAccData(sellerCoinAddress);
		
		PublicKey PublicKey = sellerAccData.getPubkey();
	    String coinAddress = sellerAccData.getCoinAddress();
	    long accNonce = sellerAccData.getCtxNonce();
     	long ptxNonce = sellerAccData.getPtxNonce();
     	BigDecimal coinBalance = sellerAccData.getCoinBalance();
     	String mintAddress = sellerAccData.getMintAddress();
     	HashMap<String,BigInteger> tradesNbalances = sellerAccData.getTradesNbalances();
     	
     	PrivateKey PrivateKey = Retrie.retrievePrivateKeyWithPublicKey(PublicKey);
     	
		String PacksAvailability = BlancChk.chkSellerPacksbalance(sellerCoinAddress,minterAddress,packs,tradesNbalances);
		
		//Check Seller Pack Balance and buyer coin balance
     	if(PacksAvailability.equals("PA") ) {
     		
			long newPtxNonce  = ptxNonce + 1; 
			 
			Acc_obj buyerAccount = new Acc_obj(coinAddress,mintAddress,accNonce,newPtxNonce,coinBalance,PublicKey,tradesNbalances);
					Str.storeSingleAccData(buyerAccount);
					
			byte[] TxSig = Hasher.generateSellOrderPackSignature(PrivateKey, sellerCoinAddress, packs, PublicKey, value, newPtxNonce, timestamp,null);
			
			byte[] sellercoinAddress = BytesToFro.convertStringToByteArray(coinAddress);
			
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] ptxNonceBytes = BytesToFro.convertLongToBytes(newPtxNonce);
			byte[] minterAddr =  BytesToFro.convertStringToByteArray(minterAddress);
			byte[] ValueBytes = BytesToFro.convertBigDecimalToByteArray(value);
			byte[] Packs = packs.toByteArray();
			byte[] type = BytesToFro.convertStringToByteArray(Type);
			
						
			transaction = new Ptx(sellercoinAddress, minterAddr, Packs, ValueBytes, TxSig, ptxNonceBytes, txtimestamp, type);
     	
     	}else if(PacksAvailability.equals("PU")) {
     		System.out.println("low free pack balance"); 
     	}
		return transaction;
		
     	
	}
	
	
}
