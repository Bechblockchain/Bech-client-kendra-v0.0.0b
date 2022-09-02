package SEWS_Protocol;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class weightResults implements Serializable{

	BigDecimal results;
	StakeObj stakeobj;
	BigInteger epoch;
	BigInteger processingEpoch;
	BigInteger validatorConfirmationEpoch;
	BigInteger epochAllowance;
	BigInteger FallPosition;
	String signature;
	long timestamp;
	
	public weightResults(StakeObj stakeobj,BigDecimal results,BigInteger epoch,BigInteger processingEpoch,BigInteger validatorConfirmationEpoch,BigInteger epochAllowance,BigInteger FallPosition,String signature,long timestamp) {
		this.stakeobj = stakeobj;
		this.results = results;
		this.epoch = epoch;
		this.processingEpoch = processingEpoch;
		this.validatorConfirmationEpoch = validatorConfirmationEpoch;
		this.epochAllowance = epochAllowance;
		this.FallPosition = FallPosition;
		this.signature =signature;
		this.timestamp = timestamp;
	}
	
	public weightResults(StakeObj stakeobj,BigDecimal results,BigInteger epoch,BigInteger processingEpoch,BigInteger validatorConfirmationEpoch,BigInteger epochAllowance,BigInteger FallPosition,long timestamp) {
		this.stakeobj = stakeobj;
		this.results = results;
		this.epoch = epoch;
		this.processingEpoch = processingEpoch;
		this.validatorConfirmationEpoch = validatorConfirmationEpoch;
		this.epochAllowance = epochAllowance;
		this.FallPosition = FallPosition;
		this.timestamp = timestamp;
	}

	public BigDecimal getResults() {
		return results;
	}
	
	public StakeObj getStakeobj() {
		return stakeobj;
	}
	
	public BigInteger getEpoch() {
		return epoch;
	}
	
	public BigInteger getProcessingEpoch() {
		return processingEpoch;
	}
	
	public BigInteger getValidatorConfirmationEpoch() {
		return validatorConfirmationEpoch;
	}
	
	public BigInteger getEpochAllowance() {
		return epochAllowance;
	}
	
	public BigInteger getFallPosition() {
		return FallPosition;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setFallPosition(BigInteger FallPosition) {
		this.FallPosition = FallPosition;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	
}
