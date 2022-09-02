package Blocks.mod;

import java.io.Serializable;

public class Epoch implements Serializable{

	String valAddr;
	long Height;
	long valPos;
	long epochStart;
	long epochEnd;
	long nextStakeProcesingTime;
	
	public Epoch(String valAddr,long Height,long valPos,long epochStart,long epochEnd,long nextStakeProcesingTime){
		this.valAddr = valAddr;
		this.Height = Height;
		this.valPos = valPos;
		this.epochStart = epochStart;
		this.epochEnd = epochEnd;
		this.nextStakeProcesingTime = nextStakeProcesingTime;
	}
	
	public String getAddress() {
		return valAddr;
	}

	public long getHeight() {
		return Height;
	}
	
	public long getValPosition() {
		return valPos;
	}
	
	public long getEpochStart() {
		return epochStart;
	}
	
	public long getEpochEnd() {
		return epochEnd;
	}
	
	public long getNextStakeProcesingTime() {
		return nextStakeProcesingTime;
	}
}
