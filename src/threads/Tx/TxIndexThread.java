package threads.Tx;

import java.util.concurrent.LinkedBlockingQueue;
import connect.Mod.Request;
import db.db_store;

public class TxIndexThread implements Runnable {

	public static LinkedBlockingQueue<Request> newTxReq = new LinkedBlockingQueue<Request>();
	
	
	@Override
	public void run() {
		while(true) {
				
			try {
				Request req = newTxReq.take();
					if(req.getCommand().equals("indexCtx")) {
					 db_store.storeCoinTxIndex(req.getItem(),req.getItem2());
					}
			} catch ( InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
				
		}
		
	}

	
	
	
	
}

