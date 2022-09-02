package SEWS_Protocol;

import java.io.Serializable;
import java.math.BigDecimal;

public class StakeObj implements Serializable{
	String address;
	BigDecimal stakeCoins;
	String lastValidationStatus;
	long stakeTimestamp;
	long lastValidationTimestamp;
	
	public StakeObj(String address, BigDecimal stakeCoins, long stakeTimestamp,long lastValidationTimestamp,String lastValidationStatus) {
		this.address = address;
		this.stakeCoins = stakeCoins;
		this.stakeTimestamp = stakeTimestamp;
		this.lastValidationTimestamp = lastValidationTimestamp;
		this.lastValidationStatus = lastValidationStatus;
	}

	public String getAddress() {
		return address;
	}
	
	public BigDecimal getStakeCoins() {
		return stakeCoins;
	}
	
	public long getTimestamp() {
		return stakeTimestamp;
	}
	
	public long getLastValidationTimestamp() {
		return lastValidationTimestamp;
	}
	
	public String getLastValidationStatus() {
		return lastValidationStatus;
	}
	
	public void setLastValidationTimestamp(long lastValidationTimestamp) {
		this.lastValidationTimestamp = lastValidationTimestamp;
	}
	public void setLastValidationStatus(String lastValidationStatus) {
		this.lastValidationStatus = lastValidationStatus;
	}

	
}
