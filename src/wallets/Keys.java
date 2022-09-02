package wallets;

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
import wallets.db.Str;

public class Keys {
	public static PrivateKey privateKey;
	public static PublicKey publicKey;
	
	public static PublicKey GenerateKeys() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
		keyGen.initialize(ecSpec, random);
	    	KeyPair keyPair = keyGen.generateKeyPair();
	    	privateKey = keyPair.getPrivate();
	    	publicKey = keyPair.getPublic();
	    	
	    	Str.storePubPrivKey(publicKey, privateKey);
	    	return publicKey;
	    	
	}

	
}
