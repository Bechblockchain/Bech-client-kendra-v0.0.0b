package crypto;

public class ByteUtil {

	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	  public static boolean increment(byte[] bytes) {
	        final int startIndex = 0;
	        int i;
	        for (i = bytes.length-1; i >= startIndex; i--) {
	            bytes[i]++;
	            if (bytes[i] != 0)
	                break;
	        }
	        // we return false when all bytes are 0 again
	        return (i >= startIndex || bytes[startIndex] != 0);
	    }
	
}
