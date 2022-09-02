package Blocks.mod;

import java.io.Serializable;
import java.util.List;

import SEWS_Protocol.weightResults;
import temp.Static;
import transc.mod.Ctx;

public class StakeBlock implements Serializable{


	String Hash; String version; String Prev_hash; String Block_num; long timestamp; List<weightResults>processedStakes;String merkleRootProcessedStakes;String merkleRootStakeTx;String validatorSig;List<Ctx>epochStakeTx;

	private String epochCreated;

	private String epochCreatedFor;
	
	public StakeBlock(String version,String epochCreated, String epochCreatedFor,String Hash, String Prev_hash,String Block_num, long timestamp,String merkleRootProcessedStakes,String merkleRootStakeTx,String validatorSig, List<Ctx> epochStakeTx, List<weightResults> processedStakes) {
		this.version = Static.VERSION;
		this.epochCreated = epochCreated;
		this.epochCreatedFor = epochCreatedFor;
		this.Hash = Hash;
		this.Prev_hash = Prev_hash;
		this.Block_num = Block_num;
		this.timestamp = timestamp;
		this.merkleRootProcessedStakes = merkleRootProcessedStakes;
		this.merkleRootStakeTx = merkleRootStakeTx;
		this.validatorSig = validatorSig;
		this.epochStakeTx = epochStakeTx;
		this.processedStakes = processedStakes;
	} 

	public String getHash() {
	    return Hash;
	}
	
	public String getPrev_hash() {
	    return Prev_hash;
	}
	
	public String getBlock_num() {
	    return Block_num;
	}
	
	public long getTimestamp() {
	    return timestamp;
	}
	
	public String getValidatorSig(){
		return validatorSig;
	}
	
	public String getMerkleRootProcessedStakes() {
	    return merkleRootProcessedStakes;
	}
	
	public String getMerkleRootStakeTx() {
	    return merkleRootStakeTx;
	}
	
	public List<weightResults> getProcessedStakes() {
	    return processedStakes;
	}
	
	public List<Ctx> getEpochStakeTx() {
	    return epochStakeTx;
	}
	
	public String getVersion() {
		// TODO Auto-generated method stub
		return version;
	}

	public String getEpochCreated() {
		// TODO Auto-generated method stub
		return epochCreated;
	}
	
	public String getEpochCreatedFor() {
		// TODO Auto-generated method stub
		return epochCreatedFor;
	}
	
}
