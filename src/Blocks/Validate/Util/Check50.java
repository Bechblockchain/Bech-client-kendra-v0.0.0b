package Blocks.Validate.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import Blocks.db.Block_db;
import SEWS_Protocol.weightResults;
import temp.Static;

public class Check50 {

public static long checkstaker50Num() {
		
		BigDecimal staker50num = null;
		List<weightResults> prevEpochResults = null;	
	
		String stakeblockHash = Static.TEMP_STAKE_BLOCK.getPrev_hash();
		prevEpochResults = Block_db.getSingleStakeBlockData(stakeblockHash).getProcessedStakes();
		prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
		
		int counter = -1;
		
		for(int i = 0; i < prevEpochResults.size();i++) {
			counter++;
			if(prevEpochResults.get(i).getStakeobj().getAddress().equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS)) {
				prevEpochResults.remove(i);
				if(i != 0) {
					for(int j = 0; j < counter;i++) {
						prevEpochResults.remove(j);
					}
				}
				break;
			}
		}
		staker50num = new BigDecimal(prevEpochResults.size()*0.5).setScale(0, RoundingMode.DOWN);
		
		return staker50num.longValue();
	}
	
  
    public static BigDecimal checkEachstaker50Partition(BigDecimal staker50Partition)  {
		
		BigDecimal staker50num = null;
		List<weightResults> prevEpochResults = null;	
	
		String stakeblockHash = Static.TEMP_STAKE_BLOCK.getPrev_hash();
		prevEpochResults = Block_db.getSingleStakeBlockData(stakeblockHash).getProcessedStakes();
		prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
		
		int counter = -1;
		
		for(int i = 0; i < prevEpochResults.size();i++) {
			counter++;
			if(prevEpochResults.get(i).getStakeobj().getAddress().equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS)) {
				prevEpochResults.remove(i);
				if(i != 0) {
					for(int j = 0; j < counter;i++) {
						prevEpochResults.remove(j);
					}
				}
				break;
			}
		}
		staker50num = new BigDecimal(prevEpochResults.size()*0.5).setScale(0, RoundingMode.DOWN);
		
		return (staker50Partition.divide(staker50num)).setScale(2, RoundingMode.HALF_EVEN);
	}

	
	
	
}
