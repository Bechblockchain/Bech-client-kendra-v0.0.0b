package threads;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import connect.Network;
import temp.Static;
import wallets.Keys;
import wallets.addresses.CoinAddress;
import wallets.addresses.PackMintAddress;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class ValidatorAccountThread implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if(Static.EPOCH_VALIDATOR_ADDRESS != Static.NATIVE_VALIDATOR_ADDRESS) {
				PublicKey publicKey = Keys.GenerateKeys();
				 
		         PrivateKey privateKey = Retrie.retrievePrivateKeyWithPublicKey(publicKey);
		         Str.storePubPrivKey(publicKey, privateKey);
		         
				 String coinAddress = CoinAddress.createCoinAddress(publicKey);
				 String packMintAddress = PackMintAddress.createPackMintAddress(publicKey);
				 long ptxNonce = 0;
				 long ctxNonce = 0;
				 HashMap<String,BigInteger> tradeAddresses = new HashMap<String,BigInteger>(); 
				 
				 Acc_obj account = new Acc_obj(coinAddress,packMintAddress,ctxNonce,ptxNonce,new BigDecimal(0.00).setScale(2,RoundingMode.HALF_EVEN),publicKey,tradeAddresses);
				 
				
				 Str.storeSingleAccData(account);
				 Str.storeNativeValidatorAddress(coinAddress);
				 Str.storeCoinAddress(coinAddress);
				 
				 Network.broadcastNewAcount(account);
				 Static.NATIVE_VALIDATOR_ADDRESS = coinAddress;
				 System.out.println("Native Staking Account Coin Address: " + coinAddress + " COPY");
				 System.out.println("Native Staking Account Pack Mint Address: " + packMintAddress + " COPY" + "\n");
				 
			}else {
				 System.out.println("Staking account is in use!..try again after current epoch");
			}
			 
			 
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidAlgorithmParameterException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
