package wallets.addresses;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import crypto.Base58;

public class PackTradeAddress {
	private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String ALGORITHM = "AES";
 
	
	  public static String toHexString(byte[] input, int offset, int length) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = offset; i < offset + length; i++) {
			stringBuilder.append(String.format("%02X", input[i] & 0xFF));
			}

				return stringBuilder.toString();
	  }
	    
	 public static String createTradeAddress(String issuerMintAddress, String CoinAddress) {
	        try {
	        	String strToEncrypt = CoinAddress;
	            prepareSecreteKey(issuerMintAddress);
	            Cipher cipher = Cipher.getInstance(ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	            byte[]array = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
	            String trade = Base58.encode(array).substring(Base58.encode(array).length() - 30);
	            return trade;
	            //return Base64.getEncoder().encodeToString();
	        } catch (Exception e) {
	            System.out.println("Error while encrypting: " + e.toString());
	        }
	        return null;
	  }
	 
	 public static String createFreePackAddress(String issuerMintAddress, String CoinAddress) {
	        try {
	        	String strToEncrypt = CoinAddress;
	            prepareSecreteKey(issuerMintAddress);
	            Cipher cipher = Cipher.getInstance(ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	            byte[]array = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
	            String trade = Base58.encode(array).substring(Base58.encode(array).length() - 30);
	           String freeTradeAddr =  toHex(trade);
	            return freeTradeAddr.substring(freeTradeAddr.length()-20);
	            //return Base64.getEncoder().encodeToString();
	        } catch (Exception e) {
	            System.out.println("Error while encrypting: " + e.toString());
	        }
	        return null;
	    }
	 
	 public static String toHex(String arg) throws UnsupportedEncodingException {
		    return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
     }
	 
	 
	  public static void prepareSecreteKey(String myKey) {
	        MessageDigest sha = null;
	        try {
	            key = myKey.getBytes(StandardCharsets.UTF_8);
	            sha = MessageDigest.getInstance("SHA-1");
	            key = sha.digest(key);
	            key = Arrays.copyOf(key, 16);
	            secretKey = new SecretKeySpec(key, ALGORITHM);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	    }
	    
	 
}
