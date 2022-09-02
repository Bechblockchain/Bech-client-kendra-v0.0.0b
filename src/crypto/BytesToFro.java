package crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import Blocks.mod.Block_obj;
import KryoMod.KryoSeDe;
import SEWS_Protocol.weightResults;
import penalty.Report;
import transc.mod.Ctx;
import transc.mod.Ptx;
import wallets.mod.Acc_obj;

public class BytesToFro {
	
	static Kryo kryo;
	
	public static byte[] convertBigDecimalToByteArray(BigDecimal digDec) throws IOException {
	 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(bos);
	    dos.writeInt(digDec.scale());
	    dos.write(digDec.unscaledValue().toByteArray());
	    dos.close(); // flush
	    byte[] array = bos.toByteArray();
	 
        return array;
	}
	 
	/////////////////////////////////////////////////////
    public static String bytesToHex(byte[] bytes) {
    	StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            hexStringBuffer.append(byteToHex(bytes[i]));
        }
        return hexStringBuffer.toString();
	}
	 
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
    
    /////////////////////////////////////////////////////
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
              "Invalid hexadecimal String supplied.");
        }
        
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
        	
        	  int firstDigit = toDigit(hexString.substring(i, i + 2).charAt(0));
              int secondDigit = toDigit(hexString.substring(i, i + 2).charAt(1));
             
            bytes[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
        }
        return bytes;
    }
    
    
    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
              "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
    
	public static byte[] convertStringToByteArray(String string) {
		byte[] byteArrray = null;
		if(string != null) {
			Charset charset = StandardCharsets.UTF_8;
			byteArrray = string.getBytes(charset);
		}
				
		return byteArrray;
	}
	
	public static String convertByteArrayToString(byte[] bytesArray) {
		String string = null;
		if(bytesArray != null) {
			 Charset charset = StandardCharsets.UTF_8;
			  string = new String(bytesArray, charset);
		}
		 	
	  return string;
	}
	
	  //long to bytes
    public static byte[] convertLongToBytes(long l) {
	    byte[] result = new byte[8];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}

	  
	  //bytes to long
	public static long convertBytesToLong(final byte[] b) {
	    long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	  
	  
	  public static byte[] convertObjToByteArray(weightResults results) throws IOException {
		  byte[] array  = null;
		  if(results != null) {  
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    Output output = new Output(byteArrayOutputStream);
		    KryoSeDe.kryo.writeClassAndObject(output, results);
		    output.flush();
		    output.close();
		    array = byteArrayOutputStream.toByteArray();
		  }
		    return array;
	  }

	  public static byte[] convertObjToByteArray(Block_obj results) throws IOException {
		  byte[] array  = null;
		  if(results != null) {  
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    Output output = new Output(byteArrayOutputStream);
		    KryoSeDe.kryo.writeClassAndObject(output, results);
		    output.flush();
		    output.close();
		    array = byteArrayOutputStream.toByteArray();
		  }
		    return array;
	  }
	  
	  
	  public static byte[] convertObjToByteArray(Ctx ctx) throws IOException {
		  byte[] array  = null;
		  if(ctx != null) {
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    Output output = new Output(byteArrayOutputStream);
		    KryoSeDe.kryo.writeClassAndObject(output, ctx);
		    output.flush();
		    output.close();
		    array = byteArrayOutputStream.toByteArray();
		  }
		    return array;
	  }
	  
	 
	  
	  public static byte[] convertObjToByteArray(Report report) throws IOException {
		  byte[] array  = null;
		  if(report != null) {
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    Output output = new Output(byteArrayOutputStream);
		    KryoSeDe.kryo.writeClassAndObject(output, report);
		    output.flush();
		    output.close();
		    array = byteArrayOutputStream.toByteArray();
		  }
		    return array;
	  }
	   
	  
	  public static byte[] convertObjToByteArray(Ptx ptx) throws IOException {
		  byte[] array  = null;
		  if(ptx != null) {
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    Output output = new Output(byteArrayOutputStream);
		    KryoSeDe.kryo.writeClassAndObject(output, ptx);
		    output.flush();
		    output.close();
		    array =  byteArrayOutputStream.toByteArray();
		   
		  }
		  return array;
	  }
	  
	  /////////////////////////////////////////////////////////////////  
	  
	  public static Report convertByteArrayToReport(byte[] report) throws IOException, ClassNotFoundException {
		  Report object = null;
		  if(report != null) { 
		  ByteArrayInputStream inputStream = new ByteArrayInputStream(report);
		    Input input = new Input(inputStream);
		    object = (Report)KryoSeDe.kryo.readClassAndObject(input);
		    input.close();
		  }
			return object;
	  }
	   
	  
	  public static Ctx convertByteArrayToCtx(byte[] ctx) throws IOException, ClassNotFoundException {
		  Ctx object = null;
		  if(ctx != null) {
		  ByteArrayInputStream inputStream = new ByteArrayInputStream(ctx);
		    Input input = new Input(inputStream);
		    object = (Ctx)KryoSeDe.kryo.readClassAndObject(input);
		    input.close();
		  }
			return object;
	  }
	 
	  
	
	 public static BigDecimal convertBytesToBigDecimal(byte[] array) throws IOException {
		 BigDecimal bigDecimal = null;
		  if(array != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(array);
		    DataInputStream dis = new DataInputStream(bis);
		    int sc = dis.readInt(); //grab 4 bytes
		    BigInteger unscaledVal = new BigInteger(Arrays.copyOfRange(array, 4, array.length));
		    bigDecimal = new BigDecimal(unscaledVal, sc);
		  }
	        return bigDecimal;
	 }

	
}
