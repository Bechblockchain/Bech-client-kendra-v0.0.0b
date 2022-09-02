package wallets.crypto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Hasher {

	
	public static String returnPrivateKeyString(PrivateKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	
	public static String returnPublicKeyString(PublicKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static PrivateKey getPrivateKeyfromByteArray(byte[] key) {
		PrivateKey privkey = null;
		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("ECDSA","BC");
			privkey = kf.generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return privkey;
	}
	
	
	public static PublicKey getPublicKeyfromByteArray(byte[] key) {
		PublicKey pubkey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance("ECDSA","BC");
			pubkey = kf.generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pubkey;
		
	}

	
}
