package Blocks.epoch;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import connect.Mod.Request;
import temp.Static;

public class FormBlock implements Runnable{
	
	public static LinkedBlockingQueue<Request> startRequest = new LinkedBlockingQueue<Request>();
	
	ForkJoinPool formblockPool = new ForkJoinPool(Static.NUM_OF_CORES/2);

	  @Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				
				try {
					System.out.println("Form thread. starting..\n");
					Request req = startRequest.take();
					if(req.getWeightResults() != null) {
						System.out.println("...starting weight processing");
						formblockPool.invoke( new ProcessBlocks(req.getWeightResults(),req.getIsclear()));
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	  
	

}
