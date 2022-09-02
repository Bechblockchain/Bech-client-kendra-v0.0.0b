package wallets.addresses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

import org.bouncycastle.jcajce.provider.digest.Keccak;

import crypto.Base58;
import crypto.Ripemd160;

public class PackMintAddress {

	

	public static String createPackMintAddress(PublicKey publicKey) throws NoSuchAlgorithmException {
		ECPublicKey epub = (ECPublicKey) publicKey;
		ECPoint pt = epub.getW();
		byte[] bcPub = new byte[25];
		bcPub[0] = 2;
		System.arraycopy(pt.getAffineX().toByteArray(), 0, bcPub, 1, 24);

		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] s1 = sha.digest(bcPub);

		byte[] ripeMD = Ripemd160.getHash(s1);

		//add 0x00
		byte[] ripeMDPadded = new byte[ripeMD.length + 1];
		ripeMDPadded[0] = 0;

		System.arraycopy(ripeMD, 0, ripeMDPadded, 1, 1);

		byte[] shaFinal = sha.digest(sha.digest(ripeMDPadded));

		//append ripeMDPadded + shaFinal = sumBytes
		byte[] sumBytes = new byte[25];
		System.arraycopy(ripeMDPadded, 0, sumBytes, 0, 21);
		System.arraycopy(shaFinal, 0, sumBytes, 21, 4);

		//base 58 encode
		String mintAddress = Base58.encode(sumBytes);
		
		return mintAddress;
	}
	
	
}
