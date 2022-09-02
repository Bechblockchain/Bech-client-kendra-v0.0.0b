package wallets.addresses;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import org.bouncycastle.jcajce.provider.digest.Keccak;
public class CoinAddress {

	public static String createCoinAddress(PublicKey publicKey) throws NoSuchAlgorithmException {
		byte[] publicKeyBytes = publicKey.getEncoded();
    	String hashedPublicKey = getAddress(publicKeyBytes);
    	//Take only last 50 characters of hash as the address
    	String coinAddress = hashedPublicKey.substring(hashedPublicKey.length() - 50);
		return coinAddress;
	}
	
	private static String getAddress(byte[] publicKey) {
		//Use Keccak256 hash on public key
		Keccak.DigestKeccak keccak = new Keccak.Digest256();
		keccak.update(publicKey);
		return toHexString(keccak.digest(), 0, keccak.digest().length);
	}
	
	public static String toHexString(byte[] input, int offset, int length) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = offset; i < offset + length; i++) {
		stringBuilder.append(String.format("%02x", input[i] & 0xFF));
		}
			return stringBuilder.toString();
	}
	
	
		
	
}
