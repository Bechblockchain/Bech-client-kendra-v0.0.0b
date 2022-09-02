package threads;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import Blocks.Validate.NonRecursive;
import Blocks.Validate.ValCoinBlock;
import Blocks.db.Block_db;
import Blocks.epoch.FormBlock;
import Blocks.mod.Block_obj;
import SEWS_Protocol.weightResults;
import connect.Network;
import connect.Mod.Request;
import temp.Holder;
import temp.Static;

public class CoinBlockThread implements Runnable{

	public static LinkedBlockingQueue<Block_obj> newCoinBlock = new LinkedBlockingQueue<Block_obj>();
	
	ForkJoinPool blockValPool = new ForkJoinPool(Static.NUM_OF_CORES/2);

	@Override
	public void run() {
		while(true) {
			try {
				Block_obj blockInLine = newCoinBlock.take();
			
				if(blockInLine.getBlock_num().equals("0")) {
					
					blockValPool.invoke(new ValCoinBlock(blockInLine)).booleanValue();
					
				}else if(Static.START_TYPE.equals(Static.WARM_START) && Long.parseLong(Static.PREV_BLOCK_NUM  + 1) == Long.parseLong(blockInLine.getBlock_num())) {
					String validator = blockInLine.getValidator_Addr();
					if(validator.equals(Static.EPOCH_VALIDATOR_ADDRESS) && blockInLine.getVersion().equals(Static.VERSION)){
						validator = Static.EPOCH_VALIDATOR_ADDRESS;
						
							if(!useRecursive(blockInLine).booleanValue()) {
								
								List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
						  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
									
						  		for(weightResults weightresults: results) {
										if(weightresults.getStakeobj().getAddress().equals(validator)) {
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
											Network.broadcastCoinBlock(blockInLine);
									  		break;
										}
									}
							}else {
								Static.NATIVE_BLOCK_HEIGHT ++; 
								Network.broadcastCoinBlock(blockInLine);
							}
						
					}
					
				}
				
			} catch ( InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public Boolean useRecursive(Block_obj blockInLine) {
		boolean istrue = true;
		if(Static.EPOCH_VALIDATOR_ADDRESS.equals(Static.NATIVE_VALIDATOR_ADDRESS)) {
			try {
				istrue = NonRecursive.validateNewCoinBlock(blockInLine);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			istrue = blockValPool.invoke(new ValCoinBlock(blockInLine)).booleanValue();
		}
		return istrue;
	}
	
}
