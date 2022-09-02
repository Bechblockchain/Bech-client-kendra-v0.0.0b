package threads;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import Blocks.Validate.ValStakeBlock;
import Blocks.db.Block_db;
import Blocks.epoch.FormBlock;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.weightResults;
import connect.Network;
import connect.Mod.Request;
import temp.Holder;
import temp.Static;

public class StakeBlockThread implements Runnable{

	public static LinkedBlockingQueue<StakeBlock> newStakeBlock = new LinkedBlockingQueue<StakeBlock>();
	
	@Override
	public void run() {
		while(true) {
			try {
				StakeBlock stakeblock = newStakeBlock.take();
			
				if(stakeblock.getBlock_num().equals("0")) {
					
					ValStakeBlock.validateNewStakeBlock(stakeblock);
					
				}else if(Static.START_TYPE.equals(Static.WARM_START) && Long.parseLong(Static.PREV_STAKE_BLOCK_NUM  + 1) == Long.parseLong(stakeblock.getBlock_num())) {
			
					if(stakeblock.getVersion().equals(Static.VERSION)){
						
							if(ValStakeBlock.validateNewStakeBlock(stakeblock) == false) {
								
								List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
						  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
									
						  		for(weightResults weightresults: results) {
									if(weightresults.getStakeobj().getAddress().equals(Static.EPOCH_VALIDATOR_ADDRESS)) {
										BigInteger position = weightresults.getFallPosition();
										Static.PREVIOUS_VALIDATOR_CONFIRMATION_TIME = weightresults.getValidatorConfirmationEpoch();
										weightResults nativeStakerWeight = results.get((position).intValue() + 1);
										String nativeStakerAddress = nativeStakerWeight.getStakeobj().getAddress();
										
										//start next epoch if validator has next best fallposition
										if(Static.NATIVE_VALIDATOR_ADDRESS.equals(nativeStakerAddress)) {
											 try {
												 Request req = new Request(null,null,null,nativeStakerWeight,false);
												 FormBlock.startRequest.put(req);
										   	 } catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
											 }

												Holder.epochStakeTxs.clear();
												Holder.processedStakerAddr.clear();
										}else {
											break;
										}
										Network.broadcastStakeBlock(stakeblock);
								  		break;
									}
								}
							}else {
								Static.TEMP_STAKE_BLOCK = stakeblock;
								Network.broadcastStakeBlock(stakeblock);
							}
						
					}
					
				}
				
			} catch ( IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
