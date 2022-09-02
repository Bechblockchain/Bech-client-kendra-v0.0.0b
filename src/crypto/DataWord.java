package crypto;

public class DataWord implements Comparable<DataWord> {
	 private byte[] data = new byte[32];

	 @Override
	    public int compareTo(DataWord o) {
	        if (o == null || o.getData() == null) return -1;
	        int result = FastByteComparisons.compareTo(
	                data, 0, data.length,
	                o.getData(), 0, o.getData().length);
	        // Convert result into -1, 0 or 1 as is the convention
	        return (int) Math.signum(result);
	    }
	 
	 public byte[] getData() {
	        return data;
	 }
	 
	 public DataWord(byte[] data) {
			if (data == null)
				this.data = ByteUtil.EMPTY_BYTE_ARRAY;
			else if (data.length <= 32)
				System.arraycopy(data, 0, this.data, 32 - data.length, data.length);
			else
				throw new RuntimeException("Data word can't exit 32 bytes: " + data);       	
		}

}
