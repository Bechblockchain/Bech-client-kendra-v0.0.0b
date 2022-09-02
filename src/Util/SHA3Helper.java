package Util;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.util.encoders.Hex;

public class SHA3Helper {
	
	private static int DEFAULT_SIZE = 256;
	
	public static byte[] sha3(String message) {
        return sha3(Hex.decode(message), new SHA3Digest(DEFAULT_SIZE), true);
    }

    public static byte[] sha3(byte[] message) {
        return sha3(message, new SHA3Digest(DEFAULT_SIZE), true);
    }
    
    
    private static byte[] sha3(byte[] message, SHA3Digest digest, boolean bouncyencoder) {
        return doSha3(message, digest, bouncyencoder);
    }
	
    private static byte[] doSha3(byte[] message, SHA3Digest digest, boolean bouncyencoder) {
        byte[] hash = new byte[digest.getDigestSize()];

        if (message.length != 0) {
            digest.update(message, 0, message.length);
        }
        digest.doFinal(hash, 0);
        return hash;
    }
    
}
