package transc.mod;

import java.io.Serializable;

public class Ptx implements Serializable{
	
	public byte[] sellerCoinAddress, buyerCoinAddress; // Recipients address/public key.
	public byte[] packs,value,type;
	public byte[] signature,timestamp,PkNonce,minter,stat,data; // this is to prevent anybody else from spending funds in our wallet.
	
	// Constructor: 
	public Ptx(byte[] sellerCoinAddress, byte[] buyerCoinAddress, byte[]minter, byte[] packs, byte[] value, byte[] signature,byte[] PkNonce,byte[] timestamp,byte[] type) {
		this.sellerCoinAddress = sellerCoinAddress;
		this.buyerCoinAddress = buyerCoinAddress;
		this.minter = minter;
		this.packs = packs;
		this.value = value;
		this.signature = signature;
		this.PkNonce = PkNonce;
		this.timestamp = timestamp;
		this.type = type;
	}
	
	public Ptx(byte[] sellerCoinAddress, byte[]minter, byte[] packs, byte[] value, byte[] signature,byte[] PkNonce,byte[] timestamp,byte[] type) {
		this.sellerCoinAddress = sellerCoinAddress;
		this.minter = minter;
		this.packs = packs;
		this.value = value;
		this.signature = signature;
		this.PkNonce = PkNonce;
		this.timestamp = timestamp;
		this.type = type;
	}
	
	public Ptx(byte[] buyerCoinAddress, byte[]minter, byte[] packs, byte[] value, byte[] signature,byte[] PkNonce,byte[] timestamp,byte[] stat,byte[] data,byte[] type) {
		this.buyerCoinAddress = buyerCoinAddress;
		this.minter = minter;
		this.packs = packs;
		this.value = value;
		this.signature = signature;
		this.PkNonce = PkNonce;
		this.timestamp = timestamp;
		this.type = type;
		this.stat = stat;
		this.data = data;
	}
	
	public byte[] getFromCoinAddress() {
		return sellerCoinAddress;
	}
	
	public byte[] getToCoinAddress() {
		return buyerCoinAddress;
	}
	
	public byte[] getPacks() {
		return packs;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	public byte[] getTimestamp() {
		return timestamp;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public byte[] getPkNonce() {
		return PkNonce;
	}
	
	public byte[] getMinter() {
		return minter;
	}
	public byte[] getType() {
		return type;
	}
	
	
}
