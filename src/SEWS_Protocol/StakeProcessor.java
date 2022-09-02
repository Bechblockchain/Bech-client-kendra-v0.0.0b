package SEWS_Protocol;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import SEWS_Protocol.db.retrieve;
import SEWS_Protocol.db.store;
import crypto.BytesToFro;
import temp.Holder;
import temp.Static;

public class StakeProcessor {

	public static List<weightResults> weighStakes() {
		
		long processingTimestamp = System.currentTimeMillis();
		List<StakeObj> stakeList = null;
		List<weightResults> weighedStakes = null;
		
		stakeList = retrieve.retrieveBulkStakeData();
		if(stakeList != null) {
			List<weightResults> Stakeslist = returnWeightedStakes(stakeList,processingTimestamp);
			Stakeslist.sort((o1, o2) -> o2.getResults().compareTo(o1.getResults())); 
			
			for(weightResults weights: Stakeslist) {
				BigInteger position = BigInteger.valueOf(Stakeslist.indexOf(weights));
				weights.setFallPosition(position);
				//((weightResults) Stakeslist).setFallPosition(position);
				
			}
			Stakeslist.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition())); 
			weighedStakes = Stakeslist;
		}else {
			return weighedStakes;
		}
		
		return weighedStakes;
	}
	
	public static List<weightResults> weighStakes(long processingTimestamp) {
		
		List<StakeObj> stakeList = null;
		List<weightResults> weighedStakes = null;
		
		stakeList = retrieve.retrieveBulkStakeData();
		if(stakeList != null) {
			List<weightResults> Stakeslist = returnWeightedStakes(stakeList,processingTimestamp);
			Stakeslist.sort((o1, o2) -> o2.getResults().compareTo(o1.getResults())); 
			
			for(weightResults weights: Stakeslist) {
				BigInteger position = BigInteger.valueOf(Stakeslist.indexOf(weights));
				weights.setFallPosition(position);
				
			}
			Stakeslist.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition())); 
			weighedStakes = Stakeslist;
		}else {
			return weighedStakes;
		}
			
		return weighedStakes;
	}
	
	public static List<weightResults> returnWeightedStakes(List<StakeObj> stakeList,long processingTimestamp){
		List<weightResults> weightedResults = new ArrayList<>();
		BigInteger position =  BigInteger.valueOf(0);
		for(StakeObj stakeobj: stakeList) {
				if(isFirstStake(stakeobj) && isValid(stakeobj,processingTimestamp) ) {
					byte[] signature = BytesToFro.convertStringToByteArray(stakeobj.toString() + returnNextStakeRatio(stakeobj,processingTimestamp).toString() + returnValidationTimeSpan(stakeobj).toBigInteger().toString() + returnProcessingEpoch(stakeobj).toBigInteger().toString() + returnValidatorConfirmationEpoch(stakeobj).toBigInteger().toString() + returnAllowanceEpoch(stakeobj).toBigInteger().toString() + position.toString());	
					weightResults weight = new weightResults(stakeobj, returnFirstStakeRatio(stakeobj,processingTimestamp),returnValidationTimeSpan(stakeobj).toBigInteger(),returnProcessingEpoch(stakeobj).toBigInteger(),returnValidatorConfirmationEpoch(stakeobj).toBigInteger(),returnAllowanceEpoch(stakeobj).toBigInteger(),position,BytesToFro.bytesToHex(signature),processingTimestamp);
					weightedResults.add(weight);
				}else {
					if(isValid(stakeobj,processingTimestamp) ) {
					byte[] signature = BytesToFro.convertStringToByteArray(stakeobj.toString() + returnNextStakeRatio(stakeobj,processingTimestamp).toString() + returnValidationTimeSpan(stakeobj).toBigInteger().toString() + returnProcessingEpoch(stakeobj).toBigInteger().toString() + returnValidatorConfirmationEpoch(stakeobj).toBigInteger().toString() + returnAllowanceEpoch(stakeobj).toBigInteger().toString() + position.toString());	
					weightResults weight = new weightResults(stakeobj,returnNextStakeRatio(stakeobj,processingTimestamp),returnValidationTimeSpan(stakeobj).toBigInteger(),returnProcessingEpoch(stakeobj).toBigInteger(),returnValidatorConfirmationEpoch(stakeobj).toBigInteger(),returnAllowanceEpoch(stakeobj).toBigInteger(),position,BytesToFro.bytesToHex(signature),processingTimestamp);
					weightedResults.add(weight);
					}
				}
		}
		return weightedResults;
	}
	
	public static boolean isValid(StakeObj stakeobj,long processingTimestamp) {
		BigDecimal stakedCoins = stakeobj.getStakeCoins();
		long stakeTimestamp = stakeobj.getTimestamp(); 

		long stakeTimestampDiff = processingTimestamp - stakeTimestamp;
		if(stakedCoins.compareTo(Static.MIN_STAKE_VALUE) < 0 ) {
			Holder.ObsoleteStakeAcc.add(stakeobj);
			return false;
		}
		
		if(stakeTimestampDiff > Static.STAKE_OBJ_OBSOLETE_TIME_THRESHOLD) {
			Holder.ObsoleteStakeAcc.add(stakeobj);
			return false;
		}
		
		if(Holder.gehennaList.contains(stakeobj.getAddress())) {
			Holder.ObsoleteStakeAcc.add(stakeobj);
			return false;
		}
		return true;
	}
	
	public static boolean isFirstStake(StakeObj stakeobj) {
		String lastValidationStatus = stakeobj.getLastValidationStatus();
		long lastValidationTimestamp = stakeobj.getLastValidationTimestamp();
		if(lastValidationTimestamp == 0 && lastValidationStatus.equals(Static.NONE)) {
			return true;
		}
		
		return false;
	}

	public static BigDecimal returnFirstStakeRatio(StakeObj stakeobj,long processingTimestamp) {
		BigDecimal weightRatio = null;
			
		return weightRatio;
	}
	
	public static BigDecimal returnNextStakeRatio(StakeObj stakeobj,long processingTimestamp) {
		BigDecimal weightRatio = null;
		
		return weightRatio;
}
	
	//Expected epoch time(EET)
	public static BigDecimal returnValidationTimeSpan(StakeObj stakeobj) {
		BigDecimal validationTime = null;
		
		return validationTime;
	}
	
	//Expected time to commence stake processing for next epoch(ENEPT)
	public static BigDecimal returnProcessingEpoch(StakeObj stakeobj) {
		 BigDecimal stakeProcessingTime = null;
		 
		 return stakeProcessingTime;
	}
	
	//Expected time to end stake processing and commence next epoch validator confirmation(EVCT)
	public static BigDecimal returnValidatorConfirmationEpoch(StakeObj stakeobj) {
		BigDecimal validatorConfirmationTime = null;
		
		return validatorConfirmationTime;
	}
	
	//Time range validator is allowed to freely end epoch prior to EET(Time Allowance)
	public static BigDecimal returnAllowanceEpoch(StakeObj stakeobj) {
		BigDecimal timeAllowance = null;
		
		return timeAllowance;
	}

	public static void updateValStartStakeTime(String validatorAddress,long timestamp)  {
		StakeObj stakeobj = retrieve.retrieveSingleStakeData(validatorAddress);
		stakeobj.setLastValidationTimestamp(timestamp);
		store.storeSingleStakeData(stakeobj);
		store.storeSingleEpochData(validatorAddress,timestamp);
	}
	
	public static void updateValStartStakeStatus(String validatorAddress,String status)  {
		StakeObj stakeobj = retrieve.retrieveSingleStakeData(validatorAddress);
		stakeobj.setLastValidationStatus(status);
		store.storeSingleStakeData(stakeobj);
	}
	
}
