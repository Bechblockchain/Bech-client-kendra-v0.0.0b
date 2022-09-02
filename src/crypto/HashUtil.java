package crypto;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.util.encoders.Hex;

import Util.ByteArrayWrapper;
import Util.LRUMap;
import Util.SHA3Helper;

public class HashUtil {
	
	private static final int MAX_ENTRIES = 100;
	private static int DEFAULT_SIZE = 256;
	private static LRUMap<ByteArrayWrapper, byte[]> sha3Cache = new LRUMap<>(0, MAX_ENTRIES);
	
	public static byte[] sha3(byte[] input) {
        ByteArrayWrapper inputByteArray = new ByteArrayWrapper(input);
        byte[] result = sha3Cache.get(inputByteArray);
        if(result != null)
                return result;
        result = SHA3Helper.sha3(input);
        sha3Cache.put(inputByteArray, result);
        return result; 
}
	
	 
}
