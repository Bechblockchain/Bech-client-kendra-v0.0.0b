package test;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import temp.Static;
import wallets.crypto.Hasher;

@Command( name = "test", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "chain test")
public class KeyGen implements Runnable{

	public static PrivateKey privateKey;
	public static PublicKey publicKey;
	
	public static void createAccount() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
	
		keyGen.initialize(ecSpec, random);   
	    	KeyPair keyPair = keyGen.generateKeyPair();
	    	
	    	privateKey = keyPair.getPrivate();
	    	publicKey = keyPair.getPublic();
	    	
	    	System.out.println(" TEST PRIVKEY STRING:  " + Hasher.returnPrivateKeyString(privateKey));
	    	
	    	System.out.println(" TEST PUBKEY STRING:  " + Hasher.returnPublicKeyString(publicKey) + "\n");
	    	long core = Runtime.getRuntime().availableProcessors();
	    	if( Runtime.getRuntime().availableProcessors() < 12) {
	    		System.out.println("Your current CPU core of " + core + " is below requirement... upgrage to at least 12 cores");
	    	}
			
	        
	}

	
	 public static void main(String[] args) throws Exception {
		 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		 new CommandLine(new KeyGen()).execute(args);
	 }


	@Override
	public void run() {
		// TODO Auto-generated method stub
		 try {
			createAccount();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
