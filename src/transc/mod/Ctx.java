package transc.mod;

import java.io.Serializable;

public class Ctx implements Serializable{
	
	byte[] FromAddress, ToAddress; // Recipients address.
	byte[] value;//double
	byte[] txSig; // this is to prevent anybody else from spending funds in our wallet.
	byte[] timestamp;//long
	byte[] nonce;//int
	byte[] report;
	byte[] epochNumber;
	byte[] epochHash;
	byte[] range;
	byte[] coinRewardAfterPartition;
	byte[] stakeRewardAfterPartition;
	byte[] stakers50Partition;
	byte[] eachStaker50Partition;
	byte[] vaultRatio;
	
	// Constructor for normal tx 
	public Ctx(byte[] FromAddress, byte[] ToAddress, byte[] value, byte[] timestamp, byte[] nonce, byte[] txSig, byte[] range) {
		this.FromAddress = FromAddress;
		this.ToAddress = ToAddress;
		this.value = value;
		this.timestamp = timestamp;
		this.txSig = txSig;
		this.nonce = nonce;
		this.range = range;
		
	}
	
	//Constructor for penalty tx
	public Ctx(byte[] FromAddress, byte[] ToAddress, byte[] value, byte[] timestamp, byte[] nonce, byte[] txSig, byte[] report, byte[] range) {
		this.FromAddress = FromAddress;
		this.ToAddress = ToAddress;
		this.value = value;
		this.timestamp = timestamp;
		this.txSig = txSig;
		this.nonce = nonce;
		this.report = report;
		this.range = range;
		
	}
	
	//Constructor for validator reward tx
	public Ctx(byte[] FromAddress, byte[] ToAddress, byte[] value,byte[] coinRewardAfterPartition,byte[] stakeRewardAfterPartition,byte[] vaultRatio, byte[] timestamp, byte[] nonce, byte[] txSig, byte[] epochNumber, byte[] epochHash, byte[] range) {
		this.FromAddress = FromAddress;
		this.ToAddress = ToAddress;
		this.value = value;
		this.coinRewardAfterPartition = coinRewardAfterPartition;
		this.stakeRewardAfterPartition = stakeRewardAfterPartition;
		this.vaultRatio = vaultRatio;
		this.timestamp = timestamp;
		this.txSig = txSig;
		this.nonce = nonce;
		this.epochNumber = epochNumber;
		this.epochHash = epochHash;
		this.range = range;
		
	}
	
	//constructor for top 50% stakers
	public Ctx(byte[] FromAddress, byte[] ToAddress, byte[] value,byte[] coinRewardAfterPartition,byte[] stakeRewardAfterPartition,byte[] vaultRatio,byte[] stakers50Partition,byte[] eachStaker50Partition, byte[] timestamp, byte[] nonce, byte[] txSig, byte[] epochNumber, byte[] epochHash, byte[] range) {
		this.FromAddress = FromAddress;
		this.ToAddress = ToAddress;
		this.value = value;
		this.coinRewardAfterPartition = coinRewardAfterPartition;
		this.stakeRewardAfterPartition = stakeRewardAfterPartition;
		this.vaultRatio = vaultRatio;
		this.stakers50Partition = stakers50Partition;
		this.eachStaker50Partition = eachStaker50Partition;
		this.timestamp = timestamp;
		this.txSig = txSig;
		this.nonce = nonce;
		this.epochNumber = epochNumber;
		this.epochHash = epochHash;
		this.range = range;
		
	}
	
	
	public byte[] getFromAddress() {
		return FromAddress;
	}
	
	public void setFromAddress(byte[] FromAddress) {
		this.FromAddress = FromAddress;
	}
	
	public byte[] getToAddress() {
		return ToAddress;
	}
	
	public void setToAddress(byte[] ToAddress) {
		this.ToAddress = ToAddress;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	public byte[] getCoinRewardAfterPartition(){
		return coinRewardAfterPartition;
	}
	
	public byte[] getStakeRewardAfterPartition(){
		return stakeRewardAfterPartition;
	}
	
	public byte[] getVaultRatio() {
		return vaultRatio;
	}
	
	public byte[] getEachStaker50Partition() {
		return eachStaker50Partition;
	}
	
	public byte[] getStakers50Partition() {
		return stakers50Partition;
	}
	
	public void setValue(byte[] value) {
		this.value = value;
	}
	
	public byte[] getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(byte[] timestamp) {
		this.timestamp = timestamp;
	}
	
	public byte[] getTxSig() {
		return txSig;
	}
	
	public void setTxSig(byte[] txSig) {
		this.txSig = txSig;
	}
	
	public byte[] getNonce() {
		return nonce;
	}
	
	public void setNonce(byte[] nonce) {
		this.nonce = nonce;
	}
	

	public byte[] getReport() {
		return report;
	}
	
	public void setReport(byte[] report) {
		this.report = report;
	}
	
	public byte[] getEpochNumber() {
		return epochNumber;
	}
	
	public void setEpochNumber(byte[] epochNumber) {
		this.epochNumber = epochNumber;
	}
	
	public byte[] getEpochHash() {
		return epochHash;
	}
	
	public void setEpochHash(byte[] epochHash) {
		this.epochHash = epochHash;
	}
	
	public byte[] getRange() {
		return range;
	}
	
	public void setRange(byte[] range) {
		this.range = range;
	}
	
}
