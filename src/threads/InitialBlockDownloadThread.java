package threads;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import Blocks.Validate.ValCoinBlock;
import Blocks.mod.Block_obj;
import node.DisconectAllOutBoundPeers;
import temp.Holder;
import temp.Static;

public class InitialBlockDownloadThread implements Runnable{
	
	public static LinkedBlockingQueue<List<Block_obj>> downloadedBlocks = new LinkedBlockingQueue<List<Block_obj>>();
	
	ForkJoinPool blockValPool = new ForkJoinPool(Static.NUM_OF_CORES/2);
	
	@Override
	public void run() {
		while(true) {
			try {
				List<Block_obj> blocksInLine = downloadedBlocks.take();
			
					for(Block_obj block : blocksInLine) {
						if(!blockValPool.invoke(new ValCoinBlock(block))) {
							destroyChain(downloadedBlocks);
							return;
						}
					}
				
			} catch (InterruptedException e) {;
				e.printStackTrace();
				 Thread.currentThread().interrupt();
			}
			
		}
		
	}

	public void destroyChain(LinkedBlockingQueue<List<Block_obj>> downloadedBlocks) {
		
			DisconectAllOutBoundPeers.clearAllOutboundPeers();
				downloadedBlocks.clear();
			
	}

}
