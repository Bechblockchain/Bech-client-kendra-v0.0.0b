package cli;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.util.HashMap;

import connect.Network;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Static;
import wallets.Keys;
import wallets.addresses.CoinAddress;
import wallets.addresses.PackMintAddress;
import wallets.crypto.Hasher;
import wallets.db.Str;
import wallets.mod.Acc_obj;

@Command( name = "createAcc", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\naccount creation class")
public class Account implements Runnable {

	@Option(names = { "-c", "--create" }, required = true,description = "create new account...add 'native' to set as staking account")
	String create;
	
	@Option(names = { "-n", "--native" }, description = "set account as staking account 'native'")
	String param;
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Account()).execute(args);
	}
	
	public void createAccount() {
		
		try {
			 PublicKey publicKey = Keys.GenerateKeys();
			 
			 String pubKeyString = Hasher.returnPublicKeyString(publicKey);
			 String coinAddress = CoinAddress.createCoinAddress(publicKey);
			 String packMintAddress = PackMintAddress.createPackMintAddress(publicKey);
			 long ptxNonce = 0;
			 long ctxNonce = 0;
			 HashMap<String,BigInteger> tradeAddresses = new HashMap<String,BigInteger>(); 
			 
			 Acc_obj account = new Acc_obj(coinAddress,packMintAddress,ctxNonce,ptxNonce,new BigDecimal(0.00).setScale(2,RoundingMode.HALF_EVEN),publicKey,tradeAddresses);
			 
			 Str.storeSingleAccData(account);
			 Str.storeCoinAddress(coinAddress);
			 if(param.equals("native")) {
				 Str.storeNativeValidatorAddress(coinAddress);
				 Static.NATIVE_VALIDATOR_ADDRESS = coinAddress;
			 }
			 Network.broadcastNewAcount(account);
			 
			 System.out.println(" Coin Address- " + coinAddress + " COPY");
			 System.out.println(" Mint Address- " + packMintAddress + " COPY");
			 System.out.println(" Public Key- " + pubKeyString);
			 System.out.println(" Account Balnce- " + new BigDecimal(0.00).setScale(2,RoundingMode.HALF_EVEN) + "\n");
			
			 
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		createAccount();
	}

	
	
}
