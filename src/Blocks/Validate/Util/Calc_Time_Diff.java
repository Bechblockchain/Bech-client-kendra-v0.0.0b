package Blocks.Validate.Util;

import java.math.BigInteger;

import Blocks.mod.Block_obj;
import Blocks.mod.PreviousBlockObj;

public class Calc_Time_Diff {


    public static boolean ValidateTimestamp(Block_obj newblock) {
    	PreviousBlockObj parentBlock = PreviousBlockObj.getLatestBlockData();
			
		BigInteger allowedTimeAfterPrevBlock = BigInteger.valueOf(200);
  		if((BigInteger.valueOf(newblock.getTimestamp()).subtract(allowedTimeAfterPrevBlock).compareTo(BigInteger.valueOf(parentBlock.getTimestamp())) < 0)){
  			
  			return false;
  		}
  		return true;
	 }
  
	
}
